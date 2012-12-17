package jadex.extension.envsupport.observer.graphics.jmonkey.renderer;

import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.jmonkey.ViewportJMonkey;

public class PointLightRenderer extends AbstractJMonkeyRenderer {

	private Box	box;
	
	Geometry geo;

	public Spatial draw(DrawableCombiner3d dc, Primitive3d primitive,
			Object obj, ViewportJMonkey vp) {
		
		System.out.println("idi" + identifier);
		box = new Box(Vector3f.ZERO, 0.0001f, 0.0001f, 0.0001f);

		geo = new Geometry(identifier, box);

		return geo;
	}

}
