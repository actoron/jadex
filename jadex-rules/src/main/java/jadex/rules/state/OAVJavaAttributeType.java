package jadex.rules.state;

import jadex.commons.SReflect;
import jadex.commons.SUtil;

import java.beans.PropertyDescriptor;
 
/**
 *  Attribute type for Java object types.
 */
public class OAVJavaAttributeType extends OAVAttributeType
{
	//-------- attributes --------
	
	/** The property descriptor. */
	protected PropertyDescriptor propdesc;
	
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
	
	/**
	 *  Get the property descriptor.
	 *  @return The property descriptor.
	 */
	public PropertyDescriptor getPropertyDescriptor()
	{
		return propdesc;
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
		
		if(!ret && obj instanceof OAVJavaAttributeType)
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
}
