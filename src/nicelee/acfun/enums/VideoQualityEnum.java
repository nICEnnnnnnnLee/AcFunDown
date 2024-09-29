package nicelee.acfun.enums;

import nicelee.acfun.util.Logger;

public enum VideoQualityEnum {
	Q50("2160P", 50, "2160P"), // ac30217383
	Q40("1080P+", 40, "1080P+"), // ac30217383
	Q31("1080P60", 31, "1080P60"), // ac46303302
	Q30("1080P", 30, "1080P"), // ac30217383
	Q21("720P60", 21, "720P60"), // ac46303302
	Q20("720P", 20, "720P"), // ac30217383
	Q10("540P", 10, "540P"), // ac30217383
	Q00("360P", 0, "360P"); // ac30217383
//	Q1080P60("1080P60", 116, "高清1080P60"),
//	Q1080PPlus("1080P+", 112, "高清1080P+"),
//	Q1080P("1080P", 80, "高清1080P"),
//	Q720P60("720P60", 74, "高清720P60"),
//	Q720P("720P", 64, "高清720P"),
//	Q480P("480P", 32, "清晰480P"),
//	Q320P("320P", 16, "流畅320P");

	private String quality;
	private int qn;
	private String description;

	private static final int[] qns;

	static {
		VideoQualityEnum[] enums = VideoQualityEnum.values();
		qns = new int[enums.length];
		for (int i = 0; i < qns.length; i++)
			qns[i] = enums[i].qn;
	}

	VideoQualityEnum(String quality, int qn, String description) {
		this.quality = quality;
		this.qn = qn;
		this.description = description;
	}

	public static int[] availableQNs() {
		return qns;
	}

	public static String getQualityDescript(int qn) {
		VideoQualityEnum[] enums = VideoQualityEnum.values();
		for (VideoQualityEnum item : enums) {
			if (item.getQn() == qn) {
				return item.getDescription();
			}
		}
		return null;
	}

	public static int getQN(String quality) {
		VideoQualityEnum[] enums = VideoQualityEnum.values();
		for (VideoQualityEnum item : enums) {
			if (item.getQuality().equals(quality)) {
				return item.getQn();
			}
		}
		Logger.println("未找到相应选项：" + quality);
		return 0;
	}

	public static boolean contains(int quality) {
		VideoQualityEnum[] enums = VideoQualityEnum.values();
		for (VideoQualityEnum item : enums) {
			if (item.getQn() == quality) {
				return true;
			}
		}
		return false;
	}

	public String getQuality() {
		return quality;
	}

	public int getQn() {
		return qn;
	}

	String getDescription() {
		return description;
	}
}
