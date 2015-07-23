package jadex.bdiv3.runtime.impl;

import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanCapability;
import jadex.bdiv3.annotation.PlanReason;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.features.impl.BDIAgentFeature;
import jadex.bdiv3.features.impl.IInternalBDIAgentFeature;
import jadex.bdiv3.model.MBody;
import jadex.bdiv3.model.MElement;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bdiv3.runtime.ICapability;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IPojoComponentFeature;
import jadex.commons.MethodInfo;
import jadex.rules.eca.ChangeInfo;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
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
//		Class<?> mbd = body!=null? body: plan.getClass();
		MBody mbody = ((MPlan)rplan.getModelElement()).getBody();
		bodymethod = mbody.getBodyMethod(ia.getClassLoader()).getMethod(ia.getClassLoader());
		MethodInfo mi = mbody.getPassedMethod(ia.getClassLoader());
		if(mi!=null)
			passedmethod = mi.getMethod(ia.getClassLoader());
		mi = mbody.getFailedMethod(ia.getClassLoader());
		if(mi!=null)
			failedmethod = mi.getMethod(ia.getClassLoader());
		mi = mbody.getAbortedMethod(ia.getClassLoader());
		if(mi!=null)
			abortedmethod = mi.getMethod(ia.getClassLoader());
		
		if(plan!=null)
			injectElements();//ia.getComponentFeature(IPojoComponentFeature.class).getPojoAgent());
	}
	
	//-------- methods --------
	
	/**
	 * 
	 */
	public Object getBody()
	{
		if(plan==null)
		{
			try
			{
				// create plan  
//				if(plan==null)
//				{
					Constructor<?>[] cons = body.getDeclaredConstructors();
					for(Constructor<?> c: cons)
					{
						Object[] params = BDIAgentFeature
							.getInjectionValues(c.getParameterTypes(), c.getParameterAnnotations(), rplan.getModelElement(), null, rplan, null, ia);
						if(params!=null)
						{
							try
							{
								c.setAccessible(true);
								plan = c.newInstance(params);
								break;
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
						}						
					}
					if(plan==null)
						throw new RuntimeException("Plan body has no accessible constructor (maybe wrong args?): "+body);
//				}
				
				injectElements();
			}
			catch(RuntimeException e)
			{
				StringWriter	sw	= new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				ia.getLogger().warning("Plan '"+this+"' threw exception: "+sw);
				throw e;
			}
			catch(Exception e)
			{
				StringWriter	sw	= new StringWriter();
				e.printStackTrace(new PrintWriter(sw));
				ia.getLogger().warning("Plan '"+this+"' threw exception: "+sw);
				throw new RuntimeException(e);
			}
		}
		
		return plan;
	}

	/**
	 *  Get the plan.
	 *  @return The plan.
	 */
	public Object getPojoPlan()
	{
		return plan;
	}

	/**
	 *  Inject plan elements.
	 */
	protected void injectElements()
	{
		try
		{
			Class<?> bcl = body;
			while(!Object.class.equals(bcl))
			{
				Field[] fields = bcl.getDeclaredFields();
				for(Field f: fields)
				{
					if(f.isAnnotationPresent(PlanAPI.class))
					{
						f.setAccessible(true);
						f.set(plan, getRPlan());
					}
					else if(f.isAnnotationPresent(PlanCapability.class))
					{
						// Find capability based on model element (or use agent).
						String	capaname	= null;
						int idx = rplan.getModelElement().getName().lastIndexOf(MElement.CAPABILITY_SEPARATOR);
						if(idx!=-1)
						{
							capaname = rplan.getModelElement().getName().substring(0, idx);
						}

						// Pojo specific code.
						Object pojocapa	= capaname!=null
							? (ia.getComponentFeature0(IInternalBDIAgentFeature.class) instanceof BDIAgentFeature ? ((BDIAgentFeature)ia.getComponentFeature(IBDIAgentFeature.class)).getCapabilityObject(capaname) : null)
							: (ia.getComponentFeature0(IPojoComponentFeature.class)!=null ? ia.getComponentFeature(IPojoComponentFeature.class).getPojoAgent() : null);

						
						if(f.getType().isAssignableFrom(IInternalAccess.class))
						{
							f.setAccessible(true);
							f.set(plan, new CapabilityPojoWrapper(ia, pojocapa, capaname).getAgent());
						}
						else if(f.getType().isAssignableFrom(ICapability.class))
						{
							f.setAccessible(true);
							f.set(plan, new CapabilityPojoWrapper(ia, pojocapa, capaname));
						}
						else if(pojocapa!=null && f.getType().isAssignableFrom(pojocapa.getClass()))
						{
							f.setAccessible(true);
							f.set(plan, pojocapa);
						}
						else
						{
							throw new RuntimeException("Cannot set @PlanCapability: "+f+", capaname="+capaname+", pojocapa="+pojocapa);
						}
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
							else 
							{
								f.setAccessible(true);
								f.set(plan, r);
							}
						}
						else if(r instanceof ChangeEvent)
						{
							Class<?> ft = f.getType();
							f.setAccessible(true);
							if(ft.equals(ChangeEvent.class))
							{
								f.set(plan, r);
							}
							else
							{
								Object val = ((ChangeEvent)r).getValue();
								if(val instanceof ChangeInfo)
								{
									if(ft.equals(ChangeInfo.class))
									{
										f.set(plan, val);
									}
									else
									{
										f.set(plan, ((ChangeInfo<?>)val).getValue());
									}
								}
								else
								{
									f.set(plan, val);
								}
							}
						}
					}
				}
				
				bcl = bcl.getSuperclass();
			}
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
	 *  Invoke the body.
	 */
	public Object invokeBody(Object[] params) throws BodyAborted
	{
		try
		{
			getBody();
			bodymethod.setAccessible(true);
			return bodymethod.invoke(plan, params);
		}
		catch(Exception e)
		{
			Throwable	t	= e instanceof InvocationTargetException ? ((InvocationTargetException)e).getTargetException() : e;
			if(t instanceof Error)
			{
				throw (Error)t;
			}
			else if(t instanceof RuntimeException)
			{
				throw (RuntimeException)t;
			}
			else
			{
				throw new RuntimeException(t);
			}
//			throw t instanceof BodyAborted? (BodyAborted)t: t instanceof RuntimeException ? (RuntimeException)t : new RuntimeException(t);
		}
	}
	
	/**
	 *  Invoke the plan passed method.
	 */
	public Object invokePassed(Object[] params)
	{
		Object ret = null;
		if(passedmethod!=null)
		{
			try
			{
				passedmethod.setAccessible(true);
				ret = passedmethod.invoke(plan, params);			
			}
			catch(Exception e)
			{
				Throwable	t	= e instanceof InvocationTargetException ? ((InvocationTargetException)e).getTargetException() : e;
				throw t instanceof RuntimeException ? (RuntimeException)t : new RuntimeException(t);
			}
		}
		return ret;
	}

	/**
	 *  Invoke the plan failed method.
	 */
	public Object invokeFailed(Object[] params)
	{
		Object ret = null;
		if(failedmethod!=null)
		{
			try
			{
				failedmethod.setAccessible(true);
				ret = failedmethod.invoke(plan, params);			
			}
			catch(Exception e)
			{
				Throwable	t	= e instanceof InvocationTargetException ? ((InvocationTargetException)e).getTargetException() : e;
				throw t instanceof RuntimeException ? (RuntimeException)t : new RuntimeException(t);
			}
		}
		return ret;
	}

	/**
	 *  Invoke the plan aborted method.
	 */
	public Object invokeAborted(Object[] params)
	{
		Object ret = null;
		if(abortedmethod!=null)
		{
			try
			{
				abortedmethod.setAccessible(true);
				ret = abortedmethod.invoke(plan, params);			
			}
			catch(Exception e)
			{
				Throwable	t	= e instanceof InvocationTargetException ? ((InvocationTargetException)e).getTargetException() : e;
				throw t instanceof RuntimeException ? (RuntimeException)t : new RuntimeException(t);
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