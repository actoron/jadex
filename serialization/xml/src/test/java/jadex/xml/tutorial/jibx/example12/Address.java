
package jadex.xml.tutorial.jibx.example12;

public class Address {
    public String street;
    public String city;
    public String state;
    public Integer zip;

    public String toString()
	{
		return "Address(city=" + this.city + ", state=" + this.state
			+ ", street=" + this.street + ", zip=" + this.zip + ")";
	}
}
