package jadex.bdiv3.runtime.impl;

import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanPlan;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.model.MBody;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.model.MethodInfo;
import jadex.bridge.IInternalAccess;
import jadex.commons.SReflect;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 *  Plan body that is represented as complete class.
 */
public class ClassPlanBody extends AbstractPlanBody
{
	//-------- attributes --------
	
	/** The body class. */
	protected Class<?> body;
	
	/** The body instance. */
	protected Object plan;
	
	/** The body method. */
	protected Method bodymethod;
	
	/** The passed method. */
	protected Method passedmethod;

	/** The failed method. */
	protected Method failedmethod;

	/** The aborted method. */
	protected Method abortedmethod;
	
	//--------- constructors ---------
	
	/**
	 *  Create a new plan body.
	 */
	public ClassPlanBody(IInternalAccess ia, RPlan rplan, Class<?> body)
	{
		this(ia, rplan, body, null);
	}
	
	/**
	 *  Create a new plan body.
	 */
	public ClassPlanBody(IInternalAccess ia, RPlan rplan, Object plan)
	{
		this(ia, rplan, plan.getClass(), plan);
	}
	
	/**
	 *  Create a new plan body.
	 */
	public ClassPlanBody(IInternalAccess ia, RPlan rplan, Class<?> body, Object plan)
	{
		super(ia, rplan);
		this.body = body;
		this.plan = plan;
		Class<?> mbd = body!=null? body: plan.getClass();
		MBody bo=((MPlan)rplan.getModelElement()).getBody();
		bodymethod = ((MPlan)rplan.getModelElement()).getBody()
			.getBodyMethod(mbd).getMethod(ia.getClassLoader());
		MethodInfo mi = ((MPlan)rplan.getModelElement()).getBody().getPassedMethod(mbd);
		if(mi!=null)
			passedmethod = mi.getMethod(ia.getClassLoader());
		mi = ((MPlan)rplan.getModelElement()).getBody().getFailedMethod(mbd);
		if(mi!=null)
			failedmethod = mi.getMethod(ia.getClassLoader());
		mi = ((MPlan)rplan.getModelElement()).getBody().getAbortedMethod(mbd);
		if(mi!=null)
			abortedmethod = mi.getMethod(ia.getClassLoader());
	}
	
	//-------- methods --------
	
	/**
	 *  Invoke the body.
	 */
	public Object invokeBody(Object agent, Object[] params)
	{
		try
		{
			// create plan  
			if(plan==null)
			{
				Constructor<?>[] cons = body.getConstructors();
				for(Constructor<?> c: cons)
				{
					Class<?>[] ptypes = c.getParameterTypes();
					if(ptypes.length==0)
					{
						try
						{
							plan = c.newInstance(new Object[0]);
							break;
						}
						catch(Exception e)
						{
						}
					}
					if(ptypes.length==1 && SReflect.isSupertype(ptypes[0], agent.getClass()))
					{
						try
						{
							plan = c.newInstance(new Object[]{agent});
							break;
						}
						catch(Exception e)
						{
						}
					}
				}
				if(plan==null)
					throw new RuntimeException("Plan body has no empty constructor: "+body);
			}
			
			// inject plan elements
			Field[] fields = body.getDeclaredFields();
			for(Field f: fields)
			{
				if(f.isAnnotationPresent(PlanPlan.class))
				{
					f.setAccessible(true);
					f.set(plan, getRPlan());
				}
				else if(f.isAnnotationPresent(PlanCapability.class))
				{
					f.setAccessible(true);
					f.set(plan, agent);
				}
				else if(f.isAnnotationPresent(PlanReason.class))
				{
					Object r = getRPlan().getReason();
					if(r instanceof RProcessableElement)
					{
						Object reason = ((RProcessableElement)r).getPojoElement();
						if(reason!=null)
						{
							f.setAccessible(true);
							f.set(plan, reason);
						}
					}
				}
			}
			
			bodymethod.setAccessible(true);
			return bodymethod.invoke(plan, params);
		}
		catch(RuntimeException e)
		{
			throw e;
		}
		catch(Exception e)
		{
//			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Invoke the plan passed method.
	 */
	public Object invokePassed(Object agent, Object[] params)
	{
		Object ret = null;
		if(passedmethod!=null)
		{
			try
			{
				passedmethod.setAccessible(true);
				ret = passedmethod.invoke(plan, params);			
			}
			catch(RuntimeException e)
			{
				throw e;
			}
			catch(Exception e)
			{
//				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		return ret;
	}

	/**
	 *  Invoke the plan failed method.
	 */
	public Object invokeFailed(Object agent, Object[] params)
	{
		Object ret = null;
		if(failedmethod!=null)
		{
			try
			{
				failedmethod.setAccessible(true);
				ret = failedmethod.invoke(plan, params);			
			}
			catch(RuntimeException e)
			{
				throw e;
			}
			catch(Exception e)
			{
//				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		return ret;
	}

	/**
	 *  Invoke the plan aborted method.
	 */
	public Object invokeAborted(Object agent, Object[] params)
	{
		Object ret = null;
		if(abortedmethod!=null)
		{
			try
			{
				abortedmethod.setAccessible(true);
				ret = abortedmethod.invoke(plan, params);			
			}
			catch(RuntimeException e)
			{
				throw e;
			}
			catch(Exception e)
			{
//				e.printStackTrace();
				throw new RuntimeException(e);
			}
		}
		return ret;
	}
	
	/**
	 *  Get the passed parameters.
	 */
	public Class<?>[] getPassedParameterTypes()
	{
		return passedmethod==null? null: passedmethod.getParameterTypes();
	}

	/**
	 *  Get the failed parameters.
	 */
	public Class<?>[] getFailedParameterTypes()
	{
		return failedmethod==null? null: failedmethod.getParameterTypes();
		
	}

	/**
	 *  Get the aborted parameters.
	 */
	public Class<?>[] getAbortedParameterTypes()
	{
		return abortedmethod==null? null: abortedmethod.getParameterTypes();		
	}
	
	/**
	 *  Get the body parameter types.
	 */
	public Class<?>[] getBodyParameterTypes()
	{
		return bodymethod.getParameterTypes();
	}
}