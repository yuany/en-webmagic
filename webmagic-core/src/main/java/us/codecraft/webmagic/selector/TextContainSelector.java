package us.codecraft.webmagic.selector;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class TextContainSelector extends AbstractedSelector {

	private String text;

	public TextContainSelector(String text) {
		super();
		this.text = text.toLowerCase();
	}

	public TextContainSelector(String text, String defaultValue) {
		super(defaultValue);
		this.text = text;
	}

	@Override
	public String select(String text) {
		String _text = text.toLowerCase();
		if (_text.contains(this.text)) {
			return this.text;
		}
		return StringUtils.isNotEmpty(this.defaultValue) ? this.defaultValue : null;
	}

	@Override
	public List<String> selectList(String text) {
		List<String> list = new ArrayList<String>(1);
		String value = select(text);
		if (StringUtils.isNotEmpty(value))
			list.add(value);
		return list;
	}

}
