package jadex.adapter.base.envsupport.observer.graphics.drawable;

import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJ2D;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJOGL;
import jadex.adapter.base.envsupport.observer.gui.SObjectInspector;
import jadex.javaparser.IParsedExpression;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;

import javax.media.opengl.GL;

import sun.security.action.GetBooleanAction;


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
		modComposite_ = new ModulateComposite();
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
	}

	public void init(ViewportJOGL vp)
	{
		texture_ = vp.getClampedTexture(vp.getContext(), texturePath_);
	}

	public synchronized void doDraw(DrawableCombiner dc, Object obj, ViewportJ2D vp)
	{
		Graphics2D g = vp.getContext();
		
		IVector2 size = (IVector2)dc.getBoundValue(obj, getSize());
		
		BufferedImage image = image_;
		
		g.translate(-size.getXAsDouble() / 2.0, -size.getYAsDouble() / 2.0);
		if (!setupMatrix(dc, obj, g))
			return;
		
		currentColor_ = (Color) dc.getBoundValue(obj, color_);
		
		if (!Color.WHITE.equals(currentColor_))
		{
			Composite c = g.getComposite();
			g.setComposite(new ModulateComposite());
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
		
		currentColor_ = (Color) dc.getBoundValue(obj, color_);
		
		gl.glColor4fv(currentColor_.getComponents(null), 0);
		
		if(setupMatrix(dc, obj, gl));
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
	
	private class ModulateComposite implements Composite
	{
		public CompositeContext createContext(ColorModel srcColorModel,
				ColorModel dstColorModel, RenderingHints hints)
		{
			return new ModulateContext(srcColorModel, dstColorModel);
		}
		
		private class ModulateContext implements CompositeContext
		{
			private ColorModel srcColorModel;
			private ColorModel dstColorModel;
			
			public ModulateContext(ColorModel srcColorModel, ColorModel dstColorModel)
			{
				this.srcColorModel = srcColorModel;
				this.dstColorModel = dstColorModel;
			}
			
			public void compose(Raster src, Raster dstIn, WritableRaster dstOut)
			{
				float[] clrPx = currentColor_.getComponents(null);
				
				int maxX = Math.min(src.getWidth(), dstIn.getWidth());
				int maxY = Math.min(src.getHeight(), dstIn.getHeight());
				float[] tmpPx = new float[4];
				for (int y = 0; y < maxY; ++y)
				{
					for (int x = 0; x < maxX; ++x)
					{
						Object inPixel = src.getDataElements(x, y, null);
						tmpPx = srcColorModel.getNormalizedComponents(inPixel, tmpPx, 0);
						
						tmpPx[0] *= clrPx[0];
						tmpPx[1] *= clrPx[1];
						tmpPx[2] *= clrPx[2];
						float invsa = 1.0f - tmpPx[3];
						
						float[] dstPx = dstColorModel.getNormalizedComponents(dstIn.getDataElements(x, y, null), null, 0);
						tmpPx[0] = (tmpPx[0] * tmpPx[3]) + (dstPx[0] * invsa);
						tmpPx[1] = (tmpPx[1] * tmpPx[3]) + (dstPx[1] * invsa);
						tmpPx[2] = (tmpPx[2] * tmpPx[3]) + (dstPx[2] * invsa);
						tmpPx[3] = 1.0f;
						
						Object outPixel = dstColorModel.getDataElements(tmpPx, 0, null);
						dstOut.setDataElements(x, y, outPixel);
					}
				}
			}
			
			public void dispose()
			{
				srcColorModel = null;
				dstColorModel = null;
			}
		}
	}
}
