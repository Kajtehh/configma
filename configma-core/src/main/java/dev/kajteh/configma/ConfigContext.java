package dev.kajteh.configma;

import dev.kajteh.configma.annotation.meta.comment.CommentPrefix;
import dev.kajteh.configma.annotation.meta.Footer;
import dev.kajteh.configma.annotation.meta.Header;
import dev.kajteh.configma.annotation.meta.Spacing;
import dev.kajteh.configma.schema.ConfigSchema;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public record ConfigContext(Class<?> type, List<String> header, List<String> footer, int spacing, String commentPrefix, Map<String, List<String>> comments, Map<String, String> inlineComments) {

    private final static int DEFAULT_SPACING = 1;

    public static ConfigContext of(final Class<?> type) {
        return new ConfigContext(
                type,
                type.isAnnotationPresent(Header.class) ? List.of(type.getAnnotation(Header.class).value()) : null,
                type.isAnnotationPresent(Footer.class) ? List.of(type.getAnnotation(Footer.class).value()) : null,
                type.isAnnotationPresent(Spacing.class) ? type.getAnnotation(Spacing.class).value() : DEFAULT_SPACING,
                type.isAnnotationPresent(CommentPrefix.class) ? type.getAnnotation(CommentPrefix.class).value() : null,
                new HashMap<>(),
                new HashMap<>()
        );
    }

    public String commentPrefix(final String defaultPrefix) {
        return Optional.ofNullable(this.commentPrefix).orElse(defaultPrefix);
    }

    public void registerComments(final ConfigSchema<?> schema, final Function<String, String> formatter, final String parentPath) {
        for (final var field : schema.fields()) {
            final var name = field.key().name(formatter);
            final var path = parentPath != null ? parentPath + "." + name : name;

            if (field.comments() != null)
                this.comments.put(path, field.comments());

            if (field.inlineComment() != null)
                this.inlineComments.put(path, field.inlineComment());

            if (field.isNested())
                this.registerComments(field.nestedSchema(schema.instance()), formatter, path);
        }
    }
}
