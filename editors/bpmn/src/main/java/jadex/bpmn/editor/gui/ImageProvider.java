package jadex.bpmn.editor.gui;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Composite;
import java.awt.CompositeContext;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.GradientPaint;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.Ellipse2D;
import java.awt.geom.GeneralPath;
import java.awt.geom.Rectangle2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ConvolveOp;
import java.awt.image.Kernel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.InputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;

import jadex.bpmn.editor.gui.stylesheets.EventShape;
import jadex.bpmn.editor.gui.stylesheets.GatewayShape;
import jadex.commons.Tuple3;

/**
 *  Class for providing images, either stored or generated, with a cache.
 *
 */
public class ImageProvider
{
	/** Thick frame type. */
	public static final int EMPTY_FRAME_TYPE  = -1;
	
	/** Thick frame type. */
	public static final int THICK_FRAME_TYPE  = 0;
	
	/** Thin frame type. */
	public static final int THIN_FRAME_TYPE   = 1;
	
	/** Double frame type. */
	public static final int DOUBLE_FRAME_TYPE = 2;
	
	/** Rhombus base shape. */
	public static final Shape SHAPE_RHOMBUS;
	
	/** Rounded rectangle base shape */
	public static final Shape SHAPE_ROUNDED_RECTANGLE;
	
	/** Rounded rectangle base shape */
	public static final Shape SHAPE_RECTANGLE;
	
	/** Ellipse base shape */
	public static final Shape SHAPE_ELLIPSE;
	
	/** Darkening factor for non-highlighted icons */
	protected static final float NON_HIGHLIGHT_DARKENING_FACTOR = 0.92f;
	
	/** The base icon size. */
	protected static final int BASE_ICON_SIZE = 128;
	
	/** The activation shift. */
	protected static final int ACTIVATION_SHIFT = BASE_ICON_SIZE >>> 3;
	
	/** The button size. */
	protected static final int BUTTON_SIZE = BASE_ICON_SIZE - ACTIVATION_SHIFT;
	
	/** The image symbol inset factor. */
	protected static final double IMAGE_SYMBOL_INSET_FACTOR = 0.15;
	
	protected static final double SHADOW_SCALE = 0.875;
	
//	/** The shadow size. */
//	protected static final int SHADOW_SIZE = ACTIVATION_SHIFT >>> 1;
	
	/** The frame thickness. */
	protected static final int FRAME_THICKNESS = BUTTON_SIZE / 12;
	
	/** The frame thickness factor for thin frames. */
	protected static final int THIN_FRAME_THICKNESS = FRAME_THICKNESS / 3;
	
	/** The glass effect shrink factor. */
	protected static final double GLASS_SHRINK = 0.7;
	
	/** The gui directory */
	protected static final String GUI_DIR = ImageProvider.class.getPackage().getName().replaceAll("\\.", "/");
	
	/** The image directory */
	protected static final String IMAGE_DIR = GUI_DIR + "/images/";
	
	/** The font directory */
	protected static final String FONT_DIR = GUI_DIR + "/fonts/";
	
	protected static final ConvolveOp BLUR_FILTER_X;
	protected static final ConvolveOp BLUR_FILTER_Y;
	static
	{
		int dia = BUTTON_SIZE / 12 + 1;
		int rad = (dia - 1) / 2;
        float[] data = new float[dia];
        
        float sig = rad / 3.0f;
        float sig22 = 2.0f * sig * sig;
        float sigroot = (float) Math.sqrt(sig22 * Math.PI);
        float sum = 0.0f;
        
        for (int i = -rad; i <= rad; ++i)
        {
            float dist = i * i;
            int index = i + rad;
            data[index] = (float) Math.exp(-dist / sig22) / sigroot;
            sum += data[index];
        }
        
        for (int i = 0; i < data.length; i++)
        {
            data[i] /= sum;
        }        
        
        Kernel kernel = new Kernel(dia, 1, data);
        BLUR_FILTER_X = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        kernel = new Kernel(1, dia, data);
        BLUR_FILTER_Y = new ConvolveOp(kernel, ConvolveOp.EDGE_NO_OP, null);
        
	}
	
	static
	{
//		double bs2 = BUTTON_SIZE * 0.5;
//		GeneralPath gp = new GeneralPath();
//		gp.moveTo(0, bs2);
//		gp.lineTo(bs2, BUTTON_SIZE);
//		gp.lineTo(BUTTON_SIZE, bs2);
//		gp.lineTo(bs2, 0);
//		gp.closePath();
//		SHAPE_RHOMBUS = gp;
//		
//		SHAPE_ROUNDED_RECTANGLE = new RoundRectangle2D.Double(0.0, 0.0, BUTTON_SIZE, BUTTON_SIZE, BASE_ICON_SIZE >>> 1, BASE_ICON_SIZE >>> 1);
//		
//		SHAPE_ELLIPSE = new Ellipse2D.Double(0.0, 0.0, BUTTON_SIZE, BUTTON_SIZE);
//		
//		SHAPE_RECTANGLE = new Rectangle2D.Double(0.0, 0.0, BUTTON_SIZE, BUTTON_SIZE);
		
		double bs2 = 0.5;
		GeneralPath gp = new GeneralPath();
		gp.moveTo(0, bs2);
		gp.lineTo(bs2, 1);
		gp.lineTo(1, bs2);
		gp.lineTo(bs2, 0);
		gp.closePath();
		SHAPE_RHOMBUS = gp;
		
		SHAPE_ROUNDED_RECTANGLE = new RoundRectangle2D.Double(0.0, 0.0, 1, 1, 0.5, 0.5);
		
		SHAPE_ELLIPSE = new Ellipse2D.Double(0.0, 0.0, 1, 1);
		
		SHAPE_RECTANGLE = new Rectangle2D.Double(0.0, 0.0, 1, 1);
	}
	
	/** The singleton instance */
//	protected static ImageProvider instance;
	
	/** The image cache. */
	protected Map<Object, Image> imagecache = Collections.synchronizedMap(new HashMap<Object, Image>());
	
	/** The generated image cache. */
//	protected Map<String, Image> genimagecache = Collections.synchronizedMap(new HashMap<String, Image>());
	
	/** The rendered text cache. */
//	protected Map<String, Image> textimagecache = Collections.synchronizedMap(new HashMap<String, Image>());
	
	/** The icon font used. */
	protected Font iconfont;
	
