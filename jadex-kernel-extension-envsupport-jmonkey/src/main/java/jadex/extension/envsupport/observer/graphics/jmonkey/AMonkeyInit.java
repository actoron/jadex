package jadex.extension.envsupport.observer.graphics.jmonkey;

import java.awt.Dimension;
import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.app.SimpleApplication;
import com.jme3.app.StatsAppState;
import com.jme3.app.state.AbstractAppState;
import com.jme3.app.state.AppState;
import com.jme3.asset.AssetManager;
import com.jme3.audio.Environment;
import com.jme3.effect.ParticleEmitter;
import com.jme3.light.Light;
import com.jme3.math.ColorRGBA;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.post.FilterPostProcessor;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.BatchNode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.shadow.BasicShadowRenderer;
import com.jme3.shadow.PssmShadowRenderer;
import com.jme3.terrain.geomipmap.TerrainQuad;

import de.lessvoid.nifty.Nifty;
// import com.jme3.post.*;
// import com.jme3.post.ssao.SSAOFilter;
import jadex.extension.envsupport.environment.ISpaceController;
import jadex.extension.envsupport.observer.graphics.drawable3d.special.NiftyScreen;
import jadex.extension.envsupport.observer.graphics.jmonkey.appstate.ICustomStateCreator;
import jadex.extension.envsupport.observer.graphics.jmonkey.appstate.camera.DefaultCameraState;
import jadex.extension.envsupport.observer.graphics.jmonkey.appstate.gui.DefaultGuiController;
import jadex.extension.envsupport.observer.graphics.jmonkey.appstate.userinteraction.InteractionState;
import jadex.extension.envsupport.observer.graphics.jmonkey.appstate.userinteraction.SelectionControl;
import jadex.extension.envsupport.observer.graphics.jmonkey.cameratypes.FlyCamera;
import jadex.extension.envsupport.observer.graphics.jmonkey.cameratypes.FocusCamera;
import jadex.extension.envsupport.observer.graphics.jmonkey.util.StringNames;


/**
 * The Second Abstract Application for the renders the 3d output for Jadex in
 * the Jmonkey Engine This Class has most of the Getter and Setter and init
 * Methods from the MonkeyApp for better structure
 * 
 * @author 7willuwe
 */


public abstract class AMonkeyInit extends SimpleApplication implements AnimEventListener
{
	// The Animation Channels for Animations
	protected HashMap<String, AnimChannel>		animChannels;

	protected HashMap<String, ParticleEmitter>	particleEmitters;

	protected ArrayList<Light>					lights				= new ArrayList<Light>();

	protected FilterPostProcessor				fpp;

	// The Terrain
	protected TerrainQuad						terrain;

	// The Cameras
	protected FocusCamera						focusCam;

	protected FlyCamera							flyCamera;

	// The NiftyGUIDisplay
	protected NiftyJmeDisplay					niftyDisplay;

	// Helper Classes jop
	public Node									gridNode;

	// Dimensions
	protected float								appSize;

	protected float								appScaled;

	protected float								spaceSize;

	// The Selected Object by the User as Spatial and ID
	protected Spatial							selectedSpatial;

	protected int								selectedTarget;

	// Shadow Renderer
	protected PssmShadowRenderer				pssmRenderer;

	protected BasicShadowRenderer				bsr;

	// Booleans
	protected boolean							isGrid;

	protected boolean							walkCam;

	protected boolean							hudactive			= false;

	protected boolean							focusCamActive		= false;

	protected boolean							complexShadows		= true;

	protected boolean							cleanupPostFilter	= true;

	protected boolean							ambientOcclusion	= true;

	// TODO: thats crap
	protected String							cameraSelection		= "Default";

	protected ArrayList<String>					toDelete			= new ArrayList<String>();

	protected ArrayList<Spatial>				toAdd				= new ArrayList<Spatial>();

//	Vector3Int									selectedworldcoord;

	BatchNode									staticbatchgeo		= new BatchNode(StringNames.BATCH_NODE);

	Node										staticgeo			= new Node(StringNames.STATIC_NODE);

	protected boolean							defaultGui			= true;

	protected String							guiCreatorPath;

	protected ArrayList<NiftyScreen>			niftyScreens;

	protected ISpaceController					spaceController;

	protected Dimension							canvassize;

	protected DefaultCameraState				cameraState;

