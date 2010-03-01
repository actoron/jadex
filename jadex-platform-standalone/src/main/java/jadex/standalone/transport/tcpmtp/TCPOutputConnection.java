package jadex.standalone.transport.tcpmtp;

import jadex.standalone.transport.MessageEnvelope;
import jadex.standalone.transport.codecs.CodecFactory;
import jadex.standalone.transport.codecs.IEncoder;
import jadex.standalone.transport.tcpmtp.TCPTransport.Cleaner;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 *  TCP output connection for sending messages to a specific target address. 
 */
class TCPOutputConnection
{
	//-------- attributes --------
	
	/** The client socket for sending data. */
	protected Socket sock;

	/** The output stream. */
	protected OutputStream sos;
	
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
	 *  @param enc
	 *  @throws IOException
	 */
	public TCPOutputConnection(InetAddress iaddr, int iport, CodecFactory codecfac, 
		Cleaner cleaner, ClassLoader classloader) throws IOException
	{
		this.sock = new Socket(iaddr, iport);
		this.sos = new BufferedOutputStream(sock.getOutputStream());
		this.codecfac = codecfac;
		this.cleaner = cleaner;
		this.classloader = classloader;
		//address = SMTransport.SERVICE_SCHEMA+iaddr.getHostAddress()+":"+iport;
	}

	//-------- methods --------
	
	/**
	 *  Send a message.
	 *  @param msg The message.
	 *  (todo: relax synchronization by performing sends 
	 *  on extra sender thread of transport)
	 */
	public synchronized boolean send(MessageEnvelope msg)
	{
		boolean ret = false;
		
		try
		{
			IEncoder enc = codecfac.getDefaultEncoder();
			byte codec_id = codecfac.getCodecId(enc.getClass());
			if(codec_id==-1)
				throw new IOException("Codec id not found: "+enc);
			
			byte[] enc_msg = enc.encode(msg, classloader);
			
			int size = enc_msg.length+TCPTransport.PROLOG_SIZE;
			
			sos.write(codec_id);
			sos.write(size >> 24 & 0xFF);
			sos.write(size >> 16 & 0xFF);
			sos.write(size >> 8 & 0xFF);
			sos.write(size & 0xFF);
			sos.write(enc_msg);
			sos.flush();
			ret = true;
			cleaner.refresh();
		}
		catch(IOException e)
		{
			close();
		}
		
		return ret;
	}
	
	/**
	 *  Test if the connection is closed.
	 *  @return True, if closed.
	 */
	public boolean isClosed()
	{
		return sock.isClosed();
	}
	
	/**
	 *  Close the connection.
	 */
	public void close()
	{
		try
		{
			sock.close();
		}
		catch(IOException e)
		{
			//e.printStackTrace();
		}
		cleaner.remove();
	}
}
