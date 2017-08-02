package jadex.bridge.component.impl.remotecommands;

import java.util.Map;

import jadex.bridge.component.IMsgHeader;
import jadex.bridge.service.types.security.IMsgSecurityInfos;

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
	
	//-------- methods --------

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
	
	/**
	 *  Check if it is ok to execute the command.
	 *  Override for specific checks.
	 */
	public boolean checkSecurity(IMsgSecurityInfos secinfos, IMsgHeader header)
	{
		return true;
	}
}
