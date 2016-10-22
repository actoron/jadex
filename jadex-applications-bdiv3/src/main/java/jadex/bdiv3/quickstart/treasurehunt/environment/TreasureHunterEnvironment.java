package jadex.bdiv3.quickstart.treasurehunt.environment;

import java.awt.BorderLayout;
import java.awt.Point;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.Closeable;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.impl.IInternalExecutionFeature;

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
	
	/** The gui window. */
	protected JFrame	window;
	
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
		this.treasures	= new LinkedHashSet<Treasure>();
		for(int i=1; i<=10; i++)
		{
			treasures.add(Treasure.create(rnd, width, height));
		}
		
		SwingUtilities.invokeLater(new Runnable()
		{
			@Override
			public void run()
			{
				window	= new JFrame("Jadex Treasure Hunter World");
				window.getContentPane().add(BorderLayout.CENTER, new EnvironmentPanel(TreasureHunterEnvironment.this));
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
					window.addWindowListener(new WindowAdapter()
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
	 *  Get a copy of the current treasures.
	 */
	public synchronized Set<Treasure>	getTreasures()
	{
		Set<Treasure>	ret	= new LinkedHashSet<Treasure>();
		for(Treasure t: treasures)
		{
			ret.add(t.clone());
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
				window.dispose();
			}
		});
	}
}
