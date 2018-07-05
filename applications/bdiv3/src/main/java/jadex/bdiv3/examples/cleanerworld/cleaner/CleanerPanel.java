package jadex.bdiv3.examples.cleanerworld.cleaner;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Collection;
import java.util.Iterator;

import javax.swing.JPanel;

import jadex.bdiv3.examples.cleanerworld.world.Chargingstation;
import jadex.bdiv3.examples.cleanerworld.world.Cleaner;
import jadex.bdiv3.examples.cleanerworld.world.Location;
import jadex.bdiv3.examples.cleanerworld.world.MapPoint;
import jadex.bdiv3.examples.cleanerworld.world.Waste;
import jadex.bdiv3.examples.cleanerworld.world.Wastebin;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IPojoComponentFeature;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.future.SwingDefaultResultListener;

/**
 *  Panel for showing the cleaner world view.
 */
class CleanerPanel extends JPanel
{
	//-------- attributes --------
	
	/** The cleaner agent. */
	protected IExternalAccess	agent;
		
	/** The latest world view information. */
	protected DrawData	drawdata;
		
	/** Flag to indicate that the draw data is currently updated to avoid multiple updates in parallel. */
	protected boolean	updating;
		
	//-------- constructors --------

	/**
	 *  Create a cleaner panel.
	 */
	public CleanerPanel(IExternalAccess agent)
	{
		this.agent = agent;
	}
	
	//-------- JPanel methods --------

	/**
	 *  Paint the world view.
	 */
	protected void	paintComponent(Graphics g)
	{
		if(!updating)
		{
			updating	= true;
			try
			{
				IFuture	fut	= agent.scheduleStep(new UpdateStep());
				fut.addResultListener(new SwingDefaultResultListener()
				{
					public void customResultAvailable(Object result)
					{
						CleanerPanel.this.drawdata	= (DrawData)result;
						updating	= false;
					}
					public void customExceptionOccurred(Exception exception)
					{
//						exception.printStackTrace();
//						updating	= false;	// Keep to false to disable any more updates
					}
				});
			}
			catch(ComponentTerminatedException e) 
			{
				// Keep updating to false to disable any more updates
			}
		}
		
		if(drawdata!=null)
		{
			// Paint background (dependent on daytime).
			Rectangle	bounds	= getBounds();
			g.setColor(drawdata.daytime ? Color.lightGray : Color.darkGray);
			g.fillRect(0, 0, bounds.width, bounds.height);

			// Paint map points
			double cellh = 1/(double)drawdata.ycnt;
			double cellw = 1/(double)drawdata.xcnt;
			for(int i=0; i<drawdata.visited_positions.length; i++)
			{
				Point	p	= onScreenLocation(drawdata.visited_positions[i].getLocation(), bounds);
				int h = 1;
				if(drawdata.max_quantity>0)
					h	= (int)(((double)drawdata.visited_positions[i].getQuantity())*cellh/drawdata.max_quantity*bounds.height);
				int y = (int)(p.y+cellh/2*bounds.height-h);
				g.setColor(new Color(54, 10, 114));
				g.setColor(Color.GREEN);
				//System.out.println("h: "+h);
				g.fillRect(p.x+(int)(cellw*0.3*bounds.width), y,
					Math.max(1, (int)(cellw/10*bounds.width)), h);
			}

			for(int i=0; i<drawdata.visited_positions.length; i++)
			{
				Point	p	= onScreenLocation(drawdata.visited_positions[i].getLocation(), bounds);
				int	h = (int)(drawdata.visited_positions[i].getSeen()*cellh*bounds.height);
				int y = (int)(p.y+cellw/2*bounds.height-h);
				g.setColor(new Color(10, 150, 150));
				//System.out.println("h: "+h);
				g.fillRect(p.x+(int)(cellw*0.4*bounds.width), y,
					Math.max(1, (int)(cellw/10*bounds.width)), h);
			}

			// Paint the cleaners.
			for(int i=0; i<drawdata.cleaners.length; i++)
			{
				// Paint agent.
				Point	p	= onScreenLocation(drawdata.cleaners[i].getLocation(), bounds);
				int w	= (int)(drawdata.cleaners[i].getVisionRange()*bounds.width);
				int h	= (int)(drawdata.cleaners[i].getVisionRange()*bounds.height);
				g.setColor(new Color(100, 100, 100));	// Vision
				g.fillOval(p.x-w, p.y-h, w*2, h*2);
				g.setColor(new Color(50, 50, 50, 180));
				g.fillOval(p.x-3, p.y-3, 7, 7);
				g.drawString(drawdata.cleaners[i].getName(),
					p.x+5, p.y-5);
				g.drawString("battery: " + (int)(drawdata.cleaners[i].getChargestate()*100.0) + "%",
					p.x+5, p.y+5);
				g.drawString("waste: " + (drawdata.cleaners[i].getCarriedWaste()!=null ? "yes" : "no"),
					p.x+5, p.y+15);
			}

			// Paint agent.
			Point	p	= onScreenLocation(drawdata.my_location, bounds);
			int w	= (int)(drawdata.my_vision*bounds.width);
			int h	= (int)(drawdata.my_vision*bounds.height);
			g.setColor(new Color(255, 255, 64, 180));	// Vision
			g.fillOval(p.x-w, p.y-h, w*2, h*2);
			g.setColor(Color.black);	// Agent
			g.fillOval(p.x-3, p.y-3, 7, 7);
			g.drawString(agent.getIdentifier().getLocalName(),
				p.x+5, p.y-5);
			g.drawString("battery: " + (int)(drawdata.my_chargestate*100.0) + "%",
				p.x+5, p.y+5);
			g.drawString("waste: " + (drawdata.my_waste ? "yes" : "no"),
				p.x+5, p.y+15);

			// Paint charge Stations.
			for(int i=0; i<drawdata.chargingstations.length; i++)
			{
				g.setColor(Color.blue);
				p	= onScreenLocation(drawdata.chargingstations[i].getLocation(), bounds);
				g.drawRect(p.x-10, p.y-10, 20, 20);
				g.setColor(drawdata.daytime ? Color.black : Color.white);
				g.drawString(drawdata.chargingstations[i].getName(), p.x+14, p.y+5);
			}

			// Paint waste bins.
			for(int i=0; i<drawdata.wastebins.length; i++)
			{
				g.setColor(Color.red);
				p = onScreenLocation(drawdata.wastebins[i].getLocation(), bounds);
				g.drawOval(p.x-10, p.y-10, 20, 20);
				g.setColor(drawdata.daytime ? Color.black : Color.white);
				g.drawString(drawdata.wastebins[i].getName()+" ("+drawdata.wastebins[i].getWastes().length+"/"+drawdata.wastebins[i].getCapacity()+")", p.x+14, p.y+5);
			}

			// Paint waste.
			for(int i=0; i<drawdata.wastes.length; i++)
			{
				g.setColor(Color.red);
				p	= onScreenLocation(drawdata.wastes[i].getLocation(), bounds);
				g.fillOval(p.x-3, p.y-3, 7, 7);
			}

			// Paint movement targets.
			for(int i=0; i<drawdata.dests.length; i++)
			{
				g.setColor(Color.black);
				p = onScreenLocation((Location)drawdata.dests[i], bounds);
				g.drawOval(p.x-5, p.y-5, 10, 10);
				g.drawLine(p.x-7, p.y, p.x+7, p.y);
				g.drawLine(p.x, p.y-7, p.x, p.y+7);
			}
		}
	}
	
