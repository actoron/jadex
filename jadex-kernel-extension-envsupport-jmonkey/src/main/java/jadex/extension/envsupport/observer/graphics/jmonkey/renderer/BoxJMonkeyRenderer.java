package jadex.extension.envsupport.observer.graphics.jmonkey.renderer;

import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.jmonkey.ViewportJMonkey;

import com.jme3.bounding.BoundingBox;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;


public class BoxJMonkeyRenderer extends AbstractJMonkeyRenderer
{
	/** Box for jMonkey. */
	private Box	box;

	public Spatial draw(DrawableCombiner3d dc, Primitive3d primitive,
			Object obj, ViewportJMonkey vp)
	{

		// Unit Cube
		box = new Box(Vector3f.ZERO, 1, 1, 1);

		geo = new Geometry(identifier, box);

		return geo;


	}


}
