package jadex.tools.simcenter;

import jadex.base.service.simulation.ISimulationService;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.clock.IClockService;
import jadex.commons.ChangeEvent;
import jadex.commons.IChangeListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.SwingDefaultResultListener;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JPanel;
import javax.swing.Timer;

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

	/** Flag indicating that an update is required. */
	protected boolean	updatecalled;
	
	/** Flag indicating that an update is in progress. */
	protected boolean	updating;
	
	//-------- constructors --------

	/**
	 *  Create an abstract time panel.
	 */
	public AbstractTimePanel(IServiceProvider container)
	{
		this.container = container;
		
		// The clock listener updates the gui when the clock changes.
		clocklistener = new IChangeListener()
		{
			public void changeOccurred(ChangeEvent e)
			{
				invokeUpdateView();
			}
		};
		
		// The simservice listener is used for getting informed when the clock is exchanged 
		// and when the its execution state changes.
		contextlistener = new SimChangeListener();		
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
				SServiceProvider.getService(getServiceProvider(), ISimulationService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new SwingDefaultResultListener(AbstractTimePanel.this)
				{
					public void customResultAvailable(Object result)
					{
						((ISimulationService)result).addChangeListener(contextlistener);
						SServiceProvider.getService(getServiceProvider(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
							.addResultListener(new SwingDefaultResultListener(AbstractTimePanel.this)
						{
							public void customResultAvailable(Object result)
							{
								((IClockService)result).addChangeListener(clocklistener);
								invokeUpdateView();
							}
						});
					}
				});
			}
			else
			{
				SServiceProvider.getService(getServiceProvider(), ISimulationService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new SwingDefaultResultListener(AbstractTimePanel.this)
				{
					public void customResultAvailable(Object result)
					{
						((ISimulationService)result).removeChangeListener(contextlistener);
						SServiceProvider.getService(getServiceProvider(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
							.addResultListener(new SwingDefaultResultListener(AbstractTimePanel.this)
						{
							public void customResultAvailable(Object result)
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
	 *  Extract data and update the view.
	 */
	public abstract IFuture	updateView();

	/**
	 * 	Called to cause a gui update.
	 */
	public void invokeUpdateView()
	{
		boolean	update	= false;
		synchronized(this)
		{
			if(updating)
			{
				updatecalled	= true;
			}
			else
			{
				update 	= true;
				updatecalled	= false;
				updating	= true;
			}
		}
		
		if(update)
		{
			Timer	timer	= new Timer(50, new ActionListener()
			{
				public void actionPerformed(ActionEvent e)
				{
					updateView().addResultListener(new SwingDefaultResultListener(AbstractTimePanel.this)
					{
						public void customResultAvailable(Object result)
						{
							boolean	update	= false;
							synchronized(AbstractTimePanel.this)
							{
								if(updatecalled)
								{
									update	= true;
									updatecalled	= false;
								}
								updating	= false;
							}
							if(update)
							{
								invokeUpdateView();
							}
						}
						
						public void customExceptionOccurred(Exception exception)
						{
							customResultAvailable(null);
							super.customExceptionOccurred(exception);
						}
					});
				}
			});
			timer.setRepeats(false);
			timer.start();
		}
	}
	
	class SimChangeListener implements IChangeListener
	{
		protected IClockService clock;
		
		public SimChangeListener()
		{
			SServiceProvider.getService(getServiceProvider(), IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM)
				.addResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object result)
				{
					clock = (IClockService)result;
				}
			});
		}
		
		public void changeOccurred(final ChangeEvent e)
		{
			SServiceProvider.getService(getServiceProvider(),
				IClockService.class, RequiredServiceInfo.SCOPE_PLATFORM).addResultListener(new DefaultResultListener()
			{
				public void resultAvailable(Object result)
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



