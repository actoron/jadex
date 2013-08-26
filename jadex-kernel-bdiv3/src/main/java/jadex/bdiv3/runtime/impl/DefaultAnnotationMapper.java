package jadex.bdiv3.runtime.impl;

import jadex.bdiv3.model.BDIModel;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MParameter;
import jadex.commons.MethodInfo;

import java.lang.reflect.Method;
import java.util.List;

/**
 *  Service mapper that uses the annotations in a goal.
 *  If not present it will try the following:
 *  - use declared goal parameters as service parameters
 *  - use declared goal result
 */
public class DefaultAnnotationMapper<T> implements IServiceParameterMapper<T>
{
	/** The interpreter. */
	protected BDIAgentInterpreter interpreter;
	
	/** The service name. */
	protected String sername;
	
	/**
	 * 
	 */
	public DefaultAnnotationMapper(String sername, BDIAgentInterpreter interpreter)
	{
		this.sername = sername;
		this.interpreter = interpreter;
	}
	
	/**
	 *  Create service parameters.
	 *  @param goal The pojo goal.
	 *  @param m The service method called.
	 *  @return The parameter array for the service call.
	 */
	public Object[] createServiceParameters(T goal, Method m)
	{
		Object[] ret = null;
		
		BDIModel model = interpreter.getBDIModel();
		final MGoal mgoal = model.getCapability().getGoal(goal.getClass().getName());
		MethodInfo mi = mgoal.getServiceParameterMapping(sername==null? "": sername);
		
		if(mi!=null)
		{
			try
			{
				Method me = mi.getMethod(model.getClassloader());
				ret = (Object[])me.invoke(goal, new Object[]{m});
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
		// Try using goal parameters if nothing is specified
		else
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
					ret[i] = p.getValue(goal, model.getClassloader());
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
	public void handleServiceResult(T goal, Method m, Object result)
	{
		RGoal rgoal = interpreter.getCapability().getRGoal(goal);
		final MGoal mgoal = rgoal.getMGoal();
		MethodInfo mi = mgoal.getServiceParameterMapping(sername==null? "": sername);
		
		if(mi!=null)
		{
			try
			{
				Method me = mi.getMethod(interpreter.getClassLoader());
				me.invoke(goal, new Object[]{m, result});
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
		// Try using goal result if nothing is specified
		else
		{
			rgoal.setGoalResult(result, interpreter.getClassLoader());
		}
	}
}
