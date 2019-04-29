package jadex.tools.web.security;

import java.util.Set;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.Boolean3;
import jadex.commons.future.Future;
import jadex.commons.future.FutureBarrier;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.tools.web.jcc.JCCPluginAgent;

/**
 *  Security web jcc plugin.
 */
@ProvidedServices({@ProvidedService(name="starterweb", type=IJCCSecurityService.class)})
@Agent(autostart=Boolean3.TRUE)
public class JCCSecurityPluginAgent extends JCCPluginAgent implements IJCCSecurityService
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The plugin component string. */
	protected String component;
	
	
	/**
	 *  Get the plugin name.
	 *  @return The plugin name.
	 */
	public IFuture<String> getPluginName()
	{
		return new Future<String>("security");
	}
	
	/**
	 *  Get the plugin UI path.
	 *  @return The plugin ui path.
	 */
	public String getPluginUIPath()
	{
		return "jadex/tools/web/security/security.tag";
	}
	
	/**
	 *  Set if the platform secret shall be used.
	 *  @param usesecret The flag.
	 */
	public IFuture<Void> setUseSecret(boolean usesecret)
	{
		return agent.getService(ISecurityService.class)
			.thenAccept(s -> s.setUsePlatformSecret(usesecret));
	}
	
	/**
	 *  Set if the platform secret shall be printed.
	 *  @param printsecret The flag.
	 */
	public IFuture<Void> setPrintSecret(boolean printsecret)
	{
		return agent.getService(ISecurityService.class)
			.thenAccept(s -> s.setPrintPlatformSecret(printsecret));
	}
	
	/**
	 *  Get security state.
	 *  @return The security state.
	 */
	public IFuture<SecurityState> getSecurityState()
	{
		Future<SecurityState> ret = new Future<>();
		
		final SecurityState ss = new SecurityState();
		
		FutureBarrier<Void> bar = new FutureBarrier<>();

//		bar.addFuture(agent.getService(ISecurityService.class)
//			.then((ISecurityService s) -> s.getPlatformSecret(agent.getId()))
//			.thenAccept((String s) -> ss.setPlatformSecret(s)));
//
//		bar.addFuture(agent.getService(ISecurityService.class)
//			.then((ISecurityService s) -> s.isPrintPlatformSecret())
//			.thenAccept((Boolean b) -> ss.setPrintSecret(b)));
//
//		bar.addFuture(agent.getService(ISecurityService.class)
//			.then((ISecurityService s) -> s.isUsePlatformSecret())
//			.thenAccept((Boolean b) -> ss.setUseSecret(b)));
//
//		bar.addFuture(agent.getService(ISecurityService.class)
//			.then((ISecurityService s) -> s.getNetworkNames())
//			.thenAccept((Set<String> n) -> ss.setNetworkNames(n.toArray(new String[0]))));
//		bar.waitFor().thenAccept((Void)->ret.setResult(null));
		
		agent.getService(ISecurityService.class)
			.then((ISecurityService s) -> 
			{
				bar.addFuture(s.getPlatformSecret(agent.getId())
					.thenAccept((String sec) -> ss.setPlatformSecret(sec)));
				bar.addFuture(s.isPrintPlatformSecret()
					.thenAccept((Boolean b) -> ss.setPrintSecret(b)));
				bar.addFuture(s.isUsePlatformSecret()
					.thenAccept((Boolean b) -> ss.setUseSecret(b)));
				bar.addFuture(s.getNetworkNames()
					.thenAccept((Set<String> n) -> ss.setNetworkNames(n.toArray(new String[0]))));
					
				return bar.waitFor();
			}).thenAccept((Void)->ret.setResult(null));
		
		return ret;
	}

	/**
	 *  Security state infos.
	 */
	public static class SecurityState
	{
		protected String platformsecret;
		protected boolean usesecret;
		protected boolean printsecret;
		protected String[] networknames;
		protected Object[] networksecrets;
		
		/**
		 *  Create a new security state.
		 */
		public SecurityState()
		{
		}
		
		/**
		 *  Get the usesecret.
		 *  @return The usesecret
		 */
		public boolean isUseSecret()
		{
			return usesecret;
		}
		
		/**
		 *  Set the usesecret.
		 *  @param usesecret The usesecret to set
		 */
		public SecurityState setUseSecret(boolean usesecret)
		{
			this.usesecret = usesecret;
			return this;
		}
		
		/**
		 *  Get the printsecret.
		 *  @return The printsecret
		 */
		public boolean isPrintSecret()
		{
			return printsecret;
		}
		
		/**
		 *  Set the printsecret.
		 *  @param printsecret The printsecret to set
		 */
		public SecurityState setPrintSecret(boolean printsecret)
		{
			this.printsecret = printsecret;
			return this;
		}
		
		/**
		 *  Get the networknames.
		 *  @return The networknames
		 */
		public String[] getNetworkNames()
		{
			return networknames;
		}
		
		/**
		 *  Set the networknames.
		 *  @param networknames The networknames to set
		 */
		public SecurityState setNetworkNames(String[] networknames)
		{
			this.networknames = networknames;
			return this;
		}
		
		/**
		 *  Get the networksecrets.
		 *  @return The networksecrets
		 */
		public Object[] getNetworkSecrets()
		{
			return networksecrets;
		}
		
		/**
		 *  Set the networksecrets.
		 *  @param networksecrets The networksecrets to set
		 */
		public SecurityState setNetworkSecrets(Object[] networksecrets)
		{
			this.networksecrets = networksecrets;
			return this;
		}

		/**
		 *  Get the platformsecret.
		 *  @return The platformsecret
		 */
		public String getPlatformSecret()
		{
			return platformsecret;
		}

		/**
		 *  Set the platformsecret.
		 *  @param platformsecret The platformsecret to set
		 */
		public void setPlatformSecret(String platformsecret)
		{
			this.platformsecret = platformsecret;
		}
	}
}
