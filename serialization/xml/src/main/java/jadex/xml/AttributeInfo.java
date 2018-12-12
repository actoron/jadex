package jadex.xml;

import jadex.xml.stax.QName;

/**
 *  Base class for attribute infos.
 */
public class AttributeInfo
{
	//-------- constants --------
	
	/** Constant for identifying content. */
	public static final String CONTENT = "__content"; 
	
	/** Constant for identifying comment. */
	public static final String COMMENT = "__comment"; 

	/** The value of this attribute is used as id. */
	public static final String ID = "id";

	/** The value of this attribute is used as idref. */
	public static final String IDREF = "idref";
	
	//-------- attributes --------
	
	/** The access info. */
	protected AccessInfo accessinfo;
	
	/** The attribute converter. */
	protected IAttributeConverter converter;
	
	/** Is this attribute used as id or idref. */
	protected String id;
	
	// todo: ?
	/** Flag for writing attribute as tag. */
//	protected boolean writeastag;
	
	//-------- constructors --------
		
	/**
	 *  Create a new attribute info. 
	 */
	public AttributeInfo(AccessInfo accessinfo)
	{
		this(accessinfo, null);
	}
	
	/**
	 *  Create a new attribute info. 
	 */
	public AttributeInfo(AccessInfo accessinfo, IAttributeConverter converter)
	{
		this(accessinfo, converter, null);
	}
	
	/**
	 *  Create a new attribute info. 
	 */
	public AttributeInfo(AccessInfo accessinfo, IAttributeConverter converter, String id)
	{
		this.accessinfo = accessinfo;
		this.converter = converter;
		this.id = id;
	}

	//-------- methods --------
	
	/**
	 *  Get the accessinfo.
	 *  @return The accessinfo.
	 */
	public AccessInfo getAccessInfo()
	{
		return this.accessinfo;
	}
	
	/**
	 *  Get the attributeidentifier.
	 *  @return The attributeidentifier.
	 */
	public Object getAttributeIdentifier()
	{
		return accessinfo.getObjectIdentifier();
	}
	
	/**
	 *  Get the attribute name as path.
	 *  @return The attribute names.
	 */
	public QName[] getXMLAttributeNames()
	{
		return accessinfo.getXmlObjectNames();
	}
	
	/**
	 *  Is ignore read.
	 *  @return True, if should be ignored when reading.
	 */
	public boolean isIgnoreRead()
	{
		return accessinfo.isIgnoreRead();
	}
	
	/**
	 *  Is ignore write.
	 *  @return True, if should be ignored when writing.
	 */
	public boolean isIgnoreWrite()
	{
		return accessinfo.isIgnoreWrite();
	}
	
	/**
	 *  Get the attribute converter.
	 *  @return The converter.
	 */
	public IAttributeConverter getConverter()
	{
		return this.converter;
	}

	/**
	 *  Get the id.
	 *  @return The id.
	 */
	public String getId()
	{
		return id;
	}	

}
