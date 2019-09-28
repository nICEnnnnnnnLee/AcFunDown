package nicelee.acfun.parsers.impl;

import java.util.regex.Pattern;

import nicelee.acfun.annotations.Acfun;
import nicelee.acfun.model.VideoInfo;

@Acfun(name = "aaParser detail", note = "番剧单集")
public class AAParser_Detail extends ABParser {

	private final static Pattern pattern = Pattern.compile("aa[0-9]+_[0-9]+_[0-9]+");
	private String albumId_groupId_id;

	// public EPParser(HttpRequestUtil util,IParamSetter paramSetter, int pageSize)
	// {
	public AAParser_Detail(Object... obj) {
		super(obj);
	}

	@Override
	public boolean matches(String input) {
		matcher = pattern.matcher(input);
		boolean matches = matcher.find();
		if (matches) {
			albumId_groupId_id = matcher.group();
		}
		return matches;
	}

	@Override
	public String validStr(String input) {
		return albumId_groupId_id;
	}

	@Override
	public VideoInfo result(String input, int videoFormat, boolean getVideoLink) {
		return getAVDetail(albumId_groupId_id, getVideoLink);
	}


}
