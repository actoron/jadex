package jadex.base.relay;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.fipa.SFipa;
import jadex.bridge.service.types.awareness.AwarenessInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.collection.ArrayBlockingQueue;
import jadex.commons.collection.IBlockingQueue;
import jadex.commons.collection.IBlockingQueue.ClosedException;
import jadex.commons.concurrent.TimeoutException;
import jadex.commons.transformation.binaryserializer.IErrorReporter;
import jadex.commons.transformation.binaryserializer.SBinarySerializer;
import jadex.platform.service.message.RemoteMarshalingConfig;
import jadex.platform.service.message.transport.httprelaymtp.RelayConnectionManager;
import jadex.platform.service.message.transport.httprelaymtp.SRelay;
import jadex.xml.bean.JavaReader;


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
		// System.getProperty() does not return environment variables, but just server VM properties.
		String	home	= System.getenv("RELAY_HOME");
		if(home!=null)
		{
			dir	= new File(home);
		}
		else
		{
			if("true".equals(System.getProperty("relay.standalone")))
			{				
				dir	= new File(".", ".relaystats");
			}
			else
			{
				dir	= new File(System.getProperty("user.home"), ".relaystats");
			}
		}
		
		if(!dir.exists())
		{
			if(!dir.mkdirs())
			{
				getLogger().info("Cannot mkdirs: "+dir);
			}
		}
		else if(!dir.isDirectory())
		{
			throw new RuntimeException("Settings path '"+dir+"' is not a directory.");
		}
		SYSTEMDIR	= dir;
		
		getLogger().info("Relay settings directory (change with $RELAY_HOME): "+SYSTEMDIR.getAbsolutePath());
	}
	
	//-------- attributes --------
	
	/** The settings loaded from file. */
	protected RelayServerSettings	settings;
	
	/** The relay map (id -> queue for pending requests). */
	protected Map<String, IBlockingQueue<Message>>	map;
	
	/** Info about connected platforms.*/
	protected Map<Object, PlatformInfo>	platforms;
	
	/** Config with marshaling infos. */
	protected RemoteMarshalingConfig rmc;
	
	/** The peer list. */
	protected PeerList	peers;
	
	/** The statistics database (if any). */
	protected StatsDB	statsdb;
	
	/** The connection manager for communicating with remote peers. */
	protected RelayConnectionManager	conman;
	
	//-------- constructors --------

	/**
	 *  Initialize the handler.
	 */
	public RelayHandler()
	{
		this.map	= Collections.synchronizedMap(new HashMap<String, IBlockingQueue<Message>>());
		this.platforms	= Collections.synchronizedMap(new LinkedHashMap<Object, PlatformInfo>());
		rmc	= new RemoteMarshalingConfig();
		this.settings	= new RelayServerSettings();
		try
		{
			settings.loadSettings(new File(RelayHandler.SYSTEMDIR, "peer.properties"), true);
		}
		catch(Exception e)
		{
			getLogger().warning("Could not load relay settings: "+e);
		}
		this.peers	= new PeerList(this);
		this.statsdb	= StatsDB.createDB(settings.getId());
		this.conman	= new RelayConnectionManager();
		
		// Register communication classes with aliases
//		STransformation.registerClass(MessageEnvelope.class);
		
		// Add initial peers.
		peers.addPeers(settings.getInitialPeers(), true);
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
				PlatformInfo	pi	= it.next();
				pi.disconnect();
				if(statsdb!=null)
				{
					statsdb.save(pi);
				}
			}
		}
		
		if(statsdb!=null)
		{
			this.statsdb.shutdown();
		}
		
		this.peers.dispose();
		
		this.conman.dispose();
	}
	
	//-------- methods --------
	
	/**
	 *  Get the settings.
	 */
	public RelayServerSettings	getSettings()
	{
		return settings;
	}
	
	/**
	 *  Get the connection manager.
	 */
	public RelayConnectionManager	getConnectionManager()
	{
		return conman;
	}
	
	/**
	 *  Get the peer list.
	 */
	public PeerList getPeerList()
	{
		return peers;
	}
	
	/**
	 *  Called when a platform registers itself at the relay. 
	 *  Initializes required data structures, such that messages can be queued.
	 */
	public void initConnection(String id, String hostip, String hostname, String protocol)
	{
		PlatformInfo	info	= platforms.get(id);
		if(info==null)
		{
			info	= new PlatformInfo(id, settings.getId(), hostip, hostname, protocol);
			platforms.put(id, info);
		}
		else
		{
			// Throws exception, if reconnect not allowed (e.g. from different IP).
			info.reconnect(hostip, hostname, protocol, statsdb);
		}
		
		if(statsdb!=null)
		{
			statsdb.save(info);
		}
		
		IBlockingQueue<Message>	queue	= map.get(id);
		if(queue!=null)
		{
			// Close old queue to free old servlet request
			getLogger().info("Closing old queue due to reconnect of: "+id);
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
		
		// Inform peers about connected platform.
		sendPlatformInfo(info);
				
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
					long	timeout	= (long)(SRelay.PING_DELAY*0.85);
//					getLogger().info("Waiting for message for: "+info.getId()+", timeout="+timeout);
					Message	msg	= queue.dequeue(timeout);	// Todo: make ping delay configurable on per client basis
					info.updateLastActiveTime();
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
//					getLogger().info("Activity timeout. Requesting ping from: "+info.getId()+", timeout="+info.testPlatformTimeout(SRelay.PING_DELAY));
					if(info.testPlatformTimeout(SRelay.PING_DELAY))
					{
						throw new TimeoutException("No platform activity in the last "+SRelay.PING_DELAY+" ms.");
					}
					
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
			getLogger().info("Closing queue due disconnect of: "+id);
			List<Message>	items	= queue.setClosed(true);
			map.remove(id);
			PlatformInfo	platform	= platforms.remove(id);
			if(platform!=null)
			{
				platform.disconnect();
				if(statsdb!=null)
				{
					statsdb.save(platform);
				}
			}
			AwarenessInfo	awainfo	= platform!=null ? platform.getAwarenessInfo() : null;
			if(awainfo!=null)
			{
//				System.out.println("Sending offline info: "+id);
				awainfo.setState(AwarenessInfo.STATE_OFFLINE);
				sendAwarenessInfos(awainfo, true, false);
			}
			else if(platform!=null)
			{
				sendPlatformInfo(platform);
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
		
		// Only send message when request is not https or target is also connected via https.
		PlatformInfo	targetpi	= platforms.get(targetid);
		IBlockingQueue<Message>	queue	= map.get(targetid);
		if(queue!=null && targetpi!=null && (!protocol.equals("https") || targetpi.getScheme().equals("https")))
		{
			Message	msg	= new Message(SRelay.MSGTYPE_DEFAULT, in);
			queue.enqueue(msg);
			msg.getFuture().get(30000);	// todo: how to set a useful timeout value!?
		}
		else
		{
			throw new RuntimeException("message not sent: "+targetid+", "+targetpi+", "+queue);
		}
	}
	
	/**
	 *  Called when an awareness info is received from a connected platform.
	 */
	public void handleAwareness(InputStream in) throws Exception
	{
		// Read dummy target id.
		readString(in);
		
		// Read total message length.
		byte[]	len	= readData(in, 4);
		int	length	= SUtil.bytesToInt(len);
		
		// Read message and extract awareness info content.
		byte[] buffer = readData(in, length-1);
//		MessageEnvelope	msg	= (MessageEnvelope)MapSendTask.decodeMessage(buffer, null, rmc.getAllSerializers(), rmc.getAllCodecs(), getClass().getClassLoader(), null);//IErrorReporter.IGNORE);
		Map<String, Object>	msg	= (Map<String, Object>) SBinarySerializer.readObjectFromStream(new ByteArrayInputStream(buffer), null, null, getClass().getClassLoader(), null, null);
//		IBinaryCodec[]	pcodecs	= MapSendTask.getCodecs(buffer, rmc.getAllCodecs());
		AwarenessInfo	info;
		if(SFipa.JADEX_RAW.equals(msg.get(SFipa.LANGUAGE)))
		{
			info = (AwarenessInfo)msg.get(SFipa.CONTENT);
		}
		else if(SFipa.JADEX_XML.equals(msg.get(SFipa.LANGUAGE)))
		{
			info = (AwarenessInfo)JavaReader.objectFromByteArray((byte[])msg.get(SFipa.CONTENT), getClass().getClassLoader(), IErrorReporter.IGNORE);
		}
		else //if(SFipa.JADEX_BINARY.equals(msg.getMessage().get(SFipa.LANGUAGE)))
		{
			info = (AwarenessInfo)SBinarySerializer.readObjectFromByteArray((byte[])msg.get(SFipa.CONTENT), null, null, getClass().getClassLoader(), null);
		}
		
		// Update platform awareness info.
		String	id	= info.getSender().getPlatformName();
		PlatformInfo	platform	= platforms.get(id);
		boolean	initial	= platform!=null && platform.getAwarenessInfo()==null && AwarenessInfo.STATE_ONLINE.equals(info.getState());
		if(platform!=null)
		{
			platform.updateLastActiveTime();
			platform.setAwarenessInfo(info);
//			platform.setPreferredCodecs(pcodecs);
			
			if(statsdb!=null)
			{
				statsdb.save(platform);
			}				
		}
		
		sendAwarenessInfos(info, true, initial);
	}
	
	/**
	 *  Called when an offline status change is posted by a platform.
	 */
	public void handleOffline(String hostip, InputStream in) throws Exception
	{
		// Read platform id
		String	id	= readString(in);

		// Read total message length.	should be 0
		readData(in, 4);
		
		// Only accept status if from same IP
		PlatformInfo	pi	= platforms.get(id);
		if(pi==null)
		{
			throw new RuntimeException("No such platform: "+id);
		}
		else if(!hostip.equals(pi.getHostIP()))
		{
			throw new RuntimeException("Offline request from wrong IP: "+id+", "+hostip+", "+pi.getHostIP());			
		}
		else
		{
			PlatformInfo	platform	= platforms.remove(id);
			if(platform!=null)
			{
				platform.disconnect();
				if(statsdb!=null)
				{
					statsdb.save(platform);
				}
			}
			
			AwarenessInfo	awainfo	= platform!=null ? platform.getAwarenessInfo() : null;
			if(awainfo!=null)
			{
				awainfo.setState(AwarenessInfo.STATE_OFFLINE);
				sendAwarenessInfos(awainfo, true, false);
			}
			else if(platform!=null)
			{
				sendPlatformInfo(platform);
			}
			
			IBlockingQueue<Message>	queue	= map.get(id);
			if(queue!=null)
			{
				getLogger().info("Closing queue due offline notification of: "+id);
				List<Message>	items	= queue.setClosed(true);
				map.remove(id);
				for(int i=0; i<items.size(); i++)
				{
					items.get(i).getFuture().setException(new RuntimeException("Target disconnected."));
				}
			}			
		}

	}
	
	/**
	 *  Called when a single platform info is received from a peer relay server.
	 */
	public void handlePlatform(InputStream in) throws Exception
	{
		// Read peer url.
		String	peerurl	= readString(in);
		
		// Read total message length.
		byte[]	len	= readData(in, 4);
		int	length	= SUtil.bytesToInt(len);
		
		// Read message and extract platform info content.
		byte[] buffer = readData(in, length-1);
		
		PlatformInfo	info	= (PlatformInfo) SBinarySerializer.readObjectFromStream(new ByteArrayInputStream(buffer), getClass().getClassLoader());
//		PlatformInfo	info	= (PlatformInfo)MapSendTask.decodeMessage(buffer, null, rmc.getAllSerializers(), rmc.getAllCodecs(), getClass().getClassLoader(), IErrorReporter.IGNORE);
//		IBinaryCodec[]	pcodecs	= MapSendTask.getCodecs(buffer, rmc.getAllCodecs());
		
		PeerHandler	peer	= peers.addPeer(peerurl);
		
		peer.updatePlatformInfo(info);
		if(info.getAwarenessInfo()!=null)
		{
			sendAwarenessInfos(info.getAwarenessInfo(), false, false);
		}			
	}
	
	/**
	 *  Called when a ping is received from a specific sender.
	 */
	public void handlePing(String id)
	{
		PlatformInfo	pi	= platforms.get(id);
		if(pi!=null)
		{
			pi.updateLastActiveTime();
//			RelayHandler.getLogger().info("Received ping from platform: "+id);
		}
	}
	
	/**
	 *  Called when platform infos are received from a peer relay server.
	 */
	public void handlePlatforms(InputStream in) throws Exception
	{
		// Read peer url.
		String	peerurl	= readString(in);
		
		// Read total message length.
		byte[]	len	= readData(in, 4);
		int	length	= SUtil.bytesToInt(len);
		
		// Read message and extract platform info content.
		byte[] buffer = readData(in, length-1);
		PlatformInfo[]	infos	= (PlatformInfo[]) SBinarySerializer.readObjectFromStream(new ByteArrayInputStream(buffer), getClass().getClassLoader());
//		PlatformInfo[]	infos	= (PlatformInfo[])MapSendTask.decodeMessage(buffer, null, rmc.getAllSerializers(), rmc.getAllCodecs(), getClass().getClassLoader(), IErrorReporter.IGNORE);
//		IBinaryCodec[]	pcodecs	= MapSendTask.getCodecs(buffer, rmc.getAllCodecs());
		
		PeerHandler	peer	= peers.addPeer(peerurl);
		
		// Remember previously connected platforms.
		Map<String, PlatformInfo>	old	= new LinkedHashMap<String, PlatformInfo>();
		for(PlatformInfo info: peer.getPlatformInfos())
		{
			if(info.getAwarenessInfo()!=null)
			{
				old.put(info.getId(), info);
			}
		}
		peer.clearPlatformInfos();
		
		// Send infos for currently connected platforms
		for(PlatformInfo info: infos)
		{
			peer.updatePlatformInfo(info);
			if(info.getAwarenessInfo()!=null)
			{
				sendAwarenessInfos(info.getAwarenessInfo(), false, false);
				old.remove(info.getId());
			}
		}
		
		// Send offline infos for remaining previous platforms.
		for(PlatformInfo info: old.values())
		{
			AwarenessInfo	awainfo	= info.getAwarenessInfo();
			awainfo.setState(AwarenessInfo.STATE_OFFLINE);
			sendAwarenessInfos(awainfo, false, false);
		}
	}
	
	/**
	 *  Send requested db entries.
	 */
	public void handleSyncRequest(String peerid, int startid, int cnt, OutputStream out) throws Exception
	{
		PlatformInfo[]	pi	= getStatisticsDB().getPlatformInfosForSync(peerid, startid, cnt);
//		byte[]	entries	= MapSendTask.encodeMessage(pi, null, rmc.getDefaultSerializer(), rmc.getDefaultCodecs(), getClass().getClassLoader());
//		out.write(entries);
		SBinarySerializer.writeObjectToStream(out, pi, getClass().getClassLoader());
	}

	/**
	 *  Get the current platforms
	 */
	public PlatformInfo[]	getCurrentPlatforms()
	{
		// Fetch array to avoid concurrency problems
		return platforms.values().toArray(new PlatformInfo[0]);
	}
	
	/**
	 *  Get the statistics database (if any).
	 */
	public StatsDB	getStatisticsDB()
	{
		return this.statsdb;
	}
	
	/**
	 *  Get the current peers.
	 */
	public PeerHandler[]	getCurrentPeers()
	{
		return peers.getPeers();
	}

	/**
	 * Specifies the url under which this relay instance is reachable.
	 * @param url
	 */
	public void setUrl(String url)
	{
		settings.setUrl(url);
	}
	
	/**
	 *  Get the available servers as comma-separated list of URLs.
	 *  Also updates the known peers, if necessary.
	 *  @param requesturl	Public URL of this relay server as known from the received request.
	 *  @param peerurl	URL of a remote peer if sent as part of the request (or null).
	 *  @param peerstate	Latest DB id if sent as part of the request (or -1).
	 *  @param initial	True when remote peer recovers from failure (or false).
	 */
	public String	handleServersRequest(String requesturl, String peerurl, String peerid, int peerstate, boolean initial)
	{
		if(peerurl!=null)
		{
			PeerHandler	peer	= peers.addPeer(peerurl, peerid, peerstate);

			// Send own awareness infos to new peer.
			if(initial)
			{
				peer.setSent(true);
				sendPlatformInfos(peer, getCurrentPlatforms());
			}
		}
		return peers.getURLs(requesturl);
	}
	
	/**
	 *  Send a single platform info to all peer relay servers.
	 */
	public void	sendPlatformInfo(PlatformInfo info)
	{
		try
		{
			byte[]	peerinfo	= null;
			for(PeerHandler peer: peers.getPeers())
			{
//				if(peer.isConnected())
				{
					if(peerinfo==null)
					{
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						SBinarySerializer.writeObjectToStream(baos, info, getClass().getClassLoader());
						peerinfo	= baos.toByteArray();
//						peerinfo	= MapSendTask.encodeMessage(info, null, rmc.getDefaultSerializer(), rmc.getDefaultCodecs(), getClass().getClassLoader());
					}
					peer.addDebugText(3, "Sending platform info to peer "+info.getId());
					conman.postMessage(peer.getUrl()+"platforminfo", new BasicComponentIdentifier(settings.getUrl()), new byte[][]{peerinfo});
					peer.addDebugText(3, "Sent platform info to peer "+info.getId());
				}
			}
		}
		catch(IOException e)
		{
			for(PeerHandler peer: peers.getPeers())
			{
				if(peer.isConnected())
				{
					peer.addDebugText(3, "Error sending platform info to peer: "+peer.getUrl()+"platforminfo, "+e);
				}
			}
			getLogger().warning("Error sending platform info to peer: "+e);
		}					
	}
	
	/**
	 *  Send platform infos to a peer relay server.
	 */
	public void	sendPlatformInfos(PeerHandler peer, PlatformInfo[] infos)
	{
		try
		{
			peer.addDebugText(3, "Sending platform infos to peer: "+infos.length);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			SBinarySerializer.writeObjectToStream(baos, infos, getClass().getClassLoader());
			byte[]	peerinfo	= baos.toByteArray();
//			byte[]	peerinfo	= MapSendTask.encodeMessage(infos, null, rmc.getDefaultSerializer(), rmc.getDefaultCodecs(), getClass().getClassLoader());
			conman.postMessage(RelayConnectionManager.httpAddress(peer.getUrl())+"platforminfos", new BasicComponentIdentifier(settings.getUrl()), new byte[][]{peerinfo});
			peer.addDebugText(3, "Sent platform infos.");
		}
		catch(IOException e)
		{
			peer.addDebugText(3, "Error sending platform infos to peer: "+peer.getUrl()+"platforminfos, "+e);
			getLogger().warning("Error sending platform infos to peer: "+peer.getUrl()+"platforminfos, "+e);
		}					
	}
	
	//-------- helper methods --------	

	/**
	 *  Send awareness messages for a new or changed awareness info.
	 */
	protected void	sendAwarenessInfos(AwarenessInfo awainfo, boolean local, boolean initial)
	{
//		System.out.println("sending awareness infos: "+awainfo.getSender().getPlatformName()+", "+platforms.size());
//		pcodecs	= pcodecs!=null ? pcodecs : rmc.getDefaultCodecs();
		
		String	id	= awainfo.getSender().getPlatformName();
		PlatformInfo	platform	= platforms.get(id);
		
		// Ignore remote platforms if also connected local
		// (e.g. remote relay is down, platform reconnects at local relay,
		// afterwards local detects remote is down and wants to send offline info for already reconnected platform)
		if(platform==null || local)
		{
			byte[]	propinfo	= null;
			byte[]	nopropinfo	= null;
			
			Map.Entry<String, IBlockingQueue<Message>>[] platformentries = map.entrySet().toArray(new Map.Entry[0]);
			for(int i=0; i<platformentries.length; i++)
			{
				// Send awareness to other platforms with awareness on.
				PlatformInfo	p2	= platforms.get(platformentries[i].getKey());
				AwarenessInfo	awainfo2	= p2!=null ? p2.getAwarenessInfo() : null;
				if(awainfo2!=null && !id.equals(platformentries[i].getKey()))
				{
					try
					{
						// Send awareness infos with or without properties, for backwards compatibility with Jadex 2.1
						if(awainfo2.getProperties()==null && nopropinfo==null)
						{
							AwarenessInfo	awanoprop	= awainfo;
							if(awainfo.getProperties()!=null)
							{
								awanoprop	= new AwarenessInfo(awainfo.getSender(), awainfo.getState(), awainfo.getDelay(), 
									awainfo.getIncludes(), awainfo.getExcludes(), awainfo.getMasterId(), SReflect.getInnerClassName(this.getClass()));
								awanoprop.setProperties(null);
							}
							
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							SBinarySerializer.writeObjectToStream(baos, awanoprop, getClass().getClassLoader());
							byte[]	data	= baos.toByteArray();
//							byte[]	data	= MapSendTask.encodeMessage(awanoprop, null, rmc.getDefaultSerializer(), getClass().getClassLoader());
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
							ByteArrayOutputStream baos = new ByteArrayOutputStream();
							SBinarySerializer.writeObjectToStream(baos, awainfo, getClass().getClassLoader());
							byte[]	data	= baos.toByteArray();
//							byte[]	data	= MapSendTask.encodeMessage(awainfo, null, rmc.getDefaultSerializer(), getClass().getClassLoader());
							propinfo	= new byte[data.length+4];
							System.arraycopy(SUtil.intToBytes(data.length), 0, propinfo, 0, 4);
							System.arraycopy(data, 0, propinfo, 4, data.length);
							
							if(awainfo.getProperties()==null)
							{
								nopropinfo	= propinfo;
							}
	
						}
						
	//						System.out.println("queing awareness info to:"+entries[i].getKey());
						platformentries[i].getValue().enqueue(new Message(SRelay.MSGTYPE_AWAINFO, new ByteArrayInputStream(awainfo2.getProperties()==null ? nopropinfo : propinfo)));
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
							awainfo2	= new AwarenessInfo(awainfo2.getSender(), awainfo2.getState(), awainfo2.getDelay(), 
								awainfo2.getIncludes(), awainfo2.getExcludes(), awainfo2.getMasterId(), SReflect.getInnerClassName(this.getClass()));
							awainfo2.setProperties(null);
						}
						
						ByteArrayOutputStream baos = new ByteArrayOutputStream();
						SBinarySerializer.writeObjectToStream(baos, awainfo2, getClass().getClassLoader());
						byte[]	data2	= baos.toByteArray();
//						byte[]	data2	= MapSendTask.encodeMessage(awainfo2, null, rmc.getDefaultSerializer(), pcodecs, getClass().getClassLoader());
						byte[]	info2	= new byte[data2.length+4];
						System.arraycopy(SUtil.intToBytes(data2.length), 0, info2, 0, 4);
						System.arraycopy(data2, 0, info2, 4, data2.length);
						
						try
						{
	//							System.out.println("queing awareness info to:"+id);
							map.get(id).enqueue(new Message(SRelay.MSGTYPE_AWAINFO, new ByteArrayInputStream(info2)));
						}
						catch(ClosedException e)
						{
							// Queue closed, because platform just disconnected.
						}
					}					
				}
			}
	
			// Send awareness infos from connected peers.
			if(initial)
			{
				PeerHandler[] apeers = peers.getPeers();
				for(PeerHandler peer: apeers)
				{
					if(peer.isConnected())
					{
						PlatformInfo[]	infos	= peer.getPlatformInfos();
						for(PlatformInfo pi: infos)
						{
							if(pi.getAwarenessInfo()!=null)
							{
								AwarenessInfo	awainfo2	= pi.getAwarenessInfo();
								// Send awareness infos with or without properties, for backwards compatibility with Jadex 2.1
								if(awainfo.getProperties()==null && awainfo2.getProperties()!=null)
								{
									awainfo2	= new AwarenessInfo(awainfo2.getSender(), awainfo2.getState(), awainfo2.getDelay(), 
										awainfo2.getIncludes(), awainfo2.getExcludes(), awainfo2.getMasterId(), SReflect.getInnerClassName(this.getClass()));
									awainfo2.setProperties(null);
								}
								
								ByteArrayOutputStream baos = new ByteArrayOutputStream();
								SBinarySerializer.writeObjectToStream(baos, awainfo2, getClass().getClassLoader());
								byte[]	data2	= baos.toByteArray();
//								byte[]	data2	= MapSendTask.encodeMessage(awainfo2, null, rmc.getDefaultSerializer(), platform.getPreferredCodecs(), getClass().getClassLoader());
								byte[]	info2	= new byte[data2.length+4];
								System.arraycopy(SUtil.intToBytes(data2.length), 0, info2, 0, 4);
								System.arraycopy(data2, 0, info2, 4, data2.length);
								
								try
								{
		//								System.out.println("queing awareness info to:"+id);
									map.get(id).enqueue(new Message(SRelay.MSGTYPE_AWAINFO, new ByteArrayInputStream(info2)));
								}
								catch(ClosedException e)
								{
									// Queue closed, because platform just disconnected.
								}
							}
						}
					}
				}
			}
	
			// Distribute platform info to peer relay servers, if locally disconnected platform. (todo: send asynchronously?)
			if(local)
			{
				if(platform==null)
				{
					platform	= new PlatformInfo();
					platform.setId(awainfo.getSender().getName());
					platform.setDisconnectDate(new Date());	// set disconnected date to indicate removed platform.
					awainfo.setState(AwarenessInfo.STATE_OFFLINE);
					platform.setAwarenessInfo(awainfo);
				}
				sendPlatformInfo(platform);
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
