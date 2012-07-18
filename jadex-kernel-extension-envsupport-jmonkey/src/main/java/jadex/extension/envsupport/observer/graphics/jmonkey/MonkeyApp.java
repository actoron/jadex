package jadex.extension.envsupport.observer.graphics.jmonkey;

import jadex.extension.envsupport.observer.graphics.jmonkey.camera.FlyCamera;
import jadex.extension.envsupport.observer.graphics.jmonkey.controller.GuiController;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
import com.jme3.input.ChaseCamera;
import com.jme3.input.FlyByCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.BasicShadowRenderer;
import com.jme3.shadow.PssmShadowRenderer;
import com.jme3.system.JmeCanvasContext;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.HillHeightMap;

import de.lessvoid.nifty.Nifty;


/**
 * The Application that renders the 3d output for Jadex in the Jmonkey Engine
 * 
 * @author 7willuwe
 */
public class MonkeyApp extends SimpleApplication implements AnimEventListener
{

	private boolean			_walkCam;
	
	private boolean			 _chaseCamera;

	private float			_appDimension;

	private float			_spaceSize;

	private Node			_geometryNode;

	private Node			_gridNode;

	private Node			_staticNode;

	private TerrainQuad		_terrain;

	private Spatial			_selectedSpatial;

	private ChaseCamera		_chaseCam;

	// Helper Classes jop
	private monkeyApp_Grid	_gridHandler;

	private int				_selectedTarget;
	
	private HashMap<String, AnimChannel> _animChannels; 
	
	private PssmShadowRenderer pssmRenderer;
	
	private BasicShadowRenderer bsr;
	
	private boolean _isGrid;
	
	private NiftyJmeDisplay niftyDisplay;
	
	protected FlyCamera flyCamera;
	

	

	public MonkeyApp(float dim, float spaceSize, boolean isGrid)
	{
		_appDimension = dim;
		_isGrid = isGrid;
		_spaceSize = spaceSize;
		_geometryNode = new Node("geometryNode");
		_staticNode = new Node("staticNode");
		_gridNode = new Node("gridNode");
		_walkCam = false;
		_selectedTarget = -1;
		_selectedSpatial = null;

	}

	@Override
	public void simpleInitApp()
	{
		//Base Setup
		Logger.getLogger("").setLevel(Level.SEVERE);
//		Logger.getLogger("de.lessvoid.nifty").setLevel(Level.SEVERE); 
//		Logger.getLogger("NiftyInputEventHandlingLog").setLevel(Level.SEVERE); 
		viewPort.setBackgroundColor(ColorRGBA.LightGray);

		//Init Methods
		initAudio();
		initCam();
		initRoot();
		initRenderer();
		initKeys();
		initNifty();
		
		stateManager.getState(StatsAppState.class).toggleStats();
	}

	
	private void initNifty() {
        niftyDisplay = new NiftyJmeDisplay(
                assetManager, inputManager, audioRenderer, guiViewPort);
        /** Create a new NiftyGUI object */
        Nifty nifty = niftyDisplay.getNifty();
        /** Read your XML and initialize your custom ScreenController */
        nifty.fromXml("jadex3d/interface/BaseHud.xml", "hud", new GuiController(this));

        guiViewPort.addProcessor(niftyDisplay);

//        flyCam.setDragToRotate(true);
		
	}

	private void initRenderer() {
//		  FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
//		  BloomFilter bf=new BloomFilter(BloomFilter.GlowMode.Objects);
//		  fpp.addFilter(bf);
//		  viewPort.addProcessor(fpp);
		
		
//	    bsr = new BasicShadowRenderer(assetManager, 256);
//	    bsr.setDirection(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal()); // light direction
//	    viewPort.addProcessor(bsr);
	    
	    
	    pssmRenderer = new PssmShadowRenderer(assetManager, 1024, 3);
	    pssmRenderer.setDirection(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal()); // light direction
	    pssmRenderer.setShadowIntensity(0.6f);
	    viewPort.addProcessor(pssmRenderer);
	    


	    rootNode.setShadowMode(ShadowMode.Off);
		
	}

