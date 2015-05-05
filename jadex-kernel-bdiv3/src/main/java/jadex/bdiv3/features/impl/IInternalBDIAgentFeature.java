package jadex.bdiv3.features.impl;

import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.model.IBDIModel;
import jadex.bdiv3.model.MElement;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.impl.RCapability;
import jadex.bdiv3.runtime.impl.RPlan;
import jadex.bdiv3.runtime.impl.RProcessableElement;
import jadex.rules.eca.RuleSystem;

import java.lang.annotation.Annotation;
import java.util.Collection;

/**
 *  Methods internally called on the BDI agent feature. 
 */
public interface IInternalBDIAgentFeature extends IBDIAgentFeature
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

	/**
	 *  Get a capability pojo object.
	 *  @return The capability pojo.
	 */
	public Object	getCapabilityObject(String name);
}
