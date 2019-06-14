package jadex.tools.web.cloudview;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import jadex.bridge.ClassInfo;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.component.RemoteMethodInvocationHandler;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.Boolean3;
import jadex.commons.future.Future;
import jadex.commons.future.FutureBarrier;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.tools.web.jcc.IJCCWebService;
import jadex.tools.web.jcc.JCCPluginAgent;

@ProvidedServices({@ProvidedService(name="cloudviewweb", type=IJCCCloudviewService.class)})
@Agent(autostart=Boolean3.TRUE)
public class JCCCloudviewPluginAgent extends JCCPluginAgent implements IJCCCloudviewService
{
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  Get the networks of platforms.
	 *  @return The networks.
	 */
	public IFuture<Map<String, String[]>> getPlatformNetworks(String cid)
	{
		final Future<Map<String, String[]>> ret = new Future<>();
		IJCCWebService ws = agent.searchLocalService(new ServiceQuery<>(IJCCWebService.class));
		ws.getPlatforms().addResultListener(new IResultListener<Collection<IComponentIdentifier>>()
		{
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
				ret.setResult(new HashMap<>());
			}
			public void resultAvailable(Collection<IComponentIdentifier> platforms)
			{
				FutureBarrier<Void> bar = new FutureBarrier<Void>();
				final Map<String, String[]> res = new HashMap<String, String[]>();
				for (final IComponentIdentifier id : platforms)
				{
					final Future<Void> done = new Future<Void>();
					bar.addFuture(done);
					/*agent.searchService(new ServiceQuery<>(ISecurityService.class).setPlatform(id).setScope(ServiceScope.PLATFORM).setSearchStart(id)).addResultListener(new IResultListener<ISecurityService>()
					{
						public void exceptionOccurred(Exception exception)
						{
							exception.printStackTrace();
							done.setResult(null);
						};
						public void resultAvailable(ISecurityService secserv)
						{
							System.out.println("Got service for " + secserv);
							secserv.getNetworkNames().addResultListener(new IResultListener<Set<String>>()
							{
								public void exceptionOccurred(Exception exception)
								{
									exception.printStackTrace();
									done.setResult(null);
								}
								public void resultAvailable(Set<String> nws)
								{
									System.out.println("Got networks for " + id);
									res.put(id.toString(), nws.stream().toArray(String[]::new));
									done.setResult(null);
								};
							});
						};
					});*/
					
					ISecurityService secserv = null;
					if ((new ComponentIdentifier(cid)).getRoot().equals(agent.getId().getRoot()))
					{
						secserv = agent.getLocalService(ISecurityService.class);
					}
					else
					{
						ComponentIdentifier rsec = new ComponentIdentifier("security@" + cid);
						IServiceIdentifier sid = BasicService.createServiceIdentifier(rsec, new ClassInfo(ISecurityService.class), null, null, null, null, null, true);
						secserv = (ISecurityService) RemoteMethodInvocationHandler.createRemoteServiceProxy(agent, sid);
					}
					
					secserv.getNetworkNames().addResultListener(new IResultListener<Set<String>>()
					{
						public void exceptionOccurred(Exception exception)
						{
							exception.printStackTrace();
							done.setResult(null);
						}
						public void resultAvailable(Set<String> nws)
						{
							res.put(id.toString(), nws.stream().toArray(String[]::new));
							done.setResult(null);
						};
					});
					
					/*if (cid != null)
					{
						// Filter Platforms
						Set<String> localnetworks = new HashSet(Arrays.asList(res.get(cid)));
						for (Iterator<Map.Entry<String, String[]>> it = res.entrySet().iterator(); it.hasNext(); )
						{
							Map.Entry<String, String[]> entry = it.next();
							if (Arrays.stream(entry.getValue()).anyMatch(localnetworks::contains))
								continue;
							it.remove();
						}
						
					}*/
				}
				bar.waitFor().thenAccept(d -> ret.setResult(res));
			}
		});
		return ret;
	}
	
	/**
	 *  Returns the plugin name.
	 *  @return Plugin name.
	 */
	public IFuture<String> getPluginName()
	{
		return new Future<>("cloudview");
	}
	
	/**
	 *  Returns priority.
	 *  @return Priority.
	 */
	public IFuture<Integer> getPriority()
	{
		return new Future<Integer>(80);
	}
	
	/**
	 *  Return UI path.
	 *  @return UI path.
	 */
	public String getPluginUIPath()
	{
		return "jadex/tools/web/cloudview/cloudview.tag";
	}
}
