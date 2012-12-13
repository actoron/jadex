package jadex.commons;


import java.lang.reflect.Method;
import java.util.Arrays;

/**
 *  All info for identifying a method.
 */
public class MethodInfo
{
	//-------- attributes --------

	/** The method name. */
	protected String name;
	
	/** The parameter classes. */
	protected Class<?>[] parametertypes;
	
	//-------- constructors --------
	
	/**
	 *  Create a new method info.
	 */
	public MethodInfo()
	{
	}
	
	/**
	 *  Create a new method info.
	 */
	public MethodInfo(Method m)
	{
		this(m.getName(), m.getParameterTypes());
	}
	
	/**
	 *  Create a new method info.
	 */
	public MethodInfo(String name, Class<?>[] parametertypes)
	{
		this.name = name;
		this.parametertypes = parametertypes.clone();
	}

	//-------- methods --------

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
	 *  Get the parametertypes.
	 *  @return the parametertypes.
	 */
	public Class<?>[] getParameterTypes()
	{
		return parametertypes;
	}

	/**
	 *  Set the parametertypes.
	 *  @param parametertypes The parametertypes to set.
	 */
	public void setParameterTypes(Class<?>[] parametertypes)
	{
		this.parametertypes = parametertypes.clone();
	}
	
	/**
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
		result = prime * result + ((name == null) ? 0 : name.hashCode());
		result = prime * result + MethodInfo.hashCode(parametertypes);
		return result;
	}

	/**
	 *  Test if an object is equal to this.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = false;
		if(obj instanceof MethodInfo)
		{
			MethodInfo other = (MethodInfo)obj;
			ret = SUtil.equals(name, other.name) && Arrays.equals(parametertypes, other.parametertypes);
		}
		return ret;
	}

	/**
	 * Returns a hash code value for the array
	 * @param array the array to create a hash code value for
	 * @return a hash code value for the array
	 */
	private static int hashCode(Object[] array)
	{
		int prime = 31;
		if(array == null)
			return 0;
		int result = 1;
		for(int index = 0; index < array.length; index++)
		{
			result = prime * result + (array[index] == null ? 0 : array[index].hashCode());
		}
		return result;
	}
	
	/**
	 *  Test if two methods have the same signature.
	 * /
	protected static boolean hasEqualSignature(Method ma, Method mb)
	{
		boolean ret = ma.getName().equals(mb.getName());
		
		if(ret)
		{
			Class reta = ma.getReturnType();
			Class retb = mb.getReturnType();
			ret = reta.equals(retb);
			if(ret)
			{
				Class[] paramsa = ma.getParameterTypes();
				Class[] paramsb = mb.getParameterTypes();
				ret = Arrays.equals(paramsa, paramsb);
			}
		}
		
		return ret;
	}*/
}
