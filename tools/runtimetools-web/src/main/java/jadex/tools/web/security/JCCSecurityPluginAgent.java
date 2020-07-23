package jadex.tools.web.security;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.Boolean3;
import jadex.commons.collection.MultiCollection;
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
@ProvidedServices({@ProvidedService(name="securityweb", type=IJCCSecurityService.class)})
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
	 *  Get the plugin priority.
	 *  @return The plugin priority.
	 */
	public IFuture<Integer> getPriority()
	{
		return new Future<Integer>(90);
	}
	
	/**
	 *  Get the plugin UI path.
	 *  @return The plugin ui path.
	 */
	public String getPluginUIPath()
	{
		return "jadex/tools/web/security/security.tag";
		//return "jadex/tools/web/starter/security.js";
	}
	
	/**
	 *  Get the security service of the own platform or of cid platform.
	 *  @param cid The platform id.
	 *  @return 
	 */
	protected IFuture<ISecurityService> getSecurityService(IComponentIdentifier cid)
	{
		if(cid==null || cid.hasSameRoot(cid))
		{
			return agent.getService(ISecurityService.class);
		}
		else
		{
			return agent.searchService(new ServiceQuery<ISecurityService>(ISecurityService.class).setPlatform(cid).setScope(ServiceScope.PLATFORM));
		}
	}
	
	/**
	 *  Set if the platform secret shall be used.
	 *  @param usesecret The flag.
	 */
	public IFuture<Void> setUseSecret(boolean usesecret, IComponentIdentifier cid)
	{
		return getSecurityService(cid).thenCompose(s -> s.setUsePlatformSecret(usesecret));
	}
	
	/**
	 *  Set if the platform secret shall be printed.
	 *  @param printsecret The flag.
	 */
	public IFuture<Void> setPrintSecret(boolean printsecret, IComponentIdentifier cid)
	{
		return getSecurityService(cid).thenCompose(s -> s.setPrintPlatformSecret(printsecret));
	}
	
	/**
	 *  Adds a role for an entity (platform or network name).
	 *  @param entity The entity name.
	 *  @param role The role name.
	 *  @return Null, when done.
	 */
	public IFuture<Void> addRole(String entity, String role, IComponentIdentifier cid)
	{
		return getSecurityService(cid).thenCompose(s -> s.addRole(entity, role));
	}
	
	/**
	 *  Adds a role of an entity (platform or network name).
	 *  @param entity The entity name.
	 *  @param role The role name.
	 *  @return Null, when done.
	 */
	public IFuture<Void> removeRole(String entity, String role, IComponentIdentifier cid)
	{
		return getSecurityService(cid).thenCompose(s -> s.removeRole(entity, role));
	}
	
	/**
	 *  Adds a new network.
	 *  @param networkname The network name.
	 *  @param secret The secret, null to remove.
	 *  @return Null, when done.
	 */
	public IFuture<Void> addNetwork(String networkname, String secret, IComponentIdentifier cid)
	{
		return getSecurityService(cid).thenCompose(s -> s.setNetwork(networkname, secret));
	}
	
	/**
	 *  Remove a network.
	 *  @param networkname The network name.
	 *  @param secret The secret, null to remove the network completely.
	 *  @return Null, when done.
	 */
	public IFuture<Void> removeNetwork(String networkname, String secret, IComponentIdentifier cid)
	{
		return getSecurityService(cid).thenCompose(s -> s.removeNetwork(networkname, secret));
	}
	
	/**
	 *  Adds a trusted platform name.
	 *  @param name The name.
	 *  @return null, when done.
	 */
	public IFuture<Void> addTrustedPlatformName(String name, IComponentIdentifier cid)
	{
		return getSecurityService(cid).thenCompose(s -> s.addTrustedPlatform(name));
	}
	
	/**
	 *  Removes a trusted platform name.
	 *  @param name The name.
	 *  @return null, when done.
	 */
	public IFuture<Void> removeTrustedPlatformName(String name, IComponentIdentifier cid)
	{
		return getSecurityService(cid).thenCompose(s -> s.removeTrustedPlatform(name));
	}
	
	/**
	 *  Get security state.
	 *  @return The security state.
	 */
	public IFuture<SecurityState> getSecurityState(IComponentIdentifier cid)
	{
		Future<SecurityState> ret = new Future<>();
		
		final SecurityState ss = new SecurityState();
		
		FutureBarrier<Void> bar = new FutureBarrier<>();

		// alternative code with less indentition but repeatedly fetches service
		
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
		
		getSecurityService(cid)
			.thenCompose((ISecurityService s) -> 
			{
				bar.addFuture(s.getPlatformSecret(agent.getId())
					.thenAccept((String sec) -> ss.setPlatformSecret(sec)));
				bar.addFuture(s.isPrintPlatformSecret()
					.thenAccept((Boolean b) -> ss.setPrintSecret(b)));
				bar.addFuture(s.isUsePlatformSecret()
					.thenAccept((Boolean b) -> ss.setUseSecret(b)));
				bar.addFuture(s.getAllKnownNetworks()
					.thenAccept((MultiCollection<String, String> ns) ->
					{
//						List<Tuple2<String, String>> nets = ns.entrySet().stream().flatMap(mi-> 
//							mi.getValue().stream().map(v->new Tuple2<String, String>(mi.getKey(), v))).collect(Collectors.toList());
						List<String[]> nets = ns.entrySet().stream().flatMap(mi-> 
							mi.getValue().stream().map(v->new String[]{mi.getKey(), v})).collect(Collectors.toList());
						ss.setNetworks(nets);
					}));
				
				bar.addFuture(s.getRoleMap()
					.thenAccept((Map<String, Set<String>> rs) ->
					{
						List<String[]> nets = rs.entrySet().stream().flatMap(ri-> 
							ri.getValue().stream().map(v->new String[]{ri.getKey(), v})).collect(Collectors.toList());
						ss.setRoles(nets);
					}));
				
				bar.addFuture(s.getNameAuthoritiesInfo()
					.thenAccept((String[][] as) -> {ss.setNameAuthorities(Arrays.asList(as));}));
				
				bar.addFuture(s.getTrustedPlatforms()
					.thenAccept((Set<String> as) -> {ss.setTrustedPlatformNames(as);}));
					
				return bar.waitFor();
			}).thenAccept((Void)->ret.setResult(ss));
		
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
//		protected List<Tuple2<String, String>> networks;
		protected List<String[]> networks;
		protected List<String[]> roles;
		protected List<String[]> nameauthorities;
		protected Set<String> trustedplatformnames;
		
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
		 *  Get the networks.
		 *  @return The networks
		 * /
		public List<Tuple2<String, String>> getNetworks()
		{
			return networks;
		}*/

		/**
		 *  Set the networks.
		 *  @param networks The networks to set
		 * /
		public void setNetworks(List<Tuple2<String, String>> networks)
		{
			this.networks = networks;
		}*/

		/**
		 *  Get the networks.
		 *  @return The networks
		 */
		public List<String[]> getNetworks()
		{
			return networks;
		}

		/**
		 *  Set the networks.
		 *  @param networks The networks to set
		 */
		public void setNetworks(List<String[]> networks)
		{
			this.networks = networks;
		}

		/**
		 *  Set the platformsecret.
		 *  @param platformsecret The platformsecret to set
		 */
		public void setPlatformSecret(String platformsecret)
		{
			this.platformsecret = platformsecret;
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
		 *  Get the roles.
		 *  @return The roles
		 */
		public List<String[]> getRoles()
		{
			return roles;
		}

		/**
		 *  Set the roles.
		 *  @param roles The roles to set
		 */
		public void setRoles(List<String[]> roles)
		{
			this.roles = roles;
		}

		/**
		 *  Get the nameAuthorities.
		 *  @return The nameAuthorities
		 */
		public List<String[]> getNameAuthorities()
		{
			return nameauthorities;
		}

		/**
		 *  Set the nameAuthorities.
		 *  @param nameAuthorities The nameAuthorities to set
		 */
		public void setNameAuthorities(List<String[]> nameAuthorities)
		{
			this.nameauthorities = nameAuthorities;
		}

		/**
		 * @return the trustedplatformnames
		 */
		public Set<String> getTrustedPlatformNames()
		{
			return trustedplatformnames;
		}

		/**
		 * @param trustedplatformnames the trustedplatformnames to set
		 */
		public void setTrustedPlatformNames(Set<String> trustedplatformnames)
		{
			this.trustedplatformnames = trustedplatformnames;
		}
	}
}