	protected SelectionControl					selectionControl;

	protected Node								staticNode;

	private Nifty								nifty;


	public AMonkeyInit(float dim, float appScaled, float spaceSize, boolean isGrid, boolean shader, String camera, String guiCreatorPath,
			ISpaceController spaceController)
	{
		// Set the Variables
		this.appSize = dim;
		this.appScaled = appScaled;
		this.isGrid = isGrid;
		this.spaceSize = spaceSize;
		this.staticNode = new Node("staticNode");
		this.gridNode = new Node("gridNode");
		this.walkCam = false;
		this.selectedTarget = -1;
		this.selectedSpatial = null;
		this.ambientOcclusion = shader;
		this.cameraSelection = camera;
		this.guiCreatorPath = guiCreatorPath;
		this.spaceController = spaceController;

	}

	// Keep loggers to avoid loosings settings due to garbage collection.
	Logger	deflogger;
	Logger	niftylogger;
	Logger	niftyinput;
	
	protected void simpleInit()
	{
		this.fpp = new FilterPostProcessor(assetManager);

		// Base Setup
		deflogger	= Logger.getLogger("");
		niftylogger	= Logger.getLogger("de.lessvoid.nifty");
		niftyinput	= Logger.getLogger("NiftyInputEventHandlingLog");
		deflogger.setLevel(Level.SEVERE);
		niftylogger.setLevel(Level.SEVERE);
		niftyinput.setLevel(Level.SEVERE);

		viewPort.setBackgroundColor(ColorRGBA.Black);
		stateManager.getState(StatsAppState.class).toggleStats();


		initRoot();

		initRenderer(complexShadows);
		initAudio();
		initNifty();
		initCam();
		initSelection();
		initCustom();


	}

	private void initSelection()
	{
		if(guiCreatorPath.equals("None"))
		{
			InteractionState interactionState = new InteractionState();
			stateManager.attach(interactionState);
		}
		this.selectionControl = new SelectionControl();
		stateManager.attach(this.selectionControl);
	}

	private void initRoot()
	{
		this.rootNode.attachChild(this.staticNode);


		monkeyApp_Grid gridHandler = new monkeyApp_Grid(appSize, spaceSize, assetManager, isGrid);
		this.gridNode = gridHandler.getGrid();
		this.staticgeo.setLocalScale(appScaled);
		this.staticbatchgeo.setLocalScale(appScaled);

		this.staticNode.setLocalScale(appScaled);
		this.rootNode.attachChild(staticgeo);
		this.rootNode.attachChild(staticbatchgeo);


	}

	public void initCam()
	{
		if(cameraSelection.equals("Default"))
		{
			cameraState = new DefaultCameraState();
			stateManager.attach(cameraState);
		}
//		else if(cameraSelection.equals("Iso"))
//		{
//			IsoCameraState isoState = new IsoCameraState();
//			stateManager.attach(isoState);
//		}


	}


	private void initRenderer(boolean complexShadows)
	{


		viewPort.addProcessor(fpp);


		// SimpleWaterProcessor waterProcessor = new
		// SimpleWaterProcessor(assetManager);
		// waterProcessor.setReflectionScene(geometryNode);
		//
		// Vector3f waterLocation=new Vector3f(0,-4,0);
		// waterProcessor.setPlane(new Plane(Vector3f.UNIT_Y,
		// waterLocation.dot(Vector3f.UNIT_Y)));
		//
		// waterProcessor.setDebug(false);
		// waterProcessor.setWaveSpeed(0.02f);
		// // waterProcessor.setRefractionClippingOffset(10.1f);
		// waterProcessor.setWaterColor(ColorRGBA.Cyan.mult(10.5f));
		// // waterProcessor.setLightPosition(new Vector3f(-100,20,-100));
		// waterProcessor.setWaterDepth(40);
		// waterProcessor.setWaterTransparency(0.1f);
		// viewPort.addProcessor(waterProcessor);


		// BloomFilter bf=new BloomFilter(BloomFilter.GlowMode.Scene);
		// fpp.addFilter(bf);

		//


        
        //CARTOONFILTER
//        CartoonEdgeFilter cartoon = new CartoonEdgeFilter();
//        fpp.addFilter(cartoon);

		if(ambientOcclusion)
		{
//			SSAOFilter ssaoFilter = new SSAOFilter(20.00f, 20.92f, 0.3f, 0.61f);

			// ssaoFilter.setUseAo(false);
			// ssaoFilter.setUseOnlyAo(true);
//			fpp.addFilter(ssaoFilter);

		}
		// else
		// {
		// bsr = new BasicShadowRenderer(assetManager, 256);
		// bsr.setDirection(new Vector3f(-.5f,-.5f,-.5f).normalizeLocal());
		// //light direction
		// viewPort.addProcessor(bsr);
		// }

		// pssmRenderer = new PssmShadowRenderer(assetManager, 1024, 3);
		// pssmRenderer.setDirection(new Vector3f(-.5f, -.5f,
		// -.5f).normalizeLocal()); // light
		// // direction
		// pssmRenderer.setShadowIntensity(0.6f);
		// viewPort.addProcessor(pssmRenderer);
		//


		// BLOOOM FILTER
		// FilterPostProcessor fpp=new FilterPostProcessor(assetManager);
		// BloomFilter bf=new BloomFilter(BloomFilter.GlowMode.Objects);
		// fpp.addFilter(bf);
		// viewPort.addProcessor(fpp);

		rootNode.setShadowMode(ShadowMode.Off);

	}

