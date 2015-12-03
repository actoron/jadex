package jadex.extension.envsupport.observer.graphics.opengl;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.color.ColorSpace;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DataBufferByte;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.lang.reflect.Field;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.media.opengl.GL;
import javax.media.opengl.GLAutoDrawable;
import javax.media.opengl.GLCanvas;
import javax.media.opengl.GLCapabilities;
import javax.media.opengl.GLEventListener;
import javax.media.opengl.GLException;
import javax.media.opengl.glu.GLU;

import com.sun.opengl.impl.GLDrawableHelper;
import com.sun.opengl.util.j2d.TextRenderer;

import jadex.commons.SUtil;
import jadex.extension.envsupport.math.IVector2;
import jadex.extension.envsupport.math.Vector2Double;
import jadex.extension.envsupport.observer.graphics.AbstractViewport;
import jadex.extension.envsupport.observer.graphics.drawable.DrawableCombiner;
import jadex.extension.envsupport.observer.graphics.drawable.Primitive;
import jadex.extension.envsupport.observer.graphics.layer.Layer;
import jadex.extension.envsupport.observer.perspective.IPerspective;


/**
 * OpenGL/JOGL-based Viewport. This viewport attempts to use OpenGL for drawing.
 * Exceptions/Errors may be thrown if OpenGL cannot be linked, also be sure to
 * test isValid() afterwards to verify the availability of necessary extensions.
 */
public class ViewportJOGL extends AbstractViewport
{
	/** Minimum canvas size */
	private static final int MIN_SIZE = 32;
	
	/** Clamped texture cache. */
	//private Map					clampedTextureCache_;

	/** Repeating texture cache. */
	//private Map					repeatingTextureCache_;
	
	/** Repeating texture cache. */
	private Map					textureCache_;

	/** Display lists. */
	private Map					displayLists_;

	/** True, until the OpenGL context is initialized. */
	@SuppressWarnings("unused")
	private volatile boolean	uninitialized_;

	/** This will be true if the OpenGL context supports all necessary extensions. */
	@SuppressWarnings("unused")
	private volatile boolean	valid_;

	/** True, if non-power-of-two texture support is available. */
	private boolean				npot_;
	
	/** Action that renders the frame. */
	private Runnable			renderFrameAction_;
	
	/** Current OpenGL rendering context */
	private GL context_;
	
	/** The class loader. */
	private ClassLoader	classloader;
	
	/** The text renderers */
	private Map textRenderers_;
	
	/** OpenGL thread execution queue */
	private List glQueue_;
	
	/** The renderers. */
	private static final IGLRenderer[] RENDERERS = new IGLRenderer[6];
	static
	{
		RENDERERS[0] = new EllipseGLRenderer();
		RENDERERS[1] = new RectangleGLRenderer();
		RENDERERS[2] = new RegularPolygonGLRenderer();
		RENDERERS[3] = new TextGLRenderer();
		RENDERERS[4] = new TexturedRectangleGLRenderer();
		RENDERERS[5] = new TriangleGLRenderer();
	}
	
	/** The layer renderers. */
	private static final ILayerGLRenderer[] LAYER_RENDERERS = new ILayerGLRenderer[3];
	static
	{
		LAYER_RENDERERS[0] = new ColorLayerGLRenderer();
		LAYER_RENDERERS[1] = new GridLayerGLRenderer();
		LAYER_RENDERERS[2] = new TiledLayerGLRenderer();
	}

	/**
	 * Creates a new OpenGL-based viewport. May throw UnsatisfiedLinkError and
	 * RuntimeException if linking to OpenGL fails.
	 * 
	 * @param layerObject object holding properties for pre/postlayers
	 * @param libService library service for loading resources.
	 */
	public ViewportJOGL(IPerspective persp, ClassLoader classloader)
	{
		super(persp);
		this.classloader	= classloader;
		uninitialized_ = true;
		valid_ = false;
		npot_ = false;
		//clampedTextureCache_ = Collections.synchronizedMap(new HashMap());
		//repeatingTextureCache_ = Collections.synchronizedMap(new HashMap());
		textureCache_ = Collections.synchronizedMap(new HashMap());
		displayLists_ = Collections.synchronizedMap(new HashMap());
		textRenderers_ = Collections.synchronizedMap(new TextRendererLRUMap(15));
		glQueue_ = Collections.synchronizedList(new ArrayList());

		try
		{
			JOGLNativeLoader.loadJOGLLibraries();
			GLCapabilities caps = new GLCapabilities();
			caps.setDoubleBuffered(true);
			caps.setHardwareAccelerated(true);
			canvas_ = new ResizeableGLCanvas(caps);
			((GLCanvas)canvas_).addGLEventListener(new GLController());
		}
		catch(GLException e)
		{
			throw e;
		}
		catch(Error e)
		{
			throw e;
		}

		MouseController mc = new MouseController();
		canvas_.addMouseListener(mc);
		canvas_.addMouseWheelListener(mc);
		canvas_.addMouseMotionListener(mc);

		setSize(new Vector2Double(1.0));
		renderFrameAction_ = new Runnable()
		{
			public void run()
			{
				GLCanvas canvas = ((GLCanvas)ViewportJOGL.this.canvas_);
				if (canvas == null)
					return;
//				System.out.println("repaint");
				canvas.display();
//				System.out.println("repaint done");
				rendering	= false;
			}
		};
	}
	
