package jadex.rules.state.io.xml;

import jadex.rules.state.IOAVState;

/**
 *  User context for storing information required
 *  during XML read.
 */
public class OAVUserContext
{
	//-------- attributes --------
	
	/** The OAV state. */
	protected IOAVState	state;
	
	/** The custom context. */
	protected Object	custom;
	
	//-------- constructors --------
	
	/**
	 *  Create an OAV user context.
	 *  @param	state	The state.
	 *  @param custom	An optional custom context object.
	 */
	public OAVUserContext(IOAVState state, Object custom)
	{
		this.state	= state;
		this.custom	= custom;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the state.
	 *  @return The state.
	 */
	public IOAVState getState()
	{
		return state;
	}

	/**
	 *  Set the state.
	 *  @param state	The state to set.
	 */
	public void setState(IOAVState state)
	{
		this.state = state;
	}

	/**
	 *  Get the custom context object.
	 *  @return The custom context object.
	 */
	public Object getCustom()
	{
		return custom;
	}

	/**
	 *  Set the custom context object.
	 *  @param custom	The custom context object to set.
	 */
	public void setCustom(Object custom)
	{
		this.custom = custom;
	}
}
