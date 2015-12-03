package jadex.extension.envsupport.observer.graphics.opengl;

import java.awt.Color;

import javax.media.opengl.GL;

import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.observer.graphics.layer.Layer;
import jadex.extension.envsupport.observer.gui.SObjectInspector;
import jadex.extension.envsupport.observer.perspective.IPerspective;

public class ColorLayerGLRenderer implements ILayerGLRenderer
{
	/**
	 * Draws the layer.
	 * @param persp the Perspective
	 * @param layer the layer being drawn
	 * @param areaSize the area size
	 * @param vp the viewport
	 */
	public void draw(IPerspective persp, Layer layer, IVector2 areaSize, ViewportJOGL vp)
	{
		Color c = layer.getColor() instanceof Color? (Color)layer.getColor(): (Color)SObjectInspector.getProperty(persp, (String)layer.getColor(), "$perspective", 
				vp.getPerspective().getObserverCenter().getSpace().getFetcher());
		if(c==null)
			c=Color.WHITE;
		
		GL gl = vp.getContext();
		
		gl.glColor4fv(c.getComponents(null), 0);
		
		gl.glBegin(GL.GL_QUADS);
		gl.glVertex2f(0.0f, 0.0f);
		gl.glVertex2f(0.0f, areaSize.getYAsFloat());
		gl.glVertex2f(areaSize.getXAsFloat(), areaSize.getYAsFloat());
		gl.glVertex2f(areaSize.getXAsFloat(), 0.0f);
		gl.glEnd();
	}
}
