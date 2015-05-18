package jadex.bdiv3.model;

import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.features.impl.BDIAgentFeature;
import jadex.bridge.ClassInfo;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.FieldInfo;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.rules.eca.EventType;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 *  Belief model.
 */
public class MBelief extends MElement
{
	/** The field target. */
	protected FieldInfo ftarget;

	/** The method targets. */
	protected MethodInfo mgetter;
	protected MethodInfo msetter;
	
	/** The collection implementation class. */
	protected String impl;
	
	/** The dynamic flag. */
	protected boolean dynamic;
	
	/** The update rate. */
	protected long	updaterate;
	
	/** Flag if is multi. */
	protected Boolean multi;
	
	/** The events this belief depends on. */
	protected Collection<String> events;
	
	/** The raw events. */
	protected Collection<EventType> rawevents;

	//-------- additional xml properties --------
	
	/** The default fact. */
	protected UnparsedExpression fact;
	
	/** The type (if explicitly specified. */
	protected ClassInfo clazz;
	
	/**
	 *	Bean Constructor. 
	 */
	public MBelief()
	{
	}
	
	/**
	 *  Create a new belief.
	 */
	public MBelief(FieldInfo target, String impl, boolean dynamic, long updaterate, String[] events, Collection<EventType> rawevents)
	{
		super(target!=null? target.getName(): null);
		this.ftarget = target;
		this.impl = impl;
		this.dynamic = dynamic;
		this.updaterate	= updaterate;
		this.events = new HashSet<String>();
		if(events!=null)
		{
			// Is dynamic when events are given
			if(events.length>0)
				dynamic = true;
			for(String ev: events)
			{
				this.events.add(ev);
			}
		}
		this.rawevents = rawevents;
		
//		System.out.println("bel: "+(target!=null?target.getName():"")+" "+dynamic);
	}

	/**
	 *  Create a new belief.
	 */
	public MBelief(MethodInfo target, String impl, boolean dynamic, long updaterate, String[] events, Collection<EventType> rawevents)
	{
		this((FieldInfo)null, impl, dynamic, updaterate, events, rawevents);
		
		if(target.getName().startsWith("get"))
		{
			this.mgetter = target;
			name = target.getName().substring(3);			
		}
		else if(target.getName().startsWith("is"))
		{
			this.mgetter = target;
			name = target.getName().substring(2);			
		}
		else// if(target.getName().startsWith("set"))
		{
			this.msetter = target;
			name = target.getName().substring(3);			
		}
		
		name = name.substring(0, 1).toLowerCase()+name.substring(1);
	}
	
//	/**
//	 *  Get the target.
//	 *  @return The target.
//	 */
//	public FieldInfo getFieldTarget()
//	{
//		return ftarget;
//	}
//
//	/**
//	 *  Set the target.
//	 *  @param target The target to set.
//	 */
//	public void setFieldTarget(FieldInfo target)
//	{
//		this.ftarget = target;
//	}
//	
//	/**
//	 *  Get the target.
//	 *  @return The target.
//	 */
//	public MethodInfo[] getMethodTarget()
//	{
//		return mtarget;
//	}
//
//	/**
//	 *  Set the target.
//	 *  @param target The target to set.
//	 */
//	public void setMethodTarget(MethodInfo[] target)
//	{
//		this.mtarget = target;
//	}
	
	/**
	 *  Get the impl.
	 *  @return The impl.
	 */
	public String getImplClassName()
	{
		return impl;
	}

	/**
	 *  Set the impl.
	 *  @param impl The impl to set.
	 */
	public void setImplClassName(String impl)
	{
		this.impl = impl;
	}
	
	/**
	 *  Get the events.
	 *  @return The events.
	 */
	public Collection<String> getEvents()
	{
		return events;
	}

	/**
	 *  Set the events.
	 *  @param events The events to set.
	 */
	public void setEvents(Collection<String> events)
	{
		this.events.clear();
		this.events.addAll(events);
	}
	
	/**
	 *  Get the dynamic.
	 *  @return The dynamic.
	 */
	public boolean isDynamic()
	{
		return dynamic;
	}

	/**
	 *  Set the dynamic.
	 *  @param dynamic The dynamic to set.
	 */
	public void setDynamic(boolean dynamic)
	{
		this.dynamic = dynamic;
	}
	
	/**
	 *  Get the updaterate.
	 *  @return The updaterate.
	 */
	public long getUpdaterate()
	{
		return updaterate;
	}

	/**
	 *  Set the updaterate.
	 *  @param updaterate The updaterate to set.
	 */
	public void setUpdaterate(long updaterate)
	{
		this.updaterate = updaterate;
	}
	
	/**
	 *  Set the mgetter.
	 *  @param mgetter The mgetter to set.
	 */
	public void setGetter(MethodInfo mgetter)
	{
		this.mgetter = mgetter;
	}

	/**
	 *  Set the msetter.
	 *  @param msetter The msetter to set.
	 */
	public void setSetter(MethodInfo msetter)
	{
		this.msetter = msetter;
	}
	
	/**
	 *  Get the clazz.
	 *  @return The clazz
	 */
	public ClassInfo getClazz()
	{
		return clazz;
	}