	private void initCustom()
	{
		if(!guiCreatorPath.equals("None"))
		{

			ICustomStateCreator customCreator;
			try
			{
				
				Constructor con = Class.forName(this.guiCreatorPath, true, Thread.currentThread().getContextClassLoader()).getConstructor(
						new Class[]{SimpleApplication.class, ISpaceController.class});

				customCreator = (ICustomStateCreator)con.newInstance(new Object[]{this, spaceController});

				NiftyScreen startScreen = null;
				for(NiftyScreen nscreen : customCreator.getNiftyScreens())
				{
					System.out.println("nscreen " + nscreen.getName());
					nifty.fromXml(nscreen.getPath(), nscreen.getName(), customCreator.getScreenController());
					stateManager.attach((AppState)customCreator.getScreenController());
					if(nscreen.isStartScreen() || startScreen == null)
					{
						startScreen = nscreen;
					}
				}

				nifty.gotoScreen(startScreen.getName());

				// TODO: extra class
				if(!customCreator.getCustomAppStates().isEmpty())
				{
					for(AbstractAppState state : customCreator.getCustomAppStates())
					{
						stateManager.attach(state);
					}
					
				}

			}
			catch(ClassNotFoundException cnfe)
			{

				throw new RuntimeException("Cannot find this GUI-Creator", cnfe);

			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}


		}
	}

	private void initNifty()
	{

		/** Create a new NiftyGUI object */
		niftyDisplay = new NiftyJmeDisplay(assetManager, inputManager, audioRenderer, guiViewPort);
		nifty = niftyDisplay.getNifty();


		nifty.setIgnoreKeyboardEvents(true);


		/** Read your XML and initialize your custom ScreenController */
		nifty.fromXml("jadex3d/interface/BaseHud.xml", "hud", new DefaultGuiController(this));
		nifty.fromXml("jadex3d/interface/BaseHud.xml", "default", new DefaultGuiController(this));
		nifty.gotoScreen("hud");

		guiViewPort.addProcessor(niftyDisplay);
	}

	private void initAudio()
	{
		audioRenderer.setEnvironment(new Environment(Environment.Dungeon));
	}

	public void onAnimChange(AnimControl control, AnimChannel channel, String animName)
	{
	}

