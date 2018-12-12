package jadex.extension.envsupport.observer.graphics.jmonkey.renderer.geometry;

import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Dome;

import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.observer.graphics.drawable3d.Dome3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.jmonkey.ViewportJMonkey;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.AbstractJMonkeyRenderer;



public class DomeJMonkeyRenderer extends AbstractJMonkeyRenderer
{
	/** Dome for jMonkey. */
	private Dome dome;

	public Spatial draw(DrawableCombiner3d dc, Primitive3d primitive,
			SpaceObject sobj, ViewportJMonkey vp) {

		
			float radius = (float)((Dome3d) primitive).getRadius();
			int samples = (int)((Dome3d) primitive).getSamples();
			int planes = (int)((Dome3d) primitive).getPlanes();

	
			dome = new Dome(Vector3f.ZERO, planes, samples, radius, false);


			geo = new Geometry(identifier, dome);

			return geo;
			
		
	}

}
