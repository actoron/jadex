package jadex.extension.envsupport.observer.graphics.jmonkey.cameratypes;

import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.controls.Trigger;

/**
 *
 * @author lohnn
 */
public class Triggers
{
    public static final Trigger leftTrigger = new KeyTrigger(KeyInput.KEY_A),
            rightTrigger = new KeyTrigger(KeyInput.KEY_D),
            downTrigger = new KeyTrigger(KeyInput.KEY_S),
            upTrigger = new KeyTrigger(KeyInput.KEY_W),
            rotLeftTrigger = new MouseAxisTrigger(MouseInput.AXIS_X, true),
            rotRightTrigger = new MouseAxisTrigger(MouseInput.AXIS_X, false),
            tiltUpTrigger = new MouseAxisTrigger(MouseInput.AXIS_Y, true),
            tiltDownTrigger = new MouseAxisTrigger(MouseInput.AXIS_Y, false),
            zoomOutTrigger = new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true),
            zoomInTrigger = new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false),
            toggleRotate = new MouseButtonTrigger(MouseInput.BUTTON_MIDDLE),
                        lefts = new KeyTrigger(KeyInput.KEY_LEFT),
                        rights = new KeyTrigger(KeyInput.KEY_RIGHT),
                        ups = new KeyTrigger(KeyInput.KEY_UP),
                        downs = new KeyTrigger(KeyInput.KEY_DOWN),
                        
                        
            rightsmouse = new MouseAxisTrigger(MouseInput.AXIS_X, true),
         	leftsmouse = new MouseAxisTrigger(MouseInput.AXIS_X, false),
            upsmouse = new	 MouseAxisTrigger(MouseInput.AXIS_Y, true),
            downsmouse = new 	 MouseAxisTrigger(MouseInput.AXIS_Y, false),
            queueButton = new KeyTrigger(KeyInput.KEY_LSHIFT),
            secondButton = new MouseButtonTrigger(MouseInput.BUTTON_RIGHT),
            actionButton = new MouseButtonTrigger(MouseInput.BUTTON_LEFT);
            
            

}
