import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.TimeZone;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.Ref;
import org.eclipse.jgit.lib.Repository;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.gradle.api.Project;
import org.gradle.tooling.BuildException;

/**
 *  Helper class for automatic fetching of build version info from local git repo/file system.
 *  
 *  See gitversion.md for details.
 */
public class BuildVersionManager 
{
	//-------- constants --------
	
	/** Path to previously generated properties file with major and minor version (use this if present, removed by 'gradlew clean' or 'gradlew cleanCreateVersionInfo'). */
	public static final String	BUILD_PROPS_PATH	= "build/jadexversion.properties";
	
	/** Path to source tree properties file with major and minor version (use if not yet generated). */
	public static final String	SOURCE_PROPS_PATH	= "src/main/buildutils/jadexversion.properties";
	
	/** Prefix of properties for major and minor as set by user. */
	public static final String	SOURCE_PROPS_PREFIX	= "jadexversion_";
	
	/** Prefix of properties for other properties set during build. */
	public static final String	BUILD_PROPS_PREFIX	= BuildVersionInfo.PROPS_PREFIX;
	
	/** The date formatter for time stamps. */
	public static final SimpleDateFormat TIMESTAMP_FORMAT	= new SimpleDateFormat("yyyyMMddHHmmss");
	{
		TIMESTAMP_FORMAT.setTimeZone(TimeZone.getTimeZone("UTC"));
	}
	
	//-------- attributes --------
	
	/** The (root) project. */
	protected Project	project;
	
	/** The build version info, read lazily to allow clean task to execute first. */
	protected BuildVersionInfo	info;
	
	//-------- constructors --------
	
	/**
	 *  Create a build version manager for the given project.
	 */
	public BuildVersionManager(Project project)
	{
		this.project	= project;
	}
	
	//-------- methods --------

	/**
	 *  Manager is used as version object so we generate a nice version string on access.
	 */
	@Override
	public String toString()
	{
		return getInfo().toString();
	}
	
	/**
	 *  The version info object.
	 */
	public synchronized BuildVersionInfo	getInfo()
	{
		Thread.dumpStack();
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.exit(0);
		if(info==null)
		{
			info	= fetchVersionInfo();
			System.out.println("Fetched version info: "+info);
		}
		return info;
	}
	
	//-------- helper methods --------
	
	/**
	 *  Fetch version information from local state.
	 *  See gitversion.md for details.
	 */
	protected BuildVersionInfo	fetchVersionInfo()
	{
		BuildVersionInfo	ret;
		
		// Fetch major and minor from file
		File	pfile	= new File(project.getProjectDir(), BUILD_PROPS_PATH);
		if(!pfile.exists())
			pfile	= new File(project.getProjectDir(), SOURCE_PROPS_PATH);
		Properties	props	= new Properties();
		try(InputStream is= new FileInputStream(pfile))
		{
			props.load(is);
			System.out.println("Loaded version info from: "+pfile.getCanonicalPath());
			props.store(new PrintWriter(System.out), null);
		}
		catch(Exception e)
		{
			throw new BuildException("Failed to read "+pfile, e);
		}
		
		try
		{
			// Try if build info is included in properties, i.e. when built from dist sources -> use values from prop.
			ret	= fetchVersionInfoFromProps(props);
		}
		catch(Exception e)
		{
//			e.printStackTrace();
			// No build info included -> try to read local git repo.
			int major	= Integer.parseInt(props.getProperty(SOURCE_PROPS_PREFIX+"major"));
			int minor	= Integer.parseInt(props.getProperty(SOURCE_PROPS_PREFIX+"minor"));
			
			try(Repository repository = new FileRepositoryBuilder()
				.setGitDir(new File(project.getProjectDir(), ".git"))
				.readEnvironment() // scan environment GIT_* variables
				.build())
			{
				ret	= fetchVersionInfoFromRepo(major, minor, repository);
			}
			catch(Exception e2)
			{
				// Not in git repo -> use major.minor.9999-SNAPSHOT and current time. (branch is unknown)
//				e2.printStackTrace();
				ret	= new BuildVersionInfo(major, minor, 9999, null, TIMESTAMP_FORMAT.format(new Date()), null, true);
			}
		}
		
		return ret;
	}

	/**
	 *  Fetch version info for build from dist sources.
	 */
	protected BuildVersionInfo fetchVersionInfoFromProps(Properties props)
	{
		// Increment patch and append '-SNAPSHOT' unless dirty=false was set.
		Object	pdirty	= project.getProperties().get("dirty");
		boolean	dirty	= pdirty==null || !pdirty.equals("false");
		return BuildVersionInfo.fromProperties(props, dirty);
	}

	/**
	 *  Fetch version info for build from git repo.
	 *  @throws IOException in case of errors. 
	 */
	protected BuildVersionInfo fetchVersionInfoFromRepo(int major, int minor, Repository repository) throws Exception
	{
		boolean dirty	= !Git.wrap(repository).status().call().isClean();
		String branch	= repository.getBranch();
		
//		String	tags	= git.describe().setMatch(major+"."+minor+".*").abbrev(0).call();	// --abbrev=0 not supported :(, cf. https://bugs.eclipse.org/bugs/show_bug.cgi?id=537883
		String	prefix	= "refs/tags/"+major+"."+minor+".";
		int	patch	= 0;
		ObjectId	latest	= null;
		for(Ref ref: repository.getRefDatabase().getRefsByPrefix(prefix))
		{
			String	spatch	= ref.getName().substring(prefix.length());
			try
			{
				int	tagpatch	= Integer.parseInt(spatch);
				if(tagpatch>=patch)
				{
					latest	= ref.getObjectId();
					patch	= tagpatch;
				}
			}
			catch(NumberFormatException nfe)
			{
//				System.out.println("Ignoring tag: "+spatch);
			}
		}
		
		// Increment patch when dirty or latest tag not at head
		String	timestamp	= null;
		if(latest!=null)
		{
			if(dirty)
			{
				patch++;
                timestamp	= TIMESTAMP_FORMAT.format(new Date());	// Timestamp for reference in version properties, not part of version string.
			}
			else
			{
				// Clean workdir -> when not at head then increment patch and add timestamp from latest commit
				ObjectId head = repository.resolve("HEAD");
				if(!latest.equals(head))
				{
					patch++;
					latest	= head;
		            try (RevWalk walk = new RevWalk(repository))
		            {
		                RevCommit commit = walk.parseCommit(head);
		                timestamp	= TIMESTAMP_FORMAT.format(new Date(1000L*commit.getCommitTime()));	// Unix timestamp w/o milliseconds
		                walk.dispose();
		            }
				}
			}
		}
		
		return new BuildVersionInfo(major, minor, patch, branch, timestamp, latest.getName(), dirty);
	}
}
