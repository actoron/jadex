package jadex.extension.envsupport.observer.graphics.jmonkey.renderer.geometry;

import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Cylinder;

import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.observer.graphics.drawable3d.Cylinder3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.jmonkey.ViewportJMonkey;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.AbstractJMonkeyRenderer;


public class CylinderJMonkeyRenderer extends AbstractJMonkeyRenderer
{
	/** Cylinder for jMonkey. */
	private Cylinder	cylinder;

	public Spatial draw(DrawableCombiner3d dc, Primitive3d primitive,
			SpaceObject sobj, ViewportJMonkey vp)
	{

		float radius = (float)((Cylinder3d)primitive).getRadius();
		float height = (float)((Cylinder3d)primitive).getHeight();
		
		// Height *2 for same Scale like a box or sphere
		cylinder = new Cylinder(30, 30, radius, height*2, true);

		geo = new Geometry(identifier, cylinder);

		return geo;


	}

}
