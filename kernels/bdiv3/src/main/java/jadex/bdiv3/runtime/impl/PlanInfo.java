package jadex.bdiv3.runtime.impl;

import java.util.ArrayList;
import java.util.List;

import jadex.bdiv3.model.MPlan;
import jadex.bdiv3x.runtime.IParameter;


/**
 *  Transferable information about a plan.
 */
public class PlanInfo	extends AbstractBDIInfo
{
	//-------- attributes --------
	
	/** The plan state (body, passed, failed, aborted). */
	protected String state;
	
	/** The parameter (array of strings parameters). */
	protected List<ParameterInfo> paraminfos;
	
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
	 * /
	public PlanInfo(Object id, String type, String state)
	{
		super(id, type);
		this.state	= state;
	}*/
	
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
	public PlanInfo setState(String state)
	{
		this.state = state;
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
	public PlanInfo setParameterInfos(ParameterInfo[] paraminfos) 
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
	public PlanInfo addParameterInfo(ParameterInfo paraminfo)
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
		return "PlanInfo(id="+id
			+ ", type=" + this.type
			+ ", state=" + this.state
			+ ")";
	}

	//-------- helper methods --------
	
	/**
	 *  Create an info object for a plan.
	 */
	public static PlanInfo	createPlanInfo(RPlan plan)
	{
		String	id	= ""+plan.hashCode();
//		if(id.indexOf('@')!=-1)	// 'plan_<num>@stateid'
//		{
//			id	= id.substring(0, id.indexOf('@'));
//		}
//		if(id.startsWith("goal_"))	// 'plan_<num>@stateid'
//		{
//			id	= id.substring(5);
//		}
		
		MPlan mplan	= (MPlan)plan.getModelElement();
//		String type	= mplan.getName();
		String type	= RCapability.getBeautifiedName(mplan.getName());
		String paid = plan.getReason()!=null && plan.getReason() instanceof RParameterElement? ""+plan.getReason().hashCode(): null;
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
		//return new PlanInfo(id, type, plan.getLifecycleState().toString());
		PlanInfo pi = (PlanInfo)new PlanInfo()
			.setState(plan.getLifecycleState().toString())
			.setType(type)
			.setId(id)
			.setParentId(paid);
		
		IParameter[] ps = plan.getParameters();
		if(ps!=null)
		{
			for(IParameter p: ps)
			{
				pi.addParameterInfo(ParameterInfo.createParameterInfo(p, plan.getAgent().getClassLoader()));
			}
		}
		
		return pi;
	}
}
