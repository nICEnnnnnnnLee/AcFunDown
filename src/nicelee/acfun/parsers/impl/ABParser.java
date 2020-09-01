package nicelee.acfun.parsers.impl;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.acfun.annotations.Acfun;
import nicelee.acfun.downloaders.impl.M3u8Downloader;
import nicelee.acfun.enums.VideoQualityEnum;
import nicelee.acfun.model.ClipInfo;
import nicelee.acfun.model.VideoInfo;
import nicelee.acfun.util.HttpCookies;
import nicelee.acfun.util.HttpHeaders;
import nicelee.acfun.util.Logger;

//depreciated
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
			albumId_groupId_id = matcher.group().replace("ab", "aa");
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
	// private final static Pattern pABVideoInfo = Pattern.compile("var bgmInfo ?=
	// ?(.*?)</script>");
	private final static Pattern pABVideoInfo = Pattern.compile("window.bangumiData ?= ?(.*?});");

	protected VideoInfo getAVDetail(String albumId_groupId_id, boolean getVideoLink) {
		// String[] IDs = albumId_groupId_id.split("_");

		VideoInfo viInfo = new VideoInfo();
		viInfo.setVideoId(albumId_groupId_id);

		// 获取json
		HttpHeaders headers = new HttpHeaders();
		String basicInfoUrl = String.format("https://www.acfun.cn/bangumi/%s", albumId_groupId_id);
		String html = util.getContent(basicInfoUrl, headers.getCommonHeaders("www.acfun.cn"),
				HttpCookies.getGlobalCookies());
//		String basicInfoUrl = String.format("https://www.acfun.cn/album/abm/bangumis/video?albumId=%s&groupId=%s&num=1&size=1000&_=%d", 
//				IDs[0].replace("ab", ""), IDs[1], System.currentTimeMillis());

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
			int qnList[] = new int[] { 4,3,2,1,0 };
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
	 * 查询视频链接(查询1次)
	 * 
	 * @external output linkQN 保存返回链接的清晰度
	 * @param albumId_groupId_id aaxxx_xxx_xxx
	 * @param videoId  
	 * @param qn
	 * @param downFormat
	 * @return 链接
	 */
	@Override
	public String getVideoLink(String albumId_groupId_id, String videoId, int qn, int downFormat) {
		HttpHeaders headers = new HttpHeaders();

		// 获取总的m3u8
		String basicInfoUrl = String.format("https://www.acfun.cn/bangumi/%s",
				albumId_groupId_id);
		String html = util.getContent(basicInfoUrl, headers.getCommonHeaders("www.acfun.cn"),
				HttpCookies.getGlobalCookies()); // 查询1次
		Matcher matcher = pABVideoInfo.matcher(html);
		matcher.find();
		String json = matcher.group(1);
		//Logger.println(json);
		// window.pageInfo.currentVideoInfo.ksPlayJson
		JSONObject jObj = new JSONObject(
				new JSONObject(json).getJSONObject("currentVideoInfo").getString("ksPlayJson"));
		JSONArray jArr = jObj.getJSONArray("adaptationSet").getJSONObject(0).getJSONArray("representation");
		Integer realQn = null;
		for (int i = 0; i < jArr.length(); i++) {
			if (VideoQualityEnum.getQualityDescript(qn).equals(jArr.getJSONObject(i).getString("qualityLabel"))) {
				Logger.println("找到相应清晰度:" + VideoQualityEnum.getQualityDescript(qn));
				realQn = i;
				break;
			}
		}

		if(realQn == null) {
			Logger.println("没有找到相应清晰度");
			realQn = 0;
			if (qn <= realQn) {
				realQn = qn;
			}
		}
		JSONObject qnobj = jArr.getJSONObject(realQn);
		Logger.println(qnobj.getString("url"));
		paramSetter.setRealQN(VideoQualityEnum.getQN(qnobj.getString("qualityLabel")));
		return qnobj.getString("url");
	}

}
