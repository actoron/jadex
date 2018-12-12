package jadex.bdiv3.runtime;

import jadex.bridge.IInternalAccess;

/**
 * 
 */
public interface ICapability
{
	//-------- ICapability interface --------
	
	/**
	 *  Add a belief listener.
	 *  @param name The belief name.
	 *  @param listener The belief listener.
	 */
	public <T> void addBeliefListener(final String name, final IBeliefListener<T> listener);
	
	/**
	 *  Remove a belief listener.
	 *  @param name The belief name.
	 *  @param listener The belief listener.
	 */
	public <T> void removeBeliefListener(String name, IBeliefListener<T> listener);
	
	/**
	 *  Get the agent.
	 */
	public IInternalAccess	getAgent();
	
	/**
	 *  Get the pojo capability object.
	 *  Only applicable for Pojo BDI agents (i.e. not for XML BDI agents).
	 *  @return The user defined java object for the capability.
	 *  @throws UnsupportedOperationException for XML BDI agents.
	 */
	public Object getPojoCapability();
	
//	/**
//	 *  Get the service container of the capability.
//	 */
//	public IServiceContainer getServiceContainer();

	
//	/**
//	 *  Get the model element.
//	 *  @return The model element.
//	 */
//	public MElement getModelElement();
//	
//	/**
//	 *  Get the id.
//	 *  @return The id.
//	 */
//	public String getId();
//	
//	
//	/**
//	 *  Get the goals.
//	 *  @return The goals.
//	 */
//	public Collection<IGoal> getGoals();
//	
//	/**
//	 *  Get goals of a specific pojo type.
//	 *  @param type The type.
//	 *  @return The goals.
//	 */
//	public Collection<RGoal> getGoals(MGoal mgoal);
//	
//	/**
//	 *  Get goals of a specific pojo type.
//	 *  @param type The type.
//	 *  @return The goals.
//	 */
//	public Collection<RGoal> getGoals(Class<?> type);
//	
//	/**
//	 *  Test if a goal is contained.
//	 *  @param type The type.
//	 *  @return The goals.
//	 */
//	public boolean containsGoal(Object pojogoal);
//
//	/**
//	 *  Get the plans.
//	 *  @return The plans.
//	 */
//	public Collection<RPlan> getPlans();
//
//	/**
//	 *  Get goals of a specific pojo type.
//	 *  @param type The type.
//	 *  @return The goals.
//	 */
//	public Collection<RPlan> getPlans(MPlan mplan);
}
