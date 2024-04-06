package bitxon.jackson;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.*;

import static net.javacrumbs.jsonunit.assertj.JsonAssertions.assertThatJson;
import static org.assertj.core.api.Assertions.assertThat;

class JavaTimeTest {
    static final Clock CLOCK = Clock.fixed(Instant.parse("2024-03-25T10:29:17.878675Z"), ZoneId.systemDefault());
    ObjectMapper objectMapper = JsonMapper.builder()
        .addModule(new JavaTimeModule())
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS)
        .build();

    @Test
    void play() {
        System.out.println("Instant now          : " + Instant.now(CLOCK));
        System.out.println("LocalDateTime now    : " + LocalDateTime.now(CLOCK));
        System.out.println("OffsetDateTime now   : " + OffsetDateTime.now(CLOCK));
        System.out.println("ZonedDateTime now    : " + ZonedDateTime.now(CLOCK));
    }


// ------------------------------ Instant No Format -----------------------------------------------
    @Nested
    class InstantNoFormat {

        record Entity(
            Instant time
        ) {}
        static final Entity ENTITY = new Entity(Instant.parse("2024-03-25T10:29:17.878675Z"));
        static final String JSON = "{\"time\" : \"2024-03-25T10:29:17.878675Z\"}";

        @Test
        void javaToJson() throws Exception {
            // when
            var json = objectMapper.writeValueAsString(ENTITY);
            // then
            assertThatJson(json).isEqualTo(JSON);
        }

        @Test
        void jsonToJava() throws Exception {
            // when
            var entity = objectMapper.readValue(JSON, Entity.class);
            // then
            assertThat(entity).isEqualTo(ENTITY);
        }
    }

// ------------------------------ Instant Hardcoded Default Format --------------------------------
    @Nested
    class InstantHardcodedDefaultFormat {

        record Entity(
            // !!! we restrict nanoseconds to 6 digits !!! - behavior is different from Instant without format
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'", timezone = "UTC")
            Instant time
        ) {}
        static final Entity ENTITY = new Entity(Instant.parse("2024-03-25T10:29:17.878675Z"));
        static final String JSON = "{\"time\" : \"2024-03-25T10:29:17.878675Z\"}";

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
            assertThat(result).isEqualTo(ENTITY);
        }
    }

// ------------------------------ Instant No Nanoseconds Format -----------------------------------
    @Nested
    class InstantNoNanosFormat {

        record Entity(
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", timezone = "UTC")
            Instant time
        ) {}
        static final Entity ENTITY = new Entity(Instant.parse("2024-03-25T10:29:17Z"));
        static final String JSON = "{\"time\" : \"2024-03-25T10:29:17Z\"}";

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
            assertThat(result).isEqualTo(ENTITY);
        }
    }


// ------------------------------ LocalDateTime No Format -----------------------------------------
    @Nested
    class LocalDateTimeNoFormat {

        record Entity(
            LocalDateTime time
        ) {}
        static final Entity ENTITY = new Entity(LocalDateTime.parse("2024-03-25T10:29:17.878675"));
        static final String JSON = "{\"time\" : \"2024-03-25T10:29:17.878675\"}";

        @Test
        void javaToJson() throws Exception {
            // when
            var json = objectMapper.writeValueAsString(ENTITY);
            // then
            assertThatJson(json).isEqualTo(JSON);
        }

        @Test
        void jsonToJava() throws Exception {
            // when
            var entity = objectMapper.readValue(JSON, Entity.class);
            // then
            assertThat(entity).isEqualTo(ENTITY);
        }
    }

// ------------------------------ LocalDateTime Hardcoded Default Format --------------------------
    @Nested
    class LocalDateTimeHardcodedDefaultFormat {

        record Entity(
            // !!! we restrict nanoseconds to 6 digits !!! - behavior is different from LocalDateTime without format
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSSSS", timezone = "UTC")
            LocalDateTime time
        ) {}
        static final Entity ENTITY = new Entity(LocalDateTime.parse("2024-03-25T10:29:17.878675"));
        static final String JSON = "{\"time\" : \"2024-03-25T10:29:17.878675\"}";

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
            assertThat(result).isEqualTo(ENTITY);
        }
    }

// ------------------------------ LocalDateTime No Nanoseconds Format -----------------------------
    @Nested
    class LocalDateTimeNoNanosFormat {

        record Entity(
            @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss", timezone = "UTC")
            LocalDateTime time
        ) {}
        static final Entity ENTITY = new Entity(LocalDateTime.parse("2024-03-25T10:29:17"));
        static final String JSON = "{\"time\" : \"2024-03-25T10:29:17\"}";

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
            assertThat(result).isEqualTo(ENTITY);
        }
    }

}
