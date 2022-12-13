package part2.test_classes;

import part2.Property;

import java.time.Instant;

public class PropertyClassFormatAbsence {
    private String stringProperty;
    @Property(name = "numberProperty")
    private int myNumber;

    private Instant timeProperty;

    public void setStringProperty(String stringProperty) {
        this.stringProperty = stringProperty;
    }

    public void setMyNumber(int myNumber) {
        this.myNumber = myNumber;
    }

    public void setTimeProperty(Instant timeProperty) {
        this.timeProperty = timeProperty;
    }
}

