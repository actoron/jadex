package jadex.platform.service.autoupdate;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;
import java.util.zip.ZipFile;

import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.daemon.StartOptions;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

/**
 *  The file update agent is based on a scan directory in which new versions
 *  are detected. If a new version was found the agent will initiate a platform
 *  restart.
 */
@Arguments(replace=false, value=
{
	@Argument(name="rootdir", clazz=String.class, description="Directory where to create new distribution directories", defaultvalue="\".\""),
	@Argument(name="scandir", clazz=String.class, defaultvalue="\".\""),
	@Argument(name="excludedirs", clazz=String.class, defaultvalue="\"tmp.*\""),
	@Argument(name="includefiles", clazz=String.class, defaultvalue="\"jadex-(([0-9]+\\\\.)|(.*addon)|(pro)).*.zip\"", description="Only main Jadex distribution jars."),
	@Argument(name="safetydelay", clazz=long.class, description="Additional waiting time before update to prevent updating to incomplete builds.", defaultvalue="10000"),
	@Argument(name="libdir", clazz=String.class, description="Directory in the distribution, where jar files are located.", defaultvalue="\"lib\""),
})
@RequiredServices(
{
	@RequiredService(name="libservice", type=ILibraryService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_PLATFORM))
})
@Agent
public class FileUpdateAgent extends UpdateAgent
{
	@AgentArgument
	protected String rootdir;
	
	@AgentArgument
	protected String scandir;
	
	@AgentArgument
	protected String libdir;
	
	/** The newest version date (either current version or newest detected file). */
	protected long newestversion;
	
	/** The file include pattern. */
	@AgentArgument
	protected String includefiles;
	
	/** The dir exclude pattern. */
	@AgentArgument
	protected String excludedirs;
	
	/** The safety delay. */
	@AgentArgument
	protected long safetydelay;

