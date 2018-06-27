package jadex.micro;

/**
 *  Custom persistent data for micro agents.
 */
public class MicroAgentPersistInfo
{
	//-------- attributes --------
	
	/** The pojo agent object provided by the user. */
	protected Object useragentobject;
	
	//-------- constructors --------
	
	/**
	 *  Empty constructor for bean compatibility.
	 */
	public MicroAgentPersistInfo()
	{
	}
	
//	/**
//	 *  Creates the state info object.
//	 */
//	public MicroAgentPersistInfo(IPojoMicroAgent agent)
//	{
//		setUserAgentObject(agent.getPojoAgent());
//	}

	/**
	 *  Gets the user agent object.
	 *
	 *  @return The user agent object.
	 */
	public Object getUserAgentObject()
	{
		return useragentobject;
	}

	/**
	 *  Sets the user agent object.
	 *
	 *  @param useragentobject The user agent object to set.
	 */
	public void setUserAgentObject(Object useragentobject)
	{
		this.useragentobject = useragentobject;
	}
}
