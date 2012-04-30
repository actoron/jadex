package jadex.extension.envsupport.observer.graphics.jmonkey.renderer;

import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.jmonkey.ViewportJMonkey;

import com.jme3.bounding.BoundingBox;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.debug.Arrow;



public class ArrowJMonkeyRenderer extends AbstractJMonkeyRenderer
{
	/** Box for jMonkey. */
	private Arrow arrow;

	public Spatial draw(DrawableCombiner3d dc, Primitive3d primitive,
			Object obj, ViewportJMonkey vp) {

		    arrow = new Arrow(sizelocal);
		    
		    arrow.setLineWidth(2); 

			geo = new Geometry(identifier, arrow);

			return geo;
			
		
	}




}
