package jadex.extension.envsupport.observer.graphics.jmonkey.renderer;

import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Object3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.jmonkey.ViewportJMonkey;

import java.util.HashMap;
import java.util.TreeSet;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.SkeletonDebugger;

public abstract class AObject3dRenderer extends AbstractJMonkeyRenderer {
	
	protected Spatial object;
	
	protected AnimControl control;
	
	protected String matpath;
	
	protected Material mat;

	protected HashMap<String, AnimChannel> animChannels; 

	protected void loadAndSetMesh(DrawableCombiner3d dc, Primitive3d primitive,
			Object obj, ViewportJMonkey vp) {
		String file = ((String)dc.getBoundValue(obj, ((Object3d)primitive).getModelPath(), vp));
		if(file==null)
		{
			 file = (String)((Object3d) primitive).getModelPath();
		}
		
		 if(file!=null&&file.contains("?"))
		 {
			 SpaceObject sobj = (SpaceObject) obj;
			 String neighborhood =  (String) sobj.getProperty("neighborhood");
			 file = file.replace("?.j3o", "");
			 file = file.concat(neighborhood);
			 file = file.concat(".j3o");
		 }

	     object = assetManager.loadModel(file);
	        
	     object.setName(identifier);
		
	}
	
	protected void loadAndSetMaterial(DrawableCombiner3d dc, Primitive3d primitive, Object obj, ViewportJMonkey vp) {
		
		
		matpath = ((String)dc.getBoundValue(obj, ((Object3d)primitive).getMaterialPath(), vp));
		if(matpath==null || matpath.equals(""))
		{
			matpath = (String)((Object3d) primitive).getMaterialPath();
		}

        if(!matpath.equals(""))
        {
        	mat = assetManager.loadMaterial(matpath);

        	object.setMaterial(mat);
        }
		
	}
	
	protected void loadAndSetAnimation(DrawableCombiner3d dc, Primitive3d primitive,
			Object obj, ViewportJMonkey vp) {
		TreeSet<String> channels = 		((TreeSet<String>)dc.getBoundValue(obj, ((Object3d)primitive).getChannels(), vp));

		object.setUserData("Animation", false);
		
		control = object.getControl(AnimControl.class);
		if(control != null)
		{
			control = object.getControl(AnimControl.class);
			
			if((Boolean)((Object3d) primitive).isRigDebug())
			{
			    SkeletonDebugger skeletonDebug =  new SkeletonDebugger("skeleton", control.getSkeleton());
			    Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
			    mat2.setColor("Color", ColorRGBA.Green);
			    mat2.getAdditionalRenderState().setDepthTest(false);
			    skeletonDebug.setMaterial(mat2);
			    ((Node)object).attachChild(skeletonDebug);
			}

		    
		    if(channels.size()>0)
		    {
		    	((Node)object).setUserData("Animation", true);
		    	animChannels = new HashMap<String, AnimChannel>();
		    	for(String c : channels)
		    	{
		    		AnimChannel tmp = control.createChannel();
		    		vp.getAnimChannels().put(c+" "+ obj.hashCode(), tmp);
		    		
		    	}
		    }
		}
		
	}

}
