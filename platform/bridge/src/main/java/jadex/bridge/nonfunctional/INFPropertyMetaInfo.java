package jadex.bridge.nonfunctional;

import jadex.bridge.nonfunctional.INFProperty.Target;

/**
 * Meta information about a non-functional property.
 */
public interface INFPropertyMetaInfo
{
	/**
	 *  Gets the name of the property.
	 *  @return The name of the property.
	 */
	public String getName();

	/**
	 *  Gets the type of the property.
	 *  This is the Java type of the values.
	 *  @return The type of the property.
	 */
	public Class<?> getType();
	
	/**
	 *  Gets the unit of the property.
	 *  @return The unit of the property.
	 */
	public Class<?> getUnit();

	/**
	 *  Checks if the property is dynamic.
	 *  @return The dynamic.
	 */
	public boolean isDynamic();
	
	/**
	 *  Gets the update rate of the property, if it exists, for dynamic properties.
	 *  @return The update rate.
	 */
	public long getUpdateRate();
	
	/**
	 *  Checks if the property is real time.
	 *  @return The real time flag.
	 */
	public boolean isRealtime();
	
	/**
	 *  Get the target of the property. If the target is not
	 *  the element itself a reference will be created.
	 *  @return The target where the nf property is declared.
	 *  (This element will collect the data).
	 */
	public Target getTarget();
}
