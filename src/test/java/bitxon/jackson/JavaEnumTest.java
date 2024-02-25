package bitxon.jackson;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import java.util.stream.Stream;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

class JavaEnumTest {
// ------------------------------ Configuration ---------------------------------------------------

    enum UserType {
        @JsonProperty("admin")
        ADMIN,
        @JsonProperty("user/v2")
        USER,
        @JsonProperty("user/v1")
        USER_LEGACY,
    }

    ObjectMapper objectMapper = JsonMapper.builder().build();


// ------------------------------ Data ------------------------------------------------------------

    static Stream<Arguments> enumToJsonValue() {
        return Stream.of(
            Arguments.of(UserType.ADMIN, "admin"),
            Arguments.of(UserType.USER, "user/v2"),
            Arguments.of(UserType.USER_LEGACY, "user/v1")
        );
    }

// ------------------------------ Tests -----------------------------------------------------------


    @ParameterizedTest
    @MethodSource("enumToJsonValue")
    void javaToJson(UserType userType, String jsonValue) throws Exception {
        // when
        var result = objectMapper.writeValueAsString(userType);
        // then
        assertThatJson(result).isEqualTo(jsonValue);
    }

    @ParameterizedTest
    @MethodSource("enumToJsonValue")
    void jsonToJava(UserType userType, String jsonValue) throws Exception {
        // when
        var result = objectMapper.convertValue(jsonValue, UserType.class);
        // then
        assertThat(result).isEqualTo(userType);
    }
}
