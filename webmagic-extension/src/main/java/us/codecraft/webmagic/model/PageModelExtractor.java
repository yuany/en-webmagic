package us.codecraft.webmagic.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import us.codecraft.webmagic.Page;
import us.codecraft.webmagic.model.annotation.ConfigInfo;
import us.codecraft.webmagic.model.annotation.ExprType;
import us.codecraft.webmagic.model.annotation.ComboExtract;
import us.codecraft.webmagic.model.annotation.ExtractBy;
import us.codecraft.webmagic.model.annotation.ExtractBy2;
import us.codecraft.webmagic.model.annotation.ExtractBy3;
import us.codecraft.webmagic.model.annotation.ExtractByRaw;
import us.codecraft.webmagic.model.annotation.ExtractByUrl;
import us.codecraft.webmagic.model.annotation.HelpUrl;
import us.codecraft.webmagic.model.annotation.TargetUrl;
import us.codecraft.webmagic.selector.AndSelector;
import us.codecraft.webmagic.selector.CssSelector;
import us.codecraft.webmagic.selector.TextContainSelector;
import us.codecraft.webmagic.selector.OrSelector;
import us.codecraft.webmagic.selector.RegexSelector;
import us.codecraft.webmagic.selector.Selector;
import us.codecraft.webmagic.selector.XpathSelector;

/**
 * Model主要逻辑类。将一个带注解的POJO转换为一个PageModelExtractor。<br>
 * 
 * @author code4crafter@gmail.com <br>
 *         Date: 13-8-1 <br>
 *         Time: 下午9:33 <br>
 */
class PageModelExtractor {

	private List<Pattern> targetUrlPatterns = new ArrayList<Pattern>();

	private Selector targetUrlRegionSelector;

	private List<Pattern> helpUrlPatterns = new ArrayList<Pattern>();

	private Selector helpUrlRegionSelector;

	private Class<?> clazz;

	private List<FieldExtractor> fieldExtractors;

	private Extractor extractor;

	public static PageModelExtractor create(Class<?> clazz) {
		PageModelExtractor pageModelExtractor = new PageModelExtractor();
		pageModelExtractor.init(clazz);
		return pageModelExtractor;
	}

	private void init(Class<?> clazz) {
		this.clazz = clazz;
		initClassExtractors();
		fieldExtractors = new ArrayList<FieldExtractor>();
		for (Field field : clazz.getDeclaredFields()) {
			field.setAccessible(true);
			FieldExtractor fieldExtractor = null;
			boolean hasComboExtract = false;
			fieldExtractor = getAnnotationExtract(clazz, field);
			if (fieldExtractor == null) {
				fieldExtractor = getAnnotationExtractBy(clazz, field);
			} else {
				hasComboExtract = true;
			}
			FieldExtractor fieldExtractorTmp = getAnnotationExtractByRaw(clazz, field);
			if (fieldExtractor != null && fieldExtractorTmp != null) {
				throw new IllegalStateException(
						"Only one of 'ExtractBy ExtractByRaw ExtractByUrl' can be added to a field!");
			} else if (fieldExtractor == null && fieldExtractorTmp != null) {
				fieldExtractor = fieldExtractorTmp;
			}
			if (!hasComboExtract) {
				// ExtractBy2 & ExtractBy3
				if (fieldExtractor != null) {
					addAnnotationExtractBy2(fieldExtractor);
					addAnnotationExtractBy3(fieldExtractor);
				}
			}
			fieldExtractorTmp = getAnnotationExtractByUrl(clazz, field);
			if (fieldExtractor != null && fieldExtractorTmp != null) {
				throw new IllegalStateException(
						"Only one of 'ExtractBy ExtractByRaw ExtractByUrl' can be added to a field!");
			} else if (fieldExtractor == null && fieldExtractorTmp != null) {
				fieldExtractor = fieldExtractorTmp;
			}
			if (fieldExtractor != null) {
				if (!fieldExtractor.isMulti() && !String.class.isAssignableFrom(field.getType())) {
					throw new IllegalStateException("Field " + field.getName() + " must be string");
				} else if (fieldExtractor.isMulti() && !List.class.isAssignableFrom(field.getType())) {
					throw new IllegalStateException("Field " + field.getName() + " must be list");
				}
				fieldExtractors.add(fieldExtractor);
			}
		}
	}

