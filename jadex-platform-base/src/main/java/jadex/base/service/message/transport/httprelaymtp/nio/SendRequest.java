package jadex.base.service.message.transport.httprelaymtp.nio;

import jadex.base.service.message.ManagerSendTask;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.concurrent.Token;
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
	
	/** The relay server address (host/port). */
	protected Tuple2<String, Integer>	address;
	
	/** The current buffer. */
	protected int	bufnum;
	
	/** The token to acquire for avoiding duplicate sending. */
	protected Token	token;
	
	/** True, if the token has been acquired. */
	protected boolean	hastoken;
	
	/** The future to be informed, when sending is finished. */
	protected Future<Void>	fut;
	
	/** The HTTP response as it becomes available. */
	protected StringBuffer	response;
	
	/** The expected response length. */
	protected int length;
	
	/** The idle flag. */
	protected boolean	idle;
	
	//-------- constructors ---------
	
	/**
	 *  Create a send request.
	 */
	public SendRequest(ManagerSendTask task, Token token, Future<Void> fut, Tuple2<String, Integer> address, String path, Logger logger)
	{
		this.logger	= logger;
		this.buffers	= new LinkedList<ByteBuffer>();
		this.fut	= fut;
		this.token	= token;
		this.address	= address;
		
		// Only called with receivers on same platform, therefore safe to get root id from first receiver.
		IComponentIdentifier	recid	= task.getReceivers()[0].getRoot();
		byte[]	id	= JavaWriter.objectToByteArray(recid, getClass().getClassLoader());
		byte[]	prolog	= task.getProlog();
		byte[]	data	= task.getData();
		byte[]	header	= 
			( "POST "+path+" HTTP/1.1\r\n"
			+ "Content-Type: application/octet-stream\r\n"
			+ "Host: "+address.getFirstEntity()+":"+address.getSecondEntity()+"\r\n"
			+ "Content-Length: "+(4+id.length+4+prolog.length+data.length)+"\r\n"
			+ "\r\n"
			).getBytes();

		buffers.add(ByteBuffer.wrap(header));
		buffers.add(ByteBuffer.wrap(SUtil.intToBytes(id.length)));
		buffers.add(ByteBuffer.wrap(id));
		buffers.add(ByteBuffer.wrap(SUtil.intToBytes(prolog.length+data.length)));
		buffers.add(ByteBuffer.wrap(prolog));
		buffers.add(ByteBuffer.wrap(data));
		
//		System.out.println("Try sending with relay: "+SUtil.arrayToString(task.getReceivers()));

	}
	
	//-------- IHttpRequest interface --------
	
	/**
	 *  Get the host/port pair to connect to.
	 */
	public Tuple2<String, Integer>	getAddress()
	{
		return address;
	}
	
	/**
	 *  Let the request know that it is running on a (potentially closed) idle connection.
	 *  The request might want to reschedule, e.g. only if an error occured on an idle connection.
	 */
	public void	setIdle(boolean idle)
	{
		this.idle	= idle;
	}
	
	/**
	 *  Reschedule the request in case of connection inactivity?
	 */
	public boolean	reschedule()
	{
		// Reschedule when idle.
		return idle;
	}
	
	/**
	 *  Called before read/write operations.
	 *  Also called after the request has been rescheduled in case of errors.
	 */
	public void	initRequest()
	{
		bufnum	= 0;
		for(int i=0; i<buffers.size(); i++)
			buffers.get(i).rewind();
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

		hastoken	= hastoken || token.acquire();
		if(hastoken)
		{
//			System.out.println("Sending with relay: "+address);
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
				key.cancel();
				if(idle)
				{
					reschedule	= 0;
					logger.info("nio-relay rescheduling message due to failed request on idle connection: "+e);
				}
				else
				{
					fut.setException(e);
				}
			}
		}
		else
		{
//			System.out.println("Not sending with relay: "+address);
			key.interestOps(0);
			fut.setException(new RuntimeException("Not sending."));
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
			
			// Complete HTTP response header -> message sending completed.
			if(!fut.isDone() && response.indexOf("\r\n\r\n")!=-1)
			{
				int	cl	= response.indexOf("Content-Length:");
				boolean	close	= response.indexOf("Connection: close")!=-1
					|| cl==-1;	// Close connection when no content length supplied, because we don't know how much to read until next request.
				
				if(close)
				{
//					System.out.println("connection closed");
					sc.close();
					key.cancel();
				}
				else
				{
					// Expected response length is content length plus header length.
					length	= Integer.parseInt(response.substring(cl+15, response.indexOf("\r\n", cl+15)).trim())
						+ response.indexOf("\r\n\r\n")+4;
					if(length<=response.length())
					{
						// Response completely read -> socket channel now available for next request.
						key.interestOps(0);
					}
				}
				
				if("HTTP/1.1 200 OK".equals(response.substring(0, response.indexOf("\r\n"))))
				{
					fut.setResult(null);
				}
				else
				{
					fut.setException(new IOException("HTTP response: "+response));
//					if(length<=response.length())
//					{
//						System.out.println("Complete error response: "+response);
//					}
//					else
//					{
//						System.out.println("Incomplete error response: "+response);
//					}
				}
					
			}

			// Received rest of content -> free connection
			else if(fut.isDone() && length<=response.length())
			{
				// Response completely read -> socket channel now available for next request.
				key.interestOps(0);
			}
		}
		catch(Exception e)
		{
			// Message send failed: retry.
			key.cancel();
			if(idle)
			{
				reschedule	= 0;
				logger.info("nio-relay rescheduling message due to failed response on idle connection: "+e);
			}
			else
			{
				fut.setException(e);
			}
		}
		
		return reschedule;
	}
}
