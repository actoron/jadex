package jadex.xwiki;

import java.util.Date;

/**
 *  Struct for holding information about a build.
 */
public class NightlyBuild
{
	//-------- attribibutes --------
	
	/** The file name of the build. */
	protected String	name;
	
	/** The date of the build. */
	protected Date	date;
	
	/** The download url of the build. */
	protected String	url;
	
	//-------- constructors --------
	
	/**
	 *  Create a new nightly build object.
	 */
	public NightlyBuild(String name, Date date, String url)
	{
		this.name	= name;
		this.date	= date;
		this.url	= url;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the name of the build.
	 */
	public String	getName()
	{
		return name;
	}
	
	/**
	 *  Get the date of the build.
	 */
	public Date	getDate()
	{
		return date;
	}
	
	/**
	 *  Get the url of the build.
	 */
	public String	getUrl()
	{
		return url;
	}
	
	/**
	 *  Get a string representation of this build.
	 */
	public String	toString()
	{
		return "NightlyBuild("+name+", "+date+", "+url+")";
	}
}
