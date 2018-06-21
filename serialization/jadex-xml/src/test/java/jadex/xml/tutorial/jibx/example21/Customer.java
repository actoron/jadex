
package jadex.xml.tutorial.jibx.example21;

public class Customer {
    public Name name;
    public String street;
    public String city;
    public String state;
    public Integer zip;
	
    public String toString()
	{
		return "Customer ["
			+ (this.city != null ? "city=" + this.city + ", " : "")
			+ (this.name != null ? "name=" + this.name + ", " : "")
			+ (this.state != null ? "state=" + this.state + ", " : "")
			+ (this.street != null ? "street=" + this.street + ", " : "")
			+ (this.zip != null ? "zip=" + this.zip : "") + "]";
	}
    
}
