package jadex.base.service.message.transport.localmtp;

import jadex.base.service.message.ManagerSendTask;
import jadex.base.service.message.transport.ITransport;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.concurrent.Token;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;


/**
 *  The local transport for sending messages on the
 *  same platform (just calls the local deliver method).
 */
public class LocalTransport implements ITransport
{
	//-------- constants --------
	
	/** The schema name. */
	public final static String SCHEMA = "local-mtp://";
	
	//-------- attributes --------
	
	/** The message service. */
	protected IMessageService msgservice;
	
	/** The addresses. */
	protected String[] addresses;
	
	/** The platform name. */
//	protected String platformname;
	
	/** The platform. */
	protected IServiceProvider container;
	
	//-------- constructors --------
	
	/**
	 *  Init the transport.
	 *  @param platform The platform.
	 *  @param settings The settings.
	 */
	public LocalTransport(IServiceProvider container)
	{
		this.container = container;
		this.addresses = new String[0];
//		this.platformname = platform.getName();
	}

	/**
	 *  Start the transport.
	 */
	public IFuture<Void> start()
	{
		final Future<Void> ret = new Future<Void>();
		SServiceProvider.getService(container, IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IMessageService, Void>(ret)
		{
			public void customResultAvailable(IMessageService result)
			{
				msgservice = result;
				ret.setResult(null);
			}
		});
		return ret;
	}
	
	/**
	 *  Perform cleanup operations (if any).
	 */
	public IFuture<Void> shutdown()
	{
		return IFuture.DONE;
	}
	
	//-------- methods --------
	
	/**
	 *  Send a message to receivers on the same platform.
	 *  This method is called concurrently for all transports.
	 *  Each transport should try to connect to the target platform
	 *  (or reuse an existing connection) and afterwards acquire the token.
	 *  
	 *  The one transport that acquires the token (i.e. the first connected transport) gets to send the message.
	 *  All other transports ignore the current message and return an exception,
	 *  but may keep any established connections open for later messages.
	 *  
	 *  @param task The message to send.
	 *  @param token The token to be acquired before sending. 
	 *  @return A future indicating successful sending or exception, when the message was not send by this transport.
	 */
	public IFuture<Void>	sendMessage(ManagerSendTask task, Token token)
	{
		IFuture<Void>	ret;
		if(task.getReceivers()[0].getPlatformName().equals(((IComponentIdentifier)container.getId()).getPlatformName())
			&& token.acquire())
		{
			msgservice.deliverMessage(task.getMessage(), task.getMessageType().getName(), task.getReceivers());
			ret	= IFuture.DONE;
		}
		else
		{
			ret	= new Future<Void>(new RuntimeException("Can only deliver to local agents"));
		}
		return ret;
	}

	
	/**
	 *  Returns the prefix of this transport
	 *  @return Transport prefix.
	 */
	public String getServiceSchema()
	{
		return "local:";
	}
	
	/**
	 *  Get the adresses of this transport.
	 *  @return An array of strings representing the addresses 
	 *  of this message transport mechanism.
	 */
	public String[] getAddresses()
	{
		return addresses;
	}

}
