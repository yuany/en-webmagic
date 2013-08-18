package us.codecraft.webmagic.selector;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

/**
 * css风格的选择器。包装了Jsoup。<br>
 * 
 * @author code4crafter@gmail.com <br>
 *         Date: 13-4-21 Time: 上午9:39
 */
public class CssSelector extends AbstractedSelector {

	private String selectorText;

	private boolean isOuterHtml;

	private String attrName;

	public CssSelector(String selectorText) {
		super();
		this.selectorText = selectorText;
		this.isOuterHtml = true;
		this.attrName = "";
	}

	public CssSelector(String selectorText, boolean isOuterHtml) {
		super();
		this.selectorText = selectorText;
		this.isOuterHtml = isOuterHtml;
		this.attrName = "";
	}

	public CssSelector(String selectorText, boolean isOuterHtml, String attrName) {
		super();
		this.selectorText = selectorText;
		this.isOuterHtml = isOuterHtml;
		this.attrName = attrName;
	}

	public CssSelector(String selectorText, boolean isOuterHtml, String attrName, AbstractedSelector.Temp tmpObj) {
		super(tmpObj);
		this.selectorText = selectorText;
		this.isOuterHtml = isOuterHtml;
		this.attrName = attrName;
	}

	@Override
	public String select(String text) {
		Document doc = Jsoup.parse(text);
		Elements elements = doc.select(selectorText);
		if (CollectionUtils.isEmpty(elements)) {
			return handleNullVal();
		}
		Element ele = elements.get(0);
		return getValue(ele);
	}

	@Override
	public List<String> selectList(String text) {
		List<String> strings = new ArrayList<String>();
		Document doc = Jsoup.parse(text);
		Elements elements = doc.select(selectorText);
		if (CollectionUtils.isNotEmpty(elements)) {
			for (Element element : elements) {
				String value = getValue(element);
				if (!StringUtils.isEmpty(value)) {
					strings.add(value);
				}
			}
		}
		return strings;
	}

	private String getValue(Element ele) {
		if (ele == null)
			return handleNullVal();
		String val = StringUtils.isEmpty(this.attrName) ? (isOuterHtml ? ele.outerHtml() : ele.text()) : ele
				.attr(this.attrName);
		val = handleVal(val);
		return val;
	}
}
