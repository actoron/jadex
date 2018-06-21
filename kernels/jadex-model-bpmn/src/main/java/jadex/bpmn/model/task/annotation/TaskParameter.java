package jadex.bpmn.model.task.annotation;

/**
 *  A declared parameter of a task.
 */
public @interface TaskParameter
{
	//-------- constants --------
	
	/** The constant for direction in (value is read only and assigned before task is started). */
	public static String DIRECTION_IN = "in";
	
	/** The constant for direction out (value is write only and propagated after task is finished). */
	public static String DIRECTION_OUT = "out";

	/** The constant for direction inout (value is read/write). */
	public static String DIRECTION_INOUT = "inout";
	
	//-------- attributes --------
	
	/** The direction (in, out or inout). */
	public String direction() default DIRECTION_INOUT;
	
	/** The clazz (i.e. type) of the parameter. */
	public Class<?> clazz() default Object.class;
	
	/** The parameter name. */
	public String name();
	
	/** The initial value (as Java expression). */
	public String initialvalue() default "null";
	
	/** The parameter description. */
	public String description() default "";
}
