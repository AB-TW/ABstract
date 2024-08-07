package cn.dpc.ab.persistent.couchbase;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

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
            this.objData = (Map<?, ?>) data;
            return;
        }

        if (data instanceof List) {
            this.arrayData = ((List<?>) data).stream().map(ObjectAdapter::new).toList();
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
            return objData;
        }

        if (null != this.arrayData) {
            return arrayData.stream().map(item -> ((ObjectAdapter) item).toObject()).toList();
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
