package jadex.extension.envsupport.observer.graphics.jmonkey.renderer.special;

import java.util.ArrayList;

import com.jme3.material.Material;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Spatial;
import com.jme3.terrain.geomipmap.TerrainQuad;
import com.jme3.terrain.heightmap.AbstractHeightMap;
import com.jme3.terrain.heightmap.HillHeightMap;
import com.jme3.terrain.heightmap.ImageBasedHeightMap;
import com.jme3.texture.Texture;
import com.jme3.texture.Texture.WrapMode;

import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Terrain3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.TerrainTexture;
import jadex.extension.envsupport.observer.graphics.jmonkey.ViewportJMonkey;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.AbstractJMonkeyRenderer;


public class TerrainJMonkeyRenderer extends AbstractJMonkeyRenderer
{
	/** heightmap for jMonkey. */
	Material mat_terrain;
	TerrainQuad terrain;
	public Spatial draw(DrawableCombiner3d dc, Primitive3d primitive,
			SpaceObject sobj, ViewportJMonkey vp)
	{
		
		boolean isRnd = (Boolean)((Terrain3d)primitive).isRnd();
		String terrapath = (String)((Terrain3d)primitive).getTerrapath();
		String alphamapfile = terrapath.concat((String)((Terrain3d)primitive).getAlphamap());
		

		
		ArrayList<TerrainTexture> textures = (ArrayList<TerrainTexture>)((Terrain3d)primitive).getTextures();

	    mat_terrain = new Material(assetManager, 
	            "Common/MatDefs/Terrain/Terrain.j3md");
	 
	    /** Alpha Map for Textures until 5 Layers */
	    mat_terrain.setTexture("Alpha", assetManager.loadTexture(alphamapfile));
	    
	    
	    
	    for(TerrainTexture tex : textures)
	    {
	    	if(!tex.getTexture().equals(""))
	    	{
	    		Texture text = assetManager.loadTexture(terrapath.concat(tex.getTexture()));
	    		text.setWrap(WrapMode.Repeat);
	    	    mat_terrain.setTexture(tex.getName(), text);
	    	    mat_terrain.setFloat(tex.getName().concat("Scale"), tex.getScale());
	    	}
	    }
	    
	    AbstractHeightMap heightmap = null;
		
	    if(isRnd)
	    {
			int tiles = (Integer)((Terrain3d)primitive).getTiles();
			int iterations = (Integer)((Terrain3d)primitive).getIterations();
			int minradius = (Integer)((Terrain3d)primitive).getMinradius();
			int maxradius = (Integer)((Terrain3d)primitive).getMaxradius();
			int seed = (Integer)((Terrain3d)primitive).getSeed();
	    	
		    try
			{
				heightmap = new HillHeightMap(tiles+1, iterations, minradius, maxradius, (byte) seed);
				terrain = new TerrainQuad("Terrain", 65, tiles+1, heightmap.getHeightMap());
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		    
	    }
	    else
	    {
			String heightmapfile = terrapath.concat((String)((Terrain3d)primitive).getHeightmap());
			
			int patchsize = (Integer)((Terrain3d)primitive).getPatchsize();
			int picsize = (Integer)((Terrain3d)primitive).getPicsize();
		    
		    /** Create the height map */
		    
		    Texture heightMapImage = assetManager.loadTexture(
		    		heightmapfile);
		    heightmap = new ImageBasedHeightMap(heightMapImage.getImage());
		    heightmap.load();
		    
		    terrain = new TerrainQuad("Terrain", patchsize+1, picsize+1, heightmap.getHeightMap());
	    }

	    

	 
	    /** 3. We have prepared material and heightmap. 
	     * Now we create the actual terrain:
	     * 3.1) Create a TerrainQuad and name it "my terrain".
	     * 3.2) A good value for terrain tiles is 64x64 -- so we supply 64+1=65.
	     * 3.3) We prepared a heightmap of size 512x512 -- so we supply 512+1=513.
	     * 3.4) As LOD step scale we supply Vector3f(1,1,1).
	     * 3.5) We supply the prepared heightmap itself.
	     */
	    
	    
	 
	    /** 4. We give the terrain its material */
//	    mat_terrain.getAdditionalRenderState().setWireframe(true);
	    
	    
		String shadow = primitive.getShadowtype();
		
		if(shadow.equals(Primitive3d.SHADOW_CAST))
		{
			terrain.setShadowMode(ShadowMode.Cast);
		}
		else if(shadow.equals(Primitive3d.SHADOW_RECEIVE))
		{
			terrain.setShadowMode(ShadowMode.Receive);
		}
		


	    terrain.setMaterial(mat_terrain);
	    
		return terrain;


	}

}
