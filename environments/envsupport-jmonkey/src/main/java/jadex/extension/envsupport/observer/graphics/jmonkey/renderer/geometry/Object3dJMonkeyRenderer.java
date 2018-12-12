package jadex.extension.envsupport.observer.graphics.jmonkey.renderer.geometry;

import java.util.ArrayList;

import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Object3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.special.Materialfile;
import jadex.extension.envsupport.observer.graphics.drawable3d.special.SpecialAction;
import jadex.extension.envsupport.observer.graphics.jmonkey.ViewportJMonkey;


public class Object3dJMonkeyRenderer extends AObject3dRenderer
{
	/** 3d Object for jMonkey. */

//	private SimpleWaterProcessor	waterProcessor;


	public Spatial draw(DrawableCombiner3d dc, Primitive3d primitive, SpaceObject sobj, ViewportJMonkey vp)
	{


		 matpath = ((String)dc.getBoundValue(sobj, ((Object3d)primitive).getMaterialPath(), vp));
			if(matpath==null || matpath.equals(""))
			{
				matpath = (String)((Object3d) primitive).getMaterialPath();
			}
			
		loadAndSetMesh(dc, primitive, sobj, vp);

		loadAndSetMaterial(dc, primitive, sobj, vp);

		loadAndSetAnimation(dc, primitive, sobj, vp);

		loadAndSetComplexMaterial(dc, primitive, sobj, vp);


		return object;


	}

	protected void loadAndSetComplexMaterial(DrawableCombiner3d dc, Primitive3d primitive, Object obj, ViewportJMonkey vp)
	{
		ArrayList<Materialfile> materialvalues = ((Object3d)primitive).getMaterials();
		if(materialvalues != null)
		{
			if(object instanceof Node)
			{
				Node objectnode = (Node)object;

				for(Spatial s : objectnode.getChildren())
				{
					for(Materialfile mat : materialvalues)
					{

						if(mat.getName().equals(s.getName()))
						{
							
							String matpath = mat.getPath();
							Material mati = assetManager.loadMaterial(matpath);
							
							if(vp.materials.containsKey(matpath))
							{
								mati = vp.materials.get(matpath);
							}
							else
							{
								vp.materials.put(matpath, mati);
							}
							
							
							s.setMaterial(mati);
							if(mat.isUseAlpha())
							{
								mati.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
//								s.setQueueBucket(Bucket.Translucent);
								s.setQueueBucket(Bucket.Transparent);

								// IF WATER FILTER!
							}
							
							if(mat.getSpecialAction()==SpecialAction.DELETE)
							{
								((Node)object).detachChild(s);
							}

						}

					}
				}


				

			}


		}


	}


}
