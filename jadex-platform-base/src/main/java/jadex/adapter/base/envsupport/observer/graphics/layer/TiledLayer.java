package jadex.adapter.base.envsupport.observer.graphics.layer;

import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Double;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJ2D;
import jadex.adapter.base.envsupport.observer.graphics.ViewportJOGL;

import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;

import javax.media.opengl.GL;


/**
 * A layer consisting of image tiles.
 */
public class TiledLayer implements ILayer
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

	/** Size of the tiles. */
	private IVector2			tileSize_;

	/**
	 * Creates a new TiledLayer.
	 */
	public TiledLayer()
	{
		this(new Vector2Double(1.0), "");
	}

	/**
	 * Creates a new TiledLayer.
	 * 
	 * @param tileSize size of an individual tile
	 * @param texturePath resource path of the texture
	 */
	public TiledLayer(IVector2 tileSize, String texturePath)
	{
		this.tileSize_ = tileSize.copy();
		this.texturePath_ = texturePath;
		texture_ = 0;
	}
	
	/**
	 * Initializes the layer for a Java2D viewport
	 * 
	 * @param vp the viewport
	 * @param g Graphics2D context
	 */
	public void init(ViewportJ2D vp, Graphics2D g)
	{
		image_ = vp.getImage(texturePath_);
		imageToUser_ = new AffineTransform();
		imageToUser_.scale(1.0 / image_.getWidth(), 1.0 / image_
				.getHeight());
	}

	/**
	 * Initializes the layer for an OpenGL viewport
	 * 
	 * @param vp the viewport
	 * @param gl OpenGL context
	 */
	public void init(ViewportJOGL vp, GL gl)
	{
		texture_ = vp.getRepeatingTexture(gl, texturePath_);
	}

	public void draw(IVector2 areaSize, ViewportJ2D vp, Graphics2D g)
	{
		for(double x = 0.0; x < areaSize.getXAsDouble(); x = x
				+ tileSize_.getXAsDouble())
		{
			for(double y = 0.0; y < areaSize.getYAsDouble(); y = y
					+ tileSize_.getYAsDouble())
			{
				AffineTransform transform = g.getTransform();
				g.translate(x, y);
				g.scale(tileSize_.getXAsDouble(), tileSize_.getYAsDouble());
				g.drawImage(image_, imageToUser_, null);
				g.setTransform(transform);
			}
		}
	}

	public synchronized void draw(IVector2 areaSize, ViewportJOGL vp, GL gl)
	{
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, texture_);

		//float pTilesX = areaSize.getXAsFloat() / tileSize_.getXAsFloat();
		//float pTilesY = areaSize.getYAsFloat() / tileSize_.getYAsFloat();

		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
		
		gl.glMatrixMode(GL.GL_TEXTURE);
		gl.glPushMatrix();
		gl.glScalef(tileSize_.getXAsFloat(), tileSize_.getYAsFloat(), 1.0f);
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
		

		/*gl.glBegin(GL.GL_QUADS);
		for(float x = 0.0f; x < areaSize.getXAsFloat(); x = x
				+ tileSize_.getXAsFloat())
		{
			for(float y = 0.0f; y < areaSize.getYAsFloat(); y = y
					+ tileSize_.getYAsFloat())
			{
				gl.glTexCoord2f(0.0f, 0.0f);
				gl.glVertex2f(x, y);
				gl.glTexCoord2f(1.0f, 0.0f);
				gl.glVertex2f(x + tileSize_.getXAsFloat(), y);
				gl.glTexCoord2f(1.0f, 1.0f);
				gl.glVertex2f(x + tileSize_.getXAsFloat(), y
						+ tileSize_.getYAsFloat());
				gl.glTexCoord2f(0.0f, 1.0f);
				gl.glVertex2f(x, y + tileSize_.getYAsFloat());
			}
		}
		gl.glEnd();*/

		gl.glDisable(GL.GL_TEXTURE_2D);
	}

	public ILayer copy()
	{
		return new TiledLayer(tileSize_, texturePath_);
	}

	public boolean equals(Object obj)
	{
		if(obj instanceof TiledLayer)
		{
			TiledLayer other = (TiledLayer)obj;
			return ((tileSize_.equals(other.tileSize_)) && (texturePath_
					.equals(other.texturePath_)));
		}
		return false;
	}
}
