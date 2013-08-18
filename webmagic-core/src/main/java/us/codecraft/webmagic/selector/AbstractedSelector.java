package us.codecraft.webmagic.selector;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;


public abstract class AbstractedSelector implements Selector {

	protected Temp tmpObj;

	public AbstractedSelector(Temp tmpObj) {
		super();
		this.tmpObj = tmpObj;
	}

	public AbstractedSelector() {
		super();
		tmpObj = new Temp();
	}

	protected String handleNullVal() {
		boolean isTrim = tmpObj.isTrim();
		String defaultValue = tmpObj.getDefaultValue();
		return hasDefaultValue() ? (isTrim ? defaultValue.trim() : defaultValue) : null;
	}

	protected String handleVal(String val) {
		boolean isTrim = tmpObj.isTrim();
		boolean isRemoveTag = tmpObj.isRemoveTag();
		String defaultValue = tmpObj.getDefaultValue();
		val = StringUtils.isEmpty(val) && hasDefaultValue() ? defaultValue : val;
		val = isTrim ? val.trim() : val;
		val = isRemoveTag ? Jsoup.parse(val).text() : val;
		return val;
	}

	protected boolean hasDefaultValue() {
		return StringUtils.isNotEmpty(this.tmpObj.getDefaultValue());
	}

	public static class Temp {

		private String defaultValue;

		private boolean isTrim;

		private boolean isRemoveTag;
		

		public Temp() {
			super();
			this.defaultValue = null;
			this.isTrim = false;
			this.isRemoveTag = false;
		}

		public String getDefaultValue() {
			return defaultValue;
		}

		public void setDefaultValue(String defaultValue) {
			this.defaultValue = defaultValue;
		}

		public boolean isTrim() {
			return isTrim;
		}

		public void setTrim(boolean isTrim) {
			this.isTrim = isTrim;
		}

		public boolean isRemoveTag() {
			return isRemoveTag;
		}

		public void setRemoveTag(boolean isRemoveTag) {
			this.isRemoveTag = isRemoveTag;
		}

	}
	
	protected String getDefaultValue() {
		return this.tmpObj.getDefaultValue();
	}
}
