package jadex.rules.state;

import java.lang.reflect.Array;
import java.lang.reflect.Method;

import jadex.commons.IPropertyObject;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.beans.PropertyDescriptor;
 
/**
 *  Attribute type for Java object types.
 */
public class OAVJavaAttributeType extends OAVAttributeType
{
	//-------- attributes --------
	
	/** The property descriptor. */
	protected PropertyDescriptor propdesc;
	
	/** The property object read method. */
	protected static volatile Method propreadmethod;
	
	//-------- constructors --------
	
	/**
	 *  Create a new OAV attribute type.
	 *  @param otype The object type holding the attribute.
	 *  @param name	The name of the OAV attribute type.
	 *  @param mult The multiplicity.
	 *  @param type The type.
	 *  @param def The default value.
	 */
	public OAVJavaAttributeType(OAVObjectType otype, String name, OAVObjectType type,
		String mult, Object def, PropertyDescriptor propdesc)
	{
		super(otype, name, type, mult, def, null);
		this.propdesc = propdesc;
	}
	
	//-------- methods --------
	
	/**
	 *  Compute the hashcode.
	 *  @return The hashcode.
	 */
	// Hack!!! Do not include otype in hashcode, as attribute can be inherited.
	protected int calcHashCode()
	{
		final int prime = 31;
		int result = prime + name.hashCode();
//		result = prime * result + otype.hashCode();
		result = prime * result + type.hashCode();
		return result;
	}

	/**
	 *  Test for equality.
	 *  @return True, if equal.
	 */
	public boolean equals(Object obj)
	{
		boolean ret = this==obj;
		
		if(!ret && obj!=null && obj.getClass().equals(this.getClass()))
		{
			OAVJavaAttributeType attr = (OAVJavaAttributeType)obj;
			if(SUtil.equals(name, attr.getName()))
			{
				Class	clazz1	= ((OAVJavaType)otype).getClazz();
				Class	clazz2	= ((OAVJavaType)attr.getObjectType()).getClazz();
				ret = SReflect.isSupertype(clazz1, clazz2) || SReflect.isSupertype(clazz2, clazz1);
			}
		}
		return ret;
	}

	/**
	 *  Get the attribute value from a given object.
	 *  @param object	The object.
	 *  @return	The value.
	 */
	public Object accessProperty(Object object)
	{
		Object	ret;
		
		// Length of arrays.
		if("length".equals(getName()) && ((OAVJavaType)getObjectType()).getClazz().isArray())
		{
			ret	= Integer.valueOf(Array.getLength(object));
		}
		
		// A property of an IPropertyObject.
		else if(IPropertyObject.class.isAssignableFrom(((OAVJavaType)getObjectType()).getClazz()))
		{
			try
			{
				if(propreadmethod==null)
				{
					propreadmethod = IPropertyObject.class.getMethod("getProperty", new Class[]{String.class});
				}
				ret = propreadmethod.invoke(object, new Object[]{name});
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		
		// Bean method.
		else
		{
			Method rm = propdesc.getReadMethod();
			if(rm==null)
				throw new RuntimeException("No attribute accessor found: "+this);
			try
			{
				ret = rm.invoke(object, new Object[0]);
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		return ret;
	}
}
