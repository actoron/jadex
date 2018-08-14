package jadex.bridge;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cms.CMSStatusEvent;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.IParameterGuesser;
import jadex.commons.IValueFetcher;
import jadex.commons.Tuple2;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITuple2Future;

/**
 *  Internal access adapter.
 */
public class InternalAccessAdapter implements IInternalAccess//, INonUserAccess
{
	/** The delegate access. */
	protected IInternalAccess access;
	
	/**
	 *  Create a new adapter.
	 */
	public InternalAccessAdapter(IInternalAccess access)
	{
		this.access = access;
	}
	
//	//-------- INonUserAccess interface --------
//	
//	/**
//	 *  Get the exception, if any.
//	 *  
//	 *  @return The failure reason for use during cleanup, if any.
//	 */
//	public Exception	getException()
//	{
//		return ((INonUserAccess)access).getException();
//	}
//	
//	/**
//	 *  Get the shared platform data.
//	 *  
//	 *  @return The objects shared by all components of the same platform (registry etc.). See starter for available data.
//	 */
//	public Map<String, Object>	getPlatformData()
//	{
//		return ((INonUserAccess)access).getPlatformData();
//	}

	//-------- IInternalAccess interface --------
	
	/**
	 *  @deprecated From 3.0. Use getComponentFeature(IArgumentsResultsFeature.class).getArguments()
	 *  Get an argument value per name.
	 *  @param name The argument name.
	 *  @return The argument value.
	 */
	public Object getArgument(String name)
	{
		return access.getArgument(name);
	}
	
	/**
	 *  @deprecated From 3.0. Use internal access.
	 *  @return The interpreter.
	 */
	public IInternalAccess getInterpreter()
	{
		return this;
	}
	
	/**
	 *  @deprecated From version 3.0 - replaced with internal access.
	 *  Get the service provider.
	 *  @return The service provider.
	 */
	public IInternalAccess getServiceContainer()
	{
		return this;
	}
	
	/**
	 *  @deprecated From version 3.0 - replaced with internal access.
	 *  Get the service provider.
	 *  @return The service provider.
	 */
	public IInternalAccess getServiceProvider()
	{
		return this;
	}
	
	/**
	 *  @deprecated From version 3.0 - replaced with internal access.
	 *  Get the internal access.
	 *  @return The internal access.
	 */
	public IInternalAccess getInternalAccess()
	{
		return this;
	}
	
	/**
	 *  @deprecated From version 3.0 - Use getComponentFeature(IRequiredServicesFeatures.class).getService()
	 *  Get a required service of a given name.
	 *  @param name The service name.
	 *  @return The service.
	 */
	public <T> IFuture<T> getService(String name)
	{
		return getFeature(IRequiredServicesFeature.class).getService(name);
	}
	
	/**
	 *  @deprecated From version 3.0 - replaced with getComponentFeature(IExecutionFeature.class).scheduleStep()
	 *  Execute a component step.
	 */
	public <T> IFuture<T> scheduleStep(IComponentStep<T> step)
	{
		return getFeature(IExecutionFeature.class).scheduleStep(step);
	}
	
	/**
	 * 	@deprecated From version 3.0 - replaced with getComponentFeature(IExecutionFeature.class).waitForDelay()
	 *  Wait for some time and execute a component step afterwards.
	 */
	public <T>	IFuture<T> waitForDelay(long delay, IComponentStep<T> step)
	{
		return getFeature(IExecutionFeature.class).waitForDelay(delay, step);
	}
	
	/**
	 *  Get the model of the component.
	 *  @return	The model.
	 */
	public IModelInfo getModel()
	{
		return access.getModel();
	}

	/**
	 *  Get the configuration.
	 *  @return	The configuration.
	 */
	public String getConfiguration()
	{
		return access.getConfiguration();
	}
	
	/**
	 *  Get the id of the component.
	 *  @return	The component id.
	 */
	public IComponentIdentifier	getId()
	{
		return access.getId();
	}
	
	/**
	 *  Get a feature of the component.
	 *  @param feature	The type of the feature.
	 *  @return The feature instance.
	 */
	public <T> T getFeature(Class<? extends T> type)
	{
		return access.getFeature(type);
	}
	
	/**
	 *  Get a feature of the component.
	 *  @param feature	The type of the feature.
	 *  @return The feature instance.
	 */
	public <T> T getFeature0(Class<? extends T> type)
	{
		return access.getFeature0(type);
	}
	
	/**
	 *  Get the component description.
	 *  @return	The component description.
	 */
	// Todo: hack??? should be internal to CMS!?
	public IComponentDescription	getDescription()
	{
		return access.getDescription();
	}
	
	/**
	 *  Get the component description.
	 *  @return	The component description.
	 */
	// Todo: hack??? should be internal to CMS!?
	public IFuture<IComponentDescription>	getDescription(IComponentIdentifier cid)
	{
		return access.getDescription(cid);
	}
	
	/**
	 *  Kill the component.
	 */
	public IFuture<Map<String, Object>> killComponent()
	{
		return access.killComponent();
	}
	
