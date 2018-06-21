package jadex.bridge.modelinfo;

import jadex.bridge.ClassInfo;
import jadex.commons.MethodInfo;

/**
 * 
 */
public class NFRPropertyInfo extends NFPropertyInfo
{
	/** The method info. */
	protected MethodInfo methodinfo;
	
	/**
	 *  Create a new property.
	 */
	public NFRPropertyInfo()
	{
	}
	
	/**
	 *  Create a new property.
	 *  @param name The name.
	 *  @param clazz The clazz.
	 */
	public NFRPropertyInfo(String name, ClassInfo clazz, MethodInfo mi)
	{
		this.name = name;
		this.clazz = clazz;
		this.methodinfo = mi;
	}

	/**
	 *  Get the methodInfo.
	 *  return The methodInfo.
	 */
	public MethodInfo getMethodInfo()
	{
		return methodinfo;
	}

	/**
	 *  Set the methodInfo. 
	 *  @param methodInfo The methodInfo to set.
	 */
	public void setMethodInfo(MethodInfo methodinfo)
	{
		this.methodinfo = methodinfo;
	}
}
