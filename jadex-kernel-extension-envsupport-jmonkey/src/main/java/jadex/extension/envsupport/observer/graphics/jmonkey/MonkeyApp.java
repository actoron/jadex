package jadex.extension.envsupport.observer.graphics.jmonkey;

import com.jme3.math.Vector2f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainLodControl;
import com.jme3.terrain.geomipmap.TerrainQuad;

/**
 * The Application that renders the 3d output for Jadex in the Jmonkey Engine
 * 
 * it get the refreshed Geometry and Static Objects from the Viewport
 * 
 * @author 7willuwe
 */
public class MonkeyApp extends AMonkeyFunctions {

	public MonkeyApp(float dim, float spaceSize, boolean isGrid) {
		super(dim, spaceSize, isGrid);
	}

	public void simpleInitApp() {
		super.simpleInit();
	}



	public void simpleUpdate(float tpf) {
		super.simpleUpdateAbstract(tpf);
//		System.out.println("dim");
		if(cleanupPostFilter)
		{
			System.out.println("cleanup!");
			fpp.cleanup();
			cleanupPostFilter = false;
		}


	}

	public Node getGeometry() {
		return this.geometryNode;
	}

	public void setGeometry(Node geometry) {
		this.geometryNode = geometry;
		this.rootNode.attachChild(geometryNode);
	}

	public void setStaticGeometry(Node staticNode) {
		this.staticNode = staticNode;
		// Add SKY direct to Root
		Spatial sky = staticNode.getChild("Skymap");
		if (sky != null) {
			sky.removeFromParent();
			this.rootNode.attachChild(sky);
		}
		// Add TERRAIN direct to Root
		Spatial terra = staticNode.getChild("Terrain");
		if (terra != null) {
			terra.removeFromParent();
//			ShadowMode mode = terra.getShadowMode();
			terrain = (TerrainQuad) terra;
			terrain.setLocalTranslation(appDimension / 2, 0,
					appDimension / 2);
			/** 5. The LOD (level of detail) depends on were the camera is: */
			TerrainLodControl control = new TerrainLodControl(terrain, cam);
			terrain.addControl(control);
			terrain.setShadowMode(ShadowMode.Receive);

			this.rootNode.attachChild(terrain);
		}
		this.rootNode.attachChild(staticNode);

	}



	public float getHeightAt(Vector2f vec) {
		float height = 0;
		if (terrain != null) {
			vec = vec.mult(appDimension / spaceSize);
			height = terrain.getHeight(vec) / appDimension * spaceSize;
		}

		return height;
	}

}