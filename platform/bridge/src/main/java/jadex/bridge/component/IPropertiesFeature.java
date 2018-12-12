package jadex.bridge.component;


/**
 *  This features provides properties, i.e. system-level configuration options.
 */
public interface IPropertiesFeature
{
	/**
	 *  Get a property value.
	 *  @param name	The property name.
	 *  @return The property value (if any).
	 */
	public Object	getProperty(String name);
}
