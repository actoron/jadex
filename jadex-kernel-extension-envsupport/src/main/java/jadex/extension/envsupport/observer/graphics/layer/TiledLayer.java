package jadex.extension.envsupport.observer.graphics.layer;

import java.awt.Color;

import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;


/**
 * A layer consisting of image tiles.
 */
public class TiledLayer extends Layer
{
	/** Texture path. */
	protected String			texturePath_;

	/** Texture ID for OpenGL operations. */
	//private int					texture_;

	/** Image for Java2D operations. */
	//private BufferedImage		image_;

	/** AffineTransform from image space to user space for Java2D. */
	//private AffineTransform		imageToUser_;

	/** Size of the tiles. */
	private IVector2			tileSize_;
	
	/** Inverted size of the tiles. */
	private IVector2			invTileSize_;
	
	/** Modulation color or color binding */
	//private Object				modColor_;
	
	/** Composite for modulating in Java2D */
//	private Composite modComposite_;
	
	/** The current property object */
//	private Object propObject;

	/**
	 * Creates a new TiledLayer.
	 */
	public TiledLayer()
	{
		this(new Vector2Double(1.0), Color.WHITE, "");
	}

	/**
	 * Creates a new TiledLayer.
	 * 
	 * @param tileSize size of an individual tile
	 * @param color the modulation color
	 * @param texturePath resource path of the texture
	 */
	public TiledLayer(IVector2 tileSize, Object color, String texturePath)
	{
		super(Layer.LAYER_TYPE_TILED, color);
		this.tileSize_ = tileSize.copy();
		//this.modColor_ = color==null? Color.WHITE: color;
		this.invTileSize_ = (new Vector2Double(1.0)).divide(tileSize_);
		this.texturePath_ = texturePath;
		//texture_ = 0;
	}
	
	/**
	 *  The tile size.
	 */
	public IVector2 getTileSize()
	{
		return tileSize_;
	}
	
	/**
	 *  Get the inverted tile size.
	 */
	public IVector2 getInvTileSize()
	{
		return invTileSize_;
	}
	
	/**
	 *  Get the texture path.
	 *  
	 *  @return The texture path.
	 */
	public String getTexturePath()
	{
		return texturePath_;
	}
	
	/**
	 *  Get the hash code.
	 */
	public int hashCode()
	{
		return tileSize_.hashCode() * 31 + texturePath_.hashCode();
	}
	
	/**
	 *  Compares the layers.
	 */
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
