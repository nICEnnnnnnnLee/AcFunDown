package nicelee.acfun.parsers.impl;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.acfun.annotations.Acfun;
import nicelee.acfun.downloaders.impl.M3u8Downloader;
import nicelee.acfun.model.ClipInfo;
import nicelee.acfun.model.VideoInfo;
import nicelee.acfun.util.HttpCookies;
import nicelee.acfun.util.HttpHeaders;
import nicelee.acfun.util.Logger;

@Acfun(name = "abParser", note = "番剧单集")
public class ABParser extends AbstractBaseParser {

	private final static Pattern pattern = Pattern.compile("ab[0-9]+_[0-9]+_[0-9]+");
	private String albumId_groupId_id;

	// public EPParser(HttpRequestUtil util,IParamSetter paramSetter, int pageSize)
	// {
	public ABParser(Object... obj) {
		super(obj);
	}

	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		boolean matches = matcher.find();
		if (matches) {
			albumId_groupId_id = matcher.group();
		}
		return matches;
	}

	@Override
	public String validStr(String input) {
		return albumId_groupId_id;
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		return getAVDetail(albumId_groupId_id, getVideoLink);
	}

	// "id":327107,"groupId":34168,"albumId":5024869,"jcContentId":10418187,"danmakuId":10441665
	// ab5024869_34168_328537
	private final static Pattern pABVideoInfo = Pattern.compile("var bgmInfo ?= ?(.*?)</script>");

	protected VideoInfo getAVDetail(String albumId_groupId_id, boolean getVideoLink) {
		//String[] IDs = albumId_groupId_id.split("_");

		VideoInfo viInfo = new VideoInfo();
		viInfo.setVideoId(albumId_groupId_id);

		// 获取json
		HttpHeaders headers = new HttpHeaders();
		String basicInfoUrl = String.format("https://www.acfun.cn/bangumi/%s", albumId_groupId_id);
		String html = util.getContent(basicInfoUrl, headers.getCommonHeaders("www.acfun.cn"), HttpCookies.getGlobalCookies());
//		String basicInfoUrl = String.format("https://www.acfun.cn/album/abm/bangumis/video?albumId=%s&groupId=%s&num=1&size=1000&_=%d", 
//				IDs[0].replace("ab", ""), IDs[1], System.currentTimeMillis());

		Matcher matcher = pABVideoInfo.matcher(html);
		matcher.find();
		String json = matcher.group(1);
		System.out.println(json);

		// 获取ab总体信息
		JSONObject jObj = new JSONObject(json).getJSONObject("album");
		viInfo.setVideoName(jObj.getString("title"));
		viInfo.setBrief(jObj.getString("intro"));
		viInfo.setAuthor("番剧");
		viInfo.setAuthorId("番剧");
		viInfo.setVideoPreview(jObj.getString("coverImageH"));

		// 获取各P信息
		JSONArray array = new JSONObject(json).getJSONObject("video").getJSONArray("videos");
		LinkedHashMap<Long, ClipInfo> clipMap = new LinkedHashMap<Long, ClipInfo>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject clipObj = array.getJSONObject(i);
			ClipInfo clip = new ClipInfo();
			clip.setAvTitle(viInfo.getVideoName());
			clip.setAvId(albumId_groupId_id);
			clip.setcId(clipObj.getLong("videoId"));
			clip.setPage(i);
//			clip.setTitle(clipObj.getString("episodeName") + " " + clipObj.getString("newTitle"));
			clip.setTitle(clipObj.getString("newTitle"));
			clip.setPicPreview(clipObj.getString("image"));

			LinkedHashMap<Integer, String> links = new LinkedHashMap<Integer, String>();
			try {
				int qnList[] = new int[] { 0, 1, 2, 3 };
				for (int qn : qnList) {
					if (getVideoLink) {
						String link = getVideoLink(albumId_groupId_id, "" + clip.getcId(), qn, 0);
						links.put(qn, link);
					} else {
						links.put(qn, "");
					}
				}
				clip.setLinks(links);
			} catch (Exception e) {
				e.printStackTrace();
				clip.setLinks(links);
			}

			clipMap.put(clip.getcId(), clip);
		}
		viInfo.setClips(clipMap);
		viInfo.print();
		return viInfo;
	}

	/**
	 * 查询视频链接(查询两次)
	 * 
	 * @external output linkQN 保存返回链接的清晰度
	 * @param avId 视频的avid
	 * @param cid  av下面可能不只有一个视频, avId + cid才能确定一个真正的视频
	 * @param qn
	 * @return 链接
	 */
	@Override
	public String getVideoLink(String albumId_groupId_id, String videoId, int qn, int downFormat) {
		HttpHeaders headers = new HttpHeaders();
		
		// 获取总的m3u8
		String basicInfoUrl = String.format("https://www.acfun.cn/rest/pc-direct/play/playInfo/m3u8Auto?videoId=%s",
				videoId);
		String json = util.getContent(basicInfoUrl, headers.getCommonHeaders("www.acfun.cn"), HttpCookies.getGlobalCookies()); // 查询1次

		JSONObject jObj = new JSONObject(json).getJSONObject("playInfo");
		String totalLink = jObj.getJSONArray("streams").getJSONObject(0).getJSONArray("playUrls").getString(0);

		// 由总m3u8获取对应清晰度的链接
		LinkedList<String> links = new LinkedList<String>();
		String m3u8Content = util.getContent(totalLink, headers.getEmptyHeaders()); // 查询1次
		Logger.println(m3u8Content);
		String[] lines = m3u8Content.split("\r?\n");
		
		String result = null;
		int count = 0;
		for (String line : lines) {
			if (!line.startsWith("#") && !line.isEmpty()) {
				// 如果是相对路径，补全
				if (!line.startsWith("http")) {
					line = M3u8Downloader.genABUrl(line, totalLink);
					links.add(line);
					result = line;
					if(count == qn) {
						break;
					}
					count++;
				}
			}
		}
		paramSetter.setRealQN(count);
		return result;
	}

}
