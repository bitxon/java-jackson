package bitxon.jackson;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

class SimpleTest {
// ------------------------------ Configuration ---------------------------------------------------

    private record Entity(Integer id, String fullName, LocalDateTime registration) {}

    ObjectMapper objectMapper = JsonMapper.builder()
        .addModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .build();


// ------------------------------ Data ------------------------------------------------------------

    static final String JSON = """
        {
          "id" : 12,
          "fullName" : "Mike Walker",
          "registration" : "2012-11-27T23:45:54"
        }
        """;
    static final Entity ENTITY = new Entity(
        12,
        "Mike Walker",
        LocalDateTime.parse("2012-11-27T23:45:54"));

// ------------------------------ Tests -----------------------------------------------------------


    @Test
    void javaToJson() throws Exception {
        // when
        var result = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(ENTITY);
        // then
        assertThatJson(result).isEqualTo(JSON);
    }

    @Test
    void jsonToJava() throws Exception {
        // when
        var result = objectMapper.readValue(JSON, Entity.class);
        // then
        assertThat(result).isEqualTo(ENTITY);
    }


    // ------------------------------ Entities ----------------------------------------------

}
