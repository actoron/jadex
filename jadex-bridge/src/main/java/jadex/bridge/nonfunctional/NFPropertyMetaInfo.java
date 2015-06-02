package jadex.bridge.nonfunctional;

import jadex.bridge.nonfunctional.INFProperty.Target;


/**
 * Meta information about a non-functional property.
 */
public class NFPropertyMetaInfo implements INFPropertyMetaInfo
{
	/** Name of the property. */
	protected String name;
	
	/** Type of the property. */
	protected Class<?> type;
	
	/** Unit of the property value. */
	protected Class<?> unit;
	
	/** Flag indicating if the property is dynamic. */
	protected boolean dynamic;
	
	/** The update rate. */
	protected long updaterate;
	
	/** Flag indicating if the update rate is real time. */
	protected boolean realtime;
	
	/** The target. */
	protected Target target;
	
	
	/**
	 *  Creates an empty meta info.
	 */
	public NFPropertyMetaInfo()
	{
	}
	
	/**
	 *  Creates a meta info.
	 *  
	 *  @param name Name of the property.
	 */
	public NFPropertyMetaInfo(String name, Class<?> type)
	{
		this.name = name;
		this.type = type;
	}
	
	/**
	 *  Creates a meta info.
	 *  
	 *  @param name Name of the property.
	 */
	public NFPropertyMetaInfo(String name, Class<?> type, Class<?> unit, boolean dynamic)
	{
		this.name = name;
		this.type = type;
		this.unit = unit;
		this.dynamic = dynamic;
	}
	
	/**
	 *  Creates a meta info.
	 *  
	 *  @param name Name of the property.
	 *  @param type Type of the property.
	 *  @param unit Unit of the property.
	 *  @param dynamic Flag if the property is dynamic.
	 *  @param updaterate Update rate of the property.
	 *  @param target Target of the property.
	 */
	public NFPropertyMetaInfo(String name, Class<?> type, Class<?> unit, boolean dynamic, long updaterate, boolean realtime, Target target)
	{
		this.name = name;
		this.type = type;
		this.unit = unit;
		this.dynamic = dynamic;
		this.updaterate = updaterate;
		this.target = target;
		this.realtime	= realtime;
	}
	
	/**
	 *  Gets the name of the property.
	 *
	 *  @return The name of the property.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Gets the type of the property.
	 *
	 *  @return The type of the property.
	 */
	public Class<?> getType()
	{
		return type;
	}
	
	/**
	 *  Gets the unit of the property.
	 *
	 *  @return The unit of the property.
	 */
	public Class<?> getUnit()
	{
		return unit;
	}

	/**
	 *  Checks if the property is dynamic.
	 *
	 *  @return The dynamic.
	 */
	public boolean isDynamic()
	{
		return dynamic;
	}
	
	/**
	 *  Gets the update rate of the property, if it exists, for dynamic properties.
	 *  
	 *  @return The update rate.
	 */
	public long getUpdateRate()
	{
		return updaterate;
	}
	
	/**
	 *  Checks if the property is real time.
	 *
	 *  @return The real time flag.
	 */
	public boolean isRealtime()
	{
		return realtime;
	}
	

	/**
	 *  Sets the name of the property.
	 *
	 *  @param name The name of the property.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Sets the type of the property.
	 *
	 *  @param type The type.
	 */
	public void setType(Class<?> type)
	{
		this.type = type;
	}

	/**
	 *  Sets the unit of the property.
	 *
	 *  @param unit The unit of the property.
	 */
	public void setUnit(Class<?> unit)
	{
		this.unit = unit;
	}

	/**
	 *  Sets the dynamic flag of the property.
	 *
	 *  @param dynamic The dynamic flag value.
	 */
	public void setDynamic(boolean dynamic)
	{
		this.dynamic = dynamic;
	}

	/**
	 *  Sets the update rate of the property for dynamic properties.
	 *
	 *  @param updaterate The update rate.
	 */
	public void setUpdateRate(long updaterate)
	{
		this.updaterate = updaterate;
	}

	/**
	 *  Sets the real time flag of the property.
	 *
	 *  @param realtime The real time flag value.
	 */
	public void setRealtime(boolean realtime)
	{
		this.realtime = realtime;
	}
	
	/**
	 *  Get the target.
	 *  @return The target.
	 */
	public Target getTarget()
	{
		return target==null? Target.Self: target;
	}

	/**
	 *  Set the target.
	 *  @param target The target to set.
	 */
	public void setTarget(Target target)
	{
		this.target = target;
	}
}
