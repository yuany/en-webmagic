package us.codecraft.webmagic.model.annotation;


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
    String attr() default "";
    
    String defaultValue() default "";
}
