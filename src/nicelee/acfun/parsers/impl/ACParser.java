package nicelee.acfun.parsers.impl;

import java.util.regex.Pattern;

import nicelee.acfun.annotations.Acfun;
import nicelee.acfun.model.VideoInfo;

@Acfun(name = "ac", note="普通视频")
public class ACParser extends AbstractBaseParser {

	private final static Pattern pattern = Pattern.compile("ac[0-9]+");
	private String avId;

//	public AVParser(HttpRequestUtil util,IParamSetter paramSetter, int pageSize) {
	public ACParser(Object... obj) {
		super(obj);
	}

	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		boolean matches = matcher.find();
		if (matches) {
			avId = matcher.group();
			System.out.println("匹配ACParser: " + avId);
		}
		return matches;
	}

	@Override
	public String validStr(String input) {
		return avId;
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		System.out.println("ACParser正在获取结果" + avId);
		return getAVDetail(avId, videoFormat, getVideoLink);
	}

}
