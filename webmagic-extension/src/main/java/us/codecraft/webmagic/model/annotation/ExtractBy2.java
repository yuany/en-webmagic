package us.codecraft.webmagic.model.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * 定义类或者字段的抽取规则，只能在Extract、ExtractByRaw之后使用。<br>
 * @see {@link ComboExtract}
 * @author code4crafter@gmail.com <br>
 * Date: 13-8-1 <br>
 * Time: 下午8:40 <br>
 */
@Deprecated
@Retention(java.lang.annotation.RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD})
public @interface ExtractBy2 {

    String value();

    ExprType type() default ExprType.XPATH;
}
