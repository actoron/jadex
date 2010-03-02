package jadex.tools.simcenter;

import jadex.base.ISimulationService;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.service.IServiceContainer;
import jadex.service.clock.IClockService;

import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *	Base panel for displaying/editing clock related data.
 *  Handles listener and swing update issues. 
 */
public abstract class AbstractTimePanel extends JPanel
{
	//-------- attributes --------
	
	/** The service container. */
	protected IServiceContainer container;
	
	/** The clock listener. */
	protected IChangeListener clocklistener;

	/** The context listener. */
	protected IChangeListener contextlistener;

	/** The active state (active = updates gui on clock changes). */
	protected boolean active;

	/** Flag indicating that update view was called but not yet executed. */
	protected boolean	updatecalled;
	
	//-------- constructors --------

	/**
	 *  Create an abstract time panel.
	 */
	public AbstractTimePanel(IServiceContainer container)
	{
		this.container = container;
//		this.simservice = context;
		
		// The clock listener updates the gui when the clock changes.
		clocklistener = new IChangeListener()
		{
			public void changeOccurred(ChangeEvent e)
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
		contextlistener = new IChangeListener()
		{
			IClockService oldclock	= getClockService();
			public void changeOccurred(ChangeEvent e)
			{
				IClockService newclock	= getClockService();
				if(oldclock!=newclock)
				{
					oldclock.removeChangeListener(clocklistener);
					newclock.addChangeListener(clocklistener);
					
					// Inform listener that clock has changed.
					clocklistener.changeOccurred(e);
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
	protected IServiceContainer getPlatform()
	{
		return container;
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
