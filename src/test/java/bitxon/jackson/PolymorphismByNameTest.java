package bitxon.jackson;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.fasterxml.jackson.annotation.JsonTypeName;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

class PolymorphismByNameTest {
// ------------------------------ Configuration ---------------------------------------------------

    @JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "customType", defaultImpl = Unknown.class)
    @JsonSubTypes({
        @JsonSubTypes.Type(value = User.class, name = "user"),
        @JsonSubTypes.Type(value = Admin.class, names = {"admin", "admin/v1", "admin/v2"})
    })
    private interface AbstractRole {}

    @JsonTypeName("user")
    private record User(String userId) implements AbstractRole {}
    @JsonTypeName("admin")
    private record Admin(String adminId) implements AbstractRole {}
    @JsonTypeName("unknown")
    private record Unknown() implements AbstractRole {}


    ObjectMapper objectMapper = JsonMapper.builder().build();

// ------------------------------ Data ------------------------------------------------------------

    static final String EMPTY_JSON = "{}";

    static final String USER_JSON = "{\"customType\" : \"user\", \"userId\": \"u-0000\"}";
    static final User USER_ENTITY = new User( "u-0000");

    static final String ADMIN_JSON = "{\"customType\" : \"admin\",\"adminId\": \"a-9999\"}";
    static final String ADMIN_1_JSON = "{\"customType\" : \"admin/v1\",\"adminId\": \"a-9999\"}";
    static final String ADMIN_2_JSON = "{\"customType\" : \"admin/v2\",\"adminId\": \"a-9999\"}";
    static final Admin ADMIN_ENTITY = new Admin( "a-9999");

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
    void serializeAdminToJson() throws Exception {
        // when
        var result = objectMapper.writeValueAsString(ADMIN_ENTITY);
        // then
        assertThatJson(result).isEqualTo(ADMIN_JSON);
    }

    @ValueSource(strings = {
        ADMIN_JSON,
        ADMIN_1_JSON,
        ADMIN_2_JSON,
    })
    @ParameterizedTest
    void deserializeJsonToAdmin(String json) throws Exception {
        // when
        AbstractRole abstractRole = objectMapper.readValue(json, AbstractRole.class);
        // then
        assertThat(abstractRole)
            .isExactlyInstanceOf(Admin.class)
            .isEqualTo(ADMIN_ENTITY);
    }

    @Test
    void deserializeJsonToDefault() throws Exception {
        // when
        AbstractRole abstractRole = objectMapper.readValue(EMPTY_JSON, AbstractRole.class);
        // then
        assertThat(abstractRole)
            .isExactlyInstanceOf(Unknown.class);
    }

    @Test
    void serializeDefaultToJson() throws Exception {
        // when
        var result = objectMapper.writeValueAsString(new Unknown());
        // then
        assertThatJson(result).isEqualTo("{\"customType\": \"unknown\"}");
    }
}
