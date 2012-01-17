package jadex.base.service.message.transport.httprelaymtp.io;

import jadex.base.service.message.ManagerSendTask;
import jadex.base.service.message.transport.ITransport;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.xml.bean.JavaWriter;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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
	}
	
	//-------- methods --------
	
	/**
	 *  Start the transport.
	 */
	public IFuture<Void> start()
	{
		// Create the receiver (starts automatically).
		receiver	= new HttpReceiver(component.getExternalAccess(), address);
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
	 *  Send a message to receivers on the same platform.
	 *  @param message The message to send.
	 *  @return A future indicating if sending was successful.
	 */
	public IFuture<Void>	sendMessage(final ManagerSendTask task)
	{
		final Future<Void>	ret	= new Future<Void>();
		try
		{
			// Message service only calls transport.sendMessage() with receivers on same destination
			// so just use first to fetch platform id.
			IComponentIdentifier	targetid	= task.getReceivers()[0].getRoot();
			byte[]	iddata	= JavaWriter.objectToByteArray(targetid, HttpRelayTransport.class.getClassLoader());
			
			URL	url	= new URL(address);
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
			
			ret.setResult(null);
		}
		catch(Exception e)
		{
			ret.setException(e);
		}
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
