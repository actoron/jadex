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

/**
 *  Handler for a send request.
 */
public class SendRequest	implements IHttpRequest
{
	//-------- attributes --------

	/** The data to be sent. */
	protected List<ByteBuffer>	buffers;
	
	/** The future to be informed, when sending is finished. */
	protected Future<Void>	fut;
	
	/** The HTTP response as it becomes available. */
	protected StringBuffer	response;
	
	//-------- constructors ---------
	
	/**
	 *  Create a send request.
	 */
	public SendRequest(ManagerSendTask task, Future<Void> fut, String host, int port, String path)
	{
		this.buffers	= new LinkedList<ByteBuffer>();
		this.fut	= fut;
		
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
	 *  Write the HTTP request to the NIO connection.
	 *  May be called multiple times, if not all data can be send at once.
	 *  Has to change the interest to OP_READ, once all data is sent.
	 */
	public void handleWrite(SelectionKey key)
	{
		SocketChannel	sc	= (SocketChannel)key.channel();
//		System.out.println("Sending on "+sc);

		try
		{
			boolean	more	= true;
			while(more)
			{
				ByteBuffer	buf	= buffers.get(0);
				sc.write(buf);

				// Output buffer is full: stop sending for now, but keep interest.
				if(buf.remaining()>0)
				{
					// Output buffer is full: stop sending for now, but keep interest.
					more	= false;
				}
				
				// One buffer written: continue with next.
				else if(buffers.size()>1)
				{
					buffers.remove(0);
				}
				
				// All buffers written: stop sending and register interest in answer.
				else
				{
//					System.out.println("Message sent");
					more	= false;
					buffers.remove(0);
					key.interestOps(SelectionKey.OP_READ);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fut.setException(e);
			key.cancel();
		}
	}
	
	/**
	 *  Receive the HTTP response from the NIO connection.
	 *  May be called multiple times, if not all data can be send at once.
	 *  Has to deregister interest in the connection, once required data is received.
	 *  May close the connection or leave it open for reuse if the server supports keepalive.
	 */
	public void	handleRead(SelectionKey key)
	{
		SocketChannel	sc	= (SocketChannel)key.channel();
//		System.out.println("Reading from "+sc);
		
		try
		{
			if(response==null)
				response	= new StringBuffer();
			
			// Can read all data available as there can be only one response.
			ByteBuffer	inbuf = ByteBuffer.allocate(256);
			int	read	= sc.read(inbuf);
			while(read>0)
			{
				response.append(new String(inbuf.array(), 0, read));
				inbuf.clear();
				read	= sc.read(inbuf);
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
				
				if(!"HTTP/1.1 200 OK".equals(response.substring(0, response.indexOf("\r\n"))))
					throw new IOException("HTTP response: "+response);
					
				fut.setResult(null);
//				System.out.println("Received response: "+received);
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
			fut.setException(e);
			key.cancel();
		}
	}
}
