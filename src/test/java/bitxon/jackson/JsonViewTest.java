package bitxon.jackson;

import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

class JsonViewTest {
// ------------------------------ Configuration ---------------------------------------------------

    private interface Views {
        interface Internal {}
        interface External {}
    }

    private record User(
        String name,
        @JsonView(Views.Internal.class)
        String internalUuid,
        @JsonView(Views.External.class)
        String externalId
    ) {}

    ObjectMapper objectMapper = JsonMapper.builder()
        .enable(MapperFeature.DEFAULT_VIEW_INCLUSION) // enabled by default, this is just for note
        .build();

// ------------------------------ Data ------------------------------------------------------------

    static final User USER_WITH_ALL_FIELDS = new User("Mike", "internalValue", "externalValue");
    static final String JSON_WITH_ALL_FIELDS = """
        {
            "name":"Mike",
            "externalId":"externalValue",
            "internalUuid":"internalValue"
        }
        """;

// ------------------------------ Tests -----------------------------------------------------------

    @Test
    void deserializeInternalFields() throws JsonProcessingException {
        final User deserializedValue = objectMapper
            .readerWithView(Views.Internal.class)
            .forType(User.class)
            .readValue(JSON_WITH_ALL_FIELDS);

        assertThat(deserializedValue)
            .hasFieldOrPropertyWithValue("name", "Mike")
            .hasFieldOrPropertyWithValue("internalUuid", "internalValue")
            .hasFieldOrPropertyWithValue("externalId", null);
    }

    @Test
    void deserializeExternalFields() throws JsonProcessingException {
        final User deserializedValue = objectMapper
            .readerWithView(Views.External.class)
            .forType(User.class)
            .readValue(JSON_WITH_ALL_FIELDS);

        assertThat(deserializedValue)
            .hasFieldOrPropertyWithValue("name", "Mike")
            .hasFieldOrPropertyWithValue("internalUuid", null)
            .hasFieldOrPropertyWithValue("externalId", "externalValue");
    }

    @Test
    void serializeInternalFields() throws JsonProcessingException {
        final String serializedValue = objectMapper
            .writerWithView(Views.Internal.class)
            .writeValueAsString(USER_WITH_ALL_FIELDS);

        assertThatJson(serializedValue)
            .isObject().doesNotContainKey("externalId")
            .isEqualTo("{\"name\":\"Mike\",\"internalUuid\":\"internalValue\"}");
    }


    @Test
    void serializeExternalFields() throws JsonProcessingException {
        final String serializedValue = objectMapper
            .writerWithView(Views.External.class)
            .writeValueAsString(USER_WITH_ALL_FIELDS);

        assertThatJson(serializedValue)
            .isObject().doesNotContainKey("internalUuid")
            .isEqualTo("{\"name\":\"Mike\",\"externalId\":\"externalValue\"}");
    }

}
