package part2.test_classes;

import part2.Property;

import java.time.Instant;

public class PropertyClassInvalidFormatPattern {
    private String stringProperty;
    @Property(name = "numberProperty")
    private int myNumber;
    @Property(format = "dd.MM.yyyy tt:mm")
    private Instant timeProperty;
}