	private FieldExtractor getAnnotationExtractByUrl(Class<?> clazz, Field field) {
		FieldExtractor fieldExtractor = null;
		ExtractByUrl extractByUrl = field.getAnnotation(ExtractByUrl.class);
		if (extractByUrl != null) {
			String regexPattern = extractByUrl.value();
			if (regexPattern.trim().equals("")) {
				regexPattern = ".*";
			}
			fieldExtractor = new FieldExtractor(field, new RegexSelector(regexPattern), FieldExtractor.Source.Url,
					extractByUrl.notNull(), extractByUrl.multi());
			Method setterMethod = getSetterMethod(clazz, field);
			if (setterMethod != null) {
				fieldExtractor.setSetterMethod(setterMethod);
			}
		}
		return fieldExtractor;
	}

	private FieldExtractor getAnnotationExtract(Class<?> clazz, Field field) {
		FieldExtractor fieldExtractor = null;
		ComboExtract extract = field.getAnnotation(ComboExtract.class);
		if (extract != null) {
			ExtractBy[] extractBys = extract.value();
			ComboExtract.OP op = extract.op();
			for (int i = 0; i < extractBys.length; i++) {
				ExtractBy extractBy = extractBys[i];
				ConfigInfo configInfo = extractBy.configure();
				boolean isOuterHtml = configInfo.isOuterHtml();
				String attrName = configInfo.attr();
				String defaultValue = configInfo.defaultValue();
				
				if (i == 0) {
					fieldExtractor = getAnnotationExtractBy(extractBy, clazz, field);
				} else {
					fieldExtractor = addExtractBy(fieldExtractor, extractBy.type(), extractBy.value(), op, isOuterHtml, attrName, defaultValue);
				}
			}
		}

		return fieldExtractor;
	}

	private Selector getSelector(ExprType type, String expr) {
		return getSelector(type, expr, true, null, null);
	}
	
	private Selector getSelector(ExprType type, String expr, boolean isOuterHtml, String attrName, String defaultValue) {
		Selector selector = null;
		switch (type) {
		case CSS:
			selector = new CssSelector(expr, isOuterHtml, attrName, defaultValue);
			break;
		case REGEX:
			selector = new RegexSelector(expr, defaultValue);
			break;
		case XPATH:
			selector = new XpathSelector(expr, defaultValue);
			break;
		case CONTAINS:
			selector = new TextContainSelector(expr, defaultValue);
			break;
		default:
			selector = new XpathSelector(expr, defaultValue);
		}
		return selector;
	}
	
	private FieldExtractor addExtractBy(FieldExtractor fieldExtractor, ExprType type, String expr, ComboExtract.OP op,
			boolean isOuterHtml, String attrName, String defaultValue) {
		if (fieldExtractor == null)
			return null;
		Selector selector = getSelector(type, expr, isOuterHtml, attrName,defaultValue);
		if (ComboExtract.OP.AND.equals(op)) {
			fieldExtractor.setSelector(new AndSelector(fieldExtractor.getSelector(), selector));
		} else {
			fieldExtractor.setSelector(new OrSelector(fieldExtractor.getSelector(), selector));
		}
		return fieldExtractor;
	}
	
	private FieldExtractor addExtractBy(FieldExtractor fieldExtractor, ExprType type, String expr, ComboExtract.OP op) {
		return addExtractBy(fieldExtractor, type, expr, op, false, null, null);
	}

