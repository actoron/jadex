package jadex.base.relay;

import jadex.base.service.message.transport.MessageEnvelope;
import jadex.bridge.ComponentIdentifier;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.types.awareness.AwarenessInfo;
import jadex.bridge.service.types.message.ICodec;
import jadex.commons.SUtil;
import jadex.commons.collection.ArrayBlockingQueue;
import jadex.commons.collection.IBlockingQueue;
import jadex.commons.concurrent.TimeoutException;
import jadex.commons.future.ThreadSuspendable;
import jadex.commons.transformation.binaryserializer.BinarySerializer;
import jadex.platform.service.message.MapSendTask;
import jadex.platform.service.message.transport.codecs.CodecFactory;
import jadex.platform.service.message.transport.httprelaymtp.RelayConnectionManager;
import jadex.platform.service.message.transport.httprelaymtp.SRelay;
import jadex.xml.bean.JavaReader;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;


/**
 *  Basic relay functionality to be used with or without servlet.
 */
public class RelayHandler
{
	//-------- constants --------

	/** The directory for settings and statistics. */ 
	public final static File	SYSTEMDIR;
	
	static
	{
		File dir;
		String	home	= System.getenv("RELAY_HOME");	// System.getProperty() does not return environment variables, but just server VM properties.
		if(home!=null)
		{
			dir	= new File(home);
		}
		else
		{
			dir	= new File(System.getProperty("user.home"), ".relaystats");
		}
		
		if(!dir.exists())
		{
			dir.mkdirs();
		}
		else if(!dir.isDirectory())
		{
			throw new RuntimeException("Settings path '"+dir+"' is not a directory.");
		}
		SYSTEMDIR	= dir;
		
		getLogger().info("Relay settings directory: "+SYSTEMDIR.getAbsolutePath());
	}
	
	//-------- attributes --------
	
	/** The relay map (id -> queue for pending requests). */
	protected Map<String, IBlockingQueue<Message>>	map;
	
	/** Info about connected platforms.*/
	protected Map<Object, PlatformInfo>	platforms;
	
	/** The available codecs for awareness infos (cached for speed). */
	protected Map<Byte, ICodec>	codecs;
	
	/** The peer list. */
	protected PeerList	peers;	
	
	//-------- constructors --------

	/**
	 *  Initialize the handler.
	 */
	public RelayHandler()
	{
		this.map	= Collections.synchronizedMap(new HashMap<String, IBlockingQueue<Message>>());
		this.platforms	= Collections.synchronizedMap(new LinkedHashMap<Object, PlatformInfo>());
		CodecFactory	cfac	= new CodecFactory();
		this.codecs	= cfac.getAllCodecs();
		this.peers	= new PeerList();
	}
	
	/**
	 *  Cleanup on shutdown.
	 */
	public void dispose()
	{
		if(map!=null && !map.isEmpty())
		{
			for(Iterator<IBlockingQueue<Message>> it=map.values().iterator(); it.hasNext(); )
			{
				IBlockingQueue<Message>	queue	= it.next();
				it.remove();
				List<Message>	items	= queue.setClosed(true);
				for(int i=0; i<items.size(); i++)
				{
					items.get(i).getFuture().setException(new RuntimeException("Target disconnected."));
				}
			}
		}
		if(platforms!=null && !platforms.isEmpty())
		{
			for(Iterator<PlatformInfo> it=platforms.values().iterator(); it.hasNext(); )
			{
				it.next().disconnect();	// Writes end time in DB.
			}
		}
		StatsDB.getDB().shutdown();
		this.peers.dispose();
	}
	
	//-------- methods --------
	
