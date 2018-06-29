package jadex.bdiv3.model;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

import jadex.bdiv3.BDIClassReader;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanContextCondition;
import jadex.bdiv3.annotation.PlanFailed;
import jadex.bdiv3.annotation.PlanPassed;
import jadex.bdiv3.annotation.PlanPrecondition;
import jadex.bdiv3.runtime.impl.ServiceCallPlan;
import jadex.bridge.ClassInfo;
import jadex.commons.MethodInfo;
import jadex.micro.MicroClassReader.DummyClassLoader;

/**
 *  The plan mbody.
 */
public class MBody
{
	protected static final MethodInfo MI_NOTFOUND = new MethodInfo();
	
	/** The body as seperate class. */
	protected MethodInfo method;
	
	/** The body as seperate class. */
	protected ClassInfo clazz;

	/** The body as required service. */
	protected String servicename;
	
	/** The body as required service. */
	protected String servicemethodname;
	
	/** The parameter mapper. */
	protected ClassInfo mapperclass;
	
	/** The body as component type. */
	protected String component;
	
	// double check locking requires volatile 
	// http://www.cs.umd.edu/~pugh/java/memoryModel/DoubleCheckedLocking.html 
	
	/** The body method cached for speed. */
	protected volatile MethodInfo bodymethod;

	/** The passed method cached for speed. */
	protected volatile MethodInfo passedmethod;

	/** The failed method cached for speed. */
	protected volatile MethodInfo failedmethod;

	/** The aborted method cached for speed. */
	protected volatile MethodInfo abortedmethod;
	
	/** The precondition method cached for speed. */
	protected volatile MethodInfo preconditionmethod;

	/** The precondition method cached for speed. */
	protected volatile MethodInfo contextconditionmethod;

	/**
	 *	Bean Constructor. 
	 */
	public MBody()
	{
	}
	
	/**
	 *  Create a new mbody.
	 */
	public MBody(MethodInfo method, ClassInfo clazz, String servicename,
		String servicemethodname, ClassInfo mapperclass, String component)
	{
		this.method = method;
		this.clazz = clazz;
		this.servicename = servicename;
		this.servicemethodname = servicemethodname;
		this.mapperclass = mapperclass;
		this.component	= component;
	}

	/**
	 *  Get the method.
	 *  @return The method.
	 */
	public MethodInfo getMethod()
	{
		return method;
	}

	/**
	 *  Set the method.
	 *  @param method The method to set.
	 */
	public void setMethod(MethodInfo method)
	{
		this.method = method;
	}

	/**
	 *  Get the clazz.
	 *  @return The clazz.
	 */
	public ClassInfo getClazz()
	{
		if(clazz==null && getServiceName()!=null)
			clazz = new ClassInfo(ServiceCallPlan.class.getName());
		return clazz;
	}

	/**
	 *  Set the clazz.
	 *  @param clazz The clazz to set.
	 */
	public void setClazz(ClassInfo clazz)
	{
		this.clazz = clazz;
	}

	/**
	 *  Get the servicename.
	 *  @return The servicename.
	 */
	public String getServiceName()
	{
		return servicename;
	}

	/**
	 *  Set the servicename.
	 *  @param servicename The servicename to set.
	 */
	public void setServiceName(String servicename)
	{
		this.servicename = servicename;
	}

	/**
	 *  Get the servicemethodname.
	 *  @return The servicemethodname.
	 */
	public String getServiceMethodName()
	{
		return servicemethodname;
	}

	/**
	 *  Set the servicemethodname.
	 *  @param servicemethodname The servicemethodname to set.
	 */
	public void setServiceMethodName(String servicemethodname)
	{
		this.servicemethodname = servicemethodname;
	}
	
	/**
	 *  Get the mapperclass.
	 *  @return The mapperclass.
	 */
	public ClassInfo getMapperClass()
	{
		return mapperclass;
	}

	/**
	 *  Set the mapperclass.
	 *  @param mapperclass The mapperclass to set.
	 */
	public void setMapperclass(ClassInfo mapperclass)
	{
		this.mapperclass = mapperclass;
	}
	
	/**
	 *  Get the component.
	 *  @return The component.
	 */
	public String getComponent()
	{
		return component;
	}

	/**
	 *  Set the component.
	 *  @param component The component to set.
	 */
	public void setComponent(String component)
	{
		this.component = component;
	}


	/**
	 * 
	 */
	public MethodInfo getBodyMethod(ClassLoader cl)
	{
		if(clazz!=null)
		{
			if(bodymethod==null)
			{
				synchronized(this)
				{
					if(bodymethod==null)
					{
						Class<?> body = clazz.getType(cl);
						bodymethod = getMethod(body, PlanBody.class, cl);
						if(bodymethod==null)
							throw  new RuntimeException("Plan has no body method: "+body);
					}
				}
			}
		}
		
		return bodymethod;
	}
	
