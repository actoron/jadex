package org.activecomponents.udp.holepunching.server;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.activecomponents.udp.holepunching.server.commands.IServerCommand;

public class ServerConnection implements Runnable, IConnectedHost, IRegisteredHost
{
	protected static final String WELCOME_MSG = "Actoron UDP Holepunching Server\nWelcome. Please enter your requests.\n";
	
	public static final List<String> COMMAND_CLASSNAMES = new ArrayList<String>();
	public static final List<String> COMMAND_CLASSNAMES_WEB = new ArrayList<String>();
	
	/** Socket that receives commands from the client. */
	protected Socket cmdsocket;
	
	protected DatagramSocket dgsocket;
	
	protected Map<String, IRegisteredHost> registeredhosts;
	
	protected IServerCommand[] commands;
	
	protected byte[] readbuffer;
	
	protected boolean running;
	
	protected volatile String registered;
	
	public ServerConnection(Socket cmdsocket, DatagramSocket dgsocket, Map<String, IRegisteredHost> registeredhosts)
	{
		this.cmdsocket = cmdsocket;
		this.dgsocket = dgsocket;
		this.registeredhosts = registeredhosts;
		readbuffer = new byte[512];
		
		commands = getCommands(COMMAND_CLASSNAMES, getClass().getClassLoader());
		
		Thread t = new Thread(this);
		t.setDaemon(true);
		t.start();
	}
	
	public void run()
	{
		running = true;
		try
		{
			cmdsocket.setKeepAlive(true);
			writeMsg(WELCOME_MSG);
			while (running)
			{
				String line = readNextLine();
				if (line == null)
				{
					running = false;
				}
				else
				{
					line = line.trim();
//					System.out.println("CMD from " + cmdsocket.getInetAddress().getHostAddress() + ": " +line);
					String[] splitline = line.split("\\s");
					String cmd = splitline[0];
					List<String> args = new ArrayList<String>();
					for (int i = 1; i < splitline.length; ++i)
					{
						if (splitline.length > 0)
							args.add(splitline[i]);
					}
					String ret = runCommand(cmd, args.toArray(new String[args.size()]));
					writeMsg(ret);
				}
			}
		}
		catch(Exception e)
		{
			running = false;
		}
		if (registered != null)
		{
			synchronized (registeredhosts)
			{
				registeredhosts.remove(registered);
			}
		}
		try
		{
			cmdsocket.close();
		}
		catch(Exception e1)
		{
		}
	}
	
	/**
	 *  Writes a message to the connected host.
	 *  
	 *  @param msg The message.
	 */
	public void writeMsg(String msg)
	{
		try
		{
			OutputStream os = cmdsocket.getOutputStream();
			synchronized (os)
			{
				os.write(msg.getBytes(Server.UTF8));
				os.flush();
			}
		}
		catch(IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Gets the address of the connected host.
	 *  @return The address.
	 */
	public InetAddress getRemoteAddress()
	{
		return cmdsocket.getInetAddress();
	}
	
	/**
	 *  Retrieves the UDP socket for testing communication.
	 *  
	 *  @return The UDP socket.
	 */
	public DatagramSocket getUdpSocket()
	{
		return dgsocket;
	}
	
	/**
	 *  Retrieves the registered hosts.
	 *  
	 *  @return The registered hosts.
	 */
	public Map<String, IRegisteredHost> getRegisteredHosts()
	{
		return registeredhosts;
	}
	
	public String getRegistered()
	{
		return registered;
	}
	
	public void setRegistered(String registered)
	{
		this.registered = registered;
	}
	
	protected String runCommand(String cmd, String[] args)
	{
		for (int i = 0; i < commands.length; ++i)
		{
			if (commands[i] != null && commands[i].isApplicable(cmd))
			{
				return commands[i].execute(cmd, args, this);
			}
		}
		return cmd+": command not found\n";
	}
	
	protected String readNextLine() throws Exception
	{
		String ret = null;
		int pos = 0;
		while (ret == null)
		{
			int in = cmdsocket.getInputStream().read();
			if (in == -1)
			{
				throw new RuntimeException("End of Stream.");
			}
			
			readbuffer[pos] = (byte) in;
			++pos;
			
			if (in == 10 || in == 13)
			{
				ret = new String(readbuffer, 0, pos, Server.UTF8);
			}
			else
			{
				if (pos == readbuffer.length)
				{
					throw new RuntimeException("Command buffer exceeded.");
				}
			}
		}
		
		return ret;
	}
	
	public static IServerCommand[] getCommands(List<String> classnames, ClassLoader cl)
	{
		IServerCommand[] ret = new IServerCommand[classnames.size()];
		for (int i = 0; i < ret.length; ++i)
		{
			String name = classnames.get(i);
			if (!IServerCommand.class.getSimpleName().equals(name) && !name.contains("$"))
			{
				try
				{
					String prefix = "";
					if ((name.indexOf('.') < 0))
						prefix = IServerCommand.class.getPackage().getName() + ".";
					ret[i] = (IServerCommand) cl.loadClass(prefix + name).newInstance();
				}
				catch (Exception e)
				{
					System.out.println(name);
					System.out.println(Arrays.toString(classnames.toArray()));
					e.printStackTrace();
				}
			}
		}
		
		return ret;
	}
	
	static
	{
		String pckgname = IServerCommand.class.getPackage().getName();
		ClassLoader classloader = ServerConnection.class.getClassLoader();
		
		URL[] urls =  ((URLClassLoader)classloader).getURLs();

		List<String> classnames = new ArrayList<String>();
		for(URL url : urls)
		{
			try
			{
				File f = new File(url.toURI());
				if(f.getName().endsWith(".jar"))
				{
					JarFile	jar = null;
					
					try
					{
						jar	= new JarFile(f);
						for(Enumeration<JarEntry> e=jar.entries(); e.hasMoreElements(); )
						{
							JarEntry je = e.nextElement();
							String name = je.getName().replaceAll("[\\/]", ".");
							if(name.startsWith(pckgname) && name.endsWith(".class"))	
							{
								String cn = name.substring(0, name.length() - 6);
								cn = cn.substring(cn.lastIndexOf('.') + 1);
								classnames.add(cn);
							}
						}
						jar.close();
					}
					catch(Exception e)
					{
					}
					finally
					{
						if(jar!=null)
						{
							jar.close();
						}
					}
				}
				else if(f.isDirectory())
				{
					String pckgloc = f.getAbsolutePath() + File.separator + pckgname.replaceAll("\\.", File.separator);
					String[] files = (new File(pckgloc)).list();
					for (String file : files)
					{
						if (file.endsWith(".class"))
						{
							String cn = file.substring(file.lastIndexOf(File.separator) + 1, file.length() - 6);
							classnames.add(cn);
						}
					}
				}
			}
			catch(Exception e)
			{
			}
		}
		COMMAND_CLASSNAMES.addAll(classnames);
		
		COMMAND_CLASSNAMES_WEB.addAll(classnames);
		COMMAND_CLASSNAMES_WEB.remove("Register");
		COMMAND_CLASSNAMES_WEB.add("com.actoron.udp.holepunching.server.webcommands.Register");
		COMMAND_CLASSNAMES_WEB.add("com.actoron.udp.holepunching.server.webcommands.Listen");
	}
}
