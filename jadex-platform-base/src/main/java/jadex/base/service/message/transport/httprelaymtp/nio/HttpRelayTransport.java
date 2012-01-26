package jadex.base.service.message.transport.httprelaymtp.nio;

import jadex.base.service.message.ManagerSendTask;
import jadex.base.service.message.transport.ITransport;
import jadex.base.service.message.transport.httprelaymtp.SRelay;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
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
					selectorthread	= new HttpSelectorThread(component.getComponentIdentifier().getRoot(), address.substring(6), ms, component.getLogger());
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
	 *  @param message The message to send.
	 *  @return A future indicating if sending was successful.
	 */
	public IFuture<Void>	sendMessage(final ManagerSendTask task)
	{
		final Future<Void>	ret	= new Future<Void>();
		
		// Fetch all addresses
		Set<String>	addresses	= new LinkedHashSet<String>();
		for(int i=0; i<task.getReceivers().length; i++)
		{
			String[]	raddrs	= task.getReceivers()[i].getAddresses();
			for(int j=0; j<raddrs.length; j++)
			{
				if(raddrs[i].startsWith(getServiceSchema()))
					addresses.add(raddrs[j].substring(6));	// strip 'relay-' prefix.
			}			
		}

		// Iterate over all different addresses and try to send
		if(!addresses.isEmpty())
		{
			if(addresses.size()==1)
			{
				this.selectorthread.addSendTask(task, addresses.iterator().next(), ret);
			}
			else
			{
				final Iterator<String>	it	= addresses.iterator();
				IResultListener<Void>	rl	= new DelegationResultListener<Void>(ret)
				{
					public void exceptionOccurred(Exception exception)
					{
						if(it.hasNext())
						{
							Future<Void>	fut	= new Future<Void>();
							selectorthread.addSendTask(task, it.next(), fut);
							fut.addResultListener(this);
						}
						else
						{
							ret.setException(exception);
						}
					}
				};
				rl.exceptionOccurred(null);	// First call to start iteration of addresses.
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
}
