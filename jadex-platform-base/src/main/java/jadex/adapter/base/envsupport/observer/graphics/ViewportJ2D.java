package jadex.adapter.base.envsupport.observer.graphics;

import jadex.adapter.base.envsupport.math.IVector2;
import jadex.adapter.base.envsupport.math.Vector2Double;
import jadex.adapter.base.envsupport.observer.graphics.drawable.DrawableCombiner;
import jadex.adapter.base.envsupport.observer.graphics.layer.ILayer;
import jadex.bridge.ILibraryService;

import java.awt.Canvas;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import javax.imageio.ImageIO;


/**
 * This class manages the GUI and all user interaction.
 */
public class ViewportJ2D extends AbstractViewport implements ComponentListener
{
	private Map			imageCache_;

	/** Action that renders the frame. */
	private Runnable	renderFrameAction_;
	
	/** The current draw context */
	private Graphics2D context_;
	
	/** Cache for text-images */
	protected Map 		textCache_;

	/**
	 * Creates a new Viewport.
	 * 
	 * @param libService the library service
	 */
	public ViewportJ2D(ILibraryService libService)
	{
		super();

		libService_ = libService;
		imageCache_ = Collections.synchronizedMap(new HashMap());
		textCache_ = Collections.synchronizedMap(new ImageLRUCache(100));
		
		canvas_ = new ViewportCanvas();
		canvas_.addComponentListener(this);

		canvas_.addMouseListener(new MouseController());

		renderFrameAction_ = new Runnable()
		{
			public void run()
			{
				canvas_.repaint();
			};
		};
	}

	/**
	 * Returns an image for texturing
	 * 
	 * @param path resource path of the image
	 */
	public BufferedImage getImage(String path)
	{
		BufferedImage image = (BufferedImage)imageCache_.get(path);

		if(image == null)
		{
			image = loadImage(path);
			imageCache_.put(path, image);
		}

		return image;
	}

	public void refresh()
	{
		EventQueue.invokeLater(renderFrameAction_);
	}
	
	public Graphics2D getContext()
	{
		return context_;
	}

	/**
	 * Sets up the image transform.
	 * 
	 * @param sizeX image x-size
	 * @param sizeY image y-size
	 * @return the transform
	 */
	public AffineTransform getImageTransform(int sizeX, int sizeY)
	{
		AffineTransform imageTransform = new AffineTransform();
		imageTransform.translate(1.0 * inversionFlag_.getXAsInteger(),
				1.0 * inversionFlag_.getYAsInteger());
		imageTransform.scale(-((inversionFlag_.getXAsInteger() << 1) - 1)
				/ (double)sizeX, -((inversionFlag_.getYAsInteger() << 1) - 1)
				/ (double)sizeY);
		return imageTransform;
	}
	
	/**
	 *  Returns the image of a text
	 *  
	 *  @param info information on the text
	 */
	public BufferedImage getTextImage(TextInfo info)
	{
		synchronized (textCache_)
		{
			BufferedImage image = (BufferedImage) textCache_.get(info);
			if (image == null)
			{
				image = convertTextToImage(info.getFont(), info.getColor(), info.getText());
				textCache_.put(info, image);
			}
			
			return image;
		}
	}

	// Component events
	public void componentHidden(ComponentEvent e)
	{
	}

	public void componentMoved(ComponentEvent e)
	{
	}

	public void componentResized(ComponentEvent e)
	{
		setSize(size_);
	}

	public void componentShown(ComponentEvent e)
	{
	}

	/**
	 * Loads an image.
	 * 
	 * @param path resource path of the image
	 */
	private BufferedImage loadImage(String path)
	{
		ClassLoader cl = libService_.getClassLoader();

		BufferedImage image = null;
		try
		{
			image = ImageIO.read(cl.getResource(path));
			AffineTransform tf = AffineTransform.getScaleInstance(1, -1);
			tf.translate(0, -image.getHeight());
			AffineTransformOp op = new AffineTransformOp(tf,
					AffineTransformOp.TYPE_NEAREST_NEIGHBOR);
			image = op.filter(image, null);
		}
		catch(Exception e)
		{
		}

		return image;
	}

	private class ViewportCanvas extends Canvas
	{
		private BufferedImage		backBuffer_;

		private Rectangle.Double	clearRectangle_;

		private GeneralPath			scissorPolygon_;

		public ViewportCanvas()
		{
			backBuffer_ = new BufferedImage(1, 1,
					BufferedImage.TYPE_4BYTE_ABGR_PRE);
			scissorPolygon_ = new GeneralPath();
			setupScissorPolygon();
			clearRectangle_ = new Rectangle.Double();
			clearRectangle_.x = 0.0;
			clearRectangle_.y = 0.0;
			clearRectangle_.width = size_.getXAsDouble();
			clearRectangle_.height = size_.getYAsDouble();
		}

		public Dimension minimumSize()
		{
			return new Dimension(1, 1);
		}

		public Dimension getMinimumSize()
		{
			return new Dimension(1, 1);
		}

