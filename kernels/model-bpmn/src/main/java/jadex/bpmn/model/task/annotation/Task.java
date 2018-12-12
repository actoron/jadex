package jadex.bpmn.model.task.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *  Meta information for a task,
 *  e.g. used by editor to fill in
 *  parameters.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Task
{
	/**
	 *  A human readable description of the task.
	 */
	public String description() default "";
	
	/**
	 *  The parameters of the task.
	 */
	public TaskParameter[] parameters() default {};
	
	/**
	 *  The properties of the task.
	 */
	public TaskProperty[] properties() default {};
	
	/**
	 *  The gui.
	 */
	public TaskPropertyGui gui() default @TaskPropertyGui();
}
