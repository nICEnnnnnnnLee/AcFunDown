package nicelee.acfun.parsers.impl;

import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.acfun.annotations.Acfun;
import nicelee.acfun.enums.VideoQualityEnum;
import nicelee.acfun.model.ClipInfo;
import nicelee.acfun.model.VideoInfo;
import nicelee.acfun.util.HttpCookies;
import nicelee.acfun.util.HttpHeaders;
import nicelee.acfun.util.Logger;

//@Acfun(name = "aaParser", note = "番剧")
public class AABangumiParser extends AbstractBaseParser {

//	private final static Pattern pattern = Pattern.compile("https://www.acfun.cn/bangumi/(aa[0-9]+)");
	private final static Pattern pattern = Pattern.compile("(aa[0-9]+)");
	private String bangumiId;

	public AABangumiParser(Object... obj) {
		super(obj);
	}

	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		boolean matches = matcher.find();
		if (matches) {
			bangumiId = matcher.group(1);
		}
		return matches;
	}

	@Override
	public String validStr(String input) {
		return input;
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		return getAVDetail(bangumiId, getVideoLink);
	}

	private final static Pattern pABVideoInfo = Pattern.compile("window.bangumiList ?= ?(.*?});");

	protected VideoInfo getAVDetail(String bangumiId, boolean getVideoLink) {

		VideoInfo viInfo = new VideoInfo();
		viInfo.setVideoId(bangumiId);

		// 获取json
		HttpHeaders headers = new HttpHeaders();
		String basicInfoUrl = String.format("https://www.acfun.cn/bangumi/%s", bangumiId);
		String html = util.getContent(basicInfoUrl, headers.getCommonHeaders("www.acfun.cn"),
				HttpCookies.getGlobalCookies());
		Matcher matcher = pABVideoInfo.matcher(html);
		matcher.find();
		String json = matcher.group(1);
		Logger.println(json);

		// 获取ab总体信息
		JSONArray items = new JSONObject(json).getJSONArray("items");
		JSONObject item0 = items.getJSONObject(0);
		viInfo.setVideoName(item0.getString("bangumiTitle"));
		viInfo.setBrief(viInfo.getVideoName());
		viInfo.setAuthor("番剧");
		viInfo.setAuthorId(bangumiId);
		viInfo.setVideoPreview(item0.getJSONObject("belongResource").getString("coverImageV"));

		// 获取各P信息
		LinkedHashMap<Long, ClipInfo> clipMap = new LinkedHashMap<Long, ClipInfo>();
		for (int i = 0; i < items.length(); i++) {
			JSONObject clipObj = items.getJSONObject(i);
			ClipInfo clip = new ClipInfo();
			clip.setAvTitle(viInfo.getVideoName());
			clip.setAvId(bangumiId);
			clip.setcId(clipObj.getLong("itemId"));
			clip.setPage(i);
			clip.setRemark(i);
			clip.setTitle(clipObj.getString("episodeName") + " " + clipObj.getString("title"));
			clip.setPicPreview(clipObj.getJSONObject("imgInfo").getString("thumbnailImageCdnUrl"));

			LinkedHashMap<Integer, String> links = new LinkedHashMap<Integer, String>();
			try {
				int qnList[] = new int[] { 4, 3, 2, 1, 0 };
				for (int qn : qnList) {
					if (getVideoLink) {
						String link = getVideoLink(bangumiId, "" + clip.getcId(), qn, 0);
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

	private final static Pattern pBangumiData = Pattern.compile("window.bangumiData ?= ?(.*?});");

	@Override
	public String getVideoLink(String bangumiId, String cid, int qn, int downFormat) {
		HttpHeaders headers = new HttpHeaders();
		String basicInfoUrl = String.format("https://www.acfun.cn/bangumi/%s_36188_%s", bangumiId, cid);
		String html = util.getContent(basicInfoUrl, headers.getCommonHeaders("www.acfun.cn"),
				HttpCookies.getGlobalCookies());
		Matcher matcher = pBangumiData.matcher(html);
		matcher.find();
		String json = matcher.group(1);
		Logger.println(json);

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

		if (realQn == null) {
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
