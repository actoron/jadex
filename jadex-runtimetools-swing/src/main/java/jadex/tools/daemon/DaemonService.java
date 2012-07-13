package jadex.tools.daemon;

import jadex.bridge.ComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.ChangeEvent;
import jadex.commons.IRemoteChangeListener;
import jadex.commons.SUtil;
import jadex.commons.StreamCopy;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.gui.SGUI;

import java.io.File;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  The daemon service.
 */
@Service
public class DaemonService implements IDaemonService
{
	//-------- attributes --------
	
	/** The agent. */
	@ServiceComponent
	protected IInternalAccess agent;
		
	/** The started platforms. */
	protected Map<IComponentIdentifier, Process> platforms;

	/** The listeners. */
	protected List<IRemoteChangeListener<IComponentIdentifier>>	listeners;

	
	//-------- methods --------
	
	/**
	 *  Start the service.
	 */
	@ServiceStart
	public void start()
	{
		platforms = Collections.synchronizedMap(new HashMap());
		listeners = Collections.synchronizedList(new ArrayList());
		// addService("daemonservice", IDaemonService.class, new
		// DaemonService(getExternalAccess()),
		// BasicServiceInvocationHandler.PROXYTYPE_DIRECT);

		SGUI.invokeLater(new Runnable()
		{
			public void run()
			{
				DaemonPanel.createGui(agent.getExternalAccess());
			}
		});
	}
	
	/**
	 * Start a platform using a configuration.
	 * @param args The arguments.
	 */
	public IFuture<IComponentIdentifier> startPlatform(StartOptions opt)
	{
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();

		final StartOptions options = opt == null ? new StartOptions() : opt;

		// if(options.getMain()==null)
		// {
		// options.setMain("jadex.base.Starter");
		// }

		if(options.getClassPath() == null || options.getClassPath().length() == 0)
		{
			IFuture<ILibraryService> fut = agent.getServiceContainer().getRequiredService("libservice");
			fut.addResultListener(new ExceptionDelegationResultListener<ILibraryService, IComponentIdentifier>(ret)
			{
				public void customResultAvailable(ILibraryService result)
				{
					ILibraryService ls = (ILibraryService)result;
					ls.getAllURLs().addResultListener(new ExceptionDelegationResultListener<List<URL>, IComponentIdentifier>(ret)
					{
						public void customResultAvailable(List<URL> urls)
						{
							List<String> res = new ArrayList<String>();
							for(int i = 0; i<urls.size(); i++)
							{
								URL url = (URL)urls.get(i);
								String name = SUtil.convertURLToString(url);
								if(name != null)
								{
									res.add(name);
								}
								else
								{
									System.out.println("Cannot convert url to file: "+ url);
								}
							}
							StringBuffer buf = new StringBuffer();
							for(int i = 0; i < res.size(); i++)
							{
								buf.append(res.get(i));
								if(i + 1 < res.size())
									buf.append(File.pathSeparator);
							}
							options.setClassPath(buf.toString());

							doStartPlatform(options).addResultListener(new DelegationResultListener<IComponentIdentifier>(ret));
						}
					});
				}
			});
		}
		else
		{
			doStartPlatform(options).addResultListener(
				agent.createResultListener(new DelegationResultListener<IComponentIdentifier>(ret)));
		}

		return ret;
	}

