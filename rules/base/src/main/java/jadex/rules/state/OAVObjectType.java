package jadex.rules.state;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
 *  Type definition for an object stored
 *  as OAV triples. Type handling is useful for debugging and
 *  may be ignored for performance in production environments.
 */
public class OAVObjectType
{
	//-------- attributes --------
	
	/** The name of the OAV object type. */
	protected String	name;
	
	/** The supertype of this type (if any). */
	protected OAVObjectType supertype;
	
	/** The attribute descriptions. */
	protected Map	attributes;
	
	/** The type model. */
	protected OAVTypeModel tmodel;
	
	//-------- constructors --------
	
	/**
	 *  Create a new OAV object type.
	 *  @param name	The name of the OAV object type.
	 * /
	public OAVObjectType(String name)
	{
		this(name, null);
	}*/
	
	/**
	 *  Create a new OAV object type.
	 *  @param name	The name of the OAV object type.
	 *  @param supertype The supertype.
	 */
	protected OAVObjectType(String name, OAVObjectType supertype, OAVTypeModel tmodel)
	{
		if(name==null)
			throw new IllegalArgumentException("Name must not be null.");
		this.name	= name;
		this.supertype = supertype;
		this.tmodel = tmodel;
	}

	//-------- methods --------
	
	/**
	 *  Get the name of the OAV object type.
	 *  @return The name of the OAV object type.
	 */
	public String	getName()
	{
		return this.name;
	}

	/**
	 *  Add an attribute type description.
	 *  @param attribute	The OAV attribute type.
	 */
	protected void	addAttributeType(OAVAttributeType attribute)
	{
		if(attributes==null)
			attributes	= new HashMap();
		if(attributes.containsKey(attribute.getName()))
			throw new RuntimeException("Attribute already defined: "+attribute);
		attributes.put(attribute.getName(), attribute);
	}
	
	/**
	 *  Create a new attribute type.
	 *  @param name The name.
	 *  @param type The type.
	 *  @param mult The multiplicity.
	 *  @param def The default value.
	 */
	public OAVAttributeType createAttributeType(String name, 
		OAVObjectType type)
	{
		return createAttributeType(name, type, OAVAttributeType.NONE, null);
	}
	
	/**
	 *  Create a new attribute type.
	 *  @param name The name.
	 *  @param type The type.
	 *  @param mult The multiplicity.
	 *  @param def The default value.
	 */
	public OAVAttributeType createAttributeType(String name, 
		OAVObjectType type, String mult)
	{
		return createAttributeType(name, type, mult, null);
	}
	
	/**
	 *  Create a new attribute type.
	 *  @param name The name.
	 *  @param type The type.
	 *  @param mult The multiplicity.
	 *  @param def The default value.
	 */
	public OAVAttributeType createAttributeType(String name, 
		OAVObjectType type, String mult, Object def)
	{
		if(!tmodel.contains(type))
			throw new RuntimeException("Unknown object type: "+type);
		OAVAttributeType attr = new OAVAttributeType(this, name, type, mult, def, null);
		addAttributeType(attr);
		return attr;
	}
	
	/**
	 *  Create a new attribute type.
	 *  @param name The name.
	 *  @param type The type.
	 *  @param mult The multiplicity.
	 *  @param def The default value.
	 */
	public OAVAttributeType createAttributeType(String name, 
		OAVObjectType type, String mult, Object def, OAVAttributeType idxattr)
	{
		if(!tmodel.contains(type))
			throw new RuntimeException("Unknown object type: "+type);
		OAVAttributeType attr = new OAVAttributeType(this, name, type, mult, def, idxattr);
		addAttributeType(attr);
		return attr;
	}
	

	/**
	 *  Get an attribute type description.
	 *  @param attribute	The name of the attribute.
	 *  @return The OAV attribute type.
	 */
	public OAVAttributeType	getAttributeType(String attribute)
	{
		OAVAttributeType ret = getAttributeType0(attribute);
		
		if(ret==null)
			throw new RuntimeException("Attribute not found: "+this+", "+attribute);
		
		return ret;
	}
	
	/**
	 *  Get an attribute type description.
	 *  @param attribute	The name of the attribute.
	 *  @return The OAV attribute type.
	 */
	public OAVAttributeType	getAttributeType0(String attribute)
	{
		OAVAttributeType ret = (OAVAttributeType)(attributes!=null? attributes.get(attribute): null);
		if(ret==null)
		{
			OAVObjectType	type	= this.getSupertype();
			while(ret==null && type!=null)
			{
				ret = type.getAttributeType0(attribute);
				type = type.getSupertype();
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get an attribute type description.
	 *  @param attribute	The name of the attribute.
	 *  @return The OAV attribute type.
	 */
	public OAVAttributeType	getDeclaredAttributeType0(String attribute)
	{
		return (OAVAttributeType)(attributes!=null? attributes.get(attribute): null);
	}
	
	/**
	 *  Get the declared attribute types (i.e. not those of super types).
	 */
	public Collection	getDeclaredAttributeTypes()
	{
		return attributes!=null ? attributes.values() : Collections.EMPTY_SET;
	}
	
	/**
	 *  Get the supertype of this typ.
	 *  @return The supertype (if any).
	 */
	public OAVTypeModel	getTypeModel()
	{
		return tmodel;
	}
	
	/**
	 *  Get the supertype of this typ.
	 *  @return The supertype (if any).
	 */
	public OAVObjectType getSupertype()
	{
		return supertype;
	}
	
	/**
	 *  Test if two types are equal.
	 *  @return True if equal.
	 */
	public boolean equals(Object object)
	{
		return object instanceof OAVObjectType && ((OAVObjectType)object).getName().equals(name);
	}
	
	/**
	 *  Get the hash code.
	 *  @return The hashcode.
	 */
	public int hashCode()
	{
		return 31+name.hashCode();
	}

	/**
	 *  Test if this type is same type or subtype of another type.
	 *  @param type The type to test.
	 *  @return True, if this object is same type or subtype.
	 */
	public boolean isSubtype(OAVObjectType type)
	{
		boolean ret = false;
		
		OAVObjectType tmp = this;
		while(tmp!=null && !ret)
		{
			if(type.equals(tmp))
				ret = true;
			else
				tmp = tmp.getSupertype();
		}
		
		return ret;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "OAVObjectType("+name+")";
	}
}