	public void setSize(IVector2 size)
	{
		glQueue_.add(new Runnable()
			{
				public void run()
				{
					synchronized(textRenderers_)
					{
						for (Iterator it = textRenderers_.values().iterator(); it.hasNext(); )
						{
							TextRenderer tr = (TextRenderer) it.next();
							tr.dispose();
						}
						textRenderers_.clear();
					}
				}
			});
		super.setSize(size);
	}

	public void refresh()
	{
		if(!rendering)
		{
			rendering	= true;
			EventQueue.invokeLater(renderFrameAction_);
		}
	}

	/**
	 * Verifies the OpenGL context is valid and useable.
	 */
	public boolean isValid()
	{
		//TODO: Hack: do proper validity checking
		return true;
		/*while(uninitialized_)
		{
			try
			{
				Thread.sleep(100);
			}
			catch(InterruptedException e)
			{
			}
		}
		return valid_;*/
	}

	/**
	 * Returns a repeating texture.
	 * 
	 * @param gl OpenGL interface
	 * @param path resource path of the texture
	 * @return the texture
	 */
	/*public int getRepeatingTexture(GL gl, String path)
	{
		Integer texture = (Integer)repeatingTextureCache_.get(path);
		if(texture == null)
		{
			// Disable dodgy MipMapping in JOGL (AssertionError), use plain bilinear interpolation
			//texture = loadTexture(gl, path, GL.GL_REPEAT, GL.GL_LINEAR_MIPMAP_LINEAR);
			texture = loadTexture(gl, path, GL.GL_REPEAT, GL.GL_LINEAR);
			repeatingTextureCache_.put(path, texture);
		}

		return texture.intValue();
	}*/

	/**
	 * Returns a clamped texture.
	 * 
	 * @param gl OpenGL interface
	 * @param path resource path of the texture
	 * @return the texture
	 */
	/*public int getClampedTexture(GL gl, String path)
	{

		Integer texture = (Integer)clampedTextureCache_.get(path);
		if(texture == null)
		{
			texture = loadTexture(gl, path, GL.GL_CLAMP_TO_EDGE, GL.GL_LINEAR);
			clampedTextureCache_.put(path, texture);
		}

		return texture.intValue();
	}*/
	
	/**
	 * Returns a texture.
	 * 
	 * @param gl OpenGL interface
	 * @param path resource path of the texture
	 * @return the texture
	 */
	public int getTexture(GL gl, String path)
	{
		Integer texture = (Integer)textureCache_.get(path);
		if(texture == null)
		{
			// Disable dodgy MipMapping in JOGL (AssertionError), use plain bilinear interpolation
			//texture = loadTexture(gl, path, GL.GL_REPEAT, GL.GL_LINEAR_MIPMAP_LINEAR);
			texture = loadTexture(gl, path, GL.GL_LINEAR);
			textureCache_.put(path, texture);
		}

		return texture.intValue();
	}
	
	/**
	 * Returns an appropriate text renderer.
	 * 
	 * @param font the font for the renderer
	 * @return the renderer
	 */
	public TextRenderer getTextRenderer(Font font)
	{
		TextRenderer tr = (TextRenderer) textRenderers_.get(font);
		if (tr == null)
		{
//			tr = new TextRenderer(font);
			// Activate mipmapping, because renderer otherwise tends to hang.
			tr = new TextRenderer(font, false, false, null, true);
			tr.setUseVertexArrays(false);
			textRenderers_.put(font, tr);
		}
		return tr;
	}

