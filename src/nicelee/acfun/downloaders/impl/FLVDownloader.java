package nicelee.acfun.downloaders.impl;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nicelee.acfun.annotations.Acfun;
import nicelee.acfun.downloaders.IDownloader;
import nicelee.acfun.enums.StatusEnum;
import nicelee.acfun.util.CmdUtil;
import nicelee.acfun.util.HttpHeaders;
import nicelee.acfun.util.HttpRequestUtil;
import nicelee.ui.Global;


@Acfun(name = "flv-downloader", 
	type = "downloader",
	note = "FLV下载")
public class FLVDownloader implements IDownloader {

	protected HttpRequestUtil util;
	protected File file = null;
	protected int currentTask = 1;
	protected int totalTaskCnt = 1;
	protected StatusEnum convertingStatus = StatusEnum.NONE;
	protected long sumSuccessDownloaded = 0;

	@Override
	public boolean matches(String url) {
		if(url.contains(".flv")) {
			return true;
		}
		return false;
	}
	
	@Override
	public void init(HttpRequestUtil util) {
		this.util = util;
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
		return download(url, avId, qn, page, ".flv");
	}
	
	protected boolean download(String url, String avId, int qn, int page, String suffix) {
		convertingStatus = StatusEnum.NONE;
		currentTask = 1;
		String fName = avId + "-" + qn + "-p" + page;
		HttpHeaders header = new HttpHeaders();
		if(file == null) {
			file = new File(Global.savePath, fName + suffix);
		}
		if (url.contains("#")) {
			String links[] = url.split("#");
			totalTaskCnt = links.length;
			Pattern numUrl = Pattern.compile("^([0-9]+)(http.*)$");
			if(util.getStatus() == StatusEnum.STOP)
				return false;
			// 从 currentTask 继续开始任务
			util.init();
			for (int i = currentTask - 1; i < links.length; i++) {
				currentTask = (i + 1);
				Matcher matcher = numUrl.matcher(links[i]);
				matcher.find();
				String order = matcher.group(1);
				String tUrl = matcher.group(2);
				String fileName = fName + "-part" + order + suffix;
				if (!util.download(tUrl, fileName, header.getBiliWwwFLVHeaders(avId))) {
					return false;
				}
				sumSuccessDownloaded += util.getTotalFileSize();
				util.reset();
			}
			// 下载完毕后,进行合并
			convertingStatus = StatusEnum.PROCESSING;
			boolean result = CmdUtil.convert(fName + suffix, links.length);
			if (result) {
				convertingStatus = StatusEnum.SUCCESS;
			} else {
				convertingStatus = StatusEnum.FAIL;
			}
			return result;
		} else {
			String fileName = fName + suffix;
			boolean succ = util.download(url, fileName, header.getBiliWwwFLVHeaders(avId));
			if (succ) {
				sumSuccessDownloaded += util.getTotalFileSize();
				util.reset();
			}
			return succ;
		}
	}

	/**
	 * 返回当前状态
	 * 
	 * @return
	 */
	@Override
	public StatusEnum currentStatus() {
		// totalTask
		// currentTask
		// util status; // 0 正在下载; 1 下载完毕; -1 出现错误; -2 人工停止;-3 队列中
		if(file != null && file.exists() && (convertingStatus == StatusEnum.SUCCESS || convertingStatus == StatusEnum.NONE)) {
			return StatusEnum.SUCCESS;
		}
		//System.out.println("转码状态： " + convertingStatus.getDescription());
		//System.out.println("下载工具状态： " + util.getStatus().getDescription());
		// 如果当前是最后一个任务
		if (currentTask == totalTaskCnt) {
			// 当前任务转码状态判断
			if (convertingStatus == StatusEnum.SUCCESS) {
				return StatusEnum.SUCCESS;
			} else if (convertingStatus == StatusEnum.FAIL) {
				return StatusEnum.FAIL;
			} else if(convertingStatus == StatusEnum.PROCESSING){
				return StatusEnum.PROCESSING;
			} else { //与转码无关
				return util.getStatus();
			}
		}

		switch (util.getStatus()) {
		case DOWNLOADING:
			return StatusEnum.DOWNLOADING;
		case STOP:
			return StatusEnum.STOP;
		case FAIL:
			return StatusEnum.FAIL;
		case SUCCESS: {
			return StatusEnum.DOWNLOADING;
		}
		default:
			return StatusEnum.NONE;// 还未处理，在队列中
		}
	}

	/**
	 * 返回总任务数
	 * 
	 * @return
	 */
	@Override
	public int totalTaskCount() {
		return totalTaskCnt;
	}

	/**
	 * 返回当前第几个任务
	 * 
	 * @return
	 */
	@Override
	public int currentTask() {
		return currentTask;
	}
	
	@Override
	public void startTask() {
		util.init();
	}
	@Override
	public void stopTask() {
		util.stopDownload();
	}

	@Override
	public long sumTotalFileSize() {
		return sumSuccessDownloaded + util.getTotalFileSize();
	}

	@Override
	public long sumDownloadedFileSize() {
		return sumSuccessDownloaded + util.getDownloadedFileSize();
	}

	@Override
	public long currentFileDownloadedSize() {
		return util.getDownloadedFileSize();
	}

	@Override
	public long currentFileTotalSize() {
		return util.getTotalFileSize();
	}
	@Override
	public File file() {
		return file;
	}

}
