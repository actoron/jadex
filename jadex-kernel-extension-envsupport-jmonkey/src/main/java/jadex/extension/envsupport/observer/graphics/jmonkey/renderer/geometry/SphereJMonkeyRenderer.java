package jadex.extension.envsupport.observer.graphics.jmonkey.renderer.geometry;

import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;

import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.jmonkey.ViewportJMonkey;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.AbstractJMonkeyRenderer;


public class SphereJMonkeyRenderer extends AbstractJMonkeyRenderer
{
	/**
	 * Generates a Circle.
	 * @param dc the DrawableCombiner drawing the object
	 * @param primitive the primitive3d being drawn
	 * @param obj the object being drawn
	 * @param vp the viewport
	 */
	/** Sphere for jMonkey. */
	private Sphere sphere;

	public Spatial draw(DrawableCombiner3d dc, Primitive3d primitive,
			SpaceObject sobj, ViewportJMonkey vp) {
			
			sphere = new Sphere(16, 16, 1);
		
			geo = new Geometry(identifier, sphere);
			
			return geo;
}
	
}
