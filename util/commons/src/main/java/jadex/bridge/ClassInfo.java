package jadex.bridge;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

import jadex.commons.SReflect;


/**
 *  The class info struct serves for saving class information.
 *  A class info may hold a class itself or the class name for
 *  resolving the class. This struct should be used at all places
 *  where classes are meant to be send to remote nodes. In this
 *  case the class info will only transfer the class name forcing
 *  the receiver to lookup the class itself. The class loader
 *  for resolving a class info can be found by using the corresponding
 *  resource identifier (rid) of the component or service that uses
 *  the class.
 */
public class ClassInfo
{
	/** Empty class info array. */
	public static final ClassInfo[] EMPYT_CLASSINFO_ARRAY = new ClassInfo[0];
	
	//-------- attributes --------
	
	/** The service interface type as string. */
	protected String typename;
	
	/** The service interface type. */
	protected Class<?> type;
	
	/** The generic type info (e.g. when obtained via method parameter). */
	protected String geninfo;
	
	/** The classloader with which this info was loaded.
	    Note 1: This must not be the same as method.getClass().getClassLoader() because 
	    the latter returns the loader responsible for the class which could be higher
	    in the parent hierarchy.
	    Note 2: The check current_cl==last_cl is not perfect because when invoked
	    with a parent classloader it will reload the class (although not necessary) */
	protected ClassLoader classloader;
	
	//-------- constructors --------
	
	/**
	 *  Create a new class info.
	 */
	public ClassInfo()
	{
		// Bean constructor, do not delete.
	}

	//-------- methods --------
	
	/**
	 *  Create a new class info.
	 *  @param type The class info.
	 */
	public ClassInfo(Class<?> type)
	{
		if(type==null)
			throw new IllegalArgumentException("Type must not be null.");
		this.type = type; // remember only classname to avoid classloader dependencies
//		this.typename = SReflect.getClassName(type);
	}
	
	/**
	 *  Create a new class info.
	 *  @param type The class info.
	 */
	public ClassInfo(Type type)
	{
		if(type==null)
			throw new IllegalArgumentException("Type must not be null.");
		
		if(type instanceof ParameterizedType)
		{ 
			geninfo = type.toString();
		}

		this.type = SReflect.getClass(type);
//		this.type = type; // remember only classname to avoid classloader dependencies
//		this.typename = SReflect.getClassName(type);
	}
	
	/**
	 *  Create a new class info.
	 *  @param typename The class name.
	 */
	public ClassInfo(String typename)
	{
		if(typename==null)
			throw new IllegalArgumentException("Typename must not be null.");
		
		int pos = typename.indexOf("<");
		if(pos!=-1)
		{
			this.typename = typename.substring(0, pos);
			this.geninfo = typename;
		}
		else
		{
			this.typename = typename;
		}
	}

	/**
	 *  Get the type name.
	 *  @return the type name.
	 */
	public String getTypeName()
	{
		String ret =  typename!=null? typename: type!=null? SReflect.getClassName(type): null;
		return ret;
	}

	/**
	 *  Set the name.
	 *  @param typename The name to set.
	 */
	public void setTypeName(String typename)
	{
		this.typename = typename;
	}
	
	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public Class<?> getType(ClassLoader cl)
	{
		if(cl==null)
			throw new IllegalArgumentException("Not allowed with cl==null, use getType0() instead!");
		
		if(classloader!=cl)
		{
			type = SReflect.classForName0(type!=null? SReflect.getClassName(type): typename, cl);
			// Todo: assert that classloader is always the same -> currently reload required for bdi class rewriting?
			//assert type!=null || classloader==null : "Try to load type :"+getTypeName()+" with wrong classloader: "+classloader+", "+cl;
			if(type!=null)
				classloader = cl;
		}
		return type;
	}
	
