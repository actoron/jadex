package jadex.bdi.runtime.interpreter;

import jadex.bdi.features.IBDIAgentFeature;
import jadex.bdi.features.impl.BDIAgentFeature;
import jadex.bdi.features.impl.IInternalBDIAgentFeature;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.rules.state.IOAVState;

import java.util.ArrayList;
import java.util.List;

/**
 *  Transferable information about a plan.
 */
public class PlanInfo	extends AbstractBDIInfo
{
	//-------- attributes --------
	
	/** The plan state (body, passed, failed, aborted). */
	protected String state;
	
	//-------- constructors --------
	
	/**
	 *  Create a new info.
	 */
	public PlanInfo()
	{
		// Bean constructor.
	}

	/**
	 *  Create a new info.
	 */
	public PlanInfo(Object id, String type, String state)
	{
		super(id, type);
		this.state	= state;
	}
	
	//--------- methods ---------
	
	/**
	 *  Return the state.
	 */
	public String getState()
	{
		return state;
	}

	/**
	 *  Set the state.
	 */
	public void setState(String state)
	{
		this.state = state;
	}


	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "PlanInfo(id="+id
			+ ", type=" + this.type
			+ ", state=" + this.state
			+ ")";
	}

	//-------- helper methods --------
	
	/**
	 *  Create an info object for a plan.
	 */
	public static PlanInfo	createPlanInfo(IOAVState state, Object plan, Object scope)
	{
		String	id	= plan.toString();
		if(id.indexOf('@')!=-1)	// 'plan_<num>@stateid'
		{
			id	= id.substring(0, id.indexOf('@'));
		}
		if(id.startsWith("goal_"))	// 'plan_<num>@stateid'
		{
			id	= id.substring(5);
		}
		Object	mgoal	= state.getAttributeValue(plan, OAVBDIRuntimeModel.element_has_model);
		String type	= (String)state.getAttributeValue(mgoal, OAVBDIMetaModel.modelelement_has_name);
		if(scope!=null)
		{
			IInternalBDIAgentFeature interpreter	= BDIAgentFeature.getInterpreter(state);
			List	path	= new ArrayList();
			if(interpreter.findSubcapability(interpreter.getAgent(), scope, path))
			{
				for(int i=path.size()-1; i>=0; i--)
				{
					type	= state.getAttributeValue(path.get(i), OAVBDIRuntimeModel.capabilityreference_has_name) + "." + type;
				}
			}
		}
		return new PlanInfo(id, type,
			(String)state.getAttributeValue(plan, OAVBDIRuntimeModel.plan_has_lifecyclestate));
	}
}