	public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName)
	{
		if(channel.getLoopMode() == LoopMode.DontLoop)
		{
			channel.reset(true);
		}
	}

	public void simpleUpdateAbstract(float tpf)
	{
		// Update the SoundListener to the CamLocation and Rotation
		listener.setLocation(cam.getLocation());
		listener.setRotation(cam.getRotation());

	}

	// Getter and Setter

	public void setChannels(HashMap<String, AnimChannel> animChannels)
	{
		this.animChannels = animChannels;
	}

	public HashMap<String, AnimChannel> getChannels()
	{
		return this.animChannels;
	}

	public Collection<com.jme3.renderer.Caps> getCaps()
	{
		return renderer.getCaps();
	}

	/**
	 * @return the animChannels
	 */
	public HashMap<String, AnimChannel> getAnimChannels()
	{
		return animChannels;
	}

	/**
	 * @param animChannels the animChannels to set
	 */
	public void setAnimChannels(HashMap<String, AnimChannel> animChannels)
	{
		this.animChannels = animChannels;
	}

	/**
	 * @return the terrain
	 */
	public TerrainQuad getTerrain()
	{
		return terrain;
	}

	/**
	 * @param terrain the terrain to set
	 */
	public void setTerrain(TerrainQuad terrain)
	{
		this.terrain = terrain;
	}

	/**
	 * @return the focusCam
	 */
	public FocusCamera getFocusCam()
	{
		return focusCam;
	}

	/**
	 * @param focusCam the focusCam to set
	 */
	public void setFocusCam(FocusCamera focusCam)
	{
		this.focusCam = focusCam;
	}

	/**
	 * @return the flyCamera
	 */
	public FlyCamera getFlyCamera()
	{
		return flyCamera;
	}

	/**
	 * @param flyCamera the flyCamera to set
	 */
	public void setFlyCamera(FlyCamera flyCamera)
	{
		this.flyCamera = flyCamera;
	}

	/**
	 * @return the niftyDisplay
	 */
	public NiftyJmeDisplay getNiftyDisplay()
	{
		return niftyDisplay;
	}

	/**
	 * @param niftyDisplay the niftyDisplay to set
	 */
	public void setNiftyDisplay(NiftyJmeDisplay niftyDisplay)
	{
		this.niftyDisplay = niftyDisplay;
	}

	/**
	 * @return the gridNode
	 */
	public Node getGridNode()
	{
		return gridNode;
	}

	/**
	 * @param gridNode the gridNode to set
	 */
	public void setGridNode(Node gridNode)
	{
		this.gridNode = gridNode;
	}


	/**
	 * @return the appDimension
	 */
	public float getAppDimension()
	{
		return appScaled;
	}

	/**
	 * @param appDimension the appDimension to set
	 */
	public void setAppDimension(float appDimension)
	{
		this.appScaled = appDimension;
	}

	/**
	 * @return the spaceSize
	 */
	public float getSpaceSize()
	{
		return spaceSize;
	}

	/**
	 * @param spaceSize the spaceSize to set
	 */
	public void setSpaceSize(float spaceSize)
	{
		this.spaceSize = spaceSize;
	}

	/**
	 * @return the selectedSpatial
	 */
	public Spatial getSelectedSpatial()
	{
		return selectedSpatial;
	}

	/**
	 * @param selectedSpatial the selectedSpatial to set
	 */
	public void setSelectedSpatial(Spatial selectedSpatial)
	{
		this.selectedSpatial = selectedSpatial;
	}

	/**
	 * @return the selectedTarget
	 */
	public int getSelectedTarget()
	{
		return selectedTarget;
	}

	/**
	 * @param selectedTarget the selectedTarget to set
	 */
	public void setSelectedTarget(int selectedTarget)
	{
		this.selectedTarget = selectedTarget;
	}

	/**
	 * @return the pssmRenderer
	 */
	public PssmShadowRenderer getPssmRenderer()
	{
		return pssmRenderer;
	}

	/**
	 * @param pssmRenderer the pssmRenderer to set
	 */
	public void setPssmRenderer(PssmShadowRenderer pssmRenderer)
	{
		this.pssmRenderer = pssmRenderer;
	}

	/**
	 * @return the bsr
	 */
	public BasicShadowRenderer getBsr()
	{
		return bsr;
	}

	/**
	 * @param bsr the bsr to set
	 */
	public void setBsr(BasicShadowRenderer bsr)
	{
		this.bsr = bsr;
	}

	/**
	 * @return the isGrid
	 */
	public boolean isGrid()
	{
		return isGrid;
	}

	/**
	 * @param isGrid the isGrid to set
	 */
	public void setGrid(boolean isGrid)
	{
		this.isGrid = isGrid;
	}

	/**
	 * @return the walkCam
	 */
	public boolean isWalkCam()
	{
		return walkCam;
	}

	/**
	 * @param walkCam the walkCam to set
	 */
	public void setWalkCam(boolean walkCam)
	{
		this.walkCam = walkCam;
	}

	/**
	 * @return the hudactive
	 */
	public boolean isHudactive()
	{
		return hudactive;
	}

	/**
	 * @param hudactive the hudactive to set
	 */
	public void setHudactive(boolean hudactive)
	{
		this.hudactive = hudactive;
	}

	/**
	 * @return the focusCamActive
	 */
	public boolean isFocusCamActive()
	{
		return focusCamActive;
	}

	/**
	 * @param focusCamActive the focusCamActive to set
	 */
	public void setFocusCamActive(boolean focusCamActive)
	{
		this.focusCamActive = focusCamActive;
	}

	/**
	 * @return the complexShadows
	 */
	public boolean isComplexShadows()
	{
		return complexShadows;
	}

	/**
	 * @param complexShadows the complexShadows to set
	 */
	public void setComplexShadows(boolean complexShadows)
	{
		this.complexShadows = complexShadows;
	}

	/**
	 * @return the assetManager
	 */
	public AssetManager getAssetManager()
	{
		return assetManager;
	}

	/**
	 * @return the lights
	 */
	public ArrayList<Light> getLights()
	{
		return lights;
	}

	/**
	 * @param lights the lights to set
	 */
	public void setLights(ArrayList<Light> lights)
	{
		this.lights = lights;
		for(Light l : this.lights)
		{
			rootNode.addLight(l);
		}
	}

	/**
	 * @return the cleanupPostFilter
	 */
	public boolean isCleanupPostFilter()
	{
		return cleanupPostFilter;
	}

	/**
	 * @param cleanupPostFilter the cleanupPostFilter to set
	 */
	public void setCleanupPostFilter(boolean cleanupPostFilter)
	{
		this.cleanupPostFilter = cleanupPostFilter;
	}


	/**
	 * @return the particleEmiters
	 */
	public HashMap<String, ParticleEmitter> getParticleEmiters()
	{
		return particleEmitters;
	}

	/**
	 * @param particleEmiters the particleEmiters to set
	 */
	public void setParticleEmiters(HashMap<String, ParticleEmitter> particleEmiters)
	{
		this.particleEmitters = particleEmiters;
	}

	/**
	 * @return the toDelete
	 */
	public ArrayList<String> getToDelete()
	{
		return toDelete;
	}

	/**
	 * @param toDelete the toDelete to set
	 */
	public void setToDelete(ArrayList<String> toDelete)
	{
		if(this.toDelete.isEmpty())
		{
			this.toDelete = toDelete;
		}
		else
		{
			for(String newSp : toDelete)
			{
				if(this.toDelete.contains(newSp))
				{

				}
				else
				{
					this.toDelete.add(newSp);
				}

			}

		}

	}

	/**
	 * @return the toAdd
	 */
	public ArrayList<Spatial> getToAdd()
	{
		return toAdd;
	}

	/**
	 * @param toAdd the toAdd to set
	 */
	public void setToAdd(ArrayList<Spatial> toAdd)
	{
		if(this.toAdd.isEmpty())
		{
			this.toAdd = toAdd;
		}
		else
		{
			for(Spatial newSp : toAdd)
			{
				if(this.toAdd.contains(newSp))
				{

				}
				else
				{
					this.toAdd.add(newSp);
				}

			}
		}
	}

