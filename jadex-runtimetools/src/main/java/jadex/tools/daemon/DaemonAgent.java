package jadex.tools.daemon;

import jadex.bridge.IArgument;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.commons.Future;
import jadex.commons.IFuture;
import jadex.commons.SUtil;
import jadex.commons.StreamCopy;
import jadex.commons.concurrent.DelegationResultListener;
import jadex.commons.concurrent.IResultListener;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.library.ILibraryService;
import jadex.micro.MicroAgent;
import jadex.micro.MicroAgentMetaInfo;

import java.io.File;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.SwingUtilities;

/**
 * 
 */
public class DaemonAgent extends MicroAgent
{
	//-------- attributes --------
	
	/** The started platforms. */
	protected Map platforms; 
	
	//-------- methods --------
	
	/**
	 *  Called once after agent creation.
	 */
	public void agentCreated()
	{
		platforms = new HashMap();
		addService(new DaemonService(getExternalAccess()));
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				DaemonPanel.createGui(getExternalAccess());
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
		
		if(options.getMain()==null)
		{
			options.setMain("jadex.base.Starter");
		}
		
		if(options.getClassPath()==null)
		{
			SServiceProvider.getService(getServiceProvider(), ILibraryService.class)
				.addResultListener(createResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object source, Object result)
				{
					ILibraryService ls = (ILibraryService)result;
					ls.getAllURLs().addResultListener(createResultListener(new DelegationResultListener(ret)
					{
						public void customResultAvailable(Object source, Object result)
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
							options.setClassPath((String[])res.toArray(new String[res.size()]));
							
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
			
			Process proc = Runtime.getRuntime().exec(options.getStartCommand(), null, newcurdir);
//			FilterInputStream fis = new FilterInputStream(proc.getInputStream())
//			{
//				public int read() throws IOException
//				{
//					int ret = super.read();
//					
//					return ret;
//				}
//			});
			

			FilterOutputStream fos = new FilterOutputStream(System.out)
			{
				StringBuffer buf = new StringBuffer();
				boolean fin = false;
				
				public void write(byte[] b) throws IOException
				{
					synchronized(this)
					{
						if(!fin)
						{
							buf.append(b);
							for(int i=0; i<b.length; i++)
							{
								if(" ".equals(b[i]))
								{
									fin = true;
									ret.setResult(buf.toString());
									break;
								}
							}
						}
					}
					super.write(b);
				}
				
				public void write(byte[] b, int off, int len) throws IOException
				{
					synchronized(this)
					{
						if(!fin)
						{
							for(int i=0; i<len; i++)
							{
								buf.append(b[i]);
								if(" ".equals(b[i]))
								{
									fin = true;
									ret.setResult(buf.toString());
									break;
								}
							}
						}
					}
					super.write(b, off, len);
				}
				
				public void write(int b) throws IOException
				{
					synchronized(this)
					{
						if(!fin)
						{
							buf.append(b);
							if(" ".equals(b))
							{
								fin = true;
								ret.setResult(buf.toString());
							}
						}
					}
					super.write(b);
				}
			};
			
			new Thread(new StreamCopy(proc.getInputStream(), fos)).start();
			new Thread(new StreamCopy(proc.getErrorStream(), fos)).start();
			
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
		
		// Start platform in the same VM.
	//	Starter.createPlatform(args).addResultListener(new IResultListener()
	//	{
	//		public void resultAvailable(Object source, Object result)
	//		{
	//			IExternalAccess platform = (IExternalAccess)result;
	//			platforms.put(platform.getComponentIdentifier(), platform);
	//			ret.setResult(result);
	//		}
	//		
	//		public void exceptionOccurred(Object source, Exception exception)
	//		{
	//			ret.setException(exception);
	//		}
	//	});
		
		return ret;
	}
	
	/**
	 *  Shutdown a platform.
	 *  @param cid The platform id.
	 */
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
	}
	
	//-------- static methods --------

	/**
	 *  Get the meta information about the agent.
	 */
	public static MicroAgentMetaInfo getMetaInfo()
	{
		return new MicroAgentMetaInfo("This agent offers the daemon service.", null, 
			new IArgument[]{}//new Argument("infos", "Initial information records.", "InformationEntry[]")}
			, null, null, SUtil.createHashMap(new String[]{"componentviewer.viewerclass"}, new Object[]{"jadex.tools.daemon.DaemonViewerPanel"}));
	}

}
