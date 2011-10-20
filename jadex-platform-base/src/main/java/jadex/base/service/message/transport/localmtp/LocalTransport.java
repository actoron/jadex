package jadex.base.service.message.transport.localmtp;

import jadex.base.service.message.transport.ITransport;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.Map;

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
	public IFuture start()
	{
		final Future ret = new Future();
		SServiceProvider.getService(container, IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				msgservice = (IMessageService)result;
				ret.setResult(null);
			}
		});
		return ret;
	}
	
	/**
	 *  Perform cleanup operations (if any).
	 */
	public IFuture shutdown()
	{
		return new Future(null);
		// nothing to do.
	}
	
	//-------- methods --------
	
	/**
	 *  Send a message.
	 *  @param message The message to send.
	 */
	public IFuture	sendMessage(Map message, String msgtype, IComponentIdentifier[] recs, byte[] codecids)
	{
		IFuture	ret;
		if(recs[0].getPlatformName().equals(((IComponentIdentifier)container.getId()).getPlatformName()))
		{
			msgservice.deliverMessage(message, msgtype, recs);
			ret	= IFuture.DONE;
		}
		else
		{
			ret	= new Future(new RuntimeException("Can only deliver to local agents"));
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
