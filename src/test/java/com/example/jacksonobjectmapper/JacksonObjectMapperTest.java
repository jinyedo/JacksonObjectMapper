package com.example.jacksonobjectmapper;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@SpringBootTest
public class JacksonObjectMapperTest {

    @Test // 객체를 JSON 으로 변환
    public void objToJson() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        User user = new User("Owen", 23);
        // 파일 출력
        objectMapper.writeValue(new File("user.json"), user);
        // 문자열 출력
        String userAsString = objectMapper.writeValueAsString(user);
        System.out.println(userAsString);
    }

    @Test // JSON 을 객체로 변환
    public void jsonToObj() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "{\"name\": \"Owen\", \"age\": 23}";
        // JSON 형식의 문자열을 객체로 변환
        User jsonStrToObj = objectMapper.readValue(json, User.class);
        // JSON 파일을 객체로 변환
        User jsonFileToObj = objectMapper.readValue(new File("user.json"), User.class);
        // JSON URL 을 객체로 변환
        User jsonUrlToObj = objectMapper.readValue(new URL("file:user.json"), User.class);

        System.out.println("jsonStrToObj: " + jsonStrToObj);
        System.out.println("jsonFileToObj: " + jsonFileToObj);
        System.out.println("jsonUrlToObj: " + jsonUrlToObj);
    }

    @Test // JSON 을 JsonNode 로 변경
    public void jsonToJsonNode() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "{\"name\": \"Owen\", \"age\": 23}";
        JsonNode jsonNode = objectMapper.readTree(json);
        String name = jsonNode.get("name").asText();
        int age = jsonNode.get("age").asInt();
        System.out.println("name: " + name + ", age: " + age);
    }

    @Test // JSON 을 List 로 변환
    public void jsonToList() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonArray = "[{\"name\": \"Owen\", \"age\": 23}, {\"name\": \"Ryan\", \"age\": 20}]";
        List<User> users = objectMapper.readValue(jsonArray, new TypeReference<List<User>>() {});
        for (User user : users) {
            System.out.println(user);
        }
    }

    @Test // JSON 을 Map 으로 변환
    public void jsonToMap() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String jsonArray = "[{\"name\": \"Owen\", \"age\": 23}, {\"name\": \"Ryan\", \"age\": 20}]";
        List<Map<String, Object>> users = objectMapper.readValue(
                jsonArray,
                new TypeReference<List<Map<String, Object>>>() {}
        );
        for (Map<String, Object> map : users) {
            for (String key : map.keySet()) {
                System.out.print(key + ": "+ map.get(key) + "\t");
            }
            System.out.println();
        }
    }

    @Test // JSON 에는 있지만 Mapping 될 Object 에는 없는 필드를 무시해야하는 경우
    public void jsonToObjIfFieldThatDoesNotExistIgnore() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "{\"name\":\"Owen\",\"age\":23,\"sex\":\"M\"}";
        // 아래 설정을 하지 않는다면 오류 발생
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        User user = objectMapper.readValue(json, User.class);
        System.out.println(user);
    }

    @Test // JSON 에서 Value 를 빈 값으로 넘겨줄 경우 빈 값 허용
    public void jsonToObjIfJsonValueIsEmptyNullable() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "{\"name\": \"null\", \"age\": null}";
        // JSON Value 가 null 일 경우 true 라면 오류 발생(default 는 false)
        objectMapper.configure(DeserializationFeature.FAIL_ON_NULL_FOR_PRIMITIVES, false);
        User user = objectMapper.readValue(json, User.class);
        System.out.println(user);
    }

    @Test // 객체의 필드명을 Custom 해서 JSON 으로 넘겨주기
    public void serialize() throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        SimpleModule module = new SimpleModule();

        // User 필드 커스텀 목록 - Key: User 의 원래 필드명, Value: 새롭게 커스텁할 필드명
        Map<String, String> userCustomFields = new HashMap<>();
        userCustomFields.put("name", "user_name");
        userCustomFields.put("age", "user_age");
        userCustomFields.put("pets", "user_pets");

        // Pet 필드 커스텀 목록 - Key: Pet 의 원래 필드명, Value: 새롭게 커스텁할 필드명
        Map<String, String> petCustomFields = new HashMap<>();
        petCustomFields.put("kind", "pet_kind");

        module.addSerializer(User.class, new CustomSerializer(userCustomFields));
        module.addSerializer(Pet.class, new CustomSerializer(petCustomFields));
        objectMapper.registerModule(module);

        String userJson = objectMapper.writeValueAsString(
                new User(
                        "Owen",
                        23,
                        List.of(
                                new Pet("Dog", "dog", 5),
                                new Pet("Cat", "cat", 10)
                        )
                )
        );
        System.out.println(userJson);
    }
}
