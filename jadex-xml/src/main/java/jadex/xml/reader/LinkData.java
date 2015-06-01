package jadex.xml.reader;

import java.util.Arrays;

import jadex.xml.stax.QName;

/**
 *  Data for linking two objects.
 */
public class LinkData
{
	//-------- attributes --------
	
	/** The child object. */
	protected Object child;
	
	/** The linkinfo. */
	protected Object linkinfo;
	
	/** The pathname. */
	protected QName[] pathname;
	
	//-------- constructors --------
	
	/**
	 *  Create a new link data.
	 */
	public LinkData(Object child, Object linkinfo, QName[] pathname)
	{
		this.child = child;
		this.linkinfo = linkinfo;
		this.pathname = pathname;
	}

	//-------- methods --------
	
	/**
	 *  Get the child.
	 *  @return The child.
	 */
	public Object getChild()
	{
		return this.child;
	}

	/**
	 *  Get the linkinfo.
	 *  @return The linkinfo.
	 */
	public Object getLinkinfo()
	{
		return this.linkinfo;
	}

	/**
	 *  Get the pathname.
	 *  @return The pathname.
	 */
	public QName[] getPathname()
	{
		return this.pathname;
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "LinkData(child=" + this.child + ", linkinfo=" + this.linkinfo
			+ ", pathname=" + Arrays.toString(this.pathname) + ")";
	}
}
