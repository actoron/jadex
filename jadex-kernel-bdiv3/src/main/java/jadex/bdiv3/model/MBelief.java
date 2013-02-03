package jadex.bdiv3.model;

import jadex.commons.FieldInfo;
import jadex.commons.SReflect;

import java.lang.reflect.Field;
import java.util.ArrayList;
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
	/** The target. */
	protected FieldInfo target;

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
		super(target.getName());
		this.target = target;
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
	 *  Get the target.
	 *  @return The target.
	 */
	public FieldInfo getTarget()
	{
		return target;
	}

	/**
	 *  Set the target.
	 *  @param target The target to set.
	 */
	public void setTarget(FieldInfo target)
	{
		this.target = target;
	}
	
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
	 *  Get the multi.
	 *  @return The multi.
	 */
	public boolean isMulti(ClassLoader cl)
	{
		if(multi==null)
		{
			Field f = target.getField(cl);
			Class<?> ftype = f.getType();
			if(SReflect.isSupertype(List.class, ftype) 
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
}
