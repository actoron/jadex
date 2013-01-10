package jadex.extension.envsupport.observer.graphics.jmonkey;

import jadex.extension.envsupport.observer.graphics.drawable3d.special.NiftyScreen;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.util.List;

import com.jme3.collision.CollisionResults;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.material.Material;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.system.JmeCanvasContext;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.HillHeightMap;

/**
 * The Abstract Application for the renders the 3d output for Jadex in the Jmonkey Engine
 * 
 * This Class holds most of the Functions and KeyCommands for better structure
 * 
 * @author 7willuwe
 */
public abstract class AMonkeyFunctions extends AMonkeyInit{

	public AMonkeyFunctions(float dim, float appScaled, float spaceSize, boolean isGrid, boolean shader, String camera, String guiCreatorPath, List<NiftyScreen> niftyScreens) 
	{
		super(dim, appScaled, spaceSize, isGrid, shader, camera, guiCreatorPath, niftyScreens);
		
	}



	protected void simpleInit() {
		super.simpleInit();
		
		
		// Init Methods
		initKeys();

	}
	
	public void simpleUpdateAbstract(float tpf) {
		super.simpleUpdateAbstract(tpf);
		if (walkCam) {

		}
		
	}

	
	/**
	 * This Functions fires a F11 Command to the Canvas.
	 */
	public void fireFullscreen() {
		System.out.println("fullscreen command aus JMonkey");
		
		

		JmeCanvasContext context = (JmeCanvasContext) getContext();

		KeyEvent event = new KeyEvent(context.getCanvas(),
				KeyEvent.KEY_PRESSED, EventQueue.getMostRecentEventTime(), 0,
				KeyEvent.VK_F11, KeyEvent.CHAR_UNDEFINED);

		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(event);
		
		cleanupPostFilter = true;

	}
	
	
	/**
	 * This Functions moves the Camera for the WalkCamera
	 */
	private void moveCamera(float value, boolean sideways) {
		Vector3f vel = new Vector3f();
		Vector3f pos = cam.getLocation().clone();

		if (sideways) {
			cam.getLeft(vel);
		} else {
			cam.getDirection(vel);
		}
		vel.multLocal(value * 10);

		pos.addLocal(vel);
		
		if(walkCam)
		pos.setY(getHeightForCam(pos.x, pos.z));

		cam.setLocation(pos);
	}
	
	public void getSpacePosition()
	{
		CollisionResults results = fireRaytrace();
		if (results.size() > 0) {
			Geometry target = results.getClosestCollision().getGeometry();
			// Here comes the
			// action:
			Spatial selectedsp = target;

			try {
				
			
			// we look for the SpaceObject-Parent
			if (selectedsp != null) {
				while (!Character.isDigit(selectedsp.getName().charAt(0))) {
					selectedsp = selectedsp.getParent();
				}

				System.out.println("name: " + selectedsp.getName());
			}
			}
			catch (NullPointerException e) {
				System.out.println("AMonkeyFunctions: Selection NULL");
			}
		}
		
	}
	public void fireSelection() {
		CollisionResults results = fireRaytrace();

		int selection = -1;
		Spatial selectedspatial = null;
		if (results.size() > 0) {
			Geometry target = results.getClosestCollision().getGeometry();
			// Here comes the
			// action:
			Spatial selectedsp = target;

			try {
				
			
			// we look for the SpaceObject-Parent
			if (selectedsp != null) {
				while (!Character.isDigit(selectedsp.getName().charAt(0))) {
					selectedsp = selectedsp.getParent();
				}

				selection = Integer.parseInt(selectedsp.getName());
				selectedspatial = selectedsp;
				System.out.println("selected!");
			}
			}
			catch (NullPointerException e) {
				System.out.println("AMonkeyFunctions: Selection NULL");
			}
		}
		setSelectedTarget(selection);
		setSelectedSpatial(selectedspatial);
		if(focusCamActive)
		{
			if(selectedspatial!=null)
			focusCam.setSpatial(selectedspatial);
			else
			focusCam.setSpatial(staticNode);
		}

	}
	
	public void setHeight() {
		if (terrain != null) {
			HillHeightMap heightmap = null;
			try {
				heightmap = new HillHeightMap(513, 2000, 25, 100,
						(long) ((byte) 100 * Math.random()));
			} catch (Exception ex) {
				ex.printStackTrace();
			}
			Material mat = terrain.getMaterial();
			Vector3f scale = terrain.getLocalScale();
			Vector3f trans = terrain.getLocalTranslation();
			rootNode.detachChildNamed("Terrain");
			terrain = new TerrainQuad("Terrain", 65, 513,
					heightmap.getHeightMap());

			terrain.setLocalTranslation(trans);
			terrain.setLocalScale(scale);
			terrain.setMaterial(mat);
			terrain.setShadowMode(ShadowMode.Receive);
			rootNode.attachChild(terrain);
		}
	}

