package cn.dpc.ab.persistent.r2dbc;

import cn.dpc.ab.persistent.utils.JsonUtil;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;
import java.util.Map;

/**
 * ObjectAdapter represents a data adapter for object.
 */
@Getter
@Setter
@NoArgsConstructor
public class ObjectAdapter {
    private DataType type;
    private String data;

    /**
     * Constructor.
     *
     * @param data the data to be adapted.
     */
    public ObjectAdapter(Object data) {
        if (null == data) {
            this.type = DataType.STRING;
            return;
        }

        if (data instanceof Map<?, ?>) {
            this.type = DataType.OBJECT;
            this.data = JsonUtil.toJson(data);
            return;
        }

        if (data instanceof List) {
            this.type = DataType.ARRAY;
            this.data = JsonUtil.toJson(data);
            return;
        }

        if (data instanceof Boolean) {
            this.type = DataType.BOOLEAN;
            this.data = String.valueOf(data);
            return;
        }

        if (data instanceof Number) {
            this.type = DataType.NUMBER;
            this.data = String.valueOf(data);
            return;
        }

        this.type = DataType.STRING;
        this.data = String.valueOf(data);
    }

    /**
     * Constructor.
     *
     * @param type the type of the data.
     * @param data the data to be adapted.
     **/
    public ObjectAdapter(DataType type, String data) {
        this.type = type;
        this.data = data;
    }

    /**
     * toObject converts the data to object.
     *
     * @return the object
     */
    public Object toObject() {
        return switch (this.type) {
            case OBJECT -> JsonUtil.fromJson(this.data, Map.class);
            case NUMBER -> Double.parseDouble(this.data);
            case BOOLEAN -> Boolean.parseBoolean(this.data);
            case ARRAY -> JsonUtil.fromJson(this.data, List.class);
            default -> this.data;
        };
    }



    /**
     * DataType represents the type of the data.
     */
    public enum DataType {
        STRING,
        OBJECT,
        NUMBER,
        BOOLEAN,
        ARRAY
    }
}


