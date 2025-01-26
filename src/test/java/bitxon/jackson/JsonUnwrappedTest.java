package bitxon.jackson;

import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;


import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class JsonUnwrappedTest {
    // ------------------------------ Configuration ---------------------------------------------------

    record Address(String street, String city) {}
    record User(String fullName, @JsonUnwrapped Address address) {}

    ObjectMapper objectMapper = JsonMapper.builder().build();


// ------------------------------ Data ------------------------------------------------------------

    static final String JSON = """
        {
          "fullName" : "Mike Walker",
          "street" : "Main Street",
          "city" : "New York"
        }
        """;
    static final User USER = new User(
        "Mike Walker",
        new Address(
            "Main Street",
            "New York"
        ));

// ------------------------------ Tests -----------------------------------------------------------


    @Test
    void javaToJson() throws Exception {
        // when
        var result = objectMapper.writeValueAsString(USER);
        // then
        assertThatJson(result).isEqualTo(JSON);
    }

    @Test
    void jsonToJava() throws Exception {
        assertThatThrownBy(() -> objectMapper.readValue(JSON, User.class))
            .isInstanceOf(Exception.class)
            .hasMessageContaining("`@JsonUnwrapped`: combination not yet supported");
    }
}
