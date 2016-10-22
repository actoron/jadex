package jadex.bdiv3.quickstart.treasurehunt.environment;

import java.awt.BorderLayout;
import java.awt.Point;
import java.util.LinkedHashSet;
import java.util.Random;
import java.util.Set;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *  The treasure hunter world representation.
 */
public class TreasureHunterEnvironment
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
	}
}
