package jadex.extension.envsupport.observer.graphics.jmonkey.renderer;


import com.jme3.scene.Spatial;

import jadex.extension.envsupport.environment.SpaceObject;
import jadex.extension.envsupport.observer.graphics.drawable3d.DrawableCombiner3d;
import jadex.extension.envsupport.observer.graphics.drawable3d.Primitive3d;
import jadex.extension.envsupport.observer.graphics.jmonkey.ViewportJMonkey;

public interface IJMonkeyRenderer
{

	Spatial draw(DrawableCombiner3d dc, Primitive3d primitive, SpaceObject sobj,
			ViewportJMonkey vp);

	Spatial prepareAndExecuteDraw(DrawableCombiner3d drawableCombiner3d,
			Primitive3d primitive, SpaceObject sobj, ViewportJMonkey vp);
	
	Spatial prepareAndExecuteUpdate(DrawableCombiner3d drawableCombiner3d,
			Primitive3d primitive, SpaceObject sobj, ViewportJMonkey vp, Spatial sp);

}
