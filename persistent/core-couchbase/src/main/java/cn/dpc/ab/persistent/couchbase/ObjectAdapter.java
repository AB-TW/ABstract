package cn.dpc.ab.persistent.couchbase;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * ObjectAdapter represents a object in the database.
 */
@Getter
@Setter
@NoArgsConstructor
public class ObjectAdapter {
    private String stringData;
    private Map<?, ?> objData;
    private Number numData;
    private Boolean boolData;
    private List<?> arrayData;

    /**
     * Constructor.
     *
     * @param data the data to be adapted.
     */
    public ObjectAdapter(Object data) {
        if (null == data) {
            return;
        }

        if (data instanceof Map<?, ?>) {
            this.objData = ((Map<?, ?>) data).entrySet().stream()
                    .map(entry -> (entry.getValue() instanceof Map<?, ?> || entry.getValue() instanceof List)
                            ? Map.entry(entry.getKey(), new ObjectAdapter(entry.getValue()))
                            : entry
                    )
                    .filter(entry -> null != entry.getValue())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
            return;
        }

        if (data instanceof List) {
            this.arrayData = ((List<?>) data).stream().map(item -> (item instanceof Map<?, ?> || item instanceof List)
                    ? new ObjectAdapter(item)
                    : item).toList();
            return;
        }

        if (data instanceof Boolean) {
            this.boolData = (Boolean) data;
            return;
        }

        if (data instanceof Number) {
            this.numData = (Number) data;
            return;
        }

        this.stringData = String.valueOf(data);
    }

    /**
     * Converts the data to a object.
     *
     * @return the object.
     */
    public Object toObject() {
        if (null != this.objData) {
            return objData.entrySet().stream()
                    .map(entry -> entry.getValue() instanceof ObjectAdapter
                            ? Map.entry(entry.getKey(), ((ObjectAdapter) entry.getValue()).toObject())
                            : entry
                    ).filter(entry -> null != entry.getValue())
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        }

        if (null != this.arrayData) {
            return arrayData.stream().map(item -> item instanceof ObjectAdapter
                    ? ((ObjectAdapter) item).toObject()
                    : item).toList();
        }

        if (null != numData) {
            return numData;
        }

        if (null != boolData) {
            return boolData;
        }

        return this.stringData;
    }
}
