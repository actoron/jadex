package jadex.xml;


/**
 *  Info object for subobjects, i.e. objects that are contained in another object.
 */
public class SubobjectInfo extends AbstractInfo
{
	//-------- attributes --------
	
	/** The access info. */
	protected AccessInfo accessinfo;
	
	/** The subobject converter. */
	protected ISubObjectConverter converter;

	/** The multiplicity. */
	protected boolean multi;

	/** The object info (for writing). */
	protected ObjectInfo objectinfo;
	
	/** The flattening flag. */
	protected Boolean flattening;
	
	//-------- constructors --------
	
	/**
	 *  Create a new subobject info.
	 */
	public SubobjectInfo(XMLInfo xmlinfo, AccessInfo accessinfo)
	{
		this(xmlinfo, accessinfo, null);
	}
	
	/**
	 *  Create a new subobject info.
	 */
	public SubobjectInfo(XMLInfo xmlinfo, AccessInfo accessinfo, ISubObjectConverter converter)
	{
		this(xmlinfo, accessinfo, converter, false);
	}
	
	/**
	 *  Create a new subobject info.
	 */
	public SubobjectInfo(XMLInfo xmlinfo, AccessInfo accessinfo, ISubObjectConverter converter, boolean multi)
	{
		this(xmlinfo, accessinfo, converter, multi, null);
	}
	
	/**
	 *  Create a new subobject info.
	 */
	public SubobjectInfo(XMLInfo xmlinfo, AccessInfo accessinfo, ISubObjectConverter converter, boolean multi, ObjectInfo objectinfo)
	{
		this(xmlinfo, accessinfo, converter, multi, objectinfo, null);
	}
	
	/**
	 *  Create a new subobject info.
	 */
	public SubobjectInfo(XMLInfo xmlinfo, AccessInfo accessinfo, ISubObjectConverter converter, boolean multi, ObjectInfo objectinfo, Boolean flattening)
	{
		super(xmlinfo);
		this.accessinfo = accessinfo;
		this.converter = converter;
		this.multi = multi;
		this.objectinfo = objectinfo;
		this.flattening = flattening;
	}
	
	/**
	 *  Create a new subobject info.
	 */
	public SubobjectInfo(AccessInfo accessinfo)
	{
		this(accessinfo, null);
	}
	
	/**
	 *  Create a new subobject info.
	 */
	public SubobjectInfo(AccessInfo accessinfo, ISubObjectConverter converter)
	{
		this(accessinfo, converter, false);
	}
	
	/**
	 *  Create a new subobject info.
	 */
	public SubobjectInfo(AccessInfo accessinfo, ISubObjectConverter converter, boolean multi)
	{
		this(accessinfo, converter, multi, null);
	}
	
	/**
	 *  Create a new subobject info.
	 */
	public SubobjectInfo(AccessInfo accessinfo, ISubObjectConverter converter, boolean multi, ObjectInfo objectinfo)
	{
		this(new XMLInfo(accessinfo.getXmlObjectNames()), accessinfo, converter, multi, objectinfo);
	}
	
	/**
	 *  Create a new subobject info.
	 */
	public SubobjectInfo(AccessInfo accessinfo, ISubObjectConverter converter, boolean multi, ObjectInfo objectinfo, Boolean flattening)
	{
		this(new XMLInfo(accessinfo.getXmlObjectNames()), accessinfo, converter, multi, objectinfo, flattening);
	}

	
	//-------- methods --------
	
	/**
	 *  Get the link info.
	 *  @return The link info.
	 */
	public AccessInfo getAccessInfo()
	{
		return this.accessinfo;
	}
	
	/**
	 *  Get the converter.
	 *  @return The converter.
	 */
	public ISubObjectConverter getConverter()
	{
		return this.converter;
	}

	/**
	 *  Test if it is a multi subobject.
	 *  @return True, if multi.
	 * /
	public Boolean getMulti()
	{
		return multi;
	}*/
	
	/**
	 *  Test if it is a multi subobject.
	 *  @return True, if multi.
	 */
	public boolean isMulti()
	{
		return multi;
	}
	
	/**
	 *  Get the objectinfo.
	 *  @return The objectinfo.
	 */
	public ObjectInfo getObjectInfo()
	{
		return this.objectinfo;
	}

	/**
	 *  Get the flattening.
	 *  @return The flattening.
	 */
	public Boolean getFlattening()
	{
		return this.flattening;
	}
	
}
