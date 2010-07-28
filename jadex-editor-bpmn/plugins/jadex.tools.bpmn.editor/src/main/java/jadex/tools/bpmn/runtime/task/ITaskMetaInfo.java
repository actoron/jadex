package jadex.tools.bpmn.runtime.task;

public interface ITaskMetaInfo
{

	/**
	 *  Get the description.
	 *  @return The description.
	 */
	public abstract String getDescription();

	/**
	 *  Get the parameters.
	 *  @return The parameters.
	 */
	public abstract IParameterMetaInfo[] getParameterMetaInfos();

}
