package jadex.extension.envsupport;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.ServiceCall;
import jadex.bridge.modelinfo.IExtensionInstance;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ITerminationCommand;
import jadex.commons.future.SubscriptionIntermediateFuture;
import jadex.extension.envsupport.environment.IEnvironmentSpace;
import jadex.extension.envsupport.environment.ISpaceAction;
import jadex.extension.envsupport.environment.ISpaceObject;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 *  Environment service implementation.
 */
public abstract class AbstractEnvironmentService
{
	//-------- attributes --------
	
	/** The component. */
	@ServiceComponent
	protected IInternalAccess	component;
	
	/** The environment space instance name used for space lookup. */
	protected String	spacename;
	
	/** The environment space. */
	protected IEnvironmentSpace	space;
	
	//-------- constructors --------
	
	/**
	 *  Create an environment service for a given space.
	 *  @param spacename	The name of the space instance.
	 */
	public AbstractEnvironmentService(String spacename)
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
	public ISubscriptionIntermediateFuture<Object>	register(final String objecttype)
	{
		final SubscriptionIntermediateFuture<Object>	ret	=
			new SubscriptionIntermediateFuture<Object>(new ITerminationCommand()
		{
			public void terminated(Exception reason)
			{
				// Todo: destroy avatar on termination.
				System.out.println("Terminated...");
			}
			
			public boolean checkTermination(Exception reason)
			{
				return true;
			}
		});
		
		getCallingComponent().addResultListener(
			new ExceptionDelegationResultListener<IComponentDescription, Collection<Object>>(ret)
		{
			public void customResultAvailable(IComponentDescription desc)
			{
				Map<String, Object>	props	= new HashMap<String, Object>();
				props.put(ISpaceObject.PROPERTY_OWNER, desc);
				space.createSpaceObject(objecttype, props, null);
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
	public IFuture<Void>	performAction(final String actiontype, final Map<String, Object> parameters)
	{
		final Future<Void>	ret	= new Future<Void>();
		
		getCallingComponent().addResultListener(
			new ExceptionDelegationResultListener<IComponentDescription, Void>(ret)
		{
			public void customResultAvailable(IComponentDescription desc)
			{
				parameters.put(ISpaceAction.ACTOR_ID, desc);
				space.performSpaceAction(actiontype, parameters, new ExceptionDelegationResultListener<Object, Void>(ret)
				{
					public void customResultAvailable(Object result)
					{
						ret.setResult(null);
					}
				});
			}
		});
		
		return ret;
	}
	
	//-------- helper methods --------
	
	/**
	 *  Get description of the calling component.
	 */
	protected IFuture<IComponentDescription>	getCallingComponent()
	{
		final Future<IComponentDescription>	ret	= new Future<IComponentDescription>();
		final IComponentIdentifier	caller	= ServiceCall.getCurrentInvocation().getCaller();
		
		// Hack!!! Space cannot be looked up in service start as service is initialized before envsupport extension.
		final Future<Void>	spacedone	= new Future<Void>();
		if(space==null)
		{
			component.getExternalAccess().getExtension(spacename)
				.addResultListener(new ExceptionDelegationResultListener<IExtensionInstance, IComponentDescription>(ret)
			{
				public void customResultAvailable(IExtensionInstance result)
				{
					space	= (IEnvironmentSpace)result;
					spacedone.setResult(null);
				}
			});
		}
		else
		{
			spacedone.setResult(null);
		}

		// Get component description.
		spacedone.addResultListener(new ExceptionDelegationResultListener<Void, IComponentDescription>(ret)
		{
			public void customResultAvailable(Void result)
			{
				component.getServiceContainer().searchService(IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM)
					.addResultListener(new ExceptionDelegationResultListener<IComponentManagementService, IComponentDescription>(ret)
				{
					public void customResultAvailable(IComponentManagementService cms)
					{
						cms.getComponentDescription(caller)
							.addResultListener(new DelegationResultListener<IComponentDescription>(ret));
					}
				});
			}
		});
		
		return ret;
	}
}
