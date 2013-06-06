package jadex.bdiv3.model;

import jadex.commons.FieldInfo;
import jadex.commons.SReflect;

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
	
	/** Flag if is multi. */
	protected Boolean multi;
	
	/** The events this belief depends on. */
	protected Set<String> events;
	
	/**
	 *  Create a new belief.
	 */
	public MBelief(FieldInfo target, String impl, boolean dynamic, String[] events)
	{
		super(target!=null? target.getName(): null);
		this.ftarget = target;
		this.impl = impl;
		this.dynamic = dynamic;
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
	}

	/**
	 *  Create a new belief.
	 */
	public MBelief(MethodInfo target, String impl, boolean dynamic, String[] events)
	{
		this((FieldInfo)null, impl, dynamic, events);
		
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
	 *  Set the value of the belief.
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
}
