package jadex.extension.envsupport.observer.graphics.jmonkey.renderer.special;
import com.jme3.effect.ParticleEmitter;
import com.jme3.export.InputCapsule;
import com.jme3.export.JmeExporter;
import com.jme3.export.JmeImporter;
import com.jme3.export.OutputCapsule;
import com.jme3.export.Savable;
import com.jme3.material.Material;
import java.io.IOException;
 
public class EffectSaver implements Savable {
    private ParticleEmitter particelEmmitter;  // some custom user data
    

    public void write(JmeExporter ex) throws IOException {
        OutputCapsule capsule = ex.getCapsule(this);

        capsule.write(particelEmmitter,  "someJmeObject",  new ParticleEmitter());
    }
 
    public void read(JmeImporter im) throws IOException {
        InputCapsule capsule = im.getCapsule(this);

        particelEmmitter  = (ParticleEmitter)capsule.readSavable("someJmeObject",  new ParticleEmitter());
    }

	/**
	 * @return the particelEmmitter
	 */
	public ParticleEmitter getParticelEmmitter()
	{
		return particelEmmitter;
	}

	/**
	 * @param particelEmmitter the particelEmmitter to set
	 */
	public void setParticelEmmitter(ParticleEmitter particelEmmitter)
	{
		this.particelEmmitter = particelEmmitter;
	}
}