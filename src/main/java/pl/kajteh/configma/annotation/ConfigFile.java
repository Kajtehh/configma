package pl.kajteh.configma.annotation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface ConfigFile {
    String[] header() default {};
    String[] footer() default {};
}
