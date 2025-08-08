package pl.kajteh.configma.serialization.serializer;

import java.util.List;

public interface SerializerPack {
    List<Serializer<?>> getSerializers();
}
