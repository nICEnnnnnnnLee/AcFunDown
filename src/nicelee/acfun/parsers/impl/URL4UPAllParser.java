package nicelee.acfun.parsers.impl;

import java.util.LinkedHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import nicelee.acfun.annotations.Acfun;
import nicelee.acfun.model.ClipInfo;
import nicelee.acfun.model.VideoInfo;
import nicelee.acfun.util.HttpHeaders;
import nicelee.acfun.util.Logger;

@Acfun(name = "URL4UPAllParser", ifLoad = "listAll", note = "个人上传的视频列表")
public class URL4UPAllParser extends AbstractPageQueryParser<VideoInfo> {

	private final static Pattern pattern = Pattern.compile("acfun\\.cn/u/([0-9]+)\\.aspx");
	private String spaceID;

	public URL4UPAllParser(Object... obj) {
		super(obj);
	}

	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		if (matcher.find()) {
			System.out.println("匹配UP主主页全部视频,返回 ac1 ac2 ac3 ...");
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

	private final static Pattern acPattern = Pattern.compile("<a href=\"/v/(ac[0-9]+)\"");
	@Override
	protected boolean query(int page, int min, int max, Object... obj) {
		int videoFormat = (int) obj[0];
		boolean getVideoLink = (boolean) obj[1];
		try {
			// UP主信息
			String indexUrl = String.format("https://www.acfun.cn/u/%s.aspx", spaceID);
			String indexHtml = util.getContent(indexUrl, new HttpHeaders().getCommonHeaders("www.acfun.cn"));
			int begin = indexHtml.indexOf("<script>var UPUser = ");
			int end = indexHtml.indexOf("var pageList =", begin);
			String userJson = indexHtml.substring(begin + 21, end);
			JSONObject jUser = new JSONObject(userJson);
			
			if (pageQueryResult.getVideoName() == null) {
				pageQueryResult.setVideoId(spaceID);
				pageQueryResult.setAuthor(jUser.getString("username"));
				pageQueryResult.setVideoName(pageQueryResult.getAuthor() + "的视频列表");
				pageQueryResult.setVideoPreview(jUser.getString("userImg"));
				pageQueryResult.setAuthorId(spaceID);
				pageQueryResult.setBrief("视频列表 - " + paramSetter.getPage());
			}

			// UP主视频列表
			String urlFormat = "https://www.acfun.cn/space/next?uid=%s&type=video&orderBy=2&pageNo=%d";
			String url = String.format(urlFormat, spaceID, page);
			String json = util.getContent(url, new HttpHeaders().getCommonHeaders("www.acfun.cn"));
			System.out.println(url);
			System.out.println(json);
			JSONObject jobj = new JSONObject(json);
			
			String results[] = jobj.getJSONObject("data").getString("html").split("播放中");
			LinkedHashMap<Long, ClipInfo> map = pageQueryResult.getClips();
			for (int i = min - 1; i < results.length && i < max; i++) {
				Matcher matcher = acPattern.matcher(results[i]);
				matcher.find();
				map.putAll(convertVideoToClipMap(
						matcher.group(1), 
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
