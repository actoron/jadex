package jadex.xwiki;

import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;
import java.util.TreeSet;

/**
 *  Struct for holding information about a build.
 */
public class NightlyBuild
{
	//-------- attributes --------
	
	/** The name of the build (e.g. jadex-xml). */
	protected String	name;
	
	/** The file name of the build (e.g. jadex-xml-2.0-rc3-dist.zip). */
	protected String	filename;
	
	/** The date of the build. */
	protected Date	date;
	
	/** The file size (in MB). */
	protected double	size;
	
	/** The download url of the build. */
	protected String	url;
	
	/** Additional (earlier) builds. */
	protected Set	previous;
	
	//-------- constructors --------
	
	/**
	 *  Create a new nightly build object.
	 */
	public NightlyBuild(String name, String filename, Date date, double size, String url)
	{
		this.name	= name;
		this.filename	= filename;
		this.date	= date;
		this.size	= size;
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
	 *  Get the filename of the build.
	 */
	public String	getFilename()
	{
		return filename;
	}
	
	/**
	 *  Get the date of the build.
	 */
	public Date	getDate()
	{
		return date;
	}
	
	/**
	 *  Get the size of the build.
	 */
	public double	getSize()
	{
		return size;
	}
	
	/**
	 *  Get the url of the build.
	 */
	public String	getUrl()
	{
		return url;
	}
	
	/**
	 *  Add a build.
	 *  Added builds are sorted, such that the root build is always the latest.
	 */
	public void	addBuild(NightlyBuild build)
	{
		if(!build.getName().equals(getName()))
			throw new IllegalArgumentException("Can only add matching builds: "+this.getName()+", "+build.getName());
		
		// Swap if build is newer.
		if(getDate().before(build.getDate()))
		{
			String	tmpfile	= filename;
			Date	tmpdate	= date;
			double	tmpsize	= size;
			String	tmpurl	= url;
			filename	= build.getFilename();
			date	= build.getDate();
			size	= build.getSize();
			url	= build.getUrl();
			build.filename	= tmpfile;
			build.date	= tmpdate;
			build.size	= tmpsize;
			build.url	= tmpurl;
		}
		
		if(previous==null)
		{
			previous	= new TreeSet(new BuildDateComparator());
		}
		
		previous.add(build);
	}
	
	/**
	 *  Get the previous builds (if any).
	 */
	public NightlyBuild[]	getPreviousBuilds()
	{
		return (NightlyBuild[])(previous!=null ? previous.toArray(new NightlyBuild[previous.size()]) : null);
	}
	
	/**
	 *  Get a string representation of this build.
	 */
	public String	toString()
	{
		StringBuffer	ret	= new StringBuffer();
		ret.append("NightlyBuild(");
		ret.append(name);
		ret.append(", ");
		ret.append(filename);
		ret.append(", ");
		ret.append(date);
		ret.append(", ");
		ret.append(size);
		ret.append(", ");
		ret.append(url);
		ret.append(")");
		
		if(previous!=null)
		{
			for(Iterator it=previous.iterator(); it.hasNext(); )
			{
				ret.append("\n\t");
				ret.append(it.next());
			}
		}
		
		return ret.toString();
	}
	
	//-------- helper classes --------
	
	/**
	 * Compare builds based on date.
	 */
	public static class BuildDateComparator implements Comparator
	{
		public int compare(Object obj1, Object obj2)
		{
			return ((NightlyBuild)obj2).getDate().compareTo(((NightlyBuild)obj1).getDate());
		}
	}

	/**
	 * Compare builds based on date.
	 */
	public static class BuildNameComparator implements Comparator
	{
		public int compare(Object obj1, Object obj2)
		{
			return ((NightlyBuild)obj1).getName().compareTo(((NightlyBuild)obj2).getName());
		}
	}
}
