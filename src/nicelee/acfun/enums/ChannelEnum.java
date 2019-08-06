package nicelee.acfun.enums;

public enum ChannelEnum {
	默认("默认", 0),
	AC正义("AC正义", 177),
	番剧("番剧", 155),
	动画("动画", 1),
	音乐("音乐", 58),
	舞蹈偶像("舞蹈·偶像", 123),
	游戏("游戏", 59),
	娱乐("娱乐", 60),
	科技("科技", 70),
	影视("影视", 68),
	体育("体育", 69),
	鱼塘("鱼塘", 125);
	
	private String description;
	private int id;
	
	ChannelEnum(String description, int id){
		this.description = description;
		this.id = id;
	}
	
	public static int getChannelId(String description) {
		ChannelEnum[] enums = ChannelEnum.values();
		for(ChannelEnum item : enums) {
			if(item.getDescription().equals(description)) {
				return item.getId();
			}
		}
		return 0;
	}
	
	public static String getDescription(int id) {
		ChannelEnum[] enums = ChannelEnum.values();
		for(ChannelEnum item : enums) {
			if(id == item.getId()) {
				return item.getDescription();
			}
		}
		return "";
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}
	
}
