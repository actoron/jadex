package jadex.bdiv3.model;

import jadex.commons.SReflect;
import jadex.commons.SUtil;

/**
 * 
 */
public class MClassBasedElement extends MProcessableElement
{
	/** The target. */
	protected String target;
	protected Class<?> targetclass;
	
	/**
	 *	Bean Constructor. 
	 */
	public MClassBasedElement()
	{
	}
	
	/**
	 *  Create a new belief.
	 */
	public MClassBasedElement(String name, String target, boolean posttoall, boolean rebuild, boolean randomselection, ExcludeMode excludemode)
	{
		super(name, posttoall, rebuild, randomselection, excludemode);
		this.target = target;
	}

	/**
	 *  Get the target.
	 *  @return The target.
	 */
	public String getTarget()
	{
		return target;
	}
	
	/**
	 *  Get the target.
	 *  @return The target.
	 */
	public Class<?> getTargetClass(ClassLoader cl)
	{
		if(targetclass==null && target!=null)
			targetclass = SReflect.findClass0(target, null, cl);
		return targetclass;
	}

	/**
	 *  Set the target.
	 *  @param target The target to set.
	 */
	public void setTarget(String target)
	{
		this.target = target;
	}
	
	/**
	 * 
	 */
	public boolean equals(Object other)
	{
		if(!(other instanceof MGoal))
			return false;
		
		String oname = ((MGoal)other).getName();
		String otarget = ((MGoal)other).getTarget();
		
		return other instanceof MGoal && 
			(SUtil.equals(name, oname) && SUtil.equals(target, otarget));
//			target.equals(((MGoal)other).getTarget()))
	}

	/**
	 * 
	 */
	public int hashCode()
	{
		return target!=null? target.hashCode(): getName().hashCode();
	}
}
