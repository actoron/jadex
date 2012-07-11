package jadex.gpmn.editor.gui;

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
	 *  Loads an image.
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
			else if (name.startsWith("tooltext_"))
			{
				ret = new BufferedImage(256, 256, BufferedImage.TYPE_4BYTE_ABGR_PRE);
				Graphics2D g = ((BufferedImage) ret).createGraphics();
				g.setColor(Color.BLACK);
				g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
				g.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
				
				String text = name.substring(9);
				
				Shape s = iconfont.createGlyphVector(g.getFontRenderContext(), text).
						getOutline(0f, 0f);
				Rectangle b = s.getBounds();
				
				g.translate(122.0 - b.getWidth() * 0.5 - b.getX(), 122.0 - b.getHeight() * 0.5 - b.getY());
				g.fill(s);
				g.dispose();
			}
			
			genimagecache.put(name, ret);
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
	protected Image generateOrLoadImage(String name)
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
