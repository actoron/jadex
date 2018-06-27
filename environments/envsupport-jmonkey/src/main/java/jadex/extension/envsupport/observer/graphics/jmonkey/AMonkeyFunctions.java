package jadex.extension.envsupport.observer.graphics.jmonkey;

import java.awt.EventQueue;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;

import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.system.JmeCanvasContext;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.HillHeightMap;

import jadex.extension.envsupport.environment.ISpaceController;
import jadex.extension.envsupport.math.IVector3;
import jadex.extension.envsupport.math.Vector3Int;


/**
 * The Abstract Application for the renders the 3d output for Jadex in the
 * Jmonkey Engine This Class holds most of the Functions  for
 * better structure
 * 
 * @author 7willuwe
 */
public abstract class AMonkeyFunctions extends AMonkeyInit
{

	public AMonkeyFunctions(float dim, float appScaled, float spaceSize, boolean isGrid, boolean shader, String camera, String guiCreatorPath,
			ISpaceController spaceController)
	{
		super(dim, appScaled, spaceSize, isGrid, shader, camera, guiCreatorPath, spaceController);

	}


	protected void simpleInit()
	{
		super.simpleInit();

	}

	public void simpleUpdateAbstract(float tpf)
	{
		super.simpleUpdateAbstract(tpf);

	}


	/**
	 * This Functions fires a F11 Command to the Canvas.
	 */
	public void fireFullscreen()
	{
	
		JmeCanvasContext context = (JmeCanvasContext)getContext();

		KeyEvent event = new KeyEvent(context.getCanvas(), KeyEvent.KEY_PRESSED, EventQueue.getMostRecentEventTime(), 0, KeyEvent.VK_F11,
				KeyEvent.CHAR_UNDEFINED);

		Toolkit.getDefaultToolkit().getSystemEventQueue().postEvent(event);

		cleanupPostFilter = true;

	}


	/**
	 * This Functions moves the Camera for the WalkCamera
	 */
	public void moveCamera(float value, boolean sideways)
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

		if(walkCam)
			pos.setY(getHeightForCam(pos.x, pos.z));

		cam.setLocation(pos);
	}

	public Vector3Int getSelectedWorldCoord()
	{
		return selectionControl.getSelectedWorldCoord();
	}
	
	public Object getSelectedSpaceObjectId()
	{
		return selectionControl.computeSelectedId();
	}
	
	public IVector3 getWorldContactPoint()
	{
		return selectionControl.getMouseContactPoint();
	}
	

	public void randomizeHeightMap()
	{
		if(terrain != null)
		{
			try
			{
				HillHeightMap heightmap	= new HillHeightMap(513, 2000, 25, 100, (long)((byte)100 * Math.random()));
				Material mat = terrain.getMaterial();
				Vector3f scale = terrain.getLocalScale();
				Vector3f trans = terrain.getLocalTranslation();
				rootNode.detachChildNamed("Terrain");
				terrain = new TerrainQuad("Terrain", 65, 513, heightmap.getHeightMap());

				terrain.setLocalTranslation(trans);
				terrain.setLocalScale(scale);
				terrain.setMaterial(mat);
				terrain.setShadowMode(ShadowMode.Receive);
				rootNode.attachChild(terrain);
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}

	

	/*
	 * Use only for Camera
	 */
	private float getHeightForCam(float x, float z)
	{
		Float height = 0f;
		if(terrain != null)
		{
			Vector2f vec = new Vector2f(x, z);
			height = terrain.getHeight(vec) + 3;

		}

		return (height.isNaN() ? 3 : height);
	}

	
}