	/**
	 * 
	 */
	public MethodInfo getPassedMethod(ClassLoader cl)
	{
		if(clazz!=null)
		{
			if(passedmethod==null && !MI_NOTFOUND.equals(passedmethod))
			{
				synchronized(this)
				{
					if(passedmethod==null && !MI_NOTFOUND.equals(passedmethod))
					{
						Class<?> body = clazz.getType(cl);
						passedmethod = getMethod(body, PlanPassed.class, cl);
						if(passedmethod==null)
							passedmethod = MI_NOTFOUND;
					}
				}
			}
		}
		
		return MI_NOTFOUND.equals(passedmethod)? null: passedmethod;
	}
	
	/**
	 * 
	 */
	public MethodInfo getFailedMethod(ClassLoader cl)
	{
		if(clazz!=null)
		{
			if(failedmethod==null && !MI_NOTFOUND.equals(failedmethod))
			{
				synchronized(this)
				{
					if(failedmethod==null && !MI_NOTFOUND.equals(failedmethod))
					{
						Class<?> body = clazz.getType(cl);
						failedmethod = getMethod(body, PlanFailed.class, cl);
						if(failedmethod==null)
							failedmethod = MI_NOTFOUND;
					}
				}
			}
		}
		
		return MI_NOTFOUND.equals(failedmethod)? null: failedmethod;
	}
	
	/**
	 * 
	 */
	public MethodInfo getAbortedMethod(ClassLoader cl)
	{
		if(clazz!=null)
		{
			if(abortedmethod==null && !MI_NOTFOUND.equals(abortedmethod))
			{
				synchronized(this)
				{
					if(abortedmethod==null && !MI_NOTFOUND.equals(abortedmethod))
					{
						Class<?> body = clazz.getType(cl);
						abortedmethod = getMethod(body, PlanAborted.class, cl);
						if(abortedmethod==null)
							abortedmethod = MI_NOTFOUND;
					}
				}
			}
		}
		
		return MI_NOTFOUND.equals(abortedmethod)? null: abortedmethod;
	}
	
	/**
	 * 
	 */
	public MethodInfo getPreconditionMethod(ClassLoader cl)
	{
		if(clazz!=null)
		{
			if(preconditionmethod==null && !MI_NOTFOUND.equals(preconditionmethod))
			{
				synchronized(this)
				{
					if(preconditionmethod==null && !MI_NOTFOUND.equals(preconditionmethod))
					{
						Class<?> body = clazz.getType(cl);
						preconditionmethod = getMethod(body, PlanPrecondition.class, cl);
						if(preconditionmethod==null)
							preconditionmethod = MI_NOTFOUND;
					}
				}
			}
		}
		
		return MI_NOTFOUND.equals(preconditionmethod)? null: preconditionmethod;
	}
	
	/**
	 * 
	 */
	public MethodInfo getContextConditionMethod(ClassLoader cl)
	{
		if(clazz!=null)
		{
			if(contextconditionmethod==null && !MI_NOTFOUND.equals(contextconditionmethod))
			{
				synchronized(this)
				{
					if(contextconditionmethod==null && !MI_NOTFOUND.equals(contextconditionmethod))
					{
						Class<?> body = clazz.getType(cl);
						contextconditionmethod = getMethod(body, PlanContextCondition.class, cl);
						if(contextconditionmethod==null)
							contextconditionmethod = MI_NOTFOUND;
					}
				}
			}
		}
		return MI_NOTFOUND.equals(contextconditionmethod)? null: contextconditionmethod;
	}
	
	/**
	 * 
	 */
	public static MethodInfo getMethod(Class<?> body, Class<? extends Annotation> type, ClassLoader cl)
	{
		MethodInfo ret = null;
		
		Class<?> bcl = body;
		
		while(!Object.class.equals(bcl) && ret==null)
		{
			Method[] ms = bcl.getDeclaredMethods();
			for(Method m: ms)
			{
				if(BDIClassReader.isAnnotationPresent(m, type, cl))
				{
					ret = new MethodInfo(m);
					break;
				}
			}
			
			bcl = bcl.getSuperclass();
		}
		
		return ret;
	}
	
	/**
	 *  Get the line number of the declaration.
	 */
	public int getLineNumber(ClassLoader cl)
	{
		int ret = -1;
		if(clazz!=null)
		{
			try
			{
				Class<?> c = (Class<?>)clazz.getType(cl);
				Method m = c.getMethod("__getLineNumber", new Class[0]);
				ret = ((Integer)m.invoke(null, new Object[0])).intValue();
			}
			catch(Exception e)
			{
			}
		}
		else if(method!=null)
		{
			try
			{
				Method met = method.getMethod(cl);
				Class<?> c = met.getDeclaringClass();
				Method m = c.getMethod("__getLineNumber"+method.getName(), new Class[0]);
				ret = ((Integer)m.invoke(null, new Object[0])).intValue();
			}
			catch(Exception e)
			{
			}
		}
			
		return ret;
	}
}
