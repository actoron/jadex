package jadex.bpmn.model.task.annotation;


/**
 *  A declared property of a task.
 */
public @interface TaskProperty
{	
	/** The clazz (i.e. type) of the propery. */
	public Class<?> clazz() default Object.class;
	
	/** The property name. */
	public String name();
	
	/** The initial value (as Java expression). */
	public String initialvalue() default "null";
	
	/** The parameter description. */
	public String description() default "";
}
