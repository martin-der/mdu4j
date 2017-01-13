package net.tetrakoopa.mdu4j.util.sel.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(value = ElementType.FIELD)
public @interface SimpleELAttribut {

	String name() default "";

	boolean ignore() default false;

}
