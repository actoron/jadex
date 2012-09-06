package jadex.platform.service.autoupdate;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.commons.HttpConnectionManager;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.AgentService;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

/**
 *  Download latest files from e.g. jadex nightly builds page.
 */
@Agent
@Description("Download latest files from e.g. jadex nightly builds page.")
@Arguments({
	
	@Argument(description="The base server URL to start looking for subdirectories.",
		name="baseurl", clazz=String.class, defaultvalue="\"http://jadex.informatik.uni-hamburg.de/nightlybuilds/\""),
	
	@Argument(description="The target directory for downloading changed files (defaults to current directory).",
		name="targetdir", clazz=String.class, defaultvalue="\".\""),
	
	@Argument(description="Regular expression to match against path names (defaults to any).",
		name="pattern", clazz=String.class, defaultvalue="\".*\""),
	
	@Argument(description="The update interval (defaults to 24h).",
		name="interval", clazz=long.class, defaultvalue=""+1000*60*60*24),

	@Argument(description="Disable printing of download notifications to console.",
		name="quiet", clazz=boolean.class, defaultvalue=""+false)
})
@RequiredServices(
	@RequiredService(name="tp", type=IDaemonThreadPoolService.class, binding=@Binding(scope=Binding.SCOPE_PLATFORM))
)
public class DirectoryDownloaderAgent
{
	//-------- attributes --------
	
	/** The agent. */
	@Agent
	protected IInternalAccess	agent;
	
	/** The base server URL to start looking for subdirectories. */
	@AgentArgument
	protected String	baseurl;
	
	/** The target directory for downloading changed files (defaults to current directory). */
	@AgentArgument
	protected String	targetdir;
	
	/** Regular expression to match against path names (defaults to any). */
	@AgentArgument
	protected String	pattern;
	
	/** The update interval (defaults to 24h). */
	@AgentArgument
	protected long	interval;
	
	/** Disable printing of download notifications to console. */
	@AgentArgument
	protected boolean	quiet;
	
	/** The thread pool for asynchronous download. */
	@AgentService
	protected IDaemonThreadPoolService	tp;
	
	/** The connection manager for open connections. */
	protected HttpConnectionManager	conman;
	
	//-------- methods --------
	
	/**
	 *  Start the agent.
	 */
	@AgentCreated
	public void	start()
	{
		conman	= new HttpConnectionManager();
	}
	
	/**
	 *  Terminate the agent.
	 */
	@AgentKilled
	public void	shutdown()
	{
		conman.dispose();
	}
	
	/**
	 *  Execute the agent behavior.
	 */
	@AgentBody
	public void	body()
	{
		final Future<Void>	updated = new Future<Void>();
		Runnable	run	= new Runnable()
		{
			public void run()
			{
				update().addResultListener(new DelegationResultListener<Void>(updated));
			}
		};
		
		tp.execute(run);
		
		updated.addResultListener(new DefaultResultListener<Void>(agent.getLogger())
		{
			public void resultAvailable(Void result)
			{
				agent.waitForDelay(interval, new IComponentStep<Void>()
				{
					public IFuture<Void> execute(IInternalAccess ia)
					{
						body();
						return IFuture.DONE;
					}
				});
			}
		});
	}
	
