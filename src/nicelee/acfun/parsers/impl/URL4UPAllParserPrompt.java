package nicelee.acfun.parsers.impl;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONObject;

import nicelee.acfun.annotations.Acfun;
import nicelee.acfun.model.VideoInfo;
import nicelee.acfun.util.HttpHeaders;

@Acfun(name = "URL4UPAllParser", ifLoad = "promptAll", note = "个人上传的视频列表")//promptAll
public class URL4UPAllParserPrompt extends AbstractPageQueryParser<StringBuilder> {

	private final static Pattern pattern = Pattern.compile("acfun\\.cn/u/([0-9]+)\\.aspx");
	private String spaceID;

	public URL4UPAllParserPrompt(Object... obj) {
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
		//return getAVList4Space(spaceID, paramSetter.getPage());
		return result(pageSize, paramSetter.getPage(), spaceID).toString();
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		System.err.println("URL4UPAllParser 解析器不支持该方法！！");
		return null;
	}

	@Override
	public void initPageQueryParam() {
		API_PMAX = 20;
		pageQueryResult = new StringBuilder();
	}

	private final static Pattern acPattern = Pattern.compile("<a href=\"/v/(ac[0-9]+)\"");
	@Override
	protected boolean query(int page, int min, int max, Object... obj) {
		try {
			String urlFormat = "https://www.acfun.cn/space/next?uid=%s&type=video&orderBy=2&pageNo=%d";
			String url = String.format(urlFormat, spaceID, page);
			String json = util.getContent(url, new HttpHeaders().getCommonHeaders("www.acfun.cn"));
			System.out.println(url);
			System.out.println(json);
			JSONObject jobj = new JSONObject(json);
			String results[] = jobj.getJSONObject("data").getString("html").split("播放中");
			for (int i = min - 1; i < results.length && i < max; i++) {
				Matcher matcher = acPattern.matcher(results[i]);
				matcher.find();
				pageQueryResult.append(" ").append(matcher.group(1));
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}
}
