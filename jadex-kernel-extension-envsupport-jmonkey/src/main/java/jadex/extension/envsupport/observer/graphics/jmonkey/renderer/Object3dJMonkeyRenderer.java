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


public class Object3dJMonkeyRenderer extends AbstractJMonkeyRenderer
{
	/** Object for jMonkey. */
	private Spatial object;
	
	private AnimControl control;
	
	private String matpath;
	
	private Material mat;

	private HashMap<String, AnimChannel> animChannels; 
	
	public Spatial draw(DrawableCombiner3d dc, Primitive3d primitive,
			Object obj, ViewportJMonkey vp)
	{	

		matpath = ((String)dc.getBoundValue(obj, ((Object3d)primitive).getMaterialPath(), vp));
		if(matpath==null || matpath.equals(""))
		{
			matpath = (String)((Object3d) primitive).getMaterialPath();
//			System.out.println("material: " + matpath);
		}
		String file = ((String)dc.getBoundValue(obj, ((Object3d)primitive).getModelPath(), vp));
		if(file==null)
		{
			 file = (String)((Object3d) primitive).getModelPath();
		}
		 if(file.contains("?"))
		 {
			 SpaceObject sobj = (SpaceObject) obj;
			 String neighborhood = Integer.toBinaryString(((Integer) sobj.getProperty("neighborhood")));
			 file = parseTilesets(file, neighborhood);
//			 System.out.println("file: " + file);

		 }
		 

	
		
		Boolean hasLight = (Boolean)((Object3d) primitive).isHasLightMaterials();

        object = assetManager.loadModel(file);
        
        object.setName(identifier);
        
        if(!matpath.equals(""))
        {
        	mat = assetManager.loadMaterial(matpath);

        	object.setMaterial(mat);
//        	System.out.println("gesetztes material: " + matpath);
        }
		 
        
        
        
        
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
//			    for (String anim : control.getAnimationNames()) { System.out.println(anim); }
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
		    		AnimChannel tmp = control.createChannel();
		    		vp.getAnimChannels().put(c+" "+ obj.hashCode(), tmp);
		    		
		    	}
		    	
		    }
		    
		    
		}
		return object;


	}

	private String parseTilesets(String file, String neighborhood) {
		
		
		 String ret = file;
		 // Parse to correct Length according to the files
		 while(neighborhood.length()<8)
		 {
			 neighborhood = "0".concat(neighborhood);
		 }
		 // Special Random Cases
		 if(neighborhood.equals("00111110")||neighborhood.equals("10001111")||neighborhood.equals("11100011")||neighborhood.equals("11111000"))
		 {
			 int rnd = (int) (4*Math.random());
			 switch (rnd) {
			case 0:
				neighborhood = neighborhood.concat("a");
				break;
			case 1:
				neighborhood = neighborhood.concat("b");
				break;
			case 2:
				neighborhood = neighborhood.concat("c");
				break;
			case 3:
				neighborhood = neighborhood.concat("d");
				break;

			default:
				break;
			}
		 }
		 
		 ret = file.replace("?.j3o", "");
		 ret = ret.concat(neighborhood);
		 ret = ret.concat(".j3o");
//		 System.out.println("file: " + ret);
		return ret;
	}


}
