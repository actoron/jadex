package jadex.extension.envsupport.observer.graphics.jmonkey.renderer;

import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Object3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.special.Animation;
import jadex.extension.envsupport.observer.graphics.drawable3d.special.Materialfile;
import jadex.extension.envsupport.observer.graphics.jmonkey.ViewportJMonkey;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.SkeletonDebugger;


public class Object3dJMonkeyRenderer extends AObject3dRenderer
{
	/** 3d Object for jMonkey. */

	
	public Spatial draw(DrawableCombiner3d dc, Primitive3d primitive,
			Object obj, ViewportJMonkey vp)
	{	

		loadAndSetMesh(dc, primitive, obj, vp);
		
		loadAndSetMaterial(dc, primitive, obj, vp);
		
		loadAndSetAnimation(dc, primitive, obj, vp);
		
		loadAndSetComplexMaterial(dc, primitive, obj, vp);

		return object;


	}
	
	protected void loadAndSetComplexMaterial(DrawableCombiner3d dc, Primitive3d primitive, Object obj, ViewportJMonkey vp) {
		ArrayList<Materialfile> materials = ((Object3d)primitive).getMaterials();
		if(materials!=null)
		{
		if(object instanceof Node)
		{
			Node objectnode = (Node)object; 
			
			for(Spatial s : objectnode.getChildren())
			{
				for(Materialfile mat : materials)
				{
					if(mat.getName().equals(s.getName()))
					{
						s.setMaterial(assetManager.loadMaterial(mat.getPath()));
					}
				}
				
				}
				
				
			}
		}
		}


		
		    
		
	
	
	








}
