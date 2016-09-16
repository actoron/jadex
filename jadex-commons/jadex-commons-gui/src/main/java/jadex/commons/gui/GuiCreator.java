package jadex.commons.gui;

import java.awt.Component;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import javax.swing.SwingUtilities;

import jadex.commons.SUtil;

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
	protected Component gui;

	//-------- constructors --------

	/**
	 *  Create a new clock.
	 */
	public GuiCreator(final Class<? extends Component> clazz, final Class<?>[] argclasses, final Object[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					Constructor<?> con = clazz.getConstructor(argclasses);
					gui = (Component)con.newInstance(args);
				}
				catch(Exception e)
				{
					throw SUtil.throwUnchecked(e);
				}
			}
		});
	}
	
	/**
	 *  Create a new clock.
	 */
	public GuiCreator(final Method createmethod, final Class<?>[] argclasses, final Object[] args)
	{
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				try
				{
					gui = (Component)createmethod.invoke(null, args);
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
	public Component getGui()
	{
		return gui;
	}
}