	private CollisionResults fireRaytrace() {
		this.selectedSpatial = null;
		// Reset results list.
		CollisionResults results = new CollisionResults();
		// Convert screen click
		// to 3d position
		Vector2f click2d = inputManager.getCursorPosition();
		Vector3f click3d = cam.getWorldCoordinates(
				new Vector2f(click2d.x, click2d.y), 0f).clone();
		Vector3f dir = cam
				.getWorldCoordinates(new Vector2f(click2d.x, click2d.y), 1f)
				.subtractLocal(click3d).normalize();
		// Aim the ray from the
		// clicked spot
		// forwards.
		Ray ray = new Ray(click3d, dir);
		// Collect intersections
		// between ray and all
		// nodes in results
		// list.
		// rootNode.collideWith(ray, results);
		this.getRootNode().getChild("Geometry").collideWith(ray, results);

		return results;
	}
	
	/*
	 * Use only for Camera
	 */
	private float getHeightForCam(float x, float z) {
		Float height = 0f;
		if (terrain != null) {
			Vector2f vec = new Vector2f(x, z);
			height = terrain.getHeight(vec) + 3;
			
		}

		return (height.isNaN()?3:height);
	}
	
	/** Custom Keybinding: Map named actions to inputs. */
	private void initKeys() {
		

		inputManager.addMapping("Select", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
		inputManager.addMapping("Leftclick", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
		inputManager.addMapping("Random", new KeyTrigger(KeyInput.KEY_SPACE));
		inputManager.addMapping("Hud", new KeyTrigger(KeyInput.KEY_F1));
//		inputManager.addMapping("Wireframe", new KeyTrigger(KeyInput.KEY_F2));
//		inputManager.addMapping("ChaseCam", new KeyTrigger(KeyInput.KEY_F3));
//		inputManager.addMapping("FollowCam", new KeyTrigger(KeyInput.KEY_F4));
//		inputManager.addMapping("WalkCam", new KeyTrigger(KeyInput.KEY_F6));
		inputManager.addMapping("Grid", new KeyTrigger(KeyInput.KEY_F8));
		inputManager.addMapping("Fullscreen", new KeyTrigger(KeyInput.KEY_F11),new KeyTrigger(KeyInput.KEY_F));
		inputManager.addMapping("ZoomIn", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
		inputManager.addMapping("ZoomOut", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));

		ActionListener actionListener = new ActionListener() {
			public void onAction(String name, boolean keyPressed, float tpf) {

				if (keyPressed && name.equals("Hud")) {
					if(defaultGui)
					{
						if (hudactive) {
							niftyDisplay.getNifty().gotoScreen("hud");
						} else {
							niftyDisplay.getNifty().gotoScreen("default");
						}

						hudactive = !hudactive;
					}
					
				}

				else if (keyPressed && name.equals("Fullscreen")) {
					fireFullscreen();

				}
				else if (keyPressed && name.equals("Wireframe")) {
					makeWireframe();

				}

				else if (keyPressed && name.equals("Random")) {
					setHeight();

				}
				else if (keyPressed && name.equals("Grid")) {

					if (rootNode.getChild("gridNode") != null) {
						rootNode.detachChild(gridNode);

					} else {
						rootNode.attachChild(gridNode);
					}

				}

				if (name.equals("Select") && keyPressed) {
					fireSelection();
				}
				


				else if (!focusCamActive&&name.equals("ZoomIn")) {
					moveCamera(6, false);
				} 
				
				else if (!focusCamActive &&name.equals("ZoomOut")) {
					moveCamera(-6, false);
				}
				
				else if (!focusCamActive && keyPressed && name.equals("WalkCam")) {
					walkCam = !walkCam;
					System.out.println("walkcam!");
				}

				else if (keyPressed && name.equals("ChaseCam")) {

					focusCamActive = !focusCamActive;
					
					if (focusCamActive) {
						if (selectedSpatial != null)
						{
							focusCam.setSpatial(selectedSpatial);
						}
						else
						{
							focusCam.setSpatial(staticNode);
						}
						focusCam.setEnabled(true);
						flyCamera.setEnabled(false);
						walkCam = false;


					} else 
					{
						focusCam.setEnabled(false);
						flyCamera.setEnabled(true);
					}



				}
			}

			private void makeWireframe()
			{
				

				
			}

		};
		
		ActionListener leftClickListener = new ActionListener() {
			
			public void onAction(String name, boolean keyPressed, float tpf) {
				
				if (name.equals("Leftclick") && keyPressed) {
					System.out.println("leftclick jmonkey!");
					getSpacePosition();
				}
				
			}
				
		};

		inputManager.addListener(actionListener, new String[] { "Hud" });
		inputManager.addListener(actionListener, new String[] { "Random" });
		inputManager.addListener(actionListener, new String[] { "Grid" });
		inputManager.addListener(actionListener, new String[] { "ChaseCam" });
		inputManager.addListener(actionListener, new String[] { "WalkCam" });
		inputManager.addListener(actionListener, new String[] { "ZoomIn" });
		inputManager.addListener(actionListener, new String[] { "ZoomOut" });
		inputManager.addListener(leftClickListener, new String[] { "Leftclick" });
		inputManager.addListener(actionListener, new String[] { "Select" });
		inputManager.addListener(actionListener, new String[] { "Fullscreen" });
		inputManager.addListener(actionListener, new String[] { "FollowCam" });
		inputManager.addListener(actionListener, new String[] { "Fullscreen" });

	}
}
