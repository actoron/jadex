package jadex.xml;

import jadex.xml.bean.BeanAccessInfo;
import jadex.xml.stax.QName;

/**
 *  Meta info for accessing (reading / writing) an element.
 */
public class AccessInfo
{
	//-------- constants --------
	
	/** Constant for identifying this. */
	public static final String THIS = "__this";
	
	/** Ignore when reading. */
	public static final String IGNORE_READ = "ignore_read";
	
	/** Ignore when writing. */
	public static final String IGNORE_WRITE = "ignore_write";

	/** Ignore when reading and writing. */
	public static final String IGNORE_READWRITE = "ignore_readwrite";
	
	//-------- attributes --------

	/** The object identifier. */
	protected Object objectidentifier;

	/** The xml object name. */
	protected QName[] xmlobjectnames;
	
	/** The default value read. */
	protected Object defaultvalue;
	
	/** The ignore property. */
	protected String ignore;
	
	/** The extra info. */
	protected Object extrainfo;


	//-------- constructors --------
	
	/**
	 *  Create a new access info.
	 */
	public AccessInfo(QName xmlobjectname)
	{
		this(xmlobjectname, null);
	}
	
	/**
	 *  Create a new access info.
	 */
	public AccessInfo(QName xmlobjectname, Object objectidentifier)
	{
		this(xmlobjectname, objectidentifier, null);
	}
	
	/**
	 *  Create a new access info.
	 */
	public AccessInfo(QName xmlobjectname, Object objectidentifier, String ignore)
	{
		this(xmlobjectname, objectidentifier, ignore, null);
	}
	
	/**
	 *  Create a new access info.
	 */
	public AccessInfo(QName xmlobjectname, Object objectidentifier,
		String ignore, Object defaultvalue)
	{
		this(xmlobjectname, objectidentifier, ignore, defaultvalue, null);
	}
	
	/**
	 *  Create a new access info.
	 */
	public AccessInfo(QName xmlobjectname, Object objectidentifier,
		String ignore, Object defaultvalue, Object extrainfo)
	{
		this(xmlobjectname!=null ? new QName[]{xmlobjectname} : null, objectidentifier, ignore, defaultvalue, extrainfo);
	}
	
	/**
	 *  Create a new access info.
	 */
	public AccessInfo(QName[] xmlobjectnames)
	{
		this(xmlobjectnames, null);
	}
	
	/**
	 *  Create a new access info.
	 */
	public AccessInfo(QName[] xmlobjectnames, Object objectidentifier)
	{
		this(xmlobjectnames, objectidentifier, null);
	}
	
	/**
	 *  Create a new access info.
	 */
	public AccessInfo(QName[] xmlobjectnames, Object objectidentifier, String ignore)
	{
		this(xmlobjectnames, objectidentifier, ignore, null);
	}
	
	/**
	 *  Create a new access info.
	 */
	public AccessInfo(QName[] xmlobjectnames, Object objectidentifier,
		String ignore, Object defaultvalue)
	{
		this(xmlobjectnames, objectidentifier, ignore, defaultvalue, null);
	}

	/**
	 *  Create a new access info.
	 */
	public AccessInfo(QName[] xmlobjectnames, Object objectidentifier,
		String ignore, Object defaultvalue, Object extrainfo)
	{
		this.xmlobjectnames = xmlobjectnames;
		this.objectidentifier = objectidentifier!=null? objectidentifier: xmlobjectnames!=null? xmlobjectnames[0].getLocalPart(): null;
		this.ignore = ignore;
		this.defaultvalue = defaultvalue;
		this.extrainfo = extrainfo;

		if(ignore!=null && (!IGNORE_READ.equals(ignore) && !IGNORE_WRITE.equals(ignore) && !IGNORE_READWRITE.equals(ignore)))
			throw new RuntimeException("Ignore must have one of predefined values: "+ignore);
	
		if(defaultvalue instanceof BeanAccessInfo)
			throw new RuntimeException("here: "+xmlobjectnames[0]+" "+objectidentifier);
	}

	/**
	 *  Create a new access info.
	 */
	public AccessInfo(String xmlobjectname)
	{
		this(xmlobjectname, null);
	}
	
	/**
	 *  Create a new access info.
	 */
	public AccessInfo(String xmlobjectname, Object objectidentifier)
	{
		this(xmlobjectname, objectidentifier, null);
	}
	
	/**
	 *  Create a new access info.
	 */
	public AccessInfo(String xmlobjectname, Object objectidentifier, String ignore)
	{
		this(xmlobjectname, objectidentifier, ignore, null);
	}
	
	/**
	 *  Create a new access info.
	 */
	public AccessInfo(String xmlobjectname, Object objectidentifier,
		String ignore, Object defaultvalue)
	{
		this(xmlobjectname, objectidentifier, ignore, defaultvalue, null);
	}
	
	/**
	 *  Create a new access info.
	 */
	public AccessInfo(String xmlobjectname, Object objectidentifier,
		String ignore, Object defaultvalue, Object extrainfo)
	{
		this(xmlobjectname!=null? QName.valueOf(xmlobjectname): null, objectidentifier, ignore, defaultvalue, extrainfo);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the objectidentifier.
	 *  @return The objectidentifier.
	 */
	public Object getObjectIdentifier()
	{
		return this.objectidentifier;
	}

	/**
	 *  Get the xmlobjectname.
	 *  @return The xmlobjectname.
	 */
	public QName getXmlObjectName()
	{
		return this.xmlobjectnames!=null ? xmlobjectnames[0] : null;
	}

	/**
	 *  Get the xmlobjectnames.
	 *  @return The xmlobjectnames.
	 */
	public QName[] getXmlObjectNames()
	{
		return this.xmlobjectnames;
	}

	/**
	 *  Get the defaultvalue.
	 *  @return The defaultvalue.
	 */
	public Object getDefaultValue()
	{
		return this.defaultvalue;
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
	 *  Get the extrainfo.
	 *  @return The extrainfo.
	 */
	public Object getExtraInfo()
	{
		return this.extrainfo;
	}
}