//	public Vector3Int getSelectedworldcoord()
//	{
//		return selectedworldcoord;
//	}

//	public void setSelectedworldcoord(Vector3Int selectedworldcoord)
//	{
//		this.selectedworldcoord = selectedworldcoord;
//	}

	public void setCanvassize(Dimension canvasSize)
	{
		this.canvassize = canvasSize;


	}

	public Dimension getCanvassize()
	{
		return canvassize;
	}


	public float getAppSize()
	{
		return appSize;
	}

	public void setAppSize(float appSize)
	{
		this.appSize = appSize;
	}

	public String getCameraSelection()
	{
		return cameraSelection;
	}

	public void setCameraSelection(String cameraSelection)
	{
		this.cameraSelection = cameraSelection;
	}

	public float getAppScaled()
	{
		return appScaled;
	}

	public void setAppScaled(float appScaled)
	{
		this.appScaled = appScaled;
	}

	public ISpaceController getSpaceController()
	{
		return spaceController;
	}

	public void setSpaceController(ISpaceController spaceController)
	{
		this.spaceController = spaceController;
	}

	/**
	 * @return the fpp
	 */
	public FilterPostProcessor getFpp()
	{
		return fpp;
	}

	/**
	 * @param fpp the fpp to set
	 */
	public void setFpp(FilterPostProcessor fpp)
	{
		this.fpp = fpp;
	}


}