	/**
	 * Start a platform using a configuration.
	 * 
	 * @param args The arguments. / public IFuture startPlatform(StartOptions
	 *        opt) { final Future ret = new Future(); // Start platform in the
	 *        same VM. Starter.createPlatform(args).addResultListener(new
	 *        IResultListener() { public void resultAvailable(Object source,
	 *        Object result) { IExternalAccess platform =
	 *        (IExternalAccess)result;
	 *        platforms.put(platform.getComponentIdentifier(), platform);
	 *        ret.setResult(result); } public void exceptionOccurred(Object
	 *        source, Exception exception) { ret.setException(exception); } });
	 *        return ret; }
	 */
	public IFuture<IComponentIdentifier> doStartPlatform(StartOptions options)
	{
		final Future<IComponentIdentifier> ret = new Future<IComponentIdentifier>();

		// Start platform in another VM.
		try
		{
			// Can be called in another directory
			File newcurdir = new File(options.getStartDirectory());
			newcurdir.mkdirs();

			String cmd = options.getStartCommand();

			System.out.println("Starting process: " + cmd);

			final Process proc = Runtime.getRuntime().exec(options.getStartCommand(), null, newcurdir);

			FilterOutputStream fos = new FilterOutputStream(System.out)
			{
				StringBuffer	buf	= new StringBuffer();
				boolean	fin	= false;

				public void write(byte[] b) throws IOException
				{
					if(!fin)
					{
						buf.append(new String(b));
						for(int i = 0; i < b.length; i++)
						{
							if(' ' == b[i])
							{
								fin = true;
								IComponentIdentifier cid = new ComponentIdentifier(buf.toString());
								platforms.put(cid, proc);
								notifyListeners(new ChangeEvent<IComponentIdentifier>(null,IDaemonService.ADDED, cid));
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
						for(int i = 0; i < len; i++)
						{
							buf.append((char)b[i]);
							if(' ' == b[i])
							{
								fin = true;
								IComponentIdentifier cid = new ComponentIdentifier(buf.toString());
								platforms.put(cid, proc);
								notifyListeners(new ChangeEvent<IComponentIdentifier>(null, IDaemonService.ADDED, cid));
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
						if(' ' == b)
						{
							fin = true;
							IComponentIdentifier cid = new ComponentIdentifier(buf.toString());
							platforms.put(cid, proc);
							notifyListeners(new ChangeEvent<IComponentIdentifier>(null, IDaemonService.ADDED, cid));
							ret.setResult(cid);
						}
					}
					super.write(b);
				}
			};

			new Thread(new StreamCopy(proc.getInputStream(), fos)).start();
			new Thread(new StreamCopy(proc.getErrorStream(), System.err)).start();

			// boolean finished = false;
			// while(!finished && !abort)
			// {
			// try
			// {
			// Thread.sleep(300);
			// if(proc.exitValue()!=0)
			// throw new
			// RuntimeException("Java returned an error or could not be invoked.");
			// finished = true;
			// }
			// catch(IllegalThreadStateException ie)
			// {
			// if(abort)
			// proc.destroy();
			// }
			// }

			/*
			 * Process proc = Runtime.getRuntime().exec(cmd+" "+cmdline); new
			 * Thread(new StreamCopy(proc.getInputStream(),
			 * System.out)).start(); new Thread(new
			 * StreamCopy(proc.getErrorStream(), System.out)).start();
			 * if(proc.waitFor()!=0) // todo: does not work because process
			 * output stream is not read :-(
			 * //if(Runtime.getRuntime().exec(cmd+" -classpath "
			 * +cp+cmdline).waitFor()!=0) { throw new
			 * RuntimeException("Javadoc returned an error or could not be invoked."
			 * ); }
			 */
		}
		catch(Exception ex)
		{
			ex.printStackTrace();
			throw new RuntimeException("Could not start process. Reason: "+ ex.getMessage());
		}

		return ret;
	}

	/**
	 * Shutdown a platform.
	 * @param cid The platform id.
	 */
	public IFuture<Void> shutdownPlatform(final IComponentIdentifier cid)
	{
		final Future<Void> ret = new Future<Void>();

		Process proc = (Process)platforms.get(cid);
		if(proc == null)
		{
			ret.setException(new RuntimeException("Platform not found: " + cid));
		}
		else
		{
			proc.destroy();
			ret.setResult(null);
			notifyListeners(new ChangeEvent<IComponentIdentifier>(this, IDaemonService.REMOVED, cid));
		}

		return ret;
	}

	/**
	 * Shutdown a platform.
	 * 
	 * @param cid The platform id. / public IFuture shutdownPlatform(final
	 *        IComponentIdentifier cid) { final Future ret = new Future();
	 *        IExternalAccess platform = (IExternalAccess)platforms.get(cid);
	 *        if(platform==null) { ret.setException(new
	 *        RuntimeException("No platform found: "+cid)); } else {
	 *        SServiceProvider.getService(platform.getServiceProvider(),
	 *        IComponentManagementService.class) .addResultListener(new
	 *        IResultListener() { public void resultAvailable(Object source,
	 *        Object result) { IComponentManagementService cms =
	 *        (IComponentManagementService)result;
	 *        cms.destroyComponent(cid).addResultListener(new
	 *        DelegationResultListener(ret)); } public void
	 *        exceptionOccurred(Object source, Exception exception) {
	 *        ret.setException(exception); } }); } return ret; }
	 */

	/**
	 * Get the component identifiers of all (managed) platforms.
	 * 
	 * @return Collection of platform ids.
	 */
	public IFuture<Set<IComponentIdentifier>> getPlatforms()
	{
		return new Future<Set<IComponentIdentifier>>(platforms.keySet());
	}

	/**
	 * Add a change listener.
	 * 
	 * @param listener The change listener.
	 */
	public IFuture<Void> addChangeListener(IRemoteChangeListener<IComponentIdentifier> listener)
	{
		listeners.add(listener);
		return IFuture.DONE;
	}

	/**
	 * Remove a change listener.
	 * 
	 * @param listener The change listener.
	 */
	public IFuture<Void> removeChangeListener(IRemoteChangeListener<IComponentIdentifier> listener)
	{
		listeners.remove(listener);
		return IFuture.DONE;
	}

	/**
	 * Notify the listeners.
	 */
	protected void notifyListeners(ChangeEvent<IComponentIdentifier> event)
	{
		IRemoteChangeListener<IComponentIdentifier>[] alisteners;
		synchronized(this)
		{
			alisteners = listeners.isEmpty() ? null: (IRemoteChangeListener<IComponentIdentifier>[])listeners.toArray(new IRemoteChangeListener[0]);
		}
		if(alisteners != null)
		{
			for(int i = 0; i < alisteners.length; i++)
			{
				final IRemoteChangeListener<IComponentIdentifier> lis = alisteners[i];
				alisteners[i].changeOccurred(event).addResultListener(agent.createResultListener(new IResultListener<Void>()
				{
					public void resultAvailable(Void result)
					{
					}

					public void exceptionOccurred(Exception exception)
					{
						// System.out.println("Removing listener: "+lis);
						removeChangeListener(lis);
					}
				}));
			}
		}
	}
}
