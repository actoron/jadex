package jadex.tools.simcenter;

import jadex.base.service.simulation.ISimulationService;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.commons.concurrent.SwingDefaultResultListener;
import jadex.service.IServiceProvider;
import jadex.service.SServiceProvider;
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
	protected IServiceProvider container;
	
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
	public AbstractTimePanel(IServiceProvider container)
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
		contextlistener = new SimChangeListener();
		
//		contextlistener = new IChangeListener(IClockService clock)
//		{
//			protected IClockService oldclock;
//			public IChangeListener(IClockService clock)
//			{
//				this.oldclock = clock;
//			}
//			
////			IClockService oldclock	= getClockService();
//			public void changeOccurred(ChangeEvent e)
//			{
//				IClockService newclock	= getClockService();
//				if(oldclock!=newclock)
//				{
//					oldclock.removeChangeListener(clocklistener);
//					newclock.addChangeListener(clocklistener);
//					
//					// Inform listener that clock has changed.
//					clocklistener.changeOccurred(e);
//				}
//				
//				invokeUpdateView();
//			}
//		};
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
				SServiceProvider.getService(getServiceProvider(),
					ISimulationService.class).addResultListener(new SwingDefaultResultListener(AbstractTimePanel.this)
				{
					public void customResultAvailable(Object source, Object result)
					{
						((ISimulationService)result).addChangeListener(contextlistener);
						SServiceProvider.getService(getServiceProvider(),
							IClockService.class).addResultListener(new SwingDefaultResultListener(AbstractTimePanel.this)
						{
							public void customResultAvailable(Object source, Object result)
							{
								((IClockService)result).addChangeListener(clocklistener);
								updateView();
							}
						});
					}
				});
			}
			else
			{
				SServiceProvider.getService(getServiceProvider(),
					ISimulationService.class).addResultListener(new SwingDefaultResultListener(AbstractTimePanel.this)
				{
					public void customResultAvailable(Object source, Object result)
					{
						((ISimulationService)result).removeChangeListener(contextlistener);
						SServiceProvider.getService(getServiceProvider(),
								IClockService.class).addResultListener(new SwingDefaultResultListener(AbstractTimePanel.this)
						{
							public void customResultAvailable(Object source, Object result)
							{
								((IClockService)result).removeChangeListener(clocklistener);
							}
						});
					}
				});
			}
		}
	}
	
	/**
	 *  Get the platform.
	 *  @return The platform.
	 */
	protected IServiceProvider getServiceProvider()
	{
		return container;
	}
	
	/**
	 *  Get the simulation service.
	 *  @return The simulation service.
	 * /
	protected ISimulationService getSimulationService()
	{
		return (ISimulationService)getPlatform().getService(ISimulationService.class);
	}*/
	
	/**
	 *  Get the simulation service.
	 *  @return The simulation service.
	 * /
	protected IClockService getClockService()
	{
		return (IClockService)getPlatform().getService(IClockService.class);
	}*/
	
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
	
	class SimChangeListener implements IChangeListener
	{
		protected IClockService clock;
		
		public SimChangeListener()
		{
			SServiceProvider.getService(getServiceProvider(),
					IClockService.class).addResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					clock = (IClockService)result;
				}
			});
		}
		
		public void changeOccurred(final ChangeEvent e)
		{
			SServiceProvider.getService(getServiceProvider(),
				IClockService.class).addResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					IClockService newclock = (IClockService)result;//getClockService();
					if(clock!=newclock)
					{
						clock.removeChangeListener(clocklistener);
						newclock.addChangeListener(clocklistener);
						
						// Inform listener that clock has changed.
						clocklistener.changeOccurred(e);
					}
					
					invokeUpdateView();
				}
			});
		}
	};
}