	/**
	 *  Check for updates and download new files.
	 */
	protected IFuture<Void>	update()
	{
		// Extract root (server) url.
		Scanner	s	= new Scanner(baseurl);
		s.findWithinHorizon("(.*?://.*?/).*", 0);
		final String	rooturl	= s.match().group(1);
		final String	relurl	= baseurl.substring(0, baseurl.lastIndexOf("/")+1);
		if(!quiet)
		{
			System.out.println("Root url is: "+rooturl);
			System.out.println("Relative url is: "+relurl);
		}
		s.close();

		try
		{
			// Find newest directory.
			String	dir	= null;
			File	localdir	= null;
			File	tmpdir	= null;
			HttpURLConnection	con	= conman.openConnection(baseurl);
			s	= new Scanner(new BufferedInputStream(con.getInputStream()));
			while(s.findWithinHorizon("<a href=\"(.*?)\">", 0)!=null)
			{
				String	match	= s.match().group(1);
				if(match.startsWith("/"))
				{
					match	= rooturl+match.substring(1);
				}
				else
				{
					match	= relurl+match;
				}
				
				if(match.endsWith("/") && (dir==null || dir.compareTo(match)<0))
				{
					dir	= match;
					String	ldir	= dir.substring(relurl.length(), dir.length()-1);
					localdir	= new File(targetdir, ldir);
					tmpdir	= new File(targetdir, "tmp_"+ldir);
				}
			}
			s.close();
			conman.remove(con);
			
			if(dir!=null)
			{
				
				if(!quiet)
				{
					System.out.println("Downloading from "+dir+" to "+tmpdir);
				}
				
				// Discover files from that directory.
				List<String>	files	= new ArrayList<String>();
				con	= conman.openConnection(dir);
				s	= new Scanner(new BufferedInputStream(con.getInputStream()));
				while(s.findWithinHorizon("<a href=\"(.*?)\">", 0)!=null)
				{
					String	file	= s.match().group(1);

					if(file.startsWith("/"))
					{
						file	= rooturl+file.substring(1);
					}
					else
					{
						file	= relurl+file;
					}

					if(file.startsWith(dir)	// ignores back links
						&& file.substring(dir.length()).matches(pattern))
					{
						files.add(file);
					}				
				}
				s.close();
				conman.remove(con);
				
				// Check for downloading discovered files.
				for(Iterator<String> it=files.iterator(); it.hasNext(); )
				{
					String file	= it.next();
					con	= conman.openConnection(file);
					String	localfile	= file.substring(dir.length());
					File	targetfile	= new File(localdir, localfile);
					if(!targetfile.exists() || con.getLastModified()>targetfile.lastModified())
					{
						if(!quiet)
						{
							System.out.println("Downloading new(er) file: "+file);
						}
						targetfile	= new File(tmpdir, localfile);
						targetfile.getParentFile().mkdirs();
						int	length	= con.getContentLength();
						long	done	= 0;
						InputStream	is	= con.getInputStream();
						OutputStream	os	= new FileOutputStream(targetfile);
						byte[]	buf	= new byte[8192];
						int read;
						String	pct	= "";
						long	lastprint	= 0;
						while((read=is.read(buf))!=-1)
						{
							os.write(buf, 0, read);
							if(!quiet && length>0)
							{
								done	+= read;
								String	newpct	= (1000*done/length)/10.0+" %";
								if(!pct.equals(newpct) && lastprint+1000<System.currentTimeMillis())
								{
									lastprint	= System.currentTimeMillis();
									// Doesn't work in eclipse, grrr: https://bugs.eclipse.org/bugs/show_bug.cgi?id=76936
	//								for(int i=0; i<pct.length(); i++)
	//								{
	//									System.out.print("\b");
	//								}
									pct	= newpct;
									System.out.println(pct);
								}
							}
						}
						if(!quiet)
						{
							System.out.println("100 %");
						}
						os.close();
						is.close();
					}
					else
					{
						System.out.println("Skipping up-to-date file: "+file);
						it.remove();
					}
					conman.remove(con);
				}
				
				// Change tmp dir to real dir.
				if(tmpdir.exists())
				{
					if(localdir.exists())
					{
						for(String file: files)
						{
							String	lfile	= file.substring(dir.length());
							File	localfile	= new File(localdir, lfile);
							File	tmpfile	= new File(tmpdir, lfile);
							if(!quiet)
							{
								System.out.println("Renaming "+tmpfile+" to "+localfile+".");
							}
							if(localfile.exists())
							{
								localfile.delete();
							}
							tmpfile.renameTo(localfile);
						}
						tmpdir.delete();
					}
					else
					{
						if(!quiet)
						{
							System.out.println("Renaming "+tmpdir+" to "+localdir+".");
						}
						tmpdir.renameTo(localdir);
					}
				}
			}
			else
			{
				if(!quiet)
				{
					System.out.println("No download dir found.");					
				}
			}
		}
		catch(Exception e)
		{
			if(!quiet)
			{
				System.out.println("Download stopped: "+ e.getMessage());
			}
		}
		
		return IFuture.DONE;
	}
}