	private void initRoot() {
		this.rootNode.attachChild(_staticNode);
		DirectionalLight sun = new DirectionalLight();
		sun.setColor(ColorRGBA.White);
		sun.setDirection(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal());
		rootNode.addLight(sun);

		AmbientLight al = new AmbientLight();
		al.setColor(ColorRGBA.White.mult(0.5f));
		rootNode.addLight(al);
		
		_gridHandler = new monkeyApp_Grid(_appDimension, _spaceSize, assetManager, _isGrid);
		_gridNode = _gridHandler.getGrid();
		this.rootNode.attachChild(_geometryNode);
		
	}

	private void initAudio()
	{

	    
	}
	
	
	/** Custom Keybinding: Map named actions to inputs. */
	private void initKeys()
	{
		
		
		inputManager.addMapping("Select", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
		// You can map one or several inputs to one named action
		inputManager.addMapping("Random", new KeyTrigger(KeyInput.KEY_SPACE));

		inputManager.addMapping("ChaseCam", new KeyTrigger(KeyInput.KEY_F3));
		inputManager.addMapping("FollowCam", new KeyTrigger(KeyInput.KEY_F4));
		inputManager.addMapping("ChangeCam", new KeyTrigger(KeyInput.KEY_F6));
		inputManager.addMapping("Grid", new KeyTrigger(KeyInput.KEY_F8));
		inputManager.addMapping("Fullscreen", new KeyTrigger(KeyInput.KEY_F11));
		inputManager.addMapping("Fullscreen", new KeyTrigger(KeyInput.KEY_F));
		inputManager.addMapping("ZoomIn", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
		inputManager.addMapping("ZoomOut", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));




		ActionListener actionListener = new ActionListener()
		{
			public void onAction(String name, boolean keyPressed, float tpf)
			{
				

				if(keyPressed && name.equals("Fullscreen"))
				{
					fireFullscreen();


				}

				if(keyPressed && name.equals("Random"))
				{
					setHeight();

				}
				if(keyPressed && name.equals("Grid"))
				{

					if(rootNode.getChild("gridNode") != null)
					{
						rootNode.detachChild(_gridNode);

					}
					else
					{
						rootNode.attachChild(_gridNode);
					}

				}


				if(name.equals("Select") && keyPressed)
				{
					fireSelection();
				}

				else if(name.equals("ZoomIn"))
				{
					moveCamera(6, false);
				}
				else if(name.equals("ZoomOut"))
				{
					moveCamera(-6, false);
				}
				else if(keyPressed && name.equals("ChangeCam"))
				{
					_walkCam = !_walkCam;

				}
				
				else if(keyPressed && name.equals("ChaseCam"))
				{
					if(_selectedSpatial != null)
					{
						_chaseCamera = !_chaseCamera;
						
						
						 if(_chaseCamera)
						 {
							
							 _chaseCam.setSpatial(_selectedSpatial);
							 _chaseCam.setEnabled(true);	
							 flyCamera.setEnabled(false);
						 }
						 else
						 {
							 
							 _chaseCam.setEnabled(false);
							 flyCamera.setEnabled(true);
						 }
						 
					}
					else
					{
						
						_chaseCam.setEnabled(false);
						flyCamera.setEnabled(true);
					}

				}
			}



		};

		inputManager.addListener(actionListener, new String[]{"Random"});
		inputManager.addListener(actionListener, new String[]{"Grid"});
		inputManager.addListener(actionListener, new String[]{"ChaseCam"});
		inputManager.addListener(actionListener, new String[]{"ChangeCam"});
		inputManager.addListener(actionListener, new String[]{"ZoomIn"});
		inputManager.addListener(actionListener, new String[]{"ZoomOut"});
		inputManager.addListener(actionListener, new String[]{"Select"});
		inputManager.addListener(actionListener, new String[]{"FollowCam"});
		inputManager.addListener(actionListener, new String[]{"Fullscreen"});
		


	}

	public void fireFullscreen() {
		System.out.println("fullscreen command aus JMonkey");
		
		JmeCanvasContext context = (JmeCanvasContext)getContext();
	
		KeyEvent event = new KeyEvent(context.getCanvas(), KeyEvent.KEY_PRESSED,  EventQueue.getMostRecentEventTime(), 0, KeyEvent.VK_F11, KeyEvent.CHAR_UNDEFINED);

		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent( event );
		
	}
	
	public void initCam()
	{
		_chaseCam = new ChaseCamera(cam, rootNode, inputManager);
		_chaseCam.setSmoothMotion(true);
		_chaseCam.setDragToRotate(false);
		_chaseCam.setDefaultDistance(500f);
		_chaseCam.setMaxDistance(1000f);
		_chaseCam.setMinDistance(1f);
		_chaseCam.setEnabled(false);

		/** Configure cam to look at scene */
		cam.setLocation(new Vector3f(_appDimension * 1.2f, _appDimension / 2, _appDimension / 2));
		cam.lookAt(new Vector3f(_appDimension/2, 0, _appDimension/2), Vector3f.UNIT_Y);
		cam.setFrustumNear(1f);
		cam.setFrustumFar(_appDimension*5);
		flyCam.setEnabled(false);
		
		flyCamera = new FlyCamera(cam);
		flyCamera.setUpVector(Vector3f.UNIT_Y);
		flyCamera.registerWithInput(inputManager);
		flyCamera.setDragToRotate(true);
		
		flyCamera.setEnabled(true);
		flyCamera.setMoveSpeed(_appDimension);

	     // Change the mappings we don't want

 

        
	

		
//		
//		Camera cam_n    = cam.clone();
//		cam.setViewPort( 0.0f , 1.0f   ,   0.0f , 1.0f );
//		cam_n.setViewPort( 0.8f , 1.0f   ,   0.8f , 1.0f );
//		cam_n.setLocation(new Vector3f(_appDimension/2, _appDimension*1.5f, _appDimension/2));
//		cam_n.lookAt(new Vector3f(_appDimension/2, 0, _appDimension/2), Vector3f.UNIT_Y);
		 
		
//		ViewPort view = renderManager.createMainView("View of camera #1", cam);
//		view.setEnabled(true);
//		view.setClearFlags(true, true, true);
//		view.attachScene(rootNode);
//		view.setBackgroundColor(ColorRGBA.Black);
//		
//		ViewPort view_n = renderManager.createMainView("View of camera #2", cam_n);
//		view_n.setEnabled(true);
//		view_n.setClearFlags(true, true, true);
//		view_n.attachScene(rootNode);
//		view_n.setBackgroundColor(ColorRGBA.Black);


	}

	private void moveCamera(float value, boolean sideways)
	{
		Vector3f vel = new Vector3f();
		Vector3f pos = cam.getLocation().clone();

		if(sideways)
		{
			cam.getLeft(vel);
		}
		else
		{
			cam.getDirection(vel);
		}
		vel.multLocal(value * 10);

		pos.addLocal(vel);

		cam.setLocation(pos);
	}

	public Collection<com.jme3.renderer.Caps> getCaps()
	{
		return renderer.getCaps();
	}


	public AssetManager getAssetManager()
	{
		return assetManager;
	}

	public void simpleUpdate(float tpf)
	{		
		if(_walkCam)
		{
			Vector3f loc = cam.getLocation();
			loc.setY(getHeightForCam(loc.x, loc.z));
			cam.setLocation(loc);
		}
		
	    listener.setLocation(cam.getLocation());
	    listener.setRotation(cam.getRotation());

	}

	public Node getGeometry()
	{
		return _geometryNode;
	}

	public void setGeometry(Node geometry)
	{
		_geometryNode = geometry;
		this.rootNode.attachChild(_geometryNode);
	}

	public void setStaticGeometry(Node staticNode)
	{
		_staticNode = staticNode;
		// Add SKY direct to Root
		Spatial sky = staticNode.getChild("Skymap");
		if(sky != null)
		{
			sky.removeFromParent();
			this.rootNode.attachChild(sky);
		}
		// Add TERRAIN direct to Root
		Spatial terra = staticNode.getChild("Terrain");
		if(terra != null)
		{
			terra.removeFromParent();
			ShadowMode mode = terra.getShadowMode();
			_terrain = (TerrainQuad)terra;
			_terrain.setLocalTranslation(_appDimension / 2, 0, _appDimension / 2);
			/** 5. The LOD (level of detail) depends on were the camera is: */
			TerrainLodControl control = new TerrainLodControl(_terrain, cam);
			_terrain.addControl(control);
			_terrain.setShadowMode(ShadowMode.Receive);

			this.rootNode.attachChild(_terrain);
		}
		this.rootNode.attachChild(_staticNode);

	}

	/*
	 * Use only for Camera
	 */
	private float getHeightForCam(float x, float z)
	{
		float height = 0;
		if(_terrain != null)
		{
			Vector2f vec = new Vector2f(x, z);
			height = _terrain.getHeight(vec) +3;
		}

		return height;
	}

	public float getHeightAt(Vector2f vec)
	{
		float height = 0;
		if(_terrain != null)
		{
			vec = vec.mult(_appDimension/_spaceSize);
			height = _terrain.getHeight(vec) / _appDimension  *_spaceSize;
		}

		return height;

	}

	public void setHeight()
	{
		if(_terrain != null)
		{
			HillHeightMap heightmap = null;
			try
			{
				heightmap = new HillHeightMap(513, 2000, 25, 100, (long)((byte)100 * Math.random()));
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			Material mat = _terrain.getMaterial();
			Vector3f scale = _terrain.getLocalScale();
			Vector3f trans = _terrain.getLocalTranslation();
			rootNode.detachChildNamed("Terrain");
			_terrain = new TerrainQuad("Terrain", 65, 513, heightmap.getHeightMap());

			_terrain.setLocalTranslation(trans);
			_terrain.setLocalScale(scale);
			_terrain.setMaterial(mat);
			_terrain.setShadowMode(ShadowMode.Receive);
			rootNode.attachChild(_terrain);
		}
	}

	
	private CollisionResults fireRaytrace()
	{
		_selectedSpatial = null;
		// Reset results list.
		CollisionResults results = new CollisionResults();
		// Convert screen click
		// to 3d position
		Vector2f click2d = inputManager.getCursorPosition();
		Vector3f click3d = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 0f).clone();
		Vector3f dir = cam.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f).subtractLocal(click3d).normalize();
		// Aim the ray from the
		// clicked spot
		// forwards.
		Ray ray = new Ray(click3d, dir);
		// Collect intersections
		// between ray and all
		// nodes in results
		// list.
		// rootNode.collideWith(ray, results);
		_geometryNode.collideWith(ray, results);
		
		return results;
	}