	private FieldExtractor getAnnotationExtractBy(Class<?> clazz, Field field) {
		ExtractBy extractBy = field.getAnnotation(ExtractBy.class);
		FieldExtractor fieldExtractor = getAnnotationExtractBy(extractBy, clazz, field);
		return fieldExtractor;
	}

	private FieldExtractor getAnnotationExtractBy(ExtractBy extractBy, Class<?> clazz, Field field) {
		FieldExtractor fieldExtractor = null;
		if (extractBy != null) {
			String value = extractBy.value();
			ConfigInfo configInfo = extractBy.configure();
			Selector selector = getSelector(extractBy.type(), value, configInfo.isOuterHtml(), configInfo.attr(), configInfo.defaultValue());
			fieldExtractor = new FieldExtractor(field, selector, FieldExtractor.Source.Html, extractBy.notNull(),
					extractBy.multi());
			Method setterMethod = getSetterMethod(clazz, field);
			if (setterMethod != null) {
				fieldExtractor.setSetterMethod(setterMethod);
			}
		}
		return fieldExtractor;
	}

	@SuppressWarnings("deprecation")
	private void addAnnotationExtractBy2(FieldExtractor fieldExtractor) {
		ExtractBy2 extractBy = fieldExtractor.getField().getAnnotation(ExtractBy2.class);
		if (extractBy != null) {
			String expr = extractBy.value();
			ExprType type = extractBy.type();
			addExtractBy(fieldExtractor, type, expr, ComboExtract.OP.AND);
		}
	}

	@SuppressWarnings("deprecation")
	private void addAnnotationExtractBy3(FieldExtractor fieldExtractor) {
		ExtractBy3 extractBy = fieldExtractor.getField().getAnnotation(ExtractBy3.class);
		if (extractBy != null) {
			String expr = extractBy.value();
			ExprType type = extractBy.type();
			addExtractBy(fieldExtractor, type, expr, ComboExtract.OP.AND);
		}
	}

	private FieldExtractor getAnnotationExtractByRaw(Class<?> clazz, Field field) {
		FieldExtractor fieldExtractor = null;
		ExtractByRaw extractByRaw = field.getAnnotation(ExtractByRaw.class);
		if (extractByRaw != null) {
			String value = extractByRaw.value();
			Selector selector = getSelector(extractByRaw.type(), value);
			fieldExtractor = new FieldExtractor(field, selector, FieldExtractor.Source.RawHtml, extractByRaw.notNull(),
					extractByRaw.multi());
			Method setterMethod = getSetterMethod(clazz, field);
			if (setterMethod != null) {
				fieldExtractor.setSetterMethod(setterMethod);
			}
		}
		return fieldExtractor;
	}

	public static Method getSetterMethod(Class<?> clazz, Field field) {
		String name = "set" + StringUtils.capitalize(field.getName());
		try {
			Method declaredMethod = clazz.getDeclaredMethod(name, field.getType());
			declaredMethod.setAccessible(true);
			return declaredMethod;
		} catch (NoSuchMethodException e) {
			return null;
		}
	}

	private void initClassExtractors() {
		Annotation annotation = clazz.getAnnotation(TargetUrl.class);
		if (annotation == null) {
			targetUrlPatterns.add(Pattern.compile(".*"));
		} else {
			TargetUrl targetUrl = (TargetUrl) annotation;
			String[] value = targetUrl.value();
			for (String s : value) {
				Pattern pattern = normalizeRegex(s);
				if (pattern != null)
					targetUrlPatterns.add(pattern);
			}
			if (!targetUrl.sourceRegion().equals("")) {
				targetUrlRegionSelector = new XpathSelector(targetUrl.sourceRegion());
			}
		}
		annotation = clazz.getAnnotation(HelpUrl.class);
		if (annotation != null) {
			HelpUrl helpUrl = (HelpUrl) annotation;
			String[] value = helpUrl.value();
			for (String s : value) {
				Pattern pattern = normalizeRegex(s);
				if (pattern != null)
					helpUrlPatterns.add(pattern);
			}
			if (!helpUrl.sourceRegion().equals("")) {
				helpUrlRegionSelector = new XpathSelector(helpUrl.sourceRegion());
			}
		}
		annotation = clazz.getAnnotation(ExtractBy.class);
		if (annotation != null) {
			ExtractBy extractBy = (ExtractBy) annotation;
			extractor = new Extractor(new XpathSelector(extractBy.value()), Extractor.Source.Html, extractBy.notNull(),
					extractBy.multi());
		}
	}
	
