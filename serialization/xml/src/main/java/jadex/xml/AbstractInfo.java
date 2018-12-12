package jadex.xml;

import java.util.Comparator;

import jadex.commons.IFilter;
import jadex.commons.SReflect;
import jadex.xml.stax.QName;

/**
 *  Superclass for XML object type/link infos.
 */
public class AbstractInfo
{
	//-------- attributes --------
	
	protected XMLInfo xmlinfo;
	
	/** The xml path elements without tag. */
	protected QName[] xmlpathelementswithouttag;
	
	/** The info id. */
	protected int id;
	
	/** The id cnt. */
	protected static int idcnt;

	//-------- constructors --------
	
	/**
	 *  Create an abstract OAV info.
	 */
	public AbstractInfo(XMLInfo xmlinfo)
	{
		this.xmlinfo = xmlinfo;
			
		synchronized(AbstractInfo.class)
		{
			this.id = idcnt++;
		}
	}

	//-------- methods --------

	/**
	 *  Get the xmlinfo.
	 *  @return The xmlinfo.
	 */
	public XMLInfo getXMLInfo()
	{
		return xmlinfo;
	}
	
	/**
	 *  Get the xmlpath
	 */
	public String getXMLPath()
	{
		return xmlinfo!=null? xmlinfo.getXMLPath(): null;
	}

	/**
	 *  Get the xml tag
	 */
	public QName getXMLTag()
	{
		return xmlinfo!=null && xmlinfo.getXMLPathElements()!=null? xmlinfo.getXMLPathElements()[xmlinfo.getXMLPathElements().length-1]: null;
	}
	
	/**
	 *  Get the xmlpath as string array.
	 *  @return The xmlpath.
	 */
	public QName[] getXMLPathElements()
	{
		return xmlinfo!=null && xmlinfo.getXMLPathElements()!=null? xmlinfo.getXMLPathElements(): null;
	}
	
	/**
	 *  Get the xml path without element.
	 */
	public QName[] getXMLPathElementsWithoutElement()
	{
		if(xmlpathelementswithouttag==null && xmlinfo!=null)
		{
			xmlpathelementswithouttag = new QName[xmlinfo.getXMLPathElements().length-1];
			System.arraycopy(xmlinfo.getXMLPathElements(), 0, xmlpathelementswithouttag, 0, xmlinfo.getXMLPathElements().length-1);
		}
		return xmlpathelementswithouttag;
	}
	
	/**
	 *  Get the xml path without element.
	 * /
	public String getXMLPathWithoutElement()
	{
		String ret = "";
		String xmlpath = getXMLPath();
		if(xmlpath!=null)
		{
			int idx = xmlpath.lastIndexOf("/");
			if(idx!=-1)
				ret = xmlpath.substring(0, idx-1);
		}
		return ret;
	}*/
	
	/**
	 *  Get the path depth.
	 */
	public int getXMLPathDepth()
	{
		return xmlinfo!=null && xmlinfo.getXMLPathElements()!=null? xmlinfo.getXMLPathElements().length-1: 0;
	}
	
	/**
	 *  Get the filter.
	 *  @return the filter
	 */
	public IFilter<Object> getFilter()
	{
		return xmlinfo!=null ? xmlinfo.getFilter() : null;
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
		if(xmlinfo!=null && xmlinfo.getXMLPath()!=null)
		{
			sbuf.append("(path=");
			sbuf.append(xmlinfo.getXMLPath());
		}
		sbuf.append(")");
		return sbuf.toString();
	}

	//-------- helper classes --------
	
	/**
	 *  Compare infos by specificity of the XML path.
	 */
	public static final class SpecificityComparator implements Comparator<AbstractInfo>
	{
		public int compare(AbstractInfo arg0, AbstractInfo arg1)
		{
			int	ret	= 0;
			if(arg0!=arg1)
			{
				AbstractInfo m1 = (AbstractInfo)arg0;
				AbstractInfo m2 = (AbstractInfo)arg1;
				ret = m2.getXMLPathDepth() - m1.getXMLPathDepth();
				if(ret==0)
					ret = m1.getXMLPath()!=null && m2.getXMLPath()==null? 1:
						m1.getXMLPath()==null && m2.getXMLPath()!=null? -1:
						m1.getXMLPath()!=null && m2.getXMLPath()!=null? 
						m2.getXMLPath().length() - m1.getXMLPath().length()!=0?
						m2.getXMLPath().length() - m1.getXMLPath().length():
						m2.getXMLPath().compareTo(m1.getXMLPath()): 0;
				if(ret==0)
					ret = m1.getFilter()!=null && m2.getFilter()==null? 1: 
						m1.getFilter()==null && m2.getFilter()!=null? -1: 
						m1.getFilter()!=null && m2.getFilter()!=null? m2.getId()-m1.getId()
						: 0;
				if(ret==0)
					throw new RuntimeException("Info should differ: "+m1+" "+m2);
			}
			return ret;
		}
	}
}
