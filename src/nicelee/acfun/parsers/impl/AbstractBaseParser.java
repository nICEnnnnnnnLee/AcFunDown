package nicelee.acfun.parsers.impl;

import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.acfun.enums.VideoQualityEnum;
import nicelee.acfun.model.ClipInfo;
import nicelee.acfun.model.VideoInfo;
import nicelee.acfun.parsers.IInputParser;
import nicelee.acfun.parsers.IParamSetter;
import nicelee.acfun.util.HttpCookies;
import nicelee.acfun.util.HttpHeaders;
import nicelee.acfun.util.HttpRequestUtil;
import nicelee.acfun.util.Logger;
import nicelee.ui.Global;

public abstract class AbstractBaseParser implements IInputParser {

	protected Matcher matcher;
	protected HttpRequestUtil util;
	protected int pageSize = 20;
	protected IParamSetter paramSetter;

	public AbstractBaseParser(Object... obj) {
		this.util = (HttpRequestUtil) obj[0];
		this.paramSetter = (IParamSetter) obj[1];
		this.pageSize = (int) obj[2];
	}

	@Override
	public abstract boolean matches(String input);

	@Override
	public abstract String validStr(String input);

	@Override
	public abstract VideoInfo result(String input, int videoFormat, boolean getVideoLink);

	/**
	 * 
	 * @param avId         字符串带ac
	 * @param videoFormat
	 * @param getVideoLink
	 * @return
	 */
	private final static Pattern pVideoInfo = Pattern.compile("window\\.videoInfo ?= ?(.*?});");

