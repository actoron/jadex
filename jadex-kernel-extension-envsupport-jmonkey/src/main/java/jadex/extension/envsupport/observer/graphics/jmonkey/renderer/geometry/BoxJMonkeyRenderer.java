package jadex.extension.envsupport.observer.graphics.jmonkey.renderer.geometry;

import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.jmonkey.ViewportJMonkey;
import jadex.extension.envsupport.observer.graphics.jmonkey.renderer.AbstractJMonkeyRenderer;


public class BoxJMonkeyRenderer extends AbstractJMonkeyRenderer
{
	/** Box for jMonkey. */
	private Box	box;

	public Spatial draw(DrawableCombiner3d dc, Primitive3d primitive,
			SpaceObject sobj, ViewportJMonkey vp)
	{

		

		
		// Unit Cube
		box = new Box(Vector3f.ZERO, 1, 1, 1);

		geo = new Geometry(identifier, box);

		return geo;


	}


}
