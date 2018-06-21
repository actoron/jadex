package jadex.bdiv3.features.impl;

import java.util.Map;

import jadex.bdiv3.model.IBDIModel;
import jadex.bdiv3.runtime.IBeliefListener;
import jadex.bdiv3.runtime.impl.RCapability;
import jadex.commons.IResultCommand;
import jadex.commons.beans.PropertyChangeEvent;
import jadex.commons.future.IFuture;
import jadex.rules.eca.EventType;
import jadex.rules.eca.RuleSystem;

/**
 *  Methods internally called on the BDI agent feature. 
 */
public interface IInternalBDIAgentFeature
{
//	/**
//	 *  Get the inited.
//	 *  @return The inited.
//	 */
//	public boolean isInited();

	/**
	 *  Get the BDI model.
	 *  @return the BDI model.
	 */
	public IBDIModel getBDIModel();

	/**
	 *  Get the runtime state.
	 *  @return the capability.
	 */
	public RCapability getCapability();
	
	/**
	 *  Get the rulesystem.
	 *  @return The rulesystem.
	 */
	public RuleSystem getRuleSystem();

	/**
	 *  Get the event adders map.
	 *  @return The event adders.
	 */
	public Map<EventType, IResultCommand<IFuture<Void>, PropertyChangeEvent>> getEventAdders();
	
//	/**
//	 *  Get parameter values for injection into method and constructor calls.
//	 */
//	public Object[] getInjectionValues(Class<?>[] ptypes, Annotation[][] anns, MElement melement, ChangeEvent event, RPlan rplan, RProcessableElement rpe);
//	
//	// todo: support parameter names via annotation in guesser (guesser with meta information)
//	/**
//	 *  Get parameter values for injection into method and constructor calls.
//	 *  @return A valid assigment or null if no assignment could be found.
//	 */
//	public Object[]	getInjectionValues(Class<?>[] ptypes, Annotation[][] anns, MElement melement, ChangeEvent event, RPlan rplan, RProcessableElement rpe, Collection<Object> vs);

//	/**
//	 *  Get a capability pojo object.
//	 *  @return The capability pojo.
//	 */
//	public Object	getCapabilityObject(String name);
	
	/**
	 *  Add a belief listener.
	 *  @param name The belief name.
	 *  @param listener The belief listener.
	 */
	public void addBeliefListener(String name, IBeliefListener<?> listener);
	
	/**
	 *  Remove a belief listener.
	 *  @param name The belief name.
	 *  @param listener The belief listener.
	 */
	public void removeBeliefListener(String name, IBeliefListener<?> listener);
}
