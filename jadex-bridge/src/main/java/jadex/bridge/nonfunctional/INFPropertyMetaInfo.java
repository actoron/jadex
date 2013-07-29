package jadex.bridge.nonfunctional;

/**
 * Meta information about a non-functional property.
 */
public interface INFPropertyMetaInfo
{
	/**
	 *  Gets the name of the property.
	 *
	 *  @return The name of the property.
	 */
	public String getName();

	/**
	 *  Gets the type of the property.
	 *
	 *  @return The type of the property.
	 */
	public Class<?> getType();
	
	/**
	 *  Gets the unit of the property.
	 *
	 *  @return The unit of the property.
	 */
	public Class<?> getUnit();

	/**
	 *  Checks if the property is dynamic.
	 *
	 *  @return The dynamic.
	 */
	public boolean isDynamic();
	
	/**
	 *  Gets the update rate of the property, if it exists, for dynamic properties.
	 *  
	 *  @return The update rate.
	 */
	public long getUpdateRate();
	
	
}
