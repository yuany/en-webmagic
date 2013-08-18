package us.codecraft.webmagic.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * extended config info
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD, ElementType.TYPE })
public @interface ConfigInfo {

	 /**
     * determine whether use which groupNo, default group 0.
     * @return
     */
    int groupNo() default 0;
    
    /**
     * determine whether output outerhtml(with tag) or text(without tag) when {@code type} is CSS
     * @return
     */
    boolean isOuterHtml() default true;
    
    /**
     * determine whether get the attribute if the value is not empty when {@code type} is CSS
     * @return
     */
    String attrName() default "";
    
    /**
     * determine whether use the value as default value if the value is not empty
     * @return
     */
    String defaultValue() default "";
    
    /**
     * determine whether trim the value if the value is {@code true}, default not trim
     * @return
     */
    boolean isTrim() default false;
    
    /**
     * determine whether remove tag the value if the value is {@code true}, default not remove
     * @return
     */
    boolean isRemoveTag() default false;
}
