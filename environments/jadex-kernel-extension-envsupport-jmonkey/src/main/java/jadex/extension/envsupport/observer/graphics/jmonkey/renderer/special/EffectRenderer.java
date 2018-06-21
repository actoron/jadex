package jadex.extension.envsupport.observer.graphics.jmonkey.renderer.special;

import java.lang.reflect.Constructor;

import com.jme3.asset.AssetManager;
import com.jme3.effect.ParticleEmitter;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.special.Effect;
import jadex.extension.envsupport.observer.graphics.jmonkey.ViewportJMonkey;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.AbstractJMonkeyRenderer;


public class EffectRenderer extends AbstractJMonkeyRenderer
{

	private Node	effectNode;

	private boolean	predefined;

	private String	predefinedId;

	private float	startsize;

	private float	endsize;

	public Spatial draw(DrawableCombiner3d dc, Primitive3d primitive, SpaceObject sobj, ViewportJMonkey vp)
	{
		effectNode = new Node("effectNode for : " + identifier);
		predefined = (boolean)((Effect)primitive).isPredefined();
		predefinedId = (String)((Effect)primitive).getPredefinedId();
		startsize = (float)((Effect)primitive).getStartsize();
		endsize = (float)((Effect)primitive).getEndsize();
		if(predefined)
		{
			setPredefinedEffect(predefinedId, startsize, endsize);
		}
		return effectNode;
	}

	private void setPredefinedEffect(String predefinedId, float startsize, float endsize)
	{


		ParticleEmitter shockwave = null;
		try
		{
			Constructor con = Class.forName(predefinedId, true, Thread.currentThread().getContextClassLoader()).getConstructor(new Class[]{AssetManager.class});

			shockwave = (ParticleEmitter)con.newInstance(new Object[]{assetManager});

		}
		catch(Exception e)
		{
			// TODO: handle exception
		}


		effectNode.attachChild(shockwave);
		// shockwave.emitAllParticles();


	}


}
