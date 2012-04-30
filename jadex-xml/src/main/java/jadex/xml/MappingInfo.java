package jadex.xml;

import java.util.Arrays;

/**
 *  The mapping info stores all for for mapping between the different
 *  elements of an object, i.e. attributes, subojects etc. 
 */
public class MappingInfo
{
	//-------- attributes --------
	
	/** The supertype. */
	protected TypeInfo supertype;
	
	/** The comment info. */
	protected Object commentinfo;
	
	/** The content info. */
	protected Object contentinfo;
	
	/** The attributes infos. */
	protected AttributeInfo[] attributeinfos;

	/** The subobject infos. */
	protected SubobjectInfo[] subobjectinfos;
	
	//-------- extra writing info --------
	
	/** The include methods flag. */
	protected Boolean includemethods;

	/** The include fields flag. */
	protected Boolean includefields;
	
	/** The include fields flag. */
	protected Boolean prefertags;

	//-------- constructors --------
	
	/**
	 *  Create a new mapping info.
	 */
	public MappingInfo(Boolean includemethods, Boolean includefields)
	{
		this(includemethods, includefields, null);
	}
	
	/**
	 *  Create a new mapping info.
	 */
	public MappingInfo(Boolean includemethods, Boolean includefields, Boolean prefertags)
	{
		this(null, null, null, null, null, includemethods, includefields, prefertags);
	}
	
	/**
	 *  Create a new mapping info.
	 */
	public MappingInfo(TypeInfo supertype)
	{
		this(supertype, (AttributeInfo[])null);
	}

	/**
	 *  Create a new mapping info.
	 */
	public MappingInfo(TypeInfo supertype, AttributeInfo[] attributeinfos)
	{
		this(supertype, attributeinfos, null);
	}
	
	/**
	 *  Create a new mapping info.
	 */
	public MappingInfo(TypeInfo supertype, SubobjectInfo[] subobjectinfos)
	{
		this(supertype, null, subobjectinfos);
	}

	/**
	 *  Create a new mapping info.
	 */
	public MappingInfo(TypeInfo supertype, Object commentinfo,
		Object contentinfo)
	{		
		this(supertype, commentinfo, contentinfo, null);
	}

	/**
	 *  Create a new mapping info.
	 */
	public MappingInfo(TypeInfo supertype, AttributeInfo[] attributeinfos,
		SubobjectInfo[] subobjectinfos)
	{
		this(supertype, null, null, attributeinfos, subobjectinfos);
	}

	/**
	 *  Create a new mapping info.
	 */
	public MappingInfo(TypeInfo supertype, Object commentinfo,
		Object contentinfo, AttributeInfo[] attributeinfos)
	{		
		this(supertype, commentinfo, contentinfo, attributeinfos, null);
	}

	/**
	 *  Create a new mapping info.
	 */
	public MappingInfo(TypeInfo supertype, Object commentinfo,
		Object contentinfo, AttributeInfo[] attributeinfos,
		SubobjectInfo[] subobjectinfos)
	{
		this(supertype, commentinfo, contentinfo, attributeinfos, subobjectinfos, null, null, null);
	}

	/**
	 *  Create a new mapping info.
	 */
	public MappingInfo(TypeInfo supertype, Object commentinfo,
		Object contentinfo, AttributeInfo[] attributeinfos,
		SubobjectInfo[] subobjectinfos, Boolean includemethods, Boolean includefields, Boolean prefertags)
	{
		this.supertype = supertype;
		this.commentinfo = commentinfo;
		this.contentinfo = contentinfo;
		this.attributeinfos = attributeinfos;
		this.subobjectinfos = subobjectinfos;
		this.includemethods = includemethods;
		this.includefields = includefields;
		this.prefertags = prefertags;
	
		if((commentinfo instanceof AttributeInfo[]) || (contentinfo instanceof AttributeInfo[]))
			System.out.println("here: "+this);
	}

	//-------- methods --------
	
	/**
	 *  Get the supertype.
	 *  @return The supertype.
	 */
	public TypeInfo getSupertype()
	{
		return supertype;
	}

	/**
	 *  Get the commentinfo.
	 *  @return The commentinfo.
	 */
	public Object getCommentInfo()
	{
		return commentinfo;
	}

	/**
	 *  Get the contentinfo.
	 *  @return The contentinfo.
	 */
	public Object getContentInfo()
	{
		return contentinfo;
	}

	/**
	 *  Get the attributeinfos.
	 *  @return The attributeinfos.
	 */
	public AttributeInfo[] getAttributeInfos()
	{
		return attributeinfos;
	}

	/**
	 *  Get the subobjectinfos.
	 *  @return The subobjectinfos.
	 */
	public SubobjectInfo[] getSubobjectInfos()
	{
		return subobjectinfos;
	}

	/**
	 *  Get the includefields.
	 *  @return The includefields.
	 */
	public Boolean getIncludeFields()
	{
		return includefields;
	}
	
	/**
	 *  Get the includemethods.
	 *  @return the includemethods.
	 */
	public Boolean getIncludeMethods()
	{
		return includemethods;
	}

	/**
	 *  Get the prefertags.
	 *  @return The prefertags.
	 */
	public Boolean getPreferTags()
	{
		return this.prefertags;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "MappingInfo(attributeinfos="
			+ Arrays.toString(this.attributeinfos) + ", commentinfo="
			+ this.commentinfo + ", contentinfo=" + this.contentinfo
			+ ", includefields=" + this.includefields + ", prefertags="
			+ this.prefertags + ", subobjectinfos="
			+ Arrays.toString(this.subobjectinfos) + ", supertype="
			+ this.supertype + ", toString()=" + super.toString() + ")";
	}
}
