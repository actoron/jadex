package jadex.extension.envsupport.observer.graphics.jmonkey.renderer;

import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Sound3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.jmonkey.ViewportJMonkey;
import com.jme3.scene.Node;

import com.jme3.audio.AudioNode;
import com.jme3.scene.Spatial;

public class SoundJMonkeyPlayer extends AbstractJMonkeyRenderer
{

	private Node node;
	
	private AudioNode soundnode;
	
	private String soundfile = new String("");
	
	private boolean looping;
	
	private boolean continuously;
	
	private boolean positional;
	
	private double volume;
	
	
	public Spatial draw(DrawableCombiner3d dc, Primitive3d primitive, Object obj, ViewportJMonkey vp)
	{
		node = new Node(identifier);
//		soundfile = ((String)dc.getBoundValue(obj, ((Sound3d)primitive).getSoundfile(), vp));
//		if(soundfile==null)
//		{
			soundfile = (String)((Sound3d) primitive).getSoundfile();
//		}
		
		looping = (boolean)((Sound3d) primitive).isLoop();
		continuously = (boolean)((Sound3d) primitive).isContinuosly();
		positional = (boolean)((Sound3d) primitive).isPositional();
		
		volume = (float)((Sound3d) primitive).getVolume();
		
		soundnode = new AudioNode(assetManager, soundfile, false);
		
		((Node)soundnode).setUserData("id", identifier);
		
		
		soundnode.setLooping(looping);
		soundnode.setPositional(positional);
		soundnode.setVolume((float)volume);
		
		node.attachChild(soundnode);
		
		if(continuously)
		{
			soundnode.setVolume((float)volume);
			soundnode.play();
		}
		
	    
		return node;
	}

}
