package jadex.transformation.jsonserializer.processors;

/**
 * 
 */
public class JsonContext
{
	protected Class<?> componentType;

	/**
	 *  Get the componentType. 
	 *  @return The componentType
	 */
	public Class<?> getComponentType()
	{
		return componentType;
	}

	/**
	 *  Set the componentType.
	 *  @param componentType The componentType to set
	 */
	public void setComponentType(Class<?> componentType)
	{
		this.componentType = componentType;
	}
}
