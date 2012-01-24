package jadex.base.service.message.transport.httprelaymtp.nio;

import jadex.base.service.message.ManagerSendTask;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.xml.bean.JavaWriter;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.util.LinkedList;
import java.util.List;
import java.util.logging.Logger;

/**
 *  Handler for a send request.
 */
public class SendRequest	implements IHttpRequest
{
	//-------- attributes --------

	/** The data to be sent. */
	protected List<ByteBuffer>	buffers;
	
	/** The logger. */
	protected Logger	logger;
	
	/** The relay server host. */
	protected String	host;
	
	/** The relay server port. */
	protected int	port;
	
	/** The current buffer. */
	protected int	bufnum;
	
	/** The future to be informed, when sending is finished. */
	protected Future<Void>	fut;
	
	/** The HTTP response as it becomes available. */
	protected StringBuffer	response;
	
	//-------- constructors ---------
	
	/**
	 *  Create a send request.
	 */
	public SendRequest(ManagerSendTask task, Future<Void> fut, String host, int port, String path, Logger logger)
	{
		this.logger	= logger;
		this.buffers	= new LinkedList<ByteBuffer>();
		this.fut	= fut;
		this.host	= host;
		this.port	= port;
		
		// Only called with receivers on same platform, therefore safe to get root id from first receiver.
		IComponentIdentifier	recid	= task.getReceivers()[0].getRoot();
		byte[]	id	= JavaWriter.objectToByteArray(recid, getClass().getClassLoader());
		byte[]	prolog	= task.getProlog();
		byte[]	data	= task.getData();
		byte[]	header	= 
			( "POST "+path+" HTTP/1.1\r\n"
			+ "Content-Type: application/octet-stream\r\n"
			+ "Host: "+host+":"+port+"\r\n"
			+ "Content-Length: "+(4+id.length+4+prolog.length+data.length)+"\r\n"
			+ "\r\n"
			).getBytes();

		buffers.add(ByteBuffer.wrap(header));
		buffers.add(ByteBuffer.wrap(SUtil.intToBytes(id.length)));
		buffers.add(ByteBuffer.wrap(id));
		buffers.add(ByteBuffer.wrap(SUtil.intToBytes(prolog.length+data.length)));
		buffers.add(ByteBuffer.wrap(prolog));
		buffers.add(ByteBuffer.wrap(data));
	}
	
	//-------- IHttpRequest interface --------
	
	/**
	 *  Get the host to connect to.
	 */
	public String	getHost()
	{
		return host;
	}
	
	/**
	 *  Get the port to connect to.
	 */
	public int	getPort()
	{
		return port;
	}
	
	/**
	 *  Handle connection success or error.
	 *  Has to change the interest to OP_WRITE, if connection was successful.
	 *  
	 *  @return	In case of errors may request to be rescheduled on a new connection:
	 *    -1 no reschedule, 0 immediate reschedule, >0 reschedule after delay (millis.)
	 */
	public int handleConnect(SelectionKey key)
	{
		try
		{
			SocketChannel	sc	= (SocketChannel)key.channel();
			boolean	finished	= sc.finishConnect();
			assert finished;
			key.interestOps(SelectionKey.OP_WRITE);
		}
		catch(Exception e)
		{
			logger.info("Message could not be sent: "+e);
			fut.setException(e);
			key.cancel();
		}
		return -1;
	}
	
	/**
	 *  Write the HTTP request to the NIO connection.
	 *  May be called multiple times, if not all data can be send at once.
	 *  Has to change the interest to OP_READ, once all data is sent.
	 *  
	 *  @return	In case of errors may request to be rescheduled on a new connection:
	 *    -1 no reschedule, 0 immediate reschedule, >0 reschedule after delay (millis.)
	 */
	public int handleWrite(SelectionKey key)
	{
		int reschedule	= -1;
		SocketChannel	sc	= (SocketChannel)key.channel();
//		System.out.println("Sending "+this+" on: "+sc);

		try
		{
			boolean	more	= true;
			while(more)
			{
				ByteBuffer	buf	= buffers.get(bufnum);
				sc.write(buf);

				// Output buffer is full: stop sending for now, but keep interest.
				if(buf.remaining()>0)
				{
					// Output buffer is full: stop sending for now, but keep interest.
					more	= false;
				}
				
				// One buffer written: continue with next.
				else if(bufnum<buffers.size()-1)
				{
					bufnum++;
				}
				
				// All buffers written: stop sending and register interest in answer.
				else
				{
//					System.out.println("Message sent");
					more	= false;
					key.interestOps(SelectionKey.OP_READ);
				}
			}
		}
		catch(Exception e)
		{
			// Request problem (e.g. reused connection already closed): try again.
			bufnum	= 0;
			for(int i=0; i<buffers.size(); i++)
				buffers.get(i).rewind();
			reschedule	= 0;
			key.cancel();
			logger.info("rescheduling message due to failed request: "+e);
		}
		
		return reschedule;
	}
	
	
	/**
	 *  Receive the HTTP response from the NIO connection.
	 *  May be called multiple times, if not all data can be send at once.
	 *  Has to deregister interest in the connection, once required data is received.
	 *  May close the connection or leave it open for reuse if the server supports keep-alive.
	 *  
	 *  @return	In case of errors may request to be rescheduled on a new connection:
	 *    -1 no reschedule, 0 immediate reschedule, >0 reschedule after delay (millis.)
	 */
	public int	handleRead(SelectionKey key)
	{
		int reschedule	= -1;
		SocketChannel	sc	= (SocketChannel)key.channel();
//		System.out.println("Receiving "+this+" on: "+sc);
		
		try
		{
			if(response==null)
				response	= new StringBuffer();
			
			// Can read all data available as there can be only one response.
			ByteBuffer	inbuf = ByteBuffer.allocate(256);
			int	read	= sc.read(inbuf);
			if(read==-1)
				throw new IOException("Stream closed");
			while(read>0)
			{
				response.append(new String(inbuf.array(), 0, read));
				inbuf.clear();
				read	= sc.read(inbuf);
				if(read==-1)
					throw new IOException("Stream closed");
			}
			
			// Complete HTTP response
			if(response.lastIndexOf("\r\n\r\n")!=-1)
			{
				boolean	close	= response.indexOf("Connection: close")!=-1;
				if(close)
				{
//					System.out.println("connection closed");
					sc.close();
					key.cancel();
				}
				else
				{
					// No more interest in reading.
					key.interestOps(0);
				}
				
				if("HTTP/1.1 200 OK".equals(response.substring(0, response.indexOf("\r\n"))))
				{
					fut.setResult(null);
					
				}
				else
				{
					fut.setException(new IOException("HTTP response: "+response));
				}
					
//				System.out.println("Received response: "+received);
			}
		}
		catch(Exception e)
		{
			// Message send failed: retry.
			bufnum	= 0;
			for(int i=0; i<buffers.size(); i++)
				buffers.get(i).rewind();
			reschedule	= 0;
			key.cancel();
			logger.info("rescheduling message due to failed response: "+e);
		}
		
		return reschedule;
	}
}
