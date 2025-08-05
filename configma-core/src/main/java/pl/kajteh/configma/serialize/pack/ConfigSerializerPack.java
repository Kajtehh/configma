package pl.kajteh.configma.serialize.pack;

import pl.kajteh.configma.serialize.ConfigSerializer;

import java.util.List;

public interface ConfigSerializerPack {
    List<ConfigSerializer<?>> getSerializers();
}