	/**
	 * Returns a previous generated display list or null if it doesn't exist
	 * 
	 * @param listName name of the list
	 * @return previously generated display list
	 */
	public Integer getDisplayList(String listName)
	{
		return (Integer)displayLists_.get(listName);
	}

	/**
	 * Sets a display list.
	 * 
	 * @param listName name of the list
	 * @param list the display list
	 */
	public void setDisplayList(String listName, Integer list)
	{
		displayLists_.put(listName, list);
	}
	
	/**
	 * Returns the current GL rendering context.
	 * @return GL context, null if none is available
	 */
	public GL getContext()
	{
		return context_;
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
		// Dispose called twice???
		if(canvas_==null)
			return;
		
		((GLCanvas) canvas_).setAutoSwapBufferMode(false);
		((GLCanvas) canvas_).getContext().destroy();
		
		try
		{
			Field helperField = GLCanvas.class.getDeclaredField("drawableHelper");
			helperField.setAccessible(true);
			
			Field tlField = GLDrawableHelper.class.getDeclaredField("perThreadInitAction");
			tlField.setAccessible(true);
			
			ThreadLocal tl = (ThreadLocal) tlField.get(helperField.get(canvas_));
			tl.remove();
		}
		catch (Exception e)
		{
		}
		
		canvas_ = null;
	}

	private void setupMatrix(GL gl)
	{
		gl.glMatrixMode(GL.GL_MODELVIEW);
		gl.glLoadIdentity();
		gl.glOrtho(paddedSize_.getXAsDouble() * inversionFlag_.getXAsInteger(),
				paddedSize_.getXAsDouble()
						* (inversionFlag_.getXAsInteger() ^ 1), paddedSize_
						.getYAsDouble()
						* inversionFlag_.getYAsInteger(), paddedSize_
						.getYAsDouble()
						* (inversionFlag_.getYAsInteger() ^ 1), -0.5, 0.5);
		//gl.glTranslated(-position_.getXAsDouble(), -position_.getYAsDouble(), 0.0);
		gl.glTranslated(-pixPosition_.getXAsDouble(), -pixPosition_.getYAsDouble(), 0.0);
		
		// Setup the scissor box
		
		
		//System.out.println(x + " " + y + " " + w + " " + h);
		
		Rectangle clipRect = getClippingBox();
		if (getInvertX())
			clipRect.x = canvas_.getWidth() - clipRect.x - clipRect.width;
		if (getInvertY())
			clipRect.y = canvas_.getHeight() - clipRect.y - clipRect.height;
		gl.glScissor(clipRect.x, clipRect.y, clipRect.width, clipRect.height);
	}
	
	/**
	 * Configures the texture matrix.
	 * 
	 * @param gl GL context
	 */
	private void setupTexMatrix(GL gl)
	{
		gl.glMatrixMode(GL.GL_TEXTURE);
		gl.glLoadIdentity();
		gl.glTranslatef(inversionFlag_.getXAsInteger(), (~inversionFlag_.getYAsInteger()) & 1, 0.0f);
		gl.glScalef(-((inversionFlag_.getXAsInteger() << 1) - 1), ((inversionFlag_.getYAsInteger() << 1) - 1), 1.0f);
		gl.glMatrixMode(GL.GL_MODELVIEW);
	}

	/**
	 * Loads a Texture
	 * 
	 * @param gl OpenGL interface
	 * @param path texture resource path
	 * @param wrapParam wrap parameter
	 */
	private synchronized Integer loadTexture(GL gl, String path, int ipMode)
	{
		BufferedImage tmpImage = null;
		try
		{
			tmpImage = ImageIO.read(SUtil.getResource(path, classloader));
			/*AffineTransform tf = AffineTransform.getScaleInstance(1, -1);
			tf.translate(0, -tmpImage.getHeight());
			AffineTransformOp op = new AffineTransformOp(tf,
					AffineTransformOp.TYPE_BILINEAR);
			tmpImage = op.filter(tmpImage, null);*/
		}
		catch(Exception e)
		{
			System.err.println("Image not found: " + path);
			throw new RuntimeException("Image not found: " + path);
		}
//		System.out.println(tmpImage);
//		return prepareTexture(gl, tmpImage, GL.GL_COMPRESSED_RGBA, wrapMode, GL.GL_LINEAR_MIPMAP_LINEAR);
		return prepareTexture(gl, tmpImage, GL.GL_RGBA, ipMode);
	}
	
