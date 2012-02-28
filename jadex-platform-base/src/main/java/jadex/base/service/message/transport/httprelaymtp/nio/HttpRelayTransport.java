package jadex.base.service.message.transport.httprelaymtp.nio;

import jadex.base.service.message.ISendTask;
import jadex.base.service.message.transport.ITransport;
import jadex.base.service.message.transport.httprelaymtp.SRelay;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
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
	protected NIOSelectorThread	selectorthread;
	
	//-------- constructors --------
	
	/**
	 *  Create a new relay transport.
	 */
	public HttpRelayTransport(IInternalAccess component, String address)
	{
		this.component	= component;
		this.address	= address;
		boolean	found	= false;
		for(int i=0; !found && i<getServiceSchemas().length; i++)
		{
			found	= address.startsWith(getServiceSchemas()[i]);
		}
		if(!found)
		{
			throw new RuntimeException("Address does not match supported service schemes: "+address+", "+SUtil.arrayToString(getServiceSchemas()));
		}
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
					selectorthread	= new NIOSelectorThread(component.getComponentIdentifier().getRoot(), address, parseAddress(address), ms, component.getLogger(), component.getExternalAccess());
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
	 *  Test if a transport is applicable for the target address.
	 *  
	 *  @return True, if the transport is applicable for the address.
	 */
	public boolean	isApplicable(String address)	
	{
		boolean	ret	= false;
//		for(int i=0; !ret && i<task.getReceivers().length; i++)
//		{
//			String[]	raddrs	= task.getReceivers()[i].getAddresses();
//			for(int j=0; !ret && j<raddrs.length; j++)
//			{
//				for(int k=0; !ret && k<getServiceSchemas().length; k++)
//				{
//					ret	= raddrs[j].toLowerCase().startsWith(getServiceSchemas()[k]);
//				}
//			}			
//		}
		return ret;
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
	public void	sendMessage(String address, ISendTask task)
	{
		// Fetch all addresses
		Set<String>	addresses	= new LinkedHashSet<String>();
		for(int i=0; i<task.getReceivers().length; i++)
		{
			String[]	raddrs	= task.getReceivers()[i].getAddresses();
			for(int j=0; j<raddrs.length; j++)
			{
				for(int k=0; k<getServiceSchemas().length; k++)
				if(raddrs[j].startsWith(getServiceSchemas()[k]))
				{
					addresses.add(raddrs[j]);
				}
			}			
		}

		// Iterate over all different addresses and try to send
		for(Iterator<String> it=addresses.iterator(); it.hasNext(); )
		{
			this.selectorthread.addSendTask(task, parseAddress(it.next()));
		}
	}
	
	/**
	 *  Returns the prefix of this transport
	 *  @return Transport prefix.
	 */
	public String[] getServiceSchemas()
	{
		// Currently does not support HTTPS.
		return new String[]{SRelay.ADDRESS_SCHEMES[0]};
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
	public Tuple2<Tuple2<String, Integer>, String> parseAddress(String address)
	{
		Tuple2<Tuple2<String, Integer>, String>	tup	= null;
		for(int i=0; tup==null && i<getServiceSchemas().length; i++)
		{
			if(address.startsWith(getServiceSchemas()[i]))
			{
				String	path	= "";
				int port	= SRelay.DEFAULT_PORTS[i];
				String host	= address.substring(getServiceSchemas()[i].length());
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
				tup	= new Tuple2<Tuple2<String, Integer>, String>(adr, path);
			}
		}
		return tup;
	}
}