	private void fireSelection()
	{
		CollisionResults results = fireRaytrace();

		int selection = -1;
		Spatial selectedspatial = null;
		if(results.size() > 0)
		{
			Geometry target = results.getClosestCollision().getGeometry();
			// Here comes the
			// action:
			Spatial selectedsp = target;

			// we look for the SpaceObject-Parent
			if(selectedsp != null)
			{
				while(!Character.isDigit(selectedsp.getName().charAt(0)))
				{
					selectedsp = selectedsp.getParent();
				}

				selection = Integer.parseInt(selectedsp.getName());
				selectedspatial = selectedsp;
			}
		}
		setSelectedTarget(selection);
		setSelectedSpatial(selectedspatial);

	} 
	
	
	public void onAnimChange(AnimControl control, AnimChannel channel, String animName)
	{
		// unused?
		
	}

	
	public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName)
	{
		if(channel.getLoopMode()==LoopMode.DontLoop)
		{
			channel.reset(true);
		}
		
	}
	
	
	
	
	
	
	
	// GETTER AND SETTER


	/**
	 * @param _selectedSpatial the _selectedSpatial to set
	 */
	private void setSelectedSpatial(Spatial selectedspatial)
	{
		this._selectedSpatial = selectedspatial;
	}

	/**
	 * @return the _selectedSpatial
	 */
	public Spatial getSelectedSpatialt()
	{
		return _selectedSpatial;
	}

	/**
	 * @return the _selectedTarget
	 */
	public int getSelectedTarget()
	{
		return _selectedTarget;
	}

	/**
	 * @param _selectedTarget the _selectedTarget to set
	 */
	public void setSelectedTarget(int selectedTarget)
	{
		this._selectedTarget = selectedTarget;
	}



	public void setChannels(HashMap<String, AnimChannel> animChannels)
	{
		this._animChannels = animChannels;
		
	}
	
	public HashMap<String, AnimChannel> getChannels()
	{
		return this._animChannels;
		
	}
	


}