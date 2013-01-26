package jadex.extension.envsupport.observer.graphics.jmonkey.appstate.camera;

import java.awt.Dimension;

import jadex.extension.envsupport.observer.graphics.jmonkey.MonkeyApp;
import jadex.extension.envsupport.observer.graphics.jmonkey.cameratypes.IsoCamera;
import jadex.extension.envsupport.observer.graphics.jmonkey.cameratypes.Triggers;

import com.jme3.app.Application;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppStateManager;
import com.jme3.asset.AssetManager;
import com.jme3.input.InputManager;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Node;


public class IsoCameraState extends AbstractAppState
{

	private MonkeyApp		app;

	private Node			rootNode;

	private AssetManager	assetManager;

	private AppStateManager	stateManager;

	private InputManager	inputManager;

	private ViewPort		viewPort;

	private Camera			cam;

	private float			appSize;

	private float			appScaled;

	private String			cameraSelection;

	private Node			camNode;

	private IsoCamera isoCam;
	
	private String[] mappings = new String[]{"+X", "-X", "+Y", "-Y"};
	private String mouseMovement = "mousemovement";
	
    private float borderMovement = 0.1f;
    private float borderMin = 0.02f;
    
    private boolean MOUSE_LEFT = false;
    private boolean MOUSE_RIGHT = false;
    private boolean MOUSE_UP = false;
    private boolean MOUSE_DOWN = false;
    
    private int count = 30;

	public void initialize(AppStateManager stateManager, Application app)
	{
		super.initialize(stateManager, app);
		this.app = (MonkeyApp)app;
		this.rootNode = this.app.getRootNode();
		this.assetManager = this.app.getAssetManager();
		this.stateManager = this.app.getStateManager();
		this.inputManager = this.app.getInputManager();
		this.viewPort = this.app.getViewPort();
		this.cam = this.app.getCamera();
		this.appSize = this.app.getAppSize();
		this.appScaled = this.app.getAppScaled();
		this.cameraSelection = this.app.getCameraSelection();

		camNode = new Node("camNode");


		initCam();
		mapInput();
		initListener();

	}
	
//	 public void update(float tpf)
//	    {
//	    	Vector2f click2d = inputManager.getCursorPosition();
////	    	System.out.println("click2d " + click2d);
//	    	
//	    	if(MOUSE_LEFT || MOUSE_RIGHT || MOUSE_DOWN || MOUSE_UP)
//	    	{
//
//	    		
//	    		if(!app.isCleanupPostFilter())
//	    		{
//	    			
//
//	            Vector3f camDir = cam.getDirection().clone().multLocal(0.8f);
//	            camDir = camDir.setY(0.0f); //Ignore up and down when moving forward
//	            camDir = camDir.normalizeLocal();
//	            Vector3f camLeft = cam.getLeft().clone().multLocal(0.5f);
//	            camLeft.setY(0.0f);
//	            
//	            Vector3f direction = Vector3f.ZERO;
//	            
//	            
//	        	if(MOUSE_LEFT)
//	        	{
//	        		direction.addLocal(camLeft).normalizeLocal();
//
//	        	}
//	        	
//	        	if(MOUSE_RIGHT)
//	        	{
//	        		direction.addLocal(camLeft.negate()).normalizeLocal();
//	        	}
//	        	
//	        	if(MOUSE_DOWN)
//	        	{
//	        		direction.addLocal(camDir.negate()).normalizeLocal();
//
//	        	}
//	        	
//	        	if(MOUSE_UP)
//	        	{
//	        		direction.addLocal(camDir).normalizeLocal();
//	        	}
//	        	
//
//	            	camNode.setLocalTranslation(camNode.getLocalTranslation().addLocal(direction.multLocal(isoCam.getMoveSpeed()/2 * tpf)));
//	            	
//	    		}
//	    		else
//	    		{
//	            	MOUSE_LEFT =  false;
//	            	MOUSE_RIGHT =   false;
//	            	MOUSE_UP =   false;
//	            	MOUSE_DOWN =  false;
//
//	    		}
//
//	    	}
//	    }
	    	

	    	




