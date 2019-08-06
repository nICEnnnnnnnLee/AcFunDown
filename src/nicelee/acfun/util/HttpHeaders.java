package nicelee.acfun.util;

import java.util.HashMap;

public class HttpHeaders {
	HashMap<String, String> headerMap = new HashMap<String, String>();
	private static HashMap<String, String> userInfoHeaderMap = null;
	private static HashMap<String, String> loginAuthHeaderMap = null;
	private static HashMap<String, String> loginAuthVaHeaderMap = null;

	public void setHeader(String key, String value) {
		headerMap.put(key, value);
	}

	public String getHeader(String key) {
		return headerMap.get(key);
	}

	public HashMap<String, String> getHeaders() {
		return headerMap;
	}

	/**
	 * 该Header配置用于登录AuthKey验证
	 */
	public HashMap<String, String> getAcFunLoginAuthVaHeaders() {
		if (loginAuthVaHeaderMap == null) {
			loginAuthVaHeaderMap = new HashMap<String, String>();
			loginAuthVaHeaderMap.put("Accept", "*/*");
			loginAuthVaHeaderMap.put("Accept-Encoding", "gzip, deflate, br");
			loginAuthVaHeaderMap.put("Accept-Language", "zh-CN,zh;q=0.8");
			loginAuthVaHeaderMap.put("Connection", "keep-alive");
			loginAuthVaHeaderMap.put("Host", "scan.acfun.cn");
			loginAuthVaHeaderMap.put("Origin", "https://www.acfun.cn");
			loginAuthVaHeaderMap.put("Referer", "https://www.acfun.cn/");
			loginAuthVaHeaderMap.put("User-Agent",
					"Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:68.0) Gecko/20100101 Firefox/68.0");
		}
		return loginAuthVaHeaderMap;
	}

	/**
	 * 该Header配置用于FLV视频下载
	 */
	public HashMap<String, String> getBiliWwwFLVHeaders(String avId) {
		headerMap.put("X-Requested-With", "ShockwaveFlash/28.0.0.137");
		//headerMap.put("Referer", "https://www.bilibili.com/video/" + avId);// need addavId
		headerMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
		return headerMap;
	}
	
	/**
	 * 该Header配置用于M4s视频下载
	 */
	public HashMap<String, String> getBiliWwwM4sHeaders(String avId) {
		headerMap.remove("X-Requested-With");
		//headerMap.put("Referer", "https://www.bilibili.com/video/" + avId);// need addavId
		headerMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
		return headerMap;
	}
	
	/**
	 * 该Header配置用于通用PC端页面访问
	 */
	public HashMap<String, String> getAcFunM3u8() {
		headerMap = new HashMap<String, String>();
		headerMap.put("Accept", "text/html,application/xhtml+xml;q=0.9,image/webp,*/*;q=0.8");
		headerMap.put("Accept-Encoding", "gzip, deflate, sdch, br");
		headerMap.put("Accept-Language", "zh-CN,zh;q=0.8");
		headerMap.put("Connection", "keep-alive");
		headerMap.put("Host", "video.acfun.cn");
		headerMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
		return headerMap;
	}
	
	/**
	 * 该Header配置用于通用PC端页面访问
	 */
	public HashMap<String, String> getAcFunTsDownload() {
		headerMap = new HashMap<String, String>();
		headerMap.put("Accept", "text/html,application/xhtml+xml;q=0.9,image/webp,*/*;q=0.8");
		headerMap.put("Accept-Encoding", "gzip, deflate, sdch, br");
		headerMap.put("Accept-Language", "zh-CN,zh;q=0.8");
		headerMap.put("Connection", "keep-alive");
		headerMap.put("Host", "video.acfun.cn");
		headerMap.put("Origin", "https://www.acfun.cn");
		headerMap.put("Referer", "https://www.acfun.cn/");
		headerMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/58.0.3029.110 Safari/537.36 SE 2.X MetaSr 1.0");
		return headerMap;
	}

	/**
	 * 该Header配置用于通用PC端页面访问
	 */
	public HashMap<String, String> getCommonHeaders(String host) {
		headerMap = new HashMap<String, String>();
		headerMap.put("Accept", "text/html,application/xhtml+xm…ml;q=0.9,image/webp,*/*;q=0.8");
		headerMap.put("Accept-Encoding", "gzip, deflate, sdch, br");
		headerMap.put("Accept-Language", "zh-CN,zh;q=0.8");
		headerMap.put("Cache-Control", "max-age=0");
		headerMap.put("Connection", "keep-alive");
		headerMap.put("Host", host);
		headerMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:65.0) Gecko/20100101 Firefox/65.0");
		return headerMap;
	}
	
	/**
	 * 该Header配置用于通用PC端页面访问
	 */
	public HashMap<String, String> getCommonHeaders() {
		headerMap = new HashMap<String, String>();
		headerMap.put("Accept", "text/html,application/xhtml+xm…ml;q=0.9,image/webp,*/*;q=0.8");
		headerMap.put("Accept-Encoding", "gzip, deflate, sdch, br");
		headerMap.put("Accept-Language", "zh-CN,zh;q=0.8");
		headerMap.put("Cache-Control", "max-age=0");
		headerMap.put("Connection", "keep-alive");
		headerMap.put("User-Agent",
				"Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:68.0) Gecko/20100101 Firefox/68.0");
		return headerMap;
	}
	
	/**
	 * 空Header配置
	 */
	public HashMap<String, String> getEmptyHeaders() {
		headerMap = new HashMap<String, String>();
		return headerMap;
	}

}