		public void paint(Graphics gfx)
		{
			try
			{
				if((backBuffer_.getWidth() != getWidth())
						|| (backBuffer_.getHeight() != getHeight()))
				{
					backBuffer_ = new BufferedImage(getWidth(), getHeight(),
							BufferedImage.TYPE_4BYTE_ABGR_PRE);
					setupScissorPolygon();
				}

				Graphics2D g = (Graphics2D)backBuffer_.getGraphics();
				g.setColor(java.awt.Color.BLACK);
				g.fillRect(0, 0, getWidth(), getHeight());
				setupTransform(g);
				context_ = g;

				synchronized(preLayers_)
				{
					for (int i = 0; i < preLayers_.length; ++i)
					{
						ILayer l = preLayers_[i];
						if (!drawObjects_.contains(l))
						{
							l.init(ViewportJ2D.this, g);
						}
						l.draw(size_, ViewportJ2D.this, g);
					}
				}

				synchronized(objectList_)
				{
					synchronized(objectLayers_)
					{
						objectLayers_.clear();
						for (Iterator it = objectList_.iterator(); it.hasNext(); )
						{
							Object[] o = (Object[]) it.next();
							DrawableCombiner d = (DrawableCombiner) o[1];
							if (!drawObjects_.contains(d))
							{
								d.init(ViewportJ2D.this);
								drawObjects_.add(d);
							}
							objectLayers_.addAll(d.getLayers());
						}
						
						AffineTransform tf = g.getTransform();
						g.translate(objShiftX_, objShiftY_);
						for(Iterator it = objectLayers_.iterator(); it
								.hasNext();)
						{
							Integer layer = (Integer)it.next();
							Iterator it2 = objectList_.iterator();
							while(it2.hasNext())
							{
								Object[] o = (Object[])it2.next();
								Object obj = o[0];
								DrawableCombiner d = (DrawableCombiner)o[1];
								d.draw(obj, layer, ViewportJ2D.this);
							}
						}
						g.setTransform(tf);
					}
				}

				synchronized(postLayers_)
				{
					for (int i = 0; i < postLayers_.length; ++i)
					{
						ILayer l = postLayers_[i];
						if (!drawObjects_.contains(l))
						{
							l.init(ViewportJ2D.this, g);
						}
						l.draw(size_, ViewportJ2D.this, g);
					}
				}
				
				context_ = null;
				
				// glScissor replacement
				g.setColor(java.awt.Color.BLACK);
				g.fill(scissorPolygon_);

				g.dispose();

				gfx.drawImage(backBuffer_, 0, 0, null);
				gfx.dispose();
			}
			catch(IllegalStateException e)
			{
			}
		}

		public void update(Graphics g)
		{
			paint(g);
		}

		private void setupTransform(Graphics2D g)
		{
			g.translate(
					backBuffer_.getWidth() * inversionFlag_.getXAsInteger(),
					backBuffer_.getHeight()
							* (inversionFlag_.getYAsInteger() ^ 1));
			g.scale((backBuffer_.getWidth() / paddedSize_.getXAsDouble())
					* -((inversionFlag_.getXAsInteger() << 1) - 1),
					(backBuffer_.getHeight() / paddedSize_.getYAsDouble())
							* ((inversionFlag_.getYAsInteger() << 1) - 1));
			g.translate(-posX_, -posY_);
		}

		private void setupScissorPolygon()
		{
			float pixShiftX = (paddedSize_.getXAsFloat() / backBuffer_.getWidth());
			float pixShiftY = (paddedSize_.getYAsFloat() / backBuffer_.getHeight());
			scissorPolygon_.reset();
			scissorPolygon_.moveTo(posX_, posY_);
			scissorPolygon_.lineTo(paddedSize_.getXAsFloat(), posY_);
			scissorPolygon_.lineTo(paddedSize_.getXAsFloat(), paddedSize_
					.getYAsFloat());
			scissorPolygon_.lineTo(posX_, paddedSize_.getYAsFloat());
			scissorPolygon_.lineTo(posX_, size_.getYAsFloat() + pixShiftY);
			scissorPolygon_.lineTo(size_.getXAsFloat() + pixShiftX, size_.getYAsFloat() + pixShiftY);
			scissorPolygon_.lineTo(size_.getXAsFloat() + pixShiftX, 0.0f);
			scissorPolygon_.lineTo(0.0f, 0.0f);
			scissorPolygon_.lineTo(0.0f, size_.getYAsFloat() + pixShiftY);
			scissorPolygon_.lineTo(posX_, size_.getYAsFloat() + pixShiftY);
			scissorPolygon_.closePath();
		}
	}
	
	private class ImageLRUCache extends LinkedHashMap
	{
		private int maxSize;
		
		public ImageLRUCache(int maxSize)
		{
			super(16, 0.75f, true);
			this.maxSize = maxSize;
		}
		
		protected boolean removeEldestEntry(Map.Entry eldest)
		{
			return (size() > maxSize);
		}
	}
}
