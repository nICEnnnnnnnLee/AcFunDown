package nicelee.acfun.parsers.impl;

import java.util.LinkedHashMap;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.acfun.annotations.Acfun;
import nicelee.acfun.enums.ChannelEnum;
import nicelee.acfun.model.ClipInfo;
import nicelee.acfun.model.VideoInfo;
import nicelee.acfun.util.HttpCookies;
import nicelee.acfun.util.HttpHeaders;
import nicelee.acfun.util.Logger;

@Acfun(name = "URL4FavParser", ifLoad = "listAll", note = "个人收藏的视频列表")
public class URL4FavParser extends AbstractPageQueryParser<VideoInfo> {

	private final static Pattern pattern = Pattern.compile("#area=favourite[^-]*");
	private String spaceID;

	public URL4FavParser(Object... obj) {
		super(obj);
	}

	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		if (matcher.find()) {
			System.out.println("匹配UP主主页全部视频,返回 ac1 ac2 ac3 ...");
			spaceID = matcher.group();
			return true;
		} else {
			return false;
		}

	}

	@Override
	public String validStr(String input) {
		return matcher.group().trim() + "&p=" + paramSetter.getPage() + "&channelId=" + paramSetter.getChannelId();
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

	@Override
	protected boolean query(int page, int min, int max, Object... obj) {
		int videoFormat = (int) obj[0];
		boolean getVideoLink = (boolean) obj[1];
		try {
			if (pageQueryResult.getVideoName() == null) {
				pageQueryResult.setVideoId(spaceID);
				pageQueryResult.setAuthor("我");
				Logger.println("getChannelId: " + paramSetter.getChannelId());
				pageQueryResult.setVideoName("我的视频列表-" + ChannelEnum.getDescription(paramSetter.getChannelId()));
				pageQueryResult.setVideoPreview("https://imgs.aixifan.com/style/image/defaultAvatar.jpg");
				pageQueryResult.setAuthorId(spaceID);
				pageQueryResult.setBrief("视频列表 - " + paramSetter.getPage());
			}

			// 视频列表
			String urlFormat = "https://www.acfun.cn/member/collection.aspx?count=%d&pageNo=%d&channelId=%d";
			String url = String.format(urlFormat, API_PMAX, page, paramSetter.getChannelId());
			String json = util.getContent(url, new HttpHeaders().getCommonHeaders("www.acfun.cn"), HttpCookies.getGlobalCookies());
			System.out.println(url);
			System.out.println(json);
			JSONArray jArray = new JSONObject(json).getJSONArray("contents");

			LinkedHashMap<Long, ClipInfo> map = pageQueryResult.getClips();
			for (int i = min - 1; i < jArray.length() && i < max; i++) {
				map.putAll(convertVideoToClipMap(
						"ac" + jArray.getJSONObject(i).getLong("aid"), 
						(page - 1) * API_PMAX + i + 1, 
						videoFormat,
						getVideoLink));
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
			//clip.setTitle(clip.getAvTitle() + "-" + clip.getTitle());
			//clip.setAvTitle(pageQueryResult.getVideoName());
			// >= V3.6, ClipInfo 增加可选ListXXX字段，将收藏夹信息移入其中
			clip.setListName(pageQueryResult.getVideoName());
			clip.setListOwnerName(pageQueryResult.getAuthor());
			
			clip.setRemark(remark);
			map.put(clip.getcId(), clip);
		}
		return map;
	}
}
