package jadex.bdi.runtime.impl.eaflyweights;

import jadex.bdi.model.IMElement;
import jadex.bdi.model.impl.flyweights.MTriggerFlyweight;
import jadex.bdi.runtime.IEAGoal;
import jadex.bdi.runtime.IEAWaitAbstraction;
import jadex.bdi.runtime.IEAWaitqueue;
import jadex.bdi.runtime.impl.SFlyweightFunctionality;
import jadex.bdi.runtime.impl.flyweights.ElementFlyweight;
import jadex.bdi.runtime.interpreter.BDIInterpreter;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.Tuple;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVObjectType;

import java.util.Collection;

/**
 *  Flyweight for a waitqueue.
 */
public class EAWaitqueueFlyweight extends EAWaitAbstractionFlyweight implements IEAWaitqueue
{
	//-------- attributes --------
	
	/** The plan. */
	protected Object rplan;
	
	//-------- constructors --------
	
	/**
	 *  Create a new element flyweight.
	 *  @param state	The state.
	 *  @param scope	The scope handle.
	 *  @param handle	The element handle.
	 */
	private EAWaitqueueFlyweight(IOAVState state, Object scope, Object rplan)
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
	public static EAWaitqueueFlyweight getWaitqueueFlyweight(IOAVState state, Object scope, Object rplan)
	{
		Tuple	key	= new Tuple(rplan, IEAWaitqueue.class);
		BDIInterpreter ip = BDIInterpreter.getInterpreter(state);
		EAWaitqueueFlyweight ret = (EAWaitqueueFlyweight)ip.getFlyweightCache(IEAWaitqueue.class, key);
		if(ret==null)
		{
			ret = new EAWaitqueueFlyweight(state, scope, rplan);
			ip.putFlyweightCache(IEAWaitqueue.class, key, ret);
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
	public IFuture getElements()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(SFlyweightFunctionality.getElements(getState(), getScope(), rplan, true));
				}
			});
		}
		else
		{
			ret.setResult(SFlyweightFunctionality.getElements(getState(), getScope(), rplan, true));
		}
		
		return ret;
	}

	/**
	 *  Get the next element.
	 *  @return The next element (or null if none).
	 */
	public IFuture removeNextElement()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					ret.setResult(SFlyweightFunctionality.removeNextElement(getState(), getScope(), rplan, true));
				}
			});
		}
		else
		{
			ret.setResult(SFlyweightFunctionality.removeNextElement(getState(), getScope(), rplan, true));
		}
		
		return ret;
	}

	/**
	 *  Remove an element.
	 */
	public IFuture removeElement(final Object element)
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					getState().removeAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitqueueelements, ((ElementFlyweight)element).getHandle());
					ret.setResult(null);
				}
			});
		}
		else
		{
			getState().removeAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitqueueelements, ((ElementFlyweight)element).getHandle());
			ret.setResult(null);
		}
		
		return ret;
	}

	/**
	 *  Add a Goal. Overrides method for checking if rgoal is already finished.
	 *  @param goal The goal.
	 */
	public IEAWaitAbstraction addGoal(final IEAGoal goal)
	{
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Object rgoal  = ((ElementFlyweight)goal).getHandle();
					// Directly add rgoal to waitqueue if already finished.
					if(SFlyweightFunctionality.isFinished(getState(), ((ElementFlyweight)goal).getHandle()))
					{
						getState().addAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitqueueelements, rgoal);
					}
					else
					{
						SFlyweightFunctionality.addGoal(getOrCreateWaitAbstraction(), (ElementFlyweight)goal, getState(), getScope());
					}
				}
			});		
		}
		else
		{
			Object rgoal  = ((ElementFlyweight)goal).getHandle();
			// Directly add rgoal to waitqueue if already finished.
			if(SFlyweightFunctionality.isFinished(getState(), ((ElementFlyweight)goal).getHandle()))
			{
				getState().addAttributeValue(rplan, OAVBDIRuntimeModel.plan_has_waitqueueelements, rgoal);
			}
			else
			{
				SFlyweightFunctionality.addGoal(getOrCreateWaitAbstraction(), (ElementFlyweight)goal, getState(), getScope());
			}
		}
		
		return this;
	}
	
	//-------- other methods --------
	
	/**
	 *  Get the number of events in the waitqueue.
	 *  @return The size of the waitqueue.
	 */
	public IFuture size()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Collection coll = getState().getAttributeValues(rplan, OAVBDIRuntimeModel.plan_has_waitqueueelements);
					ret.setResult(coll==null? new Integer(0): new Integer(coll.size()));
				}
			});
		}
		else
		{
			Collection coll = getState().getAttributeValues(rplan, OAVBDIRuntimeModel.plan_has_waitqueueelements);
			ret.setResult(coll==null? new Integer(0): new Integer(coll.size()));
		}
		
		return ret;
	}

	/**
	 *  Test if the waitqueue is empty.
	 *  @return True, if empty.
	 */
	public IFuture isEmpty()
	{
		final Future ret = new Future();
		
		if(getInterpreter().isExternalThread())
		{
			getInterpreter().getAgentAdapter().invokeLater(new Runnable()
			{
				public void run()
				{
					Collection coll = getState().getAttributeValues(rplan, OAVBDIRuntimeModel.plan_has_waitqueueelements);
					boolean bool = coll==null? true: coll.isEmpty();
					ret.setResult(bool? Boolean.TRUE: Boolean.FALSE);
				}
			});
		}
		else
		{
			Collection coll = getState().getAttributeValues(rplan, OAVBDIRuntimeModel.plan_has_waitqueueelements);
			boolean bool = coll==null? true: coll.isEmpty();
			ret.setResult(bool? Boolean.TRUE: Boolean.FALSE);
		}
		
		return ret;
	}
	
	//-------- helpers --------
	
	/**
	 *  Get flyweight for element.
	 *  @param elem The element.
	 *  @return The flyweight.
	 */
	public static ElementFlyweight getFlyweight(IOAVState state, Object rcapa, Object elem)
	{
		ElementFlyweight ret = null;
		OAVObjectType type = state.getType(elem);
		
		if(type.equals(OAVBDIRuntimeModel.goal_type))
		{
			ret = EAGoalFlyweight.getGoalFlyweight(state, rcapa, elem);
		}
		else if(type.equals(OAVBDIRuntimeModel.internalevent_type))
		{
			ret = EAInternalEventFlyweight.getInternalEventFlyweight(state, rcapa, elem);
		}
		else if(type.equals(OAVBDIRuntimeModel.messageevent_type))
		{
			ret = EAMessageEventFlyweight.getMessageEventFlyweight(state, rcapa, elem);
		}
		else if(type.isSubtype(OAVBDIRuntimeModel.changeevent_type))
		{
			String cetype = (String)state.getAttributeValue(elem, OAVBDIRuntimeModel.changeevent_has_type);
			if(OAVBDIRuntimeModel.CHANGEEVENT_GOALDROPPED.equals(cetype))
			{
				ret = EAGoalFlyweight.getGoalFlyweight(state, rcapa, state.getAttributeValue(elem, OAVBDIRuntimeModel.changeevent_has_element));
			}
			else
			{
				ret = new EAChangeEventFlyweight(state, rcapa, elem);
			}
		}
		
		return ret;
	}
	
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
	
	//-------- element interface --------
	
	/**
	 *  Get the model element.
	 *  @return The model element.
	 */
	public IMElement getModelElement()
	{
		if(getInterpreter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
					Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
					object = new MTriggerFlyweight(getState(), mscope, me);
				}
			};
			return (IMElement)invoc.object;
		}
		else
		{
			Object me = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.element_has_model);
			Object mscope = getState().getAttributeValue(getScope(), OAVBDIRuntimeModel.element_has_model);
			return new MTriggerFlyweight(getState(), mscope, me);
		}
	}	
}
