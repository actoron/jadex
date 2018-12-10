

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

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
			ret	= fetchVersionInfoFromProps(project, major, minor, 0, props);
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
}