	/**
	 *  Called when a platform registers itself at the relay. 
	 *  Initializes required data structures, such that messages can be queued.
	 */
	public void initConnection(String id, String hostip, String hostname, String protocol)
	{
		PlatformInfo	info	= platforms.get(id);
		if(info==null)
		{
			info	= new PlatformInfo(id, hostip, hostname, protocol);
			platforms.put(id, info);
		}
		else
		{
			info.reconnect(hostip, hostname);
		}
				
		IBlockingQueue<Message>	queue	= map.get(id);
		if(queue!=null)
		{
			// Close old queue to free old servlet request
			List<Message>	items	= queue.setClosed(true);
			queue	= 	new ArrayBlockingQueue<Message>();
			// Add outstanding requests to new queue.
			for(int i=0; i<items.size(); i++)
			{
				queue.enqueue(items.get(i));
			}
		}
		else
		{
			queue	= 	new ArrayBlockingQueue<Message>();
		}
		map.put(id, queue);
				
//		// Set cache header to avoid interference of proxies (e.g. vodafone umts)
//		response.setHeader("Cache-Control", "no-cache, no-transform");
//		response.setHeader("Pragma", "no-cache");
		getLogger().info("Client connected: '"+id+"'");//, "+client.getSendBufferSize());
	}
				
	/**
	 *  Called when a platform registers itself at the relay. 
	 *  Blocks the thread until the platform disconnects.
	 */
	public void handleConnection(String id, OutputStream out)
	{
		PlatformInfo	info	= platforms.get(id);
		IBlockingQueue<Message>	queue	= map.get(id);
		
		try
		{
			// Ping to let client know that it is connected.
			out.write(SRelay.MSGTYPE_PING);
			out.flush();
//			response.flushBuffer();
			
			while(true)
			{
				try
				{
					// Get next request from queue.
					Message	msg	= queue.dequeue(SRelay.PING_DELAY);	// Todo: make ping delay configurable on per client basis
//					System.out.println("sending data to:"+id);
					try
					{
						// Send message header.
						out.write(msg.getMessageType());
						
						// Copy message to output stream.
						long	start	= System.nanoTime();
						byte[]	buf	= new byte[8192];  
						int	len;
						int	cnt	= 0;
						while((len=msg.getContent().read(buf)) != -1)
						{
							out.write(buf, 0, len);
							cnt	+= len;
						}
						out.flush();
						info.addMessage(cnt, System.nanoTime()-start);
						msg.getFuture().setResult(null);
					}
					catch(Exception e)
					{
						msg.getFuture().setException(e);
						throw e;	// rethrow exception to end servlet execution for client.
					}
				}
				catch(TimeoutException te)
				{
					// Send ping and continue loop.
//					System.out.println("pinging: "+id);
					out.write(SRelay.MSGTYPE_PING);
					out.flush();
				}
			}
		}
		catch(Exception e)
		{
			// exception on queue, when same platform reconnects or servlet is destroyed
			// exception on output stream, when client disconnects
			getLogger().info("Client disconnected: "+id+", "+e);
		}
		
		if(!queue.isClosed())
		{
			List<Message>	items	= queue.setClosed(true);
			map.remove(id);
			PlatformInfo	platform	= platforms.remove(id);
			if(platform!=null)
				platform.disconnect();
			AwarenessInfo	awainfo	= platform!=null ? platform.getAwarenessInfo() : null;
			if(awainfo!=null)
			{
//				System.out.println("Sending offline info: "+id);
				awainfo.setState(AwarenessInfo.STATE_OFFLINE);
				sendAwarenessInfos(awainfo, platform.getPreferredCodecs());
			}
	//		System.out.println("Removed from map ("+items.size()+" remaining items). New size: "+map.size());
			for(int i=0; i<items.size(); i++)
			{
				items.get(i).getFuture().setException(new RuntimeException("Target disconnected."));
			}
		}
	}

	/**
	 *  Called when a message should be sent.
	 */
	public void handleMessage(InputStream in, String protocol) throws Exception
	{
		String	targetid	= readString(in);
		boolean	sent	= false;
		
		// Only send message when request is not https or target is also connected via https.
		PlatformInfo	targetpi	= platforms.get(targetid);
		if(targetpi!=null && (!protocol.equals("https") || targetpi.getScheme().equals("https")))
		{
			IBlockingQueue<Message>	queue	= map.get(targetid);
			if(queue!=null)
			{
				try
				{
					Message	msg	= new Message(SRelay.MSGTYPE_DEFAULT, in);
//					System.out.println("queing message to:"+targetid);
					queue.enqueue(msg);
					msg.getFuture().get(new ThreadSuspendable(), 30000);	// todo: how to set a useful timeout value!?
					sent	= true;
				}
				catch(Exception e)
				{
					// timeout or platform just disconnected
				}
			}
		}
		
		if(!sent)
		{
			throw new RuntimeException("message not sent");
		}
	}
	
