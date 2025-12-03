package dev.kajteh.configma.serialization.util;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public final class GenericType implements ParameterizedType {

    private final Type raw;
    private final Type[] args;

    private GenericType(final Type raw, final Type... args) {
        this.raw = raw;
        this.args = args.clone();
    }

    public static ParameterizedType of(final Type raw, final Type... args) {
        return new GenericType(raw, args);
    }

    @Override
    public Type[] getActualTypeArguments() {
        return this.args.clone();
    }

    @Override
    public Type getRawType() {
        return this.raw;
    }

    @Override
    public Type getOwnerType() {
        return null;
    }
}