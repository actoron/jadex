package jadex.xml;

import javax.xml.namespace.QName;

/**
 *  Base class for attribute infos.
 */
public class AttributeInfo
{
	//-------- constants --------
	
	/** Constant for identifying this. */
	public static final String THIS = "__this"; 

	/** Constant for identifying content. */
	public static final String CONTENT = "__content"; 
	
	/** Constant for identifying comment. */
	public static final String COMMENT = "__comment"; 
	
	/** Ignore when reading. */
	public static final String IGNORE_READ = "ignore_read";
	
	/** Ignore when writing. */
	public static final String IGNORE_WRITE = "ignore_write";

	/** Ignore when reading and writing. */
	public static final String IGNORE_READWRITE = "ignore_readwrite";

	/** The value of this attribute is used as id. */
	public static final String ID = "id";

	/** The value of this attribute is used as idref. */
	public static final String IDREF = "idref";

	
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
	
	/** Is this attribute used as id or idref. */
	protected String id;
	
	// write
	
	/** The attribute value converter for write. */
	protected ITypeConverter converterwrite;
	
	/** Flag for writing attribute as tag. */
	protected boolean writeastag;
	
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
		this(xmlattributename, attributeidentifier, ignore, converterread, converterwrite, null, false);
	}
	
	/**
	 *  Create a new bean attribute info. 
	 */
	public AttributeInfo(QName xmlattributename, Object attributeidentifier, String ignore, ITypeConverter converterread, ITypeConverter converterwrite, Object defaultvalue, boolean writeastag)
	{
		this(xmlattributename, attributeidentifier, ignore, converterread, converterwrite, null, false, null);
	}
	
	/**
	 *  Create a new bean attribute info. 
	 */
	public AttributeInfo(QName xmlattributename, Object attributeidentifier, String ignore, ITypeConverter converterread, ITypeConverter converterwrite, Object defaultvalue, boolean writeastag, String id)
	{
		this.xmlattributename = xmlattributename;
		this.attributeidentifier = attributeidentifier;
		this.ignore = ignore;
		this.converterread = converterread;
		this.converterwrite = converterwrite;
		this.defaultvalue = defaultvalue;
		this.id = id;
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
	
	/**
	 *  Create a new bean attribute info. 
	 */
	public AttributeInfo(String xmlattributename, Object attributeidentifier, String ignore, ITypeConverter converterread, ITypeConverter converterwrite, Object defaultvalue, boolean writeastag)
	{
		this(xmlattributename==null? null: QName.valueOf(xmlattributename), attributeidentifier, ignore, converterread, converterwrite, defaultvalue, writeastag);
	}
	
	/**
	 *  Create a new bean attribute info. 
	 */
	public AttributeInfo(String xmlattributename, Object attributeidentifier, String ignore, ITypeConverter converterread, ITypeConverter converterwrite, Object defaultvalue, boolean writeastag, String id)
	{
		this(xmlattributename==null? null: QName.valueOf(xmlattributename), attributeidentifier, ignore, converterread, converterwrite, defaultvalue, writeastag, id);
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

	/**
	 *  Get the id.
	 *  @return The id.
	 */
	public String getId()
	{
		return id;
	}

	/**
	 *  Set the id.
	 *  @param id The id to set.
	 */
	public void setId(String id)
	{
		this.id = id;
	}
	
}
