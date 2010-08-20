package jadex.application.space.envsupport.observer.graphics.opengl;

import jadex.application.space.envsupport.observer.graphics.drawable.DrawableCombiner;
import jadex.application.space.envsupport.observer.graphics.drawable.Primitive;
import jadex.application.space.envsupport.observer.graphics.drawable.TexturedRectangle;

import java.awt.Color;

import javax.media.opengl.GL;

public class TexturedRectangleGLRenderer extends AbstractGLRenderer
{

	/**
	 * Draws the primitive.
	 * @param dc the DrawableCombiner drawing the object
	 * @param primitive the primitive being drawn
	 * @param obj the object being drawn
	 * @param vp the viewport
	 */
	public void draw(DrawableCombiner dc, Primitive primitive, Object obj, ViewportJOGL vp)
	{
		int texture = 0;
		try
		{
			texture = ((Integer) primitive.getRenderInfo(0)).intValue();
		}
		catch (Exception e)
		{
			texture = vp.getTexture(vp.getContext(), ((TexturedRectangle) primitive).getTexturePath());
			primitive.setRenderInfo(0, new Integer(texture));
		}
		
		GL gl = vp.getContext();
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, texture);
		
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
		
		Color currentColor = (Color) dc.getBoundValue(obj, primitive.getColor(), vp);
		
		gl.glColor4fv(currentColor.getComponents(null), 0);
		
		if(setupMatrix(dc, primitive, obj, gl, vp));
		{
			gl.glBegin(GL.GL_QUADS);
			gl.glTexCoord2f(0.0f, 0.0f);
			gl.glVertex2f(-0.5f, -0.5f);
			gl.glTexCoord2f(1.0f, 0.0f);
			gl.glVertex2f(0.5f, -0.5f);
			gl.glTexCoord2f(1.0f, 1.0f);
			gl.glVertex2f(0.5f, 0.5f);
			gl.glTexCoord2f(0.0f, 1.0f);
			gl.glVertex2f(-0.5f, 0.5f);
			gl.glEnd();

			gl.glDisable(GL.GL_TEXTURE_2D);
		}
	}

}
