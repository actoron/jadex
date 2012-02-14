package jadex.base.service.message.transport.httprelaymtp.io;

import jadex.base.service.message.ISendTask;
import jadex.base.service.message.transport.ITransport;
import jadex.base.service.message.transport.httprelaymtp.SRelay;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.commons.IResultCommand;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.xml.bean.JavaWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
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
	protected HttpReceiver	receiver;
	
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
	
	//-------- methods --------
	
	/**
	 *  Start the transport.
	 */
	public IFuture<Void> start()
	{
		// Create the receiver (starts automatically).
		receiver	= new HttpReceiver(component.getExternalAccess(), address.substring(6));	// strip 'relay-' prefix.
		return IFuture.DONE;
	}

	/**
	 *  Perform cleanup operations (if any).
	 */
	public IFuture<Void> shutdown()
	{
		// Stop the reciever.
		this.receiver.stop();
		return IFuture.DONE;
	}
	
	/**
	 *  Test if a transport is applicable for the message.
	 *  
	 *  @return True, if the transport is applicable for the message.
	 */
	public boolean	isApplicable(ISendTask task)
	{
		boolean	ret	= false;
		for(int i=0; !ret && i<task.getReceivers().length; i++)
		{
			String[]	raddrs	= task.getReceivers()[i].getAddresses();
			for(int j=0; !ret && j<raddrs.length; j++)
			{
				ret	= raddrs[j].toLowerCase().startsWith(getServiceSchema());
			}			
		}
		return ret;
	}
	
	/**
	 *  Send a message to receivers on the same platform.
	 *  This method is called concurrently for all transports.
	 *  Each transport should immediately announce its interest and try to connect to the target platform
	 *  (or reuse an existing connection) and afterwards acquire the token for the task.
	 *  
	 *  The first transport that acquires the token (i.e. the first connected transport) tries to send the message.
	 *  If sending fails, it may release the token to trigger the other transports.
	 *  
	 *  All transports may keep any established connections open for later messages.
	 *  
	 *  @param task The message to send.
	 *  @return True, if the transport is applicable for the message.
	 */
	public void	sendMessage(final ISendTask task)
	{
		IResultCommand<IFuture<Void>, Void>	send	= new IResultCommand<IFuture<Void>, Void>()
		{
			public IFuture<Void> execute(Void args)
			{
				// Fetch all addresses
				Set<String>	addresses	= new LinkedHashSet<String>();
				for(int i=0; i<task.getReceivers().length; i++)
				{
					String[]	raddrs	= task.getReceivers()[i].getAddresses();
					for(int j=0; j<raddrs.length; j++)
					{
						if(raddrs[j].startsWith(getServiceSchema()))
							addresses.add(raddrs[j].substring(6));	// strip 'relay-' prefix.
					}			
				}

				boolean	delivered	= false;
				Exception	ex	= null;
				// Iterate over all different addresses and try to send
				String[] addrs = (String[])addresses.toArray(new String[addresses.size()]);
				for(int i=0; !delivered && i<addrs.length; i++)
				{
					try
					{
						// Message service only calls transport.sendMessage() with receivers on same destination
						// so just use first to fetch platform id.
						IComponentIdentifier	targetid	= task.getReceivers()[0].getRoot();
						byte[]	iddata	= JavaWriter.objectToByteArray(targetid, HttpRelayTransport.class.getClassLoader());
						
						URL	url	= new URL(addrs[i]);
						HttpURLConnection	con	= (HttpURLConnection)url.openConnection();
						con.setRequestMethod("POST");
						con.setDoOutput(true);
						con.setUseCaches(false);
						con.setRequestProperty("Content-Type", "application/octet-stream");
						con.setRequestProperty("Content-Length", ""+(4+iddata.length+4+task.getProlog().length+task.getData().length));
						con.connect();
						
						OutputStream	out	= con.getOutputStream();
						out.write(SUtil.intToBytes(iddata.length));
						out.write(iddata);
						out.write(SUtil.intToBytes(task.getProlog().length+task.getData().length));
						out.write(task.getProlog());
						out.write(task.getData());
						out.flush();
						
						int	code	= con.getResponseCode();
						if(code!=HttpURLConnection.HTTP_OK)
							throw new IOException("HTTP code "+code+": "+con.getResponseMessage());
						
						delivered	= true;
//						System.out.println("Sent with IO relay: "+task.getReceivers()[0]);
					}
					catch(Exception e)
					{
						ex	= e;
					}
				}
				
				return delivered ? IFuture.DONE : ex!=null ? new Future<Void>(ex) : new Future<Void>(new RuntimeException("Could not deliver message"));
			}
		};
		task.ready(send);
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
