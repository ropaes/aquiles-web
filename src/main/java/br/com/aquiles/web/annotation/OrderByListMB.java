package br.com.aquiles.web.annotation;

import javax.enterprise.util.Nonbinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to order beans in List, the default value is "id DESC".
 * This annotation just works with you don't override findAllBeans() method
 * <pre>
 * {@code
 * @literal @OrderByListMB
 * public MyClass extends BasicListMB<YourClass>{
 *  }
 * }
 * </pre>
 *
 * @author Ronaldo Lanhellas
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface OrderByListMB {
    @Nonbinding
    String orderBy() default "id DESC";
}
