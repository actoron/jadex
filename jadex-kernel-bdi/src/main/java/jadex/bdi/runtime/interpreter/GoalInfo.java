package jadex.bdi.runtime.interpreter;

import jadex.bdi.features.IBDIAgentFeature;
import jadex.bdi.features.impl.BDIAgentFeature;
import jadex.bdi.features.impl.IInternalBDIAgentFeature;
import jadex.bdi.model.OAVBDIMetaModel;
import jadex.rules.state.IOAVState;

import java.util.ArrayList;
import java.util.List;

/**
 *  Transferable information about a goal.
 */
public class GoalInfo	extends AbstractBDIInfo
{
	//-------- attributes --------
	
	/** The goal kind (e.g. achieve). */
	protected String	kind;
	
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
		super(id, type);
		this.kind	= kind;
		this.lifecyclestate	= lifecyclestate;
		this.processingstate	= processingstate;
	}
	
	//--------- methods ---------
	
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
		return new GoalInfo(id, kind, type,
			(String)state.getAttributeValue(goal, OAVBDIRuntimeModel.goal_has_lifecyclestate),
			(String)state.getAttributeValue(goal, OAVBDIRuntimeModel.goal_has_processingstate));
	}
}
