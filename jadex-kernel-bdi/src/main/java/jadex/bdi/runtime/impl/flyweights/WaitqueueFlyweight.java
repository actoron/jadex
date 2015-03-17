package jadex.bdi.runtime.impl.flyweights;

import jadex.bdi.runtime.IGoal;
import jadex.bdi.runtime.IWaitAbstraction;
import jadex.bdi.runtime.IWaitqueue;
import jadex.bdi.runtime.impl.SFlyweightFunctionality;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.commons.Tuple;
import jadex.rules.state.IOAVState;

import java.util.Collection;

/**
 *  Flyweight for a waitqueue.
 */
public class WaitqueueFlyweight extends WaitAbstractionFlyweight implements IWaitqueue
{
	//-------- attributes --------
	
	/** The plan. */
	protected Object	rplan;
	
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 *  @param handle	The element handle.
	 */
	private WaitqueueFlyweight(IOAVState state, Object scope, Object rplan)
	{
		super(state, scope, state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitqueuewa));
		// Hack!! Super constructor creates wa, when null. 
		if(state.getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitqueuewa)==null)
			state.setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitqueuewa, getHandle());
		this.rplan	= rplan;
		state.addExternalObjectUsage(rplan, this);
	}
	
	/**
	 *  Get or create a flyweight.
	 *  @return The flyweight.
	 */
	public static WaitqueueFlyweight getWaitqueueFlyweight(IOAVState state, Object scope, Object rplan)
	{
		Tuple	key	= new Tuple(rplan, IWaitqueue.class);
		BDIInterpreter ip = BDIAgentFeature.getInterpreter(state);
		WaitqueueFlyweight ret = (WaitqueueFlyweight)ip.getFlyweightCache(IWaitqueue.class, key);
		if(ret==null)
		{
			ret = new WaitqueueFlyweight(state, scope, rplan);
			ip.putFlyweightCache(IWaitqueue.class, key, ret);
		}
		return ret;
	}
	
	/**
	 *  Actual cleanup code.
	 *  When overriding this method, super.doCleanup() has to be called. 
	 */
	protected void	doCleanup()
	{
		if(rplan!=null)
		{
			getState().removeExternalObjectUsage(rplan, this);
			rplan	= null;
		}
		super.doCleanup();
	}
	
	//-------- waitqueue methods --------
	
	/**
	 *  Get all elements.
	 *  @return The elements.
	 */
	public Object[] getElements()
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					oarray = SFlyweightFunctionality.getElements(getState(), getScope(), rplan);

				}
			};
			return invoc.oarray;
		}
		else
		{
			return SFlyweightFunctionality.getElements(getState(), getScope(), rplan);
		}
	}

	/**
	 *  Get the next element.
	 *  @return The next element (or null if none).
	 */
	public Object removeNextElement()
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = SFlyweightFunctionality.removeNextElement(getState(), getScope(), rplan);
				}
			};
			return invoc.object;
		}
		else
		{
			return SFlyweightFunctionality.removeNextElement(getState(), getScope(), rplan);
		}
	}

	/**
	 *  Remove an element.
	 */
	public void removeElement(final Object element)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					getState().removeAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitqueueelements, ((ElementFlyweight)element).getHandle());
				}
			};
		}
		else
		{
			getState().removeAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitqueueelements, ((ElementFlyweight)element).getHandle());
		}
	}

	/**
	 *  Add a Goal. Overrides method for checking if rgoal is already finished.
	 *  @param goal The goal.
	 */
	public IWaitAbstraction addGoal(final IGoal goal)
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			new AgentInvocation()
			{
				public void run()
				{
					Object rgoal  = ((ElementFlyweight)goal).getHandle();
					// Directly add rgoal to waitqueue if already finished.
					if(goal.isFinished())
					{
						getState().addAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitqueueelements, rgoal);
					}
					else
					{
						SFlyweightFunctionality.addGoal(getOrCreateWaitAbstraction(), (ElementFlyweight)goal, getState(), getScope());
					}
				}
			};
			return this;		
		}
		else
		{
			Object rgoal  = ((ElementFlyweight)goal).getHandle();
			// Directly add rgoal to waitqueue if already finished.
			if(goal.isFinished())
			{
				getState().addAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitqueueelements, rgoal);
			}
			else
			{
				SFlyweightFunctionality.addGoal(getOrCreateWaitAbstraction(), (ElementFlyweight)goal, getState(), getScope());
			}
			return this;
		}
	}
	
	//-------- other methods --------
	
	/**
	 *  Get the number of events in the waitqueue.
	 *  @return The size of the waitqueue.
	 */
	public int	size()
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection coll = getState().getAttributeValues(rplan, OAVBDIRuntimeModel.plan_has_waitqueueelements);
					integer = coll==null? 0: coll.size();
				}
			};
			return invoc.integer;
		}
		else
		{
			Collection coll = getState().getAttributeValues(rplan, OAVBDIRuntimeModel.plan_has_waitqueueelements);
			return coll==null? 0: coll.size();
		}
	}

	/**
	 *  Test if the waitqueue is empty.
	 *  @return True, if empty.
	 */
	public boolean	isEmpty()
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Collection coll = getState().getAttributeValues(rplan, OAVBDIRuntimeModel.plan_has_waitqueueelements);
					bool = coll==null? true: coll.isEmpty();
				}
			};
			return invoc.bool;
		}
		else
		{
			Collection coll = getState().getAttributeValues(rplan, OAVBDIRuntimeModel.plan_has_waitqueueelements);
			return coll==null? true: coll.isEmpty();
		}
	}
	
	//-------- helpers --------
	
	/**
	 *  Get flyweight for element.
	 *  @param elem The element.
	 *  @return The flyweight.
	 * /
	public static ElementFlyweight getFlyweight(IOAVState state, Object rcapa, Object elem)
	{
		ElementFlyweight ret = null;
		OAVObjectType type = state.getType(elem);
		
		if(type.equals(OAVBDIRuntimeModel.goal_type))
		{
			ret = GoalFlyweight.getGoalFlyweight(state, rcapa, elem);
		}
		else if(type.equals(OAVBDIRuntimeModel.internalevent_type))
		{
			ret = InternalEventFlyweight.getInternalEventFlyweight(state, rcapa, elem);
		}
		else if(type.equals(OAVBDIRuntimeModel.messageevent_type))
		{
			ret = MessageEventFlyweight.getMessageEventFlyweight(state, rcapa, elem);
		}
		else if(type.isSubtype(OAVBDIRuntimeModel.changeevent_type))
		{
			String cetype = (String)state.getAttributeValue(elem, OAVBDIRuntimeModel.changeevent_has_type);
			if(OAVBDIRuntimeModel.CHANGEEVENT_GOALDROPPED.equals(cetype))
			{
				ret = GoalFlyweight.getGoalFlyweight(state, rcapa, state.getAttributeValue(elem, OAVBDIRuntimeModel.changeevent_has_element));
			}
			else
			{
				ret = new ChangeEventFlyweight(state, rcapa, elem);
			}
		}
		
		return ret;
	}*/
	
	/**
	 *  Get or create the waitabstraction.
	 *  @return The waitabstraction.
	 */
	protected Object getWaitAbstraction()
	{
		return getState().getAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitqueuewa);
	}
	
	/**
	 *  Create the waitabstraction.
	 *  @return The waitabstraction.
	 */
	protected Object createWaitAbstraction()
	{
		Object wa = super.createWaitAbstraction();
		getState().setAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitqueuewa, wa);
		return wa;
	}
	
}
