package jadex.bdiv3.quickstart.treasureisland.environment;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;

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
		return new Dimension(env.width, env.height);
	}

	/**
	 *  Paint the world view.
	 */
	protected void	paintComponent(Graphics g)
	{
		// Calculate size of objects depending on view size.
		int	objectsize	= Math.min(env.width, env.height)/50;
		
		// Paint background (dependent on daytime).
		Rectangle	bounds	= getBounds();
		g.setColor(/*drawdata.daytime ?*/ Color.lightGray);
		g.fillRect(0, 0, bounds.width, bounds.height);

		// Paint the treasure hunter.
		Point	p	= env.location;
		g.setColor(Color.black);
		g.fillOval(p.x-objectsize/2, p.y-objectsize/2, objectsize, objectsize);
		g.drawString("Treasure Hunter", p.x+objectsize, p.y-objectsize);
//		g.drawString("battery: " + (int)(drawdata.my_chargestate*100.0) + "%",
//			p.x+5, p.y+5);
//		g.drawString("waste: " + (drawdata.my_waste ? "yes" : "no"),
//			p.x+5, p.y+15);

//		// Paint charge Stations.
//		for(int i=0; i<drawdata.chargingstations.length; i++)
//		{
//			g.setColor(Color.blue);
//			p	= onScreenLocation(drawdata.chargingstations[i].getLocation(), bounds);
//			g.drawRect(p.x-10, p.y-10, 20, 20);
//			g.setColor(drawdata.daytime ? Color.black : Color.white);
//			g.drawString(drawdata.chargingstations[i].getName(), p.x+14, p.y+5);
//		}
//
//		// Paint waste bins.
//		for(int i=0; i<drawdata.wastebins.length; i++)
//		{
//			g.setColor(Color.red);
//			p = onScreenLocation(drawdata.wastebins[i].getLocation(), bounds);
//			g.drawOval(p.x-10, p.y-10, 20, 20);
//			g.setColor(drawdata.daytime ? Color.black : Color.white);
//			g.drawString(drawdata.wastebins[i].getName()+" ("+drawdata.wastebins[i].getWastes().length+"/"+drawdata.wastebins[i].getCapacity()+")", p.x+14, p.y+5);
//		}
//
		// Paint treasures.
		for(Treasure t: env.getTreasures())
		{
			p	= t.location;
			int size	= objectsize*t.weight/10;
			g.setColor(Color.red);
			g.fillOval(p.x-size/2, p.y-size/2, size, size);
		}
//
//		// Paint movement targets.
//		for(int i=0; i<drawdata.dests.length; i++)
//		{
//			g.setColor(Color.black);
//			p = onScreenLocation((Location)drawdata.dests[i], bounds);
//			g.drawOval(p.x-5, p.y-5, 10, 10);
//			g.drawLine(p.x-7, p.y, p.x+7, p.y);
//			g.drawLine(p.x, p.y-7, p.x, p.y+7);
//		}
	}	
}
