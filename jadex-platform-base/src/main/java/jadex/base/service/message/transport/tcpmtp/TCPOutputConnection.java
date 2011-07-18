package jadex.base.service.message.transport.tcpmtp;

import jadex.base.service.message.transport.MessageEnvelope;
import jadex.base.service.message.transport.codecs.CodecFactory;
import jadex.base.service.message.transport.codecs.ICodec;
import jadex.base.service.message.transport.tcpmtp.TCPTransport.Cleaner;
import jadex.commons.SUtil;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

/**
 *  TCP output connection for sending messages to a specific target address. 
 */
class TCPOutputConnection
{
	//-------- constants --------
	
	/** 5 sec timeout. */
	public static final int	TIMEOUT	= 5000;
	
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
//		try
//		{
//			System.out.println("TCP Connection: "+iaddr+":"+iport);
			this.sock = new Socket();
			sock.connect(new InetSocketAddress(iaddr, iport), TIMEOUT);
//			System.out.println("TCP Connection: "+iaddr+":"+iport+" established");
//		}
//		catch(IOException e)
//		{
//			System.out.println("TCP Connection: "+iaddr+":"+iport+" failed");
////			e.printStackTrace();
//			throw e;
//		}
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
	public synchronized boolean send(MessageEnvelope msg, byte[] codecids)
	{
		boolean ret = false;
		
		try
		{
			if(codecids==null || codecids.length==0)
				codecids = codecfac.getDefaultCodecIds();

			Object enc_msg = msg;
			for(int i=0; i<codecids.length; i++)
			{
				ICodec codec = codecfac.getCodec(codecids[i]);
				enc_msg	= codec.encode(enc_msg, classloader);
			}
			byte[] res = (byte[])enc_msg;
			
			int dynlen = TCPTransport.PROLOG_SIZE+1+codecids.length;
			int size = res.length+dynlen;
//			System.out.println("len: "+size);
			sos.write((byte)codecids.length);
			sos.write(codecids);
			sos.write(SUtil.intToBytes(size));
			sos.write(res);
			sos.flush();
			ret = true;
			/* $if !android $ */
			cleaner.refresh();
			/* $endif $ */
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
		/* $if !android $ */
		cleaner.remove();
		/* $endif $ */
	}
}
