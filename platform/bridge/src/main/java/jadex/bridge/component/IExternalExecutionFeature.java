package jadex.bridge.component;

import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.CMSStatusEvent;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;

/**
 *  External perspective of the execution feature. 
 */
public interface IExternalExecutionFeature extends IExternalComponentFeature
{	
	/**
	 *  Execute a component step.
	 *  @param step The component step.
	 *  @return the future result of the step execution.
	 */
	public <T> IFuture<T> scheduleStep(IComponentStep<T> step);
	
	/**
	 *  Schedule a component step but don't wait for its execution.
	 *  Scheduling a decoupled step is useful to indicate that exceptions in the
	 *  step are not handled by the caller, e.g., to have them printed to the console instead of discarded.
	 *  
	 *  @param step The component step.
	 *  @return A future indicating that the step has been scheduled (but maybe not yet executed).
	 */
	public IFuture<Void> scheduleDecoupledStep(IComponentStep<?> step);
	
	/**
	 *  Execute a component step.
	 *  @param step The component step.
	 *  @param priority The step priority.
	 *  The priority x>STEP_PRIORITY_IMMEDIATE being immediate steps,
	 *  i.e. all steps with prio x>=STEP_PRIORITY_IMMEDIATE are always executed (even when suspended).
	 *  Default steps get prio STEP_PRIORITY_NOMRAL (not immediate). 
	 */
	public <T> IFuture<T> scheduleStep(int priority, boolean inherit, IComponentStep<T> step);
	
//	/**
//	 *  Execute an immediate component step,
//	 *  i.e., the step is executed also when the component is currently suspended.
//	 */
//	public <T>	IFuture<T> scheduleImmediate(IComponentStep<T> step);
	
	/**
	 *  Wait for some time and execute a component step afterwards.
	 */
	public <T> IFuture<T> waitForDelay(long delay, IComponentStep<T> step, boolean realtime);

	/**
	 *  Wait for some time and execute a component step afterwards.
	 */
	public <T> IFuture<T> waitForDelay(long delay, IComponentStep<T> step);

	/**
	 *  Wait for some time.
	 */
	public IFuture<Void> waitForDelay(long delay, boolean realtime);
	
	/**
	 *  Wait for some time.
	 */
	public IFuture<Void> waitForDelay(long delay);
	
	/**
	 *  Wait for the next tick.
	 *  @param time The time.
	 */
	// TimerWrapper
	public IFuture<Void> waitForTick(final IComponentStep<Void> run);
	
	/**
	 *  Wait for the next tick.
	 *  @param time The time.
	 */
	// TimerWrapper
	public IFuture<Void> waitForTick();
	
	/**
	 *  Waits for the components to finish.
	 * 
	 *  @return Component results.
	 */
	public IFuture<Map<String, Object>> waitForTermination();
	
	// todo:?
//	/**
//	 *  Wait for some time and execute a component step afterwards.
//	 */
//	public IFuture waitForImmediate(long delay, IComponentStep step);

	/**
	 * Repeats a ComponentStep periodically, until terminate() is called on result future or a failure occurs in a step.
	 * 
	 * Warning: In order to avoid memory leaks, the returned subscription future does NOT store
	 * values, requiring the addition of a listener within the same step the repeat
	 * step was schedule.
	 * 
	 * @param initialdelay delay before first execution in milliseconds
	 * @param delay delay between scheduled executions of the step in milliseconds
	 * @param step The component step
	 * @return The intermediate results
	 */
	public <T> ISubscriptionIntermediateFuture<T> repeatStep(long initialdelay, long delay, IComponentStep<T> step);
	
	/**
	 * Repeats a ComponentStep periodically, until terminate() is called on result future.
	 * 
	 * Warning: In order to avoid memory leaks, the returned subscription future does NOT store
	 * values, requiring the addition of a listener within the same step the repeat
	 * step was schedule.
	 * 
	 * @param initialdelay delay before first execution in milliseconds
	 * @param delay delay between scheduled executions of the step in milliseconds
	 * @param step The component step
	 * @param ignorefailures Don't terminate repeating after a failed step.
	 * @return The intermediate results
	 */
	public <T> ISubscriptionIntermediateFuture<T> repeatStep(long initialdelay, long delay, IComponentStep<T> step, boolean ignorefailures);
	
