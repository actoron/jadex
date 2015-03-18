package jadex.bdi.features;

import java.util.List;
import java.util.logging.Logger;

import jadex.bdi.runtime.IPlanExecutor;
import jadex.bdi.runtime.interpreter.EventReificator;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.rules.state.IOAVState;


/**
 * 
 */
public interface IBDIAgentFeature
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
	public boolean findSubcapability(Object rcapa, Object targetcapa, List path);
	
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
	public void putFlyweightCache(Class type, Object key, Object flyweight);
	
	/**
	 *  Get an element from the cache.
	 */
	public Object getFlyweightCache(Class type, Object key);
}
