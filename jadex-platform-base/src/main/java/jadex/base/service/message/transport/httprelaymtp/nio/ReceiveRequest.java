package jadex.base.service.message.transport.httprelaymtp.nio;

import jadex.base.service.message.transport.httprelaymtp.SRelay;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.message.IMessageService;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.xml.bean.JavaWriter;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.nio.channels.SelectionKey;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.logging.Logger;

/**
 *  Handler for a send request.
 */
public class ReceiveRequest	implements IHttpRequest
{
	//-------- constants --------
	
	/** 2 kB as NIO buffer */
	static final int BUFFER_SIZE	= 1024 * 2;
	
	/** The expected response. */
	public static final byte[]	HTTP_OK	= "HTTP/1.1 200 OK\r\n".getBytes(Charset.forName("UTF-8"));
	
	/** The end of the header marker. */
	public static final byte[]	HEADER_END	= "\r\n\r\n".getBytes(Charset.forName("UTF-8"));
	
	/** The end of the chunk header marker. */
	public static final byte[]	CHUNK_HEADER_END	= "\r\n".getBytes(Charset.forName("UTF-8"));
	
	/** The initial state. */
	public static final int	STATE_INITIAL	= 0; 
	
	/** The state for reading the http ok line. */
	public static final int	STATE_READING_OK_HEADER	= 1; 
	
	/** The state for reading the rest of the response header (ended by '\r\n\r\n). */
	public static final int	STATE_READING_HEADER_REST	= 2; 
	
	/** The state for reading chunk header ('length'\r\n). */
	public static final int	STATE_READING_CHUNK_HEADER	= 3; 
	
	/** The state for reading message header (msgtype byte). */
	public static final int	STATE_READING_MSG_HEADER	= 4; 
	
	/** The state for reading message length (int as 4 bytes). */
	public static final int	STATE_READING_MSG_LENGTH	= 5; 
	
	/** The state for reading message body (binary). */
	public static final int	STATE_READING_MSG_BODY	= 6;
	
	/** The state for reading chunk end (\r\n). */
	public static final int	STATE_READING_CHUNK_END	= 7; 
	
	//-------- attributes --------

	/** The message service for delivering received messages. */
	protected IMessageService	ms;
	
	/** The logger. */
	protected Logger	logger;
	
	/** The component id. */
	protected IComponentIdentifier	cid;

	/** The relay server address (host/port). */
	protected Tuple2<String, Integer>	address;
	
	/** The relay server URL path. */
	protected String	path;
	
	/** The data to be sent or being received. */
	protected ByteBuffer	buf;
	
	/** The state of the receiving protocol. */
	protected int	state;
	
	/** The saved state of the receiving protocol when interrupted by chunk end. */
	protected int	oldstate;
	
	/** The amount of bytes matched for a header end marker. */
	protected int	matchpos;
	
	/** The remaining amount of bytes in the current chunk. */
	protected int	chunksize;
	
	/** The currently read chunk head data. */
	protected byte[]	chunkhead;

	/** The amount of data already available (0..data.length). */
	protected int	chunkheadpos;
	
	/** The currently read msg data. */
	protected byte[]	msg;
	
	/** The amount of data already available (0..data.length). */
	protected int	msgpos;
	
	/** The number of received messages (for testing). */
	protected int	received;
	
	//-------- constructors ---------
	
