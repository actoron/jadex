package jadex.bdi.planlib.simsupport.common.graphics.layer;

import jadex.bdi.planlib.simsupport.common.graphics.Texture2D;
import jadex.bdi.planlib.simsupport.common.graphics.ViewportJ2D;
import jadex.bdi.planlib.simsupport.common.graphics.ViewportJOGL;
import jadex.bdi.planlib.simsupport.common.math.IVector2;
import jadex.bdi.planlib.simsupport.common.math.Vector2Double;

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
	private Texture2D			texture_;

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
		texture_ = null;
	}

	public void draw(IVector2 areaSize, ViewportJ2D vp, Graphics2D g)
	{
		if(image_ == null)
		{
			image_ = vp.getImage(texturePath_);
			imageToUser_ = new AffineTransform();
			imageToUser_.scale(1.0 / image_.getWidth(), 1.0 / image_
					.getHeight());
		}

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
		if(texture_ == null)
		{
			texture_ = vp.getRepeatingTexture(gl, texturePath_);
		}

		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, texture_.getTexId());

		float pTilesX = areaSize.getXAsFloat() / tileSize_.getXAsFloat();
		float pTilesY = areaSize.getYAsFloat() / tileSize_.getYAsFloat();

		gl.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);

		gl.glBegin(GL.GL_QUADS);
		for(float x = 0.0f; x < areaSize.getXAsFloat(); x = x
				+ tileSize_.getXAsFloat())
		{
			for(float y = 0.0f; y < areaSize.getYAsFloat(); y = y
					+ tileSize_.getYAsFloat())
			{
				gl.glTexCoord2f(0.0f, 0.0f);
				gl.glVertex2f(x, y);
				gl.glTexCoord2f(texture_.getMaxX(), 0.0f);
				gl.glVertex2f(x + tileSize_.getXAsFloat(), y);
				gl.glTexCoord2f(texture_.getMaxX(), texture_.getMaxY());
				gl.glVertex2f(x + tileSize_.getXAsFloat(), y
						+ tileSize_.getYAsFloat());
				gl.glTexCoord2f(0.0f, texture_.getMaxY());
				gl.glVertex2f(x, y + tileSize_.getYAsFloat());
			}
		}
		gl.glEnd();

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
