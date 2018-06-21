package jadex.bpmn.editor.gui;

import java.awt.Insets;



/**
 *  This class containing constants for fine-tuning the GUI.
 *
 */
public class GuiConstants
{
	/** Constant 45 degree sine value. */
	public static final double SINE_45 = Math.sin(0.25 * Math.PI);
	
	/** Default button insets. */
	public static final Insets DEFAULT_BUTTON_INSETS = new Insets(2, 2, 2, 2);
	
	/** Ratio between graph and property view. */
	public static final double GRAPH_PROPERTY_RATIO = 0.7;
	
	/** Frames per second for animations */
	public static final int ANIMATION_FPS = 60;
	
	/** Time between animation frames in milliseconds, depends on FPS. */
	public static final int ANIMATION_FRAME_TIME = (int) Math.round(1000.0 / ANIMATION_FPS);
	
	/** The default icon size used. */
	public static final int DEFAULT_ICON_SIZE = 32;
	
	/** The available icon sizes. */
	public static final int[] ICON_SIZES = new int[] { 16, 24, 32, 48, 64, 96, 128 };
	
	/** The default zoom. */
	public static final double DEFAULT_ZOOM = 0.75;
	
	/** The minimum zoom level. */
	public static final int MIN_ZOOM_LEVEL = 20;
	
	/** The maximum zoom level. */
	public static final int MAX_ZOOM_LEVEL = 400;
	
	/** Factor by which the mouse shifts the view during zoom. */
	public static final double ZOOM_MOUSE_DIRECTION_FACTOR = 2.5;
	
	/** Ratio how close a zoom animation should get before "locking in" on the final scale */
	//public static final double ZOOM_ANIMATION_FINAL_RATIO = 0.995;
	
	/** Ratio of distance from final scale used as step size. */
	//public static final double ZOOM_ANIMATION_STEP_RATIO = 1.2 / ANIMATION_FRAME_TIME;
	
	// Tool Colors
	/** Selection Edit Mode Color */
	public static final String SELECT_COLOR  = "88cfc0";
	
	/** Control Point Color */
	public static final String CONTROL_POINT_COLOR  = "cfc688";
	
	/** Control Point Color */
	public static final String MESSAGE_EDGE_COLOR  = "ecc694";
	
	/** Minimum Edge Distance */
	public static final double MIN_EDGE_DIST = 18.0;
	
	/** Shift for newly pasted elements */
	public static final int PASTE_SHIFT = 10;
}
