package jadex.extension.envsupport.observer.graphics.jmonkey.renderer.geometry;

import com.jme3.bounding.BoundingBox;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Torus;

import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Torus3d;
import jadex.extension.envsupport.observer.graphics.jmonkey.ViewportJMonkey;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.AbstractJMonkeyRenderer;



public class TorusJMonkeyRenderer extends AbstractJMonkeyRenderer
{
	/** Dome for jMonkey. */
	private Torus torus;

	public Spatial draw(DrawableCombiner3d dc, Primitive3d primitive,
			SpaceObject sobj, ViewportJMonkey vp) {

		
			float innerRadius = (float)((Torus3d) primitive).getInnerRadius();
			float outerRadius = (float)((Torus3d) primitive).getOuterRadius();
			int circleSamples = (int)((Torus3d) primitive).getCircleSamples();
			int radialSamples = (int)((Torus3d) primitive).getRadialSamples();

	
			torus = new Torus(circleSamples, radialSamples, innerRadius, outerRadius);

			geo = new Geometry(identifier, torus);
			
			geo.setModelBound(new BoundingBox());

			return geo;
			
		
	}

}
