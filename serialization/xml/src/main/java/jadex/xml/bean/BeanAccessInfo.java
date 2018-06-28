package jadex.xml.bean;


/**
 *  Java bean attribute meta information.
 */
public class BeanAccessInfo 
{		
	//-------- attributes --------
	
	/** The field/method for the read process for writing a value in the read process. */
	protected Object storehelp;
	
	/** The write field/method for reading a Java value in the write process. */
	protected Object fetchhelp;
	
	
	/** The map name (if it should be put in map). */
	protected String mapname; // todo: exploit also for writing?!
	
	/** The getter method for getting the key for a map access (if not supplied the xmlname will be used). */
	protected Object keyhelp;
	
	/** The key source. */
	protected boolean keyfromparent;
	
	//-------- constructors --------
	
	/**
	 *  Create a new bean access info. 
	 */
	public BeanAccessInfo(String mapname)
	{
		this(null, null, mapname, null);
	}
	
	/**
	 *  Create a new bean access info. 
	 */
	public BeanAccessInfo(Object storehelp, Object fetchhelp)
	{
		this(storehelp, fetchhelp, null, null);
	}
	
	/**
	 *  Create a new bean access info. 
	 */
	public BeanAccessInfo(Object storehelp, Object fetchhelp, String mapname, Object keyhelp)
	{
		this(storehelp, fetchhelp, mapname, keyhelp, false);
	}
	
	/**
	 *  Create a new bean access info. 
	 */
	public BeanAccessInfo(Object storehelp, Object fetchhelp, String mapname, Object keyhelp, boolean keyfromparent)
	{
		this.storehelp = storehelp;
		this.fetchhelp = fetchhelp;
		this.mapname = mapname;
		this.keyhelp = keyhelp;
		this.keyfromparent = keyfromparent;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the storehelp.
	 *  @return The storehelp.
	 */
	public Object getStoreHelp()
	{
		return this.storehelp;
	}

	/**
	 *  Get the fetchhelp.
	 *  @return The fetchhelp.
	 */
	public Object getFetchHelp()
	{
		return this.fetchhelp;
	}
	
	/**
	 *  Set the map name.
	 *  For attributes that should be mapped to a map.
	 *  @return The mapname.
	 */
	public String getMapName()
	{
		return this.mapname;
	}

	/**
	 *  Get the keyhelp.
	 *  @return The keyhelp.
	 */
	public Object getKeyHelp()
	{
		return this.keyhelp;
	}

	/**
	 *  Get the keyfromparent.
	 *  @return The keyfromparent.
	 */
	public boolean isKeyFromParent()
	{
		return this.keyfromparent;
	}
}
