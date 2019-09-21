package nicelee.acfun.downloaders.impl;

import java.io.File;

import nicelee.acfun.annotations.Acfun;
import nicelee.acfun.enums.StatusEnum;
import nicelee.acfun.util.CmdUtil;
import nicelee.acfun.util.HttpHeaders;
import nicelee.ui.Global;


@Acfun(name = "m4s-downloader", 
	type = "downloader",
	note = "音视频分流下载, 完成后合成MP4")
public class M4SDownloader extends FLVDownloader{


	
	@Override
	public boolean matches(String url) {
		if(url.contains(".m4s")) {
			return true;
		}
		return false;
	}
	
	/**
	 * 下载视频
	 * @param url
	 * @param avId
	 * @param qn
	 * @param page
	 * @return
	 */
	@Override
	public boolean download(String url, String avId, int qn, int page) {
		convertingStatus = StatusEnum.NONE;
		errorInfo = null;
		HttpHeaders header = new HttpHeaders();
		String links[] = url.split("#");
		String fName = avId + "-" + qn + "-p" + page;
		String suffix = ".mp4";
		String videoName = fName + "_video.m4s";
		String audioName = fName + "_audio.m4s";
		String dstName = fName + suffix;
		if(file == null) {
			file = new File(Global.savePath, dstName);
		}
		totalTaskCnt = 2;
		if (util.download(links[0], videoName, header.getBiliWwwM4sHeaders(avId))) {
			// 如下载成功，统计数据后重置
			sumSuccessDownloaded += util.getTotalFileSize();
			util.reset();
			currentTask = 2;
			if(util.getStatus() == StatusEnum.STOP)
				return false;
			util.init();
			if (util.download(links[1], audioName, header.getBiliWwwM4sHeaders(avId))) {
				// 如下载成功，统计数据后重置
				sumSuccessDownloaded += util.getTotalFileSize();
				util.reset();
				// 下载完毕后,进行合并
				convertingStatus = StatusEnum.PROCESSING;
				boolean result = CmdUtil.convert(videoName, audioName, dstName);
				if (result) {
					convertingStatus = StatusEnum.SUCCESS;
				} else {
					errorInfo = "M4S文件转码失败";
					convertingStatus = StatusEnum.FAIL;
				}
				return result;
			}
			return false;
		}
		return false;
	}

}
