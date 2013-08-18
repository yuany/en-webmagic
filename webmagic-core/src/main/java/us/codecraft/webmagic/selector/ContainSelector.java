package us.codecraft.webmagic.selector;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class ContainSelector extends AbstractedSelector {

	private String text;

	public ContainSelector(String text) {
		super();
		this.text = text.toLowerCase();
	}

	public ContainSelector(String text, AbstractedSelector.Temp tmpObj) {
		super(tmpObj);
		this.text = text;
	}

	@Override
	public String select(String text) {
		String _text = text.toLowerCase();
		if (_text.contains(this.text)) {
			return this.text;
		}
		return handleNullVal();
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
