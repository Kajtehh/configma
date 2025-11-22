package dev.kajteh.configma.serialization.helper;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public abstract class TypeReference<T> {

    private final Type type;

    protected TypeReference() {
        if (!(this.getClass().getGenericSuperclass() instanceof ParameterizedType parameterizedType)) {
            throw new RuntimeException("Missing type parameter.");
        }

        this.type = parameterizedType.getActualTypeArguments()[0];
    }

    public Type getType() {
        return this.type;
    }
}