package jadex.bdiv3.quickstart.treasureisland.environment;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *  Panel for showing the treasure hunter world view.
 */
class EnvironmentPanel extends JPanel
{
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
		// Paint background (dependent on daytime).
		Rectangle	bounds	= getBounds();
		g.setColor(/*drawdata.daytime ?*/ Color.lightGray);
		g.fillRect(0, 0, bounds.width, bounds.height);

		// Paint treasures.
		for(Treasure t: env.getTreasures())
		{
			Point2D	p	= t.location;
			bounds	= getPixelUnits(new Rectangle2D.Double(p.getX()-0.01, p.getY()-0.01, 0.02, 0.02));
			g.setColor(Color.red);
			g.fillOval(bounds.x, bounds.y, bounds.width, bounds.height);
		}

		// Paint the treasure hunter.
		Point2D	p	= env.location;
		bounds	= getPixelUnits(new Rectangle2D.Double(p.getX()-0.01, p.getY()-0.01, 0.02, 0.02));
		g.setColor(Color.black);
		g.fillOval(bounds.x, bounds.y, bounds.width, bounds.height);
		g.drawString("Treasure Hunter", bounds.x+bounds.width, bounds.y);
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
}
