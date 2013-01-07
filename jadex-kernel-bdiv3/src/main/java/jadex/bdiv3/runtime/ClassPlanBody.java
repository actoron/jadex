package jadex.bdiv3.runtime;

import jadex.bdiv3.BDIClassReader;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanPlan;
import jadex.bridge.IInternalAccess;
import jadex.micro.MicroClassReader;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 
 */
public class ClassPlanBody extends AbstractPlanBody
{
	/** The method. */
	protected Class<?> body;
	protected Object plan;
	
	/** The body method. */
	protected Method bodymethod;
	
	
	/**
	 * 
	 */
	public ClassPlanBody(IInternalAccess ia, RPlan rplan, Class<?> body)
	{
		super(ia, rplan);
		this.body = body;
		
		Method[] ms = body.getDeclaredMethods();
		for(Method m: ms)
		{
//			if(MicroClassReader.isAnnotationPresent(m, PlanBody.class, ia.getClassLoader()))
			if(m.isAnnotationPresent(PlanBody.class))
			{
				bodymethod = m;
				break;
			}
		}
		if(bodymethod==null)
		{
			throw new RuntimeException("Plan has no body method: "+body);
		}
	}
	
	/**
	 * 
	 */
	public Object executeBody(Object agent, Object[] params)
	{
		try
		{
			// create plan class 
			plan = body.newInstance();
			
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
			}
			
			bodymethod.setAccessible(true);
			return bodymethod.invoke(plan, params);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 */
	public Class<?>[] getBodyParameterTypes()
	{
		return bodymethod.getParameterTypes();
	}
}