package part1;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonAutoDetect;

@JsonAutoDetect
public class Violation {
    private ViolationType type;
    @JsonAlias("fine_amount")
    private Double fineAmount;

    public void setType(ViolationType type) {
        this.type = type;
    }

    public void setFineAmount(Double fineAmount) {
        this.fineAmount = fineAmount;
    }

    public ViolationType getType() {
        return type;
    }

    public double getFineAmount() {
        return fineAmount;
    }
}