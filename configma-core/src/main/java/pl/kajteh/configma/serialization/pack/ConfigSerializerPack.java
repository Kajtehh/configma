package pl.kajteh.configma.serialization.pack;

import pl.kajteh.configma.serialization.ConfigSerializer;

import java.util.List;

public interface ConfigSerializerPack {
    List<ConfigSerializer<?>> getSerializers();
}
