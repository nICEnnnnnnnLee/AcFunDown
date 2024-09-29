package nicelee.acfun.downloaders.impl;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.util.LinkedList;
import java.util.Map.Entry;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nicelee.acfun.annotations.Acfun;
import nicelee.acfun.enums.StatusEnum;
import nicelee.acfun.util.CmdUtil;
import nicelee.acfun.util.HttpHeaders;
import nicelee.acfun.util.Logger;
import nicelee.ui.Global;
import nicelee.ui.item.DownloadInfoPanel;

@Acfun(name = "m3u8-downloader：打印输出到文件", type = "downloader", note = "m3u8")
public class AM3u8Downloader extends FLVDownloader {

	static BufferedWriter output;

	static {
		try {
            System.out.println("------------打开文件------------");
			FileOutputStream f = new FileOutputStream("download.txt", true);
			output = new BufferedWriter(new OutputStreamWriter(f, "utf-8"));
			Runtime.getRuntime().addShutdownHook(new Thread(() -> {
				try {
					System.out.println("------------关闭文件占用------------");
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}));
		} catch (FileNotFoundException | UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	}

	synchronized static String append(String data) {
		try {
			output.append(data);
			output.newLine();
			output.flush();
			return null;
		} catch (IOException e) {
			e.printStackTrace();
			return e.getMessage();
		}

	}

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
		convertingStatus = StatusEnum.NONE;
		currentTask = totalTaskCnt = 1;
		sumSuccessDownloaded = 0L;
		String fName = avId + "-" + qn + "-p" + page;
		if (file == null) {
			file = new File(Global.savePath + fName, fName + ".mp4");
		}
		DownloadInfoPanel d = null;
		for (DownloadInfoPanel dip : Global.downloadTaskList.keySet()) {
			if (dip.getAvid().equals(avId) && dip.getPage() == page) {
				d = dip;
				break;
			}
		}
		String info = String.format("%s - %s - %s", fName, d.formattedTitle, url);
		errorInfo = append(info);
		if (errorInfo == null) {
			convertingStatus = StatusEnum.SUCCESS;
			return true;
		} else {
			convertingStatus = StatusEnum.FAIL;
			return false;
		}
	}

}
