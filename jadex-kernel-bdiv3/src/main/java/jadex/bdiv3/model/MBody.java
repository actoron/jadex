package jadex.bdiv3.model;

import jadex.bridge.ClassInfo;

/**
 * 
 */
public class MBody
{
	/** The body as seperate class. */
	protected MethodInfo method;
	
	/** The body as seperate class. */
	protected ClassInfo clazz;

	/** The body as required service. */
	protected String servicename;
	
	/** The body as required service. */
	protected String servicemethodname;

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
	public String getServicename()
	{
		return servicename;
	}

	/**
	 *  Set the servicename.
	 *  @param servicename The servicename to set.
	 */
	public void setServicename(String servicename)
	{
		this.servicename = servicename;
	}

	/**
	 *  Get the servicemethodname.
	 *  @return The servicemethodname.
	 */
	public String getServicemethodname()
	{
		return servicemethodname;
	}

	/**
	 *  Set the servicemethodname.
	 *  @param servicemethodname The servicemethodname to set.
	 */
	public void setServicemethodname(String servicemethodname)
	{
		this.servicemethodname = servicemethodname;
	}
}