	/**
	 *  Called when an awareness info is received.
	 */
	public void handleAwareness(InputStream in) throws Exception
	{
		// Read target id.
		String	id	= readString(in);
		
		// Read total message length.
		byte[]	len	= readData(in, 4);
		int	length	= SUtil.bytesToInt(len);
		
		// Read message and extract awareness info content.
		byte[] buffer = readData(in, length-1);
		MessageEnvelope	msg	= (MessageEnvelope)MapSendTask.decodeMessage(buffer, codecs, getClass().getClassLoader());
		ICodec[]	pcodecs	= MapSendTask.getCodecs(buffer, codecs);
		AwarenessInfo	info;
		if(SFipa.JADEX_RAW.equals(msg.getMessage().get(SFipa.LANGUAGE)))
		{
			info = (AwarenessInfo)msg.getMessage().get(SFipa.CONTENT);
		}
		else if(SFipa.JADEX_XML.equals(msg.getMessage().get(SFipa.LANGUAGE)))
		{
			info = (AwarenessInfo)JavaReader.objectFromByteArray((byte[])msg.getMessage().get(SFipa.CONTENT), getClass().getClassLoader());
		}
		else //if(SFipa.JADEX_BINARY.equals(msg.getMessage().get(SFipa.LANGUAGE)))
		{
			info = (AwarenessInfo)BinarySerializer.objectFromByteArray((byte[])msg.getMessage().get(SFipa.CONTENT), null, null, getClass().getClassLoader(), null);
		}
		
		PeerEntry	peer	= peers.getPeer(id);
		if(peer!=null)
		{
			peer.updateAwarenessInfo(info);
		}
		
		sendAwarenessInfos(info, pcodecs);
	}
	
	/**
	 *  Get the current platforms
	 */
	public PlatformInfo[]	getCurrentPlatforms()
	{
		return platforms.values().toArray(new PlatformInfo[0]);
	}
	
	/**
	 *  Get the available servers as comma-separated list of URLs.
	 *  Also updates the known peers, if necessary.
	 *  @param requesturl	Public URL of this relay server as known from the received request.
	 *  @param peerurl	URL of a remote peer if sent as part of the request (or null).
	 */
	public String	handleServersRequest(String requesturl, String peerurl)
	{
		if(peerurl!=null)
		{
			PeerEntry	peer	= peers.addPeer(peerurl, false);

			// Send own awareness infos to new or updated peer.
			if(peer!=null && !peer.isSent())
			{
				peer.setSent(true);
				
				for(PlatformInfo pi: getCurrentPlatforms())
				{
					AwarenessInfo	awainfo	= pi.getAwarenessInfo();
					if(awainfo!=null)
					{
						Map<String, Object>	message	= new HashMap<String, Object>();
						message.put(SFipa.LANGUAGE, SFipa.JADEX_RAW);
						message.put(SFipa.CONTENT, awainfo);
						MessageEnvelope	msg	= new MessageEnvelope(message, null, null);
						byte[]	peerinfo	= MapSendTask.encodeMessage(msg, pi.getPreferredCodecs(), getClass().getClassLoader());
								
						try
						{
							System.out.println("Sending awareness to peer: "+peer.getURL());
							new RelayConnectionManager().postMessage(peer.getURL()+"awareness", new ComponentIdentifier(peers.getUrl()), new byte[][]{peerinfo});
						}
						catch(IOException e)
						{
							System.out.println("Error sending awareness to peer: "+e);
						}					
					}
				}				
			}
		}
		return peers.getURLs(requesturl);
	}
	
