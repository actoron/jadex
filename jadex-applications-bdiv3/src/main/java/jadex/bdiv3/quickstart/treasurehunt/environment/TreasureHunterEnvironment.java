package jadex.bdiv3.quickstart.treasurehunt.environment;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Closeable;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.impl.IInternalExecutionFeature;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;

/**
 *  The treasure hunter world representation.
 */
public class TreasureHunterEnvironment	implements Closeable
{
	//-------- attributes --------
	
	/** The random number generator. */
	protected Random	rnd;
	
	/** The environment width. */
	protected int	width;
	
	/** The environment height. */
	protected int	height;
	
	/** The treasure hunter location. */
	protected Point	location;
	
	/** The treasures. */
	protected Set<Treasure>	treasures;
	
	/** The gui. */
	protected EnvironmentPanel	panel;
	
	//-------- constructors --------
	
	/**
	 *  Create a treasure hunter world of given size.
	 *  @param width	The width (in pixels).
	 *  @param height	The height (in pixels).
	 */
	public TreasureHunterEnvironment(int width, int height)
	{
		this.rnd	= new Random(1);
		this.width	= width;
		this.height	= height;
		this.location	= new Point(rnd.nextInt(width), rnd.nextInt(height));
		this.treasures	= Collections.synchronizedSet(new LinkedHashSet<Treasure>());
		for(int i=1; i<=10; i++)
		{
			treasures.add(Treasure.create(rnd, width, height));
		}
		
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				panel	= new EnvironmentPanel(TreasureHunterEnvironment.this);
				JFrame	window	= new JFrame("Jadex Treasure Hunter World");
				window.getContentPane().add(BorderLayout.CENTER, panel);
				window.pack();
				window.setVisible(true);
			}
		});
		
		// Kill component on window close
		IInternalAccess	comp	= IInternalExecutionFeature.LOCAL.get();
		if(comp!=null)
		{
			final IExternalAccess	ext	= comp.getExternalAccess();
			SwingUtilities.invokeLater(new Runnable()
			{
				@Override
				public void run()
				{
					SGUI.getWindowParent(panel)
						.addWindowListener(new WindowAdapter()
					{
						@Override
						public void windowClosing(WindowEvent e)
						{
							ext.killComponent();
						}
					});
				}
			});
		}
	}
	
	//-------- environment access methods --------

	/**
	 *  Get the treasure hunter location.
	 */
	public Point	getHunterLocation()
	{
		// Return copy to prevent manipulation of original location from agent. 
		return new Point(location.x, location.y);
	}
	
	/**
	 *  Get the current treasures.
	 *  @return A copy of the current treasures.
	 */
	public Set<Treasure>	getTreasures()
	{
		// Return a copy to prevent manipulation of treasure set from agent and also avoid ConcurrentModificationException.
		synchronized(treasures)
		{
			return new LinkedHashSet<Treasure>(treasures);
		}
	}
	
	/**
	 *  Try to move a given distance.
	 *  Due to slip or terrain properties the end location might differ from the desired location.
	 *  
	 *  @param dx	The intended horizontal movement, i.e. delta-x.
	 *  @param dy	The intended vertical movement, i.e. delta-y.
	 *  @return	A future that is finished, when the movement operation is completed.
	 */
	public IFuture<Void>	move(int dx, int dy)
	{
		this.location.x	+= dx;
		this.location.y	+= dy;
		
		panel.environmentChanged();
		
		return IFuture.DONE;
	}
	
	/**
	 *  Pickup a treasure.
	 *  Only works, when at the location.
	 *  @param treasure	The treasure to be picked up.
	 *  @return	A future that is finished, when the pick up operation is completed, i.e. failed or succeeded.
	 */
	public IFuture<Void>	pickUp(Treasure treasure)
	{
		Future<Void>	ret	= new Future<Void>();
		if(treasures.contains(treasure))
		{
			if(treasure.location.equals(location))
			{
				treasures.remove(treasure);
				panel.environmentChanged();
				ret.setResult(null);
			}
			else
			{
				ret.setException(new IllegalArgumentException("Hunter "+location+" not at treasure location "+treasure.location+"."));
			}
		}
		else
		{
			ret.setException(new IllegalArgumentException("No such treasure in environment: "+treasure));
		}
		
		return ret;
	}
	
	//-------- Closeable interface --------
	
	/**
	 *  Auto close the gui when the agent is killed.
	 */
	@Override
	public void close()
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				SGUI.getWindowParent(panel).dispose();
			}
		});
	}
}
