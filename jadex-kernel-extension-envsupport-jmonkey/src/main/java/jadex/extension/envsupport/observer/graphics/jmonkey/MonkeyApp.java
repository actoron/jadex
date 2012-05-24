package jadex.extension.envsupport.observer.graphics.jmonkey;

import jadex.extension.envsupport.observer.graphics.drawable3d.Text3d;

import java.security.KeyRep;
import java.util.Collection;
import java.util.List;

import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.FlyByCamera;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.HillHeightMap;


/**
 * The Application that renders the 3d output for Jadex in the Jmonkey Engine
 * 
 * @author 7willuwe
 */
public class MonkeyApp extends SimpleApplication
{

	private boolean			_walkCam;

	private float			_areaSize;
	
	private float 			_spaceSize;

	private Node			_geometryNode;

	private Node			_gridNode;

	private Node			_staticNode;

	private TerrainQuad		_terrain;

	// Helper Classes
	private monkeyApp_Grid	_gridHandler;
	
	private int				 _selectedTarget;

	public MonkeyApp()
	{
		_areaSize = 100f;
		_spaceSize = 1f;
		_geometryNode = new Node("geometryNode");
		_staticNode = new Node("staticNode");
		_gridNode = new Node("gridNode");
		_walkCam = false;
		_selectedTarget = -1;
	}

	@Override
	public void simpleInitApp()
	{

		// Create the Cam
		setCam("Default");


		// this.rootNode.attachChild(_gridNode);
		this.rootNode.attachChild(_staticNode);


		DirectionalLight sun = new DirectionalLight();
		sun.setDirection(new Vector3f(1, 0, -2).normalizeLocal());
		sun.setColor(ColorRGBA.White);
		rootNode.addLight(sun);

		initKeys();


	}

	/** Custom Keybinding: Map named actions to inputs. */
	private void initKeys()
	{

		inputManager.addMapping("Select", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
		// You can map one or several inputs to one named action
		inputManager.addMapping("Random", new KeyTrigger(KeyInput.KEY_SPACE));

		inputManager.addMapping("ChangeCam", new KeyTrigger(KeyInput.KEY_F6));
		inputManager.addMapping("Grid", new KeyTrigger(KeyInput.KEY_F8));
		inputManager.addMapping("ZoomIn", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, false));
		inputManager.addMapping("ZoomOut", new MouseAxisTrigger(MouseInput.AXIS_WHEEL, true));
		// Add the names to the action listener.


		ActionListener actionListener = new ActionListener()
		{
			public void onAction(String name, boolean keyPressed, float tpf)
			{

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
					moveCamera(1, false);
				}
				else if(name.equals("ZoomOut"))
				{
					moveCamera(-1, false);
				}
				else if(keyPressed && name.equals("ChangeCam"))
				{
					_walkCam = !_walkCam;

				}
			}

		};

		inputManager.addListener(actionListener, new String[]{"Random"});
		inputManager.addListener(actionListener, new String[]{"Grid"});
		inputManager.addListener(actionListener, new String[]{"ChangeCam"});
		inputManager.addListener(actionListener, new String[]{"ZoomIn"});
		inputManager.addListener(actionListener, new String[]{"ZoomOut"});
		inputManager.addListener(actionListener, new String[]{"Select"});


	}

	public void setCam(String modus)
	{

		/** Configure cam to look at scene */
		cam.setLocation(new Vector3f(_areaSize * 1.1f, _areaSize*0.6f, _areaSize * 1.2f));
		cam.lookAt(new Vector3f(1, 2, 1), Vector3f.UNIT_Y);
		flyCam.setEnabled(true);
		flyCam.setMoveSpeed(20);


	}

	protected void moveCamera(float value, boolean sideways)
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
			loc.setY(getHeightAt(loc.x, loc.z));
			cam.setLocation(loc);
		}

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
			_terrain = (TerrainQuad)terra;
			_terrain.setLocalTranslation(_areaSize / 2, 0, _areaSize / 2);
			/** 5. The LOD (level of detail) depends on were the camera is: */
			TerrainLodControl control = new TerrainLodControl(_terrain, getCamera());
			_terrain.addControl(control);

			this.rootNode.attachChild(_terrain);
		}
		this.rootNode.attachChild(_staticNode);

	}

	/*
	 * Use only for Camera
	 */
	public float getHeightAt(float x, float z)
	{
		if(_terrain != null)
		{
			Vector2f vec = new Vector2f(x, z);
			float height = _terrain.getHeight(vec);
			return height + 3;
		}

		return 0;
	}

	public float getHeightAt(Vector2f vec)
	{
		if(_terrain != null)
		{
			vec = vec.mult(_areaSize);
			return _terrain.getHeight(vec) / _areaSize;
		}

		return 0;

	}

	public void setHeight()
	{
		if(_terrain != null)
		{
			HillHeightMap heightmap = null;
			try
			{
				heightmap = new HillHeightMap(257, 2000, 25, 100, (long)((byte)100 * Math.random()));
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
			Material mat = _terrain.getMaterial();
			Vector3f scale = _terrain.getLocalScale();
			Vector3f trans = _terrain.getLocalTranslation();
			rootNode.detachChildNamed("Terrain");
			_terrain = new TerrainQuad("Terrain", 65, 257, heightmap.getHeightMap());

			_terrain.setLocalTranslation(trans);
			_terrain.setLocalScale(scale);
			_terrain.setMaterial(mat);
			rootNode.attachChild(_terrain);
		}
	}
	
	private boolean gridCreated =  false;

	public void setSpaceSize(double scale, boolean isGrid)
	{
		if(!gridCreated)
		{
			_spaceSize = (float)scale;
			// Create the Grid
			_gridHandler = new monkeyApp_Grid(_areaSize, _spaceSize, assetManager, isGrid);
			_gridNode = _gridHandler.getGrid();
			this.rootNode.attachChild(_geometryNode);
			gridCreated = true;
		}


	}

	private void fireSelection()
	{
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
//		rootNode.collideWith(ray, results);
		_geometryNode.collideWith(ray, results);

		int selection = -1;
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
			}
		}
		setSelectedTarget(selection);
		
	} // else if ...

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

}