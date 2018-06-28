package jadex.extension.envsupport.observer.graphics.opengl;

import java.awt.Color;

import javax.media.opengl.GL;

import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.observer.graphics.layer.Layer;
import jadex.extension.envsupport.observer.graphics.layer.TiledLayer;
import jadex.extension.envsupport.observer.gui.SObjectInspector;
import jadex.extension.envsupport.observer.perspective.IPerspective;

public class TiledLayerGLRenderer implements ILayerGLRenderer
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
		int texture = 0;
		GL gl = vp.getContext();
		TiledLayer tl = (TiledLayer) layer;
		
		try
		{
			texture = ((Integer) layer.getRenderInfo(0)).intValue();
		}
		catch(Exception e)
		{
			texture = vp.getTexture(gl, tl.getTexturePath());
			layer.setRenderInfo(0, Integer.valueOf(texture));
		}
		
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, texture);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);
		
		Color c = layer.getColor() instanceof Color? (Color)layer.getColor(): (Color)SObjectInspector.getProperty(persp, (String)layer.getColor(), "$perspective", 
			vp.getPerspective().getObserverCenter().getSpace().getFetcher());
		gl.glColor4fv(c.getComponents(null), 0);
//		gl.glColor4fv(((Color) SObjectInspector.getPropertyAsClass(layerObject, modColor_, Color.class)).getComponents(null), 0);
		
		gl.glMatrixMode(GL.GL_TEXTURE);
		gl.glPushMatrix();
		gl.glScalef(tl.getInvTileSize().getXAsFloat(), tl.getInvTileSize().getYAsFloat(), 1.0f);
		gl.glBegin(GL.GL_QUADS);
		gl.glTexCoord2f(0.0f, 0.0f);
		gl.glVertex2f(0.0f, 0.0f);
		gl.glTexCoord2f(1.0f, 0.0f);
		gl.glVertex2f(areaSize.getXAsFloat(), 0.0f);
		gl.glTexCoord2f(1.0f, 1.0f);
		gl.glVertex2f(areaSize.getXAsFloat(), areaSize.getYAsFloat());
		gl.glTexCoord2f(0.0f, 1.0f);
		gl.glVertex2f(0.0f, areaSize.getYAsFloat());
		gl.glEnd();
		gl.glPopMatrix();
		gl.glMatrixMode(GL.GL_MODELVIEW);

		gl.glDisable(GL.GL_TEXTURE_2D);
	}
}