	/**
	 *  Kill the component.
	 *  @param e The failure reason, if any.
	 */
	public IFuture<Map<String, Object>> killComponent(Exception e)
	{
		return access.killComponent(e);
	}
	
	/**
	 *  Get the external access.
	 *  @return The external access.
	 */
	public IExternalAccess getExternalAccess()
	{
		return access.getExternalAccess();
	}
	
	/**
	 *  Get the logger.
	 *  @return The logger.
	 */
	public Logger getLogger()
	{
		return access.getLogger();
	}
	
	/**
	 *  Get the fetcher.
	 *  @return The fetcher.
	 */
	// Todo: move to IPlatformComponent?
	public IValueFetcher getFetcher()
	{
		return access.getFetcher();
	}
		
	/**
	 *  Get the parameter guesser.
	 *  @return The parameter guesser.
	 */
	// Todo: move to IPlatformComponent?
	public IParameterGuesser getParameterGuesser()
	{
		return access.getParameterGuesser();
	}
		
	/**
	 *  Get the class loader of the component.
	 */
	public ClassLoader	getClassLoader()
	{
		return access.getClassLoader();
	}
	
	/**
	 *  Get the children (if any) component identifiers.
	 *  @return The children component identifiers.
	 */
	public IFuture<IComponentIdentifier[]> getChildren(String type, IComponentIdentifier parent)
	{
		return access.getChildren(type, parent);
	}
	
	/**
	 *  Get the exception, if any.
	 *  @return The failure reason for use during cleanup, if any.
	 */
	public Exception getException()
	{
		return access.getException();
	}
	
	/**
	 *  Get the external access for a component id.
	 *  @param cid The component id.
	 *  @return The external access.
	 */
	public IFuture<IExternalAccess> getExternalAccess(IComponentIdentifier cid)
	{
		return access.getExternalAccess(cid);
	}
	
	/**
	 *  Kill the component.
	 *  @param e The failure reason, if any.
	 */
	public IFuture<Map<String, Object>> killComponent(IComponentIdentifier cid)
	{
		return access.killComponent(cid);
	}
	
	/**
	 *  Add a new component as subcomponent of this component.
	 *  @param component The model or pojo of the component.
	 */
	public IFuture<IExternalAccess> createComponent(Object component, CreationInfo info, IResultListener<Collection<Tuple2<String, Object>>> resultlistener)
	{
		return access.createComponent(component, info, resultlistener);
	}
	
	/**
	 *  Add a new component as subcomponent of this component.
	 *  @param component The model or pojo of the component.
	 */
	public ISubscriptionIntermediateFuture<CMSStatusEvent> createComponentWithResults(Object component, CreationInfo info)
	{
		return access.createComponentWithResults(component, info);
	}
	
	/**
	 *  Create a new component on the platform.
	 *  @param name The component name or null for automatic generation.
	 *  @param model The model identifier (e.g. file name).
	 *  @param info Additional start information such as parent component or arguments (optional).
	 *  @return The id of the component and the results after the component has been killed.
	 */
	public ITuple2Future<IComponentIdentifier, Map<String, Object>> createComponent(Object component, CreationInfo info)
	{
		return access.createComponent(component, info);
	}
	
	/**
	 *  Suspend the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public IFuture<Void> suspendComponent(IComponentIdentifier componentid)
	{
		return access.suspendComponent(componentid);
	}
	
	/**
	 *  Resume the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public IFuture<Void> resumeComponent(IComponentIdentifier componentid)
	{
		return access.resumeComponent(componentid);
	}
	
	/**
	 *  Execute a step of a suspended component.
	 *  @param componentid The component identifier.
	 *  @param listener Called when the step is finished (result will be the component description).
	 */
	public IFuture<Void> stepComponent(IComponentIdentifier componentid, String stepinfo)
	{
		return access.stepComponent(componentid, stepinfo);
	}
	
	/**
	 *  Set breakpoints for a component.
	 *  Replaces existing breakpoints.
	 *  To add/remove breakpoints, use current breakpoints from component description as a base.
	 *  @param componentid The component identifier.
	 *  @param breakpoints The new breakpoints (if any).
	 */
	public IFuture<Void> setComponentBreakpoints(IComponentIdentifier componentid, String[] breakpoints)
	{
		return access.setComponentBreakpoints(componentid, breakpoints);
	}
	
	/**
	 *  Add a component listener for a specific component.
	 *  The listener is registered for component changes.
	 *  @param cid	The component to be listened.
	 */
	public ISubscriptionIntermediateFuture<CMSStatusEvent> listenToComponent(IComponentIdentifier cid)
	{
		return access.listenToComponent(cid);
	}
	
	/**
	 *  Search for components matching the given description.
	 *  @return An array of matching component descriptions.
	 */
	public IFuture<IComponentDescription[]> searchComponents(IComponentDescription adesc, ISearchConstraints con)
	{
		return access.searchComponents(adesc, con);
	}
}
