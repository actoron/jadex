package jadex.extension.envsupport;

import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.IExtensionInstance;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceObject;

import java.util.Collection;
import java.util.Map;

/**
 *  Environment service implementation.
 */
@Service
public class EnvironmentService	implements	IEnvironmentService
{
	//-------- attributes --------
	
	/** The component. */
	@ServiceComponent
	protected IInternalAccess	component;
	
	/** The environment space instance name used for space lookup. */
	protected String	spacename;
	
	/** The environment space (cached on first access). */
	protected IEnvironmentSpace	space;
	
	//-------- constructors --------
	
	/**
	 *  Create an environment service for a given space.
	 *  @param spacename	The name of the space instance.
	 */
	public EnvironmentService(String spacename)
	{
		this.spacename	= spacename; 
	}
	
	//-------- IEnvironmentService --------
	
	/**
	 *  Registers the calling agent (component) in the environment.
	 *  Each agent can only register once, otherwise an exception is returned.
	 *  @param objecttype	The space object type as defined in the environment to use as avatar for the agent (component).
	 *  @return	A future through which percepts are published to the agent (component).
	 *    Termination of the future deregisters the agent (component).
	 */
	public ISubscriptionIntermediateFuture<Collection<ISpaceObject>>	register(final String objecttype)
	{
		SubscriptionIntermediateFuture<Collection<ISpaceObject>>	ret	=
			new SubscriptionIntermediateFuture<Collection<ISpaceObject>>(new Runnable()
		{
			public void run()
			{
				// Todo: destroy avatar on termination.
			}
		});
		
		getSpace().addResultListener(new ExceptionDelegationResultListener<IEnvironmentSpace, Collection<Collection<ISpaceObject>>>(ret)
		{
			public void customResultAvailable(IEnvironmentSpace space)
			{
				space.createSpaceObject(objecttype, null, null);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Perform an action.
	 *  May only be called from registered agents (components), otherwise an exception is returned.
	 *  If the action is (currently) not allowed for the agents avatar, also an exception is returned.
	 *  @param actiontype	The type name of the action as defined in the environment.
	 *  @param parameters	Parameters for the action, if any. 
	 */
	public IFuture<Void>	performAction(String actiontype, Map<String, Object> parameters)
	{
		return IFuture.DONE;
	}
	
	//-------- helper methods --------
	
	/**
	 *  Get the environment space.
	 */
	protected IFuture<IEnvironmentSpace>	getSpace()
	{
		final Future<IEnvironmentSpace>	ret	= new Future<IEnvironmentSpace>();
		if(space!=null)
		{
			ret.setResult(space);
		}
		else
		{
			component.getExternalAccess().getExtension(spacename)
				.addResultListener(new ExceptionDelegationResultListener<IExtensionInstance, IEnvironmentSpace>(ret)
			{
				public void customResultAvailable(IExtensionInstance result)
				{
					space	= (IEnvironmentSpace)result;
					ret.setResult(space);
				}
			});
		}
		return ret;
	}
}
