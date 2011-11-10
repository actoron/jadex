package jadex.base.service.message.transport.httprelaymtp;

import jadex.base.service.message.transport.ITransport;
import jadex.base.service.message.transport.MessageEnvelope;
import jadex.base.service.message.transport.codecs.CodecFactory;
import jadex.base.service.message.transport.codecs.ICodec;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.SUtil;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

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
		final Future<Void>	ret	= new Future<Void>();
		component.getServiceContainer().searchService(IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IMessageService, Void>(ret)
		{
			public void customResultAvailable(IMessageService ms)
			{
				CodecFactory	codecfac	= (CodecFactory)ms.getCodecFactory();
				// Create the receiver (starts automatically).
				receiver	= new HttpReceiver(component.getExternalAccess(), codecfac, address);
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
		// Stop the reciever.
		this.receiver.stop();
		return IFuture.DONE;
	}
	
	/**
	 *  Send a message to receivers on the same platform.
	 *  @param message The message to send.
	 *  @return A future indicating if sending was successful.
	 */
	public IFuture<Void>	sendMessage(final Map<String, Object> message, final String msgtype, final IComponentIdentifier[] receivers, final byte[] codecids)
	{
		final Future<Void>	ret	= new Future<Void>();
		component.getServiceContainer().searchService(IMessageService.class, RequiredServiceInfo.SCOPE_PLATFORM)
			.addResultListener(new ExceptionDelegationResultListener<IMessageService, Void>(ret)
		{
			public void customResultAvailable(IMessageService ms)
			{
				try
				{
					// Message service only calls transport.sendMessage() with receivers on same destination
					// so just use first to fetch platform id.
					IComponentIdentifier	targetid	= receivers[0].getRoot();
					MessageEnvelope	envelope	= new MessageEnvelope(message, Arrays.asList(receivers), msgtype);
					CodecFactory	codecfac	= (CodecFactory)ms.getCodecFactory();
					byte[] cids	= codecids;
					if(cids==null || cids.length==0)
						cids = codecfac.getDefaultCodecIds();
	
					Object enc_msg = envelope;
					for(int i=0; i<cids.length; i++)
					{
						ICodec codec = codecfac.getCodec(cids[i]);
						enc_msg	= codec.encode(enc_msg, getClass().getClassLoader());
					}
					byte[] res = (byte[])enc_msg;
					
					int dynlen = 4+1+cids.length;
					int size = res.length+dynlen;
	//				System.out.println("len: "+size);
					URL	url	= new URL(address);
					HttpURLConnection	con	= (HttpURLConnection)url.openConnection();
					con.setRequestMethod("POST");
					con.setDoOutput(true);
					con.setUseCaches(false);
					//		con.setRequestProperty("Content-Length", "12");
					OutputStream	out	= con.getOutputStream();
					SRelay.writeObject(targetid, out);
					out.write((byte)cids.length);
					out.write(cids);
					out.write(SUtil.intToBytes(size));
					out.write(res);
					out.flush();
					
					con.connect();
					con.getInputStream();	// Required, otherwise servlet will not be executed.
					
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
