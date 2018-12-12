package jadex.extension.envsupport.observer.graphics.java2d;

import java.awt.Canvas;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import javax.imageio.ImageIO;

import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.observer.graphics.AbstractViewport;
import jadex.extension.envsupport.observer.graphics.drawable.DrawableCombiner;
import jadex.extension.envsupport.observer.graphics.drawable.Primitive;
import jadex.extension.envsupport.observer.graphics.layer.Layer;
import jadex.extension.envsupport.observer.perspective.IPerspective;


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
	
	/** The class loader. */
	private ClassLoader classloader;
	
	/** The default transform */
	private AffineTransform defaultTransform_;
	
	/** Flag to indicate that viewport is disposed. */
	private boolean disposed = false;
	
	/** The renderers. */
	private static final IJ2DRenderer[] RENDERERS = new IJ2DRenderer[6];
	static
	{
		RENDERERS[0] = new EllipseJ2DRenderer();
		RENDERERS[1] = new RectangleJ2DRenderer();
		RENDERERS[2] = new RegularPolygonJ2DRenderer();
		RENDERERS[3] = new TextJ2DRenderer();
		RENDERERS[4] = new TexturedRectangleJ2DRenderer();
		RENDERERS[5] = new TriangleJ2DRenderer();
	}
	
	/** The layer renderers. */
	private static final ILayerJ2DRenderer[] LAYER_RENDERERS = new ILayerJ2DRenderer[3];
	static
	{
		LAYER_RENDERERS[0] = new ColorLayerJ2DRenderer();
		LAYER_RENDERERS[1] = new GridLayerJ2DRenderer();
		LAYER_RENDERERS[2] = new TiledLayerJ2DRenderer();
	}

	/**
	 * Creates a new Viewport.
	 * 
	 * @param layerObject object holding properties for pre/postlayers
	 * @param libService the library service
	 */
	public ViewportJ2D(IPerspective persp, ClassLoader classloader)
	{
		super(persp);

		this.classloader = classloader;
		imageCache_ = Collections.synchronizedMap(new HashMap());
		
		canvas_ = new ViewportCanvas();
		canvas_.addComponentListener(this);
		
		MouseController mc = new MouseController();
		canvas_.addMouseListener(mc);
		canvas_.addMouseWheelListener(mc);
		canvas_.addMouseMotionListener(mc);

		renderFrameAction_ = new Runnable()
		{
			public void run()
			{
				if (!disposed)
				{
					rendering	= false;
					canvas_.repaint();
				}
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
		if(!rendering)
		{
			rendering	= true;
			EventQueue.invokeLater(renderFrameAction_);
		}
	}
	
	public Graphics2D getContext()
	{
		return context_;
	}
	
	/**
	 * Returns the default transform.
	 * @return the default transform
	 */
	public AffineTransform getDefaultTransform()
	{
		return defaultTransform_;
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
	 *  Draws a primitive
	 *  @param dc The combiner.
	 *  @param primitive The primitive.
	 *  @param obj The object being drawn.
	 */
	public void drawPrimitive(DrawableCombiner dc, Primitive primitive, Object obj)
	{
		RENDERERS[primitive.getType()].prepareAndExecuteDraw(dc, primitive, obj, this);
	}
	
	/**
	 *  Disposes the Viewport.
	 */
	public void dispose()
	{
		disposed = true;
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
		if ((canvas_.getWidth() == 0) || (canvas_.getHeight() == 0))
			return;
		IVector2 oldPaddedSize = paddedSize_.copy();
		setSize(size_);
		setPosition(paddedSize_.copy().subtract(oldPaddedSize).multiply(0.5).negate().add(position_));
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
		BufferedImage image = null;
		try
		{
			image = ImageIO.read(classloader.getResource(path));
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

		public ViewportCanvas()
		{
			backBuffer_ = new BufferedImage(1, 1,
					BufferedImage.TYPE_4BYTE_ABGR_PRE);
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
				if ((getWidth() == 0) || (getHeight() == 0))
					return;
				if((backBuffer_.getWidth() != getWidth())
						|| (backBuffer_.getHeight() != getHeight()))
				{
					backBuffer_ = new BufferedImage(getWidth(), getHeight(),
							BufferedImage.TYPE_4BYTE_ABGR_PRE);
					ViewportJ2D.this.setSize(size_);
				}

				Graphics2D g = (Graphics2D)backBuffer_.getGraphics();
				defaultTransform_ = g.getTransform();
				g.setColor(bgColor_);
				g.fillRect(0, 0, getWidth(), getHeight());
				
				Rectangle clipRect = getClippingBox();
				if (getInvertX())
					clipRect.x = canvas_.getWidth() - clipRect.x - clipRect.width;
				if (!getInvertY())
					clipRect.y = canvas_.getHeight() - clipRect.y - clipRect.height;
				g.setClip(clipRect.x, clipRect.y, clipRect.width, clipRect.height);
				
				setupTransform(g);
				context_ = g;

				synchronized(preLayers_)
				{
					for (int i = 0; i < preLayers_.length; ++i)
					{
						Layer l = preLayers_[i];
						LAYER_RENDERERS[l.getType()].draw(getPerspective(), l, areaSize_, ViewportJ2D.this);
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
								//d.init(ViewportJ2D.this);
								drawObjects_.add(d);
							}
							objectLayers_.addAll(d.getLayers());
						}
						
						AffineTransform tf = g.getTransform();
						g.translate(objShiftX_, objShiftY_);
						// TODO: Hack!, get Monitor to ensure object draw synchronization
						Object monitor = getPerspective().getObserverCenter().getSpace().getMonitor();
						
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
								// TODO: Hack!, ensure object draw synchronization
								synchronized (monitor)
								{
									d.draw(obj, layer, ViewportJ2D.this);
								}
							}
						}
						g.setTransform(tf);
					}
				}

				synchronized(postLayers_)
				{
					for (int i = 0; i < postLayers_.length; ++i)
					{
						Layer l = postLayers_[i];
						LAYER_RENDERERS[l.getType()].draw(getPerspective(), l, areaSize_, ViewportJ2D.this);
					}
				}
				
				context_ = null;
				g.setTransform(defaultTransform_);
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
			g.translate(-pixPosition_.getXAsDouble(), -pixPosition_.getYAsDouble());
		}
	}

}
