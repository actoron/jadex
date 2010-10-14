package jadex.tools.daemon;

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

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public class DaemonService implements IDaemonService
{
	/** The started platforms. */
	protected Map platforms; 
	
	/**
	 *  Create a new daemon service.
	 */
	public DaemonService()
	{
		platforms = new HashMap();
	}
	
	/**
	 *  Start a platform using a configuration.
	 *  @param args The arguments.
	 */
	public IFuture startPlatform(String[] args)
	{
		final Future ret = new Future();
		
		// Start platform in another VM.
		try
		{
			StringBuffer cmd = new StringBuffer().append("java");
			List cps = fetchClasspath();
			for(int i=0; i<cps.size(); i++)
			{
				if(i==0)
					cmd.append(" -cp ");
				else
					cmd.append(File.pathSeparator);
				cmd.append("\"").append(cps.get(i)).append("\"");
			}
			cmd.append(" jadex.base.Starter");
			
//			String[] cmds = options.toCommandLineString(true);
			String cmdline = "";
//			for(int i=0; i<cmds.length; i++)
//				cmdline += " "+cmds[i];
			
			System.out.println(cmd.append(" ").append(cmdline));
			
			// Can be called in another directory
			File newcurdir = null;//new File(options.destdirname);
//			newcurdir.mkdirs();
			Process proc = Runtime.getRuntime().exec(cmd+" "+cmdline, null, newcurdir);
			new Thread(new StreamCopy(proc.getInputStream(), System.out)).start();
			new Thread(new StreamCopy(proc.getErrorStream(), System.out)).start();
			
//			boolean finished = false;
//			while(!finished && !abort)
//			{
//				try
//				{
//					Thread.sleep(300);
//					if(proc.exitValue()!=0)
//						throw new RuntimeException("Java returned an error or could not be invoked.");
//					finished = true;
//				}
//				catch(IllegalThreadStateException ie)
//				{
//					if(abort)
//						proc.destroy();
//				}
//			}
				
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
			throw new RuntimeException("Could not process Javadoc. Reason: "+ex.getMessage());
			//ex.printStackTrace();
			//failed = "Could not process Javadoc. Reason: "+ex.getMessage();
		}
		
		// Start platform in the same VM.
//		Starter.createPlatform(args).addResultListener(new IResultListener()
//		{
//			public void resultAvailable(Object source, Object result)
//			{
//				IExternalAccess platform = (IExternalAccess)result;
//				platforms.put(platform.getComponentIdentifier(), platform);
//				ret.setResult(result);
//			}
//			
//			public void exceptionOccurred(Object source, Exception exception)
//			{
//				ret.setException(exception);
//			}
//		});
		
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
	
	/**
	 *  Get all platforms.
	 *  @param The collection of platforms.
	 */
	public IFuture getPlatforms(final IComponentIdentifier cid)
	{
		return new Future();
	}
	
	/**
	 *  Fetch the current classpath
	 *  @return classpath entries as a list of strings.
	 */
	protected List fetchClasspath()
	{
		List entries	= new ArrayList();

		List cps = SUtil.getClasspathURLs(null);	// todo: classpath?
		for(int i=0; i<cps.size(); i++)
		{
			URL	url	= (URL)cps.get(i);
			String file = url.getFile();
			File f = new File(file);
			
			// Hack!!! Above code doesnt handle relative url paths. 
			if(!f.exists())
			{
				File newfile = new File(new File("."), file);
				if(newfile.exists())
				{
					f = newfile;
				}
			}
			entries.add(f.getAbsolutePath());
		}

		return entries;
	}
}
