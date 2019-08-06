package nicelee.acfun.downloaders.impl;

import java.io.File;
import java.util.LinkedList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nicelee.acfun.annotations.Acfun;
import nicelee.acfun.enums.StatusEnum;
import nicelee.acfun.util.CmdUtil;
import nicelee.acfun.util.HttpHeaders;
import nicelee.acfun.util.Logger;
import nicelee.ui.Global;

@Acfun(name = "m3u8-downloader", type = "downloader", note = "m3u8")
public class M3u8Downloader extends FLVDownloader {

	@Override
	public boolean matches(String url) {
		if (url.contains(".m3u8")) {
			return true;
		}
		return false;
	}

	/**
	 * 下载视频
	 * 
	 * @param url
	 * @param avId
	 * @param qn
	 * @param page
	 * @return
	 */
	@Override
	public boolean download(String url, String avId, int qn, int page) {
		// 前期准备
		String suffix = ".ts";
		convertingStatus = StatusEnum.NONE;
		currentTask = 1;
		String fName = avId + "-" + qn + "-p" + page;
		HttpHeaders header = new HttpHeaders();
		if (file == null) {
			file = new File(Global.savePath + fName, fName + ".mp4");
			file.getParentFile().mkdirs();
		}
		
		// 由m3u8获取ts列表
		LinkedList<String> links = new LinkedList<String>();
		String m3u8Content = util.getContent(url, header.getCommonHeaders());
		Logger.println(url);
		//Logger.println(m3u8Content);
		String[] lines =  m3u8Content.split("\r?\n");
		for(String line: lines) {
			if(!line.startsWith("#") && !line.isEmpty()) {
				// 如果是相对路径，补全
				if(!line.startsWith("http")) {
					line = genABUrl(line, url);
					links.add(line);
					//Logger.println(line);
				}
			}
		}
		
		totalTaskCnt = links.size();
//		Pattern numUrl = Pattern.compile("^([0-9]+)(http.*)$");
		if (util.getStatus() == StatusEnum.STOP)
			return false;
		// 从 currentTask 继续开始任务
		util.init();
		for (int i = currentTask - 1; i < links.size(); i++) {
			currentTask = (i + 1);
			String order = "" + currentTask;
			String tUrl = links.get(i);
			String fileName = fName + File.separatorChar + fName + "-part" + order + suffix;
			if (!util.download(tUrl, fileName, header.getEmptyHeaders())) {
				return false;
			}
			sumSuccessDownloaded += util.getTotalFileSize();
			util.reset();
		}
		// 下载完毕后,进行合并
		convertingStatus = StatusEnum.PROCESSING;
		boolean result = CmdUtil.convertM3u8(fName, links.size());
		if (result) {
			convertingStatus = StatusEnum.SUCCESS;
		} else {
			convertingStatus = StatusEnum.FAIL;
		}
		return result;
	}
	
	/**
	 * 生成绝对路径
	 */
	final static Pattern hostPattern = Pattern.compile("^https?\\://[^/]+");
	final static Pattern rootPattern = Pattern.compile("^https?\\://.*/");
	public static String genABUrl(String url, String parentUrl) {
		if(url.startsWith("http")) {
			// 如果是绝对路径，直接返回
			return url;
		}else if(url.startsWith("//")) {
			// 如果缺scheme，补上https?
			if(parentUrl.startsWith("https")) {
				return "https"+ url;
			}else {
				return "http"+ url;
			}
		}else if(url.startsWith("/")) {
			// 补上host
			Matcher m1 = hostPattern.matcher(parentUrl);
			m1.find();
			return m1.group() + url;
		}else {
			// 纯相对路径
			Matcher m2 = rootPattern.matcher(parentUrl);
			m2.find();
			return m2.group() + url;
		}
	}
}
