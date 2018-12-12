package jadex.bridge.service.component;

import java.util.logging.Logger;

import jadex.base.Starter;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.commons.DebugException;
import jadex.commons.ICommand;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  Schedule forward future executions (e.g. results) on component thread,
 *  i.e. the component is the callee side of the future.
 */
public class ComponentFutureFunctionality extends FutureFunctionality
{
	//-------- attributes --------
	
	/** The adapter. */
	protected IInternalAccess	access;
	
	//-------- constructors --------
	
	/**
	 *  Create a new future.
	 */
	public ComponentFutureFunctionality(IInternalAccess access)
	{
		super((Logger)access.getLogger());
		this.access = access;
	}
	
	/**
	 *  Send a foward command.
	 */
	@Override
	public <T> void scheduleForward(final ICommand<T> command, final T args)
	{
		if(!access.getFeature(IExecutionFeature.class).isComponentThread())
		{
			Exception ex	= Future.DEBUG ? new DebugException() : null;
			access.getFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					if(Future.DEBUG)
						DebugException.ADDITIONAL.set(ex);
					command.execute(args);
					return IFuture.DONE;
				}
			}).addResultListener(new IResultListener<Void>()
			{
				@Override
				public void exceptionOccurred(Exception exception)
				{
					// Todo: why rescue thread necessary? (e.g. DependentServicesAgent)
					if(exception instanceof ComponentTerminatedException && ((ComponentTerminatedException)exception).getComponentIdentifier().equals(access.getId()))
					{
						Starter.scheduleRescueStep(access.getId(), new Runnable()
						{
							public void run()
							{
//								System.err.println(access.getId()+": scheduled on rescue thread: "+command);
								command.execute(args);
							}
						});
					}
					else
					{
						System.err.println("Unexpected Exception: "+command);
						exception.printStackTrace();
					}
				}
				
				@Override
				public void resultAvailable(Void result)
				{
					// scheduled ok.
				}
			});
		}
		else
		{
			command.execute(args);
		}
	};
}
