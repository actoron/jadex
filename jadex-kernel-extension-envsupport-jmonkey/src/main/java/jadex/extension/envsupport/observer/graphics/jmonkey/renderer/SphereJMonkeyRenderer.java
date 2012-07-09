package jadex.extension.envsupport.observer.graphics.jmonkey.renderer;

import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.jmonkey.ViewportJMonkey;

import com.jme3.bounding.BoundingBox;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Sphere;


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
			Object obj, ViewportJMonkey vp) {
			
			sphere = new Sphere(16, 16, 1);
		
			geo = new Geometry(identifier, sphere);
			
			return geo;
}
	
}
