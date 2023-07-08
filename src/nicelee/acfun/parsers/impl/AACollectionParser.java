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
import nicelee.acfun.util.Logger;

/**
 * https://www.acfun.cn/a/aa6125087
 *
 */
@Acfun(name = "AACollectionParser", note = "视频合集")
public class AACollectionParser extends AbstractPageQueryParser<VideoInfo> {

	private final static Pattern pattern = Pattern.compile("https://www.acfun.cn/a/aa([0-9]+)");
	private String spaceID;

	public AACollectionParser(Object... obj) {
		super(obj);
	}

	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		if (matcher.find()) {
			System.out.println("匹配视频合集");
			spaceID = matcher.group(1);
			return true;
		} else {
			return false;
		}

	}

	@Override
	public String validStr(String input) {
		return matcher.group().trim() + "p=" + paramSetter.getPage();
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		Logger.println(paramSetter.getPage());
		return result(pageSize, paramSetter.getPage(), videoFormat, getVideoLink);
	}

	@Override
	public void initPageQueryParam() {
		API_PMAX = 20;
		pageQueryResult = new VideoInfo();
		pageQueryResult.setClips(new LinkedHashMap<>());
	}

	private final static Pattern pInfo = Pattern.compile("window.__INITIAL_STATE__ ?= ?(.*?});");
	@Override
	protected boolean query(int page, int min, int max, Object... obj) {
		int videoFormat = (int) obj[0];
		boolean getVideoLink = (boolean) obj[1];
		try {
			HttpHeaders headers = new HttpHeaders();
			if (pageQueryResult.getVideoName() == null) {
				String basicInfoUrl = String.format("https://www.acfun.cn/a/aa%s", spaceID);
				String html = util.getContent(basicInfoUrl, headers.getCommonHeaders("www.acfun.cn"),
						HttpCookies.getGlobalCookies());
				Matcher matcher = pInfo.matcher(html);
				matcher.find();
				String json = matcher.group(1);
				Logger.println(json);
				
				JSONObject data = new JSONObject(json).getJSONObject("album");
				JSONObject albumInfo = data.getJSONObject("albumInfo");
				pageQueryResult.setVideoId(spaceID);
				pageQueryResult.setAuthor(albumInfo.getString("authorName"));
				pageQueryResult.setVideoName(albumInfo.getString("title") + paramSetter.getPage());
				pageQueryResult.setVideoPreview(albumInfo.getString("coverImage"));
				pageQueryResult.setAuthorId(albumInfo.optString("authorId"));
				pageQueryResult.setBrief(albumInfo.getString("title"));
			}

			// 视频列表
			String url = "https://www.acfun.cn/rest/pc-direct/arubamu/content/list?page=%d&size=%d&arubamuId=%s";
			url = String.format(url, page, API_PMAX, spaceID);
			String json = util.getContent(url, new HttpHeaders().getCommonHeaders("www.acfun.cn"), HttpCookies.getGlobalCookies());
			Logger.println(json);
			JSONArray jArray = new JSONObject(json).getJSONArray("contents");

			LinkedHashMap<Long, ClipInfo> map = pageQueryResult.getClips();
			for (int i = min - 1; i < jArray.length() && i < max; i++) {
				try {
					map.putAll(convertVideoToClipMap(
							"ac" + jArray.getJSONObject(i).optString("resourceId"), 
							(page - 1) * API_PMAX + i + 1, 
							videoFormat,
							getVideoLink));
				}catch (Exception e) {
					
				}
			}
			return true;
		} catch (Exception e) {
			// e.printStackTrace();
			return false;
		}
	}

	/**
	 * 使用此方法会产生许多请求，慎用
	 * 
	 * @param acId
	 * @param remark
	 * @param videoFormat
	 * @param getVideoLink
	 * @return 将所有avId的视频封装成Map
	 */
	private LinkedHashMap<Long, ClipInfo> convertVideoToClipMap(String acId, int remark, int videoFormat,
			boolean getVideoLink) {
		LinkedHashMap<Long, ClipInfo> map = new LinkedHashMap<>();
		VideoInfo video = getAVDetail(acId, videoFormat, getVideoLink); // 耗时
		for (ClipInfo clip : video.getClips().values()) {
			try {
				//clip.setTitle(clip.getAvTitle() + "-" + clip.getTitle());
				//clip.setAvTitle(pageQueryResult.getVideoName());
				// >= V3.6, ClipInfo 增加可选ListXXX字段，将收藏夹信息移入其中
				clip.setListName(pageQueryResult.getBrief());
				clip.setListOwnerName(pageQueryResult.getAuthor());
				
				clip.setRemark(remark);
				map.put(clip.getcId(), clip);
			}catch (Exception e) {
				e.printStackTrace();
			}
		}
		return map;
	}
}
