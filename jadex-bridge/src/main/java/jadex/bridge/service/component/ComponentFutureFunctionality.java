package jadex.bridge.service.component;

import java.util.logging.Logger;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.component.interceptors.FutureFunctionality;
import jadex.commons.ICommand;
import jadex.commons.future.IFuture;

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
	public void scheduleForward(final ICommand<Void> command)
	{
		if(!access.getComponentFeature(IExecutionFeature.class).isComponentThread())
		{
			access.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					command.execute(null);
					return IFuture.DONE;
				}
			});
		}
		else
		{
			command.execute(null);
		}
	};
}
