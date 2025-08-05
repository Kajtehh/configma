package pl.kajteh.configma.serialize.pack;

import pl.kajteh.configma.serialize.ConfigSerializer;
import pl.kajteh.configma.serialize.impl.UUIDSerializer;
import pl.kajteh.configma.serialize.impl.datetime.DateSerializer;
import pl.kajteh.configma.serialize.impl.datetime.InstantSerializer;

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
