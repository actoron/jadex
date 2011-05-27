package jadex.extension.envsupport.environment;


/**
 *  An event in the environment (related to some space object).
 */
public class EnvironmentEvent
{
	//-------- constants --------
	
	/** Event type when an object has been created. */
	public static final String OBJECT_CREATED = "created";
	
	/** Event type when an object has been destroyed. */
	public static final String OBJECT_DESTROYED = "destroyed";

	/** Event type when an object has been changed. */
	public static final String OBJECT_PROPERTY_CHANGED = "property_changed";
	
	//-------- attributes --------
	
	/** The event type. */
	protected String type;
	
	/** The source space. */
	protected IEnvironmentSpace space;
	
	/** The space object. */
	protected ISpaceObject spaceobject;
	
	/** The property (if any). */
	protected String	property;
	
	/** The previous property value (if any). */
	protected Object	oldvalue;
	
	//-------- constructors --------
	
	/**
	 *  Create a new environment event.
	 */
	public EnvironmentEvent(String type, IEnvironmentSpace space, ISpaceObject object, String property, Object oldvalue)
	{
		this.type = type;
		this.space = space;
		this.spaceobject = object;
		this.property = property;
		this.oldvalue	= oldvalue;
	}

	//-------- methods --------
	
	/**
	 *  Get the event type.
	 *  @return The event type.
	 */
	public String getType()
	{
		return this.type;
	}

	/**
	 *  Get the source space.
	 *  @return The source.
	 */
	public IEnvironmentSpace getSpace()
	{
		return this.space;
	}

	/**
	 *  Get the space object.
	 *  @return The space object.
	 */
	public ISpaceObject getSpaceObject()
	{
		return this.spaceobject;
	}

	/**
	 *  Get the property.
	 *  @return The property.
	 */
	public String	getProperty()
	{
		return this.property;
	}

	/**
	 *  Get the previous property value.
	 *  @return The old value.
	 */
	public Object getOldValue()
	{
		return this.oldvalue;
	}
}
