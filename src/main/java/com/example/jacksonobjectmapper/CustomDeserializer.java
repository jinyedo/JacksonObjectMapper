package com.example.jacksonobjectmapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.ObjectCodec;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

// 미완성
public class CustomDeserializer extends JsonDeserializer<Object> {

    private final Object o;
    private final Map<String, String> customFields;
    private final List<String> errorFields;

    public CustomDeserializer(Object o, Map<String, String> customFields) {
        this.o = o;
        this.customFields = customFields;
        this.errorFields = new ArrayList<>();
    }

    @Override
    public Object deserialize(JsonParser parser, DeserializationContext context) throws IOException {
        try {
            ObjectCodec codec = parser.getCodec();
            JsonNode node = codec.readTree(parser);

            Field[] fields = o.getClass().getDeclaredFields();

            for (String key : customFields.keySet()) {
                if (node.get(key) == null) errorFields.add(key);
            }
            if (errorFields.size() > 0)
                throw new Exception(errorFields.toString() + "는 JSON 존재하지 않는 KEY 입니다.");

            for (String value : customFields.values()) {
                errorFields.clear();
                if (Arrays.stream(fields).noneMatch(field -> field.getName().equals(value))) errorFields.add(value);
            }
            if (errorFields.size() > 0)
                throw new Exception(errorFields.toString() + "는 " + o.getClass() + "에 존재하지 않는 필드입니다.");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return o;
    }
}
