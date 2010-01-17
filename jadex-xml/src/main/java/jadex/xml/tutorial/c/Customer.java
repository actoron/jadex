package jadex.xml.tutorial.c;

/**
 *  Customer example class.
 *  (taken from Jibx website)
 */
public class Customer
{
	//-------- attributes --------
	
	/** The customer number. */
	public int customernumber;
	
	/** The first name. */
    public String firstname;
    
    /** The last name. */
    public String lastname;
	
	/** The street name. */
    public String street;
    
    /** The city name. */
    public String city;
    
    /** The state name. */
    public String state;
    
    /** The zip code. */
    public Integer zip;
    
    /** The phone number. */
    public String phone;

    //-------- methods --------
    
    /**
     *  Get the string representation.
     *  @return The string representation.
     */
	public String toString()
	{
		return "Customer(city=" + city + ", customernumber=" + customernumber
			+ ", firstname=" + firstname + ", lastname=" + lastname
			+ ", phone=" + phone + ", state=" + state + ", street="
			+ street + ", zip=" + zip + ")";
	}   
}