	public ImageProvider()
	{
		try
		{
			InputStream fontis = this.getClass().getClassLoader().getResourceAsStream(FONT_DIR + "VeraBd.ttf");
			Font font = Font.createFont(Font.TRUETYPE_FONT, fontis);
			iconfont = font.deriveFont(144.0f);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Gets the singleton instance.
	 */
//	public static final ImageProvider getInstance()
//	{
//		ImageProvider ret = instance;
//		if (ret == null)
//		{
//			synchronized(ImageProvider.class)
//			{
//				if (instance == null)
//				{
//					instance = new ImageProvider();
//					ret = instance;
//				}
//			}
//		}
//		return ret;
//	}
	
	/**
	 *  Generates a generic flat image icon set in the order off, on, highlight.
	 *  
	 *  @param iconsize The icon size.
	 *  @param frametype The button frame type.
	 * 	@param symbol Symbol name or text.
	 *  @param symbolcolor Color of the symbol.
	 *  @return The generated icons.
	 */
	public Icon[] generateGenericFlatImageIconSet(int iconsize, int frametype, String symbol, Color symbolcolor)
	{
		return generateGenericFlatImageIconSet(iconsize, frametype, symbol, symbolcolor, NON_HIGHLIGHT_DARKENING_FACTOR);
	}
	
	/**
	 *  Generates a generic flat image icon set in the order off, on, highlight.
	 *  
	 *  @param iconsize The icon size.
	 *  @param frametype The button frame type.
	 * 	@param symbol Symbol name or text.
	 *  @param symbolcolor Color of the symbol.
	 *  @param darkeningfactor Factor by which to darken non-highlighted icons.
	 *  @return The generated icons.
	 */
	public Icon[] generateGenericFlatImageIconSet(int iconsize, int frametype, String symbol, Color symbolcolor, float darkeningfactor)
	{
		Image offimage = imagecache.get(new Tuple3<String, Integer, String>("flat_off", iconsize, symbol));
		Image onimage = imagecache.get(new Tuple3<String, Integer, String>("flat_on", iconsize, symbol));
		Image highimage = imagecache.get(new Tuple3<String, Integer, String>("flat_high", iconsize, symbol));
		if (offimage == null || onimage == null || highimage == null)
		{
			Image symimg = getSymbol(symbol, new Dimension(BASE_ICON_SIZE, BASE_ICON_SIZE), symbolcolor);
			BufferedImage darksymimg = new BufferedImage(BASE_ICON_SIZE, BASE_ICON_SIZE, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = darksymimg.createGraphics();
			g.setComposite(new ModulateComposite(new Color(darkeningfactor, darkeningfactor, darkeningfactor, 1.0f), true));
			g.drawImage(symimg, 0, 0, BASE_ICON_SIZE, BASE_ICON_SIZE, null);
			g.dispose();
			
//			Image frame = generateGenericFrame(frametype, SHAPE_RECTANGLE, new Dimension(iconsize, iconsize));
			Image frame = generateGenericFrame(frametype, SHAPE_RECTANGLE, new Dimension(BASE_ICON_SIZE, BASE_ICON_SIZE));
			
			offimage = new BufferedImage(BASE_ICON_SIZE, BASE_ICON_SIZE, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			g = ((BufferedImage) offimage).createGraphics();
			g.setComposite(AlphaComposite.SrcOver);
			g.drawImage(darksymimg, 0, 0, BASE_ICON_SIZE, BASE_ICON_SIZE, null);
			g.drawImage(frame, 0, 0, BASE_ICON_SIZE, BASE_ICON_SIZE, null);
			g.dispose();
			offimage = offimage.getScaledInstance(iconsize, iconsize, Image.SCALE_AREA_AVERAGING);
			BufferedImage tmpimage = new BufferedImage(iconsize, iconsize, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			g = tmpimage.createGraphics();
			g.setComposite(AlphaComposite.Src);
			g.drawImage(offimage, 0, 0, iconsize, iconsize, null);
			offimage = tmpimage;
			
			onimage = new BufferedImage(BASE_ICON_SIZE, BASE_ICON_SIZE, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			g = ((BufferedImage) onimage).createGraphics();
			g.setComposite(AlphaComposite.Src);
			g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.150f));
			//g.setColor(Color.BLACK);
			g.fillRect(0, 0, BASE_ICON_SIZE, BASE_ICON_SIZE);
			g.setComposite(AlphaComposite.SrcOver);
			g.setColor(Color.WHITE);
			g.drawImage(symimg, 0, 0, BASE_ICON_SIZE, BASE_ICON_SIZE, null);
			g.drawImage(frame, 0, 0, BASE_ICON_SIZE, BASE_ICON_SIZE, null);
			g.dispose();
			onimage = onimage.getScaledInstance(iconsize, iconsize, Image.SCALE_AREA_AVERAGING);
			tmpimage = new BufferedImage(iconsize, iconsize, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			g = tmpimage.createGraphics();
			g.setComposite(AlphaComposite.Src);
			g.drawImage(onimage, 0, 0, iconsize, iconsize, null);
			onimage = tmpimage;
			
			highimage = new BufferedImage(BASE_ICON_SIZE, BASE_ICON_SIZE, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			g = ((BufferedImage) highimage).createGraphics();
			g.setComposite(AlphaComposite.SrcOver);
			g.setColor(Color.WHITE);
			g.drawImage(symimg, 0, 0, BASE_ICON_SIZE, BASE_ICON_SIZE, null);
			g.drawImage(frame, 0, 0, BASE_ICON_SIZE, BASE_ICON_SIZE, null);
			g.dispose();
			highimage = highimage.getScaledInstance(iconsize, iconsize, Image.SCALE_AREA_AVERAGING);
			tmpimage = new BufferedImage(iconsize, iconsize, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			g = tmpimage.createGraphics();
			g.setComposite(AlphaComposite.Src);
			g.drawImage(highimage, 0, 0, iconsize, iconsize, null);
			highimage = tmpimage;
			
			imagecache.put(new Tuple3<String, Integer, String>("flat_off", iconsize, symbol), offimage);
			imagecache.put(new Tuple3<String, Integer, String>("flat_on", iconsize, symbol), onimage);
			imagecache.put(new Tuple3<String, Integer, String>("flat_high", iconsize, symbol), highimage);
		}
		
		Icon[] ret = new Icon[3];
		ret[0] = new ImageIcon(offimage);
		ret[1] = new ImageIcon(onimage);
		ret[2] = new ImageIcon(highimage);
		
		return ret;
	}
	
	/**
	 *  Generates a generic button icon set in the order off, on, highlight.
	 *  
	 *  @param iconsize The icon size.
	 *  @param baseshape The base shape.
	 * 	@param symbol Symbol name or text.
	 *  @param bgcolor Color of the icon.
	 *  @return The generated icons.
	 */
	public Icon[] generateGenericButtonIconSet(int iconsize, Shape baseshape, String symbol, Color symbolcolor, Color bgcolor)
	{
		Icon[] ret = new Icon[3];
		ret[0] = generateGenericButtonIcon(iconsize, baseshape, symbol, symbolcolor, bgcolor, false, false);
		ret[1] = generateGenericButtonIcon(iconsize, baseshape, symbol, symbolcolor, bgcolor, true, true);
		ret[2] = generateGenericButtonIcon(iconsize, baseshape, symbol, symbolcolor, bgcolor, true, false);
		return ret;
	}
	
	/**
	 *  Generates a generic button icon set in the order off, on, highlight.
	 *  
	 *  @param iconsize The icon size.
	 *  @param baseshape The base shape.
	 *  @param frametype The type of frame.
	 * 	@param symbol Symbol name or text.
	 *  @param bgcolor Color of the icon.
	 *  @return The generated icons.
	 */
	public Icon[] generateGenericButtonIconSet(int iconsize, Shape baseshape, int frametype, String symbol, Color symbolcolor, Color bgcolor)
	{
		Icon[] ret = new Icon[3];
		ret[0] = generateGenericButtonIcon(iconsize, baseshape, frametype, symbol, symbolcolor, bgcolor, false, false);
		ret[1] = generateGenericButtonIcon(iconsize, baseshape, frametype, symbol, symbolcolor, bgcolor, true, true);
		ret[2] = generateGenericButtonIcon(iconsize, baseshape, frametype, symbol, symbolcolor, bgcolor, true, false);
		return ret;
	}
	
	/**
	 *  Generates a generic button icon.
	 *  
	 *  @param iconsize The icon size.
	 *  @param baseshape The base shape.
	 * 	@param symbol Symbol name or text.
	 *  @param bgcolor Color of the icon.
	 *  @param high Highlight the icon if true.
	 *  @param shift Shift (for pressed status) if true.
	 *  @return The generated image icon.
	 */
	public ImageIcon generateGenericButtonIcon(int iconsize, Shape baseshape, String symbol, Color symbolcolor, Color bgcolor, boolean high, boolean shift)
	{
		return generateGenericButtonIcon(iconsize, baseshape, THICK_FRAME_TYPE, symbol, symbolcolor, bgcolor, high, shift);
	}
	
	/**
	 *  Generates a generic button icon.
	 *  
	 *  @param iconsize The icon size.
	 *  @param baseshape The base shape.
	 *  @param frametype The type of frame.
	 * 	@param symbol Symbol name or text.
	 *  @param bgcolor Color of the icon
	 *  @param high Highlight the icon if true.
	 *  @param shift Shift (for pressed status) if true.
	 *  @return The generated image icon.
	 */
	public ImageIcon generateGenericButtonIcon(int iconsize, Shape baseshape, int frametype, String symbol, Color symbolcolor, Color bgcolor, boolean high, boolean shift)
	{
		Dimension size = new Dimension(BUTTON_SIZE, BUTTON_SIZE);
		Image symimg = getSymbol(symbol, size, symbolcolor);
		Image frame = generateGenericFrame(frametype, baseshape, size);
		Image glass = generateGenericGlass(baseshape, size);
		Image bgshape = generateGenericBackground(baseshape, size);
		Image shadow = null;
		if (!shift)
		{
			shadow = generateGenericShadow(baseshape, size);// new Dimension(BASE_ICON_SIZE, BASE_ICON_SIZE));
		}
		
		return compositeImageIcon(new Dimension(BASE_ICON_SIZE, BASE_ICON_SIZE), new Dimension(iconsize, iconsize), symimg, bgshape, frame, glass, shadow, bgcolor, high, false);
	}
	
	/**
	 *  Generates a flat version of a button icon.
	 *  
	 *  @param iconsize The icon size.
	 *  @param baseshape The base shape.
	 *  @param frametype The type of frame.
	 * 	@param symbol Symbol name or text.
	 *  @param bgcolor Color of the icon
	 *  @param high Highlight the icon if true.
	 *  @param shift Shift (for pressed status) if true.
	 *  @return The generated image icon.
	 */
	public ImageIcon generateFlatButtonIcon(int iconsize, Shape baseshape, int frametype, String symbol, Color symbolcolor, Color bgcolor)
	{
		Dimension basesize = new Dimension(BASE_ICON_SIZE, BASE_ICON_SIZE);
		Dimension size = new Dimension(iconsize, iconsize);
		Image symimg = getSymbol(symbol, basesize, symbolcolor);
		Image frame = generateGenericFrame(frametype, baseshape, basesize);
		Image glass = generateGenericGlass(baseshape, basesize);
		Image bgshape = generateGenericBackground(baseshape, basesize);
		
		return compositeImageIcon(basesize, size, symimg, bgshape, frame, glass, null, bgcolor, true, true);
	}
	
	/**
	 *  Loads the image cache from a file.
	 *	 
	 *	@param filepath Path to the file.
	 * 	@throws IOException Exception on IO errors.
	 */
	/*@SuppressWarnings("unchecked")
	public void loadCache(String filepath) throws IOException
	{
		File file = new File(filepath);
		int size = (int) file.length();
		byte[] data = new byte[size];
		int pos = 0;
		FileInputStream is = new FileInputStream(file);
		while (pos < size)
		{
			pos += is.read(data, pos, size - pos);
		}
		is.close();
		try
		{
			imagecache = (Map<Object, Image>) SBinarySerializer.readObjectFromByteArray(data, null, null, ImageProvider.class.getClassLoader(), null);
		}
		catch (Exception e)
		{
			file.delete();
		}
	}*/
	
	/**
	 *  Saves the image cache to a file.
	 *	 
	 *	@param filepath Path to the file.
	 * 	@throws IOException Exception on IO errors.
	 */
	/*public void saveCache(String filepath) throws IOException
	{
		Map<Object, Image> cache = imagecache;
		Object[] keys = cache.keySet().toArray(new Object[cache.size()]);
		for (Object key : keys)
		{
			if (key instanceof Tuple)
			{
				Tuple tuple = (Tuple) key;
				for (int i = 0; i < tuple.getEntities().length; ++i)
				{
					if (tuple.getEntities()[i] instanceof Shape)
					{
						cache.remove(key);
					}
				}
			}
		}
		
		byte[] data = SBinarySerializer.writeObjectToByteArray(imagecache, ImageProvider.class.getClassLoader());
		File tmpfile = File.createTempFile("imagecache", ".cfg");
		FileOutputStream os = new FileOutputStream(tmpfile);
		os.write(data);
		os.close();
		SUtil.moveFile(tmpfile, new File(filepath));
	}*/
	
	/**
	 *  Composites an image icon out of multiple images.
	 *  
	 *  @param symbol The symbol image.
	 *  @param bgshape The background shape image.
	 *  @param frame The shaped frame image.
	 *  @param glass The glass effect image.
	 *  @param shadow The shadow effect image, composite pressed (shifted) image if null.
	 *  @param bgcolor The color of the icon.
	 *  @param high Highlight the icon if true.
	 *  @return The image icon.
	 */
	private ImageIcon compositeImageIcon(Dimension basesize, Dimension iconsize, Image symbol, Image bgshape, Image frame, Image glass, Image shadow, Color bgcolor, boolean high, boolean flat)
	{
		BufferedImage ret = null;
		BufferedImage tmpimg = new BufferedImage(basesize.width, basesize.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
		Graphics2D g = tmpimg.createGraphics();
		g.setComposite(new ModulateComposite(bgcolor, high));
		g.drawImage(bgshape, 0, 0, bgshape.getWidth(null), bgshape.getHeight(null), null);
		g.dispose();
		bgshape = tmpimg;
		
		Image full = new BufferedImage(basesize.width, basesize.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
		g = ((BufferedImage) full).createGraphics();
		g.setComposite(AlphaComposite.SrcOver);
		
		int x = 0;
		int y = 0;
		if (!flat)
		{
			if (shadow == null)
			{
				x = ACTIVATION_SHIFT;
				y = ACTIVATION_SHIFT;
			}
			else
			{
	//			g.drawImage(shadow, x, y, shadow.getWidth(null), shadow.getHeight(null), null);
				int sw = shadow.getWidth(null);
				int sh = shadow.getHeight(null);
				int sx = (int) Math.round((basesize.width - sw) * 0.5);
				int sy = (int) Math.round((basesize.height - sh) * 0.5);
				g.drawImage(shadow, sx, sy, sw, sh, null);
			}
		}
		
		g.drawImage(bgshape, x, y, bgshape.getWidth(null), bgshape.getHeight(null), null);
		g.drawImage(frame, x, y, frame.getWidth(null), frame.getHeight(null), null);
		g.drawImage(glass, x, y, glass.getWidth(null), glass.getHeight(null), null);
		g.drawImage(symbol, x, y, symbol.getWidth(null), symbol.getHeight(null), null);
		g.dispose();
		bgshape = null;
		
		if (basesize.width != iconsize.width || basesize.height != iconsize.height)
		{
			
			full = full.getScaledInstance(iconsize.width, iconsize.height, Image.SCALE_AREA_AVERAGING);
			ret = new BufferedImage(iconsize.width, iconsize.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			g = ret.createGraphics();
			g.setComposite(AlphaComposite.Src);
			g.drawImage(full, 0, 0, iconsize.width, iconsize.height, null);
			g.dispose();
		}
		else
		{
			ret = (BufferedImage) full;
		}
		return new ImageIcon(ret);
	}
	
	/**
	 *  Generates a generic button background for a base shape.
	 *  
	 *  @param baseshape The base shape.
	 *  @return The background image.
	 */
	private Image generateGenericBackground(Shape baseshape, Dimension size)
	{
		BufferedImage ret = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
		Graphics2D g = ((BufferedImage) ret).createGraphics();
		
		AffineTransform at = new AffineTransform();
		at.scale(size.getWidth(), size.getHeight());
		Shape shape = at.createTransformedShape(baseshape);
		
		g.setColor(Color.WHITE);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		g.fill(shape);
		g.dispose();
		
		return ret;
	}
	
	/**
	 *  Generates a generic button frame for a base shape.
	 *  
	 *  @param frametype The frame type.
	 *  @param baseshape The base shape.
	 *  @param size The size of the image.
	 *  @return The frame image.
	 */
	private Image generateGenericFrame(int frametype, Shape baseshape, Dimension size)
	{
		AffineTransform at = new AffineTransform();
		at.scale(size.getWidth(), size.getHeight());
		Shape shape = at.createTransformedShape(baseshape);
		
//		double thicknessscale = size.getWidth() / BASE_ICON_SIZE;
//		thicknessscale = Math.min(thicknessscale, size.getHeight() / BASE_ICON_SIZE);
		
		BufferedImage ret = (BufferedImage) imagecache.get(new Object[] { "frame", frametype, shape, size });
		
		if (ret == null)
		{
			ret = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = ((BufferedImage) ret).createGraphics();
			
			Area framearea = new Area(shape);
			
			if (THICK_FRAME_TYPE == frametype)
			{
				AffineTransform st = new AffineTransform();
				double sf = (double) (Math.max(size.width, size.height) - (FRAME_THICKNESS << 1)) / Math.max(size.width, size.height);
				st.translate(FRAME_THICKNESS, FRAME_THICKNESS);
				st.scale(sf, sf);
				framearea.subtract(new Area(st.createTransformedShape(shape)));
			}
			else if (THIN_FRAME_TYPE == frametype)
			{
				AffineTransform st = new AffineTransform();
				double fs = THIN_FRAME_THICKNESS;
				double sf = (double) (Math.max(size.width, size.height) - (fs * 2.0)) / Math.max(size.width, size.height);
				st.translate(fs, fs);
				st.scale(sf, sf);
				framearea.subtract(new Area(st.createTransformedShape(shape)));
			}
			else if (DOUBLE_FRAME_TYPE == frametype)
			{
				AffineTransform st = new AffineTransform();
				double fs = THIN_FRAME_THICKNESS;
				double sf = (double) (Math.max(size.width, size.height) - (fs * 2.0)) / Math.max(size.width, size.height);
				st.translate(fs, fs);
				st.scale(sf, sf);
				framearea.subtract(new Area(st.createTransformedShape(shape)));
				
				st = new AffineTransform();
				sf = (double) (Math.max(size.width, size.height) - (fs * 6.0)) / Math.max(size.width, size.height);
				st.translate(fs * 3.0, fs * 3.0);
				st.scale(sf, sf);
				framearea.add(new Area(st.createTransformedShape(shape)));
				
				st = new AffineTransform();
				sf = (double) (Math.max(size.width, size.height) - (fs * 8.0)) / Math.max(size.width, size.height);
				st.translate(fs * 4.0, fs * 4.0);
				st.scale(sf, sf);
				framearea.subtract(new Area(st.createTransformedShape(shape)));
			}
			
			if (EMPTY_FRAME_TYPE != frametype)
			{
				g.setColor(Color.BLACK);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g.fill(framearea);
				g.dispose();
			}
			
			imagecache.put(new Object[] { "frame", frametype, shape, size }, ret);
		}
		
		return ret;
	}
	
	/**
	 *  Generates a generic button shadow for a base shape.
	 *  
	 *  @param baseshape The base shape.
	 *  @return The shadow image.
	 */
	private Image generateGenericShadow(Shape baseshape, Dimension size)
	{
		AffineTransform at = new AffineTransform();
		at.scale(size.getWidth(), size.getHeight());
		Shape shape = at.createTransformedShape(baseshape);
		
		BufferedImage ret = (BufferedImage) imagecache.get(new Tuple3<String, Shape, Dimension>("shadow", shape, size));
		if (ret == null)
		{
			ret = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = ret.createGraphics();
			g.setColor(Color.DARK_GRAY);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			
//			AffineTransform st = new AffineTransform();
//			st.translate(SHADOW_SIZE, SHADOW_SIZE);
			
//			g.fill(st.createTransformedShape(shape));
			g.fill(shape);
			g.dispose();
			
			imagecache.put(new Tuple3<String, Shape, Dimension>("shadow", shape, size), ret);
		}
		
		ret = blur(ret);
		
		return ret;
	}
	
	/**
	 *  Generates a generic button glass effect for a base shape.
	 *  
	 *  @param baseshape The base shape.
	 *  @return The glass effect image.
	 */
	private Image generateGenericGlass(Shape baseshape, Dimension size)
	{
		AffineTransform at = new AffineTransform();
		at.scale(size.getWidth(), size.getHeight());
		Shape shape = at.createTransformedShape(baseshape);
		
		BufferedImage ret = (BufferedImage) imagecache.get(new Tuple3<String, Shape, Dimension>("glass", shape, size));
		
		if (ret == null)
		{
			ret = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = ((BufferedImage) ret).createGraphics();
			Shape glassarea = new Area(shape);
			((Area) glassarea).subtract(new Area(new Rectangle2D.Double(0.0, size.height * 0.5, size.width, size.height * 0.5)));
			
			AffineTransform st = new AffineTransform();
			st.translate((size.width - GLASS_SHRINK * size.width) * 0.5,
						 (size.height - GLASS_SHRINK * size.height) * 0.5);
			st.scale(GLASS_SHRINK, GLASS_SHRINK);
			
			glassarea = st.createTransformedShape(glassarea);
			Rectangle2D bounds = glassarea.getBounds2D();
			GradientPaint paint = new GradientPaint((float) bounds.getX(), (float) bounds.getY(), new Color(1.0f, 1.0f, 1.0f, 1.0f),
													(float) bounds.getX(), (float) bounds.getHeight(), new Color(1.0f, 1.0f, 1.0f, 0.0f));
			g.setPaint(paint);
			g.fill(glassarea);
			g.dispose();
			
			ret = blur(ret);
			
			imagecache.put(new Tuple3<String, Shape, Dimension>("glass", shape, size), ret);
		}
		
		return ret;
	}
	
	/**
	 *  Blurs the image.
	 *  
	 *  @param img Image to blur.
	 *  @return Blurred image.
	 */
	private BufferedImage blur(BufferedImage img)
	{
		BufferedImage tmp = new BufferedImage(img.getWidth() << 1, img.getHeight() << 1, BufferedImage.TYPE_4BYTE_ABGR_PRE);
		Graphics2D g = tmp.createGraphics();
		int w2 = img.getWidth() >>> 1;
		int h2 = img.getHeight() >>> 1;
		g.drawImage(img, w2, h2, img.getWidth() + w2, img.getHeight() + h2, 0, 0, img.getWidth(), img.getHeight(), null);
		g.dispose();
		
		tmp = BLUR_FILTER_X.filter(tmp, null);
		tmp = BLUR_FILTER_Y.filter(tmp, null);
		
		img = new BufferedImage(img.getWidth(), img.getHeight(), BufferedImage.TYPE_4BYTE_ABGR_PRE);
		g = img.createGraphics();
		g.drawImage(tmp, 0, 0, img.getWidth(), img.getHeight(), w2, h2, img.getWidth() + w2, img.getHeight() + h2, null);
		g.dispose();
		return img;
	}
	
	/**
	 *  Generates an symbol.
	 *  
	 *  @param name Name of the symbol.
	 *  @return The symbol.
	 */
	private Image generateSymbol(String name, Dimension size, Color color)
	{
		BufferedImage ret = null;
		
		if ("MessagingEdge".equals(name))
		{
			ret = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = ((BufferedImage) ret).createGraphics();
			
			double w = size.width * 0.7;
			double h = size.height * 0.7;
			double x = (size.width - w) * 0.5;
			double y = (size.height - h) * 0.5;
			double lw = size.width * 0.05;
			double aw = size.width * 0.1;
			g.setColor(color);
			Font font = iconfont.deriveFont(Math.max(size.width, size.height) * 0.45f);
			Shape s = font.createGlyphVector(g.getFontRenderContext(), "M").
					getOutline(0.0f, 0.0f);
			float sw2 = (float) (s.getBounds2D().getWidth() * 0.5);
			float sh2 = (float) (s.getBounds2D().getHeight() * 0.5);
			s = font.createGlyphVector(g.getFontRenderContext(), "M").
					getOutline((float) x + (float) w * 0.5f - sw2, (float) y + (float) h * 0.78f - sh2);
			g.fill(s);
//			g.drawLine((int) x, (int) (y + h * 0.8), (int) (x + w * 0.7), (int) (y + h * 0.8));
			GeneralPath gp = new GeneralPath();
			double lyp = y + h * 0.85;
			gp.moveTo(x, lyp);
			gp.lineTo(x + w * 0.7, lyp);
			gp.lineTo(x + w * 0.7, lyp - aw);
			gp.lineTo(x + w, lyp);
			gp.lineTo(x + w * 0.7, lyp + aw);
			gp.lineTo(x + w * 0.7, lyp);
			g.setStroke(new BasicStroke((float) lw));
			g.draw(gp);
		}
		else if ("GW_+".equals(name))
		{
			ret = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = ((BufferedImage) ret).createGraphics();
			
			g.setColor(Color.BLACK);
			g.fill(GatewayShape.getAndShape(0, 0, size.width, size.width));
		}
		else if ("add_+".equals(name))
		{
			ret = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = ((BufferedImage) ret).createGraphics();
			double offsetx = size.width * 0.1;
			double offsety = size.height * 0.1;
			double sx2 = size.width * 0.5;
			double sy2 = size.height * 0.5;
			GeneralPath gp = new GeneralPath();
			gp.moveTo(offsetx, sy2 - offsety);
			gp.lineTo(offsetx, sy2 + offsety);
			gp.lineTo(size.width - offsetx, sy2 + offsety);
			gp.lineTo(size.width - offsetx, sy2 - offsety);
			gp.closePath();
			gp.moveTo(sx2 + offsetx, offsety);
			gp.lineTo(sx2 - offsetx, offsety);
			gp.lineTo(sx2 - offsetx, size.height - offsety);
			gp.lineTo(sx2 + offsetx, size.height - offsety);
			gp.closePath();
			g.setColor(new Color(126, 229, 80));
			g.fill(gp);
		}
		else if ("remove_-".equals(name))
		{
			ret = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = ((BufferedImage) ret).createGraphics();
			double offsetx = size.width * 0.1;
			double offsety = size.height * 0.1;
			double sy2 = size.height * 0.5;
			GeneralPath gp = new GeneralPath();
			gp.moveTo(offsetx, sy2 - offsety);
			gp.lineTo(offsetx, sy2 + offsety);
			gp.lineTo(size.width - offsetx, sy2 + offsety);
			gp.lineTo(size.width - offsetx, sy2 - offsety);
			gp.closePath();
			g.setColor(new Color(126, 229, 80));
			g.fill(gp);
		}
		else if ("GW_X".equals(name))
		{
			ret = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = ((BufferedImage) ret).createGraphics();
			
			g.setColor(color);
			g.fill(GatewayShape.getXorShape(0, 0, size.width, size.height));
		}
		else if ("GW_O".equals(name))
		{
			ret = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = ((BufferedImage) ret).createGraphics();
			
			g.setColor(color);
			g.fill(GatewayShape.getOrShape(0, 0, size.width, size.height));
		}
		else if ("Pool".equals(name))
		{
			ret = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = ((BufferedImage) ret).createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setColor(Color.BLACK);
			int basex = (int) Math.round(size.width * 0.25);
			g.setStroke(new BasicStroke(THIN_FRAME_THICKNESS));
			g.drawLine(basex, 0, basex, size.height);
			Image txt = generateTextImage("P", 1.0f, size, Color.BLACK);
			basex >>>= 1;
			g.drawImage(txt, basex, 0, size.width, size.height, 0, 0, size.width - basex, size.height, null);
			g.dispose();
		}
		else if ("Lane".equals(name))
		{
			ret = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = ((BufferedImage) ret).createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setColor(Color.BLACK);
			int basex = (int) Math.round(size.width * 0.25);
			g.setStroke(new BasicStroke(THIN_FRAME_THICKNESS));
			g.drawLine(basex, 0, basex, size.height);
			Image txt = generateTextImage("L", 1.0f, size, Color.BLACK);
			basex >>>= 1;
			g.drawImage(txt, basex, 0, size.width, size.height, 0, 0, size.width - basex, size.height, null);
			g.dispose();
		}
		else if ("letter".equals(name))
		{
			ret = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = ((BufferedImage) ret).createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setColor(color);
			g.setStroke(new BasicStroke(THIN_FRAME_THICKNESS, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g.draw(EventShape.getLetterShape(0, 0, size.width, size.height));
		}
		else if ("invletter".equals(name))
		{
			ret = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = ((BufferedImage) ret).createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			Shape letter = EventShape.getLetterShape(0, 0, size.width, size.height);
			g.setColor(color);
			g.fill(letter);
			g.setStroke(new BasicStroke(THIN_FRAME_THICKNESS, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g.setComposite(AlphaComposite.Src);
			g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.0f));
			g.draw(letter);
		}
		else if ("clock".equals(name))
		{
			ret = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = ((BufferedImage) ret).createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setColor(color);
			g.setStroke(new BasicStroke(THIN_FRAME_THICKNESS, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g.draw(EventShape.getClockShape(0, 0, size.width, size.height));
		}
		else if ("page".equals(name))
		{
			ret = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = ((BufferedImage) ret).createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setColor(color);
			g.setStroke(new BasicStroke(THIN_FRAME_THICKNESS, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g.draw(EventShape.getPageShape(0, 0, size.width, size.height));
		}
		else if ("triangle".equals(name))
		{
			ret = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = ((BufferedImage) ret).createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setColor(color);
			g.setStroke(new BasicStroke(THIN_FRAME_THICKNESS, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g.draw(EventShape.getTriangleShape(0, 0, size.width, size.height));
		}
		else if ("invtriangle".equals(name))
		{
			ret = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = ((BufferedImage) ret).createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			Shape triangle = EventShape.getTriangleShape(0, 0, size.width, size.height);
			g.setColor(color);
			g.fill(triangle);
		}
		else if ("pentagon".equals(name))
		{
			ret = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = ((BufferedImage) ret).createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setColor(color);
			g.setStroke(new BasicStroke(THIN_FRAME_THICKNESS, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g.draw(EventShape.getPentagonShape(0, 0, size.width, size.height));
		}
		else if ("invpentagon".equals(name))
		{
			ret = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = ((BufferedImage) ret).createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			Shape pentagon = EventShape.getPentagonShape(0, 0, size.width, size.height);
			g.setColor(color);
			g.fill(pentagon);
		}
		else if ("bolt".equals(name))
		{
			ret = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = ((BufferedImage) ret).createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setColor(color);
			g.setStroke(new BasicStroke(THIN_FRAME_THICKNESS, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g.draw(EventShape.getBoltShape(0, 0, size.width, size.height));
		}
		else if ("invbolt".equals(name))
		{
			ret = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = ((BufferedImage) ret).createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			Shape bolt = EventShape.getBoltShape(0, 0, size.width, size.height);
			g.setColor(color);
			g.fill(bolt);
		}
		else if ("backarrows".equals(name))
		{
			ret = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = ((BufferedImage) ret).createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setColor(color);
			g.setStroke(new BasicStroke(THIN_FRAME_THICKNESS, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g.draw(EventShape.getBackArrowsShape(0, 0, size.width, size.height));
		}
		else if ("invbackarrows".equals(name))
		{
			ret = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = ((BufferedImage) ret).createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			Shape shape = EventShape.getBackArrowsShape(0, 0, size.width, size.height);
			g.setColor(color);
			g.fill(shape);
		}
		else if ("EVT_X".equals(name))
		{
			ret = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = ((BufferedImage) ret).createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			g.setColor(color);
			g.setStroke(new BasicStroke(THIN_FRAME_THICKNESS, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
			g.draw(EventShape.getXCrossShape(0, 0, size.width, size.height));
		}
		else if ("invEVT_X".equals(name))
		{
			ret = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = ((BufferedImage) ret).createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			Shape shape = EventShape.getXCrossShape(0, 0, size.width, size.height);
			g.setColor(color);
			g.fill(shape);
		}
		else if ("invcircle".equals(name))
		{
			ret = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = ((BufferedImage) ret).createGraphics();
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			Shape circle = EventShape.getCircleShape(0, 0, size.width, size.height);
			g.setColor(color);
			g.fill(circle);
		}
//		else if ("folder".equals(name))
//		{
//			ret = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
//			Graphics2D g = ((BufferedImage) ret).createGraphics();
//			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//			
//			double w = size.width * 0.7;
//			double h = size.height * 0.7;
//			double x = (size.width - w) * 0.5;
//			double y = (size.height - h) * 0.5;
//			double notchx1 = w * 0.3;
//			double notchx2 = w * 0.35;
//			double notchy = w * 0.1;
//			GeneralPath gp = new GeneralPath();
//			gp.moveTo(x, y);
//			gp.lineTo(x + notchx1, y);
//			gp.lineTo(x + notchx2, y + notchy);
//			gp.lineTo(x + w, y + notchy);
//			gp.lineTo(x + w, y + h);
//			gp.lineTo(x, y + h);
//			gp.closePath();
//			g.setColor(Color.RED);
//			g.fill(gp);
//			g.setColor(Color.BLACK);
//			g.draw(gp);
//		}
		
		return ret;
	}
	
//	private Image generateSymbol(String name, Rectangle buttonarea)
//	{
//		BufferedImage ret = null;
//		
//		if ("GW_+".equals(name))
//		{
//			ret = new BufferedImage(BASE_ICON_SIZE, BASE_ICON_SIZE, BufferedImage.TYPE_4BYTE_ABGR_PRE);
//			Graphics2D g = ((BufferedImage) ret).createGraphics();
//			
//			g.setColor(Color.BLACK);
//			g.fill(GatewayShape.getAndShape(buttonarea.x, buttonarea.y, buttonarea.width, buttonarea.height));
//		}
//		else if ("add_+".equals(name))
//		{
//			ret = new BufferedImage(BASE_ICON_SIZE, BASE_ICON_SIZE, BufferedImage.TYPE_4BYTE_ABGR_PRE);
//			Graphics2D g = ((BufferedImage) ret).createGraphics();
//			double offset = BASE_ICON_SIZE * 0.1;
//			double s2 = BASE_ICON_SIZE * 0.5;
//			GeneralPath gp = new GeneralPath();
//			gp.moveTo(offset, s2 - offset);
//			gp.lineTo(offset, s2 + offset);
//			gp.lineTo(BASE_ICON_SIZE - offset, s2 + offset);
//			gp.lineTo(BASE_ICON_SIZE - offset, s2 - offset);
//			gp.closePath();
//			gp.moveTo(s2 + offset, offset);
//			gp.lineTo(s2 - offset, offset);
//			gp.lineTo(s2 - offset, BASE_ICON_SIZE - offset);
//			gp.lineTo(s2 + offset, BASE_ICON_SIZE - offset);
//			gp.closePath();
//			g.setColor(new Color(126, 229, 80));
//			g.fill(gp);
//		}
//		else if ("remove_-".equals(name))
//		{
//			ret = new BufferedImage(BASE_ICON_SIZE, BASE_ICON_SIZE, BufferedImage.TYPE_4BYTE_ABGR_PRE);
//			Graphics2D g = ((BufferedImage) ret).createGraphics();
//			double offset = BASE_ICON_SIZE * 0.1;
//			double s2 = BASE_ICON_SIZE * 0.5;
//			GeneralPath gp = new GeneralPath();
//			gp.moveTo(offset, s2 - offset);
//			gp.lineTo(offset, s2 + offset);
//			gp.lineTo(BASE_ICON_SIZE - offset, s2 + offset);
//			gp.lineTo(BASE_ICON_SIZE - offset, s2 - offset);
//			gp.closePath();
//			g.setColor(new Color(126, 229, 80));
//			g.fill(gp);
//		}
//		else if ("GW_X".equals(name))
//		{
//			ret = new BufferedImage(BASE_ICON_SIZE, BASE_ICON_SIZE, BufferedImage.TYPE_4BYTE_ABGR_PRE);
//			Graphics2D g = ((BufferedImage) ret).createGraphics();
//			
//			g.setColor(Color.BLACK);
//			g.fill(GatewayShape.getXorShape(buttonarea.x, buttonarea.y, buttonarea.width, buttonarea.height));
//		}
//		else if ("GW_O".equals(name))
//		{
//			ret = new BufferedImage(BASE_ICON_SIZE, BASE_ICON_SIZE, BufferedImage.TYPE_4BYTE_ABGR_PRE);
//			Graphics2D g = ((BufferedImage) ret).createGraphics();
//			
//			g.setColor(Color.BLACK);
//			g.fill(GatewayShape.getOrShape(buttonarea.x, buttonarea.y, buttonarea.width, buttonarea.height));
//		}
//		else if ("Pool".equals(name))
//		{
//			ret = new BufferedImage(BASE_ICON_SIZE, BASE_ICON_SIZE, BufferedImage.TYPE_4BYTE_ABGR_PRE);
//			Graphics2D g = ((BufferedImage) ret).createGraphics();
//			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//			g.setColor(Color.BLACK);
//			int basex = (int) Math.round(buttonarea.width * 0.25);
//			g.setStroke(new BasicStroke(THIN_FRAME_THICKNESS));
//			g.drawLine(basex, buttonarea.y, basex, buttonarea.height);
//			Image txt = generateTextImage("P", 1.0f, buttonarea);
//			basex >>>= 1;
//			g.drawImage(txt, basex, buttonarea.y, BASE_ICON_SIZE, BASE_ICON_SIZE, 0, 0, BASE_ICON_SIZE - basex, BASE_ICON_SIZE, null);
//			g.dispose();
//		}
//		else if ("Lane".equals(name))
//		{
//			ret = new BufferedImage(BASE_ICON_SIZE, BASE_ICON_SIZE, BufferedImage.TYPE_4BYTE_ABGR_PRE);
//			Graphics2D g = ((BufferedImage) ret).createGraphics();
//			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//			g.setColor(Color.BLACK);
//			int basex = (int) Math.round(buttonarea.width * 0.25);
//			g.setStroke(new BasicStroke(THIN_FRAME_THICKNESS));
//			g.drawLine(basex, buttonarea.y, basex, buttonarea.height);
//			Image txt = generateTextImage("L", 1.0f, buttonarea);
//			basex >>>= 1;
//			g.drawImage(txt, basex, buttonarea.y, BASE_ICON_SIZE, BASE_ICON_SIZE, 0, 0, BASE_ICON_SIZE - basex, BASE_ICON_SIZE, null);
//			g.dispose();
//		}
//		else if ("letter".equals(name))
//		{
//			ret = new BufferedImage(BASE_ICON_SIZE, BASE_ICON_SIZE, BufferedImage.TYPE_4BYTE_ABGR_PRE);
//			Graphics2D g = ((BufferedImage) ret).createGraphics();
//			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//			g.setColor(Color.BLACK);
//			g.setStroke(new BasicStroke(THIN_FRAME_THICKNESS, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
//			g.draw(EventShape.getLetterShape(buttonarea.x, buttonarea.y, buttonarea.width, buttonarea.height));
//		}
//		else if ("invletter".equals(name))
//		{
//			ret = new BufferedImage(BASE_ICON_SIZE, BASE_ICON_SIZE, BufferedImage.TYPE_4BYTE_ABGR_PRE);
//			Graphics2D g = ((BufferedImage) ret).createGraphics();
//			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//			Shape letter = EventShape.getLetterShape(buttonarea.x, buttonarea.y, buttonarea.width, buttonarea.height);
//			g.setColor(Color.BLACK);
//			g.fill(letter);
//			g.setStroke(new BasicStroke(THIN_FRAME_THICKNESS, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
//			g.setComposite(AlphaComposite.Src);
//			g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.0f));
//			g.draw(letter);
//		}
//		else if ("clock".equals(name))
//		{
//			ret = new BufferedImage(BASE_ICON_SIZE, BASE_ICON_SIZE, BufferedImage.TYPE_4BYTE_ABGR_PRE);
//			Graphics2D g = ((BufferedImage) ret).createGraphics();
//			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//			g.setColor(Color.BLACK);
//			g.setStroke(new BasicStroke(THIN_FRAME_THICKNESS, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
//			g.draw(EventShape.getClockShape(buttonarea.x, buttonarea.y, buttonarea.width, buttonarea.height));
//		}
//		else if ("page".equals(name))
//		{
//			ret = new BufferedImage(BASE_ICON_SIZE, BASE_ICON_SIZE, BufferedImage.TYPE_4BYTE_ABGR_PRE);
//			Graphics2D g = ((BufferedImage) ret).createGraphics();
//			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//			g.setColor(Color.BLACK);
//			g.setStroke(new BasicStroke(THIN_FRAME_THICKNESS, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
//			g.draw(EventShape.getPageShape(buttonarea.x, buttonarea.y, buttonarea.width, buttonarea.height));
//		}
//		else if ("triangle".equals(name))
//		{
//			ret = new BufferedImage(BASE_ICON_SIZE, BASE_ICON_SIZE, BufferedImage.TYPE_4BYTE_ABGR_PRE);
//			Graphics2D g = ((BufferedImage) ret).createGraphics();
//			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//			g.setColor(Color.BLACK);
//			g.setStroke(new BasicStroke(THIN_FRAME_THICKNESS, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
//			g.draw(EventShape.getTriangleShape(buttonarea.x, buttonarea.y, buttonarea.width, buttonarea.height));
//		}
//		else if ("invtriangle".equals(name))
//		{
//			ret = new BufferedImage(BASE_ICON_SIZE, BASE_ICON_SIZE, BufferedImage.TYPE_4BYTE_ABGR_PRE);
//			Graphics2D g = ((BufferedImage) ret).createGraphics();
//			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//			Shape triangle = EventShape.getTriangleShape(buttonarea.x, buttonarea.y, buttonarea.width, buttonarea.height);
//			g.setColor(Color.BLACK);
//			g.fill(triangle);
//			g.setStroke(new BasicStroke(THIN_FRAME_THICKNESS, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
//			g.setComposite(AlphaComposite.Src);
//			g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.0f));
//			g.draw(triangle);
//		}
//		else if ("pentagon".equals(name))
//		{
//			ret = new BufferedImage(BASE_ICON_SIZE, BASE_ICON_SIZE, BufferedImage.TYPE_4BYTE_ABGR_PRE);
//			Graphics2D g = ((BufferedImage) ret).createGraphics();
//			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//			g.setColor(Color.BLACK);
//			g.setStroke(new BasicStroke(THIN_FRAME_THICKNESS, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
//			g.draw(EventShape.getPentagonShape(buttonarea.x, buttonarea.y, buttonarea.width, buttonarea.height));
//		}
//		else if ("invpentagon".equals(name))
//		{
//			ret = new BufferedImage(BASE_ICON_SIZE, BASE_ICON_SIZE, BufferedImage.TYPE_4BYTE_ABGR_PRE);
//			Graphics2D g = ((BufferedImage) ret).createGraphics();
//			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
//			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
//			Shape pentagon = EventShape.getPentagonShape(buttonarea.x, buttonarea.y, buttonarea.width, buttonarea.height);
//			g.setColor(Color.BLACK);
//			g.fill(pentagon);
//			g.setStroke(new BasicStroke(THIN_FRAME_THICKNESS, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
//			g.setComposite(AlphaComposite.Src);
//			g.setColor(new Color(0.0f, 0.0f, 0.0f, 0.0f));
//			g.draw(pentagon);
//		}
//		
//		return ret;
//	}
	
	/**
	 *  Renders text as image.
	 *  
	 *  @param text The text.
	 *  @param size Size of the image.
	 *  @param color Color of the text.
	 *  @return The image.
	 */
	private Image generateTextImage(String text, float scaling, Dimension size, Color color)
	{
		Image ret = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
		Graphics2D g = ((BufferedImage) ret).createGraphics();
		g.setColor(Color.BLACK);
		g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
		
		Font font = iconfont.deriveFont(BASE_ICON_SIZE * 0.5625f * scaling);
		Shape s = font.createGlyphVector(g.getFontRenderContext(), text).
				getOutline(0f, 0f);
		Rectangle b = s.getBounds();
		
		g.translate(size.width * 0.5 - b.getWidth() * 0.5 - b.getX(), size.height * 0.5 - b.getHeight() * 0.5 - b.getY());
		g.fill(s);
		g.dispose();
		
		return ret;
	}
	
	/**
	 *  Loads an image.
	 *  
	 *  @param name Name of the image.
	 *  @return The image.
	 */
	private Image loadImage(String name)
	{
		Image ret = null;
		try
		{
			ret = ImageIO.read(this.getClass().getClassLoader().getResourceAsStream(IMAGE_DIR + name));
		}
		catch (Exception e)
		{
			//throw new RuntimeException(e);
		}
		
		return ret;
	}
	
	/**
	 *  Loads a symbol.
	 *  
	 *  @param name Name of the symbol.
	 *  @return The symbol.
	 */
	private Image loadSymbol(String name, Dimension size)
	{
		Image ret = loadImage(name);
		
		if (ret != null)
		{
			double imgsymlen = Math.max(size.width, size.height) * (1.0 - IMAGE_SYMBOL_INSET_FACTOR * 2.0);
			double sf = imgsymlen / Math.max(ret.getWidth(null), ret.getHeight(null));
			ret = ret.getScaledInstance((int) Math.round(sf * ret.getWidth(null)), (int) Math.round(sf * ret.getHeight(null)), Image.SCALE_AREA_AVERAGING);
			BufferedImage tmp = new BufferedImage(size.width, size.height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = tmp.createGraphics();
			int w = ret.getWidth(null);
			int h = ret.getHeight(null);
//			int dx = buttonarea.x + ((buttonarea.width - w) >>> 1);
//			int dy = buttonarea.y + ((buttonarea.height - h) >>> 1);
			int dx = (size.width - w) >>> 1;
			int dy = (size.height - h) >>> 1;
			g.drawImage(ret, dx, dy, dx + w, dy + h, 0, 0, w, h, null);
			g.dispose();
			ret = tmp;
		}
		
		return ret;
	}
	
	/**
	 *  Loads or generates a symbol.
	 *  
	 *  @param name Name of the symbol.
	 *  @param size Size of the symbol.
	 *  @param color Color of the symbol, only works with some symbols.
	 *  @return The symbol.
	 */
	private Image getSymbol(String name, Dimension size, Color color)
	{
		Tuple3<String, Dimension, Color> key = new Tuple3<String, Dimension, Color>("symbol_" + name, size, color);
		Image ret = imagecache.get(key);
		
		if (ret == null)
		{
			ret = generateSymbol(name, size, color);
		
			if (ret == null)
			{
				ret = loadSymbol(name + ".png", size);
			}
			if (ret == null)
			{
				ret = loadSymbol(name + ".jpg", size);
			}
			if (ret == null)
			{
				ret = loadSymbol(name + ".gif", size);
			}
			if (ret == null)
			{
				String svgname = name + ".svg";
				try
				{
					InputStream svgstream = ImageProvider.class.getClassLoader().getResourceAsStream(IMAGE_DIR + svgname);
					//disable bugged svg loading, some collision between newer Java and Batik, it seems
					if (svgstream!= null)
						throw new RuntimeException("SVG loading disabled due to Batik/Java Bug.");
//					if (svgstream != null)
//					{
//						final BufferedImage[] bfimg = new BufferedImage[1];
//						ImageTranscoder tc = new ImageTranscoder()
//						{
//							public void writeImage(BufferedImage img, TranscoderOutput to)
//									throws TranscoderException
//							{
//								bfimg[0] = img;
//							}
//							
//							public BufferedImage createImage(int w, int h)
//							{
//								return new BufferedImage(w, h, BufferedImage.TYPE_4BYTE_ABGR_PRE);
//							}
//						};
//						 
//					    tc.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float) size.width);
//					    tc.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float) size.height);
//					    // Batik 1.7 bug workaround
//					    tc.addTranscodingHint(PNGTranscoder.KEY_AOI, new Rectangle2D.Float(0, 0, size.width, size.height));
//					    
//					    TranscoderInput input = new TranscoderInput(svgstream);
//					    tc.transcode(input, null);
//					    ret = bfimg[0];
//					}
				}
				catch (Exception e)
				{
					e.printStackTrace();
				}
			}
			
			if (ret == null)
			{
				ret = generateTextImage(name, 1.0f / (float) Math.sqrt(name.length()), size, color);
			}
			
			imagecache.put(name, ret);
		}
		
		return ret;
	}
	
	/**
	 *  Composite for modulation.
	 *
	 */
	protected static class ModulateComposite implements Composite
	{
		/** Modulation color */
		protected Color modcolor;
		
		/** Highlight amplification*/
		protected boolean high;
		
		/**
		 *  Creates a new Composite.
		 *  
		 *  @param modcolor The modulation color.
		 *  @param high Highlight amplification flag.
		 */
		public ModulateComposite(Color modcolor, boolean high)
		{
			this.modcolor = modcolor;
			this.high = high;
		}
		
		/**
		 *  Create the context.
		 */
		public CompositeContext createContext(ColorModel srcColorModel,
				ColorModel dstColorModel, RenderingHints hints)
		{
			return new ModulationContext(srcColorModel, dstColorModel);
		}
		
		/**
		 *  Regular "correct" modulation context.
		 *
		 */
		protected class ModulationContext implements CompositeContext
		{
			/** Source color model. */
			protected ColorModel srcColorModel;
			
			/** Destination color model. */
			protected ColorModel dstColorModel;
			
			/**
			 *  Creates the context.
			 *  
			 *  @param srcColorModel Source color model.
			 *  @param dstColorModel Destination color model.
			 */
			public ModulationContext(ColorModel srcColorModel, ColorModel dstColorModel)
			{
				this.srcColorModel = srcColorModel;
				this.dstColorModel = dstColorModel;
			}
			
			/**
			 *  Composes the image.
			 */
			public void compose(Raster src, Raster dstIn, WritableRaster dstOut)
			{
				float[] clrPx = modcolor.getComponents(null);
				
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
						tmpPx[3] *= clrPx[3];
						
						if (!high)
						{
							for (int i = 0; i < 3; ++i)
							{
								tmpPx[i] *= NON_HIGHLIGHT_DARKENING_FACTOR;
							}
						}
						
						Object outPixel = dstColorModel.getDataElements(tmpPx, 0, null);
						dstOut.setDataElements(x, y, outPixel);
					}
				}
			}
			
			/**
			 *  Disposes context.
			 */
			public void dispose()
			{
				srcColorModel = null;
				dstColorModel = null;
			}
		}
		
		/**
		 *  Accelerated modulation context, based on some assumptions
		 *  (4 color components, each 8 bits, etc.).
		 *
		 */
		protected class ModulationContextAccel implements CompositeContext
		{
			/** Source color model. */
			protected ColorModel srcColorModel;
			
			/** Destination color model. */
			protected ColorModel dstColorModel;
			
			/**
			 *  Creates the context.
			 *  
			 *  @param srcColorModel Source color model.
			 *  @param dstColorModel Destination color model.
			 */
			public ModulationContextAccel(ColorModel srcColorModel, ColorModel dstColorModel)
			{
				this.srcColorModel = srcColorModel;
				this.dstColorModel = dstColorModel;
			}
			
			/**
			 *  Composes the image.
			 */
			public void compose(Raster src, Raster dstIn, WritableRaster dstOut)
			{
				float[] clrPx = modcolor.getComponents(null);
				
				int maxX = Math.min(src.getWidth(), dstIn.getWidth());
				int maxY = Math.min(src.getHeight(), dstIn.getHeight());
				
				float amp = 1.0f;
				if (!high)
				{
					amp = NON_HIGHLIGHT_DARKENING_FACTOR;
				}
				
				for (int y = 0; y < maxY; ++y)
				{
					for (int x = 0; x < maxX; ++x)
					{
						//System.out.println(dstOut.getDataBuffer().getClass());
						Object inPixel = src.getDataElements(x, y, null);
						byte[] bPixel = (byte[]) inPixel;
						bPixel[0] = (byte) (((bPixel[0] & 0xFF) / 255.0f) * clrPx[0] * amp * 255.0f);
						bPixel[1] = (byte) (((bPixel[1] & 0xFF) / 255.0f) * clrPx[1] * amp * 255.0f);
						bPixel[2] = (byte) (((bPixel[2] & 0xFF) / 255.0f) * clrPx[2] * amp * 255.0f);
						bPixel[3] = (byte) (((bPixel[3] & 0xFF) / 255.0f) * clrPx[3] * 255.0f);
						dstOut.setDataElements(x, y, bPixel);
					}
				}
			}
			
			/**
			 *  Disposes context.
			 */
			public void dispose()
			{
				srcColorModel = null;
				dstColorModel = null;
			}
		}
	}
}
