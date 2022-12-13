package part2;

import part2.test_classes.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.file.Path;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

class UtilClassLoaderTest {
    private static Path propsPath;
    @BeforeAll
    static void setUp() {
        propsPath = Path.of("src/main/resources/part2/class.properties");
//        stringProperty=valueOfStringProperty
//        numberProperty=111
//        timeProperty=12.12.2022 09:40
//        another=testSideProperty
    }

    @Test
    void shouldNotReturnNull() {
        assertNotNull(UtilClassLoader.loadFromProperties(PropertyClassValid.class, propsPath));
    }

    @Test
    void shouldReturnNewInstanceWithAllFieldsSet() {
        PropertyClassValid expected = new PropertyClassValid();
        expected.setTimeProperty(parseInstant());
        expected.setMyNumber(111);
        expected.setStringProperty("valueOfStringProperty");
        PropertyClassValid actual = UtilClassLoader.loadFromProperties(PropertyClassValid.class, propsPath);
        System.out.println(actual);
        assertEquals(expected, actual);
    }

    @Test
    void instantWithoutFormatPropertyShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> UtilClassLoader.loadFromProperties(PropertyClassFormatAbsence.class, propsPath));
        assertEquals(exception.getMessage(), "@Property - Missing expected time format");
    }

    @Test
    void instantWithInvalidPatternShouldThrowException() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> UtilClassLoader.loadFromProperties(PropertyClassInvalidFormatPattern.class, propsPath));
        assertEquals(exception.getMessage(), "Unknown pattern letter: t");
    }

    @Test
    void shouldThrowExceptionIfPropertyDoesntMatchAnyField() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> UtilClassLoader.loadFromProperties(PropertyClassInvalidFieldName.class, propsPath));
        assertEquals(exception.getMessage(), "Found no property to match provided field: STRING");

        exception = assertThrows(IllegalArgumentException.class,
                () -> UtilClassLoader.loadFromProperties(PropertyClassInvalidPropertyName.class, propsPath));
        assertEquals(exception.getMessage(), "Found no property to match provided field annotated with name: STRING");
    }

    @Test
    void shouldThrowExceptionIfPropertyTypeIsNotSupported() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> UtilClassLoader.loadFromProperties(PropertyClassInvalidType.class, propsPath));
        assertEquals(exception.getMessage(), "Unsupported data type. Candidates are String, Instant, Integer, int");

    }

    private static Instant parseInstant() {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy HH:mm");
        LocalDateTime localDateTime = LocalDateTime.parse("12.12.2022 09:40", dateTimeFormatter);
        return localDateTime.toInstant(ZoneOffset.UTC);
    }

}