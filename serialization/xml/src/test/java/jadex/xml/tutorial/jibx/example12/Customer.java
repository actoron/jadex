
package jadex.xml.tutorial.jibx.example12;

public class Customer {
    public int customernumber;
    public String firstname;
    public String lastname;
    public Address address;
    public String phone;
	
    public String toString()
	{
		return "Customer(address=" + this.address + ", customerNumber="
			+ this.customernumber + ", firstName=" + this.firstname
			+ ", lastName=" + this.lastname + ", phone=" + this.phone + ")";
	}
}
