package bitxon.jackson;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.util.StdConverter;
import org.junit.jupiter.api.Test;


import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

class JsonInsideJsonConverterTest {
// ------------------------------ Configuration ---------------------------------------------------


    public static final class UserDeserializer extends StdConverter<String, User> {
        private final ObjectMapper objectMapper = JsonMapper.builder().build();

        @Override
        public User convert(String str) {
            try {
                return objectMapper.readValue(str, User.class);
            } catch (JsonProcessingException e) {
                return null;
            }
        }

    }

    public static final class UserSerializer extends StdConverter<User, String> {
        private final ObjectMapper objectMapper = JsonMapper.builder().build();

        @Override
        public String convert(User user) {
            try {
                return objectMapper.writeValueAsString(user);
            } catch (JsonProcessingException e) {
                return null;
            }
        }
    }


    public record User(
        String id,
        String name
    ) {}

    public record Entity (
        String type,
        @JsonDeserialize(converter = UserDeserializer.class)
        @JsonSerialize(converter = UserSerializer.class)
        User data
    ){}


    ObjectMapper objectMapper = JsonMapper.builder().build();

// ------------------------------ Data ------------------------------------------------------------

    static final Entity ENTITY = new Entity("user", new User("123", "Mike"));
    static final String JSON = """
        {
          "type": "user",
          "data": "{\\"id\\":\\"123\\",\\"name\\":\\"Mike\\"}"
        }
        """;

// ------------------------------ Tests -----------------------------------------------------------

    @Test
    void javaToJson() throws Exception {
        // when
        var result = objectMapper.writeValueAsString(ENTITY);
        // then
        assertThatJson(result).isEqualTo(JSON);
    }

    @Test
    void jsonToJava() throws Exception {

        // when
        var result = objectMapper.readValue(JSON, Entity.class);
        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(ENTITY);
    }
}
