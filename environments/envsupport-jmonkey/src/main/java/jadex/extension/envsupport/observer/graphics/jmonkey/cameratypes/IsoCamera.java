package jadex.extension.envsupport.observer.graphics.jmonkey.cameratypes;

import com.jme3.input.InputManager;
import com.jme3.math.FastMath;
import com.jme3.renderer.Camera;
import com.jme3.scene.Node;

import jadex.extension.envsupport.observer.graphics.jmonkey.MonkeyApp;

/**
 * The camera for the game
 *
 */
public class IsoCamera 
{
    private Node camNode;
    private Camera cam;
    private boolean rotating;
    private CustomChaseCamera chaseCam;
    private InputManager inputManager;
    private int moveSpeed = 100, zoomSpeed = 1, rotationSpeed = 5; //max speeds
    private int minDist = 50, maxDist = 300;
    private float acceleration = 20;
    private MonkeyApp app;


    
    public IsoCamera(Camera cam, InputManager inputManager, MonkeyApp app)
    {
        this.cam = cam;
        camNode = new Node("camNode");
        camNode.setLocalTranslation(cam.getLocation());

        chaseCam = new CustomChaseCamera(cam, camNode, inputManager);
        chaseCam.setDragToRotate(true);
        chaseCam.setToggleRotationTrigger(Triggers.toggleRotate);
        chaseCam.setInvertVerticalAxis(true);
        chaseCam.setInvertHorizontalAxis(false);
        chaseCam.setMinVerticalRotation(0.50f);
        chaseCam.setMaxVerticalRotation(FastMath.HALF_PI - 0.05f);
        chaseCam.setSmoothMotion(false);
        chaseCam.setChasingSensitivity(acceleration);
        chaseCam.setZoomInTrigger(Triggers.zoomInTrigger);
        chaseCam.setZoomOutTrigger(Triggers.zoomOutTrigger);
        chaseCam.setMinDistance(minDist);
        chaseCam.setMaxDistance(maxDist);
        chaseCam.setTrailingEnabled(false);
        chaseCam.setRotationSensitivity(rotationSpeed);
        chaseCam.setZoomSensitivity(zoomSpeed);
        this.app = app;
        

    }


    public void setInvertScrolling(boolean b)
    {
        if(b) {
            chaseCam.setZoomInTrigger(Triggers.zoomOutTrigger);
            chaseCam.setZoomOutTrigger(Triggers.zoomInTrigger);
        }else {
            chaseCam.setZoomInTrigger(Triggers.zoomInTrigger);
            chaseCam.setZoomOutTrigger(Triggers.zoomOutTrigger);
        }
    }

    /**
     * @return the rotating
     */
    public boolean isRotating()
    {
        return rotating;
    }

    /**
     * @param rotating the rotating to set
     */
    public void setRotating(boolean rotating)
    {
        this.rotating = rotating;
    }

	public Node getCamNode()
	{
		return camNode;
	}

	public void setCamNode(Node camNode)
	{
		this.camNode = camNode;
	}

	public Camera getCam()
	{
		return cam;
	}

	public void setCam(Camera cam)
	{
		this.cam = cam;
	}

	public CustomChaseCamera getChaseCam()
	{
		return chaseCam;
	}

	public void setChaseCam(CustomChaseCamera chaseCam)
	{
		this.chaseCam = chaseCam;
	}

	public InputManager getInputManager()
	{
		return inputManager;
	}

	public void setInputManager(InputManager inputManager)
	{
		this.inputManager = inputManager;
	}

	public int getMoveSpeed()
	{
		return moveSpeed;
	}

	public void setMoveSpeed(int moveSpeed)
	{
		this.moveSpeed = moveSpeed;
	}

	public int getZoomSpeed()
	{
		return zoomSpeed;
	}

	public void setZoomSpeed(int zoomSpeed)
	{
		this.zoomSpeed = zoomSpeed;
	}

	public int getRotationSpeed()
	{
		return rotationSpeed;
	}

	public void setRotationSpeed(int rotationSpeed)
	{
		this.rotationSpeed = rotationSpeed;
	}

	public int getMinDist()
	{
		return minDist;
	}

	public void setMinDist(int minDist)
	{
		this.minDist = minDist;
	}

	public int getMaxDist()
	{
		return maxDist;
	}

	public void setMaxDist(int maxDist)
	{
		this.maxDist = maxDist;
	}

	public float getAcceleration()
	{
		return acceleration;
	}

	public void setAcceleration(float acceleration)
	{
		this.acceleration = acceleration;
	}

	public MonkeyApp getApp()
	{
		return app;
	}

	public void setApp(MonkeyApp app)
	{
		this.app = app;
	}

}
