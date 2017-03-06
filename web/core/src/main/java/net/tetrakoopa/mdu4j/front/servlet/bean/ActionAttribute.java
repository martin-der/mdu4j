package net.tetrakoopa.mdu4j.front.servlet.bean;

import net.tetrakoopa.mdu4j.view.UIAttribute;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface ActionAttribute extends UIAttribute {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Info {

        String quickInfo() default "";

        String explanation() default "";

    }

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.FIELD)
    @interface Criticality {

        enum Level {
            NONE, WARNING, DISCOURAGED
        }

        Level value() default Level.NONE;

    }

}
