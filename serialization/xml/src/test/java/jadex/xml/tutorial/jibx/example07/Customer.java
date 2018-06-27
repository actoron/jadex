
package jadex.xml.tutorial.jibx.example07;

public class Customer {
    public Person person;
    public String street;
    public String city;
    public String state;
    public Integer zip;
    public String phone;

    public String toString()
	{
		return "Customer(city=" + this.city + ", person=" + this.person
			+ ", phone=" + this.phone + ", state=" + this.state
			+ ", street=" + this.street + ", zip=" + this.zip + ")";
	}
    
    
}
