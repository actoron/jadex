package jadex.gpmn.editor.gui;

import jadex.gpmn.editor.gui.SGuiHelper.ModulateComposite;
import jadex.gpmn.editor.gui.stylesheets.GpmnStylesheetColor;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.geom.Ellipse2D;
import java.awt.geom.RoundRectangle2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;

/**
 *  Loader class for images with a cache.
 *
 */
public class ImageLoader
{
	/** The image directory */
	protected static final String IMAGE_DIR = "/" + ImageLoader.class.getPackage().getName().replaceAll("\\.", "/") + "/images/";
	
	/** The image cache. */
	protected Map<String, Image> imagecache = new HashMap<String, Image>();
	
	/** The generated image cache. */
	protected Map<String, Image> genimagecache = new HashMap<String, Image>();
	
	/** The rendered text cache. */
	protected Map<String, Image> textimagecache = new HashMap<String, Image>();
	
	/** The icon font used. */
	protected Font iconfont;
	
	public ImageLoader()
	{
		try
		{
			InputStream fontis = GpmnStylesheetColor.class.getClassLoader().getResourceAsStream("jadex/gpmn/editor/gui/fonts/VeraBd.ttf");
			Font font = Font.createFont(Font.TRUETYPE_FONT, fontis);
			//iconfont = font;
			iconfont = font.deriveFont(144.0f);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Generates a circular-shaped image icon.
	 *  
	 * 	@param symbol Symbol name or text.
	 *  @param bgcolor Color of the icon
	 *  @param symbolic If false, interpret symbol as text.
	 *  @param high Highlight the icon if true.
	 *  @param shift Shift (for pressed status) if true.
	 *  @return The generated image icon.
	 */
	public ImageIcon generateCircularImageIcon(String symbol, Color bgcolor, boolean symbolic, boolean high, boolean shift)
	{
		Image symimg = null;
		if (symbolic)
		{
			symimg = getImage(symbol);
		}
		else
		{
			symimg = generateTextImage(symbol);
		}
		Image frame = getImage("circle");
		Image glass = getImage("circleglass");
		Image bgshape = getImage("bgcircle");
		Image shadow = null;
		if (!shift)
		{
			shadow = getImage("circleshadow");
		}
		
		return compositeImageIcon(symimg, bgshape, frame, glass, shadow, bgcolor, high);
	}
	
	/**
	 *  Generates a rectangular-shaped image icon.
	 *  
	 * 	@param symbol Symbol name or text.
	 *  @param bgcolor Color of the icon
	 *  @param symbolic If false, interpret symbol as text.
	 *  @param high Highlight the icon if true.
	 *  @param shift Shift (for pressed status) if true.
	 *  @return The generated image icon.
	 */
	public ImageIcon generateRectangularImageIcon(String text, Color bgcolor, boolean symbolic, boolean high, boolean shift)
	{
		Image symimg = null;
		if (symbolic)
		{
			symimg = getImage(text);
		}
		else
		{
			symimg = generateTextImage(text);
		}
		Image frame = getImage("rrect");
		Image glass = getImage("rrectglass");
		Image bgshape = getImage("bgrrect");
		Image shadow = null;
		if (!shift)
		{
			shadow = getImage("rrectshadow");
		}
		
		return compositeImageIcon(symimg, bgshape, frame, glass, shadow, bgcolor, high);
	}
	
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
	public ImageIcon compositeImageIcon(Image symbol, Image bgshape, Image frame, Image glass, Image shadow, Color bgcolor, boolean high)
	{
		BufferedImage ret = null;
		BufferedImage tmpimg = new BufferedImage(GuiConstants.BASE_ICON_SIZE, GuiConstants.BASE_ICON_SIZE, BufferedImage.TYPE_4BYTE_ABGR_PRE);
		Graphics2D g = tmpimg.createGraphics();
		g.setComposite(new ModulateComposite(bgcolor, high));
		g.drawImage(bgshape, 0, 0, GuiConstants.BASE_ICON_SIZE, GuiConstants.BASE_ICON_SIZE, null);
		g.dispose();
		bgshape = tmpimg;
		
		Image full = new BufferedImage(GuiConstants.BASE_ICON_SIZE, GuiConstants.BASE_ICON_SIZE, BufferedImage.TYPE_4BYTE_ABGR_PRE);
		g = ((BufferedImage) full).createGraphics();
		g.setComposite(AlphaComposite.SrcOver);
		
		int x = 0;
		int y = 0;
		if (shadow == null)
		{
			x = 16;
			y = 16;
		}
		else
		{
			g.drawImage(shadow, x, y, GuiConstants.BASE_ICON_SIZE, GuiConstants.BASE_ICON_SIZE, null);
		}
		
		g.drawImage(bgshape, x, y, GuiConstants.BASE_ICON_SIZE, GuiConstants.BASE_ICON_SIZE, null);
		g.setComposite(AlphaComposite.SrcOver);
		g.drawImage(frame, x, y, GuiConstants.BASE_ICON_SIZE, GuiConstants.BASE_ICON_SIZE, null);
		g.drawImage(glass, x, y, GuiConstants.BASE_ICON_SIZE, GuiConstants.BASE_ICON_SIZE, null);
		g.drawImage(symbol, x, y, GuiConstants.BASE_ICON_SIZE, GuiConstants.BASE_ICON_SIZE, null);
		g.dispose();
		bgshape = null;
		
		full = full.getScaledInstance(GuiConstants.ICON_SIZE, GuiConstants.ICON_SIZE, Image.SCALE_AREA_AVERAGING);
		ret = new BufferedImage(GuiConstants.ICON_SIZE, GuiConstants.ICON_SIZE, BufferedImage.TYPE_4BYTE_ABGR_PRE);
		g = ret.createGraphics();
		g.setComposite(AlphaComposite.Src);
		g.drawImage(full, 0, 0, GuiConstants.ICON_SIZE, GuiConstants.ICON_SIZE, null);
		g.dispose();
		return new ImageIcon(ret);
	}
	
	/**
	 *  Generates an image.
	 *  
	 *  @param name Name of the image.
	 *  @return The image.
	 */
	private Image generateImage(String name)
	{
		Image ret = genimagecache.get(name);
		
		if (ret == null)
		{
			if ("circle".equals(name))
			{
				ret = new BufferedImage(256, 256, BufferedImage.TYPE_4BYTE_ABGR_PRE);
				Graphics2D g = ((BufferedImage) ret).createGraphics();
				g.setStroke(new BasicStroke(12.0f));
				g.setColor(Color.BLACK);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g.draw(new Ellipse2D.Double(12, 12, 220, 220));
				g.dispose();
			}
			else if ("bgcircle".equals(name))
			{
				ret = new BufferedImage(256, 256, BufferedImage.TYPE_4BYTE_ABGR_PRE);
				Graphics2D g = ((BufferedImage) ret).createGraphics();
				g.setColor(Color.WHITE);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g.fill(new Ellipse2D.Double(12, 12, 220, 220));
				g.dispose();
			}
			else if ("rrect".equals(name))
			{
				ret = new BufferedImage(256, 256, BufferedImage.TYPE_4BYTE_ABGR_PRE);
				Graphics2D g = ((BufferedImage) ret).createGraphics();
				g.setStroke(new BasicStroke(12.0f));
				g.setColor(Color.BLACK);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g.draw(new RoundRectangle2D.Double(12, 12, 220, 220, 128, 128));
				g.dispose();
			}
			else if ("bgrrect".equals(name))
			{
				ret = new BufferedImage(256, 256, BufferedImage.TYPE_4BYTE_ABGR_PRE);
				Graphics2D g = ((BufferedImage) ret).createGraphics();
				g.setColor(Color.WHITE);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				g.fill(new RoundRectangle2D.Double(12, 12, 220, 220, 128, 128));
				g.dispose();
			}
			
			genimagecache.put(name, ret);
		}
		
		return ret;
	}
	
	/**
	 *  Renders text as image.
	 *  
	 *  @param text The text.
	 *  @return The image.
	 */
	private Image generateTextImage(String text)
	{
		Image ret = textimagecache.get(text);
		
		if (ret == null)
		{
			ret = new BufferedImage(GuiConstants.BASE_ICON_SIZE, GuiConstants.BASE_ICON_SIZE, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = ((BufferedImage) ret).createGraphics();
			g.setColor(Color.BLACK);
			g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
			
			Shape s = iconfont.createGlyphVector(g.getFontRenderContext(), text).
					getOutline(0f, 0f);
			Rectangle b = s.getBounds();
			
			g.translate(122.0 - b.getWidth() * 0.5 - b.getX(), 122.0 - b.getHeight() * 0.5 - b.getY());
			g.fill(s);
			g.dispose();
			
			textimagecache.put(text, ret);
		}
		
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
		Image ret = imagecache.get(name);
		
		if (ret == null)
		{
			try
			{
				ret = ImageIO.read(SGuiHelper.class.getResource(IMAGE_DIR + name));
				imagecache.put(name, ret);
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Loads or generates an image.
	 *  
	 *  @param name Name of the image.
	 *  @return The image.
	 */
	protected Image getImage(String name)
	{
		Image ret = generateImage(name);
		if (ret == null)
		{
			ret = loadImage(name + ".png");
		}
		
		return ret;
	}
	
	/**
	 *  Gets a specific image icon.
	 *  
	 *  @param filename name of the image file.
	 *  @return Image icon.
	 */
	public ImageIcon getImageIcon(String filename)
	{
		BufferedImage ret = null;
		try
		{
			Image orig = ImageIO.read(SGuiHelper.class.getResource(IMAGE_DIR + filename));
			orig = orig.getScaledInstance(GuiConstants.ICON_SIZE, GuiConstants.ICON_SIZE, Image.SCALE_AREA_AVERAGING);
			ret = new BufferedImage(GuiConstants.ICON_SIZE, GuiConstants.ICON_SIZE, BufferedImage.TYPE_4BYTE_ABGR_PRE);
			Graphics2D g = ret.createGraphics();
			g.setComposite(AlphaComposite.Src);
			g.drawImage(orig, 0, 0, GuiConstants.ICON_SIZE, GuiConstants.ICON_SIZE, null);
			g.dispose();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
		return new ImageIcon(ret);
	}
}
