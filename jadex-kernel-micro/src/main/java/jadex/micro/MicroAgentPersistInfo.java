package jadex.micro;

import jadex.kernelbase.DefaultPersistInfo;

public class MicroAgentPersistInfo extends DefaultPersistInfo
{
	/** The agent object implemented by the user. */
	protected Object useragentobject;
	
	/**
	 *  Empty constructor for bean compatibility.
	 */
	public MicroAgentPersistInfo()
	{
		super();
	}
	
	/**
	 *  Creates the state info object.
	 */
	public MicroAgentPersistInfo(MicroAgentInterpreter interpreter)
	{
		super(interpreter);
		
		if (!(interpreter.getAgent() instanceof IPojoMicroAgent))
		{
			throw new UnsupportedOperationException("Persisting non-POJO micro agent is currently unsupported: " + interpreter.getName());
		}
		
		useragentobject = ((IPojoMicroAgent) interpreter.getAgent()).getPojoAgent();
	}

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
