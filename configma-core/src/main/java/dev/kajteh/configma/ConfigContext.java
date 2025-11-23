package dev.kajteh.configma;

import dev.kajteh.configma.annotation.decoration.comment.CommentPrefix;
import dev.kajteh.configma.annotation.decoration.Footer;
import dev.kajteh.configma.annotation.decoration.Header;
import dev.kajteh.configma.annotation.decoration.Spacing;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public record ConfigContext(List<String> header, List<String> footer, int spacing, String commentPrefix, Map<String, List<String>> comments, Map<String, String> inlineComments) {

    public static ConfigContext of(final Class<?> type) {
        return new ConfigContext(
                type.isAnnotationPresent(Header.class) ? List.of(type.getAnnotation(Header.class).value()) : null,
                type.isAnnotationPresent(Footer.class) ? List.of(type.getAnnotation(Footer.class).value()) : null,
                type.isAnnotationPresent(Spacing.class) ? type.getAnnotation(Spacing.class).value() : 1,
                type.isAnnotationPresent(CommentPrefix.class) ? type.getAnnotation(CommentPrefix.class).value() : "# ",
                new HashMap<>(),
                new HashMap<>()
        );
    }
}
