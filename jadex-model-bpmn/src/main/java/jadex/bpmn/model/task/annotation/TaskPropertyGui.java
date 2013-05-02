package jadex.bpmn.model.task.annotation;

/**
 * 
 */
public @interface TaskPropertyGui
{
	/**
	 *  Supply a class implementing the gui.
	 */
	public Class<?> value() default Object.class;
	
	/**
	 *  Supply a class name implementing the gui.
	 */
	public String classname() default "";
}
