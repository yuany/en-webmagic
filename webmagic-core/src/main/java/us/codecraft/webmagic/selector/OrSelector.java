package us.codecraft.webmagic.selector;

import java.util.ArrayList;
import java.util.List;

/**
 * @author code4crafter@gmail.com <br>
 *         Date: 13-8-3 <br>
 *         Time: 下午5:29 <br>
 */
public class OrSelector implements Selector {

	private List<Selector> selectors = new ArrayList<Selector>();

	public OrSelector(Selector... selectors) {
		for (Selector selector : selectors) {
			this.selectors.add(selector);
		}
	}

	@Override
	public String select(String text) {
		for (Selector selector : selectors) {
			String _text = selector.select(text);
			if (_text != null && !"".equals(_text.trim()) && !"null".equalsIgnoreCase(_text.trim())) {
				return _text;
			}
		}
		return null;
	}

	@Override
	public List<String> selectList(String text) {
		List<String> results = new ArrayList<String>();
		for (Selector selector : selectors) {
			List<String> strings = selector.selectList(text);
			results.addAll(strings);
		}
		return results;
	}
}
