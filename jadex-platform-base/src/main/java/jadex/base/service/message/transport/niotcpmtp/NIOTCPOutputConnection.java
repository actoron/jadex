package jadex.base.service.message.transport.niotcpmtp;

import jadex.base.service.message.transport.MessageEnvelope;
import jadex.base.service.message.transport.codecs.CodecFactory;
import jadex.base.service.message.transport.codecs.ICodec;
import jadex.base.service.message.transport.niotcpmtp.NIOTCPTransport.Cleaner;
import jadex.commons.SUtil;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

/**
 *  NIO-TCP output connection for sending messages to a specific target address. 
 */
class NIOTCPOutputConnection
{
	//-------- constants --------
	
	/** 5 sec timeout. */
	public static final int TIMEOUT = 5000;
	
	/** 2MB as message buffer. */
	public static final int BUFFER_SIZE = 2* 1024 * 1024;
	
	//-------- attributes --------
	
	/** The client socket for sending data. */
	protected SocketChannel sc;
	
	/** The buffer. */
//	protected ByteBuffer buffer;
	
	/** The dead connection time. */
	protected long deadtime;
	
	/** The codec factory. */
	protected CodecFactory codecfac;

	/** The cleaner. */
	protected Cleaner cleaner;
	
	/** The classloader. */
	protected ClassLoader classloader;

	//-------- constructors --------
	
	/**
	 *  Create a new tcp connection for sending data. 
	 *  @param iaddr
	 *  @param iport
	 *  @throws IOException
	 */
	public NIOTCPOutputConnection(InetAddress iaddr, int iport, CodecFactory codecfac, 
		Cleaner cleaner, ClassLoader classloader) throws IOException
	{
		this.codecfac = codecfac;
		this.cleaner = cleaner;
		this.classloader = classloader;
		
		// Create a non-blocking socket channel
	    this.sc = SocketChannel.open();
	    
	    // todo: perform sending asynchronous to caller thread
	    //this.sc.configureBlocking(false);
	  
//		try
//		{
//			System.out.println("NIOTCP Connection: "+iaddr+":"+iport);
		    // Kick off connection establishment
//		    this.sc.connect(new InetSocketAddress(iaddr, iport));	// Requires this for non blocking (what about timeouts?) 
			sc.socket().connect(new InetSocketAddress(iaddr, iport) , TIMEOUT);	// Doesn't work for non blocking.
//			System.out.println("NIOTCP Connection: "+iaddr+":"+iport+" established");
//		}
//		catch(IOException e)
//		{
//			System.out.println("NIOTCP Connection: "+iaddr+":"+iport+" failed");
////			e.printStackTrace();
//			throw e;
//		}

//	    this.buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
	    
		//address = SMTransport.SERVICE_SCHEMA+iaddr.getHostAddress()+":"+iport;
	 }

	//-------- methods --------
	
	/**
	 *  Send a message.
	 *  @param msg The message.
	 *  Sending is done synchronously on caller thread.
	 *  (todo: relax synchronization by performing sends 
	 *  on extra sender thread of transport, only needed
	 *  if message service is used in synchronous mode.)
	 */
	public synchronized void send(MessageEnvelope msg, byte[] codecids) throws IOException
	{
		// Code using preallocated buffer
//		IEncoder enc = codecfac.getDefaultEncoder();
//		byte codec_id = codecfac.getCodecId(enc.getClass());
//		byte[] enc_msg = enc.encode(msg, classloader);
//		int size = enc_msg.length+NIOTCPTransport.PROLOG_SIZE;
//		buffer.put(codec_id);
//		buffer.putInt(size);
//		buffer.put(enc_msg);
//		buffer.flip();
//		sc.write(buffer);
//		buffer.clear();
//		cleaner.refresh();
		
		// Code using new buffers
		if(codecids==null || codecids.length==0)
			codecids = codecfac.getDefaultCodecIds();

		Object enc_msg = msg;
		for(int i=0; i<codecids.length; i++)
		{
			ICodec codec = codecfac.getCodec(codecids[i]);
			enc_msg	= codec.encode(enc_msg, classloader);
		}
		byte[] res = (byte[])enc_msg;
		
		int dynlen = NIOTCPTransport.PROLOG_SIZE+codecids.length+1;
		byte[] buffer = new byte[res.length+dynlen];
		System.arraycopy(res, 0, buffer, dynlen, res.length);
		System.arraycopy(SUtil.intToBytes(buffer.length), 0, buffer, codecids.length+1, 4);
		
		buffer[0] = (byte)codecids.length;
		System.arraycopy(codecids, 0, buffer, 1, codecids.length);
		
		sc.write(ByteBuffer.wrap(buffer));
		cleaner.refresh();
	}
	
	/**
	 *  Close the connection.
	 */
	public void close()
	{
		try
		{
			sc.close();
		}
		catch(IOException e)
		{
			//e.printStackTrace();
		}
		cleaner.remove();
	}
}
