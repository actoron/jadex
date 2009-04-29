package jadex.adapter.base.envsupport.observer.graphics.drawable;

import jadex.adapter.base.envsupport.math.IVector1;
import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Double;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJ2D;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJOGL;
import jadex.adapter.base.envsupport.observer.gui.SObjectInspector;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.media.opengl.GL;


public class TexturedRectangle extends RotatingPrimitive
{
	static private final long	serialVersionUID	= 0L;

	/** Texture path. */
	protected String			texturePath_;

	/** Texture ID for OpenGL operations. */
	private int					texture_;

	/** Image for Java2D operations. */
	private BufferedImage		image_;

	/** AffineTransform from image space to user space for Java2D. */
	private AffineTransform		imageToUser_;

	/**
	 * Creates default TexturedRectangle.
	 * 
	 * @param texturePath resource path of the texture
	 */
	public TexturedRectangle(String texturePath)
	{
		super();
		texturePath_ = texturePath;
		texture_ = 0;
		image_ = null;
	}

	/**
	 * Creates a new TexturedRectangle drawable.
	 * 
	 * @param position position or position-binding
	 * @param rotation rotation or rotation-binding
	 * @param size size or size-binding
	 * @param texturePath resource path of the texture
	 */
	public TexturedRectangle(Object position, Object rotation, Object size, String texturePath, DrawCondition drawcondition)
	{
		super(position, rotation, size, drawcondition);
		texturePath_ = texturePath;
		texture_ = 0;
		image_ = null;
	}

	public void init(ViewportJ2D vp)
	{
		image_ = vp.getImage(texturePath_);
		imageToUser_ = new AffineTransform();
		imageToUser_.scale(1.0 / image_.getWidth(), 1.0 / image_.getHeight());
	}

	public void init(ViewportJOGL vp)
	{
		texture_ = vp.getClampedTexture(vp.getContext(), texturePath_);
	}

	public synchronized void doDraw(Object obj, ViewportJ2D vp)
	{
		Graphics2D g = vp.getContext();
		AffineTransform transform = g.getTransform();
		
		IVector2 size = SObjectInspector.getVector2(obj, this.size);
		IVector1 rotation = SObjectInspector.getVector1asDirection(obj, this.rotation);
		IVector2 position = SObjectInspector.getVector2(obj, this.position);
		if ((position == null) || (size == null) || (rotation == null))
		{
			return;
		}
		
		g.translate(position.getXAsDouble() - (size.getXAsDouble() / 2),
					position.getYAsDouble() - (size.getYAsDouble() / 2));
		g.scale(size.getXAsDouble(), size.getYAsDouble());
		g.rotate(rotation.getAsDouble());
		
		g.drawImage(image_, vp.getImageTransform(image_.getWidth(), image_
				.getHeight()), null);
		g.setTransform(transform);
	}

	public synchronized void doDraw(Object obj, ViewportJOGL vp)
	{
		GL gl = vp.getContext();
		gl.glPushMatrix();
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, texture_);
		
		if (setupMatrix(obj, gl));
		{
			gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
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
		gl.glPopMatrix();
	}
}
