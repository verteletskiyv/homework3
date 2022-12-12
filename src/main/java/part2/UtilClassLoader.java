package part2;

import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UtilClassLoader {
    public static <T> T loadFromProperties(Class<T> cls, Path propertiesPath) {
        T instance = getInstance(cls);
        Map<String, String> propertiesMap = getPropertiesMap(propertiesPath);
        List<Field> allFields = Arrays.asList(cls.getDeclaredFields());
        List<Field> fieldsAnnotated = allFields.stream().filter(field -> field.isAnnotationPresent(Property.class)).toList();
        List<Field> fields = allFields.stream().filter(field -> !field.isAnnotationPresent(Property.class)).toList();
        validateFields(propertiesMap, fields, fieldsAnnotated);
        setFields(instance, propertiesMap, fields);
        setAnnotatedFields(instance, propertiesMap, fieldsAnnotated);
        return instance;
    }

    private static <T> T getInstance(Class<T> cls) {
        T t;
        try {
            t = cls.getConstructor().newInstance();
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException |
                 NoSuchMethodException e) {
            throw new RuntimeException("Failed to invoke default constructor of " + cls);
        }
        return t;
    }

    private static Map<String, String> getPropertiesMap(Path propertiesPath) {
        Properties props = new Properties();
        try {
            props.load(new FileInputStream(propertiesPath.toString()));
        } catch (IOException e) {
            throw new IllegalArgumentException("Couldn't load properties from " + propertiesPath);
        }
        return props.stringPropertyNames().stream().collect(Collectors.toMap(e -> e, props::getProperty));
    }

    private static void validateFields(Map<String, String> propertiesMap, List<Field> fields, List<Field> fieldsAnnotated) {
        List<Field> fieldsAnnotatedWithNames = fieldsAnnotated.stream()
                .filter(field -> !field.getAnnotation(Property.class).name().isEmpty()).toList();
        fieldsAnnotatedWithNames.forEach(fan -> {
            String name = fan.getAnnotation(Property.class).name();
            if (!propertiesMap.containsKey(fan.getAnnotation(Property.class).name()))
                throw new IllegalArgumentException("Found no property to match provided field annotated with name: " +name);
        });
        List<String> supportedTypes = Stream.of(int.class, Integer.class, String.class, Instant.class)
                .map(String::valueOf).toList();
        fields.forEach(f -> fieldsAnnotated.forEach(fa -> {
            if (!supportedTypes.contains(f.getType().toString()) || !supportedTypes.contains(fa.getType().toString()))
                throw new IllegalArgumentException("Unsupported data type. Candidates are String, Instant, Integer, int");
            if (!propertiesMap.containsKey(f.getName()))
                throw new IllegalArgumentException("Found no property to match provided field: " +f.getName());
        }));
    }

    private static void setFields(Object instance, Map<String, String> props, List<Field> fields) {
        props.forEach((key, value) -> fields.forEach(field -> {
            if (field.getName().equals(key)) {
                field.setAccessible(true);
                if (field.getType().equals(int.class) || field.getType().equals(Integer.class)) {
                    try {
                        field.set(instance, Integer.valueOf(value));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Requested field is not accessible");
                    }
                } else if (field.getType().equals(Instant.class)) {
                    throw new IllegalArgumentException("@Property - Missing expected time format");
                } else {
                    try {
                        field.set(instance, value);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Requested field is not accessible");
                    }
                }
            }
        }));
    }

    private static void setAnnotatedFields(Object instance, Map<String, String> props, List<Field> fields) {
        props.forEach((key, value) -> fields.forEach(field -> {
            if (!field.getAnnotation(Property.class).format().isEmpty() && field.getType().equals(Instant.class)) {
                String format = field.getAnnotation(Property.class).format();
                if (field.getAnnotation(Property.class).name().equals(key) || field.getName().equals(key)) {
                    field.setAccessible(true);
                    try {
                        field.set(instance, parseInstant(value, format));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Requested field is not accessible");
                    }
                }
            }
            if (field.getAnnotation(Property.class).name().equals(key)) {
                field.setAccessible(true);
                if (field.getType().equals(int.class) || field.getType().equals(Integer.class)) {
                    try {
                        field.set(instance, Integer.valueOf(value));
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Requested field is not accessible");
                    }
                } else {
                    try {
                        field.set(instance, value);
                    } catch (IllegalAccessException e) {
                        throw new RuntimeException("Requested field is not accessible");
                    }
                }
            }
        }));
    }

    private static Instant parseInstant(String instantString, String pattern) {
        DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(pattern);
        LocalDateTime localDateTime = LocalDateTime.parse(instantString, dateTimeFormatter);
        return localDateTime.toInstant(ZoneOffset.UTC);
    }
}