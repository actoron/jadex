package jadex.bdi.examples.cleanerworld_env.cleaner;

import jadex.bdi.examples.cleanerworld.Chargingstation;
import jadex.bdi.examples.cleanerworld.Cleaner;
import jadex.bdi.examples.cleanerworld.Location;
import jadex.bdi.examples.cleanerworld.MapPoint;
import jadex.bdi.examples.cleanerworld.Waste;
import jadex.bdi.examples.cleanerworld.Wastebin;
import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IAgentListener;
import jadex.bdi.runtime.IExpression;
import jadex.bdi.runtime.IExternalAccess;
import jadex.bdi.runtime.IGoal;
import jadex.bridge.AgentTerminatedException;
import jadex.commons.SGUI;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;


/**
 *  The gui for the cleaner world example.
 *  Shows the world from the viewpoint of a single agent.
 */
public class CleanerGui	extends JFrame
{
	//-------- constructors --------

	/**
	 *  Shows the gui, and updates it when beliefs change.
	 */
	public CleanerGui(final IExternalAccess agent)
	{
		super(agent.getAgentName());
		final JPanel map = new JPanel()
		{
			IExpression	query_max_quantity;
			
			// overridden paint method.
			protected void	paintComponent(Graphics g)
			{
				try
				{
					//System.out.println("++++++++++++++ GUI repaint from: "+Thread.currentThread());
	
					// Get world state from beliefs.
					boolean	daytime	= ((Boolean)agent.getBeliefbase().getBelief("daytime").getFact()).booleanValue();
	
					// Paint background (dependent on daytime).
					Rectangle	bounds	= getBounds();
					g.setColor(daytime ? Color.lightGray : Color.darkGray);
					g.fillRect(0, 0, bounds.width, bounds.height);
	
					// Paint map points
					MapPoint[] mps = (MapPoint[])agent.getBeliefbase().getBeliefSet("visited_positions").getFacts();
					if(query_max_quantity==null)
						query_max_quantity	= agent.getExpressionbase().getExpression("query_max_quantity");
					double max = ((MapPoint)query_max_quantity.execute()).getQuantity();
					//int xcnt = ((int[])getBeliefbase().getBelief("???").getFact("raster"))[0];
					//int ycnt = ((int[])getBeliefbase().getBelief("???").getFact("raster"))[1];
					int xcnt = ((Integer[])agent.getBeliefbase().getBeliefSet("raster").getFacts())[0].intValue();
					int ycnt = ((Integer[])agent.getBeliefbase().getBeliefSet("raster").getFacts())[1].intValue();
					double cellh = 1/(double)ycnt;
					double cellw = 1/(double)xcnt;
					for(int i=0; i<mps.length; i++)
					{
						Point	p	= onScreenLocation(mps[i].getLocation(), bounds);
						int h = 1;
						if(max>0)
							h	= (int)(((double)mps[i].getQuantity())*cellh/max*bounds.height);
						int y = (int)(p.y+cellh/2*bounds.height-h);
						g.setColor(new Color(54, 10, 114));
						//System.out.println("h: "+h);
						g.fillRect(p.x+(int)(cellw*0.3*bounds.width), y,
							Math.max(1, (int)(cellw/10*bounds.width)), h);
					}
	
					for(int i=0; i<mps.length; i++)
					{
						Point	p	= onScreenLocation(mps[i].getLocation(), bounds);
						int	h = (int)(mps[i].getSeen()*cellh*bounds.height);
						int y = (int)(p.y+cellw/2*bounds.height-h);
						g.setColor(new Color(10, 150, 150));
						//System.out.println("h: "+h);
						g.fillRect(p.x+(int)(cellw*0.4*bounds.width), y,
							Math.max(1, (int)(cellw/10*bounds.width)), h);
					}
	
					// Paint the cleaners.
					Cleaner[] cleaners = (Cleaner[])agent.getBeliefbase().getBeliefSet("cleaners").getFacts();
					for(int i=0; i<cleaners.length; i++)
					{
						// Paint agent.
						Point	p	= onScreenLocation(cleaners[i].getLocation(), bounds);
						int w	= (int)(cleaners[i].getVisionRange()*bounds.width);
						int h	= (int)(cleaners[i].getVisionRange()*bounds.height);
						g.setColor(new Color(100, 100, 100));	// Vision
						g.fillOval(p.x-w, p.y-h, w*2, h*2);
						g.setColor(new Color(50, 50, 50, 180));
						g.fillOval(p.x-3, p.y-3, 7, 7);
						g.drawString(cleaners[i].getName(),
							p.x+5, p.y-5);
						g.drawString("battery: " + (int)(cleaners[i].getChargestate()*100.0) + "%",
							p.x+5, p.y+5);
						g.drawString("waste: " + (cleaners[i].getCarriedWaste()!=null ? "yes" : "no"),
							p.x+5, p.y+15);
					}
	
					// Draw me additionally.
					// Get world state from beliefs.
					Location	agentloc	= (Location)agent.getBeliefbase().getBelief("my_location").getFact();
					double	vision	= ((Double)agent.getBeliefbase().getBelief("my_vision").getFact()).doubleValue();
					double	charge	= ((Double)agent.getBeliefbase().getBelief("my_chargestate").getFact()).doubleValue();
					boolean	waste	= agent.getBeliefbase().getBelief("carriedwaste").getFact()!=null;
	
					// Paint agent.
					Point	p	= onScreenLocation(agentloc, bounds);
					int w	= (int)(vision*bounds.width);
					int h	= (int)(vision*bounds.height);
					g.setColor(new Color(255, 255, 64, 180));	// Vision
					g.fillOval(p.x-w, p.y-h, w*2, h*2);
					g.setColor(Color.black);	// Agent
					g.fillOval(p.x-3, p.y-3, 7, 7);
					g.drawString(agent.getAgentName(),
						p.x+5, p.y-5);
					g.drawString("battery: " + (int)(charge*100.0) + "%",
						p.x+5, p.y+5);
					g.drawString("waste: " + (waste ? "yes" : "no"),
						p.x+5, p.y+15);
	
					// Paint charge Stations.
					Chargingstation[] stations = (Chargingstation[])agent.getBeliefbase()
						.getBeliefSet("chargingstations").getFacts();
					for(int i=0; i<stations.length; i++)
					{
						g.setColor(Color.blue);
						p	= onScreenLocation(stations[i].getLocation(), bounds);
						g.drawRect(p.x-10, p.y-10, 20, 20);
						g.setColor(daytime ? Color.black : Color.white);
						g.drawString(stations[i].getName(), p.x+14, p.y+5);
					}
	
					// Paint waste bins.
					Wastebin[] wastebins = (Wastebin[])agent.getBeliefbase().getBeliefSet("wastebins").getFacts();
					for(int i=0; i<wastebins.length; i++)
					{
						g.setColor(Color.red);
						p	= onScreenLocation(wastebins[i].getLocation(), bounds);
						g.drawOval(p.x-10, p.y-10, 20, 20);
						g.setColor(daytime ? Color.black : Color.white);
						g.drawString(wastebins[i].getName()+" ("+wastebins[i].getWastes().length+"/"+wastebins[i].getCapacity()+")", p.x+14, p.y+5);
					}
	
					// Paint waste.
					Waste[] wastes = (Waste[])agent.getBeliefbase().getBeliefSet("wastes").getFacts();
					for(int i=0; i<wastes.length; i++)
					{
						g.setColor(Color.red);
						p	= onScreenLocation(wastes[i].getLocation(), bounds);
						g.fillOval(p.x-3, p.y-3, 7, 7);
					}
	
					// Paint movement targets.
					IGoal[] targets = agent.getGoalbase().getGoals("achievemoveto");
					for(int i=0; i<targets.length; i++)
					{
						g.setColor(Color.black);
						p	= onScreenLocation((Location)targets[i].getParameter("location").getValue(), bounds);
						g.drawOval(p.x-5, p.y-5, 10, 10);
						g.drawLine(p.x-7, p.y, p.x+7, p.y);
						g.drawLine(p.x, p.y-7, p.x, p.y+7);
					}
				}
				catch(AgentTerminatedException e) 
				{
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		};

		getContentPane().add(BorderLayout.CENTER, map);
		setSize(300, 300);
		setLocation(SGUI.calculateMiddlePosition(this));
		setVisible(true);
		addWindowListener(new WindowAdapter()
		{
			public void windowClosing(WindowEvent e)
			{
				agent.killAgent();
			}
		});		
		
		agent.addAgentListener(new IAgentListener()
		{
			public void agentTerminating(AgentEvent ae)
			{
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						dispose();
					}
				});
			}
			
			public void agentTerminated(AgentEvent ae)
			{
			}
		});
		
		Timer	timer	= new Timer(50, new ActionListener()
		{
			public void actionPerformed(ActionEvent e)
			{
				map.invalidate();
				map.repaint();
			}
		});
		timer.start();
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
}

