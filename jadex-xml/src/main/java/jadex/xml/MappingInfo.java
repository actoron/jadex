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
	
	/** The include fields flag. */
	protected boolean includefields;

	//-------- constructors --------
	
	/**
	 *  Create a new mapping info.
	 */
	public MappingInfo(boolean includefield)
	{
		this(null, null, null, null, null, includefield);
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
		this(supertype, commentinfo, contentinfo, attributeinfos, subobjectinfos, false);
	}

	/**
	 *  Create a new mapping info.
	 */
	public MappingInfo(TypeInfo supertype, Object commentinfo,
		Object contentinfo, AttributeInfo[] attributeinfos,
		SubobjectInfo[] subobjectinfos, boolean includefields)
	{
		this.supertype = supertype;
		this.commentinfo = commentinfo;
		this.contentinfo = contentinfo;
		this.attributeinfos = attributeinfos;
		this.subobjectinfos = subobjectinfos;
		this.includefields = includefields;
	
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
	 *  Set the supertype.
	 *  @param supertype The supertype to set.
	 */
	public void setSupertype(TypeInfo supertype)
	{
		this.supertype = supertype;
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
	 *  Set the commentinfo.
	 *  @param commentinfo The commentinfo to set.
	 */
	public void setCommentInfo(Object commentinfo)
	{
		this.commentinfo = commentinfo;
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
	 *  Set the contentinfo.
	 *  @param contentinfo The contentinfo to set.
	 */
	public void setContentInfo(Object contentinfo)
	{
		this.contentinfo = contentinfo;
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
	 *  Set the attributeinfos.
	 *  @param attributeinfos The attributeinfos to set.
	 */
	public void setAttributeInfos(AttributeInfo[] attributeinfos)
	{
		this.attributeinfos = attributeinfos;
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
	 *  Set the subobjectinfos.
	 *  @param subobjectinfos The subobjectinfos to set.
	 */
	public void setSubobjectInfos(SubobjectInfo[] subobjectinfos)
	{
		this.subobjectinfos = subobjectinfos;
	}

	/**
	 *  Get the includefields.
	 *  @return The includefields.
	 */
	public boolean isIncludeFields()
	{
		return includefields;
	}

	/**
	 *  Set the includefields.
	 *  @param includefields The includefields to set.
	 */
	public void setIncludeFields(boolean includefields)
	{
		this.includefields = includefields;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "MappingInfo [attributeinfos=" + Arrays.toString(attributeinfos)
			+ ", commentinfo=" + commentinfo + ", contentinfo="
			+ contentinfo + ", includefields=" + includefields
			+ ", subobjectinfos=" + Arrays.toString(subobjectinfos)
			+ ", supertype=" + supertype + "]";
	}
}