	/**
	 * Perpares a Texture
	 * 
	 * @param gl OpenGL interface
	 * @param inputImage the image being converted to a texture
	 * @param wrapParam wrap parameter
	 */
	private Integer prepareTexture(GL gl, BufferedImage inputImage, int intFormat, int ipMode)
	{
		int width = inputImage.getWidth();
		int height = inputImage.getHeight();
		if(!npot_)
		{
			width = (int)Math.pow(2, Math.ceil(Math.log(width) / Math.log(2)));
			height = (int)Math
					.pow(2, Math.ceil(Math.log(height) / Math.log(2)));
		}

		// Convert image data
		ColorModel colorModel = new ComponentColorModel(ColorSpace
				.getInstance(ColorSpace.CS_sRGB), new int[]{8, 8, 8, 8}, true,
				false, ComponentColorModel.TRANSLUCENT, DataBuffer.TYPE_BYTE);

		WritableRaster raster = Raster.createInterleavedRaster(
				DataBuffer.TYPE_BYTE, width, height, 4, null);
		BufferedImage image = new BufferedImage(colorModel, raster, false,
				new Hashtable());
		Graphics2D g = (Graphics2D)image.getGraphics();
		//g.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
		g.setColor(Color.BLACK);
		g.setComposite(AlphaComposite.Src);
		g.fillRect(0, 0, width, height);
		g.drawImage(inputImage, 0, 0, width, height, 0, 0, inputImage.getWidth(), inputImage.getHeight(), null);
		g.dispose();
		inputImage = null;
		
		byte[] imgData = ((DataBufferByte)image.getRaster().getDataBuffer())
				.getData();
		ByteBuffer buffer = ByteBuffer.allocateDirect(imgData.length);
		buffer.order(ByteOrder.nativeOrder());
		buffer.put(imgData, 0, imgData.length);
		buffer.flip();
		//buffer.rewind();

		// Prepare texture
		int[] texId = new int[1];
		gl.glGenTextures(1, texId, 0);
		Integer texture = Integer.valueOf(texId[0]);

		gl.glEnable(GL.GL_TEXTURE_2D);
		gl.glBindTexture(GL.GL_TEXTURE_2D, texId[0]);

		//gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_S, wrapMode);
		//gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_WRAP_T, wrapMode);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MIN_FILTER,
				ipMode);
		gl.glTexParameteri(GL.GL_TEXTURE_2D, GL.GL_TEXTURE_MAG_FILTER,
				ipMode);
		
		if ((ipMode == GL.GL_LINEAR_MIPMAP_LINEAR) || (ipMode == GL.GL_LINEAR_MIPMAP_NEAREST) || (ipMode == GL.GL_NEAREST_MIPMAP_NEAREST) || (ipMode == GL.GL_NEAREST_MIPMAP_LINEAR))
		{
			GLU glu = new GLU();
			glu.gluBuild2DMipmaps(GL.GL_TEXTURE_2D, intFormat, image
					.getWidth(), image.getHeight(), GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, buffer);
		}
		else
		{
			gl.glTexImage2D(GL.GL_TEXTURE_2D, 0, intFormat, image
				.getWidth(), image.getHeight(), 0, GL.GL_RGBA,
				GL.GL_UNSIGNED_BYTE, buffer);
			/*glu.gluBuild2DMipmaps(GL.GL_TEXTURE_2D, intFormat, image
					.getWidth(), image.getHeight(), GL.GL_RGBA, GL.GL_UNSIGNED_BYTE, buffer);*/
		}

		gl.glDisable(GL.GL_TEXTURE_2D);
		
