package trainwreck.zipcode.ex3;

public class ZipCodeExample {

    private Customer customer;

    void prepare() {
        customer = new Customer();
        Label label = new Label();
        customer.fillLabel(label);
    }

}

class Customer {
    Address address;
    Address getAddress() {
        return address;
    }

    void fillLabel(Label label) {
        label.addLine(<warning descr="This piece of code violates the Law of Demeter">getAddress().getZipCode().toString()</warning>);
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
    public void addLine(String line) {
        // do something
    }
}
