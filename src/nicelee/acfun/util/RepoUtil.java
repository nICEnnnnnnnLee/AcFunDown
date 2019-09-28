package nicelee.acfun.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import nicelee.ui.Global;

public class RepoUtil {
	// avinfo 必须符合avId-qn-p的形式
	// av1234-60-p2
	static Pattern standardAvPattern; 
	static Pattern standardBangumiPattern; 
	// 存在某一清晰度后, 在下另一种清晰度时是否判断已完成
	// true : 同一视频两种清晰度算不同文件
	// false : 同一视频两种清晰度算相同文件
	static boolean definitionStrictMode;
	
	static File fRepo; // 持久化文件，存放于config/repo.config
	static Set<String> downRepo; // 已下载完成的av集合

	
	public static void init(boolean refresh) {
		definitionStrictMode = Global.repoInDefinitionStrictMode;
		if(fRepo == null || refresh) {
			fRepo = new File("config/repo.config");
			standardAvPattern = Pattern.compile("^(a[vabc][0-9_]+)-([0-9]+)(-p[0-9]+)$");
			standardBangumiPattern = Pattern.compile("^a[vabc]([0-9]+)_([0-9]+)_([0-9]+)-([0-9]+)(-p[0-9]+)$");
			downRepo = new CopyOnWriteArraySet<String>();
			if(!fRepo.exists()) {
				try {
					fRepo.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		// 先初始化downRepo
		BufferedReader buReader = null;
		try {
			buReader = new BufferedReader(new FileReader(fRepo));
			String avRecord;
			while ((avRecord = buReader.readLine()) != null) {
				// 处理历史遗留事项，保持版本兼容（aa5024886_36188_330164 所有番的groupID都是36188已经不作数了）
				// 如果是番剧
				Matcher matcherBangumi = standardBangumiPattern.matcher(avRecord);
				if(matcherBangumi.find()) {
					//Pattern.compile("^a[vabc]([0-9]+)_([0-9]+)(_[0-9]+)(-[0-9]+)(-p[0-9]+)$");
					String aaid = "aa" + matcherBangumi.group(1);
					String idWithUnderline = matcherBangumi.group(3);
					String qnWithMinus = matcherBangumi.group(4);
					if(definitionStrictMode) {
						downRepo.add(aaid + idWithUnderline + qnWithMinus);
					}else {
						downRepo.add(aaid + idWithUnderline);
					}
				}else {
					Matcher matcher = standardAvPattern.matcher(avRecord);
					if (matcher.find()) {
						if(definitionStrictMode) {
							downRepo.add(avRecord);
						}else {
							downRepo.add(matcher.group(1) + matcher.group(3));
						}
					}
				}
			}
			buReader.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 仓库里是否存在该条记录
	 * @param avRecord
	 * @return
	 */
	public static boolean isInRepo(String avRecord) {
		System.out.println("查询记录" + avRecord);
		// 对于番剧特殊处理
		Matcher matcherBangumi = standardBangumiPattern.matcher(avRecord);
		if(matcherBangumi.find()) {
			//Pattern.compile("^a[vabc]([0-9]+)_([0-9]+)(_[0-9]+)(-[0-9]+)(-p[0-9]+)$");
			String aaid = "aa" + matcherBangumi.group(1);
			String idWithUnderline = matcherBangumi.group(3);
			String qnWithMinus = matcherBangumi.group(4);
			if(definitionStrictMode) {
				return downRepo.contains(aaid + idWithUnderline + qnWithMinus);
			}else {
				return downRepo.contains(aaid + idWithUnderline);
			}
		}
		
		if(definitionStrictMode) {
			return downRepo.contains(avRecord);
		}else {
			Matcher matcher = standardAvPattern.matcher(avRecord);
			if (matcher.find()) {
				return downRepo.contains(matcher.group(1) + matcher.group(3));
			}
		}
		return false;
	}

	/**
	 * 加入并持久化保存到记录仓库
	 * <p> avRecord 必须符合avId-p-qn的形式 </p> 
	 * @param avinfo
	 */
	public static void appendAndSave(String avRecord) {
		System.out.println("已完成下载： " + avRecord);
		if(!isInRepo(avRecord)) {
			Logger.println("不在记录中");
			Matcher matcher = standardAvPattern.matcher(avRecord);
			if (matcher.find()) {
				if(definitionStrictMode) {
					downRepo.add(avRecord);
				}else {
					downRepo.add(matcher.group(1) + matcher.group(3));
				}
				appendRecordToFile(avRecord);
			}else {
				Logger.println("不匹配standardAvPattern");
			}
		}
	}
	
	
	
	synchronized static void appendRecordToFile(String line) {
		//System.out.println(Thread.currentThread().getName() + "开始记录");
		try {
			FileWriter fw = new FileWriter(fRepo, true);
			fw.write(line);
			fw.write("\n");
			fw.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		//System.out.println(Thread.currentThread().getName() + "记录完成");
	}
		
}
