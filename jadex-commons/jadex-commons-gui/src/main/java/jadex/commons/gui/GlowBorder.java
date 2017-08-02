package jadex.commons.gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferByte;

import javax.swing.border.Border;

public class GlowBorder implements Border
{
	/** Border insets. */
	protected Insets insets;
	
	/** Inner color. */
	protected Color inner = Color.RED;
	
	/** Outer color. */
	protected Color outer = null;
	
	/**
	 *  Create border.
	 */
	public GlowBorder(int top, int left, int bottom, int right)
	{
		insets = new Insets(top, left, bottom, right);
	}
	
	/**
	 *  Sets inner color.
	 *  
	 *  @param inner Inner color.
	 */
	public void setInnerColor(Color inner)
	{
		this.inner = inner;
	}
	
	/**
	 *  Sets outer color.
	 *  
	 *  @param outer Outer color.
	 */
	public void setOuterColor(Color outer)
	{
		this.outer = outer;
	}
	
	/**
	 *  Sets outer color to a transparent version of the inner color.
	 *  
	 */
	public void setOuterColorTransparent()
	{
		if (inner != null)
			outer = null;
		else
			outer = new Color(1.0f, 1.0f, 1.0f, 0.0f);
	}
	
	/**
	 *  Sets inner color to a transparent version of the outer color.
	 *  
	 */
	public void setInnerColorTransparent()
	{
		if (outer != null)
			inner = null;
		else
			inner = new Color(1.0f, 1.0f, 1.0f, 0.0f);
	}
	
	/**
	 * 
	 */
	public void paintBorder(Component c, Graphics gg, int xx, int yy, int width, int height)
	{
		Graphics2D g2 = (Graphics2D) gg;
		
		Color outer = this.outer == null ? new Color(inner.getRed(), inner.getGreen(), inner.getBlue(), 0) : this.outer;
		Color inner = this.inner == null ? new Color(outer.getRed(), outer.getGreen(), outer.getBlue(), 0) : this.inner;
		
		double or = outer.getRed() / 255.0;
		double og = outer.getGreen() / 255.0;
		double ob = outer.getBlue() / 255.0;
		double oa = outer.getAlpha() / 255.0;
		double ir = inner.getRed() / 255.0;
		double ig = inner.getGreen() / 255.0;
		double ib = inner.getBlue() / 255.0;
		double ia = inner.getAlpha() / 255.0;
		double lp = insets.left;
		double rp = width - insets.right;
		double tp = insets.top;
		double bp = height - insets.bottom;
		
		BufferedImage img = new BufferedImage(width, height, BufferedImage.TYPE_4BYTE_ABGR_PRE);
		byte[] bytes = ((DataBufferByte) img.getRaster().getDataBuffer()).getData();
		for (int y = 0; y < img.getHeight(); ++y)
		{
			for (int x = 0; x < img.getWidth(); ++x)
			{
				double r, g, b, a;
				if (x > insets.left && x < width - insets.right &&
					y > insets.top && y < height - insets.bottom)
				{
					r = g = b = a = 0;
				}
				else
				{
					double dx = 0.0;
					double dy = 0.0;
					if (x < lp)
						dx = (lp - x) / insets.left;
					else if (x > rp)
						dx = (x - rp) / insets.right;
					if (y < tp)
						dy = (tp - y) / insets.top;
					else if (y > bp)
						dy = (y - bp) / insets.bottom;
//					double val = (dx + dy) * 0.5;
//					double avg  = dy;
//					double val = Math.max(dx, dy);
//					double val = (dx * dx) + (dy * dy);
					double val = Math.min(1.0, Math.pow(((dx * dx) + (dy * dy)) / 0.5, 0.5));
					double ival = 1.0 - val;
					a = oa * val + ia * ival;
					r = or * val + ir * ival;
					g = og * val + ig * ival;
					b = ob * val + ib * ival;
				}
				
				int pos = (y * width + x) << 2;
				
				bytes[pos] = (byte) (255.0 * a);
				bytes[pos + 1] = (byte) (255.0 * b);
				bytes[pos + 2] = (byte) (255.0 * g);
				bytes[pos + 3] = (byte) (255.0 * r);
			}
		}
		g2.drawImage(img, xx, yy, null);
        
		g2.dispose();
	}
	
	/**
	 * 
	 */
	public Insets getBorderInsets(Component c)
	{
		// TODO Auto-generated method stub
		return insets;
	}

	/**
	 * 
	 */
	public boolean isBorderOpaque()
	{
		return true;
	}

}