	/**
	 *  Create a send request.
	 */
	public ReceiveRequest(IComponentIdentifier cid, String host, int port, String path, IMessageService ms, Logger logger)	throws IOException
	{
		this.ms	= ms;
		this.logger	= logger;
		this.cid	= cid;
		this.address	= new Tuple2<String, Integer>(host, new Integer(port));
		this.path	= path;
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
	 *  Handle connection success or error.
	 *  Has to change the interest to OP_WRITE, if connection was successful.
	 *  
	 *  @return	In case of errors may request to be rescheduled on a new connection:
	 *    -1 no reschedule, 0 immediate reschedule, >0 reschedule after delay (millis.)
	 */
	public int handleConnect(SelectionKey key)
	{
		int	reschedule	= -1;
		try
		{
			SocketChannel	sc	= (SocketChannel)key.channel();
			boolean	finished	= sc.finishConnect();
			assert finished;
			key.interestOps(SelectionKey.OP_WRITE);
			String	xmlid	= JavaWriter.objectToXML(cid, getClass().getClassLoader());
			byte[]	header	= 
				( "GET "+path+"?id="+URLEncoder.encode(xmlid, "UTF-8")+" HTTP/1.1\r\n"
				+ "Host: "+address.getFirstEntity()+":"+address.getSecondEntity()+"\r\n"
				+ "\r\n"
				).getBytes(Charset.forName("UTF-8"));
			buf	= ByteBuffer.wrap(header);
			state	= STATE_INITIAL;
		}
		catch(Exception e)
		{
			logger.info("Could not connect to relay server (re-attempting in 30 seconds): "+e);
			key.cancel();
			reschedule	= 30000;
		}
		return reschedule;
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
		int	reschedule	= -1;
		
		SocketChannel	sc	= (SocketChannel)key.channel();
//		System.out.println("Sending "+this+" on: "+sc);

		try
		{
			sc.write(buf);

			// Buffer written: stop sending and register interest in answer.
			if(buf.remaining()==0)
			{
				buf	= null;
				key.interestOps(SelectionKey.OP_READ);
			}
		}
		catch(Exception e)
		{
			reschedule	= 0;
			logger.info("Request error (reconnect immediately): "+e);
//			e.printStackTrace();
			key.cancel();
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
			int	bufpos	= 0;
			boolean	wait	= false;
			while(!wait)
			{
				if(state==STATE_INITIAL)
				{
					buf	= ByteBuffer.allocate(HTTP_OK.length);
					state	= STATE_READING_OK_HEADER;
				}
				
				if(state==STATE_READING_OK_HEADER)
				{
//					System.out.println("reading ok header");
					int	r	= sc.read(buf);
					if(r==-1)
						throw new IOException("Stream closed");
					if(buf.remaining()==0)
					{
						if(!Arrays.equals(HTTP_OK, buf.array()))
							throw new IOException("HTTP response: "+new String(buf.array(), Charset.forName("UTF-8")));
						state	= STATE_READING_HEADER_REST;
						buf	= ByteBuffer.allocate(BUFFER_SIZE);
						matchpos	= 2; // Matched already two characters from potential header end.
//						System.out.println("found ok header");
					}
					else
					{
						// wait for more data
						wait	= true;
					}
				}
				
				if(state==STATE_READING_HEADER_REST)
				{
//					System.out.println("reading header rest");
					int	r	= sc.read(buf);
					if(r==-1)
						throw new IOException("Stream closed");
					byte[]	bufa	= buf.array();
					bufpos	= 0;
					for(; matchpos!=4 && bufpos<buf.position(); bufpos++)
					{
						if(bufa[bufpos]==HEADER_END[matchpos])
						{
							matchpos++;
						}
						else
						{
							matchpos	= 0;
						}
					}
					
					if(matchpos==4)
					{
						state	= STATE_READING_CHUNK_HEADER;
						oldstate	= STATE_READING_MSG_HEADER;	// After chunk header start reading messages.
						matchpos	= 0;
						chunkhead	= new byte[12]; // 10 for number (max int) + 2 for header end
						if(bufpos==buf.position())
						{
							buf.clear();
							bufpos	= 0;
						}
//						System.out.println("read header rest");
					}
					else if(buf.remaining()==0)
					{
						buf.clear();
						bufpos	= 0;
					}
					else
					{
						buf.clear();
						bufpos	= 0;
						wait	= true;
					}
				}
				
				if(state==STATE_READING_CHUNK_HEADER)
				{
//					System.out.println("reading chunk header");
					if(bufpos==0)
					{
						int	r	= sc.read(buf);
						if(r==-1)
							throw new IOException("Stream closed");
					}
					byte[]	bufa	= buf.array();
					for(; matchpos!=2 && bufpos<buf.position(); bufpos++)
					{
						chunkhead[chunkheadpos++]	= bufa[bufpos];
						if(bufa[bufpos]==CHUNK_HEADER_END[matchpos])
						{
							matchpos++;
						}
						else
						{
							matchpos	= 0;
						}
					}
					
					if(matchpos==2)
					{
						// Get chunk header as string.
						String	s	= new String(chunkhead, 0, chunkheadpos-2, Charset.forName("UTF-8"));
						// Strip chunk extension (if any).
						if(s.indexOf(';')!=-1)
							s.substring(0, s.indexOf(';'));
						
						// Chunksize is encoded as string representing a hex value.
						chunksize	= Integer.parseInt(s, 16);
						matchpos	= 0;
						chunkheadpos	= 0;
						state	= oldstate;
						if(bufpos==buf.position())
						{
							buf.clear();
							bufpos	= 0;
						}
//						System.out.println("read chunk header");
					}
					else if(buf.remaining()==0)
					{
						buf.clear();
						bufpos	= 0;
					}
					else
					{
						buf.clear();
						bufpos	= 0;
						wait	= true;
					}
				}
				
				if(state==STATE_READING_MSG_HEADER)
				{
//					System.out.println("reading msg header");
					if(bufpos==0)
					{
						int	r	= sc.read(buf);
						if(r==-1)
							throw new IOException("Stream closed");
					}
					
					if(buf.position()>bufpos)
					{
						byte	msghead	= buf.array()[bufpos];
						bufpos++;
						chunksize--;
						if(msghead==SRelay.MSGTYPE_PING)
						{
//							System.out.println("read ping msg header");
							state	= STATE_READING_MSG_HEADER;
						}
						else if(msghead==SRelay.MSGTYPE_DEFAULT)
						{
//							System.out.println("read default msg header");
							state	= STATE_READING_MSG_LENGTH;
							msg	= new byte[4];
							msgpos	= 0;
						}
						
						if(chunksize==0)
						{
							oldstate	= state;
							state	= STATE_READING_CHUNK_END;
						}
						
						if(bufpos==buf.position())
						{
							buf.clear();
							bufpos	= 0;
						}
					}
					else
					{
						buf.clear();
						bufpos	= 0;
						wait	= true;
					}
				}

				if(state==STATE_READING_MSG_LENGTH)
				{
//					System.out.println("reading msg length");
					if(bufpos==0)
					{
						int	r	= sc.read(buf);
						if(r==-1)
							throw new IOException("Stream closed");
					}
					
					byte[]	bufa	= buf.array();
					for(; chunksize>0 && msgpos<4 && bufpos<buf.position(); bufpos++)
					{
						msg[msgpos++]	= bufa[bufpos];
						chunksize--;
					}
					
					if(msgpos==4)
					{
//						System.out.println("read msg length");
						state	= STATE_READING_MSG_BODY;
						msg	= new byte[SUtil.bytesToInt(msg)];
						msgpos	= 0;
					}

					if(chunksize==0)
					{
						oldstate	= state;
						state	= STATE_READING_CHUNK_END;
					}
					
					if(bufpos==buf.position())
					{
						if(buf.remaining()>0)
						{
							wait	= true;
						}
						buf.clear();
						bufpos	= 0;
					}
				}
				
				if(state==STATE_READING_MSG_BODY)
				{
//					System.out.println("reading msg body");
					if(bufpos==0)
					{
						int	r	= sc.read(buf);
						if(r==-1)
							throw new IOException("Stream closed");
					}
					
					int	length	= Math.min(chunksize, Math.min(msg.length-msgpos, buf.position()-bufpos));
					System.arraycopy(buf.array(), bufpos, msg, msgpos, length);
					chunksize -= length;
					bufpos	+= length;
					msgpos	+= length;
					
					if(msgpos==msg.length)
					{
						received++;
//						System.out.println("read msg body: "+received);
						// Message read.
						ms.deliverMessage(msg);
						msg	= null;
						msgpos	= 0;
						state	= STATE_READING_MSG_HEADER;
					}
					
					if(chunksize==0)
					{
						oldstate	= state;
						state	= STATE_READING_CHUNK_END;						
					}
					
					if(bufpos==buf.position())
					{
						if(buf.remaining()>0)
						{
							wait	= true;
						}
						buf.clear();
						bufpos	= 0;
					}
				}
				
				if(state==STATE_READING_CHUNK_END)
				{
//					System.out.println("reading chunk end");
					if(bufpos==0)
					{
						int	r	= sc.read(buf);
						if(r==-1)
							throw new IOException("Stream closed");
					}
					
					while(bufpos<buf.position() && matchpos<2)
					{
						if(buf.array()[bufpos]!=CHUNK_HEADER_END[matchpos])
							throw new IOException("Expected chunk end '\\r\\n'");
						bufpos++;
						matchpos++;
					}
					
					if(matchpos==2)
					{
//						System.out.println("read chunk end");
						state	= STATE_READING_CHUNK_HEADER;
						matchpos	= 0;
					}
					
					if(bufpos==buf.position())
					{
						if(buf.remaining()>0)
						{
							wait	= true;
						}
						buf.clear();
						bufpos	= 0;
					}
				}

			}
		}
		catch(Exception e)
		{
			reschedule	= 0;
//			e.printStackTrace();
			logger.info("Response error (reconnecting immediately): "+e);
			key.cancel();
		}
		
		return reschedule;
	}
}
