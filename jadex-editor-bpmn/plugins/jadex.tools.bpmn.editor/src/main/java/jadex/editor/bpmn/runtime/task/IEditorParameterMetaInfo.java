package jadex.editor.bpmn.runtime.task;

public interface IEditorParameterMetaInfo
{

	/**
	 *  Get the direction.
	 *  @return The direction.
	 */
	public abstract String getDirection();
	/** The implementing getDirection() method name*/
	public static final String METHOD_IJADEXPARAMETERMETAINFO_GET_DIRECTION = "getDirection";
	
	
	/**
	 *  Get the clazz.
	 *  @return The clazz.
	 */
	public abstract Class<?> getClazz();
	/** The implementing getClazz() method name*/
	public static final String METHOD_IJADEXPARAMETERMETAINFO_GET_CLAZZ = "getClazz";
	
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public abstract String getName();
	/** The implementing getName() method name*/
	public static final String METHOD_IJADEXPARAMETERMETAINFO_GET_NAME = "getName";
	

	/**
	 *  Get the initialval.
	 *  @return The initialval.
	 */
	public abstract String getInitialValue();
	/** The implementing getInitialValue() method name*/
	public static final String METHOD_IJADEXPARAMETERMETAINFO_GET_INITIAL_VALUE = "getInitialValue";
	
	
	/**
	 *  Get the description.
	 *  @return The description.
	 */
	public abstract String getDescription();
	/** The implementing getDescription() method name*/
	public static final String METHOD_IJADEXPARAMETERMETAINFO_GET_DESCRIPTION = "getDescription";
	

}
