package nicelee.acfun.parsers.impl;

import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.acfun.annotations.Acfun;
import nicelee.acfun.model.ClipInfo;
import nicelee.acfun.model.VideoInfo;
import nicelee.acfun.util.HttpCookies;
import nicelee.acfun.util.HttpHeaders;
import nicelee.acfun.util.Logger;

@Acfun(name = "aaParser", note = "番剧")
public class AAParser extends ABParser {
//https://www.acfun.cn/album/abm/bangumis/video?albumId=5024869&groupId=34168&num=1&size=20&_=1565001964314
//https://www.acfun.cn/album/abm/bangumis/video?albumId=5024869&groupId=34168&num=1&size=1000&_=1565008921550
	private final static Pattern pattern = Pattern.compile("aa[0-9]+");
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
	protected VideoInfo getAADetail(String aaId, int videoFormat, boolean isGetLink) {
		VideoInfo viInfo = new VideoInfo();
		viInfo.setVideoId(aaId);

		// 获取json
		HttpHeaders headers = new HttpHeaders();
		String basicInfoUrl = String.format("https://www.acfun.cn/bangumi/%s", aaId);
		String html = util.getContent(basicInfoUrl, headers.getCommonHeaders("www.acfun.cn"), HttpCookies.getGlobalCookies());

		int begin = html.indexOf("<script>var albumInfo =");
		int end = html.indexOf("</script>", begin);
		String json = html.substring(begin + 24, end);
		Logger.println(json);

		// 获取ab总体信息
		JSONObject jObj = new JSONObject(json);
		viInfo.setVideoName(jObj.getString("title"));
		viInfo.setBrief(jObj.getString("intro"));
		viInfo.setAuthor("番剧");
		viInfo.setAuthorId("番剧");
		viInfo.setVideoPreview(jObj.getString("coverImageH"));

		final long groupId = jObj.getJSONArray("groups").getJSONObject(0).getLong("id");

		// 获取各P信息
		String detailInfoUrl = String.format(
				"https://www.acfun.cn/album/abm/bangumis/video?albumId=%s&groupId=%d&num=1&size=1000&_=%d",
				aaId.replace("aa", ""), groupId, System.currentTimeMillis());
		String jsonClips = util.getContent(detailInfoUrl, headers.getCommonHeaders("www.acfun.cn"), HttpCookies.getGlobalCookies());
		
		JSONArray array = new JSONObject(jsonClips).getJSONObject("data").getJSONArray("content");
		LinkedHashMap<Long, ClipInfo> clipMap = new LinkedHashMap<Long, ClipInfo>();
		for (int i = 0; i < array.length(); i++) {
			JSONObject clipObj = array.getJSONObject(i).getJSONArray("videos").getJSONObject(0);
			final String albumId_groupId_id = String.format("ab%s_%d_%d", 
					aaId.replace("aa", ""), groupId, clipObj.getLong("id"));
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
				int qnList[] = new int[] { 0, 1, 2, 3 };
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
