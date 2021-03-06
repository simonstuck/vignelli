package trainwreck.builder.ex2;

public class BuilderExample {
    public void execute() {
        Banana.Builder builder = new Banana.Builder();
        builder.withLength(10).withOrigin("Bahamas").withRipeness(59).build().ripen();
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

        public Builder
        withOrigin(String origin) {
            this.origin = origin;
            return this;
        }

        public Banana
        build() {
            return new Banana(length,ripeness,origin);
        }
    }
}
