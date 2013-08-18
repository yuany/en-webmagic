package us.codecraft.webmagic.selector;

import java.util.ArrayList;
import java.util.List;

import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.TagNode;
import org.htmlcleaner.XPatherException;

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

	public XpathSelector(String xpathStr, AbstractedSelector.Temp tmpObj) {
		super(tmpObj);
		this.xpathStr = xpathStr;
	}

	@Override
	public String select(String text) {
		HtmlCleaner htmlCleaner = new HtmlCleaner();
		TagNode tagNode = htmlCleaner.clean(text);
		if (tagNode == null) {
			return handleNullVal();
		}
		try {
			Object[] objects = tagNode.evaluateXPath(xpathStr);
			if (objects != null && objects.length >= 1) {
				if (objects[0] instanceof TagNode) {
					TagNode tagNode1 = (TagNode) objects[0];
					String val = htmlCleaner.getInnerHtml(tagNode1);
					val = handleVal(val);
					return val;
				} else {
					String val = objects[0].toString();
					val = handleVal(val);
					return val;
				}
			}
		} catch (XPatherException e) {
			e.printStackTrace();
		}
		return handleNullVal();
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
				results.add(this.getDefaultValue());
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
						val = handleVal(val);
						results.add(val);
					} else {
						String val = object.toString();
						val = handleVal(val);
						results.add(val);
					}
				}
			}
		} catch (XPatherException e) {
			e.printStackTrace();
		}
		return results;
	}
}
