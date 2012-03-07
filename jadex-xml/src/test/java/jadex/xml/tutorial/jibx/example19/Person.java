
package jadex.xml.tutorial.jibx.example19;

public class Person {
    Customer customer;
    @SuppressWarnings("unused")
	private int customerNumber;
    @SuppressWarnings("unused")
	private String firstName;
    @SuppressWarnings("unused")
	private String lastName;
    
    public void preset(Object obj) {
        customer = (Customer)obj;
        System.out.println("Person.preset called");
    }
}
