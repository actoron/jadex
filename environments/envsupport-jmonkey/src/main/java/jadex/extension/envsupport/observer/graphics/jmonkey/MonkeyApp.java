package jadex.extension.envsupport.observer.graphics.jmonkey;

import java.util.ArrayList;
import java.util.HashMap;

import com.jme3.effect.ParticleEmitter;
import com.jme3.math.Vector2f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;

import jadex.extension.envsupport.environment.ISpaceController;
import jadex.extension.envsupport.observer.graphics.jmonkey.util.NodeQueue;
import jme3tools.optimize.GeometryBatchFactory;


/**
 * The Application that renders the 3d output for Jadex in the Jmonkey Engine it
 * get the refreshed Geometry and Static Objects from the Viewport
 * 
 * @author 7willuwe
 */
public class MonkeyApp extends AMonkeyFunctions
{

	private boolean		guiActive	= false;

	private NodeQueue	effectStack;


	public MonkeyApp(float dim, float appScaled, float spaceSize, boolean isGrid, boolean shader, String camera, String guiCreatorPath,
			ISpaceController spaceController)
	{
		super(dim, appScaled, spaceSize, isGrid, shader, camera, guiCreatorPath, spaceController);
		this.effectStack = new NodeQueue(10);
	}

	public void simpleInitApp()
	{

		super.simpleInit();

	}


	public void simpleUpdate(float tpf)
	{
		super.simpleUpdateAbstract(tpf);

		/**
		 * Update the Filter if necessary
		 */
		if(cleanupPostFilter)
		{
			System.out.println("cleanup!");
			fpp.cleanup();
			cleanupPostFilter = false;
		}


		/**
		 * Update the Batchnode
		 */
		if(!toDelete.isEmpty() || !toAdd.isEmpty())
		{

			handleBatchNode();

		}

	}


	private HashMap<String, ArrayList<String>>	nameMemory		= new HashMap<String, ArrayList<String>>();

	private ArrayList<String>					tmpList			= new ArrayList<String>();

	private ArrayList<String>					tmpListDelete	= new ArrayList<String>();

	private void handleBatchNode()
	{
		if(!toAdd.isEmpty())
		{

			for(Spatial add : toAdd)
			{
				if(add instanceof Node)
				{
					Node addnode = (Node)add;

					tmpList.clear();

					Node tmpNode = new Node(add.getName());
					for(Spatial addchild : addnode.getChildren())
					{
						if(addchild instanceof Node)
						{

							if(addchild.getName().startsWith("Type: 6"))
							{
								addchild.removeFromParent();
								addchild.setLocalTranslation(addnode.getLocalTranslation());
								addchild.setLocalScale(addchild.getLocalScale().multLocal(add.getLocalScale()));
								tmpNode.attachChild(addchild);
							}


						}
					}

					staticgeo.attachChild(add);


					staticbatchgeo.attachChild(tmpNode);


					if(!tmpList.isEmpty())
					{
						nameMemory.put(add.getName(), tmpList);
					}
					// staticgeo.attachChild(addnode);


					if((Boolean)addnode.getUserData("hasEffect") == true)
					{
						startEffect(addnode);

					}


				}


			}

		}


		if(!toDelete.isEmpty())
		{
			for(String id : toDelete)
			{

				staticgeo.detachChildNamed(id);
				Spatial delete = staticbatchgeo.getChild(id);


				if(delete != null)
				{
					if(delete instanceof Node)
					{
						
						Node delnode = (Node)delete;
						
						for(Spatial delspatial : delnode.getChildren())
						{
							if(delspatial instanceof Node)
							{
								((Node)delspatial).detachAllChildren();
							}
							
							delspatial.removeFromParent();
							
						}
						
						
						

					}


					staticbatchgeo.detachChild(delete);
					delete.removeFromParent();

				}


			}
		}


		staticbatchgeo.batch();

		toDelete.clear();
		toAdd.clear();

	}

	private void startEffect(Node addnode)
	{

		for(Spatial effectspatial : addnode.getChildren())
		{
			if(effectspatial instanceof Node)
			{
				Node effectnode = (Node)effectspatial;
				if(effectnode.getName().startsWith("effectNode"))
				{


					for(Spatial effect : effectnode.getChildren())

						if(effect != null && effect instanceof ParticleEmitter)
						{

							ParticleEmitter tmpeffect = ((ParticleEmitter)effect);
							tmpeffect.emitAllParticles();
						}

					Node oldnode = effectStack.push(effectnode);

					if(oldnode != null)
					{
						oldnode.removeFromParent();
					}

				}


			}
		}

	}

	public void setStaticGeometry(Node staticNode)
	{
		this.staticNode = staticNode;
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
			// ShadowMode mode = terra.getShadowMode();
			terrain = (TerrainQuad)terra;
			terrain.setLocalTranslation(appScaled / 2, 0, appScaled / 2);

			this.rootNode.attachChild(terrain);

			/** 5. The LOD (level of detail) depends on were the camera is: */
			TerrainLodControl control = new TerrainLodControl(terrain, cam);
			terrain.addControl(control);
			terrain.setShadowMode(ShadowMode.Receive);


		}
		GeometryBatchFactory.optimize(this.staticNode, true);
		this.rootNode.attachChild(this.staticNode);

	}


	public float getHeightAt(Vector2f vec)
	{
		float height = 0;
		if(terrain != null)
		{
			vec = vec.mult(appScaled / spaceSize);
			height = terrain.getHeight(vec) / appScaled * spaceSize;
		}

		return height;
	}

	/**
	 * @return the guiActive
	 */
	public boolean isGuiActive()
	{
		return guiActive;
	}

	/**
	 * @param guiActive the guiActive to set
	 */
	public void setGuiActive(boolean guiActive)
	{
		this.guiActive = guiActive;
	}


}