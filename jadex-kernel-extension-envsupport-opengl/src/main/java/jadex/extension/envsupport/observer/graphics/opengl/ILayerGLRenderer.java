package jadex.extension.envsupport.observer.graphics.opengl;

import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.observer.graphics.layer.Layer;
import jadex.extension.envsupport.observer.perspective.IPerspective;

public interface ILayerGLRenderer
{
	/**
	 * Draws the layer.
	 * @param persp the Perspective
	 * @param layer the layer being drawn
	 * @param areaSize the area size
	 * @param vp the viewport
	 */
	public abstract void draw(IPerspective persp, Layer layer, IVector2 areaSize, ViewportJOGL vp);
}