	public void initCam()
	{
		/** Configure cam to look at scene */
		cam.setLocation(new Vector3f(appSize / 2, 0, appSize / 2));
		cam.lookAt(new Vector3f(appSize / 2, 0, appSize / 2), Vector3f.UNIT_Y);
		cam.setFrustumNear(1f);
		cam.setFrustumFar(appSize * 5);
		
		app.getFlyByCamera().setEnabled(false);

		this.isoCam = new IsoCamera(cam, inputManager, app);
		camNode = isoCam.getCamNode();
		rootNode.attachChild(camNode);
		
		
		
		 Camera cam_map = cam.clone();
		 cam.setViewPort( 0.0f , 1.0f , 0.15f , 1.0f );
		 
		 
		 float height = (cam.getWidth()*0.15f)/cam.getHeight();
		 cam_map.setViewPort( 0 , 0.1f , 0.0f , height );
		
		 cam_map.setLocation(new Vector3f(appSize/2f, appSize/1.5f,
		 appSize/1.9f));
		 cam_map.lookAt(new Vector3f(appSize/2, 0, appSize/2),
		 Vector3f.UNIT_Y);
		 

		 
		 ViewPort view_map = this.app.getRenderManager().createMainView("MapView", cam_map);
		 view_map.setEnabled(true);
		 view_map.setClearFlags(true, true, true);
		 view_map.attachScene(rootNode);
		 view_map.setBackgroundColor(ColorRGBA.Black);
		
		

	}
	
	private void mapInput()
	{
		
        //Moving
        inputManager.addMapping(mappings[0], Triggers.rightTrigger);
        inputManager.addMapping(mappings[1], Triggers.leftTrigger);
        inputManager.addMapping(mappings[2], Triggers.upTrigger);
        inputManager.addMapping(mappings[3], Triggers.downTrigger);
        inputManager.addMapping(mappings[0], Triggers.rights);
        inputManager.addMapping(mappings[1], Triggers.lefts);
        inputManager.addMapping(mappings[2], Triggers.ups);
        inputManager.addMapping(mappings[3], Triggers.downs);
        

		inputManager.addMapping(mouseMovement, Triggers.downsmouse);
		inputManager.addMapping(mouseMovement, Triggers.upsmouse);
		inputManager.addMapping(mouseMovement, Triggers.leftsmouse);
		inputManager.addMapping(mouseMovement, Triggers.rightsmouse);
        
      
        inputManager.addMapping("rotate", Triggers.toggleRotate);
        

    }


    
    
	private void initListener()
	{
		ActionListener actionListener = new ActionListener()
		{
		    
			public void onAction(String name, boolean keyPressed, float tpf)
			{
		        if(name.equals("rotate")) {
		            if(keyPressed) {
		            	isoCam.setRotating(true);
		            }else {
		            	isoCam.setRotating(false);
		            }
		        }
			}
		};
		
		AnalogListener analogListener = new AnalogListener()
		{

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
		        	checkCursor(inputManager.getCursorPosition());
		        }


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
		        camNode.setLocalTranslation(camNode.getLocalTranslation().addLocal(direction.multLocal(isoCam.getMoveSpeed() * tpf)));
				
			}


			
		};
		
        inputManager.addListener(analogListener, mappings);
//        inputManager.addListener(analogListener, mouseMovement);
		
		inputManager.addListener(actionListener, "rotate");
		
	}
	
	private void checkCursor(Vector2f click2d)
	{
    	Dimension candim = app.getCanvassize();
    	

    	if(candim!=null)
    	{
        	double height = candim.getHeight();
        	double width = candim.getWidth();

        	MOUSE_LEFT = click2d.getX()<width*borderMovement ?  true :  false;
        	MOUSE_RIGHT = click2d.getX()>width-width*borderMovement ?  true :  false;
        	MOUSE_UP = click2d.getY()>height-height*borderMovement ?  true :  false;
        	MOUSE_DOWN = click2d.getY()<height*borderMovement ?  true :  false;

        	if(MOUSE_LEFT||MOUSE_RIGHT||MOUSE_UP||MOUSE_DOWN)
        	{
        		if(MOUSE_LEFT)
            	MOUSE_LEFT = click2d.getX()<width*borderMin ?  false : true;
        		
        		if(MOUSE_RIGHT)
            	MOUSE_RIGHT = click2d.getX()>width-width*borderMin ?  false :  true;
            	
            	if(MOUSE_UP)
            	MOUSE_UP = click2d.getY()>height-height*borderMin ?  false :  true;
            	
            	if(MOUSE_DOWN)
            	MOUSE_DOWN = click2d.getY()<height*borderMin ?  false :  true;
        	}
    	}
		
	}
	
	
}