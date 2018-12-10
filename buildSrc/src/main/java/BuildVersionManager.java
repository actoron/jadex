

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;

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
 *  Helper class for automatic fetching of build version info
 *  from local git repo/file system.
 *  
 *  See gitversion.md for details.
 */
public class BuildVersionManager 
{
	//-------- constants --------
	
	/** Path to properties file with major and minor version. */
	public static String	PROPS_PATH	= "src/main/buildutils/jadexversion.properties";
	
	/** Prefix of properties for major, minor, patch etc. */
	public static String	PROPS_PREFIX	= "jadexversion_";
	
	/** The date formatter for time stamps. */
	public static SimpleDateFormat TIMESTAMP_FORMAT	= new SimpleDateFormat("yyyyMMddhhmmss");
	
	//-------- methods --------
	
	/**
	 *  Fetch version information from local state.
	 *  See gitversion.md for details.
	 */
	public static BuildVersionInfo	fetchVersionInfo(Project project)
	{
		BuildVersionInfo	ret;
		
		// Fetch major and minor from file
		File	pfile	= new File(project.getProjectDir(), PROPS_PATH);
		Properties	props	= new Properties();
		try(InputStream is= new FileInputStream(pfile))
		{
			props.load(is);
		}
		catch(Exception e)
		{
			throw new BuildException("Failed to read "+pfile, e);
		}
		int major	= Integer.parseInt(props.getProperty(PROPS_PREFIX+"major"));
		int minor	= Integer.parseInt(props.getProperty(PROPS_PREFIX+"minor"));
		try
		{
			// If patch is included in properties -> build from dist sources -> use values from prop.
			int patch	= Integer.parseInt(props.getProperty(PROPS_PREFIX+"patch"));
			ret	= fetchVersionInfoFromProps(project, major, minor, patch, props);
		}
		catch(NumberFormatException e)
		{
			// Patch not included -> try to read local git repo.
			try(Repository repository = new FileRepositoryBuilder()
				.setGitDir(new File(project.getProjectDir(), ".git"))
				.readEnvironment() // scan environment GIT_* variables
				.build())
			{
				ret	= fetchVersionInfoFromRepo(project, major, minor, repository);
			}
			catch(Exception e2)
			{
				// Not in git repo -> use major.minor.9999-SNAPSHOT and current time. (branch is unknown)
				e2.printStackTrace();
				ret	= new BuildVersionInfo(major, minor, 9999, null, new SimpleDateFormat("yyyyMMddhhmmss").format(new Date()), true);
			}
		}
		
		return ret;
	}

	//-------- internal methods --------
	
	/**
	 *  Fetch version info for build from dist sources.
	 */
	protected static BuildVersionInfo fetchVersionInfoFromProps(Project project, int major, int minor, int patch, Properties props)
	{
		String	branch	= props.getProperty(PROPS_PREFIX+"branch");
		String	timestamp	= props.getProperty(PROPS_PREFIX+"timestamp");
		
		// Increment patch and append '-SNAPSHOT' unless dirty=false was set.
		Object	pdirty	= project.getProperties().get("dirty");
		boolean	dirty	= pdirty==null || !pdirty.equals("false");
		if(dirty)
		{
			patch++;
		}
		
		return new BuildVersionInfo(major, minor, patch, branch, timestamp, dirty);
	}

	/**
	 *  Fetch version info for build from git repo.
	 *  @throws IOException in case of errors. 
	 */
	protected static BuildVersionInfo fetchVersionInfoFromRepo(Project project, int major, int minor, Repository repository) throws Exception
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
		
		return new BuildVersionInfo(major, minor, patch, branch, timestamp, dirty);
	}
}
