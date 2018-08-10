package jadex.bridge;

import java.util.Collection;
import java.util.Map;
import java.util.logging.Logger;

import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService.CMSStatusEvent;
import jadex.commons.IParameterGuesser;
import jadex.commons.IValueFetcher;
import jadex.commons.Tuple2;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITuple2Future;

/**
 *  Common interface for all component types.
 *  Provides the user view of the component, i.e.,
 *  methods the component can call on itself.
 */
public interface IInternalAccess
{
	/**
	 *  Get the model of the component.
	 *  @return	The model.
	 */
	public IModelInfo getModel();

	/**
	 *  Get the configuration.
	 *  @return	The configuration.
	 */
	public String getConfiguration();
	
	/**
	 *  Get the id of the component.
	 *  @return	The component id.
	 */
	public IComponentIdentifier	getId();
	
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
	
	/**
	 *  Get the component description.
	 *  @return	The component description.
	 */
	// Todo: hack??? should be internal to CMS!?
	public IComponentDescription getDescription();
	
	/**
	 *  Add a new component as subcomponent of this component.
	 *  @param component The model or pojo of the component.
	 */
	public IFuture<IExternalAccess> createComponent(Object component, CreationInfo info, IResultListener<Collection<Tuple2<String, Object>>> resultlistener);
	
	/**
	 *  Add a new component as subcomponent of this component.
	 *  @param component The model or pojo of the component.
	 */
	public ISubscriptionIntermediateFuture<CMSStatusEvent> createComponentWithResults(Object component, CreationInfo info);
	
	/**
	 *  Create a new component on the platform.
	 *  @param name The component name or null for automatic generation.
	 *  @param model The model identifier (e.g. file name).
	 *  @param info Additional start information such as parent component or arguments (optional).
	 *  @return The id of the component and the results after the component has been killed.
	 */
	public ITuple2Future<IComponentIdentifier, Map<String, Object>> createComponent(Object component, CreationInfo info);
	
	/**
	 *  Kill the component.
	 */
	public IFuture<Map<String, Object>> killComponent();
	
	/**
	 *  Kill the component.
	 *  @param e The failure reason, if any.
	 */
	public IFuture<Map<String, Object>> killComponent(Exception e);
	
	/**
	 *  Kill the component.
	 *  @param e The failure reason, if any.
	 */
	public IFuture<Map<String, Object>> killComponent(IComponentIdentifier cid);
	
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
	
	/**
	 *  Execute a component step.
	 */
	public <T> IFuture<T> scheduleStep(IComponentStep<T> step);
	
	/**
	 *  Wait for some time and execute a component step afterwards.
	 */
	public <T>	IFuture<T> waitForDelay(long delay, IComponentStep<T> step);
		
	/**
	 *  Get the children (if any) component identifiers.
	 *  @return The children component identifiers.
	 */
	public IFuture<IComponentIdentifier[]> getChildren(String type);
	
	/**
	 *  Get the exception, if any.
	 *  @return The failure reason for use during cleanup, if any.
	 */
	public Exception getException();
	
	/**
	 *  Get the external access for a component id.
	 *  @param cid The component id.
	 *  @return The external access.
	 */
	public IFuture<IExternalAccess> getExternalAccess(IComponentIdentifier cid);
}
