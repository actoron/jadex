package jadex.gpmn.editor.gui;

/**
 *  Class containing constants for fine-tuning the GUI.
 *
 */
public class GuiConstants
{
	/* Style Constants */
	/** Goal type marker style. */
	public static final String GOAL_TYPE_STYLE = "Goal Type Style";
	
	/** Referred plan style. */
	public static final String REF_PLAN_STYLE = "Ref Plan Style";
	
	/** Activation plan style. */
	public static final String ACTIVATION_PLAN_STYLE = "Activation Plan Style";
	
	/** Plan type marker style. */
	public static final String PLAN_TYPE_STYLE = "Plan Type Style";
	
	/** Plan mode marker style. */
	public static final String PLAN_MODE_STYLE = "Plan Mode Style";
	
	/** Plan edge style. */
	public static final String PLAN_EDGE_STYLE = "Plan Edge Style";
	
	/** Activation edge style. */
	public static final String ACTIVATION_EDGE_STYLE = "Activation Edge Style";
	
	/** Suppression edge style. */
	public static final String SUPPRESSION_EDGE_STYLE = "Suppression Edge Style";
	
	/** Virtual activation edge style. */
	public static final String VIRTUAL_ACTIVATION_EDGE_STYLE = "Virtual Activation Edge Style";
	
	/** Virtual activation edge marker style. */
	public static final String VIRTUAL_ACTIVATION_EDGE_MARKER_STYLE = "Virtual Activation Edge Marker Style";
	
	/** Colors and Icons*/
	
	/** The base icon size. */
	public static final int BASE_ICON_SIZE = 256;
	
	/** The default icon size used. */
	public static final int DEFAULT_ICON_SIZE = 32;
	
	/** The available icon sizes. */
	public static final int[] ICON_SIZES = new int[] { 16, 24, 32, 48, 64, 96, 128 };
	
	/** Amplification factor for highlighted icons */
	public static final float HIGHLIGHT_AMP = 1.15f;
	
	/** Achieve Goal Color */
	public static final String ACHIEVE_GOAL_COLOR  = "#a6c198";
	
	/** Perform Goal Color */
	public static final String PERFORM_GOAL_COLOR  = "#9bc7cc";
	
	/** Maintain Goal Color */
	public static final String MAINTAIN_GOAL_COLOR = "#e1e187";
	
	/** Query Goal Color */
	public static final String QUERY_GOAL_COLOR    = "#c19898";
	
	/** Activation Plan Color */
	public static final String ACTIVATION_PLAN_COLOR  = "#e3a49c";
	
	/** Plan Color */
	public static final String REF_PLAN_COLOR  = "#b897c0";
	
	/** Suppression Edge Color */
	public static final String SUPPRESSION_EDGE_COLOR  = "bd5eea";
	
	/** Select Color */
	public static final String SELECT_COLOR  = "88cfc0";
	
	/** Control Point Color */
	public static final String CONTROL_POINT_COLOR  = "cfc688";
	//public static final String CONTROL_POINT_COLOR  = "dfff88";
	
	/* GUI Configuration */
	/** Ratio between graph and property view. */
	public static final double GRAPH_PROPERTY_RATIO = 0.7;
	
	/** Frames per second for animations */
	public static final int ANIMATION_FPS = 60;
	
	/** Time between animation frames in milliseconds, depends on FPS. */
	public static final int ANIMATION_FRAME_TIME = (int) Math.round(1000.0 / ANIMATION_FPS);
	
	/** Factor by which the mouse shifts the view during zoom. */
	public static final double ZOOM_MOUSE_DIRECTION_FACTOR = 2.5;
	
	/** Ratio how close a zoom animation should get before "locking in" on the final scale */
	public static final double ZOOM_ANIMATION_FINAL_RATIO = 0.995;
	
	/** Ratio of distance from final scale used as step size. */
	public static final double ZOOM_ANIMATION_STEP_RATIO = 1.2 / ANIMATION_FRAME_TIME;
	
	/** Speed-up for pan-throwing. */
	public static final double THROW_ANIMATION_VELOCITY_SPEEDUP = 0.235 * ANIMATION_FRAME_TIME;
	
	/** Time in milliseconds the throw animation is not slowed. */
	public static final double THROW_ANIMATION_UNSLOWED_TIME = 500;
	
	/** Resistance for slowing the pan-throw. */
	public static final double THROW_ANIMATION_RESISTANCE = 0.05;
	
	/** Velocity cut-off for pan-throwing. */
	public static final double THROW_ANIMATION_VELOCITY_CUTOFF = 0.05;
	
	/** Default visual width of goals. */
	public static final int DEFAULT_GOAL_WIDTH = 120;
	
	/** Default visual height of goals. */
	public static final int DEFAULT_GOAL_HEIGHT = 80;
	
	/** Default visual width of plans. */
	public static final int DEFAULT_PLAN_WIDTH = 120;
	
	/** Default visual height of plans. */
	public static final int DEFAULT_PLAN_HEIGHT = 80;
	
	/** Width of goal markers. */
	public static final int GOAL_MARKER_WIDTH = 24;
	
	/** Height of goal markers. */
	public static final int GOAL_MARKER_HEIGHT = 16;
	
	/** Width of activation plan markers. */
	public static final int PLAN_ACTIVATION_MARKER_WIDTH = 64;
	
	/** Width of ref plan markers. */
	public static final int PLAN_REF_MARKER_WIDTH = 48;
	
	/** Height of plan markers. */
	public static final int PLAN_MARKER_HEIGHT = 16;
	
	/** Height of plan mode markers. */
	public static final int PLAN_MODE_MARKER_HEIGHT = 16;
	
	/** Width of plan mode markers. */
	public static final int PLAN_MODE_MARKER_WIDTH = 24;
	
	/** Width of virtual activation edge markers. */
	public static final int VAE_MARKER_WIDTH = 16;
	
	/** Height of virtual activation edge markers. */
	public static final int VAE_MARKER_HEIGHT = 16;
	
}
