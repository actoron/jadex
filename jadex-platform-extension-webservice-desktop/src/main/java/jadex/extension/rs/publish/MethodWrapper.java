package jadex.extension.rs.publish;

import java.lang.reflect.Method;
import java.util.Arrays;

/**
 *  Helper struct that saves a method and a method name.
 *  The method name can be different from the original name.
 */
public class MethodWrapper
{
	/** The method name. */
	protected String name;
	
	/** The method. */
	protected Method method;

	/**
	 *  Create a new method wrapper.
	 */
	public MethodWrapper(Method method)
	{
		this(method.getName(), method);
	}
	
	/**
	 *  Create a new method wrapper.
	 */
	public MethodWrapper(String name, Method method)
	{
		this.name = name;
		this.method = method;
	}

	/**
	 *  Get the method.
	 *  @return the method.
	 */
	public Method getMethod()
	{
		return method;
	}

	/**
	 *  Set the method.
	 *  @param method The method to set.
	 */
	public void setMethod(Method method)
	{
		this.method = method;
	}

	/**
	 *  Get the name.
	 *  @return the name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the hash code.
	 */
	public int hashCode()
	{
		return name.hashCode()*31 + Arrays.hashCode(method.getParameterTypes());
	}

	/**
	 *  Equal when name and parameters are equal.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = false;
		if(obj instanceof MethodWrapper)
		{
//			ret = ((MethodWrapper)obj).getName().equals(getName());
			Method m2 = ((MethodWrapper)obj).getMethod();
			ret = method.getName().equals(m2.getName());
			if(ret)
			{
				Class[] paramtypes = method.getParameterTypes();
				Class[] pt2 = m2.getParameterTypes();
				ret = Arrays.equals(paramtypes, pt2);
			}
		}
		
		return ret;
	}
}