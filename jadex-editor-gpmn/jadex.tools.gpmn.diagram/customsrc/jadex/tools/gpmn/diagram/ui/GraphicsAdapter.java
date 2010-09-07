package jadex.tools.gpmn.diagram.ui;

import java.awt.BasicStroke;
import java.awt.Graphics2D;
import java.awt.Polygon;
import java.awt.Stroke;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

import org.eclipse.draw2d.Graphics;
import org.eclipse.draw2d.geometry.Point;
import org.eclipse.draw2d.geometry.PointList;
import org.eclipse.draw2d.geometry.Rectangle;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Font;
import org.eclipse.swt.graphics.FontMetrics;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.ImageData;
import org.eclipse.swt.graphics.RGB;

public class GraphicsAdapter extends Graphics
{
	private Graphics2D target;
	
	public GraphicsAdapter(Graphics2D target)
	{
		this.target = target;
	}

	@Override
	public void clipRect(Rectangle r)
	{
		target.clipRect(r.x, r.y, r.width, r.height);
	}

	@Override
	public void dispose()
	{
		target.dispose();
	}

	@Override
	public void drawArc(int x, int y, int w, int h, int offset, int length)
	{
		//TODO: Correct?
		target.drawArc(x, y, w, h, offset, offset + length);
	}

