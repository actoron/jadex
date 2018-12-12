package jadex.commons;

import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.Arrays;

import jadex.bridge.ClassInfo;
//import jadex.commons.transformation.annotations.Exclude;

/**
 *  All info for identifying a method.
 */
public class MethodInfo
{
	//-------- attributes --------

	/** The method name. */
	protected String name;
	
	/** The parameter classes. */
	protected ClassInfo[] parametertypes;

	// cached values
	
	/** Cached return type. */
	protected ClassInfo returntype;
	
	/** Cached class. */
	protected String classname;
	
	/** Cached method. */
	protected Method method;
	
	/** The classloader with which this info was loaded.
	    Note 1: This must not be the same as method.getClass().getClassLoader() because 
	    the latter returns the loader responsible for the class which could be higher
	    in the parent hierarchy.
	    Note 2: The check current_cl==last_cl is not perfect because when invoked
	    with a parent classloader it will reload the class (although not necessary) */
	protected ClassLoader classloader;

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
		if(m==null)
			throw new IllegalArgumentException("Method must not null");
		
		this.name = m.getName();
		
		Type[] pts = m.getGenericParameterTypes();
		Class<?>[] raw = m.getParameterTypes();
		String[] str = new String[pts.length];
		for(int i=0; i<pts.length; i++)
		{
			str[i] = SReflect.getGenericClassName(pts[i], raw[i]);
		}
		this.parametertypes = new ClassInfo[pts.length];
		for(int i = 0; i < parametertypes.length; ++i)
		{
			this.parametertypes[i] = new ClassInfo(str[i]);
		}
		this.classname = m.getDeclaringClass().getName();
		this.returntype = new ClassInfo(SReflect.getGenericClassName(m.getGenericReturnType(), m.getReturnType()));
	}
	
	/**
	 *  Create a new method info.
	 */
	public MethodInfo(String name, Class<?>[] parametertypes)
	{
		this.name = name;
		if(parametertypes!=null)
		{
			this.parametertypes = new ClassInfo[parametertypes.length];
			for(int i = 0; i < parametertypes.length; ++i)
			{
				this.parametertypes[i] = new ClassInfo(SReflect.getClassName(parametertypes[i]));
			}
		}
	}
	
	/**
	 *  Create a new method info.
	 */
	public MethodInfo(String name, ClassInfo[] parametertypes)
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
	
//	/**
//	 *  This method only exists for backward compatibility
//	 *  and decoding purposes only, do not use.
//	 */
//	@Deprecated
//	@Exclude
//	public Class<?>[] getParameterTypes()
//	{
//		// hack
////		return parametertypes2;
////		throw new UnsupportedOperationException("This method only exists for backward compatibility and decoding purposes only, do not use.");
////		throw new UnsupportedOperationException("This method only exists for backward compatibility and decoding purposes only, do not use.");
//		Class<?>[] ret = new Class<?>[parametertypes.length];
//		for (int i = 0; i < parametertypes.length; ++i)
//		{
//			try
//			{
//				ret[i] = parametertypes[i].getType(Thread.currentThread().getContextClassLoader());
//			}
//			catch(Exception e)
//			{
//			}
//		}
//		return ret;
//	}
	
	/**
	 *  Get the parametertypes as classes.
	 *  @return the parametertypes.
	 */
	public Class<?>[] getParameterTypes(ClassLoader cl)
	{
		Class<?>[] typeclasses = new Class<?>[parametertypes.length];
		for (int i = 0; i < parametertypes.length; ++i)
		{
			typeclasses[i] = parametertypes[i].getType(cl);
		}
		return typeclasses;
	}
	
	/**
	 *  Get the parametertypes as classes.
	 *  @return the parametertypes.
	 */
	public void setParameterTypes(Class<?>[] parametertypes)
	{
		this.parametertypes = new ClassInfo[parametertypes.length];
		for(int i = 0; i < parametertypes.length; ++i)
		{
//			this.parametertypes[i] = new ClassInfo(parametertypes[i].getName());
			this.parametertypes[i] = new ClassInfo(SReflect.getClassName(parametertypes[i]));
		}
	}

	/**
	 *  Get the parametertypes.
	 *  @return the parametertypes.
	 */
	public ClassInfo[] getParameterTypeInfos()
	{
		return parametertypes;
	}

	/**
	 *  Set the parametertypes.
	 *  @param parametertypes The parametertypes to set.
	 */
	public void setParameterTypeInfos(ClassInfo[] parametertypes)
	{
		//Shallow copy ok?
//		this.parametertypes = parametertypes.clone();
		this.parametertypes = new ClassInfo[parametertypes.length];
		System.arraycopy(parametertypes, 0, this.parametertypes, 0, parametertypes.length);
	}
	
	/**
	 *  Get the return type.
	 *  @return The return type.
	 */
	public ClassInfo getReturnTypeInfo()
	{
		return returntype;
	}

	/**
	 *  Sets the class name for retrieving the method.
	 * 
	 * 	@param classname Name of the class.
	 */
	public void setClassName(String classname)
	{
		this.classname = classname;
	}
	
	/**
	 *  Gets the class name for retrieving the method.
	 */
	public String	getClassName()
	{
		return classname;
	}
	
	/**
	 * 
	 */
	public Method getMethod(ClassLoader cl)
	{
		try
		{
			if(method==null || classloader != cl)
			{
				Class<?>[] types = new Class[parametertypes.length];
				for(int i=0; i<types.length; i++)
				{
					types[i] = parametertypes[i].getType(cl);
				}
				Class<?> cla = SReflect.findClass(classname, null, cl);
				method = cla.getDeclaredMethod(name, types);
				classloader = cl;
			}
			return method;
		}
		catch(RuntimeException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
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
	
	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		StringBuffer buf = new StringBuffer();
		
		// possibly add return type?
		if(returntype!=null)
		{
			buf.append(SReflect.getUnqualifiedTypeName(returntype.toString())).append(" ");
		}
		
		if(classname!=null)
		{
			buf.append(classname).append(".");
		}
		
		buf.append(getName());
		if(parametertypes!=null && parametertypes.length>0)
		{
			buf.append("(");
			for(int i=0; i<parametertypes.length; i++)
			{
				ClassInfo ci = parametertypes[i];
				buf.append(SReflect.getUnqualifiedTypeName(ci.toString()));
				if(i+1<parametertypes.length)
					buf.append(", ");
			}
			buf.append(")");
		}
		
		return buf.toString();
	}
	
	/**
	 *  Get the name with parameters, e.g. method1(String, int)
	 *  but without return type.
	 */
	public String getNameWithParameters()
	{
		StringBuffer buf = new StringBuffer();
		buf.append(getName());
		if(parametertypes!=null && parametertypes.length>0)
		{
			buf.append("(");
			for(int i=0; i<parametertypes.length; i++)
			{
				ClassInfo ci = parametertypes[i];
				buf.append(SReflect.getUnqualifiedTypeName(ci.toString()));
				if(i+1<parametertypes.length)
					buf.append(", ");
			}
			buf.append(")");
		}
		return buf.toString();
	}	
}
