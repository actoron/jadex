package jadex.application.space.envsupport.observer.graphics.layer;

import jadex.application.space.envsupport.math.IVector2;
import jadex.application.space.envsupport.math.Vector2Double;
import jadex.application.space.envsupport.observer.graphics.ModulateComposite;
import jadex.application.space.envsupport.observer.graphics.ViewportJ2D;
import jadex.application.space.envsupport.observer.graphics.ViewportJOGL;
import jadex.application.space.envsupport.observer.gui.SObjectInspector;
import jadex.application.space.envsupport.observer.perspective.IPerspective;

import java.awt.Color;
import java.awt.Composite;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

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
	
	/** Inverted size of the tiles. */
	private IVector2			invTileSize_;
	
	/** Modulation color or color binding */
	private Object				modColor_;
	
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
		this.tileSize_ = tileSize.copy();
		this.modColor_ = color==null? Color.WHITE: color;
		this.invTileSize_ = (new Vector2Double(1.0)).divide(tileSize_);
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
		imageToUser_.scale(1.0 / image_.getWidth(), 1.0 / image_.getHeight());
//		modComposite_ = new ModulateComposite()
//		{
//			protected Color getColor()
//			{
//				return modColor_ instanceof Color? (Color)modColor_: (Color)SObjectInspector.getProperty(propObject, (String)modColor_, "$perspective");
////					return (Color) SObjectInspector.getPropertyAsClass(propObject, modColor_, Color.class);
//			}
//		};
	}

	/**
	 * Initializes the layer for an OpenGL viewport
	 * 
	 * @param vp the viewport
	 * @param gl OpenGL context
	 */
	public void init(ViewportJOGL vp, GL gl)
	{
		//texture_ = vp.getRepeatingTexture(gl, texturePath_);
		texture_ = vp.getTexture(gl, texturePath_);
	}

	/**
	 * Draws the layer to a Java2D viewport
	 * 
	 * @param layerObject object with properties for the layer
	 * @param areaSize size of the area this layer covers
	 * @param vp the viewport
	 * @param g Graphics2D context
	 */
	public void draw(final IPerspective perspective, IVector2 areaSize, final ViewportJ2D vp, Graphics2D g)
	{
		Composite c = g.getComposite();
		if (!Color.WHITE.equals(modColor_))
		{
			g.setComposite(new ModulateComposite()
			{
				protected Color getColor()
				{
					return modColor_ instanceof Color? (Color)modColor_: (Color)SObjectInspector.getProperty(perspective, (String)modColor_, "$perspective", 
						vp.getPerspective().getObserverCenter().getSpace().getFetcher());
//						return (Color) SObjectInspector.getPropertyAsClass(propObject, modColor_, Color.class);
				}
			});
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
				g.drawImage(image_, vp.getImageTransform(image_.getWidth(), image_.getHeight()), null);
				g.setTransform(transform);
			}
		}
		
		g.setComposite(c);
	}

	/**
	 * Draws the layer to an OpenGL viewport
	 * 
	 * @param layerObject object with properties for the layer
	 * @param areaSize size of the area this layer covers
	 * @param vp the viewport
	 * @param gl OpenGL context
	 */
	public void draw(IPerspective perspective, IVector2 areaSize, ViewportJOGL vp, GL gl)
	{
		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, texture_);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, GL.GL_REPEAT);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, GL.GL_REPEAT);

		Color c = modColor_ instanceof Color? (Color)modColor_: (Color)SObjectInspector.getProperty(perspective, (String)modColor_, "$perspective", 
			vp.getPerspective().getObserverCenter().getSpace().getFetcher());
		gl.glColor4fv(c.getComponents(null), 0);
//		gl.glColor4fv(((Color) SObjectInspector.getPropertyAsClass(layerObject, modColor_, Color.class)).getComponents(null), 0);
		
		gl.glMatrixMode(GL.GL_TEXTURE);
		gl.glPushMatrix();
		gl.glScalef(invTileSize_.getXAsFloat(), invTileSize_.getYAsFloat(), 1.0f);
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
