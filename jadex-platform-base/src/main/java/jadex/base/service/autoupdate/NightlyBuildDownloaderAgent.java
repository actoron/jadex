package jadex.base.service.autoupdate;

import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Description;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Scanner;

/**
 *  Download latest jadex files from nightly builds page.
 */
@Agent
@Description("Download latest jadex files from nightly builds page.")
@Arguments({
	
	@Argument(description="The base server URL to start looking for subdirectories.",
		name="baseurl", clazz=String.class, defaultvalue="\"http://jadex.informatik.uni-hamburg.de\""),
	
	@Argument(description="The target directory for downloading changed files (defaults to current directory).",
		name="targetdir", clazz=String.class, defaultvalue="\".\""),
	
	@Argument(description="Regular expression to match against path names (defaults to any).",
		name="pattern", clazz=String.class, defaultvalue="\".*\""),
	
	@Argument(description="The update interval (defaults to 24h).",
		name="interval", clazz=long.class, defaultvalue=""+1000*60*60*24),

	@Argument(description="Disable printing of download notifications to console.",
		name="quiet", clazz=boolean.class, defaultvalue=""+false)
})
public class NightlyBuildDownloaderAgent
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
	
	//-------- methods --------
	
	/**
	 *  Execute the agent behavior.
	 */
	@AgentBody
	public void	body()
	{
		update();
		
		agent.waitForDelay(interval, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				update();
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Check for updates and download new files.
	 */
	protected void update()
	{
		try
		{
			// Find newest nightly-builds directory.
			String	dir	= null;
			URL	url	= new URL(baseurl+"/nightlybuilds/");
			HttpURLConnection	con	= (HttpURLConnection)url.openConnection();
			Scanner	s	= new Scanner(new BufferedInputStream(con.getInputStream()));
			while(s.findWithinHorizon("<a href=\"(.*?)\">", 0)!=null)
			{
				String	match	= s.match().group(1);
				if(dir==null || dir.compareTo(match)<0)
				{
					dir	= match;
				}
			}
			s.close();
			
			if(!quiet)
			{
				System.out.println("Downloading from: "+dir);
			}
			
			// Check for downloading all files from that directory.
			url	= new URL(baseurl+dir);
			con	= (HttpURLConnection)url.openConnection();
			s	= new Scanner(new BufferedInputStream(con.getInputStream()));
			while(s.findWithinHorizon("<a href=\"(.*?)\">", 0)!=null)
			{
				String	file	= s.match().group(1);
				if(file.startsWith(dir) && file.substring(dir.length()).matches(pattern))	// ignores back-links
				{
					URL	furl	= new URL(baseurl+file);
					HttpURLConnection	fcon	= (HttpURLConnection)furl.openConnection();
					File	targetfile	= new File(targetdir, file);
					if(!targetfile.exists() || fcon.getLastModified()>targetfile.lastModified())
					{
						if(!quiet)
						{
							System.out.println("Downloading new(er) file: "+file);
						}
						
						targetfile.getParentFile().mkdirs();
						long	length	= fcon.getContentLengthLong();
						long	done	= 0;
						InputStream	is	= fcon.getInputStream();
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
//									for(int i=0; i<pct.length(); i++)
//									{
//										System.out.print("\b");
//									}
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
					}
				}				
			}
			s.close();
		}
		catch(Exception e)
		{
			System.err.println("Warning: Download failed: "+ e);
		}
	}
}