	/**
	 *  Add a component listener for a specific component.
	 *  The listener is registered for component changes.
	 *  @param cid	The component to be listened.
	 */
	public ISubscriptionIntermediateFuture<CMSStatusEvent> listenToComponent();
	
	/**
	 *  Add a component listener for all components of a platform.
	 *  The listener is registered for component changes.
	 */
	public ISubscriptionIntermediateFuture<CMSStatusEvent> listenToAll();
	
//	/**
//	 * Search for components matching the given description.
//	 * @return An array of matching component descriptions.
//	 */
//	public IFuture<IComponentDescription[]> searchComponents(IComponentDescription adesc, ISearchConstraints con);
	
	/**
	 *  Execute a step of a suspended component.
	 *  @param componentid The component identifier.
	 *  @param listener Called when the step is finished (result will be the component description).
	 */
	public IFuture<Void> stepComponent(String stepinfo);
	
	/**
	 *  Set breakpoints for a component.
	 *  Replaces existing breakpoints.
	 *  To add/remove breakpoints, use current breakpoints from component description as a base.
	 *  @param componentid The component identifier.
	 *  @param breakpoints The new breakpoints (if any).
	 */
	public IFuture<Void> setComponentBreakpoints(String[] breakpoints);
	
	/**
	 *  Suspend the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public IFuture<Void> suspendComponent();
	
	/**
	 *  Resume the execution of an component.
	 *  @param componentid The component identifier.
	 */
	public IFuture<Void> resumeComponent();
	
	/**
	 *  Add a new component as subcomponent of this component.
	 *  @param component The model or pojo of the component.
	 */
//	public IFuture<IExternalAccess> createComponent(CreationInfo info, IResultListener<Collection<Tuple2<String, Object>>> resultlistener);
//	public IFuture<IExternalAccess> createComponent(Object component, CreationInfo info, IResultListener<Collection<Tuple2<String, Object>>> resultlistener);
	
	/**
	 *  Add a new component as subcomponent of this component.
	 *  @param component The model or pojo of the component.
	 */
//	public ISubscriptionIntermediateFuture<CMSStatusEvent> createComponentWithResults(CreationInfo info);
//	public ISubscriptionIntermediateFuture<CMSStatusEvent> createComponentWithResults(Object component, CreationInfo info);
	
	/**
	 *  Create a new component on the platform.
	 *  @param name The component name or null for automatic generation.
	 *  @param model The model identifier (e.g. file name).
	 *  @param info Additional start information such as parent component or arguments (optional).
	 *  @return The id of the component and the results after the component has been killed.
	 */
//	public ITuple2Future<IComponentIdentifier, Map<String, Object>> createComponent(CreationInfo info);
//	public ITuple2Future<IComponentIdentifier, Map<String, Object>> createComponent(Object component, CreationInfo info);
	
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
//	public IFuture<Map<String, Object>> killComponent(IComponentIdentifier cid);
	
	/**
	 *  Get the external access for a component id.
	 *  @param cid The component id.
	 *  @return The external access.
	 */
	public IExternalAccess getExternalAccess(IComponentIdentifier cid);
	
	/**
	 *  Get the external access for a component id.
	 *  @param cid The component id.
	 *  @return The external access.
	 */
	public IFuture<IExternalAccess> getExternalAccessAsync(IComponentIdentifier cid);
	
	/**
	 *  Get the component description.
	 *  @return	The component description.
	 */
	// Todo: hack??? should be internal to CMS!?
	public IFuture<IComponentDescription> getDescriptionAsync();
	
	/**
	 *  Get the component description.
	 *  @return	The component description.
	 */
	// Todo: hack??? should be internal to CMS!?
	public IFuture<IComponentDescription> getDescription(IComponentIdentifier cid);
	
	/**
	 *  Get the component descriptions.
	 *  @return	The component descriptions.
	 */
	// Todo: hack??? should be internal to CMS!?
	public IFuture<IComponentDescription[]> getDescriptions();

}
