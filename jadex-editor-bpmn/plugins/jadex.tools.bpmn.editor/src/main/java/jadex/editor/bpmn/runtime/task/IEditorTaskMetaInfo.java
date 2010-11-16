package jadex.editor.bpmn.runtime.task;

public interface IEditorTaskMetaInfo
{

	/**
	 *  Get the description.
	 *  @return The description.
	 */
	public abstract String getDescription();
	/** getDescription() method name */
	public static final String METHOD_ITASKMETAINFO_GET_DESCRIPTION = "getDescription";
	
	
	/**
	 *  Get the parameters.
	 *  @return The parameters.
	 */
	public abstract IEditorParameterMetaInfo[] getParameterMetaInfos();
	/** getParameterMetaInfos() method name */
	public static final String METHOD_ITASKMETAINFO_GET_PARAMETER_METAINFOS = "getParameterMetaInfos";

}
