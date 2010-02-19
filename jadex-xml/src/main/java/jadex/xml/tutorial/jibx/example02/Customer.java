package jadex.xml.tutorial.jibx.example02;

/**
 *  Customer example class.
 */
public class Customer
{
	//-------- attributes --------
	
	/** The person subobject. */
	public Person person;
	
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
		return "Customer(city="+city+", person="+person+", phone="
			+ phone+", state="+state+", street="+street+", zip="+zip+")";
	} 
}
