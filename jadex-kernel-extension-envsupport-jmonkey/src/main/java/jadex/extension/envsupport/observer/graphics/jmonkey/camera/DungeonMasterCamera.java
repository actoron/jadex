package jadex.extension.envsupport.observer.graphics.jmonkey.camera;

import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.math.FastMath;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.Control;

import jadex.extension.envsupport.observer.graphics.jmonkey.MonkeyApp;

import java.awt.Dimension;
import java.io.IOException;

/**
 * The camera for the game
 *
 */
public class DungeonMasterCamera implements Control, AnalogListener, ActionListener
{
    private Node camNode, rootNode;
    private Camera cam;
    private boolean rotating;
    private CustomChaseCamera chaseCam;
    private InputManager inputManager;
    private int moveSpeed = 100, zoomSpeed = 1, rotationSpeed = 5; //max speeds
    private int minDist = 50, maxDist = 300;
    private float acceleration = 20;
    private MonkeyApp app;

    private float borderMovement = 30;
    
    private boolean MOUSE_LEFT = false;
    private boolean MOUSE_RIGHT = false;
    private boolean MOUSE_UP = false;
    private boolean MOUSE_DOWN = false;
    
    public DungeonMasterCamera(Camera cam, InputManager inputManager, Spatial target, Node rootNode, MonkeyApp app)
    {
        this.cam = cam;
        camNode = new Node();
        camNode.setLocalTranslation(cam.getLocation());
        this.rootNode = rootNode;
        rootNode.attachChild(camNode);
//        registerInput(inputManager);
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
        
   
        target.addControl(this);
        registerInput(inputManager);
    }

    public void registerInput(InputManager inputManager)
    {
        this.inputManager = inputManager;

        String[] mappings = new String[]{"+X", "-X", "+Y", "-Y"};
        //Moving
        inputManager.addMapping(mappings[0], Triggers.rightTrigger);
        inputManager.addMapping(mappings[1], Triggers.leftTrigger);
        inputManager.addMapping(mappings[2], Triggers.upTrigger);
        inputManager.addMapping(mappings[3], Triggers.downTrigger);
        inputManager.addMapping(mappings[0], Triggers.rights);
        inputManager.addMapping(mappings[1], Triggers.lefts);
        inputManager.addMapping(mappings[2], Triggers.ups);
        inputManager.addMapping(mappings[3], Triggers.downs);
        
		String mouseMovement = "mousemovement";
		inputManager.addMapping(mouseMovement, Triggers.downsmouse);
		inputManager.addMapping(mouseMovement, Triggers.upsmouse);
		inputManager.addMapping(mouseMovement, Triggers.leftsmouse);
		inputManager.addMapping(mouseMovement, Triggers.rightsmouse);
        
//        inputManager.addMapping(mappings[0], Triggers.rightsmouse);
//        inputManager.addMapping(mappings[1], Triggers.leftsmouse);
//        inputManager.addMapping(mappings[2], Triggers.upsmouse);
//        inputManager.addMapping(mappings[3], Triggers.downsmouse);
        
        inputManager.addMapping("rotate", Triggers.toggleRotate);
        inputManager.addListener(this, "rotate");
        inputManager.addMapping("Get angle", new com.jme3.input.controls.KeyTrigger(KeyInput.KEY_T));
        inputManager.addListener(this, "Get angle");

        inputManager.addListener(this, mappings);
        inputManager.addListener(this, mouseMovement);
    }

    public void onAction(String name, boolean isPressed, float tpf)
    {
        if(name.equals("rotate")) {
            if(isPressed) {
                setRotating(true);
            }else {
                setRotating(false);
            }
        }
        if(name.equals("Get angle") && isPressed) {
            System.out.println(chaseCam.getDistanceToTarget());
        }
    }


