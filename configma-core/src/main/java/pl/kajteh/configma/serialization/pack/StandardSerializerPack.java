package pl.kajteh.configma.serialization.pack;

import pl.kajteh.configma.serialization.ConfigSerializer;
import pl.kajteh.configma.serialization.impl.UUIDSerializer;
import pl.kajteh.configma.serialization.impl.datetime.DateSerializer;
import pl.kajteh.configma.serialization.impl.datetime.InstantSerializer;

import java.util.List;

public class StandardSerializerPack implements ConfigSerializerPack {

    @Override
    public List<ConfigSerializer<?>> getSerializers() {
        return List.of(
                new InstantSerializer(),
                new UUIDSerializer(),
                new DateSerializer());
    }
}
