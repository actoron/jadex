package jadex.bdiv3.runtime.impl;

import java.util.ArrayList;
import java.util.List;

import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MParameter;
import jadex.bdiv3x.runtime.IParameter;
import jadex.bridge.ClassInfo;
import jadex.bridge.modelinfo.UnparsedExpression;


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
	
	/** The parameter (array of strings parameters). */
	protected List<ParameterInfo> paraminfos;
	
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
	 * /
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
	public GoalInfo setKind(String kind)
	{
		this.kind = kind;
		return this;
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
	public GoalInfo setLifecycleState(String lifecyclestate)
	{
		this.lifecyclestate = lifecyclestate;
		return this;
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
	public GoalInfo setProcessingState(String processingstate)
	{
		this.processingstate = processingstate;
		return this;
	}
	
	/**
	 * @return the paraminfos
	 */
	public ParameterInfo[] getParameterInfos() 
	{
		return paraminfos==null? new ParameterInfo[0]: paraminfos.toArray(new ParameterInfo[0]);
	}

	/**
	 *  Set the parameters 
	 *  @param paraminfos the paraminfos to set
	 */
	public GoalInfo setParameterInfos(ParameterInfo[] paraminfos) 
	{
		this.paraminfos = new ArrayList<ParameterInfo>();
		if(paraminfos!=null)
		{
			for(int i=0; i<paraminfos.length; i++)
				this.paraminfos.add(paraminfos[i]);
		}
		return this;
	}
	
	/**
	 *  Add a parameter.
	 */
	public GoalInfo addParameterInfo(ParameterInfo paraminfo)
	{
		if(this.paraminfos==null)
			this.paraminfos = new ArrayList<ParameterInfo>();
		this.paraminfos.add(paraminfo);
		return this;
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
		String paid = goal.getParent()!=null? ""+goal.getParent().hashCode(): null;
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
		//return new GoalInfo(id, kind, type, goal.getLifecycleState().toString(), goal.getProcessingState().toString());
		GoalInfo gi = (GoalInfo)new GoalInfo()
			.setLifecycleState(goal.getLifecycleState().toString())
			.setProcessingState(goal.getProcessingState().toString())
			.setKind(kind)
			.setId(id)
			.setParentId(paid)
			.setType(type);
	
		IParameter[] ps = goal.getParameters();
		if(ps!=null)
		{
			for(IParameter p: ps)
			{
				gi.addParameterInfo(ParameterInfo.createParameterInfo(p, goal.getAgent().getClassLoader()));
			}
		}
		
		return gi;
	}
}
