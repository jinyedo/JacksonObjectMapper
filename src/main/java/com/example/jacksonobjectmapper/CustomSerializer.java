package com.example.jacksonobjectmapper;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class CustomSerializer extends JsonSerializer<Object> {

    // 커스텀할 필드들 - key: Object 의 원래 필드명(조회시 사용), Value: 커스텀할 필드명
    private final Map<String, String> customFields;

    public CustomSerializer(Map<String, String> customFields) {
        this.customFields = customFields;
    }

    @Override
    public void serialize(Object o, JsonGenerator gen, SerializerProvider serializers) throws IOException {
        gen.writeStartObject(); // 커스텀 준비
        custom(o, gen); // 커스텀
        gen.writeEndObject(); // 커스텀 종료
    }

    private void custom(Object o, JsonGenerator gen) {
        try {
            List<String> errorFields = new ArrayList<>();
            Field[] fields = o.getClass().getDeclaredFields(); // Object 의 필드 리스트

            for (String key : customFields.keySet()) { // Object 에 존재하지 않는 필드라면 errorFields 에 담기
                if (Arrays.stream(fields).noneMatch(field -> field.getName().equals(key))) errorFields.add(key);
            }

            if (errorFields.size() > 0) // Object 에 존재하지 않는 필드가 있다면 예외 발생
                throw new Exception(errorFields.toString() + "는 " + o.getClass() + "에 존재하지 않는 필드입니다.");

            for (Field field : fields) { // Object 필드명 커스텀
                field.setAccessible(true); // 필드 접근 제한 설정
                // 필드명 설정 - 커스텀할 필드가 아니라면 원래 필드명으로 설정, 커스텀할 필드라면 커스텀필드로 설정
                String fieldName =StringUtils.isBlank(
                        customFields.get(field.getName())) ? field.getName() : customFields.get(field.getName()
                );
                gen.writeObjectField(fieldName, field.get(o));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}



