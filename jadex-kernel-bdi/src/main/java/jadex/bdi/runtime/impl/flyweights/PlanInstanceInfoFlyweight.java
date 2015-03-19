package jadex.bdi.runtime.impl.flyweights;

import jadex.bdi.model.IMElement;
import jadex.bdi.runtime.ICandidateInfo;
import jadex.bdi.runtime.IElement;
import jadex.bdi.runtime.IParameterElement;
import jadex.bdi.runtime.IPlan;
import jadex.bdi.runtime.impl.SFlyweightFunctionality;
import jadex.rules.state.IOAVState;

/**
 *  Flyweight for plan instance infos.
 */
public class PlanInstanceInfoFlyweight extends ElementFlyweight implements ICandidateInfo
{
	//-------- attributes --------
	
	/** The processable element. */
	protected Object rpe;
	
	//-------- constructors --------
	
	/**
	 *  Create a new plan info flyweight.
	 */
	public PlanInstanceInfoFlyweight(IOAVState state, Object scope, Object handle, Object rpe)
	{
		super(state, scope, handle);
		this.rpe = rpe;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the plan instance.
	 *  @return	The plan instance.
	 */
	public IPlan getPlan()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = PlanFlyweight.getPlanFlyweight(getState(), getScope(), getHandle());
				}
			};
			return (IPlan)invoc.object;
		}
		else
		{
			return PlanFlyweight.getPlanFlyweight(getState(), getScope(), getHandle());
		}
	}
	
	/**
	 *  Get the processable element this 
	 *  candidate was selected for.
	 *  @return	The processable element.
	 */
	public IElement getElement()
	{
		if(isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					object = SFlyweightFunctionality.getFlyweight(getState(), getScope(), rpe);
				}
			};
			return (IParameterElement)invoc.object;
		}
		else
		{
			return SFlyweightFunctionality.getFlyweight(getState(), getScope(), rpe);
		}
	}
	
	//-------- element interface --------
	
	/**
	 *  Get the model element.
	 *  @return The model element.
	 */
	public IMElement getModelElement()
	{
		throw new RuntimeException("Element has no model: "+this);
	}
}
