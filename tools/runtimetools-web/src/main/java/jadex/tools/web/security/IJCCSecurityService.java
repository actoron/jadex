package jadex.tools.web.security;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.tools.web.jcc.IJCCPluginService;
import jadex.tools.web.security.JCCSecurityPluginAgent.SecurityState;

/**
 *  Interface for the jcc security service.
 */
@Service
public interface IJCCSecurityService extends IJCCPluginService
{
	/**
	 *  Get security state.
	 *  @return The security state.
	 */
	public IFuture<SecurityState> getSecurityState();
	
	/**
	 *  Set if the platform secret shall be used.
	 *  @param usesecret The flag.
	 */
	public IFuture<Void> setUseSecret(boolean usesecret);
	
	/**
	 *  Set if the platform secret shall be printed.
	 *  @param printsecret The flag.
	 */
	public IFuture<Void> setPrintSecret(boolean printsecret);
}
