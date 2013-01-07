package jadex.extension.envsupport.observer.graphics.jmonkey.renderer.special;

import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Sound3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.jmonkey.ViewportJMonkey;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.AbstractJMonkeyRenderer;

import com.jme3.math.FastMath;
import com.jme3.scene.Node;

import com.jme3.audio.AudioNode;
import com.jme3.scene.Spatial;

public class SoundJMonkeyPlayer extends AbstractJMonkeyRenderer
{
	private AudioNode soundnode;
	
	private String soundfile = new String("");
	
	private boolean looping;
	
	private boolean continuously;
	
	private boolean positional;
	
	private double volume;
	
	//TODO: clean up!
	public Spatial draw(DrawableCombiner3d dc, Primitive3d primitive, SpaceObject sobj, ViewportJMonkey vp)
	{

			soundfile = (String)((Sound3d) primitive).getSoundfile();
			
			 if(soundfile!=null&&soundfile.contains("?"))
			 {
				 soundfile = soundfile.replace("?.ogg", "");
				 
				 int files = ((Sound3d) primitive).getNumRndFiles();
				 
				 int numberselect = (int) (Math.random()*files)+1;
				 String selected = "";
				 if(numberselect <10)
				 {
					  selected = "0"+numberselect; 
				 }
				 else
				 {
					  selected = ""+numberselect; 
				 }

				 soundfile = soundfile.concat(selected);
				 
				 soundfile = soundfile.concat(".ogg");

			 }
//		}
		
		looping = (boolean)((Sound3d) primitive).isLoop();
		continuously = (boolean)((Sound3d) primitive).isContinuosly();
		positional = (boolean)((Sound3d) primitive).isPositional();
		
		volume = (float)((Sound3d) primitive).getVolume();
		
		soundnode = new AudioNode(assetManager, soundfile, false);
		soundnode.setName(identifier);
		soundnode.setReverbEnabled(true);	
		soundnode.setLooping(looping);
		soundnode.setVolume((float)volume);
		
		if(positional)
		{
			soundnode.setPositional(true);
			soundnode.setReverbEnabled(true);
			soundnode.setRefDistance(50f);
		}

	    
		return soundnode;
	}




}
