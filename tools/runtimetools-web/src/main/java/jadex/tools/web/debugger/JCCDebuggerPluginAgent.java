package jadex.tools.web.debugger;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.cms.CMSStatusEvent;
import jadex.bridge.service.types.cms.ICMSComponentListener;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.SComponentManagementService;
import jadex.bridge.service.types.factory.SComponentFactory;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.Boolean3;
import jadex.commons.IFilter;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.tools.web.jcc.JCCPluginAgent;

/**
 *  Debugger web jcc plugin.
 */
@ProvidedServices({@ProvidedService(name="debuggerweb", type=IJCCDebuggerService.class)})
@Agent(autostart=Boolean3.TRUE)
public class JCCDebuggerPluginAgent extends JCCPluginAgent implements IJCCDebuggerService
{
	/**
	 *  Get the plugin name.
	 *  @return The plugin name.
	 */
	public IFuture<String> getPluginName()
	{
		return new Future<String>("Debugger");
	}
	
	/**
	 *  Get the plugin priority.
	 *  @return The plugin priority.
	 */
	public IFuture<Integer> getPriority()
	{
		return new Future<Integer>(70);
	}
	
	/**
	 *  Get the plugin UI path.
	 *  @return The plugin ui path.
	 */
	public String getPluginUIPath()
	{
		return "jadex/tools/web/debugger/debugger.js";
	}
	
	/**
	 *  Get the plugin icon.
	 *  @return The plugin icon.
	 */
	public IFuture<byte[]> getPluginIcon()
	{
		return loadResource("jadex/tools/web/debugger/images/debugger.png");		
	}
	
	/**
	 *  Suspend component.
	 */
	public IFuture<IComponentDescription> suspendComponent(IComponentIdentifier compo)
	{
		Future<IComponentDescription> ret = new Future<IComponentDescription>();
		agent.getExternalAccess(compo).suspendComponent().then(Void ->
		{
			agent.getDescription(compo).delegate(ret);
		}).catchEx(ret);
		return ret;
	}
	
	/**
	 *  Step component.
	 */
	public IFuture<IComponentDescription> stepComponent(IComponentIdentifier compo, String step)
	{
		//System.out.println("dostep: "+compo+" "+step);
		Future<IComponentDescription> ret = new Future<IComponentDescription>();
		agent.getExternalAccess(compo).stepComponent(step).then(Void ->
		{
			agent.getDescription(compo).delegate(ret);
		}).catchEx(ret);
		return ret;
	}
	
	/**
	 *  Resume component.
	 */
	public IFuture<IComponentDescription> resumeComponent(IComponentIdentifier compo)
	{
		System.out.println("resume called: "+compo);
		Future<IComponentDescription> ret = new Future<IComponentDescription>();
		agent.getExternalAccess(compo).resumeComponent().then(Void ->
		{
			agent.getDescription(compo).delegate(ret);
		}).catchEx(ret);
		return ret;
	}
	
	/**
	 *  Get the web component fragment for a plugin.
	 *  @param compo The component cid.
	 *  @param cid The platform cid.
	 *  @return The web debugger panel.
	 */
	public IFuture<byte[]> getDebuggerFragment(IComponentIdentifier compo)
	{
		Future<byte[]> ret = new Future<>();
		
		agent.getDescription(compo).then(desc ->
		{
			SComponentFactory.getProperty(agent.getExternalAccess(compo), desc.getType(), "debugger.panel_web")
			.then(filename ->
			{
				loadResource((String)filename).delegate(ret);
			});
		}).catchEx(ret);
		
		return ret;
	}
	
	/**
	 *  Get the component description
	 *  @param compo The component cid.
	 *  @return The component description.
	 */
	public IFuture<IComponentDescription> getComponentDescription(IComponentIdentifier compo)
	{
		return agent.getDescription(compo);
	}
	
	/**
	 *  Get the breakpoints. 
	 *  @param compo The component cid.
	 *  @return The breakpoints.
	 */
	public IFuture<String[]> getBreakpoints(IComponentIdentifier compo)
	{
		Future<String[]> ret = new Future<>();
		agent.getExternalAccess(compo).getModelAsync().then(model ->
		{
			String[] bps = model.getBreakpoints();
			ret.setResult(bps);
		})
		.catchEx(ret);
		return ret;
	}
	
	/**
	 *  Set the breakpoints. 
	 *  @param compo The component cid.
	 *  @param breakpoints The platform breakpoints.
	 */
	public IFuture<Void> setComponentBreakpoints(IComponentIdentifier compo, String[] breakpoints)
	{
		return agent.getExternalAccess(compo).setComponentBreakpoints(breakpoints);
	}
	
	
	/**
	 *  Subscribe to a component for update events.
	 *  @param compo The component cid.
	 */
	public ISubscriptionIntermediateFuture<IMonitoringEvent> subscribeToComponent(IComponentIdentifier compo)
	{
		return agent.getExternalAccess(compo).subscribeToEvents(new IFilter<IMonitoringEvent>()
		{
			public boolean filter(IMonitoringEvent ev)
			{
				return ev.getType().endsWith("step");//MicroAgentInterpreter.TYPE_STEP);	
			}
		}, true, PublishEventLevel.FINE);
	}
	
	
	/**
	 *  Subscribe to cms to get execution updates.
	 *  @param compo The component cid.
	 */
	public ISubscriptionIntermediateFuture<CMSStatusEvent> subscribeToCMS(IComponentIdentifier compo)
	{
		return SComponentManagementService.listenToAll(agent.getExternalAccess(compo));
	}
	
	
	/**
	 *  Get the sservice of the own platform or of cid platform.
	 *  @param cid The platform id.
	 *  @return 
	 * /
	protected IFuture<IService> getService(IComponentIdentifier cid)
	{
		if(cid==null || cid.hasSameRoot(getAgent().getId()))
		{
			return agent.getService(ISecurityService.class);
		}
		else
		{
			return agent.searchService(new ServiceQuery<ISecurityService>(ISecurityService.class).setPlatform(cid).setScope(ServiceScope.PLATFORM));
		}
	}*/
	
}
