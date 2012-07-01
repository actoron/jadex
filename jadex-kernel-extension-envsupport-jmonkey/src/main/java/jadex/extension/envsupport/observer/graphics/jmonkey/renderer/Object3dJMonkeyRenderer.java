package jadex.extension.envsupport.observer.graphics.jmonkey.renderer;

import java.awt.Color;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;

import jadex.extension.envsupport.observer.graphics.drawable3d.Animation;
import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Object3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.jmonkey.ViewportJMonkey;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Spatial;
import com.jme3.scene.Spatial.CullHint;
import com.jme3.scene.debug.SkeletonDebugger;
import com.jme3.texture.Texture;
import com.jme3.scene.Node;


public class Object3dJMonkeyRenderer extends AbstractJMonkeyRenderer
{
	/** Object for jMonkey. */
	private Spatial object;
	
	private AnimControl control;

	private HashMap<String, AnimChannel> animChannels; 
	
	public Spatial draw(DrawableCombiner3d dc, Primitive3d primitive,
			Object obj, ViewportJMonkey vp)
	{	

		String file = ((String)dc.getBoundValue(obj, ((Object3d)primitive).getModelPath(), vp));
		if(file==null)
		{
			 file = (String)((Object3d) primitive).getModelPath();
		}
		
		Boolean hasLight = (Boolean)((Object3d) primitive).isHasLightMaterials();

        object = assetManager.loadModel(file);
        
        object.setName(identifier);
        
		TreeSet<String> channels = 		((TreeSet<String>)dc.getBoundValue(obj, ((Object3d)primitive).getChannels(), vp));
//		List<Animation> animations = 	((List<Animation>)dc.getBoundValue(obj, ((Object3d)primitive).getAnimations(), vp));
		
		
		object.setUserData("Animation", false);
		

		
		control = object.getControl(AnimControl.class);
		if(control != null)
		{
			control = object.getControl(AnimControl.class);
			
			
			if((Boolean)((Object3d) primitive).isRigDebug())
			{
				// Show for Debugging
			    for (String anim : control.getAnimationNames()) { System.out.println(anim); }
			    SkeletonDebugger skeletonDebug = 
			            new SkeletonDebugger("skeleton", control.getSkeleton());
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
		    		vp.getAnimChannels().put(c+" "+ obj.hashCode(), control.createChannel());
		    		
		    	}
		    	
		    }
		    
		    
		}
		return object;


	}


}
