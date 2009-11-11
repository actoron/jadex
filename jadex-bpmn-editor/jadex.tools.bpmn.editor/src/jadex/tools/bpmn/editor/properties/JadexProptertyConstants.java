package jadex.tools.bpmn.editor.properties;

/**
 * Define constants as keys for EAnnotations
 * @author Claas Altschaffel
 */
public interface JadexProptertyConstants {
	
	/** Key for the common annotations of all shapes. */
	public static final String JADEX_COMMON_ANNOTATION = "common";
	
	/** Key for the annotation from the activity shape. */
	public static final String JADEX_ACTIVITY_ANNOTATION = "task";
	
	/** Key for the annotation from the flow connector. */
	public static final String JADEX_FLOW_ANNOTATION = "flow";
	
	
	/** Key for the implementing class of a task. */
	public static final String JADEX_ACTIVITY_TASK_CLASS = "class";
	
	/** Key for the parameter map of a task. */
	public static final String JADEX_ACTIVITY_TASK_PARAMETER_LIST = "prameter";
	
	
	/** Key for the parameter map of a task. */
	public static final String JADEX_FLOW_EXAMPLE_ANNOTATION = "example";
	
	/** Key for the parameter map of a task. */
	public static final String JADEX_FLOW_PARAMETER_MAPPING_LIST = "parameter";
	
}