    public void onAnalog(String name, float value, float tpf)
    {
        Vector3f camDir = cam.getDirection().clone().multLocal(0.8f);
        camDir = camDir.setY(0.0f); //Ignore up and down when moving forward
        camDir = camDir.normalizeLocal();
        Vector3f camLeft = cam.getLeft().clone().multLocal(0.5f);
        camLeft.setY(0.0f);
        
        Vector3f direction = new Vector3f(0, 0, 0);
        
        if(name.equals("mousemovement"))
        {

        	Vector2f click2d = inputManager.getCursorPosition();
        	System.out.println("click2d " + click2d);
        	


        	
        	Dimension candim = app.getCanvassize();
        	
        	System.out.println("click2d" + click2d);
        	if(candim!=null)
        	{
            	double height = candim.getHeight();
            	double width = candim.getWidth();

            	MOUSE_LEFT = click2d.getX()<borderMovement ?  true :  false;
            	MOUSE_RIGHT = click2d.getX()>width-borderMovement ?  true :  false;
            	MOUSE_UP = click2d.getY()>height-borderMovement ?  true :  false;
            	MOUSE_DOWN = click2d.getY()<borderMovement ?  true :  false;

        	}
        	
        	

        	
        	
        }

        //If camera is moving

        if(name.equals("-X")) // || inputManager.getCursorPosition().x < 5
        {
            direction.addLocal(camLeft).normalizeLocal();
        }
        if(name.equals("+X")) // || inputManager.getCursorPosition().x > settings.getWidth() - 5
        {
            direction.addLocal(camLeft.negate()).normalizeLocal();
        }
        if(name.equals("+Y")) // || inputManager.getCursorPosition().y > settings.getHeight() - 5
        {
            direction.addLocal(camDir).normalizeLocal();
        }
        if(name.equals("-Y")) // || inputManager.getCursorPosition().y < 5
        {
            direction.addLocal(camDir.negate()).normalizeLocal();
        }
        setLocation(camNode.getLocalTranslation().addLocal(direction.multLocal(moveSpeed * tpf)));
    }
    
    public int count = 50;

    public void update(float tpf)
    {
    	Vector2f click2d = inputManager.getCursorPosition();
    	System.out.println("click2d " + click2d);
    	
    	if(MOUSE_LEFT || MOUSE_RIGHT || MOUSE_DOWN || MOUSE_UP)
    	{

    		
    		if(!app.isBlockCamMoving())
    		{
    			
        	count = 50;

            Vector3f camDir = cam.getDirection().clone().multLocal(0.8f);
            camDir = camDir.setY(0.0f); //Ignore up and down when moving forward
            camDir = camDir.normalizeLocal();
            Vector3f camLeft = cam.getLeft().clone().multLocal(0.5f);
            camLeft.setY(0.0f);
            
            Vector3f direction = Vector3f.ZERO;
            
            
        	if(MOUSE_LEFT)
        	{
        		direction.addLocal(camLeft).normalizeLocal();

        	}
        	
        	if(MOUSE_RIGHT)
        	{
        		direction.addLocal(camLeft.negate()).normalizeLocal();
        	}
        	
        	if(MOUSE_DOWN)
        	{
        		direction.addLocal(camDir.negate()).normalizeLocal();

        	}
        	
        	if(MOUSE_UP)
        	{
        		direction.addLocal(camDir).normalizeLocal();
        	}
        	

            	setLocation(camNode.getLocalTranslation().addLocal(direction.multLocal(moveSpeed/2 * tpf)));
            	
    		}
    		else
    		{
            	MOUSE_LEFT =  false;
            	MOUSE_RIGHT =   false;
            	MOUSE_UP =   false;
            	MOUSE_DOWN =  false;
				count--;
				System.out.println("count: " + count);
    			if(count < 0)
    			{
    				app.setBlockCamMoving(false);
    			}
    		}

    	}
    	

    	

    	
    }

    public void setLocation(Vector3f pos)
    {
        camNode.setLocalTranslation(pos);
    }

    public Control cloneForSpatial(Spatial spatial)
    {
        DungeonMasterCamera other = new DungeonMasterCamera(cam, inputManager, spatial, rootNode, app);
        return other;
    }

    public boolean isEnabled()
    {
        return true;
    }

    public void setSpatial(Spatial spatial)
    {
    }

    public void setEnabled(boolean enabled)
    {
    }

    public void render(RenderManager rm, ViewPort vp)
    {
    }

    public void write(JmeExporter ex) throws IOException
    {
    }

    public void read(JmeImporter im) throws IOException
    {
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
}
