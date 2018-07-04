package jadex.bridge.component.impl.remotecommands;

import java.util.Map;

/**
 *  Base class for Jadex built-in remote commands.
 *  Handles exchange of non-functional properties.
 */
public abstract class AbstractInternalRemoteCommand
{
	//-------- attributes ---------
	
	/** The non-functional properties. */
	private Map<String, Object>	nonfunc;
	
	//-------- constructors --------
	
	/**
	 *  Create a remote command.
	 */
	public AbstractInternalRemoteCommand()
	{
		// Bean constructor.
	}
	
	/**
	 *  Create a remote command.
	 */
	public AbstractInternalRemoteCommand(Map<String, Object> nonfunc)
	{
		this.nonfunc	= nonfunc;
	}
	
	//-------- bean property methods --------

	/**
	 *  Get the non-functional properties.
	 */
	public Map<String, Object>	getProperties()
	{
		return nonfunc;
	}
	
	/**
	 *  Set the non-functional properties.
	 */
	public void	setProperties(Map<String, Object> nonfunc)
	{
		this.nonfunc	= nonfunc;
	}	
}
