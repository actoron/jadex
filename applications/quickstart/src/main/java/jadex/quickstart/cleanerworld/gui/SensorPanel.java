package jadex.quickstart.cleanerworld.gui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.lang.reflect.Field;

import javax.swing.JPanel;

import jadex.bridge.ComponentIdentifier;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.quickstart.cleanerworld.environment.IChargingstation;
import jadex.quickstart.cleanerworld.environment.ICleaner;
import jadex.quickstart.cleanerworld.environment.ILocation;
import jadex.quickstart.cleanerworld.environment.IWaste;
import jadex.quickstart.cleanerworld.environment.IWastebin;
import jadex.quickstart.cleanerworld.environment.SensorActuator;
import jadex.quickstart.cleanerworld.environment.impl.Environment;

/**
 *  Panel for showing the cleaner world view as provided by sensor.
 */
class SensorPanel extends JPanel
{
	// generated by eclipse
	private static final long serialVersionUID = 550716122862043896L;

	//-------- attributes --------
	
	/** The sensor. */
	private SensorActuator	sensor;
	
	/** The agent. */
	private IInternalAccess agent;
	
	//-------- constructors --------

	/**
	 *  Create a sensor panel.
	 */
	public SensorPanel(SensorActuator sensor)
	{
		this.sensor = sensor;
		
		// Get protected agent field.
		try
		{
			Field	fagent	= sensor.getClass().getDeclaredField("agent");
			agent	= (IInternalAccess)fagent.get(sensor);
		}
		catch(Exception e)
		{
			SUtil.throwUnchecked(e);
		}
	}
	
	//-------- JPanel methods --------

	/**
	 *  Paint the world view.
	 */
	protected void	paintComponent(Graphics g)
	{
		try
		{
			GuiData	data	= fetchGuiData();
			
			// Paint background (dependent on daytime).
			Rectangle	bounds	= getBounds();
			g.setColor(data.daytime ? Color.lightGray : Color.darkGray);
			g.fillRect(0, 0, bounds.width, bounds.height);

			// Paint the known cleaners.
			for(ICleaner cleaner: data.cleaners)
			{
				// Paint agent.
				Point	p	= onScreenLocation(cleaner.getLocation(), bounds);
				int w	= (int)(cleaner.getVisionRange()*bounds.width);
				int h	= (int)(cleaner.getVisionRange()*bounds.height);
				int colorcode	= Math.abs(cleaner.getAgentIdentifier().getParent().getLocalName().hashCode()%8);
				g.setColor(new Color((colorcode&1)!=0?255:100, (colorcode&2)!=0?255:100, (colorcode&4)!=0?255:100, 192));	// Vision range
				g.fillOval(p.x-w, p.y-h, w*2, h*2);
				g.setColor(new Color(50, 50, 50, 180));
				g.fillOval(p.x-3, p.y-3, 7, 7);
				g.drawString(cleaner.getAgentIdentifier().getLocalName(),
					p.x+5, p.y-5);
				g.drawString("battery: " + (int)(cleaner.getChargestate()*100.0) + "%",
					p.x+5, p.y+5);
				g.drawString("waste: " + (cleaner.getCarriedWaste()!=null ? "yes" : "no"),
					p.x+5, p.y+15);
			}

			// Paint agent.
			Point	p	= onScreenLocation(data.self.getLocation(), bounds);
			int w	= (int)(data.self.getVisionRange()*bounds.width);
			int h	= (int)(data.self.getVisionRange()*bounds.height);
			int colorcode	= Math.abs(ComponentIdentifier.getPlatformPrefix(data.self.getAgentIdentifier().getParent().getLocalName()).hashCode()%8);
			g.setColor(new Color((colorcode&1)!=0?255:100, (colorcode&2)!=0?255:100, (colorcode&4)!=0?255:100, 192));	// Vision range
			g.fillOval(p.x-w, p.y-h, w*2, h*2);
			g.setColor(Color.black);	// Agent
			g.fillOval(p.x-3, p.y-3, 7, 7);
			g.drawString(data.self.getAgentIdentifier().getLocalName(),
				p.x+5, p.y-5);
			g.drawString("battery: " + (int)(data.self.getChargestate()*100.0) + "%",
				p.x+5, p.y+5);
			g.drawString("waste: " + (data.self.getCarriedWaste()!=null ? "yes" : "no"),
				p.x+5, p.y+15);
			
//			// Paint pheromones.
//			for(IPheromone pheromone: data.pheromones)
//			{
//				colorcode	= Math.abs(pheromone.getType().hashCode()%8);
//				g.setColor(new Color((colorcode&1)!=0?192:0, (colorcode&2)!=0?192:0, (colorcode&4)!=0?192:0, (int)(192*pheromone.getStrength())));
//				p	= onScreenLocation(pheromone.getLocation(), bounds);
//				int size	= (int)(pheromone.getStrength()*7);
//				g.fillOval(p.x-size, p.y-size, size*2+1, size*2+1);
//			}

			// Paint charge stations.
			for(IChargingstation station: data.stations)
			{
				g.setColor(Color.blue);
				p	= onScreenLocation(station.getLocation(), bounds);
				g.drawRect(p.x-10, p.y-10, 20, 20);
				g.setColor(data.daytime ? Color.black : Color.white);
				g.drawString(station.getId(), p.x+14, p.y+5);
			}

			// Paint waste bins.
			for(IWastebin bin: data.wastebins)
			{
				g.setColor(Color.red);
				p = onScreenLocation(bin.getLocation(), bounds);
				g.drawOval(p.x-10, p.y-10, 20, 20);
				g.setColor(data.daytime ? Color.black : Color.white);
				g.drawString(bin.getId()+" ("+bin.getWastes().length+"/"+bin.getCapacity()+")", p.x+14, p.y+5);
			}

			// Paint waste.
			for(IWaste waste: data.wastes)
			{
				g.setColor(Color.red);
				p	= onScreenLocation(waste.getLocation(), bounds);
				g.fillOval(p.x-3, p.y-3, 7, 7);
			}

			// Paint movement target.
			if(data.target!=null)
			{
				g.setColor(Color.black);
				p = onScreenLocation(data.target, bounds);
				g.drawOval(p.x-5, p.y-5, 10, 10);
				g.drawLine(p.x-7, p.y, p.x+7, p.y);
				g.drawLine(p.x, p.y-7, p.x, p.y+7);
			}
		}
		catch(ComponentTerminatedException e)
		{	
		}
	}
	
