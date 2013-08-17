package us.codecraft.webmagic.model;

import java.lang.reflect.Field;

import us.codecraft.webmagic.selector.Selector;

public class LogisticFieldExtractor extends FieldExtractor {
	
	private String op;
	

	public LogisticFieldExtractor(Field field, Selector selector, Source source, boolean notNull, boolean multi) {
		super(field, selector, source, notNull, multi);
	}

}
