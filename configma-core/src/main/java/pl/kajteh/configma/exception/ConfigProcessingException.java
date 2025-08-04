package pl.kajteh.configma.exception;

public class ConfigProcessingException extends ConfigException {
    public ConfigProcessingException(String message) {
        super(message);
    }

    public ConfigProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}