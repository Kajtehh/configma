package dev.kajteh.configma.annotation;

import dev.kajteh.configma.serialization.serializer.Serializer;
import org.jetbrains.annotations.Nullable;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/* TODO: implement it */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface UseSerializer {
    @Nullable Class<? extends Serializer<?, ?>> value();
}