	/**
	 *  Generate the start options.
	 *  
	 *  - classpath: sets it to all jars of the newest found jadex distribution dir.
	 */
	protected IFuture<StartOptions> generateStartOptions(final UpdateInfo ui)
	{
		final Future<StartOptions> ret = new Future<StartOptions>();
		
		super.generateStartOptions(ui).addResultListener(new DelegationResultListener<StartOptions>(ret)
		{
			public void customResultAvailable(StartOptions so)
			{
				try
				{
					if(ui.getAccess()!=null)
					{
						// Todo: allow recursive search for jars.
						File startdir = new File((String)ui.getAccess());
						so.setStartDirectory(startdir.getCanonicalPath());
						File jardir	= new File(startdir, libdir);
						File[] jars = jardir.listFiles(new FilenameFilter()
						{
							public boolean accept(File dir, String name)
							{
								return name.endsWith(".jar");
							}
						});
						List<String> jarurls = new ArrayList<String>();
						for(File jar: jars)
						{
							jarurls.add(jar.getCanonicalPath());
						}
						
						so.setClassPath(flattenStrings((Iterator)SReflect.getIterator(jarurls), File.pathSeparator));
//						System.out.println("start options: "+so);
						
						ret.setResult(so);
					}
					else
					{
						ret.setResult(null);
					}
				}
				catch(Exception e)
				{
					agent.getLogger().warning("Error setting classpath: "+e.getMessage());
					ret.setException(e);
//					e.printStackTrace();
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
		
		getLastVersion().addResultListener(new ExceptionDelegationResultListener<Long, UpdateInfo>(ret)
		{
			public void customResultAvailable(Long lastver)
			{
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
//				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//				System.out.println("Running on version: "+agent.getComponentIdentifier()+" "+sdf.format(new Date(curver)));
				
				TreeSet<File> res = new TreeSet<File>(new Comparator<File>()
				{
					public int compare(File o1, File o2)
					{
						long	comp	= o2.lastModified()-o1.lastModified();
						int	ret	= comp>0 ? 1 : comp<0 ? -1 : 0;
//						agent.getLogger().info("comp: "+o1+", "+o2+", "+ret);
						return ret;
					}
				});
				findDistDirs(new File(scandir), res);
				
//				agent.getLogger().info("scanning: "+res);
				
				long foundver = 0;
				if(res.size()>0)
				{
//					System.out.println("include filter: "+includefiles);
					File[] files = res.iterator().next().listFiles(new FilenameFilter()
					{
						public boolean accept(File dir, String name)
						{
							boolean	ret	= name.toLowerCase().matches(includefiles);
//							agent.getLogger().info("match dist file: "+ret+", "+name+", "+dir.getAbsolutePath());
							return ret;
						}
					});
					
					for(int i=0; i<files.length; i++)
					{
						foundver = Math.max(foundver, files[i].lastModified());
					}
					agent.getLogger().info(agent.getComponentIdentifier()+": foundver vs lastver(+safety): "+foundver+", "+(lastver+safetydelay));
					boolean force = false; // force update
					// Only update when not younger than safetydelay and difference between versions also greater than safetydelay.
					if(foundver>lastver+safetydelay && foundver+safetydelay<System.currentTimeMillis() || force)
					{
//						System.out.println("new version");
						newestversion	= foundver;
						File dir = null;
						try
						{
							// create new directory for distribution
							Date founddate = new Date(foundver);
							dir = new File(rootdir, sdf.format(founddate)+"_dist");
							if(dir.exists())
							{
								SUtil.deleteDirectory(dir);
							}
							dir.mkdir();
							
							// unzip all files
							for(File file: files)
							{
								SUtil.unzip(new ZipFile(file), dir);
							}

							// find distribution directory (must be one)
							File[] decoms = dir.listFiles(new FileFilter()
							{
								public boolean accept(File file)
								{
									return file.isDirectory();
								}
							});
							if(decoms.length==1)
							{
								agent.getLogger().info(agent.getComponentIdentifier()+": Updating to version: "+sdf.format(founddate));

								File	target	= decoms[0];
								UpdateInfo ui = new UpdateInfo(foundver, target.getCanonicalPath());
								
								// copy .settings.xml files from current directory (if any).
//								System.out.println("copy settings "+new File(".").getAbsolutePath());
								for(File settings: new File(".").listFiles(new FileFilter()
								{
									public boolean accept(File file)
									{
										boolean	ret	= !file.isDirectory()
											&& (file.getName().endsWith(".settings.xml") || file.getName().endsWith(".properties"));
//										System.out.println("copy "+ret+": "+file);
										return ret;
									}
								}))
								{
//									System.out.println("copying: "+settings+" to "+target);
									SUtil.copyFile(settings, target);
								}

								ret.setResult(ui);
							}
							else
							{
								agent.getLogger().warning(agent.getComponentIdentifier()+": Unexpectedly found not exactly one directory in decompressed distribution: "+SUtil.arrayToString(decoms));
								SUtil.deleteDirectory(dir);
								ret.setException(new RuntimeException("Unexpectedly found not exactly one directory in decompressed distribution: "+SUtil.arrayToString(decoms)));
							}
						}
						catch(Exception e)
						{
//							e.printStackTrace();
							agent.getLogger().warning(agent.getComponentIdentifier()+": Cannot update due to "+e);
							SUtil.deleteDirectory(dir);
							ret.setException(e);
						}
					}
				}
				
				if(!ret.isDone())
				{
					// no newer version found
//					System.out.println("No newer version found, latest is: "+sdf.format(foundver));
					ret.setResult(null);
				}
			}
		});
		
		return ret;
	}
	
	/**
	 *  Recursively scan dirs to find those with a jadex distribution contained.
	 *  Sorts the found entries according to their date.
	 */
	protected void findDistDirs(File dir, TreeSet<File> results)
	{
		if(dir.exists() && dir.isDirectory())
		{
			if(dir.getName().matches(excludedirs))
				return;
			
			File[] dists = dir.listFiles(new FilenameFilter()
			{
				public boolean accept(File dir, String name)
				{
					return name.toLowerCase().matches(includefiles);
				}
			});
			if(dists.length>0)
			{
				results.add(dir);
			}
			File[] subdirs = dir.listFiles(new FileFilter()
			{
				public boolean accept(File file)
				{
					return file.isDirectory();
				}
			});
			for(File subdir: subdirs)
			{
				findDistDirs(subdir, results);
			}
		}
	}
	
	/**
	 *  Get the last version (current or from last update check).
	 *  Uses the timestamp of a jadex jar in the used classpath.
	 */
	protected IFuture<Long> getLastVersion()
	{
		final Future<Long> ret = new Future<Long>();
		
		if(newestversion!=0)
		{
			ret.setResult(Long.valueOf(newestversion));
		}
		else if(newestversion!=-1)
		{
			IFuture<ILibraryService> fut = agent.getComponentFeature(IRequiredServicesFeature.class).getRequiredService("libservice");
			fut.addResultListener(new ExceptionDelegationResultListener<ILibraryService, Long>(ret)
			{
				public void customResultAvailable(ILibraryService libser)
				{
					IFuture<List<URL>> fut = libser.getAllURLs();
					fut.addResultListener(new ExceptionDelegationResultListener<List<URL>, Long>(ret)
					{
						public void customResultAvailable(List<URL> result)
						{
							agent.getLogger().info(agent.getComponentIdentifier()+": curversion urls "+result);
							// search for jadex jar file
							for(URL url: result)
							{
								File f = SUtil.getFile(url);
								if(f.exists() && f.getName().endsWith(".jar") && f.getName().indexOf("jadex")!=-1)
								{
									agent.getLogger().info(agent.getComponentIdentifier()+": curversion1 "+new Date(f.lastModified())+", "+f.getAbsolutePath());
									newestversion = f.lastModified();
									ret.setResult(Long.valueOf(newestversion));
									break;
								}
							}
							
							if(newestversion==0)
							{
								// search for jadex classes dir
								for(URL url: result)
								{
									File f = SUtil.getFile(url);
									if(f.exists() && f.isDirectory() && f.getAbsolutePath().indexOf("jadex")!=-1)
									{
										agent.getLogger().info(agent.getComponentIdentifier()+": curversion2 "+new Date(f.lastModified())+", "+f.getAbsolutePath());
										newestversion = f.lastModified();
										ret.setResult(Long.valueOf(newestversion));
										break;
									}
								}
								
								
								// remember that nothing was found
								if(newestversion==0)
								{
									newestversion = -1;
									agent.getLogger().warning(agent.getComponentIdentifier()+": Unable to determine current version.");
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
			agent.getLogger().warning(agent.getComponentIdentifier()+": Unable to determine current version.");
			ret.setException(new RuntimeException("Unable to determine current version."));
		}
	
		return ret;
	}

	/**
	 *  Convert the root dir to absolute to avoid nesting dist dirs in dist dirs.
	 */
	protected Map<String, Object> getUpdateArguments()
	{
		Map<String, Object>	ret	= super.getUpdateArguments();
		ret.put("rootdir", new File(rootdir).getAbsolutePath());
		return ret;
	}
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
//		String pat = "jadex-[0-9]+\\..*.zip";
//		String pat = "jadex-[[0-9]+\\.|.*[addon]].*.zip";
		String pat = "jadex-(([0-9]+\\.)|(.*addon)).*.zip";
//		String pat = "jadex-.*addon.*.zip";
		
		
		System.out.println("jadex-2.1.1-SNAPSHOT.zip".matches(pat));
		System.out.println("jadex-2.1.zip".matches(pat));
		System.out.println("jadex-3d-addon.zip".matches(pat));
		System.out.println("jadex-webservice-addon-2.1.1-SNAPSHOT.zip".matches(pat));
		System.out.println("apache-maven-3.0.4-bin.zip".matches(pat));
		System.out.println("jadex-example-project.zip".matches(pat));
	}
}
