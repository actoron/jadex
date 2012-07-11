package jadex.gpmn.editor.model.gpmn;

public class ModelConstants
{
	/* Goal Types */
	/** Marks the goal as achieve goal. */
	public static final String ACHIEVE_GOAL_TYPE  = "Achieve Goal";
	
	/** Marks the goal as perform goal. */
	public static final String PERFORM_GOAL_TYPE  = "Perform Goal";
	
	/** Marks the goal as maintain goal. */
	public static final String MAINTAIN_GOAL_TYPE = "Maintain Goal";
	
	/** Marks the goal as query goal. */
	public static final String QUERY_GOAL_TYPE    = "Query Goal";
	
	/** The goal types. */
	public static final String[] GOAL_TYPES = { ACHIEVE_GOAL_TYPE,
												PERFORM_GOAL_TYPE,
												MAINTAIN_GOAL_TYPE,
												QUERY_GOAL_TYPE };
	
	/** The default goal type */
	public static final String DEFAULT_GOAL_TYPE = ACHIEVE_GOAL_TYPE;
	
	/* Goal Excludes */
	/** Never exclude the goal. */
	public static final String EXCLUDE_NEVER		  = "never";
	
	/** Exclude the goal when tried. */
	public static final String EXCLUDE_WHEN_TRIED	  = "when tried";
	
	/** Exclude the goal when failed. */
	public static final String EXCLUDE_WHEN_FAILED	  = "when failed";
	
	/** Exclude the goal when succeeded. */
	public static final String EXCLUDE_WHEN_SUCCEEDED = "when succeeded";
	
	/** Exclude modes. */
	public static final String[] EXCLUDE_MODES = { EXCLUDE_NEVER,
												   EXCLUDE_WHEN_TRIED,
												   EXCLUDE_WHEN_FAILED,
												   EXCLUDE_WHEN_SUCCEEDED };
	
	/** Default exclude mode. */
	public static final String DEFAULT_EXCLUDE = EXCLUDE_WHEN_TRIED;
	
	/* Condition Languages */
	/** Java Language */
	public static final String LANG_JAVA = "java";
	
	/** JCL Language */
	public static final String LANG_JCL = "jcl";
	
	/** Condition Languages */
	public String[] LANGUAGES = { LANG_JAVA, LANG_JCL };
	
	/** Default language. */
	public String DEFAULT_LANGUAGE = LANG_JAVA;
	
	/** Parallel activation mode. */
	public static final String ACTIVATION_MODE_PARALLEL = "Parallel";
	
	/** Sequential activation mode. */
	public static final String ACTIVATION_MODE_SEQUENTIAL = "Sequential";
	
	/** Activation modes. */
	public static final String[] ACTIVATION_MODES = { ACTIVATION_MODE_PARALLEL,
													  ACTIVATION_MODE_SEQUENTIAL };
	
	/** Default activation mode. */
	public static final String ACTIVATION_MODE_DEFAULT = ACTIVATION_MODE_PARALLEL;
	
	/** The sequential activation plan class */
	public static final String ACTIVATION_PLAN_CLASS_SEQUENTIAL = "jadex.gpmn.plan.SequentialActivationPlan";
	
	/** The parallel activation plan class */
	public static final String ACTIVATION_PLAN_CLASS_PARALLEL = "jadex.gpmn.plan.ParallelActivationPlan";
	
	/** Initial plan */
	public static final String INITAL_PLAN_CLASS = "jadex.gpmn.plan.StartAndMonitorProcessPlan";
}
