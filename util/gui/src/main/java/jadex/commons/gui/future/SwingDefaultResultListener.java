package jadex.commons.gui.future;

import java.awt.Component;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;

import jadex.base.Starter;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.impl.ExecutionComponentFeature;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.bridge.service.types.simulation.ISimulationService;
import jadex.commons.SReflect;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFunctionalExceptionListener;
import jadex.commons.future.IFunctionalResultListener;
import jadex.commons.future.IFutureCommandResultListener;
import jadex.commons.gui.SGUI;

/**
 *  Result listener that redirects callbacks on the swing thread.
 */
public class SwingDefaultResultListener<E> extends DefaultResultListener<E>	implements IFutureCommandResultListener<E>
{
	//-------- attributes --------
	
	/** The component. */
	protected Component parent;
	
	/** Custom result listener */
	protected IFunctionalResultListener<E>	customResultListener;
	
	/** Custom result listener */
	protected IFunctionalExceptionListener	customExceptionListener;
	
	/** Future for clock advancement blocking. */
	protected Future<Void>	adblock;
	
	//-------- constructors --------
	
	/**
	 * Create a new listener with functional interfaces.
	 * 
	 * @param listener The listener.
	 */
	public SwingDefaultResultListener(IFunctionalResultListener<E> customResultListener)
	{
		this(customResultListener, null);
	}

	/**
	 * Create a new listener with functional interfaces.
	 * 
	 * @param customResultListener The custom result listener.
	 * @param customExceptionListener The listener that is called on exceptions.
	 */
	public SwingDefaultResultListener(IFunctionalResultListener<E> customResultListener, IFunctionalExceptionListener customExceptionListener)
	{
		this(customResultListener, customExceptionListener, null);
	}

	/**
	 * Create a new listener with functional interfaces.
	 * 
	 * @param customResultListener The custom result listener.
	 * @param customExceptionListener The listener that is called on exceptions.
	 * @param parent The parent component (when errors should be shown as
	 *        dialog).
	 */
	public SwingDefaultResultListener(IFunctionalResultListener<E> customResultListener, IFunctionalExceptionListener customExceptionListener, Component parent)
	{
		this(parent);
		this.customResultListener = customResultListener;
		this.customExceptionListener = customExceptionListener;
	}

	protected static Future<Void>	block()
	{
		Future<Void>	adblock	= null;
		IInternalAccess	ia	= ExecutionComponentFeature.LOCAL.get();
		if(ia!=null && (Boolean.TRUE.equals(Starter.getPlatformValue(ia.getId().getRoot(), IClockService.SIMULATION_CLOCK_FLAG))
			|| ia.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(IExecutionService.class).setRequiredProxyType(ServiceQuery.PROXYTYPE_RAW)).toString().startsWith("Bisim")))
		{
			adblock	= new Future<>();
			ia.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>(ISimulationService.class))
				.addAdvanceBlocker(adblock).get();
		}
		return adblock;
	}
	
	protected static void	unblock(Future<Void> adblock)
	{
		if(adblock!=null)
			adblock.setResult(null);
	}

	/**
	 *  Create a new listener.
	 */
	public SwingDefaultResultListener()
	{
//		Thread.dumpStack();
		adblock	= block();
	}
	
	/**
	 *  Create a new listener.
	 *  @param parent The parent component (when errors should be shown as dialog).
	 */
	public SwingDefaultResultListener(Component parent)
	{
		this.parent	= parent;
		adblock	= block();
//		Thread.dumpStack();
	}
	
	/**
	 *  Create a new listener.
	 *  @param logger The logger.
	 */
	public SwingDefaultResultListener(Logger logger)
	{
		super(logger);
		adblock	= block();
//		Thread.dumpStack();
	}
	
	//-------- methods --------
	
	/**
	 *  Called when the result is available.
	 * @param result The result.
	 */
	final public void resultAvailable(final E result)
	{
		// Hack!!! When triggered from shutdown hook, swing might be terminated
		// and invokeLater has no effect (grrr).
		if(!SReflect.HAS_GUI || SwingUtilities.isEventDispatchThread())// || Starter.isShutdown())
//		if(SwingUtilities.isEventDispatchThread())
		{
			try
			{
				customResultAvailable(result);
			}
			finally
			{
				unblock(adblock);
			}
		}
		else
		{
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					try
					{
						customResultAvailable(result);
					}
					finally
					{
						unblock(adblock);
					}
				}
			});
		}
	}
	
	/**
	 *  Called when an exception occurred.
	 * @param exception The exception.
	 */
	final public void exceptionOccurred(final Exception exception)
	{
//		exception.printStackTrace();
		// Hack!!! When triggered from shutdown hook, swing might be terminated
		// and invokeLater has no effect (grrr).
		if(!SReflect.HAS_GUI || SwingUtilities.isEventDispatchThread())// || Starter.isShutdown())
//		if(SwingUtilities.isEventDispatchThread())
		{
			try
			{
				customExceptionOccurred(exception);			
			}
			finally
			{
				unblock(adblock);
			}
		}
		else
		{
//			Thread.dumpStack();
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					try
					{
						customExceptionOccurred(exception);			
					}
					finally
					{
						unblock(adblock);
					}
				}
			});
		}
	}
	
	/**
	 *  Called when the result is available.
	 * @param result The result.
	 */
	public void customResultAvailable(E result) {
		if (customResultListener != null) {
			customResultListener.resultAvailable(result);
		}
	}
	
	/**
	 *  Called when an exception occurred.
	 * @param exception The exception.
	 */
	public void customExceptionOccurred(Exception exception)
	{
		if (customExceptionListener != null) {
			customExceptionListener.exceptionOccurred(exception);
		} else {
			if(parent!=null)
			{
				SGUI.showError(parent, "Problem Occurred", "A problem occurred while performing the requested action: "
						+SReflect.getInnerClassName(exception.getClass())+" "+exception.getMessage(), exception);
	//			exception.printStackTrace();
			}
			else
			{
				super.exceptionOccurred(exception);
			}
		}
	}

	/**
	 *  Called when a command is available.
	 */
	final public void commandAvailable(final Object command)
	{
		// Hack!!! When triggered from shutdown hook, swing might be terminated
		// and invokeLater has no effect (grrr).
		if(!SReflect.HAS_GUI || SwingUtilities.isEventDispatchThread())// || Starter.isShutdown())
//		if(SwingUtilities.isEventDispatchThread())
		{
			customCommandAvailable(command);			
		}
		else
		{
//			Thread.dumpStack();
			SwingUtilities.invokeLater(new Runnable()
			{
				public void run()
				{
					customCommandAvailable(command);
				}
			});
		}
	}
	
	/**
	 *  Called when a command is available.
	 */
	public void	customCommandAvailable(Object command)
	{
		Logger.getLogger("swing-result-listener").fine("Cannot forward command: "+this+" "+command);
	}
}
