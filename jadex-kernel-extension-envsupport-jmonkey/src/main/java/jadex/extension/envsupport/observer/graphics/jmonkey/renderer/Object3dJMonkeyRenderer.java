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
import com.jme3.texture.Texture;


public class Object3dJMonkeyRenderer extends AbstractJMonkeyRenderer
{
	/** Object for jMonkey. */
	private Spatial object;

	public Spatial draw(DrawableCombiner3d dc, Primitive3d primitive,
			Object obj, ViewportJMonkey vp)
	{
		String file = (String)((Object3d) primitive).getModelPath();
		
		Boolean hasLight = (Boolean)((Object3d) primitive).isHasLightMaterials();

        object = assetManager.loadModel(file);
        
        object.setName(identifier);
        
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		
		Color c = (Color)dc.getBoundValue(obj, primitive.getColor(), vp);
		int cblue = c.getBlue();

		if(!primitive.getTexturePath().equals(""))
		{
			mat.setColor("Color",ColorRGBA.White);
			Texture tex_ml = assetManager.loadTexture(primitive.getTexturePath());
			mat.setTexture("ColorMap", tex_ml);
			object.setMaterial(mat);
		}
		else if(cblue != 64)
		{
			System.out.println("cBLUE!");
			float alpha= ((float)c.getAlpha())/255;
			ColorRGBA color = new ColorRGBA(((float)c.getRed())/255,((float)c.getGreen())/255,((float)c.getBlue())/255, alpha);
			mat.setColor("Color",color);
			object.setMaterial(mat);
		}
		else if(!vp.getCapabilities().contains("VertexTextureFetch")&&hasLight)
		{
//			mat= new Material(assetManager, "Common/MatDefs/Misc/ShowNormals.j3md");
//			object.setMaterial(mat);
			
			
//			Material mat_tt = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//
//			mat_tt.setColor("Color",ColorRGBA.White);
//			object.setMaterial(mat_tt);
//			if(!primitive.getTexturePath().equals(""))
//			{
//			
//				Texture tex_ml = assetManager.loadTexture(primitive.getTexturePath());
//				mat_tt.setTexture("ColorMap", tex_ml);
//			}
//			
//			object.setMaterial(mat_tt);
		}

		

		

		return object;


	}


}
