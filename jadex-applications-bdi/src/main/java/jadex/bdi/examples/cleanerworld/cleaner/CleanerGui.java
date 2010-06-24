package jadex.bdi.examples.cleanerworld.cleaner;

import jadex.application.space.envsupport.environment.ISpaceObject;
import jadex.application.space.envsupport.environment.space2d.Space2D;
import jadex.application.space.envsupport.math.IVector2;
import jadex.bdi.runtime.AgentEvent;
import jadex.bdi.runtime.IAgentListener;
import jadex.bdi.runtime.IBDIExternalAccess;
import jadex.bdi.runtime.IEAExpression;
import jadex.bdi.runtime.IEAGoal;
import jadex.bridge.ComponentTerminatedException;
import jadex.commons.SGUI;
import jadex.commons.ThreadSuspendable;

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
	public CleanerGui(final IBDIExternalAccess agent)
	{
		super(agent.getComponentName());
		final JPanel map = new JPanel()
		{
			protected IEAExpression	query_max_quantity;
			protected boolean printed;
			
			// overridden paint method.
			protected void	paintComponent(Graphics g)
			{
				try
				{
					//System.out.println("++++++++++++++ GUI repaint from: "+Thread.currentThread());
					
					// As paint components is called on swing thread there is no chance to use
					// callbacks. Instead blocking calls are used.
					ThreadSuspendable sus = new ThreadSuspendable(new Object());
					
					// Get world state from beliefs.
					boolean	daytime	= ((Boolean)agent.getBeliefbase().getBeliefFact("daytime").get(sus)).booleanValue();
	
					// Paint background (dependent on daytime).
					Rectangle	bounds	= getBounds();
					g.setColor(daytime ? Color.lightGray : Color.darkGray);
					g.fillRect(0, 0, bounds.width, bounds.height);
	
					// Paint map points
					MapPoint[] mps = (MapPoint[])agent.getBeliefbase().getBeliefSetFacts("visited_positions").get(sus);
					if(query_max_quantity==null)
						query_max_quantity	= (IEAExpression)agent.getExpressionbase().getExpression("query_max_quantity").get(sus);
					double max = ((MapPoint)query_max_quantity.execute()).getQuantity();
					//int xcnt = ((int[])getBeliefbase().getBelief("???").getFact("raster"))[0];
					//int ycnt = ((int[])getBeliefbase().getBelief("???").getFact("raster"))[1];
					int xcnt = ((Integer[])agent.getBeliefbase().getBeliefSetFacts("raster").get(sus))[0].intValue();
					int ycnt = ((Integer[])agent.getBeliefbase().getBeliefSetFacts("raster").get(sus))[1].intValue();
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
					ISpaceObject[] cleaners = (ISpaceObject[])agent.getBeliefbase().getBeliefSetFacts("cleaners").get(sus);
					for(int i=0; i<cleaners.length; i++)
					{
						// Paint agent.
						Point	p	= onScreenLocation((IVector2)cleaners[i].getProperty(Space2D.PROPERTY_POSITION), bounds);
						int w	= (int)(((Double)cleaners[i].getProperty("vision_range")).doubleValue()*bounds.width);
						int h	= (int)(((Double)cleaners[i].getProperty("vision_range")).doubleValue()*bounds.height);
						g.setColor(new Color(100, 100, 100));	// Vision
						g.fillOval(p.x-w, p.y-h, w*2, h*2);
						g.setColor(new Color(50, 50, 50, 180));
						g.fillOval(p.x-3, p.y-3, 7, 7);
						g.drawString(cleaners[i].getProperty(ISpaceObject.PROPERTY_OWNER).toString(),
							p.x+5, p.y-5);
						g.drawString("battery: " + (int)(((Double)cleaners[i].getProperty("chargestate")).doubleValue()*100.0) + "%",
							p.x+5, p.y+5);
						g.drawString("waste: " + (cleaners[i].getProperty("waste")!=null ? "yes" : "no"),
							p.x+5, p.y+15);
					}
	
					// Draw me additionally.
					// Get world state from beliefs.
					IVector2 agentloc = (IVector2)agent.getBeliefbase().getBeliefFact("my_location").get(sus);
					double	vision	= ((Double)agent.getBeliefbase().getBelief("my_vision").get(sus)).doubleValue();
					double	charge	= ((Double)agent.getBeliefbase().getBelief("my_chargestate").get(sus)).doubleValue();
					boolean	waste	= ((ISpaceObject)agent.getBeliefbase().getBelief("myself").get(sus)).getProperty("waste")!=null;
	
					// Paint agent.
					Point	p	= onScreenLocation(agentloc, bounds);
					int w	= (int)(vision*bounds.width);
					int h	= (int)(vision*bounds.height);
					g.setColor(new Color(255, 255, 64, 180));	// Vision
					g.fillOval(p.x-w, p.y-h, w*2, h*2);
					g.setColor(Color.black);	// Agent
					g.fillOval(p.x-3, p.y-3, 7, 7);
					g.drawString(agent.getComponentName(),
						p.x+5, p.y-5);
					g.drawString("battery: " + (int)(charge*100.0) + "%",
						p.x+5, p.y+5);
					g.drawString("waste: " + (waste ? "yes" : "no"),
						p.x+5, p.y+15);
	
					// Paint charge Stations.
					ISpaceObject[] stations = (ISpaceObject[])agent.getBeliefbase()
						.getBeliefSetFacts("chargingstations").get(sus);
					for(int i=0; i<stations.length; i++)
					{
						g.setColor(Color.blue);
						p	= onScreenLocation((IVector2)stations[i].getProperty(Space2D.PROPERTY_POSITION), bounds);
						g.drawRect(p.x-10, p.y-10, 20, 20);
						g.setColor(daytime ? Color.black : Color.white);
						g.drawString(""+stations[i].getType(), p.x+14, p.y+5);
					}
	
					// Paint waste bins.
					ISpaceObject[] wastebins = (ISpaceObject[])agent.getBeliefbase().getBeliefSetFacts("wastebins").get(sus);
					for(int i=0; i<wastebins.length; i++)
					{
						g.setColor(Color.red);
						p	= onScreenLocation((IVector2)wastebins[i].getProperty(Space2D.PROPERTY_POSITION), bounds);
						g.drawOval(p.x-10, p.y-10, 20, 20);
						g.setColor(daytime ? Color.black : Color.white);
//						g.drawString(wastebins[i].getName()+" ("+wastebins[i].getWastes().length+"/"+wastebins[i].getCapacity()+")", p.x+14, p.y+5);
						g.drawString(""+wastebins[i].getType()+" ("+wastebins[i].getProperty("wastes")+"/"+wastebins[i].getProperty("capacity")+")", p.x+14, p.y+5);
					}
	
					// Paint waste.
					ISpaceObject[] wastes = (ISpaceObject[])agent.getBeliefbase().getBeliefSetFacts("wastes").get(sus);
					for(int i=0; i<wastes.length; i++)
					{
						g.setColor(Color.red);
						IVector2 pos = (IVector2)wastes[i].getProperty(Space2D.PROPERTY_POSITION);
						if(pos!=null)
						{
							p = onScreenLocation(pos, bounds);
							g.fillOval(p.x-3, p.y-3, 7, 7);
						}
					}
	
					// Paint movement targets.
					IEAGoal[] targets = (IEAGoal[])agent.getGoalbase().getGoals("achievemoveto").get(sus);
					for(int i=0; i<targets.length; i++)
					{
						IVector2	dest	= (IVector2)targets[i].getParameterValue("location").get(sus);
						if(dest!=null)	// Hack!!! may want to move to null due to asynchronous update of waste position.
						{
							p = onScreenLocation(dest, bounds);
							g.setColor(Color.black);
							g.drawOval(p.x-5, p.y-5, 10, 10);
							g.drawLine(p.x-7, p.y, p.x+7, p.y);
							g.drawLine(p.x, p.y-7, p.x, p.y+7);
						}
					}
				}
				catch(ComponentTerminatedException e) 
				{
				}
				catch(Exception e)
				{
					if(!printed)
					{
						System.out.println("Paint problem: "+e);
						e.printStackTrace();
					}
					printed = true;
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
	protected static Point	onScreenLocation(IVector2 loc, Rectangle bounds)
	{
		assert loc!=null;
		assert bounds!=null;
		return new Point((int)(bounds.width*loc.getXAsDouble()),
			(int)(bounds.height*(1.0-loc.getYAsDouble())));
	}
}

