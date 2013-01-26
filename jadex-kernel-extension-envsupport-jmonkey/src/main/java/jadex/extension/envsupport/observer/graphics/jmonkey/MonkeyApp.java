package jadex.extension.envsupport.observer.graphics.jmonkey;

import java.awt.Dimension;
import java.util.List;

import jadex.extension.envsupport.environment.ISpaceController;
import jadex.extension.envsupport.observer.graphics.drawable3d.special.NiftyScreen;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.special.EffectSaver;
import jme3tools.optimize.GeometryBatchFactory;

import com.jme3.effect.ParticleEmitter;
import com.jme3.material.Material;
import com.jme3.math.Vector2f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.BatchNode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;



/**
 * The Application that renders the 3d output for Jadex in the Jmonkey Engine it
 * get the refreshed Geometry and Static Objects from the Viewport
 * 
 * @author 7willuwe
 */
public class MonkeyApp extends AMonkeyFunctions
{

	private boolean guiActive = false;
	
	private NodeQueue effectStack;


	public MonkeyApp(float dim, float appScaled, float spaceSize, boolean isGrid, boolean shader, String camera, String guiCreatorPath, ISpaceController spaceController)
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


	private void handleBatchNode()
	{
		if(!toAdd.isEmpty())
		{
			for(Spatial add : toAdd)
			{
				if(add instanceof Node)
				{
					Node addnode = (Node)add;
					for(Spatial addchild : addnode.getChildren())
					{
						if(addchild instanceof Node)
						{
							if(addchild.getName().startsWith("Type: 6"))
							{
								addchild.removeFromParent();
								addchild.setLocalTranslation(addnode.getLocalTranslation());
								addchild.setLocalScale(addchild.getLocalScale().multLocal(add.getLocalScale()));
								addchild.setName(add.getName());
								staticbatchgeo.attachChild(addchild);


							}

						}
					}
					staticgeo.attachChild(add);
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
				Spatial delete = staticbatchgeo.getChild(id);
				staticgeo.detachChildNamed(id);

				if(delete != null)
				{
					if(delete instanceof Node)
					{
						Node delnode = (Node)delete;
						delnode.detachAllChildren();
					}
					staticbatchgeo.detachChild(delete);
					delete.removeFromParent();

				}
				else
				{
					
				}

			}
		}


		staticbatchgeo.batch();

		toDelete.clear();
		toAdd.clear();
		
	}

	private void startEffect(Node addnode) {
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
					
					if(oldnode!=null)
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
		GeometryBatchFactory.optimize(staticNode, true);
		this.rootNode.attachChild(staticNode);

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