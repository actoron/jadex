package jadex.bdi.runtime.interpreter;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.commons.SUtil;
import jadex.rules.state.IOAVState;

import java.util.ArrayList;
import java.util.List;

/**
 *  Transferable information about a plan.
 */
public class PlanInfo
{
	//-------- attributes --------
	
	/** The plan id. */
	protected Object	id;
	
	/** The goal type (e.g. patrol_plan). */
	protected String	type;
	
	/** The plan state (body, passed, failed, aborted). */
	protected String state;
	
	//-------- constructors --------
	
	/**
	 *  Create a new goal info.
	 */
	public PlanInfo()
	{
		// Bean constructor.
	}

	/**
	 *  Create a new goal info.
	 */
	public PlanInfo(Object id, String type, String state)
	{
		this.id	= id;
		this.type	= type;
		this.state	= state;
	}
	
	//--------- methods ---------
	
	/**
	 *  Return the id.
	 */
	public Object getId()
	{
		return id;
	}

	/**
	 *  Set the id.
	 */
	public void setId(Object id)
	{
		this.id = id;
	}

	/**
	 *  Return the type.
	 */
	public String getType()
	{
		return type;
	}

	/**
	 *  Set the type.
	 */
	public void setType(String type)
	{
		this.type = type;
	}

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
		return "GoalInfo(id="+id
			+ ", type=" + this.type
			+ ", state=" + this.state
			+ ")";
	}

	/**
	 *  Test if two objects are equal.
	 */
	public boolean	equals(Object obj)
	{
		return obj instanceof PlanInfo && SUtil.equals(((PlanInfo)obj).id, id);
	}
	
	/**
	 *  Get the hashcode
	 */
	public int	hashCode()
	{
		return 31+id.hashCode();
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
			BDIInterpreter interpreter	= BDIInterpreter.getInterpreter(state);
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
