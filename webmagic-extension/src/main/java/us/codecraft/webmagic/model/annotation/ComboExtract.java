package us.codecraft.webmagic.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * @author code4crafter@gmail.com <br>
 *         Date: 13-8-16 <br>
 *         Time: 下午11:09 <br>
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
public @interface ComboExtract {
	/**
	 * operator of match logic.
	 * <p>
	 * <b>AND</b>:need match all {@code expr}<br>
	 * <b>OR</b>: just match one {@code expr}(In accordance with the order)
	 * </p>
	 */
	public enum OP {AND, OR};

	OP op() default OP.OR;

	ExtractBy[] value();
}
