package jadex.base.service.message.transport.httprelaymtp.nio;

import jadex.base.service.message.ManagerSendTask;
import jadex.base.service.message.transport.ITransport;
import jadex.base.service.message.transport.httprelaymtp.SRelay;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.Tuple2;
import jadex.commons.concurrent.Token;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Binding;

import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Set;

public class HttpRelayTransport implements ITransport
{
	//-------- attributes --------
	
	/** The component. */
	protected IInternalAccess component;
	
	/** The relay server address. */
	protected String	address;
	
	/** The receiver process. */
	protected HttpSelectorThread	selectorthread;
	
	//-------- constructors --------
	
	/**
	 *  Create a new relay transport.
	 */
	public HttpRelayTransport(IInternalAccess component, String address)
	{
		this.component	= component;
		this.address	= address;
		if(!address.startsWith(getServiceSchema()))
			throw new RuntimeException("Address does not match service schema: "+address+", "+getServiceSchema());
	}
	
	//-------- ITransport  interface --------
	
	/**
	 *  Start the transport.
	 */
	public IFuture<Void> start()
	{
		final Future<Void>	ret	= new Future<Void>();
		component.getServiceContainer().searchService(IMessageService.class, Binding.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IMessageService, Void>(ret)
		{
			public void customResultAvailable(IMessageService ms)
			{
				try
				{
					// Create the selector thread (starts automatically).
					selectorthread	= new HttpSelectorThread(component.getComponentIdentifier().getRoot(), address, ms, component.getLogger(), component.getExternalAccess());
					ret.setResult(null);
				}
				catch(Exception e)
				{
					ret.setException(e);
				}
			}
		});
		return ret;
	}

	/**
	 *  Perform cleanup operations (if any).
	 */
	public IFuture<Void> shutdown()
	{
		// Stop the reciever.
		this.selectorthread.stop();
		return IFuture.DONE;
	}
	
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
	public IFuture<Void>	sendMessage(final ManagerSendTask task, Token token)
	{
		final Future<Void>	ret	= new Future<Void>();
		
		// Fetch all addresses
		Set<String>	addresses	= new LinkedHashSet<String>();
		for(int i=0; i<task.getReceivers().length; i++)
		{
			String[]	raddrs	= task.getReceivers()[i].getAddresses();
			for(int j=0; j<raddrs.length; j++)
			{
				if(raddrs[j].startsWith(getServiceSchema()))
					addresses.add(raddrs[j]);
			}			
		}

		// Iterate over all different addresses and try to send
		if(!addresses.isEmpty())
		{
			for(Iterator<String> it=addresses.iterator(); it.hasNext(); )
			{
				this.selectorthread.addSendTask(task, token, it.next(), ret);
			}
		}
		else
		{
			ret.setException(new RuntimeException("Could not deliver message"));
		}
		
		return ret;
	}
	
	/**
	 *  Returns the prefix of this transport
	 *  @return Transport prefix.
	 */
	public String getServiceSchema()
	{
		return SRelay.ADDRESS_SCHEME;
	}
	
	/**
	 *  Get the addresses of this transport.
	 *  @return An array of strings representing the addresses 
	 *  of this message transport mechanism.
	 */
	public String[] getAddresses()
	{
		return new String[]{address};
	}

	/**
	 *  Parse the address.
	 *  @return Host, port and path.
	 */
	public static Tuple2<Tuple2<String, Integer>, String> parseAddress(String address)
	{
		String	path	= "";
		int port	= 80;
		String host	= address.substring(SRelay.ADDRESS_SCHEME.length());
		if(host.indexOf('/')!=-1)
		{
			path	= host.substring(host.indexOf('/'));
			host	= host.substring(0, host.indexOf('/'));
		}
		if(host.indexOf(':')!=-1)
		{
			port	= Integer.parseInt(host.substring(host.indexOf(':')+1));
			host	= host.substring(0, host.indexOf(':'));			
		}
		Tuple2<String, Integer>	adr	= new Tuple2<String, Integer>(host, new Integer(port));
		Tuple2<Tuple2<String, Integer>, String>	tup	= new Tuple2<Tuple2<String, Integer>, String>(adr, path);
		return tup;
	}
}