	//-------- helper methods --------	

	/**
	 *  Send awareness messages for a new or changed awareness info.
	 */
	protected void	sendAwarenessInfos(AwarenessInfo awainfo, ICodec[] pcodecs)
	{
//		System.out.println("sending awareness infos: "+awainfo.getSender().getPlatformName()+", "+platforms.size());
		// Update platform awareness info.
		String	id	= awainfo.getSender().getPlatformName();
		PlatformInfo	platform	= platforms.get(id);
		boolean	initial	= platform!=null && platform.getAwarenessInfo()==null && AwarenessInfo.STATE_ONLINE.equals(awainfo.getState());
		if(platform!=null)
		{
			platform.setAwarenessInfo(awainfo);
			platform.setPreferredCodecs(pcodecs);
		}
		
		byte[]	propinfo	= null;
		byte[]	nopropinfo	= null;
		
		Map.Entry<String, IBlockingQueue<Message>>[]	entries	= map.entrySet().toArray(new Map.Entry[0]);
		for(int i=0; i<entries.length; i++)
		{
			// Send awareness to other platforms with awareness on.
			PlatformInfo	p2	= platforms.get(entries[i].getKey());
			AwarenessInfo	awainfo2	= p2!=null ? p2.getAwarenessInfo() : null;
			if(awainfo2!=null && !id.equals(entries[i].getKey()))
			{
				try
				{
					// Send awareness infos with or without properties, for backwards compatibility with Jadex 2.1
					if(awainfo2.getProperties()==null && nopropinfo==null)
					{
						AwarenessInfo	awanoprop	= awainfo;
						if(awainfo.getProperties()!=null)
						{
							awanoprop	= new AwarenessInfo(awainfo.getSender(), awainfo.getState(), awainfo.getDelay(), awainfo.getIncludes(), awainfo.getExcludes(), awainfo.getMasterId());
							awanoprop.setProperties(null);
						}
						
						byte[]	data	= MapSendTask.encodeMessage(awanoprop, pcodecs, getClass().getClassLoader());
						nopropinfo	= new byte[data.length+4];
						System.arraycopy(SUtil.intToBytes(data.length), 0, nopropinfo, 0, 4);
						System.arraycopy(data, 0, nopropinfo, 4, data.length);
						
						if(awainfo.getProperties()==null)
						{
							propinfo	= nopropinfo;
						}

					}
					else if(awainfo2.getProperties()!=null && propinfo==null)
					{
						byte[]	data	= MapSendTask.encodeMessage(awainfo, pcodecs, getClass().getClassLoader());
						propinfo	= new byte[data.length+4];
						System.arraycopy(SUtil.intToBytes(data.length), 0, propinfo, 0, 4);
						System.arraycopy(data, 0, propinfo, 4, data.length);
						
						if(awainfo.getProperties()==null)
						{
							nopropinfo	= propinfo;
						}

					}
					
//						System.out.println("queing awareness info to:"+entries[i].getKey());
					entries[i].getValue().enqueue(new Message(SRelay.MSGTYPE_AWAINFO, new ByteArrayInputStream(awainfo2.getProperties()==null ? nopropinfo : propinfo)));
				}
				catch(Exception e)
				{
					// Queue closed, because platform just disconnected.
				}
				
				// Send other awareness infos to newly connected platform.
				if(initial)
				{
					// Send awareness infos with or without properties, for backwards compatibility with Jadex 2.1
					if(awainfo.getProperties()==null && awainfo2.getProperties()!=null)
					{
						awainfo2	= new AwarenessInfo(awainfo2.getSender(), awainfo2.getState(), awainfo2.getDelay(), awainfo2.getIncludes(), awainfo2.getExcludes(), awainfo2.getMasterId());
						awainfo2.setProperties(null);
					}
					
					byte[]	data2	= MapSendTask.encodeMessage(awainfo2, platform.getPreferredCodecs(), getClass().getClassLoader());
					byte[]	info2	= new byte[data2.length+4];
					System.arraycopy(SUtil.intToBytes(data2.length), 0, info2, 0, 4);
					System.arraycopy(data2, 0, info2, 4, data2.length);
					
					try
					{
//							System.out.println("queing awareness info to:"+id);
						map.get(id).enqueue(new Message(SRelay.MSGTYPE_AWAINFO, new ByteArrayInputStream(info2)));
					}
					catch(Exception e)
					{
						// Queue closed, because platform just disconnected.
					}
				}					
			}
		}

		// Send awareness infos from connected peers.
		if(initial)
		{
			PeerEntry[] apeers = peers.getPeers();
			for(PeerEntry peer: apeers)
			{
				if(peer.isConnected())
				{
					AwarenessInfo[]	infos	= peer.getAwarenessInfos();
					for(AwarenessInfo awainfo2: infos)
					{
						// Send awareness infos with or without properties, for backwards compatibility with Jadex 2.1
						if(awainfo.getProperties()==null && awainfo2.getProperties()!=null)
						{
							awainfo2	= new AwarenessInfo(awainfo2.getSender(), awainfo2.getState(), awainfo2.getDelay(), awainfo2.getIncludes(), awainfo2.getExcludes(), awainfo2.getMasterId());
							awainfo2.setProperties(null);
						}
						
						byte[]	data2	= MapSendTask.encodeMessage(awainfo2, platform.getPreferredCodecs(), getClass().getClassLoader());
						byte[]	info2	= new byte[data2.length+4];
						System.arraycopy(SUtil.intToBytes(data2.length), 0, info2, 0, 4);
						System.arraycopy(data2, 0, info2, 4, data2.length);
						
						try
						{
//								System.out.println("queing awareness info to:"+id);
							map.get(id).enqueue(new Message(SRelay.MSGTYPE_AWAINFO, new ByteArrayInputStream(info2)));
						}
						catch(Exception e)
						{
							// Queue closed, because platform just disconnected.
						}
					}
				}
			}
		}

		// Distribute awareness info to peer relay servers, if locally connected platform. (todo: send asynchronously?)
		if(platform!=null)
		{
			byte[]	peerinfo	= null;
			PeerEntry[] apeers = peers.getPeers();
			for(PeerEntry peer: apeers)
			{
				if(peer.isConnected())
				{
					if(peerinfo==null)
					{
						Map<String, Object>	message	= new HashMap<String, Object>();
						message.put(SFipa.LANGUAGE, SFipa.JADEX_RAW);
						message.put(SFipa.CONTENT, awainfo);
						MessageEnvelope	msg	= new MessageEnvelope(message, null, null);
						peerinfo	= MapSendTask.encodeMessage(msg, pcodecs, getClass().getClassLoader());
					}
					
					try
					{
						System.out.println("Sending awareness to peer: "+peer.getURL());
						new RelayConnectionManager().postMessage(peer.getURL()+"awareness", new ComponentIdentifier(peers.getUrl()), new byte[][]{peerinfo});
					}
					catch(IOException e)
					{
						System.out.println("Error sending awareness to peer: "+e);
					}
				}
			}					
		}
	}
	
	/**
	 * 	Read a string from the given stream.
	 *  @param in	The input stream.
	 *  @return The string.
	 *  @throws	IOException when the stream is closed.
	 */
	public static String	readString(InputStream in) throws IOException
	{
		byte[]	len	= readData(in, 4);
		int	length	= SUtil.bytesToInt(len);
		byte[] buffer = readData(in, length);
		return new String(buffer, "UTF-8");
	}
	
	/**
	 *  Read data into a byte array.
	 */
	protected static byte[] readData(InputStream is, int length) throws IOException
	{
		int num	= 0;
		byte[]	buffer	= new byte[length];
		while(num<length)
		{
			int read	= is.read(buffer, num, length-num);
			if(read==-1)
			{
				throw new IOException("Stream closed.");
			}
			num	= num + read;
		}
		return buffer;
	}
	
	/**
	 *  Get the logger.
	 */
	public static Logger	getLogger()
	{
		return Logger.getLogger("jadex.relay");		
	}
}
