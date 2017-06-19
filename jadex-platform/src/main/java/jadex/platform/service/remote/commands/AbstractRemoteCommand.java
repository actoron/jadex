package jadex.platform.service.remote.commands;

import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceNotFoundException;
import jadex.bridge.service.types.security.DefaultAuthorizable;
import jadex.bridge.service.types.security.ISecurityService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.platform.service.remote.IRemoteCommand;
import jadex.platform.service.remote.RemoteReferenceModule;

/**
 *  Default base class for remote commands.
 */
public abstract class AbstractRemoteCommand	implements IRemoteCommand
{
	/** The receiver (for processing the command in rmipreprocessor, will not be transferred). */
	protected IComponentIdentifier receiver;
	
	/** The non-functional properties. */
	protected Map<String, Object> nonfunc;
	
	// todo: gets overwritten by decoupling interceptor
	// todo: unify with non-functional properties of AbstractRemoteCommand
//	/** The declared or remote default timeout value. */
//	protected long	timeout;
	
//	/** The real time timeout flag. */
//	protected boolean	realtime;
	
	//-------- constructors --------
	
	/**
	 *  Bean constructor.
	 */
	public AbstractRemoteCommand()
	{
	}
	
	/**
	 *  Bean constructor.
	 */
	public AbstractRemoteCommand(Map<String, Object> nonfunc)
	{
		this.nonfunc = nonfunc;
	}
	
	//-------- methods --------
	
	/**
	 *  Preprocess command and replace if they are remote references.
	 */
//	public IFuture<Void>	preprocessCommand(final IInternalAccess component, RemoteReferenceModule rrm, final IComponentIdentifier target)
//	{
//		final Future<Void>	ret	= new Future<Void>();
//
//		// Hack needed for rmi preprocessor
////		ITransportAddressService tas = SServiceProvider.getLocalService(component, ITransportAddressService.class, RequiredServiceInfo.SCOPE_PLATFORM);
////		tas.getTransportComponentIdentifier(target).addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Void>(ret)
////		{
////			public void customResultAvailable(IComponentIdentifier result)
////			{
//				receiver = target;
//				
//				component.getComponentFeature(IRequiredServicesFeature.class).searchService(ISecurityService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//					.addResultListener(new IResultListener<ISecurityService>()
//				{
//					public void resultAvailable(ISecurityService sec)
//					{
//						sec.preprocessRequest(AbstractRemoteCommand.this, target)
//							.addResultListener(new DelegationResultListener<Void>(ret));
//					}
//					
//					public void exceptionOccurred(Exception exception)
//					{
//						if(exception instanceof ServiceNotFoundException)
//						{
//							ret.setResult(null);
//						}
//						else
//						{
//							ret.setException(exception);
//						}
//					}
//				});
////			}
////		});
//		
//		return ret;
//	}
	
	/**
	 *  Post-process a received command before execution
	 *  for e.g. setting security level.
	 */
//	public IFuture<Void>	postprocessCommand(IInternalAccess component, RemoteReferenceModule rrm, final IComponentIdentifier target)
//	{
//		return IFuture.DONE;
//	}

	/**
	 *  Get the receiver (rms of other side).
	 *  @return the receiver.
	 */
	public IComponentIdentifier getReceiver()
	{
		return receiver;
	}
	
	/**
	 *  Get the sender component (if other than rms).
	 */
	public IComponentIdentifier getSender()
	{
		return null;
	}
	
	/**
	 *  Get the real receiver (other than rms).
	 *  @return the real receiver.
	 */
	public IComponentIdentifier getRealReceiver()
	{
		return null;
	}
	
	/**
	 *  The origin of the request.
	 *  May be used for blacklist/whitelist authentication.
	 */
	public IComponentIdentifier	getOrigin()
	{
		return getSender();
	}
	
	/**
	 *  Get the non-functional properties of the call.
	 *  @return The non-functional properties of the call.
	 */
	public Map<String, Object> getNonFunctionalProperties()
	{
		return nonfunc;
	}
	
	/**
	 *  Get the non-functional properties of the call.
	 */
	public void setNonFunctionalProperties(Map<String, Object> nonfunc)
	{
		this.nonfunc = nonfunc;
	}
	
	/**
	 *  Get a non-func value.
	 *  @param name The name.
	 *  @return The result.
	 */
	public Object getNonFunctionalProperty(String name)
	{
		return nonfunc==null? null: nonfunc.get(name);
	}
}
