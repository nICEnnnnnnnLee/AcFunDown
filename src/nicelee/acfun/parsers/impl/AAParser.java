package nicelee.acfun.parsers.impl;

import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.acfun.annotations.Acfun;
import nicelee.acfun.model.ClipInfo;
import nicelee.acfun.model.VideoInfo;
import nicelee.acfun.util.HttpCookies;
import nicelee.acfun.util.HttpHeaders;

@Acfun(name = "aaParser", note = "番剧")
public class AAParser extends ABParser {
//https://www.acfun.cn/album/abm/bangumis/video?albumId=5024869&groupId=34168&num=1&size=20&_=1565001964314
//https://www.acfun.cn/album/abm/bangumis/video?albumId=5024869&groupId=34168&num=1&size=1000&_=1565008921550
	private final static Pattern pattern = Pattern.compile("(?!aa[0-9]+_[0-9]+_[0-9]+)aa[0-9]+");
	private String aaId;

	public AAParser(Object... obj) {
		super(obj);
	}

	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		boolean matches = matcher.find();
		if (matches) {
			aaId = matcher.group();
			System.out.println("匹配AAParser: " + aaId);
		}
		return matches;
	}

	@Override
	public String validStr(String input) {
		return aaId;
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		System.out.println("aaParser正在获取结果" + aaId);
		return getAADetail(aaId, videoFormat, getVideoLink);
	}

	/**
	 * 
	 * @input HttpRequestUtil util
	 * @param aaId
	 * @param isGetLink
	 * @return
	 */
	private final static Pattern pABVideoInfo = Pattern.compile("window.bangumiData ?= ?(.*?});");

	protected VideoInfo getAADetail(String aaId, int videoFormat, boolean isGetLink) {
		
		// 查询信息，得到groupId，构造 albumId_groupId_id
		HttpHeaders headers = new HttpHeaders();
		String infoUrl = "https://www.acfun.cn/album/abm/bangumis/video?num=1&size=1000&albumId="
				+ aaId.replace("aa", "");
		String strJson = util.getContent(infoUrl, headers.getCommonHeaders("www.acfun.cn"),
				HttpCookies.getGlobalCookies());
		JSONObject info = new JSONObject(strJson).getJSONObject("data");
		JSONArray clips = info.getJSONArray("content");

		VideoInfo viInfo = new VideoInfo();
		viInfo.setVideoId(aaId);

		// 获取json
		String basicInfoUrl = String.format("https://www.acfun.cn/bangumi/%s", aaId);
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
		LinkedHashMap<Long, ClipInfo> clipMap = new LinkedHashMap<Long, ClipInfo>();
		for (int i = 0; i < clips.length(); i++) {
			JSONObject clipObj = clips.getJSONObject(i).getJSONArray("videos").getJSONObject(0);
//			final String albumId_groupId_id = String.format("%s_%d_%d", aaId, clipObj.getLong("groupId"),
			final String albumId_groupId_id = String.format("%s_%d_%d", aaId, 36188,
					clipObj.getLong("id"));
			ClipInfo clip = new ClipInfo();
			clip.setAvTitle(viInfo.getVideoName());
			clip.setAvId(albumId_groupId_id);
			clip.setcId(clipObj.getLong("videoId"));
			clip.setPage(0);
			clip.setRemark(i);
//			clip.setTitle(clipObj.getString("episodeName") + " " + clipObj.getString("newTitle"));
			clip.setTitle(clipObj.getString("newTitle"));
			clip.setPicPreview(clipObj.getString("image"));

			LinkedHashMap<Integer, String> links = new LinkedHashMap<Integer, String>();
			try {
				int qnList[] = new int[] { 4,3,2,1,0 };
				for (int qn : qnList) {
					if (isGetLink) {
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

}
