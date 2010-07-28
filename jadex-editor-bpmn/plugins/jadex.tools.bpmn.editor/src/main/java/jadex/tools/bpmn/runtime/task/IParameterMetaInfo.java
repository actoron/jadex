package jadex.tools.bpmn.runtime.task;

public interface IParameterMetaInfo
{

	/**
	 *  Get the direction.
	 *  @return The direction.
	 */
	public abstract String getDirection();

	/**
	 *  Get the clazz.
	 *  @return The clazz.
	 */
	public abstract Class<?> getClazz();

	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public abstract String getName();

	/**
	 *  Get the initialval.
	 *  @return The initialval.
	 */
	public abstract String getInitialValue();

	/**
	 *  Get the description.
	 *  @return The description.
	 */
	public abstract String getDescription();

}
