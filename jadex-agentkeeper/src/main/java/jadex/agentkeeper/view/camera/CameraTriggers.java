package jadex.agentkeeper.view.camera;

import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;


/**
 * Setup for the Camera-Triggers
 * 
 * @author Philip Willuweit p.willuweit@gmx.de
 */
public class CameraTriggers
{
	public static Trigger	leftTrigger		= new KeyTrigger(KeyInput.KEY_A);

	public static Trigger	rightTrigger	= new KeyTrigger(KeyInput.KEY_D);

	public static Trigger	downTrigger		= new KeyTrigger(KeyInput.KEY_S);

	public static Trigger	upTrigger		= new KeyTrigger(KeyInput.KEY_W);

	public static Trigger	rotLeftTrigger	= new MouseAxisTrigger(MouseInput.AXIS_X, true);

	public static Trigger	rotRightTrigger	= new MouseAxisTrigger(MouseInput.AXIS_X, false);

	public static Trigger	tiltUpTrigger	= new MouseAxisTrigger(MouseInput.AXIS_Y, true);

	public static Trigger	tiltDownTrigger	= new MouseAxisTrigger(MouseInput.AXIS_Y, false);

	public static Trigger	zoomOutTrigger	= new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true);

	public static Trigger	zoomInTrigger	= new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false);

	public static Trigger	toggleRotate	= new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE);

	public static Trigger	lefts			= new KeyTrigger(KeyInput.KEY_LEFT);

	public static Trigger	rights			= new KeyTrigger(KeyInput.KEY_RIGHT);

	public static Trigger	ups				= new KeyTrigger(KeyInput.KEY_UP);

	public static Trigger	downs			= new KeyTrigger(KeyInput.KEY_DOWN);

	public static Trigger	rightsmouse		= new MouseAxisTrigger(MouseInput.AXIS_X, true);

	public static Trigger	leftsmouse		= new MouseAxisTrigger(MouseInput.AXIS_X, false);

	public static Trigger	upsmouse		= new MouseAxisTrigger(MouseInput.AXIS_Y, true);

	public static Trigger	downsmouse		= new MouseAxisTrigger(MouseInput.AXIS_Y, false);

	public static Trigger	queueButton		= new KeyTrigger(KeyInput.KEY_LSHIFT);

	public static Trigger	secondButton	= new MouseButtonTrigger(MouseInput.BUTTON_RIGHT);

	public static Trigger	actionButton	= new MouseButtonTrigger(MouseInput.BUTTON_LEFT);


}
