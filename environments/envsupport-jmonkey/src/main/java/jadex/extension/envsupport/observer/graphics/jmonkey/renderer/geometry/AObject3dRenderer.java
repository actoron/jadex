package jadex.extension.envsupport.observer.graphics.jmonkey.renderer.geometry;

import java.util.HashMap;
import java.util.TreeSet;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.Animation;
import com.jme3.animation.BoneTrack;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.SkeletonDebugger;

import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Object3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.jmonkey.ViewportJMonkey;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.AbstractJMonkeyRenderer;


public abstract class AObject3dRenderer extends AbstractJMonkeyRenderer
{

	protected Spatial						object;

	protected AnimControl					control;

	protected String						matpath;

	protected Material						mat;

	protected HashMap<String, AnimChannel>	animChannels;

	protected void loadAndSetMesh(DrawableCombiner3d dc, Primitive3d primitive, SpaceObject sobj, ViewportJMonkey vp)
	{
		String file = ((String)dc.getBoundValue(sobj, ((Object3d)primitive).getModelPath(), vp));
		if(file == null)
		{
			file = (String)((Object3d)primitive).getModelPath();
		}

		if(file != null && file.contains("?"))
		{
			String neighborhood = (String)sobj.getProperty("neighborhood");
			file = file.replace("?.j3o", "");
			file = file.concat(neighborhood);
			file = file.concat(".j3o");
		}


		if(matpath.equals(""))
		{
			if(vp.complexobjects.containsKey(file))
			{
				Spatial tmpObject = vp.complexobjects.get(file);
				object = tmpObject.clone();
			}
			else
			{
				Spatial tmp = assetManager.loadModel(file);
				vp.complexobjects.put(file, tmp);
				object = tmp;
			}
		}
		else
		{
			object = assetManager.loadModel(file);
		}

		object.setName(identifier);


	}

	protected void loadAndSetMaterial(DrawableCombiner3d dc, Primitive3d primitive, SpaceObject sobj, ViewportJMonkey vp)
	{


		if(!matpath.equals(""))
		{
			if(vp.materials.containsKey(matpath))
			{
				mat = vp.materials.get(matpath);
			}
			else
			{
				Material tmp = assetManager.loadMaterial(matpath);
				vp.materials.put(matpath, tmp);
				mat = tmp;
			}


			object.setMaterial(mat);
		}

	}

	protected void loadAndSetAnimation(DrawableCombiner3d dc, Primitive3d primitive, SpaceObject sobj, ViewportJMonkey vp)
	{
		TreeSet<String> channels = ((TreeSet<String>)dc.getBoundValue(sobj, ((Object3d)primitive).getChannels(), vp));

		object.setUserData("Animation", false);
		Spatial usedObject = object;
		if(object.getControl(AnimControl.class) == null){
			
			
			if(object instanceof Geometry)
			{
				
			}
			else
			{
			Spatial newobject = findAnimNode((Node) object);
			if(newobject != null){
				usedObject = newobject;
			} else {
				usedObject = object;
			}
			}
		}
		control = usedObject.getControl(AnimControl.class);
		if(control != null)
		{
			control = usedObject.getControl(AnimControl.class);

			if((Boolean)((Object3d)primitive).isRigDebug())
			{
				SkeletonDebugger skeletonDebug = new SkeletonDebugger("skeleton", control.getSkeleton());
				Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
				mat2.setColor("Color", ColorRGBA.Green);
				mat2.getAdditionalRenderState().setDepthTest(false);
				skeletonDebug.setMaterial(mat2);
				((Node)usedObject).attachChild(skeletonDebug);
			}


			if(channels.size() > 0)
			{
				((Node)usedObject).setUserData("Animation", true);
				animChannels = new HashMap<String, AnimChannel>();
				for(String c : channels)
				{
					AnimChannel tmp = control.createChannel();
					vp.getAnimChannels().put(c + " " + sobj.hashCode(), tmp);

				}
			}
		}

	}
	
    private static Node findAnimNode(Node node) {
        AnimControl l_animControl;

        l_animControl = node.getControl(AnimControl.class);
        if (l_animControl != null) {
            if (!l_animControl.getAnimationNames().isEmpty()) {
                Animation l_anim = l_animControl.getAnim(l_animControl.getAnimationNames().iterator().next());
                if (l_anim != null && l_anim.getTracks().length > 0 && l_anim.getTracks()[0] instanceof BoneTrack) {
                    return node;
                }
            }
        }
        
        for (Spatial l_child : node.getChildren()) {
            if(l_child.getControl(AnimControl.class) != null) {
                return (Node) l_child;
            }
            if(!(l_child instanceof Geometry)){
                
                Node l_ret = findAnimNode((Node) l_child);
                if (l_ret != null) {
                    return l_ret;
                }
            }
        }

        return null;
    }

}
