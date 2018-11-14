package jadex.xml;

/**
 *  Information about an object, esp. the object type or its creator.
 */
public class ObjectInfo
{
	//-------- attributes --------
	
	/** The object type. */
	protected Object typeinfo; // (class if not e.g. Ibeancreator)

	/** The post processor (if any). */
	protected IPostProcessor postproc;

	//-------- constructors --------
	
	/**
	 *  Create a new object info.
	 */
	public ObjectInfo(Object typeinfo)
	{
		this(typeinfo, null);
	}

	/**
	 *  Create a new object info.
	 */
	public ObjectInfo(Object typeinfo, IPostProcessor postproc)
	{
		this.typeinfo = typeinfo;
		this.postproc = postproc;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the typeinfo.
	 *  @return The typeinfo.
	 */
	public Object getTypeInfo()
	{
		return typeinfo;
	}

	/**
	 *  Set the typeinfo.
	 *  @param typeinfo The typeinfo to set.
	 */
	public void setTypeInfo(Object typeinfo)
	{
		this.typeinfo = typeinfo;
	}

	/**
	 *  Get the postproc.
	 *  @return The postproc.
	 */
	public IPostProcessor getPostProcessor()
	{
		return postproc;
	}

}
