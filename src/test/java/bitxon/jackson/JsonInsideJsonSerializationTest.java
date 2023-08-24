package bitxon.jackson;

import com.fasterxml.jackson.core.JacksonException;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.IOException;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

class JsonInsideJsonSerializationTest {
// ------------------------------ Configuration ---------------------------------------------------


    public static final class UserDeserializer extends StdDeserializer<User> {

        public UserDeserializer() {
            this(null);
        }

        public UserDeserializer(Class<?> vc) {
            super(vc);
        }

        @Override
        public User deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JacksonException {
            return jsonParser.readValueAs(User.class);
        }
    }

    public static final class UserSerializer extends StdSerializer<User> {

        public UserSerializer() {
            this(null);
        }

        public UserSerializer(Class<User> t) {
            super(t);
        }

        @Override
        public void serialize(User user, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException {
            jsonGenerator.writeObject(user);
        }
    }


    public record User(
        String id,
        String name
    ) {}

    public record Entity (
        String type,
        @JsonDeserialize(using = UserDeserializer.class)
        @JsonSerialize(using = UserSerializer.class)
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

    @Disabled // FIXME
    @Test
    void javaToJson() throws Exception {
        // when
        var result = objectMapper.writeValueAsString(ENTITY);
        // then
        assertThatJson(result).isEqualTo(JSON);
    }

    @Disabled // FIXME
    @Test
    void jsonToJava() throws Exception {

        // when
        var result = objectMapper.readValue(JSON, Entity.class);
        // then
        assertThat(result).usingRecursiveComparison().isEqualTo(ENTITY);
    }
}
