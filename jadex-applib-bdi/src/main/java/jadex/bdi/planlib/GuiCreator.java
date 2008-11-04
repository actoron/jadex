package jadex.bdi.planlib;

import java.lang.reflect.Constructor;

import javax.swing.JFrame;
import javax.swing.SwingUtilities;

/**
 *  The gui creator.
 *  Note! The Gui cannot be itself a JFrame because this
 *  might lead to deadlocks. new JFrame() should only be called
 *  from Swing thread. As "new JFrame()" is content of a belief
 *  it is executed in the agent's thread. 
 */
public class GuiCreator
{
	//-------- attributes --------

	/** The gui. */
	protected JFrame	frame;

	//-------- constructors --------

	/**
	 *  Create a new clock.
	 */
	public GuiCreator(final Class frameclass, final Class[] argclasses, final Object[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					Constructor con = frameclass.getConstructor(argclasses);
					frame = (JFrame)con.newInstance(args);
				}
				catch(Exception e)
				{
					throw new RuntimeException(e);
				}
			}
		});
	}

	//-------- methods --------

	/**
	 *  Get the frame.
	 *  @return The frame. 
	 */
	public JFrame getFrame()
	{
		return frame;
	}
	
}
