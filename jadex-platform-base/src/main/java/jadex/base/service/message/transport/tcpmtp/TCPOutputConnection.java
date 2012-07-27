package jadex.base.service.message.transport.tcpmtp;

import jadex.base.service.message.ISendTask;
import jadex.base.service.message.streams.StreamSendTask;
import jadex.base.service.message.transport.tcpmtp.TCPTransport.Cleaner;
import jadex.commons.SUtil;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**
 *  TCP output connection for sending messages to a specific target address. 
 */
public class TCPOutputConnection
{
	//-------- constants --------
	
	/** 5 sec timeout. */
	public static final int	TIMEOUT	= 5000;
	
	//-------- attributes --------
	
	/** The client socket for sending data. */
	protected Socket sock;

	/** The output stream. */
	protected OutputStream sos;
	
	/** The cleaner. */
	protected Cleaner cleaner;
	
	//-------- constructors --------
	
	/**
	 *  Create a new tcp connection for sending data. 
	 */
//	public TCPOutputConnection(InetAddress iaddr, int iport, Cleaner cleaner, Socket sock) throws IOException
	public TCPOutputConnection(Cleaner cleaner, Socket sock) throws IOException
	{
		this.sock = sock;
		
		// Wait for handshake byte.
//		sock.getInputStream().read();
		
//		try
//		{
//			System.out.println("TCP Connection: "+iaddr+":"+iport);
//			this.sock = new Socket();
//			sock.connect(new InetSocketAddress(iaddr, iport), TIMEOUT);
//			System.out.println("TCP Connection: "+iaddr+":"+iport+" established");
//		}
//		catch(IOException e)
//		{
//			System.out.println("TCP Connection: "+iaddr+":"+iport+" failed");
////			e.printStackTrace();
//			throw e;
//		}
		this.sos = new BufferedOutputStream(sock.getOutputStream());
		this.cleaner = cleaner;
		//address = SMTransport.SERVICE_SCHEMA+iaddr.getHostAddress()+":"+iport;
	}

	//-------- methods --------
	
	/**
	 *  Send a message.
	 *  @param msg The message.
	 *  (todo: relax synchronization by performing sends 
	 *  on extra sender thread of transport)
	 */
	public synchronized boolean send(byte[] prolog, byte[] data, ISendTask task)
	{
		boolean ret = false;
		
		try
		{
//			if(task instanceof StreamSendTask)
//			{
//				System.out.println("connection.send0 "+System.currentTimeMillis()+": "+((StreamSendTask)task).getSequenceNumber());
//			}
			sos.write(SUtil.intToBytes(prolog.length+data.length));
//			if(task instanceof StreamSendTask)
//			{
//				System.out.println("connection.send1 "+System.currentTimeMillis()+": "+((StreamSendTask)task).getSequenceNumber());
//			}
			sos.write(prolog);
//			if(task instanceof StreamSendTask)
//			{
//				System.out.println("connection.send2 "+System.currentTimeMillis()+": "+((StreamSendTask)task).getSequenceNumber());
//			}
			sos.write(data);
//			if(task instanceof StreamSendTask)
//			{
//				System.out.println("connection.send3 "+System.currentTimeMillis()+": "+((StreamSendTask)task).getSequenceNumber());
//			}
			sos.flush();
//			if(task instanceof StreamSendTask)
//			{
//				System.out.println("connection.sent "+System.currentTimeMillis()+": "+((StreamSendTask)task).getSequenceNumber());
//			}
			ret = true;
//			cleaner.refresh();
		}
		catch(IOException e)
		{
//			e.printStackTrace();
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
