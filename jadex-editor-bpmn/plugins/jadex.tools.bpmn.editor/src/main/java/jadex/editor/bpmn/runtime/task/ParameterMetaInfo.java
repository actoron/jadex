package jadex.editor.bpmn.runtime.task;

/**
 *  Meta information for a parameter.
 */
public class ParameterMetaInfo implements IEditorParameterMetaInfo
{
	//-------- constants --------
	
	/** The constant for direction in. */
	public static String DIRECTION_IN = "in";
	
	/** The constant for direction out. */
	public static String DIRECTION_OUT = "out";

	/** The constant for direction inout. */
	public static String DIRECTION_INOUT = "inout";
	
	//-------- attributes --------
	
	/** The direction. */
	protected String direction;
	
	/** The clazz. */
	protected Class<?> clazz;
	
	/** The name. */
	protected String name;
	
	/** The initial value. */
	protected String initialval;
	
	/** The parameter description. */
	protected String description;

	//-------- constructors --------
	
	/**
	 *  Create a new parameter meta info.
	 */
	public ParameterMetaInfo(String direction, Class<?> clazz, String name, String initialval, String description)
	{
		this.direction = direction;
		this.clazz = clazz;
		this.name = name;
		this.initialval = initialval;
		this.description = description;
	}
		
	//-------- methods --------
	
	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.IEditorParameterMetaInfo#getDirection()
	 */
	@Override
	public String getDirection()
	{
		return this.direction;
	}

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.IEditorParameterMetaInfo#getClazz()
	 */
	@Override
	public Class<?> getClazz()
	{
		return this.clazz;
	}

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.IEditorParameterMetaInfo#getName()
	 */
	@Override
	public String getName()
	{
		return this.name;
	}

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.IEditorParameterMetaInfo#getInitialValue()
	 */
	@Override
	public String getInitialValue()
	{
		return this.initialval;
	}

	/* (non-Javadoc)
	 * @see jadex.tools.bpmn.runtime.task.IEditorParameterMetaInfo#getDescription()
	 */
	@Override
	public String getDescription()
	{
		return this.description;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "ParameterMetaInfo(clazz=" + this.clazz + ", direction="
			+ this.direction + ", initialval=" + this.initialval
			+ ", name=" + this.name + ", description=" + this.description +")";
	}	
}
