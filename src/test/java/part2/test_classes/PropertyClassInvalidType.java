package part2.test_classes;

import part2.Property;

import java.time.Instant;

public class PropertyClassInvalidType {
    private boolean stringProperty;
    @Property(name = "numberProperty")
    private int myNumber;

    @Property(format = "dd.MM.yyyy HH:mm")
    private Instant timeProperty;

    public void setStringProperty(boolean stringProperty) {
        this.stringProperty = stringProperty;
    }

    public void setMyNumber(int myNumber) {
        this.myNumber = myNumber;
    }

    public void setTimeProperty(Instant timeProperty) {
        this.timeProperty = timeProperty;
    }
}