	/**
	 *  Get the type.
	 *  @return The type.
	 */
	public Class<?> getType(ClassLoader cl, String[] imports)
	{
		if(cl==null)
			throw new IllegalArgumentException("Not allowed with cl==null, use getType0() instead!");
		
		if(classloader!=cl)
		{
			type = SReflect.findClass0(type!=null? SReflect.getClassName(type): typename, imports, cl);
			// Todo: assert that classloader is always the same -> currently reload required for bdi class rewriting?
			//assert type!=null || classloader==null : "Try to load type :"+getTypeName()+" with wrong classloader: "+classloader+", "+cl;
			if(type!=null)
				classloader = cl;
		}
		return type;
	}
	
	/**
	 *  Get the type, if available
	 *  @return The type or null if not yet resolved.
	 */
	public Class<?> getType0()
	{
		return type;
	}
	
	/**
	 *  Set the type.
	 *  @param type The type to set.
	 */
	// renamed to exclude from XML (@XMLExclude cannot be used due to module deps)
	public void setTheType(Class<?> type)
	{
		this.type = type;
	}
	
	/**
	 *  Get the generic type name.
	 *  @return The type name.
	 */
	public String getGenericTypeName()
	{
		return geninfo!=null? geninfo: getTypeName();
	}

	/**
	 *  Get the geninfo.
	 *  @return The geninfo
	 */
	public String getGeninfo()
	{
		return geninfo;
	}

	/**
	 *  The geninfo to set.
	 *  @param geninfo The geninfo to set
	 */
	public void setGeninfo(String geninfo)
	{
		this.geninfo = geninfo;
	}

	/** 
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		final int prime = 31;
		int result = 1;
//		result = prime * result + ((getGenericTypeName() == null)? 0 : getGenericTypeName().hashCode());
		result = prime * result + ((getTypeName() == null)? 0 : getTypeName().hashCode());
		return result;
	}

	/** 
	 *  Test if object is equal to this.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = false;
		
		if(obj instanceof ClassInfo)
		{
			ClassInfo ci = (ClassInfo)obj;
//			ret = getTypeName().equals(ci.getTypeName());
//			if(ret)
//			{
//				String gtn1 = getGenericTypeName();
//				String gtn2 = ci.getGenericTypeName();
//				if(gtn1!=null && gtn2!=null)
//				{
//					ret = gtn1.equals(gtn2);
//				}
//			}
//			if(getTypeName().indexOf("WriteContext")!=-1)
//			{
//				System.out.println(getGenericTypeName()+" - "+ci.getGenericTypeName()+" "+getGenericTypeName().equals(ci.getGenericTypeName()));
//			}
			ret = getGenericTypeName().equals(ci.getGenericTypeName());
//			ret = getTypeName().equals(ci.getTypeName());
		}
		
		return ret;
	}

	/**
	 *  Get the string representation.
	 */
	public String toString()
	{
		return getGenericTypeName()!=null? getGenericTypeName(): "n/a";
	}
	
	/**
	 *  Get class info in prefix notation, i.e. String - java.lang
	 */
	public String getPrefixNotation()
	{
		String ret = null;
		if(getTypeName()!=null)
		{
			String fn = getTypeName();
			int idx = fn.lastIndexOf(".");
			if(idx!=-1)
			{
				String cn = fn.substring(idx+1);
				String pck = fn.substring(0, idx);
				ret = cn+" - "+pck;
			}
			else
			{
				ret = fn;
			}
		}
		return ret;
	}
	
	/**
	 *  Get class info in class name notation, i.e. String 
	 */
	public String getClassNameOnly()
	{
		String ret = null;
		if(getTypeName()!=null)
		{
			String fn = getTypeName();
			int idx = fn.lastIndexOf(".");
			if(idx!=-1)
			{
				ret = fn.substring(idx+1);
			}
			else
			{
				ret = fn;
			}
		}
		return ret;
	}
	
//	/**
//	 *  Main for testing
//	 */
//	public static void main(String[] args) throws Exception
//	{
//		Type t = ClassInfo.class.getMethod("getVals", new Class[0]).getGenericReturnType();
//		ClassInfo ci = new ClassInfo(t.toString());
//	}
}