	private Pattern normalizeRegex(String s) {
		Pattern pattern = null;
		if(StringUtils.isEmpty(s))
			return pattern;
		pattern = Pattern.compile("(" + s.replace(".", "\\.").replace("*", "[^\"'#]*") + ")");
		return pattern;
	}

	public Object process(Page page) {
		boolean matched = false;
		for (Pattern targetPattern : targetUrlPatterns) {
			if (targetPattern.matcher(page.getUrl().toString()).matches()) {
				matched = true;
			}
		}
		if (!matched) {
			return null;
		}
		if (extractor == null) {
			return processSingle(page, page.getHtml().toString());
		} else {
			if (extractor.multi) {
				List<Object> os = new ArrayList<Object>();
				List<String> list = extractor.getSelector().selectList(page.getHtml().toString());
				for (String s : list) {
					Object o = processSingle(page, s);
					if (o != null) {
						os.add(o);
					}
				}
				return os;
			} else {
				String select = extractor.getSelector().select(page.getHtml().toString());
				Object o = processSingle(page, select);
				return o;
			}
		}
	}

	private Object processSingle(Page page, String html) {
		Object o = null;
		try {
			o = clazz.newInstance();
			for (FieldExtractor fieldExtractor : fieldExtractors) {
				if (fieldExtractor.isMulti()) {
					List<String> value;
					switch (fieldExtractor.getSource()) {
					case RawHtml:
						value = fieldExtractor.getSelector().selectList(page.getHtml().toString());
						break;
					case Html:
						value = fieldExtractor.getSelector().selectList(html);
						break;
					case Url:
						value = fieldExtractor.getSelector().selectList(page.getUrl().toString());
						break;
					default:
						value = fieldExtractor.getSelector().selectList(html);
					}
					if ((value == null || value.size() == 0) && fieldExtractor.isNotNull()) {
						return null;
					}
					setField(o, fieldExtractor, value);
				} else {
					String value;
					switch (fieldExtractor.getSource()) {
					case RawHtml:
						value = fieldExtractor.getSelector().select(page.getHtml().toString());
						break;
					case Html:
						value = fieldExtractor.getSelector().select(html);
						break;
					case Url:
						value = fieldExtractor.getSelector().select(page.getUrl().toString());
						break;
					default:
						value = fieldExtractor.getSelector().select(html);
					}
					if (StringUtils.isEmpty(value) && fieldExtractor.isNotNull()) {
						return null;
					}
					setField(o, fieldExtractor, value);
				}
			}
			if (AfterExtractor.class.isAssignableFrom(clazz)) {
				((AfterExtractor) o).afterProcess(page);
			}
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}
		return o;
	}

	private void setField(Object o, FieldExtractor fieldExtractor, Object value) throws IllegalAccessException,
			InvocationTargetException {
		if (fieldExtractor.getSetterMethod() != null) {
			fieldExtractor.getSetterMethod().invoke(o, value);
		}
		fieldExtractor.getField().set(o, value);
	}

	Class<?> getClazz() {
		return clazz;
	}

	List<Pattern> getTargetUrlPatterns() {
		return targetUrlPatterns;
	}

	List<Pattern> getHelpUrlPatterns() {
		return helpUrlPatterns;
	}

	Selector getTargetUrlRegionSelector() {
		return targetUrlRegionSelector;
	}

	Selector getHelpUrlRegionSelector() {
		return helpUrlRegionSelector;
	}
}
