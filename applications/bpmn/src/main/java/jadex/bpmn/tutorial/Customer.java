package jadex.bpmn.tutorial;

/**
 *  A simple business object representing a customer
 *  of an insurance company.
 */
public class Customer
{
	//-------- attributes --------
	
	/** The name of the customer. */
	protected String	name;
	
	/** The gender of the customer ('male' or 'female'). */
	protected String	gender;
	
	/** The age of the customer. */
	protected int	age;
	
	/** The marital state of the customer. */
	protected boolean	married;
	
	//-------- constructors --------
	
	/**
	 *  Create a new customer with initial values. 
	 */
	public Customer(String name, String gender, int age, boolean married)
	{
		this.name	= name;
		this.gender	= gender;
		this.age	= age;
		this.married	= married;
	}
	
	//-------- methods --------
	
	/**
	 *  Check if the customer is risk adverse
	 *  or willing to take risks.
	 */
	public boolean	isRiskTaking()
	{
		// Business rule: single males of age below 40 are assumed to be risk taking.
		return gender.equals("male") && age < 40 && !married;
	}
	
	/**
	 *  Get a text representation of the customer.
	 */
	public String	toString()
	{
		return "Customer("+name+")";
	}
}
