package trainwreck.zipcode.ex6;

public class ZipCodeExample {

    private Customer customer;

    void prepare() {
        customer = new Customer();
        Label label = new Label();
        int houseNumber = <warning descr="This piece of code violates the Law of Demeter">customer.getAddress().getHouseNumber()</warning>;

        for (int i = 0; i < houseNumber; i++) {
            System.out.println("This number is before the house number: " + i);
            label.addLine("house number", getPrefix());
            if (i == 1) {
                label.addLine("" + houseNumber, getPrefix());
            }
        }
    }

    private String getPrefix() {
        return "Some Prefix";
    }
}

class Customer {
    Address address;
    Address getAddress() {
        return address;
    }
}

class Address {
    ZipCode zip;
    ZipCode getZipCode() {
        return zip;
    }

    public int getHouseNumber() {
        return 10;
    }
}

class ZipCode {
    @Override
    public String toString() {
        return "12345";
    }
}

class Label {
    public void addLine(String line, String prefix) {
        // do something
    }
}