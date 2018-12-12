package jadex.bdiv3.runtime.impl;

import jadex.bdiv3.model.MGoal;


/**
 *  Transferable information about a goal.
 */
public class GoalInfo	extends AbstractBDIInfo
{
	//-------- attributes --------
	
	/** The goal kind (e.g. achieve). */
	protected String kind;
	
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
	public static GoalInfo	createGoalInfo(RGoal goal)
	{
		String	id	= ""+goal.hashCode();
//		if(id.indexOf('@')!=-1)	// 'goal_<num>@stateid'
//		{
//			id	= id.substring(0, id.indexOf('@'));
//		}
//		if(id.startsWith("goal_"))	// 'goal_<num>@stateid'
//		{
//			id	= id.substring(5);
//		}
		
		MGoal mgoal = (MGoal)goal.getModelElement();
//		String	kind	= state.getType(mgoal).getName();
//		kind	= kind.substring(1, kind.length()-4); // 'm<xyz>goal'
		String kind = "unknown";
//		String type	= mgoal.getName();
		String type	= RCapability.getBeautifiedName(mgoal.getName());
//		if(scope!=null)
//		{
//			BDIInterpreter interpreter	= BDIInterpreter.getInterpreter(state);
//			List	path	= new ArrayList();
//			if(interpreter.findSubcapability(interpreter.getAgent(), scope, path))
//			{
//				for(int i=path.size()-1; i>=0; i--)
//				{
//					type	= state.getAttributeValue(path.get(i), OAVBDIRuntimeModel.capabilityreference_has_name) + "." + type;
//				}
//			}
//		}
		return new GoalInfo(id, kind, type, goal.getLifecycleState().toString(), goal.getProcessingState().toString());
	}
}
