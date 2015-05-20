package jadex.bdiv3.model;

import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.features.impl.BDIAgentFeature;
import jadex.bridge.ClassInfo;
import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.commons.FieldInfo;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


/**
 *  Parameter model.
 */
public class MParameter extends MElement
{
	public static Map<String, Direction> dirs = new HashMap<String, Direction>();

	/** The message direction. */
	public enum Direction
	{
		IN("in"),
		OUT("out"),
		INOUT("inout"),
		FIXED("fixed");
		
		protected String str;
		
		/**
		 *  Create a new direction
		 */
		Direction(String str)
		{
			this.str = str;
			dirs.put(str, this);
		} 
		
		/**
		 *  Get the string representation.
		 *  @return The string representation.
		 */
		public String getString()
		{
			return str;
		}
		
		/**
		 * 
		 */
		public static Direction getDirection(String name)
		{
			return dirs.get(name);
		}
	}
	
	//-------- attributes --------
	
	/** The field target. */
	protected FieldInfo ftarget;

	/** The method targets. */
	protected MethodInfo mgetter;
	protected MethodInfo msetter;
	
	/** Flag if is multi. */
	protected Boolean multi;
	
	//-------- additional xml properties --------
	
	/** The direction. */
	protected Direction direction;
	
	/** The type (if explicitly specified. */
	protected ClassInfo clazz;
	
	/** The default value. */
	protected UnparsedExpression value;
	
	/** The optional flag. */
	protected boolean	optional; 
	
	/**
	 *	Bean Constructor. 
	 */
	public MParameter()
	{
	}
	
	/**
	 *  Create a new parameter.
	 */
	public MParameter(FieldInfo ftarget)
	{
		super(ftarget!=null? ftarget.getName(): null);
		this.ftarget = ftarget;
//		System.out.println("bel: "+(target!=null?target.getName():"")+" "+dynamic);
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
	public boolean isFieldParameter()
	{
		return ftarget!=null;
	}
	
	/**
	 *  Get the value of the belief.
	 */
	public Object getValue(IInternalAccess agent)
	{
		String	capaname	= getName().indexOf(MElement.CAPABILITY_SEPARATOR)==-1
			? null : getName().substring(0, getName().lastIndexOf(MElement.CAPABILITY_SEPARATOR));
		return getValue(((BDIAgentFeature)agent.getComponentFeature(IBDIAgentFeature.class)).getCapabilityObject(capaname), agent.getClassLoader());
	}

	/**
	 *  Get the value of the belief.
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
	
//	/**
//	 *  Set the value of the belief.
//	 */
//	public boolean setValue(BDIAgentInterpreter bai, Object value)
//	{
//		String	capaname	= getName().indexOf(BDIAgentInterpreter.CAPABILITY_SEPARATOR)==-1
//			? null : getName().substring(0, getName().lastIndexOf(BDIAgentInterpreter.CAPABILITY_SEPARATOR));
//		return setValue(bai.getCapabilityObject(capaname), value, bai.getClassLoader());
//	}

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
	 *  Test if parameter is of array type.
	 */
	public boolean isArray()
	{
		boolean ret = false;
		if(isFieldParameter() && ftarget.getClassName()!=null)
		{
//			ret = ftarget.getField(cl).getType().isArray();
			ret = ftarget.getTypeName().charAt(0)=='['; 
		}
		else if(mgetter!=null && mgetter.getReturnTypeInfo()!=null)
		{
//			ret = mgetter.getMethod(cl).getReturnType().isArray();
			ret = mgetter.getReturnTypeInfo().getTypeName().charAt(0)=='['; 
		}
		return ret;
	}

	/**
	 *  Get the direction.
	 *  @return The direction
	 */
	public Direction getDirection()
	{
		return direction;
	}

	/**
	 *  The direction to set.
	 *  @param direction The direction to set
	 */
	public void setDirection(Direction direction)
	{
		this.direction = direction;
	}

	/**
	 *  Get the clazz.
	 *  @return The clazz
	 */
	public ClassInfo getClazz()
	{
		return clazz;
	}

	/**
	 *  The clazz to set.
	 *  @param clazz The clazz to set
	 */
	public void setClazz(ClassInfo clazz)
	{
		this.clazz = clazz;
	}

	/**
	 *  Get the value.
	 *  @return The value
	 */
	public UnparsedExpression getDefaultValue()
	{
		return value;
	}

	/**
	 *  The value to set.
	 *  @param value The value to set
	 */
	public void setDefaultValue(UnparsedExpression value)
	{
		this.value = value;
	}
	
	/**
	 *  Get the optional flag.
	 *  @return The otpional flag
	 */
	public boolean isOptional()
	{
		return optional;
	}

	/**
	 *  Set the optional flag.
	 *  @param optional The value to set
	 */
	public void setOptional(boolean optional)
	{
		this.optional = optional;
	}
}
