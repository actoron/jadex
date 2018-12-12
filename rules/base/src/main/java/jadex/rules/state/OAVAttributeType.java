package jadex.rules.state;

import java.util.HashSet;
import java.util.Set;

import jadex.commons.SReflect;
import jadex.commons.SUtil;


/**
 *  Type definition for an attribute of objects stored
 *  as OAV triples. Type handling is useful for debugging and
 *  may be ignored for performance in production environments.
 */
public class OAVAttributeType
{
	//-------- constants --------
	
	public static final OAVAttributeType OBJECTTYPE = new OAVAttributeType("OBJECTTYPE");
	
	/** Constants for no multiplicity. */
	public static final String NONE = "none";
	
	/** Constants for queue multiplicity. */
	public static final String QUEUE = "queue";
	
	/** Constant for list multiplicity. */
	public static final String LIST = "list";
	
	/** Constant for set multiplicity. */
	public static final String SET = "set";	
	
	/** Constant for map multiplicity. */
	public static final String MAP = "map";	
	
	/** Constant for an ordered map multiplicity. */
	public static final String ORDEREDMAP = "orderedmap";	
	
	/** Set of all multiplicity types (->use an enum). */
	public static final Set MULTIPLICITIES_ALL;
	
	/** Set of collection multiplicity types. */
	public static final Set MULTIPLICITIES_MULT;
	
	/** Set of map multiplicity types. */
	public static final Set MULTIPLICITIES_MAPS;
	
	static
	{
		MULTIPLICITIES_MULT = new HashSet();
		MULTIPLICITIES_MULT.add(QUEUE);
		MULTIPLICITIES_MULT.add(LIST);
		MULTIPLICITIES_MULT.add(SET);
		MULTIPLICITIES_MULT.add(MAP);
		MULTIPLICITIES_MULT.add(ORDEREDMAP);
		
		MULTIPLICITIES_MAPS = new HashSet();
		MULTIPLICITIES_MAPS.add(MAP);
		MULTIPLICITIES_MAPS.add(ORDEREDMAP);
		
		MULTIPLICITIES_ALL = new HashSet();
		MULTIPLICITIES_ALL.addAll(MULTIPLICITIES_MULT);
		MULTIPLICITIES_ALL.add(NONE);
	}
	
	//-------- attributes --------
	
	/** The object type having the attribute. */
	protected OAVObjectType otype;
	
	/** The name of the OAV attribute type. */
	protected String name;
	
	/** The multiplicity type. */
	protected String mult;
	
	/** The type. */
	protected OAVObjectType type;
	
	/** The default value (only supported for single-valued attributes). */
	protected Object def;
	
	/** The index attribute (used for fetching the key value of an oav object). */
	protected OAVAttributeType idxattr;
	
	/** todo: required not null?. */
	// protected boolean notnull;
	
	/** Hash code (cached for speed). */
	protected final int	hashcode;
	
	//-------- constructors --------
	
	/**
	 *  Create a new OAV attribute type.
	 */
	private OAVAttributeType(String name)
	{
		this.name = name;
		this.hashcode = hashCode();
	}
	
	/**
	 *  Create a new OAV attribute type.
	 *  @param otype The object type holding the attribute.
	 *  @param name	The name of the OAV attribute type.
	 *  @param mult The multiplicity.
	 *  @param type The type.
	 *  @param def The default value.
	 */
	protected OAVAttributeType(OAVObjectType otype, String name, OAVObjectType type,
		String mult, Object def, OAVAttributeType idxattr)
	{
		if(otype==null || type==null)
			throw new IllegalArgumentException("Type must not be null.");
		if(!MULTIPLICITIES_ALL.contains(mult))
			throw new IllegalArgumentException("Type must be one of: "+MULTIPLICITIES_ALL+" "+mult);
					
		this.otype = otype;
		this.name = name;
		this.mult = mult;
		this.type = type;
		this.def = def;
		this.idxattr = idxattr;
		this.hashcode	= calcHashCode();
	}
	
	//-------- methods --------
	
	/**
	 *  Get the object type (the type of the object holding this attribute).
	 *  @return The objecttype.
	 */
	public OAVObjectType getObjectType()
	{
		return otype;
	}
	
	/**
	 *  Get the name of the OAV attribute type.
	 *  @return The name of the OAV attribute type.
	 */
	public String	getName()
	{
		return this.name;
	}

	/**
	 *  Get the multiplicity.
	 *  @return The multiplicity.
	 */
	public String getMultiplicity()
	{
		return mult;
	}

	/**
	 *  Get the attribute type (the type of the value stored via this attribute).
	 *  @return The type.
	 */
	public OAVObjectType getType()
	{
		return type;
	}
	
	/**
	 *  Get the default value.
	 *  @return The default value.
	 */
	public Object getDefaultValue()
	{
		return def;
	}
	
	/**
	 *  Get the index attribute.
	 *  @return The index attribute.
	 */
	public OAVAttributeType getIndexAttribute()
	{
		return idxattr;
	}
	
	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return SReflect.getUnqualifiedClassName(this.getClass())+"("+name+")";
	}

	/**
	 *  Compute the hashcode.
	 *  @return The hashcode.
	 */
	public final int hashCode()
	{
		return hashcode;
	}

	/**
	 *  Compute the hashcode.
	 *  @return The hashcode.
	 */
	protected int calcHashCode()
	{
		final int prime = 31;
		int result = prime + name.hashCode();
		result = prime * result + otype.hashCode();
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
			OAVAttributeType attr = (OAVAttributeType)obj;
			if(SUtil.equals(name, attr.getName()) 
				&& SUtil.equals(otype, attr.getObjectType()))
			{
				ret = true;
			}
		}
		return ret;
	}
}
