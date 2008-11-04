package jadex.bdi.planlib.watchdog;

/**
 *  Contact information data bean.
 */
public class ContactData
{
	//-------- attributes --------

	/** The name. */
	protected String name;

	/** The email address. */
	protected String email;

	/** The mobile phone no. */
	protected String phone;

	/** The icq no. */
	protected String icq;
	
	//-------- constructors --------

	/** 
	 * Constructor for ContactData.
	 */
	public ContactData() 
	{
		/* default */
	}
	
	/**
	 *  Create a new contact.
	 * @param name 
	 *  @param email The email.
	 *  @param phone The phone.
	 * @param icq 
	 */
	public ContactData(String name, String email, String phone, String icq)
	{
		this.name = name;
		this.email = email;
		this.phone = phone;
		this.icq = icq;
	}

	//-------- methods --------

	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Set the name.
	 *  @param name The name.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the email.
	 *  @return The email.
	 */
	public String getEmail()
	{
		return email;
	}

	/**
	 *  Set the email.
	 *  @param email The email.
	 */
	public void setEmail(String email)
	{
		this.email = email;
	}

	/**
	 *  Get the phone no.
	 *  @return The phone no.
	 */
	public String getPhone()
	{
		return phone;
	}

	/**
	 *  Set the phone no.
	 *  @param phone The phone no.
	 */
	public void setPhone(String phone)
	{
		this.phone = phone;
	}
	
	/** Getter for icq
	 * @return Returns icq.
	 */
	public String getIcq()
	{
		return this.icq;
	}


	/** Setter for icq.
	 * @param icq The ContactData.java value to set
	 */
	public void setIcq(String icq)
	{
		this.icq = icq;
	}


	/**
	 * Returns a string representation of the object. In general, the
	 * <code>toString</code> method returns a string that
	 * "textually represents" this object. The result should
	 * be a concise but informative representation that is easy for a
	 * person to read.
	 * It is recommended that all subclasses override this method.
	 * <p>
	 * The <code>toString</code> method for class <code>Object</code>
	 * returns a string consisting of the name of the class of which the
	 * object is an instance, the at-sign character `<code>@</code>', and
	 * the unsigned hexadecimal representation of the hash code of the
	 * object. In other words, this method returns a string equal to the
	 * value of:
	 * <blockquote>
	 * <pre>
	 * getClass().getName() + '@' + Integer.toHexString(hashCode())
	 * </pre></blockquote>
	 *
	 * @return a string representation of the object.
	 */
	public String toString()
	{
		StringBuffer sb = new StringBuffer();
		sb.append(getClass()+"(");
		sb.append(name);
		sb.append(", ");
		sb.append(email);
		sb.append(", ");
		sb.append(phone);
		sb.append(")");
		return sb.toString();
	}
}
