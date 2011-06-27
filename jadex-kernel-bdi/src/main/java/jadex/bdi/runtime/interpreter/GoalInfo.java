package jadex.bdi.runtime.interpreter;

import jadex.bdi.model.OAVBDIMetaModel;
import jadex.commons.SUtil;
import jadex.rules.state.IOAVState;

import java.util.ArrayList;
import java.util.List;

/**
 *  Transferable information about a goal.
 */
public class GoalInfo
{
	//-------- attributes --------
	
	/** The goal id. */
	protected Object	id;
	
	/** The goal kind (e.g. achieve). */
	protected String	kind;
	
	/** The goal type (e.g. cleanup_waste). */
	protected String	type;
	
	/** The life cycle state. */
	protected String lifecyclestate;
	
	/** The processing state. */
	protected String processingstate;
	
	//-------- constructors --------
	
	/**
	 *  Create a new goal info.
	 */
	public GoalInfo()
	{
		// Bean constructor.
	}

	/**
	 *  Create a new goal info.
	 */
	public GoalInfo(Object id, String kind, String type, String lifecyclestate, String processingstate)
	{
		this.id	= id;
		this.kind	= kind;
		this.type	= type;
		this.lifecyclestate	= lifecyclestate;
		this.processingstate	= processingstate;
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
	 *  Return the kind.
	 */
	public String getKind()
	{
		return kind;
	}

	/**
	 *  Set the kind.
	 */
	public void setKind(String kind)
	{
		this.kind = kind;
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
	 *  Return the life cycle state.
	 */
	public String getLifecycleState()
	{
		return lifecyclestate;
	}

	/**
	 *  Set the life cycle state.
	 */
	public void setLifecycleState(String lifecyclestate)
	{
		this.lifecyclestate = lifecyclestate;
	}

	/**
	 *  Return the processing state.
	 */
	public String getProcessingState()
	{
		return processingstate;
	}

	/**
	 *  Set the processing state.
	 */
	public void setProcessingState(String processingstate)
	{
		this.processingstate = processingstate;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return "GoalInfo(id="+id
			+ ", kind=" + this.kind 
			+ ", type=" + this.type
			+ ", lifecyclestate=" + this.lifecyclestate 
			+ ", processingstate=" + this.processingstate
			+ ")";
	}

	/**
	 *  Test if two objects are equal.
	 */
	public boolean	equals(Object obj)
	{
		return obj instanceof GoalInfo && SUtil.equals(((GoalInfo)obj).id, id);
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
	 *  Create an info object for a goal.
	 */
	public static GoalInfo	createGoalInfo(IOAVState state, Object goal, Object scope)
	{
		String	id	= goal.toString();
		if(id.indexOf('@')!=-1)	// 'goal_<num>@stateid'
		{
			id	= id.substring(0, id.indexOf('@'));
		}
		if(id.startsWith("goal_"))	// 'goal_<num>@stateid'
		{
			id	= id.substring(5);
		}
		Object	mgoal	= state.getAttributeValue(goal, OAVBDIRuntimeModel.element_has_model);
		String	kind	= state.getType(mgoal).getName();
		kind	= kind.substring(1, kind.length()-4); // 'm<xyz>goal'
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
		return new GoalInfo(id, kind, type,
			(String)state.getAttributeValue(goal, OAVBDIRuntimeModel.goal_has_lifecyclestate),
			(String)state.getAttributeValue(goal, OAVBDIRuntimeModel.goal_has_processingstate));
	}
}
