package jadex.extension.envsupport.observer.graphics.jmonkey.renderer;

import java.awt.Color;

import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Object3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.jmonkey.ViewportJMonkey;

import com.jme3.bounding.BoundingBox;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Spatial;


public class Object3dJMonkeyRenderer extends AbstractJMonkeyRenderer
{
	/** Object for jMonkey. */
	private Spatial object;

	public Spatial draw(DrawableCombiner3d dc, Primitive3d primitive,
			Object obj, ViewportJMonkey vp)
	{
		String file = (String)((Object3d) primitive).getModelPath();
		
		

        object = assetManager.loadModel(file);


        object.setModelBound(new BoundingBox());
        
        object.setName(identifier);
		
		Color c = (Color)dc.getBoundValue(obj, primitive.getColor(), vp);
		int cblue = c.getBlue();
		if(cblue != 64)
		{
			Material mat_tt = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
			float alpha= ((float)c.getAlpha())/255;
			ColorRGBA color = new ColorRGBA(((float)c.getRed())/255,((float)c.getGreen())/255,((float)c.getBlue())/255, alpha);
			mat_tt.setColor("Color",color);
			object.setMaterial(mat_tt);
		}

		

		

		return object;


	}


}
