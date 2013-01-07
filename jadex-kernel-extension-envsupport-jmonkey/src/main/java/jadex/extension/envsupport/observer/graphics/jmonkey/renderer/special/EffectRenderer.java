package jadex.extension.envsupport.observer.graphics.jmonkey.renderer.special;

import com.jme3.effect.ParticleEmitter;
import com.jme3.effect.ParticleMesh.Type;
import com.jme3.effect.shapes.EmitterSphereShape;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.FastMath;
import com.jme3.math.Vector3f;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;

import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.special.Effect;
import jadex.extension.envsupport.observer.graphics.drawable3d.special.Materialfile;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.jmonkey.ViewportJMonkey;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.AbstractJMonkeyRenderer;


public class EffectRenderer extends AbstractJMonkeyRenderer
{

	private Node effectNode;
	
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

			if(predefinedId.equals("shockwave01"))
			{
				ParticleEmitter shockwave = new ParticleEmitter("Shockwave", Type.Triangle, 2);
				shockwave.setFaceNormal(Vector3f.UNIT_Y);
				shockwave.setStartColor(new ColorRGBA(.48f, 0.17f, 0.01f, (float)(.8f / 1)));
				shockwave.setEndColor(new ColorRGBA(.48f, 0.17f, 0.01f, 0f));
				
				shockwave.setStartSize(startsize);
				shockwave.setEndSize(endsize);

				shockwave.setParticlesPerSec(0);
				shockwave.setGravity(0, 0, 0);
				shockwave.setLowLife(0.7f);
				shockwave.setHighLife(0.7f);
				shockwave.setImagesX(1);
				shockwave.setImagesY(1);
				Material mata = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
				mata.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/shockwave.png"));
				shockwave.setMaterial(mata);

				effectNode.attachChild(shockwave);
				// shockwave.emitAllParticles();

			}

			if(predefinedId.equals("debris01"))
			{
				ParticleEmitter debris = new ParticleEmitter("Debris", Type.Triangle, 15);
				debris.setSelectRandomImage(true);
				debris.setRandomAngle(true);
				debris.setRotateSpeed(FastMath.TWO_PI * 2);
				debris.setStartColor(new ColorRGBA(ColorRGBA.Brown.r, ColorRGBA.Brown.g, ColorRGBA.Brown.b, (float)(1.0f)));
				// debris.setEndColor(new ColorRGBA(.5f, 0.5f, 0.5f,
				// 0f));

				debris.setEndColor(new ColorRGBA(ColorRGBA.Brown.r, ColorRGBA.Brown.g, ColorRGBA.Brown.b, 1.0f));

				debris.setStartSize(startsize);
				debris.setEndSize(endsize);
				
				debris.setFacingVelocity(false);
				
				debris.getParticleInfluencer().setInitialVelocity(new Vector3f(0, 5, 0));
				debris.getParticleInfluencer().setVelocityVariation(1f);

				debris.setParticlesPerSec(0);
				debris.setShape(new EmitterSphereShape(Vector3f.ZERO, 0.5f));
				// debris.setParticlesPerSec(0);
				debris.setGravity(0, 4f, 0);
				debris.setLowLife(0.7f);
				debris.setHighLife(0.9f);
				debris.setImagesX(3);
				debris.setImagesY(3);
//				debris.setLocalTranslation(0, 1f, 0);
				Material matx = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
				matx.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/Debris.png"));
				debris.setMaterial(matx);
				effectNode.attachChild(debris);
				// debris.emitAllParticles();
			}

			if(predefinedId.equals("smoke01"))
			{
				Material material = new Material(assetManager, "Common/MatDefs/Misc/Particle.j3md");
				material.setTexture("Texture", assetManager.loadTexture("Effects/Explosion/flame.png"));
				material.setFloat("Softness", 6f); //
				ParticleEmitter smoke = new ParticleEmitter("Smoke", Type.Triangle, 10);
				smoke.setParticlesPerSec(0);
				smoke.setMaterial(material);
				smoke.setShape(new EmitterSphereShape(Vector3f.ZERO, 0.8f));
				smoke.setImagesX(2);
				smoke.setImagesY(2); // 2x2 texture animation
				smoke.setStartColor(new ColorRGBA(0.0f, 0.0f, 0.0f, 0.7f)); // dark
																			// gray
				// smoke.setEndColor(new ColorRGBA(0.5f, 0.5f, 0.5f,
				// 0.01f)); // gray
				smoke.setEndColor(new ColorRGBA(ColorRGBA.Brown.r, ColorRGBA.Brown.g, ColorRGBA.Brown.b, 0.01f));
				
				smoke.setStartSize(startsize);
				smoke.setEndSize(endsize);
				
				smoke.setGravity(0, -0.01f, 0);
				smoke.setLowLife(4f);
				smoke.setHighLife(6f);
//				smoke.setLocalTranslation(0, 2f, 0);
				effectNode.attachChild(smoke);
				// smoke.emitAllParticles();
			}
			

		

	}


}
