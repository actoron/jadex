package jadex.bdi.planlib.messaging;

/** 
 *  Account for icq.
 */
public class IMAccount
{
	//-------- attributes --------
	
	/** The icq id. */
	String	id;

	/** The password. */
	String	password;

	//-------- constructors --------
	
	/** 
	 * Constructor for IMAccount.
	 */
	public IMAccount()
	{
		/* default */
	}

	/** 
	 * Constructor for IMAccount.
	 * @param id
	 * @param password
	 */
	public IMAccount(String id, String password)
	{
		this.id = id;
		this.password = password;
	}

	//-------- methods --------
	
	/** 
	 *  Getter for id
	 *  @return Returns id.
	 */
	public String getId()
	{
		return this.id;
	}

	/** 
	 *  Setter for id.
	 *  @param id The id to set.
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/** 
	 *  Getter for password.
	 *  @return Returns password.
	 */
	public String getPassword()
	{
		return this.password;
	}

	/** 
	 *  Setter for password.
	 *  @param password The password value to set.
	 */
	public void setPassword(String password)
	{
		this.password = password;
	}
}