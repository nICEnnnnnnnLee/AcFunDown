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
public class ABParser_latest extends AbstractBaseParser {

	private final static Pattern pattern = Pattern.compile("ab[0-9]+[^_]*");
	private String albumId;

	// public EPParser(HttpRequestUtil util,IParamSetter paramSetter, int pageSize)
	// {
	public ABParser_latest(Object... obj) {
		super(obj);
	}

	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		boolean matches = matcher.find();
		if (matches) {
			albumId = matcher.group();
		}
		return matches;
	}

	@Override
	public String validStr(String input) {
		return albumId;
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		return getAVDetail(albumId, getVideoLink);
	}

	// "id":327107,"groupId":34168,"albumId":5024869,"jcContentId":10418187,"danmakuId":10441665
	// ab5024869_34168_328537
	// private final static Pattern pABVideoInfo = Pattern.compile("var bgmInfo ?=
	// ?(.*?)</script>");
	private final static Pattern pABVideoInfo = Pattern.compile("window.bangumiData ?= ?(.*?});");

	protected VideoInfo getAVDetail(String albumId, boolean getVideoLink) {
		// 查询信息，得到groupId，构造 albumId_groupId_id
		HttpHeaders headers = new HttpHeaders();
		String infoUrl = "https://www.acfun.cn/album/abm/bangumis/video?albumId=" + albumId.replace("ab", "");
		String strJson = util.getContent(infoUrl, headers.getCommonHeaders("www.acfun.cn"),
						HttpCookies.getGlobalCookies());
		JSONObject info = new JSONObject(strJson).getJSONObject("data");
		JSONArray clips = info.getJSONArray("content");
		JSONObject clipJson = clips.getJSONObject(clips.length() - 1).getJSONArray("videos").getJSONObject(0);
		
		final String albumId_groupId_id = String.format("%s_%d_%d", 
				albumId, clipJson.getLong("groupId"), clipJson.getLong("id"));
		
		
		VideoInfo viInfo = new VideoInfo();
		viInfo.setVideoId(albumId_groupId_id);

		// 获取json
		String basicInfoUrl = String.format("https://www.acfun.cn/bangumi/%s", albumId);
		String html = util.getContent(basicInfoUrl, headers.getCommonHeaders("www.acfun.cn"),
				HttpCookies.getGlobalCookies());

		Matcher matcher = pABVideoInfo.matcher(html);
		matcher.find();
		String json = matcher.group(1);
		System.out.println(json);

		// 获取ab总体信息
		JSONObject jObj = new JSONObject(json);
		viInfo.setVideoName(jObj.getString("bangumiTitle"));
		viInfo.setBrief(jObj.getString("bangumiIntro"));
		viInfo.setAuthor("番剧");
		viInfo.setAuthorId("番剧");
		viInfo.setVideoPreview(jObj.getString("bangumiCoverImageH"));

		// 获取各P信息
		JSONObject current = jObj;//.getJSONObject("currentVideoInfo");
		LinkedHashMap<Long, ClipInfo> clipMap = new LinkedHashMap<Long, ClipInfo>();
		ClipInfo clip = new ClipInfo();
		clip.setAvTitle(viInfo.getVideoName());
		clip.setAvId(albumId_groupId_id);
		clip.setcId(current.getLong("videoId"));
		clip.setPage(0);
//			clip.setTitle(clipObj.getString("episodeName") + " " + clipObj.getString("newTitle"));
		clip.setTitle(current.getString("title"));
		clip.setPicPreview(current.getString("image"));

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
		String json = util.getContent(basicInfoUrl, headers.getCommonHeaders("www.acfun.cn"),
				HttpCookies.getGlobalCookies()); // 查询1次

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
					if (count == qn) {
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