	protected VideoInfo getAVDetail(String avId, int videoFormat, boolean getVideoLink) {
		VideoInfo viInfo = new VideoInfo();
		viInfo.setVideoId(avId);

		// 获取json
		HttpHeaders headers = new HttpHeaders();
		String basicInfoUrl = String.format("https://www.acfun.cn/v/%s", avId);
		String html = util.getContent(basicInfoUrl, headers.getCommonHeaders("www.acfun.cn"),
				HttpCookies.getGlobalCookies());// 请求1次

		Matcher matcher = pVideoInfo.matcher(html);
		matcher.find();
		String json = matcher.group(1);
		// System.out.println(json);

		// 获取ac总体信息
		JSONObject jObj = new JSONObject(json);
		JSONObject jUser = jObj.getJSONObject("user");
		viInfo.setVideoName(jObj.getString("title"));
		viInfo.setBrief(jObj.optString("description"));
		viInfo.setAuthor(jUser.getString("name"));
		viInfo.setAuthorId(jUser.getString("id"));
		viInfo.setVideoPreview(jObj.getString("coverUrl"));

		// 获取各P信息
		JSONArray array = jObj.getJSONArray("videoList");
		LinkedHashMap<Long, ClipInfo> clipMap = new LinkedHashMap<Long, ClipInfo>();
		int qnList[] = null;
		for (int i = 0; i < array.length(); i++) {
			JSONObject clipObj = array.getJSONObject(i);
			ClipInfo clip = new ClipInfo();
			clip.setAvTitle(viInfo.getVideoName());
			clip.setAvId(avId);
			clip.setcId(clipObj.getLong("id"));
			clip.setPage(clipObj.getInt("priority"));
			clip.setTitle(clipObj.getString("title"));
			clip.setPicPreview(viInfo.getVideoPreview());

			LinkedHashMap<Integer, String> links = new LinkedHashMap<Integer, String>();
			try {
				if (qnList == null) {
					if(Global.noQualityRequest) {
						qnList = VideoQualityEnum.availableQNs();
					} else {
						qnList = getVideoQNList(avId, String.valueOf(clip.getcId())); // 请求1次
					}
				}
				for (int qn : qnList) {
					if (getVideoLink) {
						// String link = getVideoLink(avId, String.valueOf(clip.getcId()), qn,
						// videoFormat);
						String href = String.format("/v/%s_%d", avId, clip.getPage() + 1);
						String link = getVideoLinkByHref(headers, href, qn); // 请求1次
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
	 * 0:标清 1:高清 2:超清 3:1080p
	 * 
	 * @external input HttpRequestUtil util
	 * @param avId
	 * @param cid
	 * @return 清晰度列表
	 */
	public int[] getVideoQNList(String avId, String cid) {
		// 获取cid 对应的链接href
		HttpHeaders headers = new HttpHeaders();
		String href = getHrefByIds(avId, cid, headers);

		// 获取Array
		String newhtml = util.getContent(String.format("https://www.acfun.cn%s", href),
				headers.getCommonHeaders("www.acfun.cn"), HttpCookies.getGlobalCookies());

		Matcher matcher = pVideoInfo.matcher(newhtml);
		matcher.find();
		String json = matcher.group(1);
		Logger.println(new JSONObject(json).getJSONObject("currentVideoInfo").getString("ksPlayJson"));
		JSONObject jObj = new JSONObject(
				new JSONObject(json).getJSONObject("currentVideoInfo").getString("ksPlayJson"));
		JSONArray jArr = jObj.getJSONArray("adaptationSet").getJSONObject(0).getJSONArray("representation");

		int qnList[] = new int[jArr.length()];
		Logger.println(qnList.length);
		for (int i = 0; i < qnList.length; i++) {
			qnList[i] = VideoQualityEnum.getQN(jArr.getJSONObject(i).getString("qualityLabel"));
		}
		return qnList;
	}

	/**
	 * 查询视频链接(查询两次)
	 * 
	 * @external input HttpRequestUtil util
	 * @external input downFormat
	 * @external output linkQN 保存返回链接的清晰度
	 * @param avId 视频的avid
	 * @param cid  av下面可能不只有一个视频, avId + cid才能确定一个真正的视频
	 * @param qn
	 * @return 链接
	 */
	@Override
	public String getVideoLink(String avId, String cid, int qn, int downFormat) {
		// 获取cid 对应的链接href
		HttpHeaders headers = new HttpHeaders();
		String href = getHrefByIds(avId, cid, headers); // (查询一次)
		return getVideoLinkByHref(headers, href, qn);// (查询一次)
	}

	/**
	 * 访问href页面，并提取下载链接
	 * 
	 * @param headers
	 * @param href
	 * @return 下载链接
	 */
	private String getVideoLinkByHref(HttpHeaders headers, String href, int qn) {
		// 获取json
		String newhtml = util.getContent(String.format("https://www.acfun.cn%s", href),
				headers.getCommonHeaders("www.acfun.cn"), HttpCookies.getGlobalCookies());

		Matcher matcher = pVideoInfo.matcher(newhtml);
		matcher.find();
		String json = matcher.group(1);
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

	/**
	 * 由acid + 某p具体id， 获得访问链接
	 * 
	 * @param avId
	 * @param cid
	 * @param headers
	 * @return href
	 */
	private String getHrefByIds(String avId, String cid, HttpHeaders headers) {
		String basicInfoUrl = String.format("https://www.acfun.cn/v/%s", avId);
		String html = util.getContent(basicInfoUrl, headers.getCommonHeaders("www.acfun.cn"),
				HttpCookies.getGlobalCookies());
		try {
			// data-href='/v/ac10187818_5' title="05" data-id='10205818'
			String patt = String.format("data-href='([^>]*?)' title=\"[^>]*?\" data-id='%s'", cid);
			Pattern pCidInfo = Pattern.compile(patt);
			Matcher matcher = pCidInfo.matcher(html);
			matcher.find();
			// System.out.println(href);
			String href = matcher.group(1);
			// System.out.println(href);
			return href;
		} catch (Exception e) {
			Logger.println("该ac为单p视频");
			return "/v/" + avId;
		}
	}

	@Override
	public int getVideoLinkQN() {
		return paramSetter.getRealQN();
	}

}
