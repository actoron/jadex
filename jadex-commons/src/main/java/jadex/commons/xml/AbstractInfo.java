package jadex.commons.xml;

import jadex.commons.IFilter;
import jadex.commons.SReflect;
import jadex.commons.SUtil;

import java.util.Comparator;


/**
 *  Superclass for XML object type/link infos.
 */
public class AbstractInfo
{
	//-------- constants --------
	
	/** The content xml attribute constant. */
	public static String XML_CONTENT_ATTRIBUTE = "__XML_CONTENT";
	
	//-------- attributes --------
	
	/** The xml tag/path. */
	protected String xmlpath;
	
	/** The xml tag. */
	protected String xmltag;
	
	/** The xml path depth. */
	protected int xmlpathdepth;
	
	/** The procedural filter. */
	protected IFilter filter;
	
	/** The info id. */
	protected int id;
	
	/** The id cnt. */
	protected static int idcnt;
	
	//-------- constructors --------
	
	/**
	 *  Create an abstract OAV info.
	 */
	public AbstractInfo(String xmlpath, IFilter filter)
	{
		this.xmlpath = xmlpath;
		this.filter = filter;
		synchronized(AbstractInfo.class)
		{
			this.id = idcnt++;
		}
	}
	
	//-------- methods --------

	/**
	 *  Get the xmlpath
	 */
	public String getXMLPath()
	{
		return this.xmlpath;
	}
	
	/**
	 *  Set the xmlpath.
	 */
	public void setXMLPath(String xmlpath)
	{
		this.xmlpath = xmlpath;
	}
	
	/**
	 *  Get the xml tag
	 */
	public String getXMLTag()
	{
		if(xmltag==null)
		{
			int idx = xmlpath.lastIndexOf("/");
			if(idx!=-1)
				xmltag = xmlpath.substring(idx+1);
			else
				xmltag = xmlpath;
		}
		
		return xmltag;
	}
	
	/**
	 *  Get the xml path without element.
	 */
	public String getXMLPathWithoutElement()
	{
		String ret = "";
		String xmlpath = getXMLPath();
		int idx = xmlpath.lastIndexOf("/");
		if(idx!=-1)
			ret = xmlpath.substring(0, idx-1);
		return ret;
	}
	
	/**
	 *  Get the path depth.
	 */
	public int getXMLPathDepth()
	{
		if(xmlpathdepth==0)
		{
			int idx = xmlpath.indexOf("/");
			while(idx!=-1)
			{
				xmlpathdepth++;
				idx = xmlpath.indexOf("/", idx+1);
			}
		}
		
		return xmlpathdepth;
	}
	
	/**
	 *  Get the filter.
	 *  @return the filter
	 */
	public IFilter getFilter()
	{
		return this.filter;
	}

	/**
	 *  Set the filter.
	 *  @param filter The filter to set.
	 */
	public void setFilter(IFilter filter)
	{
		this.filter = filter;
	}
	
	/**
	 *  Get the id.
	 *  @return The id.
	 */
	public int getId()
	{
		return this.id;
	}

	/**
	 *  Get a string representation of this mapping.
	 */
	public String	toString()
	{
		StringBuffer	sbuf	= new StringBuffer();
		sbuf.append(SReflect.getInnerClassName(getClass()));
		sbuf.append("(path=");
		sbuf.append(xmlpath);
		sbuf.append(")");
		return sbuf.toString();
	}

	//-------- helper classes --------
	
	
	/**
	 *  Compare infos by specificity of the XML path.
	 */
	static final class SpecificityComparator implements Comparator
	{
		public int compare(Object arg0, Object arg1)
		{
			AbstractInfo m1 = (AbstractInfo)arg0;
			AbstractInfo m2 = (AbstractInfo)arg1;
			int ret = m2.getXMLPathDepth() - m1.getXMLPathDepth();
			if(ret==0)
				ret = m2.getXMLPath().length() - m1.getXMLPath().length();
			if(ret==0)
				ret = m2.getXMLPath().compareTo(m1.getXMLPath());
			if(ret==0)
				ret = m1.filter!=null && m2.filter==null? 1: 
					m1.filter==null && m2.filter!=null? -1: 
					m1.filter!=null && m2.filter!=null? m1.getId()-m2.getId()
					: 0;
			if(ret==0)
				throw new RuntimeException("Info should differ: "+m1+" "+m2);
			return ret;
		}
	}
}
