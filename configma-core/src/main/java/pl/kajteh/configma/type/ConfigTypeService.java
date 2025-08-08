package pl.kajteh.configma.type;

import java.util.Collection;
import java.util.Map;

public class ConfigTypeService {

    private final ConfigTypeCache typeCache;

    public ConfigTypeService(final ConfigTypeCache typeCache) {
        this.typeCache = typeCache;
    }

    public void loadTypes(final String path, final Class<?> type, final Object value) {
        this.typeCache.putType(path, type);

        if(value instanceof Collection<?> collection) {
            int i = 0;

            for (Object element : collection) {
                this.loadTypes(path + ".[" + i++ + "]", element.getClass(), element);
            }
            return;
        }

        if(value instanceof Map<?,?> map) {
            for(final Map.Entry<?, ?> entry : map.entrySet()) {
                final Object k = entry.getKey();
                final Object v = entry.getValue();

                this.loadTypes(path + ".key", k.getClass(), k);
                this.loadTypes(path + ".value", v.getClass(), v);
            }
        }
    }
}
