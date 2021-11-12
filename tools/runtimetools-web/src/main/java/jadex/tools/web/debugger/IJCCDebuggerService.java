package jadex.tools.web.debugger;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.cms.CMSStatusEvent;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.monitoring.IMonitoringEvent;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.tools.web.jcc.IJCCPluginService;

/**
 *  Interface for the debugger plugin service.
 *  
 *  Note: cid needs to be always last parameter. It is used to remote 
 *  control another platform using a webjcc plugin on the gateway.
 */
@Service(system=true)
public interface IJCCDebuggerService extends IJCCPluginService
{
	/**
	 *  Suspend component.
	 */
	public IFuture<IComponentDescription> suspendComponent(IComponentIdentifier compo);
	
	/**
	 *  Step component.
	 */
	public IFuture<IComponentDescription> stepComponent(IComponentIdentifier compo, String step);
	
	/**
	 *  Resume component.
	 */
	public IFuture<IComponentDescription> resumeComponent(IComponentIdentifier compo);
	
	/**
	 *  Get the web component fragment for a plugin.
	 *  @param compo The component cid.
	 *  @param cid The platform cid.
	 *  @return The web debugger panel.
	 */
	public IFuture<byte[]> getDebuggerFragment(IComponentIdentifier compo);
	
	/**
	 *  Get the component description
	 *  @param compo The component cid.
	 *  @return The component description.
	 */
	public IFuture<IComponentDescription> getComponentDescription(IComponentIdentifier compo);
	
	/**
	 *  Get the breakpoints. 
	 *  @param compo The component cid.
	 *  @param cid The platform cid.
	 *  @return The breakpoints.
	 */
	public IFuture<String[]> getBreakpoints(IComponentIdentifier compo);
	
	/**
	 *  Set the breakpoints. 
	 *  @param compo The component cid.
	 *  @param breakpoints The platform breakpoints.
	 */
	public IFuture<Void> setComponentBreakpoints(IComponentIdentifier compo, String[] breakpoints);
	
	/**
	 *  Subscribe to cms to get execution updates.
	 *  @param compo The component cid.
	 */
	public ISubscriptionIntermediateFuture<CMSStatusEvent> subscribeToCMS(IComponentIdentifier compo);
	
	/**
	 *  Subscribe to a component for monitoring events.
	 *  @param compo The component cid.
	 */
	public ISubscriptionIntermediateFuture<IMonitoringEvent> subscribeToComponent(IComponentIdentifier compo);
}
