package part2.test_classes;

import part2.Property;

import java.time.Instant;

public class PropertyClassFormatAbsence {
    private String stringProperty;
    @Property(name = "numberProperty")
    private int myNumber;

    private Instant timeProperty;
}

