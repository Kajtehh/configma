package dev.kajteh.configma.exception;

public class ConfigException extends RuntimeException {

    public ConfigException(final String message) {
        super(message);
    }

    public ConfigException(final String message, final Throwable cause) {
        super(message, cause);
    }

    public ConfigException(final Throwable cause) {
        super(cause);
    }
}