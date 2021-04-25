package org.activecomponents.platform.service.message.transport.udpmtp;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.nio.charset.Charset;

import jadex.commons.HttpConnectionManager;
import jadex.commons.Tuple3;

public class SHolepunchServerClient
{
	/** Sends a command to the server.
	 * 
	 * @param conf Server configuration.
	 * @param cmd The command.
	 * @param args The arguments.
	 * @param timeout Timeout for the reply, no reply is needed if set to -1.
	 * @return Server reply.
	 */
	public static final String sendCommand(HolepunchServerConf conf, String cmd, String[] args, int timeout)
	{
		String ret = null;
//		System.out.println("CMDs: " + cmd);
		if (conf.getType() == HolepunchServerConf.BASIC)
		{
			Socket hpsocket = null;
			try
			{
				hpsocket = new Socket();
				hpsocket.setSoTimeout(timeout >= 0? timeout : 30000);
				hpsocket.connect(new InetSocketAddress(conf.getHost(), conf.getPort()), timeout >= 0? timeout : 30000);
				Charset utf8 = Charset.forName("UTF-8");
				BufferedReader reader = new BufferedReader(new InputStreamReader(hpsocket.getInputStream(), utf8));
				String line = "";
				while (!line.toLowerCase().contains("welcome"))
				{
					line = reader.readLine();
//					System.out.println("LINE: " + line);
				}
				
				StringBuilder cmdline = new StringBuilder(cmd);
				for (int i = 0; args != null && i < args.length; ++i)
				{
					cmdline.append(" ");
					cmdline.append(args[i]);
				}
				cmdline.append("\n");
				OutputStream os = hpsocket.getOutputStream();
//				System.out.println("CMDLINE: " + cmdline.toString());
				os.write(cmdline.toString().getBytes(utf8));
				os.flush();
				
				if (timeout >= 0)
				{
					ret = reader.readLine();
				}
				
				try
				{
					hpsocket.close();
				}
				catch(Exception e)
				{
				}
			}
			catch (SocketTimeoutException e)
			{
				e.printStackTrace();
			}
			catch(IOException e)
			{
				if (hpsocket != null)
				{
					try
					{
						hpsocket.close();
					}
					catch(Exception e1)
					{
					}
				}
				throw new RuntimeException(e);
			}
		}
		else
		{
//			System.out.println("starting www");
			StringBuilder urlbuilder = new StringBuilder();
			if (HolepunchServerConf.HTTP == conf.getType())
			{
				urlbuilder.append("http://");
			}
			else
			{
				urlbuilder.append("https://");
			}
			urlbuilder.append(conf.getHost());
			urlbuilder.append(":");
			urlbuilder.append(conf.getPort());
			urlbuilder.append("/holepunch/HpServlet?cmd=");
			URLConnection conn = null;
			try
			{
				urlbuilder.append(URLEncoder.encode(cmd, "UTF-8"));
				for (int i = 0; args != null && i < args.length; ++i)
				{
					urlbuilder.append("&p");
					urlbuilder.append(i);
					urlbuilder.append("=");
					urlbuilder.append(URLEncoder.encode(args[i], "UTF-8"));
				}
//				System.out.println("URL " + urlbuilder.toString());
				URL url = new URL(urlbuilder.toString());
//				System.out.println("opening www");
				conn= url.openConnection();
				
				if (timeout >= 0)
				{
					conn.setConnectTimeout(timeout);
				}
				else
				{
					conn.setConnectTimeout(5000);
				}
//				System.out.println("Connecting www");
				conn.connect();
				
				// Hack? Server-side abort if closed early, so must read?
//				if (timeout >= 0)
				{
//					System.out.println("readinging www");
					
					BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
					ret = reader.readLine();
//					System.out.println("return " + ret);
				}
//				System.out.println("closing www");
				HttpConnectionManager.closeConnection((HttpURLConnection) conn);
			}
			catch (SocketTimeoutException e)
			{
			}
			catch (Exception e)
			{
				if (conn != null)
				{
					HttpConnectionManager.closeConnection((HttpURLConnection) conn);
				}
			}
		}
		
//		System.out.println("CMD: " + cmd + " " + String.valueOf(ret));
		return ret;
	}
	
	/**
	 *  Registers the url with the holepunch server.
	 * 
	 *  @param conf Holepunch server configuration.
	 *  @param url The register URL.
	 *  @return Handle for listen/close operations.
	 */
	public static final Object register(HolepunchServerConf conf, String url)
	{
		Object ret = null;
		if (conf.getType() == HolepunchServerConf.BASIC)
		{
			Socket hpsocket = null;
			try
			{
				hpsocket = new Socket(conf.getHost(), conf.getPort());
				hpsocket.setKeepAlive(true);
				hpsocket.setSoTimeout(2000);
				Charset utf8 = Charset.forName("UTF-8");
				BufferedReader reader = new BufferedReader(new InputStreamReader(hpsocket.getInputStream(), utf8));
				String line = "";
				while (!line.toLowerCase().contains("welcome"))
				{
					line = reader.readLine();
				}
				
				OutputStream os = hpsocket.getOutputStream();
				os.write(("register " + url + "\n").getBytes(utf8));
				os.flush();
				
				String reply = reader.readLine();
				if (!(reply !=null && reply.toLowerCase().startsWith("registered")))
				{
					throw new RuntimeException("Not registered.");
				}
				
				ret = new Tuple3<Socket, BufferedReader, OutputStream>(hpsocket, reader, os);
			}
			catch (IOException e)
			{
			}
		}
		else
		{
			try
			{
				sendCommand(conf, "register", new String[]{url}, 2000);
				ret = url;
			}
			catch (Exception e)
			{
			}
			
		}
		
		return ret;
	}
	
	public static final String listen(HolepunchServerConf conf, Object handle)
	{
		String ret = null;
		if (handle instanceof Tuple3)
		{
			@SuppressWarnings("unchecked")
			Tuple3<Socket, BufferedReader, OutputStream> tup = (Tuple3<Socket, BufferedReader, OutputStream>) handle;
			try
			{
				tup.getFirstEntity().setSoTimeout(30000);
				ret = tup.getSecondEntity().readLine();
			}
			catch (SocketTimeoutException e)
			{
			}
			catch (Exception e)
			{
				try
				{
					tup.getFirstEntity().close();
				}
				catch (Exception e1)
				{
				}
				throw new RuntimeException(e);
			}
		}
		else if (handle instanceof String)
		{
			try
			{
				ret = sendCommand(conf, "listen", new String[] {(String) handle}, 60000);
			}
			catch (Exception e)
			{
			}
		}
		else
		{
			throw new RuntimeException("Invalid handle");
		}
		
		return ret;
	}
	
	public static final void close(Object handle)
	{
		if (handle instanceof Tuple3)
		{
			Tuple3<Socket, BufferedReader, OutputStream> tup = (Tuple3<Socket, BufferedReader, OutputStream>) handle;
			try
			{
				tup.getFirstEntity().close();
			}
			catch(Exception e)
			{
			}
		}
	}
}
