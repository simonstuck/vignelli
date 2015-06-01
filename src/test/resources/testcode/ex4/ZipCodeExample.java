package trainwreck.zipcode.ex4;

public class ZipCodeExample {

    private Customer customer;

    void prepare() {
        customer = new Customer();
        ZipCode zip = <warning descr="This piece of code violates the Law of Demeter">customer.getAddress().getZipCode()</warning>;
        Label label = new Label();
        label.addLine(zip.toString(), getPrefix());

        <warning descr="This piece of code violates the Law of Demeter">customer.getAddress().getZipCode()</warning>;
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
