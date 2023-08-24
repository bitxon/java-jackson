package bitxon.jackson;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static com.fasterxml.jackson.databind.DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES;
import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

class PolymorphismByDeductionTest {
// ------------------------------ Configuration ---------------------------------------------------

    @JsonTypeInfo(use = JsonTypeInfo.Id.DEDUCTION, defaultImpl = Unknown.class)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = User.class),
        @JsonSubTypes.Type(value = Admin.class)
    })
    private interface AbstractRole {
    }

    private record User(String userId) implements AbstractRole {}
    private record Admin(String adminId) implements AbstractRole {}
    private record Unknown() implements AbstractRole {}


    ObjectMapper objectMapper = JsonMapper.builder()
        .disable(FAIL_ON_UNKNOWN_PROPERTIES)
        .build();

// ------------------------------ Data ------------------------------------------------------------

    static final String USER_JSON = "{\"userId\": \"u-0000\"}";
    static final User USER_ENTITY = new User("u-0000");

    static final String ADMIN_JSON = "{\"adminId\": \"a-1111\"}";
    static final Admin ADMIN_ENTITY = new Admin("a-1111");

    static final String EMPTY_JSON = "{}";
    static final String MIXED_JSON1 = "{\"userId\": \"u-0000\", \"adminId\": \"a-1111\"}";
    static final String MIXED_JSON2 = "{\"adminId\": \"a-1111\", \"userId\": \"u-0000\"}";


// ------------------------------ Tests -----------------------------------------------------------

    @Test
    void deserializeJsonToUser() throws Exception {
        // when
        AbstractRole abstractRole = objectMapper.readValue(USER_JSON, AbstractRole.class);
        // then
        assertThat(abstractRole)
            .isExactlyInstanceOf(User.class)
            .isEqualTo(USER_ENTITY);
    }

    @Test
    void serializeUserToJson() throws Exception {
        // when
        var result = objectMapper.writeValueAsString(USER_ENTITY);
        // then
        assertThatJson(result).isEqualTo(USER_JSON);
    }

    @Test
    void deserializeJsonToAdmin() throws Exception {
        // when
        AbstractRole abstractRole = objectMapper.readValue(ADMIN_JSON, AbstractRole.class);
        // then
        assertThat(abstractRole)
            .isExactlyInstanceOf(Admin.class)
            .isEqualTo(ADMIN_ENTITY);
    }

    @Test
    void serializeAdminToJson() throws Exception {
        // when
        var result = objectMapper.writeValueAsString(ADMIN_ENTITY);
        // then
        assertThatJson(result).isEqualTo(ADMIN_JSON);
    }

    @Test
    void deserializeJsonToDefault() throws Exception {
        // when
        AbstractRole abstractRole = objectMapper.readValue(EMPTY_JSON, AbstractRole.class);
        // then
        assertThat(abstractRole).isExactlyInstanceOf(Unknown.class);
    }

    @Test
    void serializeDefaultToJson() throws Exception {
        // when
        var result = objectMapper.writeValueAsString(new Unknown());
        // then
        assertThatJson(result).isEqualTo(EMPTY_JSON);
    }

    @Test
    @DisplayName("Deserialize mixed JSON to User because userId field is first")
    void deserializeMixedJson1ToUser() throws Exception {
        // when
        AbstractRole abstractRole = objectMapper.readValue(MIXED_JSON1, AbstractRole.class);
        // then
        assertThat(abstractRole).isExactlyInstanceOf(User.class);
    }

    @Test
    @DisplayName("Deserialize mixed JSON to Admin because adminId field is first")
    void deserializeMixedJson2ToAdmin() throws Exception {
        // when
        AbstractRole abstractRole = objectMapper.readValue(MIXED_JSON2, AbstractRole.class);
        // then
        assertThat(abstractRole).isExactlyInstanceOf(Admin.class);
    }

}