		return texture;
	}

	private class GLController implements GLEventListener
	{
		public void display(GLAutoDrawable drawable)
		{
			if(canvas_==null)
				return;
			
			synchronized(glQueue_)
			{
				for (Iterator it = glQueue_.iterator(); it.hasNext(); )
				{
					Runnable r = (Runnable) it.next();
					r.run();
				}
			}
			
			if ((canvas_.getWidth() < MIN_SIZE) ||
				(canvas_.getHeight() < MIN_SIZE))
			{
				return;
			}
			GL gl = drawable.getGL();
			
			setupMatrix(gl);
			setupTexMatrix(gl);
			
			float[] bgc = bgColor_.getRGBComponents(null);
			gl.glClearColor(bgc[0], bgc[1], bgc[2], bgc[3]);
			gl.glClear(GL.GL_COLOR_BUFFER_BIT);

			gl.glEnable(GL.GL_SCISSOR_TEST);
			
			context_ = gl;
			
			synchronized(preLayers_)
			{
				for (int i = 0; i < preLayers_.length; ++i)
				{
					Layer l = preLayers_[i];
					LAYER_RENDERERS[l.getType()].draw(getPerspective(), l, areaSize_, ViewportJOGL.this);
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
							//d.init(ViewportJOGL.this);
							drawObjects_.add(d);
						}
						objectLayers_.addAll(d.getLayers());
					}
					
					
					// TODO: Hack!, get Monitor to ensure object draw synchronization
					Object monitor = getPerspective().getObserverCenter().getSpace().getMonitor();
					
					gl.glPushMatrix();
					gl.glTranslatef(objShiftX_, objShiftY_, 0.0f);
					for(Iterator it = objectLayers_.iterator(); it.hasNext();)
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
								d.draw(obj, layer, ViewportJOGL.this);
							}
						}
					}
					gl.glPopMatrix();
				}
			}

			synchronized(postLayers_)
			{
				for (int i = 0; i < postLayers_.length; ++i)
				{
					Layer l = postLayers_[i];
					LAYER_RENDERERS[l.getType()].draw(getPerspective(), l, areaSize_, ViewportJOGL.this);
				}
			}
			
			context_ = null;
			gl.glDisable(GL.GL_SCISSOR_TEST);
		}

		public void displayChanged(GLAutoDrawable drawable,
				boolean modeChanged, boolean deviceChanged)
		{
		}

		public void init(GLAutoDrawable drawable)
		{
			// Hack!!! Why is init called after cleanup!?
			if(canvas_==null)
				return;
			
			GL gl = drawable.getGL();
			context_ = gl;
			gl.glViewport(0, 0, canvas_.getWidth(), canvas_.getHeight());
			gl.glMatrixMode(GL.GL_MODELVIEW);

			gl.glEnable(GL.GL_BLEND);
			gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
			gl.glDisable(GL.GL_DEPTH_TEST);

			setupMatrix(gl);
			setupTexMatrix(gl);
			
			// Check for OpenGL version or extensions if needed
			if(gl.isExtensionAvailable("GL_VERSION_2_0")
					|| gl.isExtensionAvailable("GL_VERSION_2_1")
					|| gl
							.isExtensionAvailable("GL_ARB_texture_non_power_of_two"))
			{
				npot_ = true;
			}

			// TODO: Add checks.
			valid_ = true;

			/**
			 * if (!(gl.isFunctionAvailable("glGenBuffers"))) { valid_ = false;
			 * } else { }
			 */

			//clampedTextureCache_.clear();
			//repeatingTextureCache_.clear();
			textureCache_.clear();
			displayLists_.clear();

			drawObjects_.clear();

			uninitialized_ = false;
			context_ = null;
		}

		public void reshape(GLAutoDrawable drawable, int x, int y, int width,
				int height)
		{
			if ((canvas_.getWidth() < MIN_SIZE) ||
				(canvas_.getHeight() < MIN_SIZE))
			{
				return;
			}
			GL gl = drawable.getGL();
			IVector2 oldPaddedSize = paddedSize_.copy();
			setSize(size_);
			setPosition(paddedSize_.copy().subtract(oldPaddedSize).multiply(0.5).negate().add(position_));
			setupMatrix(gl);
		}

	}

	private class ResizeableGLCanvas extends GLCanvas
	{
		public void display()
		{
			if ((getWidth() < MIN_SIZE) ||
				(getHeight() < MIN_SIZE))
			{
				return;
			}
			super.display();
		}
		
		public ResizeableGLCanvas(GLCapabilities caps)
		{
			super(caps);
		}

		public Dimension minimumSize()
		{
			return new Dimension(1, 1);
		}

		public Dimension getMinimumSize()
		{
			return new Dimension(1, 1);
		}
	}
	
	private class TextRendererLRUMap extends LinkedHashMap
	{
		private int size;
		
		public TextRendererLRUMap(int size)
		{
			this.size = size;
		}
		
		protected boolean removeEldestEntry(java.util.Map.Entry entry)
		{
			if (size() > size)
			{
				TextRenderer tr = (TextRenderer) entry.getValue();
				tr.dispose();
				return true;
			}
			return false;
		}
	}
}
