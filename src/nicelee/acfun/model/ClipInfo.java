package nicelee.acfun.model;

import java.util.HashMap;
import java.util.Map.Entry;

public class ClipInfo {
	String avTitle;
	long cId;
	String avId;
	int page;
	String title;
	String picPreview;
	String listName; // 收藏夹名称 或其它集合名称（不一定存在）
	String listOwnerName; // 收藏夹主人 或其它集合的拥有者（不一定存在）
	HashMap<Integer, String> links;
	
	int remark = -1; // 用于ss番剧查询时显示顺序

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("------------------------------\r\n");
		sb.append("--avTitle为 :").append(avTitle).append(" \r\n");
		sb.append("--page为 :").append(page).append(" \r\n");
		sb.append("--cId为 :").append(cId).append(" \r\n");
		sb.append("--title为 :").append(title).append(" \r\n");
		if (avId != null)
			sb.append("--avId为 :").append(avId).append(" \r\n");
		
		if (links != null) {
			for (Entry<Integer, String> entry: links.entrySet()) {
				sb.append("----清晰度 :").append(entry.getKey()).append(" ");
				sb.append("----下载链接 :").append(entry.getValue()).append(" \r\n");
			}
		}

		return sb.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this != null && obj != null) {
			if (obj instanceof ClipInfo) {
				ClipInfo clip = (ClipInfo) obj;
				return (this.cId == clip.cId);
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return (int) cId;
	}

	public long getcId() {
		return cId;
	}

	public void setcId(long cId) {
		this.cId = cId;
	}

	public String getAvId() {
		return avId;
	}

	public void setAvId(String avId) {
		this.avId = avId;
	}

	public int getPage() {
		return page;
	}

	public void setPage(int page) {
		this.page = page;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public HashMap<Integer, String> getLinks() {
		return links;
	}

	public void setLinks(HashMap<Integer, String> links) {
		this.links = links;
	}

	public int getRemark() {
		int p = remark == -1 ? page : remark;
		return p;
	}

	public void setRemark(int remark) {
		this.remark = remark;
	}

	public String getAvTitle() {
		return avTitle;
	}

	public void setAvTitle(String avTitle) {
		this.avTitle = avTitle;
	}

	public String getPicPreview() {
		return picPreview;
	}

	public void setPicPreview(String picPreview) {
		this.picPreview = picPreview;
	}

	public String getListName() {
		return listName;
	}

	public void setListName(String listName) {
		this.listName = listName;
	}

	public String getListOwnerName() {
		return listOwnerName;
	}

	public void setListOwnerName(String listOwnerName) {
		this.listOwnerName = listOwnerName;
	}

}
