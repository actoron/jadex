package jadex.bdi.features.impl;

import jadex.bdi.runtime.IPlanExecutor;
import jadex.bdi.runtime.interpreter.EventReificator;
import jadex.bridge.IComponentStep;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.commons.future.IFuture;
import jadex.rules.state.IOAVState;

import java.util.List;
import java.util.logging.Logger;

/**
 *  System access to BDI features.
 */
public interface IInternalBDIAgentFeature
{
	/**
	 *  Get the event reificator for generating change events.
	 */
	public EventReificator	getEventReificator();

	/**
	 *  Find the path to a subcapability.
	 *  @param rcapa The start capability.
	 *  @param targetcapa The target capability.
	 *  @param path The result path as list of capas.
	 *  @return True if found.
	 */
	public boolean findSubcapability(Object rcapa, Object targetcapa, List<Object> path);
	
	/**
	 *  Get the agent instance reference.
	 *  @return The agent.
	 */
	public Object	getAgent();
	
	/**
	 *  Get the logger.
	 *  @return The logger.
	 */
	public Logger getLogger(Object rcapa);
	
	/**
	 *  Get a plan executor by name.
	 *  @param rplan The rplan.
	 *  @return The plan executor.
	 */
	public IPlanExecutor getPlanExecutor(Object rplan);
	
	/**
	 *  Sets the plan currently executed by this agent.
	 *  @param currentplan The current plan.
	 */
	public void setCurrentPlan(Object currentplan);
	
	/**
	 *  Get the agent state.
	 *  @return The agent state.
	 */
	public IOAVState getState();
	
	/**
	 *  Start monitoring the consequences.
	 */
	public void startMonitorConsequences();

	/**
	 *  Checks if consequences have been produced and
	 *  interrupts the executing plan accordingly.
	 */
	public void endMonitorConsequences();
	
	/**
	 *  Start an atomic transaction.
	 *  All possible side-effects (i.e. triggered conditions)
	 *  of internal changes (e.g. belief changes)
	 *  will be delayed and evaluated after endAtomic() has been called.
	 *  @see #endAtomic()
	 */
	public void	startAtomic();

	/**
	 *  End an atomic transaction.
	 *  Side-effects (i.e. triggered conditions)
	 *  of all internal changes (e.g. belief changes)
	 *  performed after the last call to startAtomic()
	 *  will now be evaluated and performed.
	 *  @see #startAtomic()
	 */
	public void	endAtomic();
	
	/**
	 *  Get the model info of a capability
	 *  @param rcapa	The capability.
	 *  @return The model info.
	 */
	public IModelInfo	getModel(Object rcapa);
	
	/**
	 *  Check if atomic state is enabled.
	 *  @return True, if is atomic.
	 */
	public boolean	isAtomic();
	
	/**
	 *  Set the current plan thread.
	 *  @param planthread The planthread.
	 */ 
	public void setPlanThread(Thread planthread);
	
	/**
	 *  Put an element into the cache.
	 */
	public void putFlyweightCache(Class<?> type, Object key, Object flyweight);
	
	/**
	 *  Get an element from the cache.
	 */
	public Object getFlyweightCache(Class<?> type, Object key);
	
	/**
	 *  Invoke some code with agent behaviour synchronized on the agent.
	 *  @param code The code to execute.
	 *  The method will block the externally calling thread until the
	 *  action has been executed on the agent thread.
	 *  If the agent does not accept external actions (because of termination)
	 *  the method will directly fail with a runtime exception.
	 *  Note: 1.4 compliant code.
	 *  Problem: Deadlocks cannot be detected and no exception is thrown.
	 */
	public void invokeSynchronized(final Runnable code);
	
	/**
	 *  Schedule a step of the agent.
	 *  May safely be called from external threads.
	 *  @param step	Code to be executed as a step of the agent.
	 */
	public <T> IFuture<T> scheduleStep(IComponentStep<T> step, Object scope);
	
	/**
	 *  Execute some code on the component's thread.
	 *  Unlike scheduleStep(), the action will also be executed
	 *  while the component is suspended.
	 *  @param action	Code to be executed on the component's thread.
	 *  @return The result of the step.
	 */
	public <T> IFuture<T> scheduleImmediate(IComponentStep<T> step, Object scope);
	
	/**
	 *  Check if the agent thread is accessing.
	 *  @return True, if access is ok.
	 */ 
	public boolean isPlanThread();

	/**
	 *  Add a default value for an argument (if not already present).
	 *  Called once for each argument during init.
	 *  @param name	The argument name.
	 *  @param value	The argument value.
	 */
	public boolean	addArgument(String name, Object value);

	/**
	 *  Called when the agent is removed from the platform.
	 */
	public void cleanup();
}
