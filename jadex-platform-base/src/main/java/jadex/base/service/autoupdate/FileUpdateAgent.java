package jadex.base.service.autoupdate;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.daemon.StartOptions;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipFile;

@Arguments(replace=false, value=
{
	@Argument(name="scandir", clazz=String.class, defaultvalue="\".\""),
})
@RequiredServices(
{
	@RequiredService(name="libservice", type=ILibraryService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM))
})
@Agent
/**
 *  The file update agent is based on a scan directory in which new versions
 *  are detected. If a new version was found the agent will initiate a platform
 *  restart.
 */
public class FileUpdateAgent extends UpdateAgent
{
	@AgentArgument
	protected String scandir;
	
	/** The current version date. */
	protected long curversion;

	/**
	 *  Generate the start options.s
	 */
	protected IFuture<StartOptions> generateStartOptions(UpdateInfo ui)
	{
		final IFuture<StartOptions> ret = new Future<StartOptions>();
		
		super.generateStartOptions(ui).addResultListener(new DelegationResultListener<StartOptions>(ret)
		{
			public void customResultAvailable(StartOptions result)
			{
				if(ui.getAccess()!=null)
				{
					File dir = new File((String)ui.getAccess());
					File[] jars = dir.listFiles(new FilenameFilter()
					{
						public boolean accept(File dir, String name)
						{
							return name.endsWith(".jar");
						}
					});
					
					StringBuffer buf = new StringBuffer();
					for(int i=0; i<jars.length; i++)
					{
						buf.append(jars[i]);
						if(i+1<jars.length)
							buf.append(File.pathSeparator);
					}
					ret.setClassPath(buf.toString());
				}
				else
				{
					ret.setResult(null);
				}
			}
		});
		
		return ret;
	}
	
	/**
	 *  Check if an update is available.
	 */
	protected IFuture<UpdateInfo> checkForUpdate()
	{
		final Future<UpdateInfo> ret = new Future<UpdateInfo>();
		
		getCurrentVersion().addResultListener(new ExceptionDelegationResultListener<Long, UpdateInfo>(ret)
		{
			public void customResultAvailable(Long curversion)
			{
				SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy hh:mm:ss");
				System.out.println("Running on version: "+agent.getComponentIdentifier()+" "+sdf.format(new Date(curversion)));
				
				File distdir = new File(scandir);
				
				if(distdir.exists())
				{
					File[] dists = distdir.listFiles(new FilenameFilter()
					{
						public boolean accept(File dir, String name)
						{
							return name.toLowerCase().startsWith("jadex") && name.endsWith(".zip");
						}
					});
					
					
					File newdist = null;
					for(File dist: dists)
					{
						if(dist.lastModified()>curversion)
						{
							newdist = dist;
						}
					}
					
					if(newdist!=null)
					{
						try
						{
							File tdir = new File(""+newdist.lastModified());
							tdir.mkdir();
							SUtil.unzip(new ZipFile(newdist), tdir);
							
							File[] decoms = tdir.listFiles(new FileFilter()
							{
								public boolean accept(File file)
								{
									return file.isDirectory();
								}
							});
							if(decoms.length==1)
							{
								System.out.println("Updating to version: "+sdf.format(new Date(newdist.lastModified())));

								UpdateInfo ui = new UpdateInfo(newdist.lastModified(), new File(decoms[0], "lib").getCanonicalPath());
								ret.setResult(ui);
							}
							else
							{
								ret.setException(new RuntimeException("Unexpectedly found not exactly one directory in decompressed distribution: "+SUtil.arrayToString(decoms)));
							}
						}
						catch(Exception e)
						{
							e.printStackTrace();
							ret.setException(e);
						}
					}
					else
					{
						// no newer version found
						ret.setResult(null);
					}
				}
			}
		});
		
		return ret;
	}
	
	/**
	 *  Get the current version.
	 */
	protected IFuture<Long> getCurrentVersion()
	{
		final Future<Long> ret = new Future<Long>();
		
		if(curversion!=0)
		{
			ret.setResult(new Long(curversion));
		}
		else if(curversion!=-1)
		{
			IFuture<ILibraryService> fut = agent.getRequiredService("libservice");
			fut.addResultListener(new ExceptionDelegationResultListener<ILibraryService, Long>(ret)
			{
				public void customResultAvailable(ILibraryService libser)
				{
					IFuture<List<URL>> fut = libser.getAllURLs();
					fut.addResultListener(new ExceptionDelegationResultListener<List<URL>, Long>(ret)
					{
						public void customResultAvailable(List<URL> result)
						{
							// search for jadex jar file
							for(URL url: result)
							{
								String fileurl = (url.getFile());
								if(fileurl.endsWith(".jar") && fileurl.indexOf("jadex")!=-1)
								{
									File f = new File(fileurl);
									if(f.exists())
									{
										curversion = f.lastModified();
										ret.setResult(new Long(curversion));
										break;
									}
								}
							}
							
							if(curversion==0)
							{
								// search for jadex classes dir
								for(URL url: result)
								{
									String fileurl = (url.getFile());
									if(fileurl.indexOf("jadex")!=-1)
									{
										File f = new File(fileurl);
										if(f.exists() && f.isDirectory())
										{
											curversion = f.lastModified();
											ret.setResult(new Long(curversion));
											break;
										}
									}
								}
								
								// remember that nothing was found
								if(curversion==0)
								{
									curversion = -1;
									ret.setException(new RuntimeException("Unable to determine current version."));
								}
							}
						}
					});
				}
			});
		}
		else
		{
			ret.setException(new RuntimeException("Unable to determine current version."));
		}
	
		return ret;
	}
}
