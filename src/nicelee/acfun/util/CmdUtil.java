package nicelee.acfun.util;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.ProcessBuilder.Redirect;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nicelee.ui.Global;

public class CmdUtil {

	private static final File NULL_FILE = new File(
            (System.getProperty("os.name")
                    .startsWith("Windows") ? "NUL" : "/dev/null")
    );
	private static final Redirect DISCARD = Redirect.to(NULL_FILE); // 为了兼容 java8
	
	public static boolean run(String cmd[]) {
		Process process = null;
		try {
			ProcessBuilder pb = new ProcessBuilder(cmd);
            if(Global.debugFFmpeg) {
            	pb.redirectOutput(Redirect.INHERIT);
            	pb.redirectError(Redirect.INHERIT);
            }else {
            	pb.redirectOutput(DISCARD);
            	pb.redirectError(DISCARD);
            }
			process = pb.start();
			process.waitFor();
			System.out.println("process 执行完毕");
			return true;
		} catch (Exception e) {
			// e.printStackTrace();
			Logger.println(e.toString());
			return false;
		}
	}

	/**
	 * 音视频合并转码
	 * 
	 * @param videoName
	 * @param audioName
	 * @param dstName
	 */
	public static boolean convert(String videoName, String audioName, String dstName) {
		String cmd[] = createConvertCmd(videoName, audioName, dstName);
		File mp4File = new File(Global.savePath + dstName);
		File video = new File(Global.savePath + videoName);
		File audio = new File(Global.savePath + audioName);
		if (!mp4File.exists()) {
			Logger.println("下载完毕, 正在运行转码程序...");
			run(cmd);
			if (mp4File.exists() && mp4File.length() > video.length()) {
				video.delete();
				audio.delete();
				return true;
			}
			Logger.println("转码完毕");
		} else {
			Logger.println("下载完毕");
			return true;
		}
		return false;
	}

	/**
	 * 片段合并转码(FLV)
	 * 
	 * @param videoName
	 * @param audioName
	 * @param dstName
	 */
	public static boolean convert(String dstName, int part) {
		String cmd[] = createConvertCmd(dstName, part);
		File videoFile = new File(Global.savePath + dstName);
		if (!videoFile.exists()) {
			Logger.println("下载完毕, 正在运行转码程序...");
			run(cmd);
			Logger.println("转码完毕");
			// 删除文件
			if (videoFile.exists()) {
				Matcher matcher = filePattern.matcher(dstName);
				matcher.find();
				String prefix = matcher.group(1);
				String suffix = matcher.group(2);
				List<File> fList = new ArrayList<File>();
				long fSize = 0;
				for (int i = 1; i <= part; i++) {
					File file = new File(Global.savePath + prefix + "-part" + i + suffix);
					fList.add(file);
					fSize += file.length();
				}
				Logger.println("转码后文件大小: " + videoFile.length());
				Logger.println("转码前文件大小和: " + fSize);
				if (videoFile.length() >= fSize * 0.8) {
					for (File f : fList) {
						f.delete();
					}
					// new File(Global.savePath + dstName + ".txt").delete();
					deleteAllInactiveCmdTemp();
					return true;
				}
			}
		} else {
			Logger.println("下载完毕");
			return true;
		}
		return false;
	}
	
	/**
	 * 片段合并转码(m3u8)
	 * 
	 * @param videoName
	 * @param audioName
	 * @param dstName
	 */
	public static boolean convertM3u8(String dstName, int part) {
		String cmd[] = createMergeM3u8Cmd(dstName, part);
		File videoFile = new File(Global.savePath + dstName + ".mp4");
		if (!videoFile.exists()) {
			Logger.println("下载完毕, 正在运行转码程序...");
			if(!run(cmd)) {
				Logger.println("M3U8转码异常");
				return false;
			}
			Logger.println("转码完毕");
			// 删除文件
			if (videoFile.exists()) {
				String prefix = dstName;
				String suffix = ".ts";
				List<File> fList = new ArrayList<File>();
				long fSize = 0;
				for (int i = 1; i <= part; i++) {
					File file = new File(Global.savePath + prefix + "/" + prefix + "-part" + i + suffix);
					fList.add(file);
					fSize += file.length();
					Logger.println(file.getAbsolutePath());
				}
				Logger.println("转码后文件大小: " + videoFile.length());
				Logger.println("转码前文件大小和: " + fSize);
				if (videoFile.length() >= fSize * 0.8) {
					for (File f : fList) {
						f.delete();
					}
					fList.get(0).getParentFile().delete();
					// new File(Global.savePath + dstName + ".txt").delete();
					deleteAllInactiveCmdTemp();
					return true;
				}
			}
		} else {
			Logger.println("下载完毕");
			deleteAllInactiveCmdTemp();
			return true;
		}
		return false;
	}
	
