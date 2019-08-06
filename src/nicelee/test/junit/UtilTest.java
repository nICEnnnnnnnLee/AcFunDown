package nicelee.test.junit;


import static org.junit.Assert.assertEquals;

import java.awt.Desktop;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URI;
import java.util.HashSet;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import nicelee.acfun.util.CmdUtil;
import nicelee.ui.Global;

public class UtilTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	/**
	 * 测试 删除已经生效过的临时cmd 命令文件
	 */
	//@Test
	public void testDeleteAllInactiveCmdTemp() {
		CmdUtil.deleteAllInactiveCmdTemp();
	}
	
	/**
	 * 测试 删除repo重复记录
	 */
	//@Test
	public void testDeleteRecords() {
		try {
			BufferedReader bReader = new BufferedReader(new FileReader("D:\\Workspace\\javaweb-springboot\\BilibiliDown\\release\\config\\repo.config"));
			String line = null;
			HashSet<String> set = new HashSet<>();
			while((line = bReader.readLine())!= null) {
				set.add(line);
			}
			bReader.close();
			
			FileWriter fW = new FileWriter("D:\\Workspace\\javaweb-springboot\\BilibiliDown\\release\\config\\repo2.config");
			for(Object str: set.toArray()) {
				fW.write(str.toString());
				fW.write("\r\n");
			}
			fW.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 测试 打开文件夹并选择文件
	 */
	//@Test
	public void testOpenFolder() {
		// 打开并选中
		try {
			String cmd[] = { "explorer", "/e,/select,", "D:\\Workspace\\javaweb-springboot\\BilibiliDown\\test   test1 test.txt" };
			//String cmd = "explorer /e,/select," +"D:\\Workspace\\javaweb-springboot\\BilibiliDown\\test   test1 test.txt";
			Runtime.getRuntime().exec(cmd);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/**
	 * 测试 打开bat
	 */
	//@Test
	public void testOpenBat() {
		// 打开并选中
		try {
			Desktop desktop = Desktop.getDesktop();
			desktop.open(new File("run.bat"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	/**
	 * 测试 打开URI
	 */
	//@Test
	public void testOpenURI() {
		// 打开并选中
		try {
			Desktop desktop = Desktop.getDesktop();
			desktop.browse(new URI("https://www.baidu.com"));
			//desktop.open(new File("D:\\Workspace\\javaweb-springboot\\BilibiliDown\\test   test1 test.txt"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 测试 根据格式生成文件名
	 */
	//@Test
	public void testTitleUnderCondition() {
		Global.formatStr = "avTitle-pDisplay-clipTitle-qn-(:listName 我在前面-listName-我在后面-)ddd";
		String formatedName = CmdUtil.genFormatedName("av12345", "p1", "pn2", 80, "av的标题", "片段的标题",null,null);
		System.out.println(formatedName);
		assertEquals("av的标题-pn2-片段的标题-80-ddd", formatedName);
		formatedName = CmdUtil.genFormatedName("av12345", "p1", "pn2", 80, "av的标题", "片段的标题","哈哈哈",null);
		System.out.println(formatedName);
		assertEquals("av的标题-pn2-片段的标题-80-我在前面-哈哈哈-我在后面-ddd", formatedName);
	}
	/**
	 * 测试 根据格式生成文件名
	 */
	//@Test
	public void testTitle() {
		Global.formatStr = "avTitle-pDisplay-clipTitle-qn";
		String formatedName = CmdUtil.genFormatedName("av12345", "p1", "pn2", 80, "av的标题", "片段的标题",null,null);
		System.out.println(formatedName);
		assertEquals("av的标题-pn2-片段的标题-80", formatedName);
		
		Global.formatStr = "开头avTitle-pDisplay-clipTitle-qn-pAvkkk666";
		formatedName = CmdUtil.genFormatedName("av12345", "p1", "pn2", 80, "av的标题", "片段的标题",null,null);
		System.out.println(formatedName);
		assertEquals("开头av的标题-pn2-片段的标题-80-p1kkk666", formatedName);
	}
	
	/**
	 * 测试 根据文件名
	 */
	//@Test
	public void testRename() {
		String str = "test$]WHAT ";
		str = str.replaceAll("[\\\\|\\/|:\\*\\?|<|>|\\||\\\"$]", ".");
		System.out.println(str);
		str = "av21449435-16-p1.flv".replaceFirst("av[0-9]+-[0-9]+-p[0-9]+", str);
	}
	
	/**
	 * 测试 ffmpeg环境是否可用
	 */
	@Test
	public void testFFmpeg() {
		String[] cmd = {"ffmpeg", "-version"};
		CmdUtil.run(cmd);
	}
	
}
