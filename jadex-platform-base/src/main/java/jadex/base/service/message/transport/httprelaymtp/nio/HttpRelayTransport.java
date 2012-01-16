package jadex.base.service.message.transport.httprelaymtp.nio;

import jadex.base.service.message.ManagerSendTask;
import jadex.base.service.message.transport.ITransport;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

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
	}
	
	//-------- ITransport  interface --------
	
	/**
	 *  Start the transport.
	 */
	public IFuture<Void> start()
	{
		Future<Void>	ret	= new Future<Void>();
		try
		{
			// Create the selector thread (starts automatically).
			selectorthread	= new HttpSelectorThread(component.getExternalAccess(), address);
			ret.setResult(null);
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
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
	public IFuture<Void>	sendMessage(ManagerSendTask task)
	{
		Future<Void>	ret	= new Future<Void>();
		this.selectorthread.addSendTask(task, ret);
		return ret;
	}
	
	/**
	 *  Returns the prefix of this transport
	 *  @return Transport prefix.
	 */
	public String getServiceSchema()
	{
		// As different platform can be reached through the same relay server, the complete address is actually kind of a prefix.
		return address;
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
