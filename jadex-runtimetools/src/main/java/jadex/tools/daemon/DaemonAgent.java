package jadex.tools.daemon;

import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentIdentifier;
import jadex.commons.ChangeEvent;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.SUtil;
import jadex.commons.StreamCopy;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.ProvidedServiceInfo;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.library.ILibraryService;
import jadex.micro.IMicroExternalAccess;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

/**
 *  Daemon agent provides functionalities for managing platforms.
 */
public class DaemonAgent extends MicroAgent
{	
	//-------- attributes --------
	
	/** The started platforms. */
	protected Map platforms; 
	
	/** The listeners. */
	protected List listeners;

	//-------- methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	public void agentCreated()
	{
		platforms = Collections.synchronizedMap(new HashMap());
		listeners = Collections.synchronizedList(new ArrayList());
		addDirectService(new DaemonService(getExternalAccess()));
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				DaemonPanel.createGui((IMicroExternalAccess)getExternalAccess());
			}
		});
	}
	
	/**
	 *  Start a platform using a configuration.
	 *  @param args The arguments.
	 */
	public IFuture startPlatform(StartOptions opt)
	{
		final Future ret = new Future();
		
		final StartOptions options = opt==null? new StartOptions(): opt;
		
//		if(options.getMain()==null)
//		{
//			options.setMain("jadex.base.Starter");
//		}
		
		if(options.getClassPath()==null || options.getClassPath().length()==0)
		{
			SServiceProvider.getService(getServiceProvider(), ILibraryService.class)
				.addResultListener(createResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					ILibraryService ls = (ILibraryService)result;
					ls.getAllURLs().addResultListener(createResultListener(new DelegationResultListener(ret)
					{
						public void customResultAvailable(Object result)
						{
							List urls = (List)result;
							List res = new ArrayList();
							for(int i=0; i<urls.size(); i++)
							{
								URL url = (URL)urls.get(i);
								String name = SUtil.convertURLToString(url);
								if(name!=null)
									res.add(name);
								else
									System.out.println("Cannot convert url to file: "+url);
							}
							StringBuffer buf = new StringBuffer();
							for(int i=0; i<res.size(); i++)
							{
								buf.append(res.get(i));
								if(i+1<res.size())
									buf.append(File.pathSeparator);
							}
							options.setClassPath(buf.toString());
							
							doStartPlatform(options).addResultListener(new DelegationResultListener(ret));
						}
					}));
				}	
			}));
		}
		else
		{
			doStartPlatform(options).addResultListener(createResultListener(new DelegationResultListener(ret)));
		}
	
		return ret;
	}
	
	/**
	 *  Start a platform using a configuration.
	 *  @param args The arguments.
	 * /
	public IFuture startPlatform(StartOptions opt)
	{
		final Future ret = new Future();
		
		// Start platform in the same VM.
		Starter.createPlatform(args).addResultListener(new IResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IExternalAccess platform = (IExternalAccess)result;
				platforms.put(platform.getComponentIdentifier(), platform);
				ret.setResult(result);
			}
			
			public void exceptionOccurred(Object source, Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		return ret;
	}*/
	
	/**
	 * 
	 */
	public IFuture doStartPlatform(StartOptions options)
	{
		final Future ret = new Future();
		
		// Start platform in another VM.
		try
		{
			// Can be called in another directory
			File newcurdir = new File(options.getStartDirectory());
			newcurdir.mkdirs();
			
			String cmd = options.getStartCommand();
			
			System.out.println("Starting process: "+cmd);
			
			final Process proc = Runtime.getRuntime().exec(options.getStartCommand(), null, newcurdir);

			FilterOutputStream fos = new FilterOutputStream(System.out)
			{
				StringBuffer buf = new StringBuffer();
				boolean fin = false;
				
				public void write(byte[] b) throws IOException
				{
					if(!fin)
					{
						buf.append(new String(b));
						for(int i=0; i<b.length; i++)
						{
							if(' '==b[i])
							{
								fin = true;
								IComponentIdentifier cid = new ComponentIdentifier(buf.toString());
								platforms.put(cid, proc);
								notifyListeners(new ChangeEvent(null, IDaemonService.ADDED, cid));
								ret.setResult(cid);
								break;
							}
						}
					}
					super.write(b);
				}
				
				public void write(byte[] b, int off, int len) throws IOException
				{
					if(!fin)
					{
						for(int i=0; i<len; i++)
						{
							buf.append((char)b[i]);
							if(' '==b[i])
							{
								fin = true;
								IComponentIdentifier cid = new ComponentIdentifier(buf.toString());
								platforms.put(cid, proc);
								notifyListeners(new ChangeEvent(null, IDaemonService.ADDED, cid));
								ret.setResult(cid);
								break;
							}
						}
					}
					super.write(b, off, len);
				}
				
				public void write(int b) throws IOException
				{
					if(!fin)
					{
						buf.append((char)b);
						if(' '==b)
						{
							fin = true;
							IComponentIdentifier cid = new ComponentIdentifier(buf.toString());
							platforms.put(cid, proc);
							notifyListeners(new ChangeEvent(null, IDaemonService.ADDED, cid));
							ret.setResult(cid);
						}
					}
					super.write(b);
				}
			};
			
			new Thread(new StreamCopy(proc.getInputStream(), fos)).start();
			new Thread(new StreamCopy(proc.getErrorStream(), System.err)).start();
			
	//		boolean finished = false;
	//		while(!finished && !abort)
	//		{
	//			try
	//			{
	//				Thread.sleep(300);
	//				if(proc.exitValue()!=0)
	//					throw new RuntimeException("Java returned an error or could not be invoked.");
	//				finished = true;
	//			}
	//			catch(IllegalThreadStateException ie)
	//			{
	//				if(abort)
	//					proc.destroy();
	//			}
	//		}
				
			/*Process proc = Runtime.getRuntime().exec(cmd+" "+cmdline);
			new Thread(new StreamCopy(proc.getInputStream(), System.out)).start();
			new Thread(new StreamCopy(proc.getErrorStream(), System.out)).start();
				
			if(proc.waitFor()!=0)
			// todo: does not work because process output stream is not read :-(
			//if(Runtime.getRuntime().exec(cmd+" -classpath "+cp+cmdline).waitFor()!=0)
			{
				throw new RuntimeException("Javadoc returned an error or could not be invoked.");
			}*/
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new RuntimeException("Could not start process. Reason: "+ex.getMessage());
		}
		
		return ret;
	}
	
	/**
	 *  Shutdown a platform.
	 *  @param cid The platform id.
	 */
	public IFuture shutdownPlatform(final IComponentIdentifier cid)
	{
		final Future ret = new Future();
		
		Process proc = (Process)platforms.get(cid);
		if(proc==null)
		{
			ret.setException(new RuntimeException("Platform not found: "+cid));
		}
		else
		{
			proc.destroy();
			ret.setResult(null);
			notifyListeners(new ChangeEvent(this, IDaemonService.REMOVED, cid));
		}
		
		return ret;
	}
	
	/**
	 *  Shutdown a platform.
	 *  @param cid The platform id.
	 * /
	public IFuture shutdownPlatform(final IComponentIdentifier cid)
	{
		final Future ret = new Future();
		
		IExternalAccess platform = (IExternalAccess)platforms.get(cid);
		if(platform==null)
		{
			ret.setException(new RuntimeException("No platform found: "+cid));
		}
		else
		{
			SServiceProvider.getService(platform.getServiceProvider(), IComponentManagementService.class)
				.addResultListener(new IResultListener()
			{
				public void resultAvailable(Object source, Object result)
				{
					IComponentManagementService cms = (IComponentManagementService)result;
					cms.destroyComponent(cid).addResultListener(new DelegationResultListener(ret));
				}
				
				public void exceptionOccurred(Object source, Exception exception)
				{
					ret.setException(exception);
				}
			});
		}
		
		return ret;
	}*/
	
	/**
	 *  Get the component identifiers of all (managed) platforms.
	 *  @return Collection of platform ids.
	 */
	public IFuture getPlatforms()
	{
		return new Future(platforms.keySet());
	}
		
	/**
	 *  Add a change listener.
	 *  @param listener The change listener.
	 */
	public void addChangeListener(IRemoteChangeListener listener)
	{
		listeners.add(listener);
	}
	
	/**
	 *  Remove a change listener.
	 *  @param listener The change listener.
	 */
	public void removeChangeListener(IRemoteChangeListener listener)
	{
		listeners.remove(listener);
	}
	
	/**
	 *  Notify the listeners.
	 */
	protected void notifyListeners(ChangeEvent event)
	{
		IRemoteChangeListener[] alisteners;
		synchronized(this)
		{
			alisteners	= listeners.isEmpty()? null: 
				(IRemoteChangeListener[])listeners.toArray(new IRemoteChangeListener[0]);
		}
		if(alisteners!=null)
		{
			for(int i=0; i<alisteners.length; i++)
			{
				final IRemoteChangeListener lis = alisteners[i];
				alisteners[i].changeOccurred(event).addResultListener(createResultListener(new IResultListener()
				{
					public void resultAvailable(Object result)
					{
					}
					
					public void exceptionOccurred(Exception exception)
					{
//						System.out.println("Removing listener: "+lis);
						removeChangeListener(lis);
					}
				}));
			}
		}
	}
	
	//-------- static methods --------

	/**
	 *  Get the meta information about the agent.
	 */
	public static MicroAgentMetaInfo getMetaInfo()
	{
		return new MicroAgentMetaInfo("This agent offers the daemon service.", null, 
			new IArgument[]{}//new Argument("infos", "Initial information records.", "InformationEntry[]")}
			, null, null, SUtil.createHashMap(new String[]{"componentviewer.viewerclass"}, new Object[]{"jadex.tools.daemon.DaemonViewerPanel"}),
			new RequiredServiceInfo[]{new RequiredServiceInfo("libservice", ILibraryService.class)}
			, new ProvidedServiceInfo[]{new ProvidedServiceInfo(IDaemonService.class)});
	}

}