	/**
	 *  The clazz to set.
	 *  @param clazz The clazz to set
	 */
	public void setClazz(ClassInfo clazz)
	{
		this.clazz = clazz;
	}
	
	/**
	 *  Test if this belief refers to a field.
	 *  @return True if is a field belief.
	 */
	public boolean isFieldBelief()
	{
		return ftarget!=null;
	}
	
	/**
	 *  Get the multi.
	 *  @return The multi.
	 */
	public boolean isMulti(ClassLoader cl)
	{
		if(multi==null)
		{
			Class<?> ftype = null;
			if(ftarget!=null)
			{
				Field f = ftarget.getField(cl);
				ftype = f.getType();
			}
			else 
			{
				ftype = mgetter.getMethod(cl).getReturnType();
			}
			if(ftype.isArray() || SReflect.isSupertype(List.class, ftype) 
				|| SReflect.isSupertype(Set.class, ftype)
				|| SReflect.isSupertype(Map.class, ftype))
			{
				multi = Boolean.TRUE;
			}
			else
			{
				multi = Boolean.FALSE;
			}
		}
		return multi;
	}
	
	/**
	 *  Get the value of the belief.
	 */
	public Object getValue(IInternalAccess agent)
	{
		String	capaname	= getName().indexOf(MElement.CAPABILITY_SEPARATOR)==-1
			? null : getName().substring(0, getName().lastIndexOf(MElement.CAPABILITY_SEPARATOR));
		return getValue(((BDIAgentFeature)agent.getComponentFeature(IBDIAgentFeature.class)).getCapabilityObject(capaname), agent.getClassLoader());
	}

	/**
	 *  Get the value of the belief.
	 */
	public Object getValue(Object object, ClassLoader cl)
	{
		Object ret = null;
		if(ftarget!=null)
		{
			try
			{
				Field f = ftarget.getField(cl);
				f.setAccessible(true);
				ret = f.get(object);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			try
			{
				Method m = mgetter.getMethod(cl);
				ret = m.invoke(object, new Object[0]);
			}
			catch(InvocationTargetException e)
			{
				e.getTargetException().printStackTrace();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return ret;
	}
	
	/**
	 *  Set the value of the belief.
	 */
	public boolean setValue(IInternalAccess agent, Object value)
	{
		String	capaname	= getName().indexOf(MElement.CAPABILITY_SEPARATOR)==-1
			? null : getName().substring(0, getName().lastIndexOf(MElement.CAPABILITY_SEPARATOR));
		return setValue(((BDIAgentFeature)agent.getComponentFeature(IBDIAgentFeature.class)).getCapabilityObject(capaname), value, agent.getClassLoader());
	}

	/**
	 *  Set the value of the belief.
	 */
	public boolean setValue(Object object, Object value, ClassLoader cl)
	{
		boolean field	= false;
		if(ftarget!=null)
		{
			field	= true;
			try
			{
				Field f = ftarget.getField(cl);
				f.setAccessible(true);
				f.set(object, value);
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			try
			{
				Method m = msetter.getMethod(cl);
				m.invoke(object, new Object[]{value});
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return field;
	}
	
	/**
	 *  Get the class of the belief.
	 */
	public Class<?> getType(ClassLoader cl)
	{
		Class<?> ret = null;
		if(ftarget!=null)
		{
			try
			{
				Field f = ftarget.getField(cl);
//				f.setAccessible(true);
				ret = f.getType();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			try
			{
				Method m = mgetter.getMethod(cl);
				ret = m.getReturnType();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		return ret;
	}

	/**
	 *  Get the field (for field-backed beliefs).
	 */
	public FieldInfo getField()
	{
		return ftarget;
	}

	/**
	 *  Get the getter method (for method-backed beliefs).
	 */
	public MethodInfo getGetter()
	{
		return mgetter;
	}

	/**
	 *  Get the setter method (for method-backed beliefs).
	 */
	public MethodInfo getSetter()
	{
		return msetter;
	}
	
	/**
	 *  Test if belief is of array type.
	 */
	public boolean isArrayBelief()//ClassLoader cl)
	{
		boolean ret = false;
		if(isFieldBelief() && ftarget.getClassName()!=null)
		{
//			ret = ftarget.getField(cl).getType().isArray();
			ret = ftarget.getTypeName().charAt(0)=='['; 
		}
		else if(mgetter!=null && mgetter.getReturnTypeInfo()!=null)
		{
//			ret = mgetter.getMethod(cl).getReturnType().isArray();
			ret = mgetter.getReturnTypeInfo().getTypeName().charAt(0)=='['; 
		}
		return ret;
	}

	/**
	 *  Get the rawevents.
	 *  @return The rawevents.
	 */
	public Collection<EventType> getRawEvents()
	{
		return rawevents;
	}

	/**
	 *  Set the rawevents.
	 *  @param rawevents The rawevents to set.
	 */
	public void setRawEvents(Set<EventType> rawevents)
	{
		this.rawevents = rawevents;
	}
	
	/**
	 *  Get the value.
	 *  @return The value
	 */
	public UnparsedExpression getDefaultFact()
	{
		return fact;
	}

	/**
	 *  The value to set.
	 *  @param value The value to set
	 */
	public void setDefaultFact(UnparsedExpression fact)
	{
		this.fact = fact;
	}
}
