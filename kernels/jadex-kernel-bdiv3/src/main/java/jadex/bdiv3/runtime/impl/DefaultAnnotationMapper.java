package jadex.bdiv3.runtime.impl;

import java.lang.reflect.Method;
import java.util.List;

import jadex.bdiv3.features.impl.IInternalBDIAgentFeature;
import jadex.bdiv3.model.IBDIModel;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MParameter;
import jadex.bdiv3.model.MPlan;
import jadex.bridge.IInternalAccess;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;

/**
 *  Service mapper that uses the annotations in a goal.
 *  If not present it will try the following:
 *  - use declared goal parameters as service parameters
 *  - use declared goal result
 */
public class DefaultAnnotationMapper<T> implements IServiceParameterMapper<T>
{
	/** The agent. */
	protected IInternalAccess agent;
	
	/** The service name. */
	protected String sername;
	
	/**
	 * 
	 */
	public DefaultAnnotationMapper(String sername, IInternalAccess agent)
	{
		this.sername = sername;
		this.agent = agent;
	}
	
	/**
	 *  Create service parameters.
	 *  @param goal The pojo goal.
	 *  @param m The service method called.
	 *  @return The parameter array for the service call.
	 */
	public Object[] createServiceParameters(T goal, Method m, RPlan plan)
	{
		Object[] ret = null;
		
		IBDIModel model = (IBDIModel)agent.getComponentFeature(IInternalBDIAgentFeature.class).getBDIModel();
		
		boolean done = false;
		
		MGoal mgoal;
		// Use plan parameters in xml case because they should exactly match the call in order and type
		if(goal instanceof RGoal)
		{
			mgoal = (MGoal)((RGoal)goal).getModelElement();
			MPlan mplan = (MPlan)plan.getModelElement();
			List<MParameter> params = mplan.getParameters();
			if(params==null)
			{
				ret = new Object[0];
				done = true;
			}
			else
			{
				ret = new Object[m.getParameterTypes().length];
				int cnt = 0;
				for(MParameter mparam: params)
				{
					if(MParameter.Direction.IN.equals(mparam.getDirection())
						|| MParameter.Direction.INOUT.equals(mparam.getDirection()))
					{
						ret[cnt++] = plan.getParameter(mparam.getName()).getValue();
					}
				}
				done = true;
			}
		}
		else
		{
			mgoal = model.getCapability().getGoal(goal.getClass().getName());
			MethodInfo mi = mgoal.getServiceParameterMapping(sername==null? "": sername);
		
			if(mi!=null)
			{
				try
				{
					Method me = mi.getMethod(agent.getClassLoader());
					ret = (Object[])me.invoke(goal, new Object[]{m});
					done = true;
				}
				catch(RuntimeException e)
				{
					throw e;
				}
				catch(Exception e)
				{
					throw new RuntimeException(e);
				}
			}
		}
		
		// Try using goal parameters if nothing is specified
		if(!done)
		{
			List<MParameter> params = mgoal.getParameters();
			if(params==null)
			{
				ret = new Object[0];
			}
			else
			{
				ret = new Object[params.size()];
				for(int i=0; i<params.size(); i++)
				{
					MParameter p = params.get(i);
					ret[i] = p.getValue(goal, agent.getClassLoader());
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Create service result.
	 *  @param goal The goal.
	 *  @param m The method.
	 *  @param result The service call result.
	 */
	public void handleServiceResult(T goal, Method m, Object result, RPlan plan)
	{
		boolean done = false;
		
		RGoal rgoal;
		// Use out plan parameter in case of xml
		if(goal instanceof RGoal)
		{
			rgoal = (RGoal)goal;
			MPlan mplan = (MPlan)plan.getModelElement();
			List<MParameter> params = mplan.getParameters();
			if(params!=null)
			{
				for(int i=0; i<params.size(); i++)
				{
					MParameter mparam = params.get(i);
					if(MParameter.Direction.OUT.equals(mparam.getDirection())
						|| MParameter.Direction.INOUT.equals(mparam.getDirection()))
					{
						if(SReflect.isSupertype(mparam.getType(agent.getClassLoader()), result.getClass()))
						{
							plan.getParameter(mparam.getName()).setValue(result);
							done = true;
							break;
						}
					}
				}
			}
		}
		else
		{
			rgoal = agent.getComponentFeature(IInternalBDIAgentFeature.class).getCapability().getRGoal(goal);
			MGoal mgoal = rgoal.getMGoal();
			MethodInfo mi = mgoal.getServiceParameterMapping(sername==null? "": sername);
			
			if(mi!=null)
			{
				try
				{
					Method me = mi.getMethod(agent.getClassLoader());
					me.invoke(goal, new Object[]{m, result});
					done = true;
				}
				catch(RuntimeException e)
				{
					throw e;
				}
				catch(Exception e)
				{
					throw new RuntimeException(e);
				}
			}
		}			
		
		// Try using goal result if nothing is specified
		if(!done)
		{
			rgoal.setGoalResult(result, agent.getClassLoader());
		}
	}
}
