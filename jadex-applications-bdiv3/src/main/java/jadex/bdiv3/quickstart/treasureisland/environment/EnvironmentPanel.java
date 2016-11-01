package jadex.bdiv3.quickstart.treasureisland.environment;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.util.LinkedHashSet;
import java.util.Set;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;

import jadex.commons.gui.SGUI;

/**
 *  Panel for showing the treasure hunter world view.
 */
class EnvironmentPanel extends JPanel
{
	//-------- static part --------

	/** The image icons. */
	protected static final UIDefaults	icons	= new UIDefaults(new Object[]{
		"island", SGUI.makeIcon(EnvironmentPanel.class, "images/island.png"),
		"island_treasure", SGUI.makeIcon(EnvironmentPanel.class, "images/island_treasure.png")
	});

	//-------- attributes --------
		
	/** The treasure hunter environment. */
	protected TreasureHunterEnvironment	env;
	
	/** The paint-in-progress flag. */
	protected boolean	updating;
		
	//-------- constructors --------

	/**
	 *  Create a cleaner panel.
	 */
	public EnvironmentPanel(TreasureHunterEnvironment env)
	{
		this.env	= env;
	}
	
	//-------- methods --------
	
	/**
	 *  Cause the panel to repaint.
	 */
	public void	environmentChanged()
	{
		if(!updating)
		{
			updating	= true;
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					updating	= false;
					repaint();
				}
			});
		}
	}
	
	//-------- JPanel methods --------
	
	@Override
	public Dimension getPreferredSize()
	{
		return new Dimension(800, 600);
	}

	/**
	 *  Paint the world view.
	 */
	protected void	paintComponent(Graphics g)
	{
		Rectangle	bounds	= getBounds();
		BufferedImage	img	= new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
		Graphics2D	g2	= img.createGraphics();
		g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
		
		// Paint background (dependent on daytime).
		g2.setColor(/*drawdata.daytime ?*/ Color.lightGray);
		g2.fillRect(0, 0, bounds.width, bounds.height);

		// Paint collected treasures.
		Set<Treasure>	islands;
		synchronized(env.islands)
		{
			 islands	= new LinkedHashSet(env.islands);
		}
		for(Treasure t: islands)
		{
			Point2D	p	= t.location;
			paintIcon("island", new Rectangle2D.Double(p.getX()-0.2, p.getY()-0.2, 0.4, 0.4), g2);
		}
		
		// Paint treasures.
		for(Treasure t: env.getTreasures())
		{
			Point2D	p	= t.location;
			paintIcon("island_treasure", new Rectangle2D.Double(p.getX()-0.2, p.getY()-0.2, 0.4, 0.4), g2);
		}
		
		// Paint the treasure hunter.
		Point2D	p	= env.location;
		bounds	= getPixelUnits(new Rectangle2D.Double(p.getX()-0.01, p.getY()-0.01, 0.02, 0.02));
		g2.setColor(Color.black);
		g2.fillOval(bounds.x, bounds.y, bounds.width, bounds.height);
		g2.drawString("Treasure Hunter", bounds.x+bounds.width, bounds.y);
		
		g.drawImage(img, 0, 0, this);
	}

	/**
	 *  Convert env units to pixels.
	 */
	protected Rectangle	getPixelUnits(Rectangle2D bounds)
	{
		// Calculate size of objects depending on view size.
		Dimension	size	= getSize();
		double pixelperunit	= Math.min(size.width/TreasureHunterEnvironment.WIDTH, size.height/TreasureHunterEnvironment.HEIGHT);
		return new Rectangle((int)(bounds.getX()*pixelperunit), (int)(bounds.getY()*pixelperunit), (int)(bounds.getWidth()*pixelperunit), (int)(bounds.getHeight()*pixelperunit));
	}
	
	/**
	 *  Paint an icon.
	 */
	protected void	paintIcon(String icon, Rectangle2D bounds, Graphics2D g)
	{
		Rectangle	r	= getPixelUnits(bounds);
		ImageIcon ii = (ImageIcon)icons.getIcon(icon);
		g.drawImage(ii.getImage(), r.x, r.y, r.width, r.height, this);
	}
}
