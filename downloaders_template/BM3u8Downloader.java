package nicelee.acfun.downloaders.impl;

import java.io.File;

import nicelee.acfun.annotations.Acfun;
import nicelee.acfun.enums.StatusEnum;
import nicelee.acfun.util.CmdUtil;
import nicelee.acfun.util.Logger;
import nicelee.ui.Global;

@Acfun(name = "m3u8-downloader：调用N_m3u8DL-CLI", type = "downloader", note = "m3u8")
public class BM3u8Downloader extends FLVDownloader {

	final static String M3U8_PATH;
	static {
		M3U8_PATH = System.getenv().getOrDefault("m3u8_path", "C:\\Users\\user\\Downloads\\N_m3u8DL-CLI_v2.6.3_with_ffmpeg_and_SimpleG\\N_m3u8DL-CLI_v3.0.2.exe");
		Logger.println("------加载m3u8-downloader：调用N_m3u8DL-CLI-------");
		Logger.println(M3U8_PATH);
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
		convertingStatus = StatusEnum.DOWNLOADING;
		currentTask = totalTaskCnt = 1;
		sumSuccessDownloaded = 0L;
		String fName = avId + "-" + qn + "-p" + page;
		if (file == null) {
			file = new File(Global.savePath + fName, fName + ".mp4");
		}

		String cmd[] = new String[] {
				M3U8_PATH,
				url, "--workDir", Global.savePath, "--saveName", fName, "--headers",
				"User-Agent:Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:124.0) Gecko/20100101 Firefox/124.0",
				"--maxThreads", "4", "--minThreads", "2", "--retryCount", "5", "--timeOut", "120",
				"--enableDelAfterDone", "--disableDateInfo" };
		boolean result = CmdUtil.run(cmd);

		if (result) {
			convertingStatus = StatusEnum.SUCCESS;
			return true;
		} else {
			errorInfo = "N_m3u8DL-CLI下载失败";
			convertingStatus = StatusEnum.FAIL;
			return false;
		}
	}

}
