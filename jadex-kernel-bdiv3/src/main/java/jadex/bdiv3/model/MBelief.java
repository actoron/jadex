package jadex.bdiv3.model;

import jadex.commons.FieldInfo;
import jadex.commons.SReflect;

import java.lang.reflect.Field;
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
	
	/** Flag if is multi. */
	protected Boolean multi;
	
	/**
	 *  Create a new belief.
	 */
	public MBelief(FieldInfo target, String impl)
	{
		super(target.getName());
		this.target = target;
		this.impl = impl;
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
