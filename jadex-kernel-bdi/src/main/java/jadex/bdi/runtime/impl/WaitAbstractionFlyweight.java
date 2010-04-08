package jadex.bdi.runtime.impl;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.bdi.runtime.IExternalCondition;
import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IMessageEvent;
import jadex.bdi.runtime.IWaitAbstraction;
import jadex.bdi.runtime.interpreter.AgentRules;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.MessageEventRules;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.rules.state.IOAVState;

import java.util.Collection;

/**
 *  Flyweight for a waitabstraction.
 */
public class WaitAbstractionFlyweight extends ElementFlyweight implements IWaitAbstraction
{
	//-------- constructors --------
	
	/**
	 *  Create a new waitabstraction flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 *  @param handle	The element handle.
	 */
	protected WaitAbstractionFlyweight(IOAVState state, Object scope, Object handle)
	{
		// Todo: no more lazy wa creation. is this correct??? (required for plans/timeoutexception testcase)
		super(state, scope, handle!=null? handle: state.createObject(OAVBDIRuntimeModel.waitabstraction_type));
	}
	
	/**
	 *  Get or create a flyweight.
	 *  @return The flyweight.
	 */
	public static WaitAbstractionFlyweight getWaitAbstractionFlyweight(IOAVState state, Object scope, Object handle)
	{
		BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
		WaitAbstractionFlyweight ret = (WaitAbstractionFlyweight)ip.getFlyweightCache(IWaitAbstraction.class).get(handle);
		if(ret==null)
		{
			ret = new WaitAbstractionFlyweight(state, scope, handle);
			ip.getFlyweightCache(IWaitAbstraction.class).put(handle, ret);
		}
		return ret;
	}
	
	//-------- adder methods --------
	
