package pl.kajteh.configma.extension.metadata;

import org.bukkit.configuration.file.YamlConfiguration;
import pl.kajteh.configma.extension.metadata.annotation.Comment;
import pl.kajteh.configma.extension.metadata.annotation.Description;
import pl.kajteh.configma.ConfigExtension;

import java.lang.reflect.Field;
import java.util.Arrays;

public class MetadataExtension implements ConfigExtension {

    @Override
    public void onLoad(Class<?> configClass, YamlConfiguration configuration) {
        final Description description = configClass.getAnnotation(Description.class);

        if(description == null) return;

        if(description.header() != null && description.header().length != 0) {
            configuration.options().setHeader(Arrays.asList(description.header()));
        }

        if(description.footer() != null && description.footer().length != 0) {
            configuration.options().setFooter(Arrays.asList(description.footer()));
        }
    }

    @Override
    public void onFieldSaved(Class<?> configClass, YamlConfiguration configuration, String path, Field field, Object value) {
        final Comment comment = field.getAnnotation(Comment.class);

        if(comment == null || comment.value().length == 0) return;

        configuration.setComments(path, Arrays.asList(comment.value()));
    }
}
