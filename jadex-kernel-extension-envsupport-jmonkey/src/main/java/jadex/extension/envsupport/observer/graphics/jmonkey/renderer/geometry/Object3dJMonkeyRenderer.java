package jadex.extension.envsupport.observer.graphics.jmonkey.renderer.geometry;

import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.math.Vector3Double;
import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Object3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.special.Animation;
import jadex.extension.envsupport.observer.graphics.drawable3d.special.Materialfile;
import jadex.extension.envsupport.observer.graphics.jmonkey.ViewportJMonkey;
import jadex.extension.envsupport.observer.gui.SObjectInspector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import javax.swing.border.MatteBorder;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.SkeletonDebugger;
import com.jme3.scene.shape.Quad;
import com.jme3.water.SimpleWaterProcessor;


public class Object3dJMonkeyRenderer extends AObject3dRenderer
{
	/** 3d Object for jMonkey. */

	private SimpleWaterProcessor	waterProcessor;


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
								((Node)object).detachChild(s);

							}

						}

					}
				}


				

			}


		}


	}


}
