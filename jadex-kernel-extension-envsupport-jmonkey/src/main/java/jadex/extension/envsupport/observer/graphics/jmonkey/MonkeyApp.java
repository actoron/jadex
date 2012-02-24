package jadex.extension.envsupport.observer.graphics.jmonkey;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.AssetManager;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.HillHeightMap;
 
/**
 * Example 12 - how to give objects physical properties so they bounce and fall.
 * @author base code by double1984, updated by zathras
 */
public class MonkeyApp extends SimpleApplication {
	
 private int _areaSize;
 private Node	_geometryNode;
 private Node _gridNode;
 private Node _staticNode;
 
 private TerrainQuad terrain;
 
 // Helper Classes
 private monkeyApp_Grid _gridHandler;
  public MonkeyApp(int areaSize)
{
	  _areaSize = areaSize;
	  _geometryNode = new Node("geometryNode");
	  _staticNode = new Node("staticNode");
	  _gridNode = new Node("gridNode");
}

@Override
  public void simpleInitApp() {
	
    /** Configure cam to look at scene */
    cam.setLocation(new Vector3f(_areaSize*1.1f, _areaSize*0.7f, _areaSize*1.2f));
    cam.lookAt(new Vector3f(1, 2, 1), Vector3f.UNIT_Y);
    flyCam.setEnabled(true);
    flyCam.setMoveSpeed(100);
    
    
    //Create the Grid
	 _gridHandler = new monkeyApp_Grid(_areaSize, assetManager);
	 _gridNode = _gridHandler.getGrid();

    
    this.rootNode.attachChild(_geometryNode);
    this.rootNode.attachChild(_gridNode);
    this.rootNode.attachChild(_staticNode);
	
    
    DirectionalLight sun = new DirectionalLight();
    sun.setDirection(new Vector3f(1,0,-2).normalizeLocal());
    sun.setColor(ColorRGBA.White);
    rootNode.addLight(sun);
    
    initKeys();
    
    
  }
 


	public AssetManager getAssetManager()
	{
		return assetManager;
	}
	
    public void simpleUpdate(float tpf) {
    	
    	
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
		//Add SKY direct to Root
		Spatial sky = staticNode.getChild("Skymap");
		if(sky!=null)
		{
			sky.removeFromParent();
			this.rootNode.attachChild(sky);
		}
		//Add TERRAIN direct to Root
		Spatial terra = staticNode.getChild("Terrain");
		if(terra!=null)
		{
			terra.removeFromParent();
			terrain = (TerrainQuad)terra;
		    terrain.setLocalTranslation(_areaSize/2, 0, _areaSize/2);
		    /** 5. The LOD (level of detail) depends on were the camera is: */
		    TerrainLodControl control = new TerrainLodControl(terrain, getCamera());
		    terrain.addControl(control);
		    
		    this.rootNode.attachChild(terrain);
		}
		this.rootNode.attachChild(_staticNode);
		
	}
	
	
	public float getHeightAt(Vector2f vec)
	{
		if(terrain!=null)
		{
			vec = vec.mult(_areaSize);
			return terrain.getHeight(vec)/_areaSize;	
		}
		
		return 0;
		
	}
	
	public void setHeight()
	{
		HillHeightMap heightmap = null;
		try {
		    heightmap = new HillHeightMap(257, 2000, 25, 100, (long)((byte) 100*Math.random()));
		} catch (Exception ex) {
		    ex.printStackTrace();
		}
		Material mat = terrain.getMaterial();
		Vector3f scale = terrain.getLocalScale();
		Vector3f trans = terrain.getLocalTranslation();
		rootNode.detachChildNamed("Terrain");
		terrain = new TerrainQuad("Terrain", 65, 257, heightmap.getHeightMap());
		
		terrain.setLocalTranslation(trans);
		terrain.setLocalScale(scale);
		terrain.setMaterial(mat);
		rootNode.attachChild(terrain);
	}
	
	  /** Custom Keybinding: Map named actions to inputs. */
	  private void initKeys() {
	    // You can map one or several inputs to one named action
	    inputManager.addMapping("Random",  new KeyTrigger(KeyInput.KEY_SPACE));

	    inputManager.addMapping("Rotate", new MouseButtonTrigger(MouseInput.BUTTON_LEFT));
	    // Add the names to the action listener.
	    ActionListener actionListener = new ActionListener() {
		    public void onAction(String name, boolean keyPressed, float tpf) {
		      if (name.equals("Random")) {
		        setHeight();
		      }
		    }
		  };
	    inputManager.addListener(actionListener, new String[]{"Random"});

	 
	  }

       
}