	@Override
	public void drawFocus(int x, int y, int w, int h)
	{
		Stroke oldStroke = target.getStroke();
		target.setStroke(new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 10.0f, new float[] { 1.0f, 1.0f }, 0.0f));
		target.drawRect(x, y, w, h);
		target.setStroke(oldStroke);
	}

	@Override
	public void drawImage(Image srcImage, int x, int y)
	{
		target.drawImage(convertImage(srcImage.getImageData()), x, y, null);
	}

	@Override
	public void drawImage(Image srcImage, int x1, int y1, int w1, int h1, int x2, int y2, int w2, int h2)
	{
		target.drawImage(convertImage(srcImage.getImageData()), x2, y2, x2 + w2, y2 + h2, x1, y1, x1 + w1, y1 + h1, null);
	}

	@Override
	public void drawLine(int x1, int y1, int x2, int y2)
	{
		target.drawLine(x1, y1, x2, y2);
	}

	@Override
	public void drawOval(int x, int y, int w, int h)
	{
		target.drawOval(x, y, w, h);
	}

	@Override
	public void drawPolygon(PointList points)
	{
		target.drawPolygon(convertPolygon(points));
	}

	@Override
	public void drawPolyline(PointList points)
	{
		int nPoints = points.size();
		int[] xPoints = new int[nPoints];
		int[] yPoints = new int[nPoints];
		for (int i = 0; i < nPoints; ++i)
		{
			Point p = points.getPoint(i);
			xPoints[i] = p.x;
			yPoints[i] = p.y;
		}
		target.drawPolyline(xPoints, yPoints, nPoints);
		
	}

	@Override
	public void drawRectangle(int x, int y, int width, int height)
	{
		target.drawRect(x, y, width, height);
	}

	@Override
	public void drawRoundRectangle(Rectangle r, int arcWidth, int arcHeight)
	{
		target.drawRoundRect(r.x, r.y, r.width, r.height, arcWidth, arcHeight);
	}

	@Override
	public void drawString(String s, int x, int y)
	{
		target.drawString(s, x, y);
	}

	@Override
	public void drawText(String s, int x, int y)
	{
		System.err.println("WARNING: drawText unimplemented, using drawString");
		drawString(s, x, y);
	}

	@Override
	public void fillArc(int x, int y, int w, int h, int offset, int length)
	{
		//TODO: Correct?
		target.fillArc(x, y, w, h, offset, offset + length);
	}

	@Override
	public void fillGradient(int x, int y, int w, int h, boolean vertical)
	{
		System.err.println("WARNING: fillGradient unimplemented, skipping...");
	}

	@Override
	public void fillOval(int x, int y, int w, int h)
	{
		target.fillOval(x, y, w, h);
	}

	@Override
	public void fillPolygon(PointList points)
	{
		target.fillPolygon(convertPolygon(points));
	}

	@Override
	public void fillRectangle(int x, int y, int width, int height)
	{
		target.fillRect(x, y, width, height);
	}

	@Override
	public void fillRoundRectangle(Rectangle r, int arcWidth, int arcHeight)
	{
		target.fillRoundRect(r.x, r.y, r.width, r.height, arcWidth, arcHeight);
	}

	@Override
	public void fillString(String arg0, int arg1, int arg2)
	{
		System.err.println("WARNING: fillString unimplemented, skipping...");
	}

	@Override
	public void fillText(String arg0, int arg1, int arg2)
	{
		System.err.println("WARNING: fillText unimplemented, skipping...");
	}

	@Override
	public Color getBackgroundColor()
	{
		java.awt.Color bg = target.getBackground();
		return new Color(null, bg.getRed(), bg.getGreen(), bg.getBlue());
	}

	@Override
	public Rectangle getClip(Rectangle arg0)
	{
		if (target.getClip() == null)
			return new Rectangle(new Point(Integer.MIN_VALUE, Integer.MIN_VALUE), new Point(Integer.MAX_VALUE, Integer.MAX_VALUE));
		Rectangle clip = new Rectangle();
		clip.x = target.getClip().getBounds().x;
		clip.y = target.getClip().getBounds().y;
		clip.width = target.getClip().getBounds().width;
		clip.height = target.getClip().getBounds().height;
		return clip;
	}

	@Override
	public Font getFont()
	{
		System.err.println("WARNING: getFont unimplemented, returning null...");
		return null;
	}

	@Override
	public FontMetrics getFontMetrics()
	{
		System.err.println("WARNING: getFontMetrics unimplemented, returning null...");
		return null;
	}

	@Override
	public Color getForegroundColor()
	{
		java.awt.Color fg = target.getColor();
		return new Color(null, fg.getRed(), fg.getGreen(), fg.getBlue());
	}

	@Override
	public int getLineStyle()
	{
		System.err.println("WARNING: getLineStyle unimplemented, returning null...");
		return 0;
	}

	@Override
	public int getLineWidth()
	{
		System.err.println("WARNING: getLineWidth unimplemented, returning null...");
		return 0;
	}

	@Override
	public float getLineWidthFloat()
	{
		System.err.println("WARNING: getLineWidthFloat unimplemented, returning null...");
		return 0;
	}

	@Override
	public boolean getXORMode()
	{
		return false;
	}

	@Override
	public void popState()
	{
		System.err.println("WARNING: popState unimplemented, skipping...");
	}

	@Override
	public void pushState()
	{
		System.err.println("WARNING: pushState unimplemented, skipping...");
	}

	@Override
	public void restoreState()
	{
		System.err.println("WARNING: restoreState unimplemented, skipping...");
	}

	@Override
	public void scale(double amount)
	{
		target.scale(amount, amount);
	}

	@Override
	public void setBackgroundColor(Color rgb)
	{
		target.setBackground(new java.awt.Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue()));
		
	}

	@Override
	public void setClip(Rectangle r)
	{
		target.setClip(r.x, r.y, r.width, r.height);
	}

	@Override
	public void setFont(Font arg0)
	{
		System.err.println("WARNING: setFont unimplemented, skipping...");
	}

	@Override
	public void setForegroundColor(Color rgb)
	{
		target.setColor(new java.awt.Color(rgb.getRed(), rgb.getGreen(), rgb.getBlue()));
	}

	@Override
	public void setLineMiterLimit(float arg0)
	{
		System.err.println("WARNING: setLineMiterLimit unimplemented, skipping...");
	}

	@Override
	public void setLineStyle(int arg0)
	{
		System.err.println("WARNING: setLineStyle unimplemented, skipping...");
	}

	@Override
	public void setLineWidth(int arg0)
	{
		System.err.println("WARNING: setLineWidth unimplemented, skipping...");
	}

	@Override
	public void setLineWidthFloat(float arg0)
	{
		System.err.println("WARNING: setLineWidthFloat unimplemented, skipping...");
	}

	@Override
	public void setXORMode(boolean arg0)
	{
		System.err.println("WARNING: setXORMode unimplemented, skipping...");
	}

	@Override
	public void translate(int dx, int dy) 
	{
		target.translate(dx, dy);
	}
	
	private BufferedImage convertImage(ImageData src)
	{
		BufferedImage ret = new BufferedImage(src.width, src.height, BufferedImage.TYPE_4BYTE_ABGR);
		if (src.palette.isDirect)
		{
			WritableRaster raster = ret.getRaster();
			for (int y = 0; y < src.height; ++y)
				for (int x = 0; x < src.width; ++x)
				{
					int pixel = src.getPixel(x, y);
					RGB rgb = src.palette.getRGB(pixel);
					raster.setPixel(x, y, new int[] {src.getAlpha(x, y), rgb.blue, rgb.green, rgb.red});
				}
		}
		else
		{
			RGB[] rgbs = src.palette.getRGBs();
			WritableRaster raster = ret.getRaster();
			for (int y = 0; y < src.height; ++y)
				for (int x = 0; x < src.width; ++x)
				{
					int pixel = src.getPixel(x, y);
					if (pixel != src.transparentPixel)
					{
						RGB rgb = rgbs[pixel];
						raster.setPixel(x, y, new int[] {src.getAlpha(x, y), rgb.blue, rgb.green, rgb.red});
					}
					else
						raster.setPixel(x, y, new int[] {0, 0, 0, 0});
				}
		}
		return ret;
	}
	
	private Polygon convertPolygon(PointList points)
	{
		Polygon poly = new Polygon();
		for (int i = 0; i < points.size(); ++i)
		{
			Point p = points.getPoint(i);
			poly.addPoint(p.x, p.y);
		}
		return poly;
	}
}
