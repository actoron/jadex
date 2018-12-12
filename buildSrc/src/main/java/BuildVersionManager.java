import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.time.Instant;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
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
 *  Helper class for automatic fetching of build version info from local git repo/file system.
 *  
 *  See gitversion.md for details.
 */
public class BuildVersionManager 
{
	//-------- constants --------
	
	/** Prefix of properties for major and minor as set by user. */
	public static final String	SOURCE_PROPS_PREFIX	= "jadexversion_";
	
	/** Prefix of properties for other properties set during build. */
	public static final String	BUILD_PROPS_PREFIX	= BuildVersionInfo.PROPS_PREFIX;
	
	/** The date formatter for time stamps. */
	// No simple date formatter as we need locale independent values, grrr.
	public static final DateTimeFormatter TIMESTAMP_FORMATTER = new DateTimeFormatterBuilder()
		.appendValue(ChronoField.YEAR, 4)
		.appendValue(ChronoField.MONTH_OF_YEAR, 2)
		.appendValue(ChronoField.DAY_OF_MONTH, 2)
		.appendValue(ChronoField.HOUR_OF_DAY, 2)
		.appendValue(ChronoField.MINUTE_OF_HOUR, 2)
		.appendValue(ChronoField.SECOND_OF_MINUTE, 2)
	    .toFormatter().withZone(ZoneOffset.UTC);
	
	//-------- methods --------

	/**
	 *  Fetch version information from local state.
	 *  See gitversion.md for details.
	 *  @param project	The gradle root project
	 *  @param path	The path to the properties file relative to the project directory.
	 *  @param forcedirty	True, if dirty work dir should be assumed when not in git repo tree.
	 */
	public static BuildVersionInfo	fetchVersionInfo(Project project, String path, boolean forcedirty)
	{
		BuildVersionInfo	ret;
		
		// Fetch major and minor from file
		File	pfile	= new File(project.getProjectDir(), path);
		Properties	props	= new Properties();
		try(InputStream is= new FileInputStream(pfile))
		{
			props.load(is);
			System.out.println("Loaded version info from: "+pfile.getCanonicalPath());
			props.store(new java.io.PrintWriter(System.out), null);
		}
		catch(Exception e)
		{
			throw new BuildException("Failed to read "+pfile, e);
		}
		
		try
		{
			// Try if build info is included in properties, i.e. when built from dist sources -> use values from prop.
			ret	= fetchVersionInfoFromProps(project, props, forcedirty);
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
				ret	= fetchVersionInfoFromRepo(project, major, minor, repository);
			}
			catch(Exception e2)
			{
				// Not in git repo -> use major.minor.9999-SNAPSHOT and current time. (branch is unknown)
				e2.printStackTrace();
				ret	= new BuildVersionInfo(major, minor, 9999, null, TIMESTAMP_FORMATTER.format(Instant.now()), null, true);
			}
		}
		
		return ret;
	}

	//-------- internal methods --------
	
	/**
	 *  Fetch version info for build from dist sources.
	 *  Fails if props can not be parsed (i.e. on unprocessed source props).
	 */
	protected static BuildVersionInfo fetchVersionInfoFromProps(Project project, Properties props, boolean forcedirty)
	{
		// Always create new timestamp unless -PnoForceDirty=true was set (e.g. for checkDist).
    	if(forcedirty)
    	{
    		boolean snapshot	= Boolean.parseBoolean(props.getProperty(BUILD_PROPS_PREFIX+"snapshot"));
    		// Increment patch when initially not snapshot
    		if(!snapshot)
    		{
    			int patch	= Integer.parseInt(props.getProperty(BUILD_PROPS_PREFIX+"patch"));
        		props.setProperty(BUILD_PROPS_PREFIX+"patch", ""+(patch+1));
        		props.setProperty(BUILD_PROPS_PREFIX+"snapshot", "true");
    		}
        	props.setProperty(BUILD_PROPS_PREFIX+"timestamp", TIMESTAMP_FORMATTER.format(Instant.now()));
    	}
		
		return BuildVersionInfo.fromProperties(props);
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
                timestamp	= TIMESTAMP_FORMATTER.format(Instant.now());	// Timestamp for reference in version properties, not part of version string.
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
		                timestamp	= TIMESTAMP_FORMATTER.format(Instant.ofEpochSecond(commit.getCommitTime()));
		                walk.dispose();
		            }
				}
			}
		}
		
		return new BuildVersionInfo(major, minor, patch, branch, timestamp, latest.getName(), dirty);
	}
}
