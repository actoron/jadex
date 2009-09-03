package jadex.commons.xml;

/**
 *  Base class for attribute infos.
 */
public class AttributeInfo
{
	//-------- constants --------
	
	public static final String THIS = "__this"; 

	public static final String CONTENT = "__content"; 
	
	public static final String COMMENT = "__comment"; 
	
	/** Ignore when reading. */
	public static final String IGNORE_READ = "ignore_read";
	
	/** Ignore when writing. */
	public static final String IGNORE_WRITE = "ignore_write";

	/** Ignore when reading and writing. */
	public static final String IGNORE_READWRITE = "ignore_readwrite";
	
	//-------- attributes --------
	
	// read + write
	
	/** The object attribute. */
	protected Object attributeidentifier;

	/** The xml attribute name. */
	protected QName xmlattributename;
	
	/** The default value. */
	protected Object defaultvalue;
	
	/** The ignore property. */
	protected String ignore;
	
	// read
	
	/** The attribute value converter for reading. */
	protected ITypeConverter converterread;
	
	// write
	
	/** The attribute value converter for write. */
	protected ITypeConverter converterwrite;
	
	//-------- constructors --------
		
	/**
	 *  Create a new attribute info. 
	 */
	public AttributeInfo(QName xmlattributename, Object attributeidentifier)
	{
		this(xmlattributename, attributeidentifier, null);
	}
	
	/**
	 *  Create a new attribute info. 
	 */
	public AttributeInfo(QName xmlattributename, Object attributeidentifier, String ignore)
	{
		this(xmlattributename, attributeidentifier, ignore, null, null);
	}
	
	/**
	 *  Create a new bean attribute info. 
	 */
	public AttributeInfo(QName xmlattributename, Object attributeidentifier, String ignore, ITypeConverter converterread, ITypeConverter converterwrite)
	{
		this(xmlattributename, attributeidentifier, ignore, converterread, converterwrite, null);
	}
	
	/**
	 *  Create a new bean attribute info. 
	 */
	public AttributeInfo(QName xmlattributename, Object attributeidentifier, String ignore, ITypeConverter converterread, ITypeConverter converterwrite, Object defaultvalue)
	{
		this.xmlattributename = xmlattributename;
		this.attributeidentifier = attributeidentifier;
		this.ignore = ignore;
		this.converterread = converterread;
		this.converterwrite = converterwrite;
		this.defaultvalue = defaultvalue;
	}
	
	/**
	 *  Create a new attribute info. 
	 */
	public AttributeInfo(String xmlattributename, Object attributeidentifier)
	{
		this(xmlattributename, attributeidentifier, null);
	}
	
	/**
	 *  Create a new attribute info. 
	 */
	public AttributeInfo(String xmlattributename, Object attributeidentifier, String ignore)
	{
		this(xmlattributename, attributeidentifier, ignore, null, null);
	}
	
	/**
	 *  Create a new bean attribute info. 
	 */
	public AttributeInfo(String xmlattributename, Object attributeidentifier, String ignore, ITypeConverter converterread, ITypeConverter converterwrite)
	{
		this(xmlattributename, attributeidentifier, ignore, converterread, converterwrite, null);
	}
	
	/**
	 *  Create a new bean attribute info. 
	 */
	public AttributeInfo(String xmlattributename, Object attributeidentifier, String ignore, ITypeConverter converterread, ITypeConverter converterwrite, Object defaultvalue)
	{
		this(xmlattributename==null? null: QName.valueOf(xmlattributename), attributeidentifier, ignore, converterread, converterwrite, defaultvalue);
	}

	//-------- methods --------
	
	/**
	 *  Get the attributeidentifier.
	 *  @return The attributeidentifier.
	 */
	public Object getAttributeIdentifier()
	{
		return this.attributeidentifier;
	}
	
	/**
	 *  Get the attribut name.
	 *  @return The attributename.
	 */
	public QName getXMLAttributeName()
	{
		return this.xmlattributename;
	}
	
	/**
	 *  Is ignore read.
	 *  @return True, if should be ignored when reading.
	 */
	public boolean isIgnoreRead()
	{
		return IGNORE_READ.equals(ignore) || IGNORE_READWRITE.equals(ignore);
	}
	
	/**
	 *  Is ignore write.
	 *  @return True, if should be ignored when writing.
	 */
	public boolean isIgnoreWrite()
	{
		return IGNORE_WRITE.equals(ignore) || IGNORE_READWRITE.equals(ignore);
	}
	
	/**
	 *  Get the attribute converter for reading.
	 *  @return The converter.
	 */
	public ITypeConverter getConverterRead()
	{
		return this.converterread;
	}

	/**
	 *  Get the attribute converter for writing.
	 *  @return The converter.
	 */
	public ITypeConverter getConverterWrite()
	{
		return this.converterwrite;
	}

	/**
	 *  Get the default value.
	 *  @return the defaultvalue.
	 */
	public Object getDefaultValue()
	{
		return this.defaultvalue;
	}
}