	//-------- helper methods --------

	/**
	 *  Get the on screen location for a location in  the world.
	 */
	protected static Point	onScreenLocation(Location loc, Rectangle bounds)
	{
		assert loc!=null;
		assert bounds!=null;
		return new Point((int)(bounds.width*loc.getX()),
			(int)(bounds.height*(1.0-loc.getY())));
	}
	
	//-------- helper classes --------
	
	/**
	 *  Component step to produce an uptodate draw data.
	 */
	public static class UpdateStep implements IComponentStep<DrawData>
	{
		public IFuture<DrawData> execute(IInternalAccess ia)
		{
			CleanerBDI cleaner = (CleanerBDI)ia.getFeature(IPojoComponentFeature.class).getPojoAgent();
			DrawData	drawdata	= new DrawData();
			drawdata.daytime = cleaner.isDaytime();
			drawdata.visited_positions = cleaner.getVisitedPositions().toArray(new MapPoint[0]);
			drawdata.max_quantity = cleaner.getMaxQuantity().get(0).getQuantity();
			drawdata.xcnt = cleaner.getRaster().getFirstEntity();
			drawdata.ycnt = cleaner.getRaster().getSecondEntity();
			drawdata.cleaners = cleaner.getCleaners().toArray(new Cleaner[0]);
			drawdata.chargingstations = cleaner.getChargingStations().toArray(new Chargingstation[0]);
			drawdata.wastebins = cleaner.getWastebins().toArray(new Wastebin[0]);
			drawdata.wastes = cleaner.getWastes().toArray(new Waste[0]);
			drawdata.my_vision = cleaner.getMyVision();
			drawdata.my_chargestate = cleaner.getMyChargestate();
			drawdata.my_location = cleaner.getMyLocation();
			drawdata.my_waste = cleaner.getCarriedWaste()!=null;
			Collection<CleanerBDI.AchieveMoveTo> goals = cleaner.getAgent().getFeature(IBDIAgentFeature.class).getGoals(CleanerBDI.AchieveMoveTo.class);
			drawdata.dests = new Location[goals.size()];
			Iterator<CleanerBDI.AchieveMoveTo>	goalit	= goals.iterator();
			for(int i=0; i<goals.size(); i++)
			{
				drawdata.dests[i] = goalit.next().getLocation();
			}
			return new Future<DrawData>(drawdata);
		}
	}

	/**
	 *  Data for drawing.
	 */
	public static class DrawData
	{
		// Allow object being transferred as XML using public fields.
		public static boolean XML_INCLUDE_FIELDS = true;
		
		public boolean daytime;
		public MapPoint[] visited_positions = new MapPoint[0];
		public double max_quantity;
		public int xcnt;
		public int ycnt;
		public Cleaner[] cleaners;
		public Location my_location;
		public double my_vision;
		public double my_chargestate;
		public boolean my_waste;
		public Chargingstation[] chargingstations;
		public Wastebin[] wastebins;
		public Waste[] wastes;
		public Location[] dests;
	}
}
