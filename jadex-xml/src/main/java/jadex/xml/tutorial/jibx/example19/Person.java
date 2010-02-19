
package jadex.xml.tutorial.jibx.example19;

public class Person {
    Customer customer;
    private int customerNumber;
    private String firstName;
    private String lastName;
    
    public void preset(Object obj) {
        customer = (Customer)obj;
        System.out.println("Person.preset called");
    }
}
