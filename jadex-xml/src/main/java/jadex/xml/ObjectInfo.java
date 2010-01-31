package jadex.xml;

/**
 * 
 */
public class ObjectInfo
{
	/** Constant indicating that the type is not creatable from tag information. */
	public static String NOT_CREATEDABLE_FROM_TAG = "not_creatable_from_tag";

	/** The object type. */
	protected Object typeinfo; // (class if not Ibeancreator)

	/** The post processor (if any). */
	protected IPostProcessor postproc;

	
	/**
	 * @param typeinfo
	 */
	public ObjectInfo(Object typeinfo)
	{
		this(typeinfo, null);
	}

	/**
	 * @param typeinfo
	 */
	public ObjectInfo(Object typeinfo, IPostProcessor postproc)
	{
		this.typeinfo = typeinfo;
		this.postproc = postproc;
	}
	
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

	/**
	 *  Set the postproc.
	 *  @param postproc The postproc to set.
	 */
	public void setPostProcessor(IPostProcessor postproc)
	{
		this.postproc = postproc;
	}

}
