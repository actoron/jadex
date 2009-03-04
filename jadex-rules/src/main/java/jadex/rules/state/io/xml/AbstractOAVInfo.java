package jadex.rules.state.io.xml;

import jadex.commons.SReflect;

import java.util.Comparator;


/**
 *  Superclass for XML OAV mapping/link infos.
 */
public class AbstractOAVInfo
{
	//-------- attributes --------
	
	/** The xml tag/path. */
	protected String xmlpath;
	
	/** The xml tag. */
	protected String xmltag;
	
	/** The xml path depth. */
	protected int xmlpathdepth;
	
	//-------- constructors --------
	
	/**
	 *  Create an abstract OAV info.
	 */
	public AbstractOAVInfo(String xmlpath)
	{
		this.xmlpath = xmlpath;
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
	 *  Set the xmlpath.
	 */
	public void setXMLPath(String xmlpath)
	{
		this.xmlpath = xmlpath;
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
			AbstractOAVInfo m1 = (AbstractOAVInfo)arg0;
			AbstractOAVInfo m2 = (AbstractOAVInfo)arg1;
			int ret = m2.getXMLPathDepth() - m1.getXMLPathDepth();
			if(ret==0)
				ret = m2.getXMLPath().length() - m1.getXMLPath().length();
			if(ret==0)
				ret = m2.getXMLPath().compareTo(m1.getXMLPath());
			if(ret==0)
				throw new RuntimeException("Info should differ: "+m1+" "+m2);
			return ret;
		}
	}
}
