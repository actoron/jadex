package jadex.adapter.base.envsupport.environment;


/**
 * 
 */
public class EnvironmentEvent
{
	public static final String OBJECT_CREATED = "created";
	
	public static final String OBJECT_DESTRYOED = "destroyed";
	
	/** The type. */
	protected String type;
	
	/** The source. */
	protected IEnvironmentSpace space;
	
	/** The object. */
	protected ISpaceObject object;
	
	/**
	 * 
	 */
	public EnvironmentEvent(String type, IEnvironmentSpace space, ISpaceObject object)
	{
		this.type = type;
		this.space = space;
		this.object = object;
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
	public Object getSource()
	{
		return this.space;
	}

	/**
	 * @return the value
	 */
	public Object getObject()
	{
		return this.object;
	}
}
