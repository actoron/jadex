package jadex.adapter.base.envsupport.observer.graphics.drawable;

import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJ2D;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJOGL;
import jadex.adapter.base.envsupport.observer.gui.SObjectInspector;
import jadex.javaparser.IParsedExpression;

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
	 * @param xrotation xrotation or rotation-binding
	 * @param yrotation yrotation or rotation-binding
	 * @param zrotation zrotation or rotation-binding
	 * @param size size or size-binding
	 * @param texturePath resource path of the texture
	 */
	public TexturedRectangle(Object position, Object xrotation, Object yrotation, Object zrotation, Object size, String texturePath, IParsedExpression drawcondition)
	{
		super(position, xrotation, yrotation, zrotation, size, drawcondition);
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
		
		if (!setupMatrix(obj, g))
			return;
		g.translate(-size.getXAsDouble() / 2.0, -size.getYAsDouble() / 2.0);
		
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
