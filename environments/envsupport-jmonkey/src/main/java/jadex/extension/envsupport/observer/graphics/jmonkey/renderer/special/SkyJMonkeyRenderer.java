package jadex.extension.envsupport.observer.graphics.jmonkey.renderer.special;

import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.texture.Texture;
import com.jme3.util.SkyFactory;

import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Sky3d;
import jadex.extension.envsupport.observer.graphics.jmonkey.ViewportJMonkey;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.AbstractJMonkeyRenderer;


public class SkyJMonkeyRenderer extends AbstractJMonkeyRenderer
{
	/** Sky for jMonkey. */

	Spatial	sky;

	public Spatial draw(DrawableCombiner3d dc, Primitive3d primitive,
			SpaceObject sobj, ViewportJMonkey vp)
	{

		String skyfile = (String)((Sky3d)primitive).getSkyFile();
		boolean isSphere = (boolean)((Sky3d)primitive).isSphere();


		String skypath = (String)((Sky3d)primitive).getSkyPath();
		String weststr = (String)((Sky3d)primitive).getWest();
		String eaststr = (String)((Sky3d)primitive).getEast();
		String northstr = (String)((Sky3d)primitive).getNorth();
		String southstr = (String)((Sky3d)primitive).getSouth();
		String upstr = (String)((Sky3d)primitive).getUp();
		String downstr = (String)((Sky3d)primitive).getDown();

		if(!skypath.equals(""))
		{
			Texture west = assetManager.loadTexture(skypath.concat(weststr));
			Texture east = assetManager.loadTexture(skypath.concat(eaststr));
			Texture north = assetManager.loadTexture(skypath.concat(northstr));
			Texture south = assetManager.loadTexture(skypath.concat(southstr));
			Texture up = assetManager.loadTexture(skypath.concat(upstr));
			Texture down = assetManager.loadTexture(skypath.concat(downstr));

			sky = SkyFactory.createSky(assetManager, west, east, north, south,
					up, down);
		}
		else
		{
			sky = SkyFactory.createSky(assetManager, skyfile, isSphere);
		}


		sky.setCullHint(CullHint.Never);

		sky.setName("Skymap");

		return sky;


	}

}
