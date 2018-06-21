package jadex.extension.envsupport.observer.graphics.opengl;

import java.awt.Color;

import javax.media.opengl.GL;

import jadex.extension.envsupport.observer.graphics.drawable.DrawableCombiner;
import jadex.extension.envsupport.observer.graphics.drawable.Primitive;
import jadex.extension.envsupport.observer.graphics.drawable.TexturedRectangle;

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
		GL gl = vp.getContext();
		int texture = 0;
		int dList = 0;
		try
		{
			texture = ((Integer) primitive.getRenderInfo(0)).intValue();
			dList = ((Integer) primitive.getRenderInfo(1)).intValue();
		}
		catch (Exception e)
		{
			texture = vp.getTexture(vp.getContext(), ((TexturedRectangle) primitive).getTexturePath());
			primitive.setRenderInfo(0, Integer.valueOf(texture));
			
			String listName = getClass().getName();
			Integer list = vp.getDisplayList(listName);
			if(list == null)
			{
				dList = gl.glGenLists(1);
				gl.glNewList(dList, GL.GL_COMPILE);

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
				gl.glEndList();

				list = Integer.valueOf(dList);
				vp.setDisplayList(listName, list);
			}
			dList = list.intValue();
			primitive.setRenderInfo(1, list);
		}
		
		
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, texture);
		
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_CLAMP_TO_EDGE);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_CLAMP_TO_EDGE);
		
		Color currentColor = (Color) dc.getBoundValue(obj, primitive.getColor(), vp);
		if(currentColor==null)
			currentColor=Color.WHITE;
		
		gl.glColor4fv(currentColor.getComponents(null), 0);
		
		if(setupMatrix(dc, primitive, obj, gl, vp))
			gl.glCallList(dList);
		gl.glDisable(GL.GL_TEXTURE_2D);
	}

}