	//-------- helper methods --------

	/**
	 *  Get the on screen location for a location in  the world.
	 */
	private static Point	onScreenLocation(ILocation loc, Rectangle bounds)
	{
		assert loc!=null;
		assert bounds!=null;
		return new Point((int)(bounds.width*loc.getX()),
			(int)(bounds.height*(1.0-loc.getY())));
	}
	
	/**
	 *  Fetch gui data from agent.
	 */
	private GuiData	fetchGuiData()
	{
		// Read sensor data on agent thread to avoid inconsistencies/conflicts.
		return agent.getExternalAccess().scheduleStep(new IComponentStep<GuiData>()
		{
			@Override
			public IFuture<GuiData> execute(IInternalAccess ia)
			{
				GuiData	ret	= new GuiData();
				ret.self	= sensor.getSelf();
				ret.target	= sensor.getTarget();
				ret.daytime	= sensor.isDaytime();
				ret.cleaners	= Environment.cloneList(sensor.getCleaners(), ICleaner.class);
				ret.wastes	= Environment.cloneList(sensor.getWastes(), IWaste.class);
				ret.stations	= Environment.cloneList(sensor.getChargingstations(), IChargingstation.class);
				ret.wastebins	= Environment.cloneList(sensor.getWastebins(), IWastebin.class);
//				ret.pheromones	= Environment.cloneList(sensor.getPheromones(), IPheromone.class);
				return new Future<>(ret);
			}
		}).get();
	}
	
	/**
	 *  Data transfer object from agent thread to gui thread.
	 */
	private static class GuiData
	{
		/** The cleaner. */
		public ICleaner	self;
		
		/** The current movement target, if any. */
		public ILocation	target;
		
		/** The daytime flag. */
		public boolean	daytime;
		
		/** The known other cleaners. */
		public ICleaner[]	cleaners;
		
		/** The known waste pieces. */
		public IWaste[]	wastes;
		
		/** The known charging stations. */
		public IChargingstation[]	stations;
		
		/** The known waste bins. */
		public IWastebin[]	wastebins;
		
//		/** The perceived pheromones. */
//		public IPheromone[]	pheromones;
	}
}
