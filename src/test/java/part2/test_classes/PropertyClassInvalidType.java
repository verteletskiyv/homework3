package part2.test_classes;

import part2.Property;

import java.time.Instant;

public class PropertyClassInvalidType {
    private boolean stringProperty;
    @Property(name = "numberProperty")
    private int myNumber;

    @Property(format = "dd.MM.yyyy HH:mm")
    private Instant timeProperty;
}


