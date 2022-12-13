package part2.test_classes;

import part2.Property;

import java.time.Instant;
import java.util.Objects;

public class PropertyClassValid {
    private String stringProperty;
    @Property(name = "numberProperty")
    private int myNumber;
    @Property(format = "dd.MM.yyyy HH:mm")
    private Instant timeProperty;

    public String getStringProperty() {
        return stringProperty;
    }

    public void setStringProperty(String stringProperty) {
        this.stringProperty = stringProperty;
    }

    public int getMyNumber() {
        return myNumber;
    }

    public void setMyNumber(int myNumber) {
        this.myNumber = myNumber;
    }

    public Instant getTimeProperty() {
        return timeProperty;
    }

    public void setTimeProperty(Instant timeProperty) {
        this.timeProperty = timeProperty;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PropertyClassValid propertyAnnotatedClass = (PropertyClassValid) o;
        return getMyNumber() == propertyAnnotatedClass.getMyNumber()
                && Objects.equals(getStringProperty(), propertyAnnotatedClass.getStringProperty())
                && Objects.equals(getTimeProperty(), propertyAnnotatedClass.getTimeProperty());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getStringProperty(), getMyNumber(), getTimeProperty());
    }

    @Override
    public String toString() {
        return "PropertyClassValid{" +
                "stringProperty='" + stringProperty + '\'' +
                ", myNumber=" + myNumber +
                ", timeProperty=" + timeProperty +
                '}';
    }
}
