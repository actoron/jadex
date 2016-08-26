package jadex.platform.service.message.transport.localmtp;

import java.util.Map;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.IResultCommand;
import jadex.commons.concurrent.IExecutable;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.platform.service.message.ISendTask;
import jadex.platform.service.message.MapSendTask;
import jadex.platform.service.message.MessageService;
import jadex.platform.service.message.streams.StreamSendTask;
import jadex.platform.service.message.transport.ITransport;
import jadex.platform.service.message.transport.MessageEnvelope;


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
	protected MessageService msgservice;
	
	/** The addresses. */
	protected String[] addresses;
	
	/** The platform. */
	protected IInternalAccess component;
	
	//-------- constructors --------
	
	/**
	 *  Init the transport.
	 */
	public LocalTransport(IInternalAccess component)
	{
		this.component = component;
		this.addresses = new String[]{SCHEMAS[0]+component.getComponentIdentifier().getPlatformName()};
	}

	/**
	 *  Start the transport.
	 */
	public IFuture<Void> start()
	{
		return component.getComponentFeature(IExecutionFeature.class).scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				
				Object rawms;
				try
				{
					rawms = ia.getComponentFeature(IProvidedServicesFeature.class).getProvidedServiceRawImpl(IMessageService.class);
				}
				catch (Exception e)
				{
					rawms = ia.getComponentFeature(IProvidedServicesFeature.class).getProvidedService(IMessageService.class);
				}
				msgservice=(MessageService) rawms;
				return IFuture.DONE;
			}
		});
//		final Future<Void> ret = new Future<Void>();
//		SServiceProvider.getService(component, IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM)
//			.addResultListener(new ExceptionDelegationResultListener<IMessageService, Void>(ret)
//		{
//			public void customResultAvailable(IMessageService result)
//			{
//				msgservice = result;
//				ret.setResult(null);
//			}
//		});
//		return ret;
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
				&& address.substring(getServiceSchemas()[i].length()).equals(component.getComponentIdentifier().getPlatformName());
		}
		return applicable;
	}
	
	/**
	 *  Test if a transport satisfies the non-functional requirements.
	 *  @param nonfunc	The non-functional requirements (name, value).
	 *  @param address	The transport address.
	 *  @return True, if the transport satisfies the non-functional requirements.
	 */
	public boolean	isNonFunctionalSatisfied(Map<String, Object> nonfunc, String address)
	{
		// Local satisfies all?!
		return true;
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
				if (task instanceof MapSendTask || task instanceof StreamSendTask)
				{
					msgservice.deliverMessageLocally(task);
				}
				else
				{
					byte[] prolog = task.getProlog();
					byte[] data = task.getData();
					byte[] msg = new byte[prolog.length+data.length];
					System.arraycopy(prolog, 0, msg, 0, prolog.length);
					System.arraycopy(data, 0, msg, prolog.length, data.length);
					msgservice.deliverMessage(msg);
				}
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