	/**
	 *  Add a message event.
	 *  @param type The type.
	 */
	public IWaitAbstraction addMessageEvent(final String type)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					addMessageEvent(wa, type, getState(), getScope());
				}
			};
			return this;
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			addMessageEvent(wa, type, getState(), getScope());		
			return this;
		}
	}

	/**
	 * Add a message event reply.
	 * @param me The message event.
	 */
	public IWaitAbstraction addReply(final IMessageEvent me)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					addReply(wa, me, getState(), getScope());
				}
			};
			return this;
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			addReply(wa, me, getState(), getScope());
			return this;
		}
	}

	/**
	 *  Add an internal event.
	 *  @param type The type.
	 */
	public IWaitAbstraction addInternalEvent(final String type)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					addInternalEvent(wa, type, getState(), getScope());
				}
			};
			return this;
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			addInternalEvent(wa, type, getState(), getScope());
			return this;
		}
	}

	/**
	 *  Add a goal.
	 *  @param type The type.
	 */
	public IWaitAbstraction addGoal(final String type)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					addGoal(wa, type, getState(), getScope());
				}
			};
			return this;
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			addGoal(wa, type, getState(), getScope());
			return this;
		}
	}

	/**
	 *  Add a Goal.
	 *  @param goal The goal.
	 */
	public IWaitAbstraction addGoal(final IGoal goal)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					addGoal(wa, goal, getState(), getScope());
				}
			};
			return this;		
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			addGoal(wa, goal, getState(), getScope());
			return this;
		}
	}

	/**
	 *  Add a fact changed.
	 */
	public IWaitAbstraction addFactChanged(final String type)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					addFactChanged(wa, type, getState(), getScope());
				}
			};
			return this;
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			addFactChanged(wa, type, getState(), getScope());
			return this;
		}
	}

	/**
	 *  Add a fact added.
	 */
	public IWaitAbstraction addFactAdded(final String type)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					addFactAdded(wa, type, getState(), getScope());
				}
			};
			return this;
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			addFactAdded(wa, type, getState(), getScope());
			return this;
		}
	}


	/**
	 *  Add a fact added.
	 */
	public IWaitAbstraction addFactRemoved(final String type)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					addFactRemoved(wa, type, getState(), getScope());
				}
			};
			return this;
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			addFactRemoved(wa, type, getState(), getScope());
			return this;
		}
	}
	
	/**
	 *  Add a condition.
	 *  @param condition the condition name.
	 */
	public IWaitAbstraction addCondition(final String condition)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					addCondition(wa, condition, getState(), getScope());
				}
			};
			return this;
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			addCondition(wa, condition, getState(), getScope());
			return this;
		}
	}


	/**
	 *  Add an external condition.
	 *  @param condition The condition.
	 */
	public IWaitAbstraction addExternalCondition(final IExternalCondition condition)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getOrCreateWaitAbstraction();
					addExternalCondition(wa, condition, getState(), getScope());
				}
			};
			return this;		
		}
		else
		{
			Object wa = getOrCreateWaitAbstraction();
			addExternalCondition(wa, condition, getState(), getScope());
			return this;
		}
	}

	//-------- remover methods --------

	/**
	 *  Remove a message event.
	 *  @param type The type.
	 */
	public void removeMessageEvent(final String type)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					if(wa!=null)
					{
						Collection mevents = getState().getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_messageeventtypes);
						Object[] scope = AgentRules.resolveCapability(type, OAVBDIMetaModel.messageevent_type, getScope(), getState());
						
						Object mcapa = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
						if(!getState().containsKey(mcapa, OAVBDIMetaModel.capability_has_messageevents, scope[0]))
							throw new RuntimeException("Unknown message event: "+type);
						Object mevent = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_messageevents, scope[0]);
	
						if(mevents!=null && mevents.contains(mevent))
							getState().removeAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_messageeventtypes, mevent);
					}
				}
			};
		}
		else
		{
			Object wa = getWaitAbstraction();
			if(wa!=null)
			{
				Collection mevents = getState().getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_messageeventtypes);
				Object[] scope = AgentRules.resolveCapability(type, OAVBDIMetaModel.messageevent_type, getScope(), getState());
				
				Object mcapa = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
				if(!getState().containsKey(mcapa, OAVBDIMetaModel.capability_has_messageevents, scope[0]))
					throw new RuntimeException("Unknown message event: "+type);
				Object mevent = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_messageevents, scope[0]);
	
				if(mevents!=null && mevents.contains(mevent))
					getState().removeAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_messageeventtypes, mevent);
			}
		}
	}

	/**
	 *  Remove a message event reply.
	 *  @param me The message event.
	 */
	public void removeReply(final IMessageEvent me)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					// Register event also in conversation map for message routing.
					Object rmevent = ((ElementFlyweight)me).getHandle();
					MessageEventRules.deregisterMessageEvent(getState(), rmevent, getScope());
					
					Object wa = getWaitAbstraction();
					if(wa!=null)
					{
						Collection rmevents = getState().getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_messageevents);
						if(rmevents!=null && rmevents.contains(rmevent))
							getState().removeAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_messageevents, rmevent);
					}
				}
			};
		}
		else
		{
			// Register event also in conversation map for message routing.
			Object rmevent = ((ElementFlyweight)me).getHandle();
			MessageEventRules.deregisterMessageEvent(getState(), rmevent, getScope());
			
			Object wa = getWaitAbstraction();
			if(wa!=null)
			{
				Collection rmevents = getState().getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_messageevents);
				if(rmevents!=null && rmevents.contains(rmevent))
					getState().removeAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_messageevents, rmevent);
			}
		}
	}

	/**
	 *  Remove an internal event.
	 *  @param type The type.
	 */
	public void removeInternalEvent(final String type)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					if(wa!=null)
					{
						Collection mevents = getState().getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_internaleventtypes);
						Object[] scope = AgentRules.resolveCapability(type, OAVBDIMetaModel.internalevent_type, getScope(), getState());
						
						Object mcapa = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
						if(!getState().containsKey(mcapa, OAVBDIMetaModel.capability_has_internalevents, scope[0]))
							throw new RuntimeException("Unknown internal event: "+type);
						Object mevent = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_internalevents, scope[0]);
	
						if(mevents!=null && mevents.contains(mevent))
							getState().removeAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_internaleventtypes, mevent);
					}
				}
			};
		}
		else
		{
			Object wa = getWaitAbstraction();
			if(wa!=null)
			{
				Collection mevents = getState().getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_internaleventtypes);
				Object[] scope = AgentRules.resolveCapability(type, OAVBDIMetaModel.internalevent_type, getScope(), getState());
				
				Object mcapa = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
				if(!getState().containsKey(mcapa, OAVBDIMetaModel.capability_has_internalevents, scope[0]))
					throw new RuntimeException("Unknown internal event: "+type);
				Object mevent = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_internalevents, scope[0]);
	
				if(mevents!=null && mevents.contains(mevent))
					getState().removeAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_internaleventtypes, mevent);
			}
		}
	}

	/**
	 *  Remove a goal.
	 *  @param type The type.
	 */
	public void removeGoal(final String type)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					if(wa!=null)
					{
						Collection mgoals = getState().getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_goalfinisheds);
						Object[] scope = AgentRules.resolveCapability(type, OAVBDIMetaModel.goal_type, getScope(), getState());
						
						Object mcapa = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
						if(!getState().containsKey(mcapa, OAVBDIMetaModel.capability_has_goals, scope[0]))
							throw new RuntimeException("Unknown goal: "+type);
						Object mgoal = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_goals, scope[0]);
	
						if(mgoals!=null && mgoals.contains(mgoal))
							getState().removeAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_goalfinisheds, mgoal);

						BDIInterpreter.getInterpreter(getState()).getEventReificator().removeObservedElement(mgoal);
					}
				}
			};
		}
		else
		{
			Object wa = getWaitAbstraction();
			if(wa!=null)
			{
				Collection mgoals = getState().getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_goalfinisheds);
				Object[] scope = AgentRules.resolveCapability(type, OAVBDIMetaModel.goal_type, getScope(), getState());
				
				Object mcapa = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
				if(!getState().containsKey(mcapa, OAVBDIMetaModel.capability_has_goals, scope[0]))
					throw new RuntimeException("Unknown goal: "+type);
				Object mgoal = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_goals, scope[0]);
	
				if(mgoals!=null && mgoals.contains(mgoal))
					getState().removeAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_goalfinisheds, mgoal);

				BDIInterpreter.getInterpreter(getState()).getEventReificator().removeObservedElement(mgoal);
			}
		}
	}

	/**
	 *  Remove a goal.
	 *  @param goal The goal.
	 */
	public void removeGoal(final IGoal goal)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					if(wa!=null)
					{
						Object rgoal = ((ElementFlyweight)goal).getHandle();
						Collection goals = getState().getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_goals);
						if(goals!=null && goals.contains(rgoal))
							getState().removeAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_goals, rgoal);

						BDIInterpreter.getInterpreter(getState()).getEventReificator().removeObservedElement(rgoal);
					}
				}
			};
		}
		else
		{
			Object wa = getWaitAbstraction();
			if(wa!=null)
			{
				Object rgoal = ((ElementFlyweight)goal).getHandle();
				Collection goals = getState().getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_goals);
				if(goals!=null && goals.contains(rgoal))
					getState().removeAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_goals, rgoal);

				BDIInterpreter.getInterpreter(getState()).getEventReificator().removeObservedElement(rgoal);
			}
		}
	}
	
	/**
	 *  Remove a fact changed.
	 *  @param belief The belief or beliefset.
	 */
	public void removeFactChanged(final String belief)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					if(wa!=null)
					{
						Object[] scope = AgentRules.resolveCapability(belief, OAVBDIMetaModel.beliefset_type, getScope(), getState());
						
						Object rbel;
						Object mcapa = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
						if(getState().containsKey(mcapa, OAVBDIMetaModel.capability_has_beliefs, scope[0]))
						{
							Object	mbel = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefs, scope[0]);
							rbel = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_beliefs, mbel);
						}
						else if(getState().containsKey(mcapa, OAVBDIMetaModel.capability_has_beliefsets, scope[0]))
						{
							Object	mbel = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefsets, scope[0]);
							rbel = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_beliefsets, mbel);
						}
						else
							throw new RuntimeException("Unknown belief(set): "+belief);

						getState().removeAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_factchangeds, rbel);
						BDIInterpreter.getInterpreter(getState()).getEventReificator().removeObservedElement(rbel);
					}
				}
			};
		}
		else
		{
			Object wa = getWaitAbstraction();
			if(wa!=null)
			{
				Object[] scope = AgentRules.resolveCapability(belief, OAVBDIMetaModel.beliefset_type, getScope(), getState());
				
				Object rbel;
				Object mcapa = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
				if(getState().containsKey(mcapa, OAVBDIMetaModel.capability_has_beliefs, scope[0]))
				{
					Object	mbel = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefs, scope[0]);
					rbel = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_beliefs, mbel);
				}
				else if(getState().containsKey(mcapa, OAVBDIMetaModel.capability_has_beliefsets, scope[0]))
				{
					Object	mbel = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefsets, scope[0]);
					rbel = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_beliefsets, mbel);
				}
				else
					throw new RuntimeException("Unknown belief(set): "+belief);

				getState().removeAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_factchangeds, rbel);
				BDIInterpreter.getInterpreter(getState()).getEventReificator().removeObservedElement(rbel);
			}
		}
	}
	
	/**
	 *  Remove a fact added.
	 *  @param beliefset The beliefset.
	 */
	public void removeFactAdded(final String beliefset)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					if(wa!=null)
					{
						Object[] scope = AgentRules.resolveCapability(beliefset, OAVBDIMetaModel.beliefset_type, getScope(), getState());
						
						Object rbel;
						Object mcapa = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
						if(getState().containsKey(mcapa, OAVBDIMetaModel.capability_has_beliefsets, scope[0]))
						{
							Object	mbel = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefsets, scope[0]);
							rbel = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_beliefsets, mbel);
						}
						else
							throw new RuntimeException("Unknown beliefset: "+beliefset);

						getState().removeAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_factaddeds, rbel);
						BDIInterpreter.getInterpreter(getState()).getEventReificator().removeObservedElement(rbel);
					}
				}
			};
		}
		else
		{
			Object wa = getWaitAbstraction();
			if(wa!=null)
			{
				Object[] scope = AgentRules.resolveCapability(beliefset, OAVBDIMetaModel.beliefset_type, getScope(), getState());
				
				Object rbel;
				Object mcapa = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
				if(getState().containsKey(mcapa, OAVBDIMetaModel.capability_has_beliefsets, scope[0]))
				{
					Object	mbel = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefsets, scope[0]);
					rbel = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_beliefsets, mbel);
				}
				else
					throw new RuntimeException("Unknown beliefset: "+beliefset);

				getState().removeAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_factaddeds, rbel);
				BDIInterpreter.getInterpreter(getState()).getEventReificator().removeObservedElement(rbel);
			}
		}
	}


	/**
	 *  Remove a fact removed.
	 *  @param beliefset The beliefset.
	 */
	public void removeFactRemoved(final String beliefset)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					if(wa!=null)
					{
						Object[] scope = AgentRules.resolveCapability(beliefset, OAVBDIMetaModel.beliefset_type, getScope(), getState());
						
						Object rbel;
						Object mcapa = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
						if(getState().containsKey(mcapa, OAVBDIMetaModel.capability_has_beliefsets, scope[0]))
						{
							Object	mbel = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefsets, scope[0]);
							rbel = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_beliefsets, mbel);
						}
						else
							throw new RuntimeException("Unknown beliefset: "+beliefset);

						getState().removeAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_factremoveds, rbel);
						BDIInterpreter.getInterpreter(getState()).getEventReificator().removeObservedElement(rbel);
					}
				}
			};
		}
		else
		{
			Object wa = getWaitAbstraction();
			if(wa!=null)
			{
				Object[] scope = AgentRules.resolveCapability(beliefset, OAVBDIMetaModel.beliefset_type, getScope(), getState());
				
				Object rbel;
				Object mcapa = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
				if(getState().containsKey(mcapa, OAVBDIMetaModel.capability_has_beliefsets, scope[0]))
				{
					Object	mbel = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefsets, scope[0]);
					rbel = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_beliefsets, mbel);
				}
				else
					throw new RuntimeException("Unknown beliefset: "+beliefset);

				getState().removeAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_factremoveds, rbel);
				BDIInterpreter.getInterpreter(getState()).getEventReificator().removeObservedElement(rbel);
			}
		}
	}
	
	/**
	 *  Remove a condition.
	 *  @param type The condition.
	 */
	public void removeCondition(final String type)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					if(wa!=null)
					{
						Collection mconds = getState().getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_conditiontypes);
						Object[] scope = AgentRules.resolveCapability(type, OAVBDIMetaModel.condition_type, getScope(), getState());
						
						Object mcapa = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
						if(!getState().containsKey(mcapa, OAVBDIMetaModel.capability_has_conditions, scope[0]))
							throw new RuntimeException("Unknown condition: "+type);
						Object mcond = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_conditions, scope[0]);
	
						if(mconds!=null && mconds.contains(mcond))
							getState().removeAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_conditiontypes, mconds);
					}
				}
			};
		}
		else
		{
			Object wa = getWaitAbstraction();
			if(wa!=null)
			{
				Collection mconds = getState().getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_conditiontypes);
				Object[] scope = AgentRules.resolveCapability(type, OAVBDIMetaModel.condition_type, getScope(), getState());
				
				Object mcapa = getState().getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
				if(!getState().containsKey(mcapa, OAVBDIMetaModel.capability_has_conditions, scope[0]))
					throw new RuntimeException("Unknown condition: "+type);
				Object mcond = getState().getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_conditions, scope[0]);

				if(mconds!=null && mconds.contains(mcond))
					getState().removeAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_conditiontypes, mconds);
			}
		}
	}
	
	/**
	 *  Remove an external condition.
	 *  @param condition The condition.
	 */
	public void removeExternalCondition(final IExternalCondition condition)
	{
		if(getInterpreter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object wa = getWaitAbstraction();
					if(wa!=null)
					{
						Collection conditions = getState().getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_externalconditions);
						if(conditions!=null && conditions.contains(condition))
							getState().removeAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_externalconditions, condition);
					}
				}
			};
		}
		else
		{
			Object wa = getWaitAbstraction();
			if(wa!=null)
			{
				Collection conditions = getState().getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_externalconditions);
				if(conditions!=null && conditions.contains(condition))
					getState().removeAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_externalconditions, condition);
			}
		}
	}

	/**
	 *  Get or create the waitabstraction.
	 *  @return The waitabstraction.
	 */
	protected Object getWaitAbstraction()
	{
		return getHandle();
//		return getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.plan_has_preparedwaitabstraction);
	}
	
	/**
	 *  Create the waitabstraction.
	 *  @return The waitabstraction.
	 */
	protected Object createWaitAbstraction()
	{
		setHandle(getState().createObject(OAVBDIRuntimeModel.waitabstraction_type));
//		getState().setAttributeValue(getHandle(), OAVBDIRuntimeModel.plan_has_preparedwaitabstraction, wa);
		return getHandle();
	}
	
	/**
	 *  Get or create the waitabstraction.
	 *  @return The waitabstraction.
	 */
	protected Object getOrCreateWaitAbstraction()
	{
		Object wa = getWaitAbstraction();
		if(wa==null)
			wa = createWaitAbstraction();
		return wa;
	}


	/**
	 *  Add a goal type to an OAV waitabstraction.
	 */
	public static void addGoal(Object wa, String type, IOAVState state, Object rcapa)
	{
		Collection mgoals = state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_goalfinisheds);
		Object[] scope = AgentRules.resolveCapability(type, OAVBDIMetaModel.goal_type, rcapa, state);
		
		Object mcapa = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
		if(!state.containsKey(mcapa, OAVBDIMetaModel.capability_has_goals, scope[0]))
			throw new RuntimeException("Unknown goal: "+type);
		Object mgoal = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_goals, scope[0]);

		if(mgoals==null || !mgoals.contains(mgoal))
			state.addAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_goalfinisheds, mgoal);
		
		BDIInterpreter.getInterpreter(state).getEventReificator().addObservedElement(mgoal);
	}

	/**
	 *  Add a goal instance to an OAV waitabstraction.
	 */
	public static void addGoal(Object wa, IGoal goal, IOAVState state, Object rcapa)
	{
		Collection goals = state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_goals);
		Object rgoal = ((ElementFlyweight)goal).getHandle();
		if(goals==null || !goals.contains(rgoal))
			state.addAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_goals, rgoal);

		BDIInterpreter.getInterpreter(state).getEventReificator().addObservedElement(rgoal);
	}

	/**
	 *  Add a fact changed event to an OAV waitabstraction.
	 */
	public static void addFactChanged(Object wa, String type, IOAVState state, Object rcapa)
	{
		// HACK! null->{belief, beliefset}?
		Object[] scope = AgentRules.resolveCapability(type, null, rcapa, state);
		
		Object rbel;
		Object mcapa = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
		if(state.containsKey(mcapa, OAVBDIMetaModel.capability_has_beliefs, scope[0]))
		{
			Object	mbel = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefs, scope[0]);
			rbel = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_beliefs, mbel);
		}
		else if(state.containsKey(mcapa, OAVBDIMetaModel.capability_has_beliefsets, scope[0]))
		{
			Object	mbel = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefsets, scope[0]);
			rbel = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_beliefsets, mbel);
		}
		else
			throw new RuntimeException("Unknown belief(set): "+type);

		Collection rbels = state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_factchangeds);
		if(rbels==null || !rbels.contains(rbel))
			state.addAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_factchangeds, rbel);
		
		BDIInterpreter.getInterpreter(state).getEventReificator().addObservedElement(rbel);
	}

	/**
	 *  Add a fact added event to an OAV waitabstraction.
	 */
	public static void addFactAdded(Object wa, String type, IOAVState state, Object rcapa)
	{
		Object[] scope = AgentRules.resolveCapability(type, OAVBDIMetaModel.beliefset_type, rcapa, state);
		
		Object rbel;
		Object mcapa = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
		if(state.containsKey(mcapa, OAVBDIMetaModel.capability_has_beliefsets, scope[0]))
		{
			Object	mbel = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefsets, scope[0]);
			rbel = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_beliefsets, mbel);
		}
		else
			throw new RuntimeException("Unknown beliefset: "+type);

		Collection rbels = state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_factaddeds);
		if(rbels==null || !rbels.contains(rbel))
			state.addAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_factaddeds, rbel);

		BDIInterpreter.getInterpreter(state).getEventReificator().addObservedElement(rbel);
	}

	/**
	 *  Add a fact removed event to an OAV waitabstraction.
	 */
	public static void addFactRemoved(Object wa, String type, IOAVState state, Object rcapa)
	{
		Object[] scope = AgentRules.resolveCapability(type, OAVBDIMetaModel.beliefset_type, rcapa, state);
		
		Object rbel;
		Object mcapa = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
		if(state.containsKey(mcapa, OAVBDIMetaModel.capability_has_beliefsets, scope[0]))
		{
			Object	mbel = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_beliefsets, scope[0]);
			rbel = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.capability_has_beliefsets, mbel);
		}
		else
			throw new RuntimeException("Unknown beliefset: "+type);

		Collection rbels = state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_factremoveds);
		if(rbels==null || !rbels.contains(rbel))
			state.addAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_factremoveds, rbel);

		BDIInterpreter.getInterpreter(state).getEventReificator().addObservedElement(rbel);
	}

	/**
	 *  Add an external condition to an OAV waitabstraction.
	 */
	public static void addExternalCondition(Object wa, IExternalCondition condition, IOAVState state, Object rcapa)
	{
		Collection conditions = state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_externalconditions);
		if(conditions==null || !conditions.contains(condition))
			state.addAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_externalconditions, condition);
	}

	/**
	 *  Add an internal event type to an OAV waitabstraction.
	 */
	public static void addInternalEvent(Object wa, String type, IOAVState state, Object rcapa)
	{
		Object[] scope = AgentRules.resolveCapability(type, OAVBDIMetaModel.internalevent_type, rcapa, state);
		
		Object mcapa = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
		if(!state.containsKey(mcapa, OAVBDIMetaModel.capability_has_internalevents, scope[0]))
			throw new RuntimeException("Unknown internal event: "+type);
		Object mevent = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_internalevents, scope[0]);

		Collection ievents = state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_internaleventtypes);
		if(ievents==null || !ievents.contains(mevent))
			state.addAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_internaleventtypes, mevent);
	}

	/**
	 *  Add a message event instance to an OAV waitabstraction.
	 */
	public static void addReply(Object wa, IMessageEvent me, IOAVState state, Object rcapa)
	{
		// Register event also in conversation map for message routing.
		Object rmevent = ((ElementFlyweight)me).getHandle();
		MessageEventRules.registerMessageEvent(state, rmevent, rcapa);
		
		Collection rmevents = state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_messageevents);
		if(rmevents==null || !rmevents.contains(rmevent))
			state.addAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_messageevents, rmevent);
	}

	/**
	 *  Add a message event type to an OAV waitabstraction.
	 */
	public static void addMessageEvent(Object wa, String type, IOAVState state, Object rcapa)
	{
		Collection mevents = state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_messageeventtypes);
		Object[] scope = AgentRules.resolveCapability(type, OAVBDIMetaModel.messageevent_type, rcapa, state);
		
		Object mcapa = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
		if(!state.containsKey(mcapa, OAVBDIMetaModel.capability_has_messageevents, scope[0]))
			throw new RuntimeException("Unknown message event: "+type);
		Object mevent = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_messageevents, scope[0]);

		if(mevents==null || !mevents.contains(mevent))
			state.addAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_messageeventtypes, mevent);
	}

	/**
	 *  Add a condition type to an OAV waitabstraction.
	 */
	public static void addCondition(Object wa, String condition, IOAVState state, Object rcapa)
	{
		Collection mconds = state.getAttributeValues(wa, OAVBDIRuntimeModel.waitabstraction_has_conditiontypes);
		Object[] scope = AgentRules.resolveCapability(condition, OAVBDIMetaModel.condition_type, rcapa, state);
		
		Object mcapa = state.getAttributeValue(scope[1], OAVBDIRuntimeModel.element_has_model);
		if(!state.containsKey(mcapa, OAVBDIMetaModel.capability_has_conditions, scope[0]))
			throw new RuntimeException("Unknown condition: "+condition);
		Object mcond = state.getAttributeValue(mcapa, OAVBDIMetaModel.capability_has_conditions, scope[0]);

		if(mconds==null || !mconds.contains(mcond))
			state.addAttributeValue(wa, OAVBDIRuntimeModel.waitabstraction_has_conditiontypes, mcond);
	}
}
