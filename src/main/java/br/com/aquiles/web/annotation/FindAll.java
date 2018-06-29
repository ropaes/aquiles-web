package br.com.aquiles.web.annotation;

import javax.enterprise.inject.Default;
import javax.enterprise.util.Nonbinding;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Use this annotation to find all beans by declared class.
 * This annotation only work if your property is inside a class extends AbstractMB, when PostConstruct is called the property annotaded with @FindAll is loaded. The property should use {@code List<YourClass>}, see example:
 * <pre>
 * {@code
 * public MyClass extends AbstractMB{
 *   @literal @FindAll
 *    private List<YourClass> beans;
 *  }
 * }
 * </pre>
 *
 * @author Ronaldo Lanhellas
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface FindAll {
    @Nonbinding
    Class qualifier() default Default.class;

    @Nonbinding
    String orderBy() default "id DESC";
}
