package jadex.bridge.component;

/**
 *  Feature to retrieve a pojo for the component.
 */
public interface IPojoComponentFeature
{
	/**
	 *  Get the POJO agent object.
	 *  @return The pojo agent.
	 */
	public Object getPojoAgent();
	
	/**
	 *  Get the POJO agent object casted to the pojo class.
	 *  @return The pojo agent.
	 */
	public <T> T getPojoAgent(Class<T> pojoclass);
}
