
package jadex.xml.tutorial.jibx.example06;

public class Customer {
    public int customerNumber;
    public String firstName;
    public String lastName;
    public Address address;
    public String phone;
	
    public String toString()
	{
		return "Customer(address=" + this.address + ", customerNumber="
			+ this.customerNumber + ", firstName=" + this.firstName
			+ ", lastName=" + this.lastName + ", phone=" + this.phone + ")";
	}
}
