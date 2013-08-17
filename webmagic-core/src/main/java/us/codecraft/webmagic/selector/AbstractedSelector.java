package us.codecraft.webmagic.selector;

public abstract class AbstractedSelector implements Selector {

	protected String defaultValue;

	public AbstractedSelector(String defaultValue) {
		super();
		this.defaultValue = defaultValue;
	}

	public AbstractedSelector() {
		super();
		this.defaultValue = null;
	}

}
