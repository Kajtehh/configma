package dev.kajteh.configma;

import java.util.function.Function;

public enum ConfigNamingStyle {
    CAMEL(ConfigNamingStyle::toCamelCase),
    SNAKE(ConfigNamingStyle::toSnakeCase),
    KEBAB(ConfigNamingStyle::toKebabCase);

    private final Function<String, String> formatter;

    ConfigNamingStyle(final Function<String, String> formatter) {
        this.formatter = formatter;
    }

    public String format(final String input) {
        return this.formatter.apply(input);
    }

    private static String toKebabCase(final String input) {
        return input.replaceAll("([a-z])([A-Z])", "$1-$2")
                .toLowerCase();
    }

    private static String toSnakeCase(final String input) {
        return input.replaceAll("([a-z])([A-Z])", "$1_$2")
                .toLowerCase();
    }

    private static String toCamelCase(final String input) {
        final var parts = input.split("[-_]");
        final var sb = new StringBuilder(parts[0]);

        for (int i = 1; i < parts.length; i++) {
            sb.append(parts[i].substring(0, 1).toUpperCase())
                    .append(parts[i].substring(1));
        }

        return sb.toString();
    }
}