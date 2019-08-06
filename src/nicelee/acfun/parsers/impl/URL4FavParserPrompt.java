package nicelee.acfun.parsers.impl;

import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONObject;

import nicelee.acfun.annotations.Acfun;
import nicelee.acfun.model.VideoInfo;
import nicelee.acfun.util.HttpCookies;
import nicelee.acfun.util.HttpHeaders;

@Acfun(name = "URL4FavParser", ifLoad = "promptAll", note = "个人收藏的视频列表")//promptAll
public class URL4FavParserPrompt extends AbstractPageQueryParser<StringBuilder> {

	private final static Pattern pattern = Pattern.compile("#area=favourite[^-]*");
	private String spaceID;

	public URL4FavParserPrompt(Object... obj) {
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
		return result(pageSize, paramSetter.getPage(), spaceID).toString();
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		System.err.println("URL4FavParser 解析器不支持该方法！！");
		return null;
	}

	@Override
	public void initPageQueryParam() {
		API_PMAX = 10;
		pageQueryResult = new StringBuilder();
	}

	@Override
	protected boolean query(int page, int min, int max, Object... obj) {
		try {
			String urlFormat = "https://www.acfun.cn/member/collection.aspx?count=%d&pageNo=%d&channelId=%d";
			String url = String.format(urlFormat, API_PMAX, page, paramSetter.getChannelId());
			String json = util.getContent(url, new HttpHeaders().getCommonHeaders("www.acfun.cn"), HttpCookies.getGlobalCookies());
			System.out.println(url);
			System.out.println(json);
			JSONArray jArray = new JSONObject(json).getJSONArray("contents");

			for (int i = min - 1; i < jArray.length() && i < max; i++) {
				pageQueryResult.append(" ac").append(jArray.getJSONObject(i).getLong("aid"));
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
