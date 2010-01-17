package jadex.xml.tutorial.b;

/**
 *  Person example class.
 *  (taken from Jibx website)
 */
public class Person
{
	//-------- attributes --------
	
	/** The customer number. */
	public int customernumber;
	
	/** The first name. */
    public String firstname;
    
    /** The last name. */
    public String lastname;

    //-------- methods --------

    /**
     *  Get the string representation.
     *  @return The string representation.
     */
	public String toString()
	{
		return "Person(customernumber="+customernumber+", firstname="
				+firstname+", lastname="+lastname+")";
	}
}
