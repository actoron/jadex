package jadex.bdiv3.runtime.impl;

import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanPlan;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.model.MPlan;
import jadex.bridge.IInternalAccess;

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
	
	//--------- constructors ---------
	
	/**
	 *  Create a new plan body.
	 */
	public ClassPlanBody(IInternalAccess ia, RPlan rplan, Class<?> body)
	{
		super(ia, rplan);
		this.body = body;
		bodymethod = ((MPlan)rplan.getModelElement()).getBody()
			.getBodyMethod(body).getMethod(ia.getClassLoader());
	}
	
	/**
	 *  Create a new plan body.
	 */
	public ClassPlanBody(IInternalAccess ia, RPlan rplan, Object plan)
	{
		super(ia, rplan);
		this.body = plan.getClass();
		this.plan = plan;
		bodymethod = ((MPlan)rplan.getModelElement()).getBody()
			.getBodyMethod(plan.getClass()).getMethod(ia.getClassLoader());
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
				plan = body.newInstance();
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
		catch(Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Get the body parameter types.
	 */
	public Class<?>[] getBodyParameterTypes()
	{
		return bodymethod.getParameterTypes();
	}
}