package jadex.adapter.base.envsupport.environment;


/**
 * 
 */
public class EnvironmentEvent
{
	public static final String OBJECT_CREATED = "created";
	
	public static final String OBJECT_DESTROYED = "destroyed";

	// move to other class?
	public static final String OBJECT_POSITION_CHANGED = "position_changed";
	
	/** The type. */
	protected String type;
	
	/** The source. */
	protected IEnvironmentSpace space;
	
	/** The space object. */
	protected ISpaceObject spaceobject;
	
	/** The info object for additional information (optional and event type dependent). */
	protected Object info;
	
	/**
	 * 
	 */
	public EnvironmentEvent(String type, IEnvironmentSpace space, ISpaceObject object, Object info)
	{
		this.type = type;
		this.space = space;
		this.spaceobject = object;
		this.info = info;
	}

	/**
	 * @return the type
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 * @return the source
	 */
	public IEnvironmentSpace getSpace()
	{
		return this.space;
	}

	/**
	 * @return the spaceobject
	 */
	public ISpaceObject getSpaceObject()
	{
		return this.spaceobject;
	}

	/**
	 * @return the info
	 */
	public Object getInfo()
	{
		return this.info;
	}
	
	
}
