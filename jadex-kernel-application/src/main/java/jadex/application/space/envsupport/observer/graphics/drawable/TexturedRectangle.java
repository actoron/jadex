package jadex.application.space.envsupport.observer.graphics.drawable;

import jadex.application.space.envsupport.math.IVector2;
import jadex.application.space.envsupport.observer.graphics.ModulateComposite;
import jadex.application.space.envsupport.observer.graphics.ViewportJ2D;
import jadex.application.space.envsupport.observer.graphics.ViewportJOGL;
import jadex.javaparser.IParsedExpression;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;

import javax.media.opengl.GL;

/**
 * 
 */
public class TexturedRectangle extends ColoredPrimitive
{
	private static final long	serialVersionUID	= 0L;

	/** Texture path. */
	protected String			texturePath_;

	/** Texture ID for OpenGL operations. */
	private int					texture_;

	/** Image for Java2D operations. */
	private BufferedImage		image_;
	
	/** Composite for modulating in Java2D */
	private Composite modComposite_;
	
	/** Current color value */
	private Color currentColor_;

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
	 * @param absFlags flags for setting position, size and rotation as absolutes
	 * @param c modulation color or binding
	 * @param texturePath resource path of the texture
	 */
	public TexturedRectangle(Object position, Object rotation, Object size, int absFlags, Object c, String texturePath, IParsedExpression drawcondition)
	{
		super(position, rotation, size, absFlags, c, drawcondition);
		
		texturePath_ = texturePath;
		texture_ = 0;
		image_ = null;
	}

	public void init(ViewportJ2D vp)
	{
		image_ = vp.getImage(texturePath_);
		modComposite_ = new ModulateComposite()
			{
				protected Color getColor()
				{
					return currentColor_;
				}
			};
	}

	public void init(ViewportJOGL vp)
	{
		texture_ = vp.getClampedTexture(vp.getContext(), texturePath_);
	}

	public synchronized void doDraw(DrawableCombiner dc, Object obj, ViewportJ2D vp)
	{
		Graphics2D g = vp.getContext();
		
		IVector2 size = (IVector2)dc.getBoundValue(obj, getSize(), vp);
		
		BufferedImage image = image_;
		
		g.translate(-size.getXAsDouble() / 2.0, -size.getYAsDouble() / 2.0);
		if (!setupMatrix(dc, obj, g, vp))
			return;
		
		currentColor_ = (Color) dc.getBoundValue(obj, color_, vp);
		
		if (!Color.WHITE.equals(currentColor_))
		{
			Composite c = g.getComposite();
			g.setComposite(modComposite_);
			g.drawImage(image, vp.getImageTransform(image.getWidth(), image
					.getHeight()), null);
			g.setComposite(c);
		}
		else
		{
			g.drawImage(image, vp.getImageTransform(image.getWidth(), image
					.getHeight()), null);
		}
	}

	public synchronized void doDraw(DrawableCombiner dc, Object obj, ViewportJOGL vp)
	{
		GL gl = vp.getContext();
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, texture_);
		
		currentColor_ = (Color) dc.getBoundValue(obj, color_, vp);
		
		gl.glColor4fv(currentColor_.getComponents(null), 0);
		
		if(setupMatrix(dc, obj, gl, vp));
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
