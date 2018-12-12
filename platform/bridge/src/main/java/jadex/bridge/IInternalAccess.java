package jadex.bridge;

import java.util.logging.Logger;

import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.component.IMonitoringComponentFeature;
import jadex.bridge.component.INFPropertyComponentFeature;
import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.commons.IParameterGuesser;
import jadex.commons.IValueFetcher;

/**
 *  Common interface for all component types.
 *  Provides the user view of the component, i.e.,
 *  methods the component can call on itself.
 */
public interface IInternalAccess extends IExternalAccess, IExecutionFeature, IArgumentsResultsFeature, IProvidedServicesFeature, IRequiredServicesFeature, ISubcomponentsFeature, IMonitoringComponentFeature, INFPropertyComponentFeature //extends INFPropertyProvider//extends IRemotable
{
	/**
	 *  Get the model of the component.
	 *  @return	The model.
	 */
	public IModelInfo getModel();

	/**
	 *  Get the id of the component.
	 *  @return	The component id.
	 */
	public IComponentIdentifier	getId();
	
	/**
	 *  Get the configuration.
	 *  @return	The configuration.
	 */
	public String getConfiguration();
	
	/**
	 *  Get a feature of the component.
	 *  @param feature	The type of the feature.
	 *  @return The feature instance.
	 */
	public <T> T getFeature(Class<? extends T> type);
	
	/**
	 *  Get a feature of the component without throwing exception if not present.
	 *  @param feature	The type of the feature.
	 *  @return The feature instance.
	 */
	public <T> T getFeature0(Class<? extends T> type);
	
//	/**
//	 *  Get the component description.
//	 *  @return	The component description.
//	 */
//	// Todo: hack??? should be internal to CMS!?
//	public IComponentDescription getDescription();
//	
//	/**
//	 *  Get the component description.
//	 *  @return	The component description.
//	 */
//	// Todo: hack??? should be internal to CMS!?
//	public IFuture<IComponentDescription> getDescription(IComponentIdentifier cid);
	
//	/**
//	 *  Add a new component as subcomponent of this component.
//	 *  @param component The model or pojo of the component.
//	 */
//	public IFuture<IExternalAccess> createComponent(Object component, CreationInfo info, IResultListener<Collection<Tuple2<String, Object>>> resultlistener);
//	
//	/**
//	 *  Add a new component as subcomponent of this component.
//	 *  @param component The model or pojo of the component.
//	 */
//	public ISubscriptionIntermediateFuture<CMSStatusEvent> createComponentWithResults(Object component, CreationInfo info);
//	
//	/**
//	 *  Create a new component on the platform.
//	 *  @param name The component name or null for automatic generation.
//	 *  @param model The model identifier (e.g. file name).
//	 *  @param info Additional start information such as parent component or arguments (optional).
//	 *  @return The id of the component and the results after the component has been killed.
//	 */
//	public ITuple2Future<IComponentIdentifier, Map<String, Object>> createComponent(Object component, CreationInfo info);
	
//	/**
//	 *  Kill the component.
//	 */
//	public IFuture<Map<String, Object>> killComponent();
//	
//	/**
//	 *  Kill the component.
//	 *  @param e The failure reason, if any.
//	 */
//	public IFuture<Map<String, Object>> killComponent(Exception e);
//	
//	/**
//	 *  Kill the component.
//	 *  @param e The failure reason, if any.
//	 */
//	public IFuture<Map<String, Object>> killComponent(IComponentIdentifier cid);
//	
//	/**
//	 *  Suspend the execution of an component.
//	 *  @param componentid The component identifier.
//	 */
//	public IFuture<Void> suspendComponent(IComponentIdentifier componentid);
//	
//	/**
//	 *  Resume the execution of an component.
//	 *  @param componentid The component identifier.
//	 */
//	public IFuture<Void> resumeComponent(IComponentIdentifier componentid);
//	
//	/**
//	 *  Execute a step of a suspended component.
//	 *  @param componentid The component identifier.
//	 *  @param listener Called when the step is finished (result will be the component description).
//	 */
//	public IFuture<Void> stepComponent(IComponentIdentifier componentid, String stepinfo);
//	
//	/**
//	 *  Set breakpoints for a component.
//	 *  Replaces existing breakpoints.
//	 *  To add/remove breakpoints, use current breakpoints from component description as a base.
//	 *  @param componentid The component identifier.
//	 *  @param breakpoints The new breakpoints (if any).
//	 */
//	public IFuture<Void> setComponentBreakpoints(IComponentIdentifier componentid, String[] breakpoints);
	
	/**
	 *  Get the external access.
	 *  @return The external access.
	 */
	public IExternalAccess getExternalAccess();
	
	/**
	 *  Get the logger.
	 *  @return The logger.
	 */
	public Logger getLogger();
	
	/**
	 *  Get the fetcher.
	 *  @return The fetcher.
	 */
	// Todo: move to IPlatformComponent?
	public IValueFetcher getFetcher();
		
	/**
	 *  Get the parameter guesser.
	 *  @return The parameter guesser.
	 */
	// Todo: move to IPlatformComponent?
	public IParameterGuesser getParameterGuesser();
	
	/**
	 *  Get an argument value per name.
	 *  @param name The argument name.
	 *  @return The argument value.
	 */
	public Object getArgument(String name);
	
	/**
	 *  Get the class loader of the component.
	 */
	public ClassLoader	getClassLoader();
	
//	/**
//	 *  Execute a component step.
//	 */
//	public <T> IFuture<T> scheduleStep(IComponentStep<T> step);
//	
//	/**
//	 *  Wait for some time and execute a component step afterwards.
//	 */
//	public <T>	IFuture<T> waitForDelay(long delay, IComponentStep<T> step);
		
//	/**
//	 *  Get the children (if any) component identifiers.
//	 *  @param type The local component type.
//	 *  @param parent The parent.
//	 *  @return The children component identifiers.
//	 */
//	public IFuture<IComponentIdentifier[]> getChildren(String type, IComponentIdentifier parent);
	
	/**
	 *  Get the exception, if any.
	 *  @return The failure reason for use during cleanup, if any.
	 */
	public Exception getException();
	
//	/**
//	 *  Get the external access for a component id.
//	 *  @param cid The component id.
//	 *  @return The external access.
//	 */
//	public IFuture<IExternalAccess> getExternalAccess(IComponentIdentifier cid);
//	
//	/**
//	 *  Add a component listener for a specific component.
//	 *  The listener is registered for component changes.
//	 *  @param cid	The component to be listened.
//	 */
//	public ISubscriptionIntermediateFuture<CMSStatusEvent> listenToComponent(IComponentIdentifier cid);
//	
//	/**
//	 *  Search for components matching the given description.
//	 *  @return An array of matching component descriptions.
//	 */
//	public IFuture<IComponentDescription[]> searchComponents(IComponentDescription adesc, ISearchConstraints con);//, boolean remote);
}
