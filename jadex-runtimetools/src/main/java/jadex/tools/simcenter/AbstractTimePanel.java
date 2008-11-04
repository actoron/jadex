package jadex.tools.simcenter;

import jadex.adapter.base.ISimulationService;
import jadex.adapter.base.clock.IClock;
import jadex.bridge.IClockService;
import jadex.bridge.IPlatform;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


/**
 *	Base panel for displaying/editing clock related data.
 *  Handles listener and swing update issues. 
 */
public abstract class AbstractTimePanel extends JPanel
{
	//-------- attributes --------
	
	/** The platform. */
	protected IPlatform platform;
	
	/** The clock listener. */
	protected ChangeListener clocklistener;

	/** The context listener. */
	protected ChangeListener contextlistener;

	/** The active state (active = updates gui on clock changes). */
	protected boolean active;

	/** Flag indicating that update view was called but not yet executed. */
	protected boolean	updatecalled;
	
	//-------- constructors --------

	/**
	 *  Create an abstract time panel.
	 */
	public AbstractTimePanel(IPlatform platform)
	{
		this.platform = platform;
//		this.simservice = context;
		
		// The clock listener updates the gui when the clock changes.
		clocklistener = new ChangeListener()
		{
			public void stateChanged(ChangeEvent e)
			{
				if(!updatecalled && active)
				{
					updatecalled	= true;
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							updatecalled	= false;
							updateView();
						}
					});
				}
			}
		};
		
		// The context listener is used to change the clock listener, when the clock is exchanged.
		contextlistener = new ChangeListener()
		{
			IClock oldclock	= getClock();
			public void stateChanged(ChangeEvent e)
			{
				IClock newclock	= getClock();
				if(oldclock!=newclock)
				{
					oldclock.removeChangeListener(clocklistener);
					newclock.addChangeListener(clocklistener);
					
					// Inform listener that clock has changed.
					clocklistener.stateChanged(e);
				}
			}
		};
	}
	
	/**
	 *  Activate or deactivate panel.
	 *  @param active The active state.
	 */ 
	public void setActive(boolean active)
	{
		if(active!=this.active)
		{
			this.active = active;
			if(active)
			{
				getSimulationService().addChangeListener(contextlistener);
				getClock().addChangeListener(clocklistener);
				updateView();
			}
			else
			{
				getSimulationService().removeChangeListener(contextlistener);
				getClock().removeChangeListener(clocklistener);
			}
		}
	}
	
	/**
	 *  Get the platform.
	 *  @return The platform.
	 */
	protected IPlatform getPlatform()
	{
		return platform;
	}
	
	/**
	 *  Get the simulation service.
	 *  @return The simulation service.
	 */
	protected ISimulationService getSimulationService()
	{
		return (ISimulationService)getPlatform().getService(ISimulationService.class);
	}
	
	/**
	 *  Get the simulation service.
	 *  @return The simulation service.
	 */
	protected IClock getClock()
	{
		return (IClock)getPlatform().getService(IClockService.class);
	}
	
	/**
	 *  Update the view.
	 */
	public abstract void updateView();
}
