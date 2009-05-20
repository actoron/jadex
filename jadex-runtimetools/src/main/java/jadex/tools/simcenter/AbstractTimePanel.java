package jadex.tools.simcenter;

import jadex.adapter.base.ISimulationService;
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
//				if(!updatecalled && active)
				{
//					updatecalled	= true;
					invokeUpdateView();
//					SwingUtilities.invokeLater(new Runnable()
//					{
//						public void run()
//						{
//							updatecalled	= false;
//							updateView();
//						}
//					});
				}
			}
		};
		
		// The simservice listener is used for getting informed when the clock is exchanged 
		// and when the its execution state changes.
		contextlistener = new ChangeListener()
		{
			IClockService oldclock	= getClockService();
			public void stateChanged(ChangeEvent e)
			{
				IClockService newclock	= getClockService();
				if(oldclock!=newclock)
				{
					oldclock.removeChangeListener(clocklistener);
					newclock.addChangeListener(clocklistener);
					
					// Inform listener that clock has changed.
					clocklistener.stateChanged(e);
				}
				
				invokeUpdateView();
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
				getClockService().addChangeListener(clocklistener);
				updateView();
			}
			else
			{
				getSimulationService().removeChangeListener(contextlistener);
				getClockService().removeChangeListener(clocklistener);
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
	protected IClockService getClockService()
	{
		return (IClockService)getPlatform().getService(IClockService.class);
	}
	
	/**
	 *  Update the view.
	 */
	public abstract void updateView();

	protected boolean	invoked;	
	
	/**
	 * 
	 */
	public void invokeUpdateView()
	{
		if(SwingUtilities.isEventDispatchThread())
		{
			updateView();
		}
		else
		{
			if(!invoked)
			{
				invoked	= true;
				SwingUtilities.invokeLater(new Runnable()
				{
					public void run()
					{
						invoked	= false;
						updateView();
					}
				});
			}
		}
	}
}
