package trainwreck.builder.ex3;

public class BuilderExample {
    public void execute() {
        MadStatistician madStatistician = new MadStatistician();
        Banana.Builder builder = new Banana.Builder();
        madStatistician.recordDataPoint(
                <warning descr="This piece of code violates the Law of Demeter">builder.withLength(10).withOrigin("Bahamas").withRipeness(59).build()
                        .getPeelBuilder().withNumberOfSpots(10).withYellowness(29).build().getYellowness()</warning>
        );
    }
}

class Banana {
    private final int length;
    private final String origin;
    private int ripeness;

    Banana(int length, int ripeness, String origin) {
        this.length = length;
        this.ripeness = ripeness;
        this.origin = origin;
    }

    public int getRipeness() {
        return ripeness;
    }

    public void ripen() {
        ripeness++;
    }

    public Peel.Builder getPeelBuilder() {
        return new Peel.Builder();
    }

    public static class Builder {
        private int length;
        private int ripeness;
        private String origin;

        public Builder withLength(int length) {
            this.length = length;
            return this;
        }

        public Builder withRipeness(int ripeness) {
            this.ripeness = ripeness;
            return this;
        }

        public Builder withOrigin(String origin) {
            this.origin = origin;
            return this;
        }

        public Banana build() {
            return new Banana(length,ripeness,origin);
        }
    }
}

class Peel {
    private final int yellowness;
    private final int numberOfSpots;

    private Peel(int yellowness, int numberOfSpots) {
        this.yellowness = yellowness;
        this.numberOfSpots = numberOfSpots;
    }

    public int getYellowness() {
        return yellowness;
    }

    public static class Builder {
        private int yellowness;
        private int numberOfSpots;

        public Builder withYellowness(int yellowness) {
            this.yellowness = yellowness;
            return this;
        }

        public Builder withNumberOfSpots(int numberOfSpots) {
            this.numberOfSpots = numberOfSpots;
            return this;
        }

        public Peel build() {
            return new Peel(yellowness, numberOfSpots);
        }
    }
}
class MadStatistician {
    public void recordDataPoint(int yellowness) {

    }
}