package us.codecraft.webmagic.selector;

import org.apache.commons.lang3.StringUtils;
import org.htmlcleaner.*;

import java.util.ArrayList;
import java.util.List;

/**
 * xpath的选择器。包装了HtmlCleaner。<br>
 * 
 * @author code4crafter@gmail.com <br>
 *         Date: 13-4-21 Time: 上午9:39
 */
public class XpathSelector extends AbstractedSelector {

	private String xpathStr;

	public XpathSelector(String xpathStr) {
		super();
		this.xpathStr = xpathStr;
	}

	public XpathSelector(String xpathStr, String defaultValue) {
		super(defaultValue);
		this.xpathStr = xpathStr;
	}

	@Override
	public String select(String text) {
		boolean hasDefaultValue = hasDefaultValue();
		HtmlCleaner htmlCleaner = new HtmlCleaner();
		TagNode tagNode = htmlCleaner.clean(text);
		if (tagNode == null) {
			return hasDefaultValue ? this.defaultValue : null;
		}
		try {
			Object[] objects = tagNode.evaluateXPath(xpathStr);
			if (objects != null && objects.length >= 1) {
				if (objects[0] instanceof TagNode) {
					TagNode tagNode1 = (TagNode) objects[0];
					String val = htmlCleaner.getInnerHtml(tagNode1);
					return StringUtils.isEmpty(val) && hasDefaultValue ? this.defaultValue : val;
				} else {
					String val = objects[0].toString();
					return StringUtils.isEmpty(val) && hasDefaultValue ? this.defaultValue : val;
				}
			}
		} catch (XPatherException e) {
			e.printStackTrace();
		}
		return hasDefaultValue ? this.defaultValue : null;
	}

	@Override
	public List<String> selectList(String text) {
		boolean hasDefaultValue = hasDefaultValue();
		HtmlCleaner htmlCleaner = new HtmlCleaner();
		TagNode tagNode = htmlCleaner.clean(text);
		List<String> results = null;
		if (tagNode == null) {
			if (hasDefaultValue) {
				results = new ArrayList<String>();
				results.add(this.defaultValue);
			}
			return results;
		}
		results = new ArrayList<String>();
		try {
			Object[] objects = tagNode.evaluateXPath(xpathStr);
			if (objects != null && objects.length >= 1) {
				for (Object object : objects) {
					if (object instanceof TagNode) {
						TagNode tagNode1 = (TagNode) object;
						String val = htmlCleaner.getInnerHtml(tagNode1);
						val = StringUtils.isEmpty(val) && hasDefaultValue ? this.defaultValue : val;
						results.add(val);
					} else {
						String val = object.toString();
						val = StringUtils.isEmpty(val) && hasDefaultValue ? this.defaultValue : val;
						results.add(val);
					}
				}
			}
		} catch (XPatherException e) {
			e.printStackTrace();
		}
		return results;
	}
	
	private boolean hasDefaultValue() {
    	return StringUtils.isNotEmpty(this.defaultValue);
    }
}
