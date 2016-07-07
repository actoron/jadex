package jadex.platform.service.message.transport.httprelaymtp;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URLEncoder;
import java.util.Scanner;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.HttpConnectionManager;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.ISuspendable;

/**
 *  The connection manager performs http requests and further
 *  allows asynchronously terminating open connections
 *  to avoid hanging on e.g. platform shutdown.
 */
public class RelayConnectionManager	extends HttpConnectionManager
{
	//-------- constants --------
	
	/** A buffer for reading response data (ignored but needs to be read for connection to be reusable). */
	static final byte[]	RESPONSE_BUF	= new byte[8192];
	
	//-------- methods --------
	
	/**
	 *  Ping a relay server.
	 *  @throws IOException on connection failures
	 */
	public void	ping(final String address)	throws IOException
	{
		// sun.net.www HttpUrlConnection hangs on openConnection without any means to abort :-(
		// Use extra thread to not hold up platform shutdown due to not-responding relay.
		
		if(ISuspendable.SUSPENDABLE.get()!=null)
		{
			throw new IllegalStateException("Must not be called from managed thread: "+ISuspendable.SUSPENDABLE.get());
		}
		final Future<Void>	ret	= new Future<Void>();
		Thread	t	= new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					doPing(address);
					ret.setResultIfUndone(null);
				}
				catch(Exception e)
				{
					ret.setExceptionIfUndone(e);
				}
			}
		});
		t.setDaemon(true);
		t.start();
		ret.get();
	}
	
	/**
	 *  Ping a relay server.
	 *  @throws IOException on connection failures
	 */
	protected void	doPing(String address)	throws IOException
	{
		address	= httpAddress(address);
		
		// Hack!!! when pinging before awareness message, strip extension
		if(address.endsWith("/awareness"))
		{
			address	= address.substring(0, address.lastIndexOf("/awareness")+1);
		}
		else if(address.endsWith("/awareness/"))
		{
			address	= address.substring(0, address.lastIndexOf("/awareness/")+1);
		}
		
		HttpURLConnection	con	= null;
		try
		{
			con	= openConnection(address + "ping");
			con.connect();
			int	code	= con.getResponseCode();
			while(con.getInputStream().read(RESPONSE_BUF)!=-1)
			{
			}
			con.getInputStream().close();
			if(code!=HttpURLConnection.HTTP_OK)
				throw new IOException("HTTP code "+code+": "+con.getResponseMessage());
		}
		finally
		{
			if(con!=null)
			{
				remove(con);
			}
		}
	}
	
	/**
	 *  Open a receiving connection.
	 *  The connection should be removed when it is closed to avoid memory leaks. 
	 *  @return The connection.
	 *  @throws IOException on connection failures
	 */
	public HttpURLConnection	openReceiverConnection(final String address, final IComponentIdentifier receiver)	throws IOException
	{
		// sun.net.www HttpUrlConnection hangs on openConnection without any means to abort :-(
		// Use extra thread to not hold up platform shutdown due to not-responding relay.
		
		if(ISuspendable.SUSPENDABLE.get()!=null)
		{
			throw new IllegalStateException("Must not be called from managed thread: "+ISuspendable.SUSPENDABLE.get());
		}
		final Future<HttpURLConnection>	ret	= new Future<HttpURLConnection>();
		Thread	t	= new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					ret.setResultIfUndone(doOpenReceiverConnection(address, receiver));
				}
				catch(Exception e)
				{
					ret.setExceptionIfUndone(e);
				}
			}
		});
		t.setDaemon(true);
		t.start();
		return ret.get();

	}
	
	/**
	 *  Open a receiving connection.
	 *  The connection should be removed when it is closed to avoid memory leaks. 
	 *  @return The connection.
	 *  @throws IOException on connection failures
	 */
	public HttpURLConnection	doOpenReceiverConnection(String address, IComponentIdentifier receiver)	throws IOException
	{
		address	= httpAddress(address);
		HttpURLConnection	con	= null;
		String	xmlid	= receiver.getRoot().getName();
		con	= openConnection(address+"?id="+URLEncoder.encode(xmlid, "UTF-8"));
		con.setUseCaches(false);
//		con.setRequestProperty("User-Agent", "Jadex4Android2.1");
//		con.setRequestProperty("Cache-Control", "no-cache, no-transform");
//		con.setRequestProperty("Pragma", "no-cache");
		
		//						// Hack!!! Do not validate server (todo: enable/disable by platform argument).
		//						if(con instanceof HttpsURLConnection)
		//						{
		//							HttpsURLConnection httpscon = (HttpsURLConnection) con;  
		//					        httpscon.setHostnameVerifier(new HostnameVerifier()  
		//					        {        
		//					            public boolean verify(String hostname, SSLSession session)  
		//					            {  
		//					                return true;  
		//					            }  
		//					        });												
		//						}

		// Force connection
		con.getInputStream();
		
		return con;
	}
	
	/**
	 *  Get known servers from a server.
	 *  @param address	The remote server address.
	 *  @return The comma separated server list.
	 *  @throws IOException on connection failures
	 */
	public String	getServers(String address)	throws IOException
	{
		return getPeerServers(address, null, null, 0, false);
	}

	/**
	 *  Get known servers from a peer server.
	 *  @param peeraddress	The remote server address.
	 *  @param ownaddress	The local server address supplied for mutual connection (set to null if not connecting to peer).
	 *  @param ownid	The local peer id supplied for history db synchronization (not used if not connecting to peer).
	 *  @param peerstate	Contains id of the latest history entry of that peer to enable synchronization (not used if not connecting to peer).
	 *  @param initial	True, when peer connects initially (not used if not connecting to peer).
	 *  @return The comma separated server list.
	 *  @throws IOException on connection failures
	 */
	public String	getPeerServers(final String peeraddress, final String ownaddress, final String ownid, final int dbstate, final boolean initial)	throws IOException
	{
		// sun.net.www HttpUrlConnection hangs on openConnection without any means to abort :-(
		// Use extra thread to not hold up platform shutdown due to not-responding relay.
		
		if(ISuspendable.SUSPENDABLE.get()!=null)
		{
			throw new IllegalStateException("Must not be called from managed thread: "+ISuspendable.SUSPENDABLE.get());
		}
		final Future<String>	ret	= new Future<String>();
		Thread	t	= new Thread(new Runnable()
		{
			@Override
			public void run()
			{
				try
				{
					ret.setResultIfUndone(doGetPeerServers(peeraddress, ownaddress, ownid, dbstate, initial));
				}
				catch(Exception e)
				{
					ret.setExceptionIfUndone(e);
				}
			}
		});
		t.setDaemon(true);
		t.start();
		return ret.get();
	}
	
	/**
	 *  Get known servers from a peer server.
	 *  @param peeraddress	The remote server address.
	 *  @param ownaddress	The local server address supplied for mutual connection (set to null if not connecting to peer).
	 *  @param ownid	The local peer id supplied for history db synchronization (not used if not connecting to peer).
	 *  @param peerstate	Contains id of the latest history entry of that peer to enable synchronization (not used if not connecting to peer).
	 *  @param initial	True, when peer connects initially (not used if not connecting to peer).
	 *  @return The comma separated server list.
	 *  @throws IOException on connection failures
	 */
	public String	doGetPeerServers(String peeraddress, String ownaddress, String ownid, int dbstate, boolean initial)	throws IOException
	{
		String	ret;
		HttpURLConnection	con	= null;
		peeraddress	= httpAddress(peeraddress);
		ownaddress	= ownaddress!=null ? httpAddress(ownaddress) : null;
		try
		{
			con	= openConnection(peeraddress+"servers"
				+(ownaddress!=null ? "?peerurl="+URLEncoder.encode(ownaddress, "UTF-8")+"&initial="+initial+"&peerid="+ownid+"&peerstate="+dbstate : ""));
			if(con.getContentType()!=null && con.getContentType().startsWith("text/plain"))
			{
				ret	= new Scanner(con.getInputStream(), "UTF-8").useDelimiter("\\A").next();
			}
			else
			{
				throw new IOException("Unexpected content type: "+con.getContentType());
			}
		}
		finally
		{
			if(con!=null)
			{
				remove(con);
			}
		}
		return ret;
	}
	
	/**
	 *  Post a message.
	 *  @throws IOException on connection failures
	 */
	public void postMessage(String address, IComponentIdentifier targetid, byte[][] data)	throws IOException
	{
		address	= httpAddress(address);
		byte[]	iddata	= targetid.getName().getBytes("UTF-8");
		
		HttpURLConnection	con	= null;
		OutputStream	out;
		int	code;
		try
		{
			con	= openConnection(address);
			int	datalength	= 0;
			for(int i=0; i<data.length; i++)
			{
				datalength	+= data[i].length;
			}
	
			con.setRequestMethod("POST");
			con.setDoOutput(true);
			con.setUseCaches(false);
			con.setRequestProperty("Content-Type", "application/octet-stream");
			con.setRequestProperty("Content-Length", ""+(4+iddata.length+4+datalength));
	
	//		synchronized(POOL_LOCK)
			{
				con.connect();
		
				out	= con.getOutputStream();
	
				out.write(SUtil.intToBytes(iddata.length));
				out.write(iddata);
				out.write(SUtil.intToBytes(datalength));
				for(int i=0; i<data.length; i++)
				{
					out.write(data[i]);
				}
				out.flush();
	//								out.close();
		
				code	= con.getResponseCode();
			}
			
			if(code!=HttpURLConnection.HTTP_OK)
			{
				throw new IOException(" " +con.getURL() + " " + "HTTP code "+code+": "+con.getResponseMessage()+" target="+targetid);
			}
			while(con.getInputStream().read(RESPONSE_BUF)!=-1)
			{
			}
	//		con.getInputStream().close();
		}
		finally
		{
			if(con!=null)
			{
				remove(con);
			}			
		}
	}
	
	public byte[] getDBEntries(String peeraddress, String peerid, int startid, int cnt)	throws IOException
	{
		byte[]	ret;
		HttpURLConnection	con	= null;
		peeraddress	= httpAddress(peeraddress);
		try
		{
			con	= openConnection(peeraddress+"sync"
				+ "?peerid="+URLEncoder.encode(peerid, "UTF-8")
				+ "&startid="+startid
				+ "&cnt="+cnt);
			
			InputStream	is	= con.getInputStream();
			ByteArrayOutputStream	baos = new ByteArrayOutputStream();
			int	read;
			byte[]	buf	= new byte[8192];
			while((read=is.read(buf))!=-1)
			{
				baos.write(buf, 0, read);
			}
			baos.flush();
			ret	= baos.toByteArray();
		}
		finally
		{
			if(con!=null)
			{
				remove(con);
			}
		}
		return ret;
	}

	
	//-------- helper methods --------
	
	/**
	 *  Convert a potential 'relay-' address to normal http(s) address.
	 *  Also makes sure that addresses end with '/'.
	 */
	public static String	httpAddress(String address)
	{
		if(address.startsWith("relay-"))
		{
			address	= address.substring(6);
		}
		if(!address.endsWith("/") && !address.endsWith("/awareness"))	// For compatibility with old servers: don't add slash (Todo: remove)
		{
			address	+= "/";
		}
		return address;
	}
	
	/**
	 *  Convert a potential non 'relay-' address to relay address.
	 *  Also makes sure that addresses end with '/'.
	 */
	public static String	relayAddress(String address)
	{
		if(!address.startsWith("relay-"))
		{
			address	= "relay-"+address;
		}
		if(!address.endsWith("/") && !address.endsWith("/awareness"))	// For compatibility with old servers: don't add slash (Todo: remove)
		{
			address	+= "/";
		}
		return address;
	}
	
	
	/**
	 *  Convert a potential non-https address to an https address.
	 *  Handles both relay and http addresses.
	 */
	public static String	secureAddress(String address)
	{
		if(address.startsWith("http://"))
		{
			address	= "https://" + address.substring(7);
		}
		else if(address.startsWith("relay-http://"))
		{
			address	= "relay-https://" + address.substring(13);
		}
		return address;
	}
	
	/**
	 *  Test if two addresses refer to the same server.
	 */
	public static boolean	isSameServer(String address1, String address2)
	{
		return secureAddress(httpAddress(address1)).equals(secureAddress(httpAddress(address2)));
	}
}
