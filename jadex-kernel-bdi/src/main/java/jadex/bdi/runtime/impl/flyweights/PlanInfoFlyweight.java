package jadex.bdi.runtime.impl.flyweights;

import jadex.bdi.model.IMElement;
import jadex.bdi.runtime.ICandidateInfo;
import jadex.bdi.runtime.IElement;
import jadex.bdi.runtime.IParameterElement;
import jadex.bdi.runtime.IPlan;
import jadex.bdi.runtime.impl.SFlyweightFunctionality;
import jadex.bdi.runtime.interpreter.OAVBDIRuntimeModel;
import jadex.bdi.runtime.interpreter.PlanRules;
import jadex.rules.state.IOAVState;

import java.util.Collection;

/**
 *  Flyweight for plan infos.
 */
public class PlanInfoFlyweight extends ElementFlyweight implements ICandidateInfo
{
	//-------- attributes --------
	
	/** The processable element. */
	protected Object rpe;
	
	//-------- constructors --------
	
	/**
	 *  Create a new plan info flyweight.
	 */
	public PlanInfoFlyweight(IOAVState state, Object scope, Object handle, Object rpe)
	{
		super(state, scope, handle);
		this.rpe = rpe;
	}
	
	//-------- plan interface --------
	
	/**
	 *  Get the plan instance.
	 *  @return	The plan instance.
	 */
	public IPlan getPlan()
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
		{
			AgentInvocation invoc = new AgentInvocation()
			{
				public void run()
				{
					Object plan = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.mplancandidate_has_plan);
					if(plan==null)
					{
						Object mplan = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.mplancandidate_has_mplan);
						Collection	bindings = getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.mplancandidate_has_bindings);
						plan = PlanRules.instantiatePlan(getState(), getScope(), mplan, null, rpe, bindings, null, null);
						getState().setAttributeValue(getHandle(), OAVBDIRuntimeModel.mplancandidate_has_plan, plan);
					}
					object = PlanFlyweight.getPlanFlyweight(getState(), getScope(), plan);
				}
			};
			return (IPlan)invoc.object;
		}
		else
		{
			Object plan = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.mplancandidate_has_plan);
			if(plan==null)
			{
				Object mplan = getState().getAttributeValue(getHandle(), OAVBDIRuntimeModel.mplancandidate_has_mplan);
				Collection	bindings = getState().getAttributeValues(getHandle(), OAVBDIRuntimeModel.mplancandidate_has_bindings);
				plan = PlanRules.instantiatePlan(getState(), getScope(), mplan, null, rpe, bindings, null, null);
				getState().setAttributeValue(getHandle(), OAVBDIRuntimeModel.mplancandidate_has_plan, plan);
			}
			return PlanFlyweight.getPlanFlyweight(getState(), getScope(), plan);
		}
	}
	
	/**
	 *  Get the element this 
	 *  candidate was selected for.
	 *  @return	The element.
	 */
	public IElement getElement()
	{
		if(getInterpreter().getComponentAdapter().isExternalThread())
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
