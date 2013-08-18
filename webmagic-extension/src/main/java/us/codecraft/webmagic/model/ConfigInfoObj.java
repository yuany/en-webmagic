package us.codecraft.webmagic.model;

import us.codecraft.webmagic.model.annotation.ConfigInfo;

@ConfigInfo
public final class ConfigInfoObj {

	private int groupNo;

	private boolean outerHtml;

	private String attrName;

	private String defaultValue;

	private boolean trim;

	private boolean removeTag;

	public ConfigInfoObj() {
		super();
		ConfigInfo configInfo = (ConfigInfo) this.getClass().getAnnotation(ConfigInfo.class);
		this.groupNo = configInfo.groupNo();
		this.outerHtml = configInfo.isOuterHtml();
		this.attrName = configInfo.attrName();
		this.defaultValue = configInfo.defaultValue();
		this.trim = configInfo.isTrim();
		this.removeTag = configInfo.isRemoveTag();
	}

	public int getGroupNo() {
		return groupNo;
	}

	public void setGroupNo(int groupNo) {
		this.groupNo = groupNo;
	}

	public boolean isOuterHtml() {
		return outerHtml;
	}

	public void setOuterHtml(boolean outerHtml) {
		this.outerHtml = outerHtml;
	}

	public String getAttrName() {
		return attrName;
	}

	public void setAttrName(String attrName) {
		this.attrName = attrName;
	}

	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}

	public boolean isTrim() {
		return trim;
	}

	public void setTrim(boolean trim) {
		this.trim = trim;
	}

	public boolean isRemoveTag() {
		return removeTag;
	}

	public void setRemoveTag(boolean removeTag) {
		this.removeTag = removeTag;
	}

	@Override
	public String toString() {
		return "ConfigInfoObj [groupNo=" + groupNo + ", outerHtml=" + outerHtml + ", attrName=" + attrName
				+ ", defaultValue=" + defaultValue + ", trim=" + trim + ", removeTag=" + removeTag + "]";
	}

	public static void main(String[] args) {
		System.out.println(new ConfigInfoObj());
	}

}
