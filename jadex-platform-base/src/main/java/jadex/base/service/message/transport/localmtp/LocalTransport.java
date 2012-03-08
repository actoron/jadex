package jadex.base.service.message.transport.localmtp;

import java.util.Arrays;

import jadex.base.service.message.ISendTask;
import jadex.base.service.message.transport.ITransport;
import jadex.base.service.message.transport.MessageEnvelope;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.IResultCommand;
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
	public final static String[] SCHEMAS = new String[]{"local-mtp://"};
	
	//-------- attributes --------
	
	/** The message service. */
	protected IMessageService msgservice;
	
	/** The addresses. */
	protected String[] addresses;
	
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
		this.addresses = new String[]{SCHEMAS[0]+container.getId().getPlatformName()};
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
	 *  Test if a transport is applicable for the target address.
	 *  
	 *  @return True, if the transport is applicable for the address.
	 */
	public boolean	isApplicable(String address)
	{
		boolean	applicable	= false;
		for(int i=0; !applicable && i<getServiceSchemas().length; i++)
		{
			applicable	= address.startsWith(getServiceSchemas()[i])
				&& address.substring(getServiceSchemas()[i].length()).equals(container.getId().getPlatformName());
		}
		return applicable;
	}
	
	/**
	 *  Send a message to the given address.
	 *  This method is called multiple times for the same message, i.e. once for each applicable transport / address pair.
	 *  The transport should asynchronously try to connect to the target address
	 *  (or reuse an existing connection) and afterwards call-back the ready() method on the send task.
	 *  
	 *  The send manager calls the obtained send commands of the transports and makes sure that the message
	 *  gets sent only once (i.e. call send commands sequentially and stop, when a send command finished successfully).
	 *  
	 *  All transports may keep any established connections open for later messages.
	 *  
	 *  @param address The address to send to.
	 *  @param task A task representing the message to send.
	 */
	public void	sendMessage(String address, final ISendTask task)
	{
		IResultCommand<IFuture<Void>, Void>	send	= new IResultCommand<IFuture<Void>, Void>()
		{
			public IFuture<Void> execute(Void args)
			{
//				System.out.println("Sent with local transport: "+task.getReceivers()[0]);
				
//				msgservice.deliverMessage(task.getMessage(), task.getMessageType().getName(), task.getReceivers());
				msgservice.deliverMessage(task.getMessage());
				return IFuture.DONE;
			}
		};
		task.ready(send);
	}

	
	/**
	 *  Returns the prefix of this transport
	 *  @return Transport prefix.
	 */
	public String[] getServiceSchemas()
	{
		return SCHEMAS;
	}
	
	/**
	 *  Get the addresses of this transport.
	 *  @return An array of strings representing the addresses 
	 *  of this message transport mechanism.
	 */
	public String[] getAddresses()
	{
		return addresses;
	}

}
