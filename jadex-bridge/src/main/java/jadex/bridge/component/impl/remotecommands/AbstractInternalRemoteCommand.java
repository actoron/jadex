package jadex.bridge.component.impl.remotecommands;

import java.util.Map;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IMsgHeader;
import jadex.bridge.service.annotation.Security;
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
	
	//-------- security methods --------
	
	/**
	 *  Method to provide the required security level.
	 *  Overridden by subclasses.
	 */
	protected String getSecurityLevel(IInternalAccess access)
	{
		return Security.DEFAULT;
	}
	
	/**
	 *  Check if it is ok to execute the command.
	 *  Override for specific checks.
	 */
	public boolean checkSecurity(IInternalAccess access, IMsgSecurityInfos secinfos, IMsgHeader header)
	{
		String	seclevel	= getSecurityLevel(access);
		return Security.UNRESTRICTED.equals(seclevel) || secinfos.isAuthenticated();
	}
}