	/**
	 * ts合并转码命令
	 * 
	 * @param dstName
	 * @param part
	 * @return
	 */
	public static String[] createMergeM3u8Cmd(String dstName, int part) {
		try {
			String prefix = dstName + "/" + dstName;
			String suffix = ".ts";
			File folderDown = new File(Global.savePath);
			folderDown.mkdirs();
			File file = new File(folderDown, dstName + ".mp4.txt");
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for (int i = 1; i <= part; i++) {
				bw.write("file '");
				bw.write(prefix + "-part" + i + suffix);
				bw.write("'\r\n");
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String cmd[] = { "ffmpeg", "-f", "concat", "-safe", "0", "-i", Global.savePath + dstName + ".mp4.txt", "-c", "copy",
				Global.savePath + dstName + ".mp4" };
		return cmd;
	}

	/**
	 * 音视频合并转码命令
	 * 
	 * @param dstName
	 * @return
	 */
	final static Pattern filePattern = Pattern.compile("^(.*)(\\.(mp4|flv))$");

	public static String[] createConvertCmd(String dstName, int part) {
		try {
			Matcher matcher = filePattern.matcher(dstName);
			matcher.find();
			String prefix = matcher.group(1);
			String suffix = matcher.group(2);
			File folderDown = new File(Global.savePath);
			folderDown.mkdirs();
			File file = new File(folderDown, dstName + ".txt");
			BufferedWriter bw = new BufferedWriter(new FileWriter(file));
			for (int i = 1; i <= part; i++) {
				bw.write("file '");
				bw.write(prefix + "-part" + i + suffix);
				bw.write("'\r\n");
			}
			bw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		String cmd[] = { "ffmpeg", "-f", "concat", "-safe", "0", "-i", Global.savePath + dstName + ".txt", "-c", "copy",
				Global.savePath + dstName };
		return cmd;
	}

	/**
	 * 删除已经生效过的临时cmd 文件
	 * 
	 * 类似于
	 * 
	 * @ex1 av12345-64-p1.flv.txt
	 * @ex2 av12345-64-p2-part1.flv // 在转码判断里面删除，防止误删
	 * @ex3 av12345-64-p2-part1.flv.part
	 * @ex4 av12345-64-p3.mp4.part
	 * 
	 * @return
	 */
	final static Pattern cmdFolderPattern = Pattern.compile("^a[vabc][0-9_]+-[0-9]+-p[0-9]+$");
	final static Pattern cmdTxtPattern = Pattern.compile("^(a[vabc][0-9_]+-[0-9]+-p[0-9]+\\.(flv|mp4))\\.txt$");
	final static Pattern cmdDonePartPattern = Pattern.compile("^a[vabc][0-9_]+-[0-9]+-p[0-9]+-part[0-9]+\\.(flv|mp4)$");
	final static Pattern cmdPartPattern = Pattern.compile("^(.*)\\.part$");
	final static Pattern standardFileNamePattern = Pattern.compile("^a[vabc][0-9_]+-[0-9]+-p[0-9]+\\.(flv|mp4)$");

	public static void deleteAllInactiveCmdTemp() {
		// 找到下载文件夹
		File folderDown = new File(Global.savePath);
		// 筛选下载文件夹
		FilenameFilter filter = new FilenameFilter() {
			@Override
			public boolean accept(File dir, String name) {
				// 文件夹，如果已经存在转换完的对应视频，则可以删除
				Matcher matcherFolder = cmdFolderPattern.matcher(name);
				if (matcherFolder.find()) {
					File file = new File(dir, matcherFolder.group() + ".mp4");
					if (file.exists()) {
						return true;
					}
					return false;
				}
				// txt文件，如果已经存在转换完的对应视频，则可以删除
				Matcher matcherTxt = cmdTxtPattern.matcher(name);
				if (matcherTxt.find()) {
					File file = new File(dir, matcherTxt.group(1));
					if (file.exists()) {
						return true;
					}
					return false;
				}
				if (Global.restrictTempMode) {
					// .part文件，如果已经存在转换完的对应视频，则可以删除
					Matcher matcherPart = cmdPartPattern.matcher(name);
					if (matcherPart.find()) {
						String fName = matcherPart.group(1).replaceFirst("-part[0-9]+", "");
						if (standardFileNamePattern.matcher(fName).matches()) {
							File file = new File(dir, fName);
							if (file.exists()) {
								return true;
							}
						}
						return false;
					}
//					// 部分完成了的flv|mp4文件，如果已经存在转换完的对应视频，则可以删除
//					Matcher matcherDonePart = cmdDonePartPattern.matcher(name);
//					if (matcherDonePart.find()) {
//						File file = new File(dir, matcherDonePart.group().replaceFirst("-part[0-9]+", ""));
//						if (file.exists()) {
//							return true;
//						}
//						return false;
//					}
				}
				return false;
			}
		};
		if(folderDown.exists()) {
			// 删除下载文件
			for (File file : folderDown.listFiles(filter)) {
				System.out.println("尝试删除" + file.getName());
				file.delete();
			}
		}
	}

	/**
	 * 视频片段合并转码命令
	 * 
	 * @param videoName
	 * @param audioName
	 * @param dstName
	 * @return
	 */
	public static String[] createConvertCmd(String videoName, String audioName, String dstName) {
		String cmd[] = { "ffmpeg", "-i", Global.savePath + videoName, "-i", Global.savePath + audioName, "-c", "copy",
				Global.savePath + dstName };
		String str = String.format("ffmpeg命令为: \r\nffmpeg -i %s -i %s -c copy %s", Global.savePath + videoName,
				Global.savePath + audioName, Global.savePath + dstName);
		Logger.println(str);
		return cmd;
	}

	/**
	 * 下载成功后重命名 或者 追加重命名文件
	 * 
	 * @param avid_q
	 * @param formattedTitle
	 * @throws IOException
	 */
	// public static boolean doRenameAfterComplete = true;
	public synchronized static void convertOrAppendCmdToRenameBat(final String avid_q, final String formattedTitle,
			int page) {
		try {
			// 获取已完成文件
			File originFile = getFileByAvQnP(avid_q, page);
			String fName = originFile.getName();
			String tail = fName.substring(fName.length() - 4);

			if (Global.doRenameAfterComplete) {
				File file = new File(Global.savePath, formattedTitle + tail);
				originFile.renameTo(file);
			} else {
				File f = new File(Global.savePath, "rename.bat");
				boolean isExist = f.exists();
				System.out.println(f.getAbsolutePath() + "是否存在? " + f.exists());
				FileWriter fw;
				fw = new FileWriter(f, true);
				if (!isExist) {
					// .bat切为UTF-8编码, 防止中文乱码
					fw.write("@echo off\r\nchcp 65001\r\n");
				}
				String cmd = String.format("rename \"%s\" \"%s%s\"\r\n", fName, formattedTitle, tail);
				fw.write(cmd);
				fw.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * 获取文件
	 * 
	 * @param avid_q
	 * @param page
	 * @return
	 */
	public static File getFileByAvQnP(String avid_q, int page) {
		String name = avid_q + "-p" + page;
		Logger.println(name);
		File fMp4 = new File(Global.savePath, name + ".mp4");
		if (fMp4.exists()) {
			return fMp4;
		}
		File fFlv = new File(Global.savePath, name + ".flv");
		if (fFlv.exists()) {
			return fFlv;
		}
		return null;
	}

	// ## avId - av号 e.g. av1234567
	// ## pAv - av 的第几个视频 e.g. p1/p2
	// ## pDisplay - 合集的第几个视频 e.g. pn1/pn2
	// ## qn - 清晰度值 e.g. 32/64/80
	// ## avTitle - av标题
	// ## clipTitle - 视频小标题
	//
	// 以下可能不存在
	// 用法举例 (:listName 我在前面-listName-我在后面)   ===>  我在前面-某收藏夹的名称-我在后面
	// ### listName - 集合名称  e.g. 某收藏夹的名称
	// ### listOwnerName - 集合的拥有者 e.g. 某某某 （假设搜索的是某人的收藏夹）
	// public static String formatStr = "avTitle-pDisplay-clipTitle-qn";
	static Pattern splitUnit = Pattern.compile("avId|pAv|pDisplay|qn|avTitle|clipTitle|listName|listOwnerName|\\(\\:([^ ]+) ([^\\)]*)\\)");

	public static String genFormatedName(String avId, String pAv, String pDisplay, int qn, String avTitle,
			String clipTitle, String listName, String listOwnerName) {
		// 生成KV表
		HashMap<String, String> paramMap = new HashMap<String, String>();
		paramMap.put("avId", avId);
		paramMap.put("pAv", pAv);
		paramMap.put("pDisplay", pDisplay);
		paramMap.put("qn", "" + qn);
		paramMap.put("avTitle", avTitle);
		paramMap.put("clipTitle", clipTitle);
		paramMap.put("listName", listName);
		paramMap.put("listOwnerName", listOwnerName);
		//paramMap.put("clipTitle", clipTitle);

		// 匹配格式字符串
		// avTitle-pDisplay-clipTitle-qn
		return genFormatedName(paramMap, Global.formatStr);
	}

	/**
	 * @param paramMap
	 * @param matcher
	 * @return
	 */
	private static String genFormatedName(HashMap<String, String> paramMap, String formatStr) {
		StringBuilder sb = new StringBuilder();
		Matcher matcher = splitUnit.matcher(formatStr);
		int pointer = 0;
		while (matcher.find()) {
			// 加入匹配单位前的字符串
			sb.append(formatStr.substring(pointer, matcher.start()));
			String ifStr = matcher.group(1);//条件语句
			if(ifStr != null) {
				if(paramMap.get(ifStr)!= null) {
					sb.append(genFormatedName(paramMap, matcher.group(2)));
				}
//				Logger.println();
			}else {
				// 加入匹配单位对应的值
				sb.append(paramMap.get(matcher.group()));
			}
			// 改变指针位置
			pointer = matcher.end();
		}
		// 加入最后不匹配单位的部分
		sb.append(formatStr.substring(pointer));
		// 去掉文件名称的非法字符
		return sb.toString().replaceAll("[\\\\|\\/|:\\*\\?|<|>|\\||\\\"$]", ".");
	}
}
