package net.tetrakoopa.mdu4j.front.servlet.bean;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public interface CommonRequestParameter {

    interface Attribute {
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        @interface Key {
            String [] value();
        }
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        @interface EmptyIsNull {
            boolean value() default true;
        }
        @Retention(RetentionPolicy.RUNTIME)
        @Target(ElementType.FIELD)
        @interface Default {
            boolean value() default true;
        }
    }

    String KEY_CONTENT_TYPE = "c-t";

    String VALUES_TRUE [] = { "yes","oui","true","1", "" };

    String VALUES_FALSE [] = { "no","non","false","0" };

}
