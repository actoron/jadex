package jadex.xml;

import jadex.commons.IFilter;
import jadex.commons.SReflect;

import java.util.Comparator;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;


/**
 *  Superclass for XML object type/link infos.
 */
public class AbstractInfo
{
	//-------- attributes --------
	
	/** The xml tag/path. */
	protected String xmlpath;
	
	/** The xml path elements. */
	protected QName[] xmlpathelements;
	
	/** The xml path elements without tag. */
	protected QName[] xmlpathelementswithouttag;
	
	/** The info id. */
	protected int id;
	
	/** The id cnt. */
	protected static int idcnt;

	protected XMLInfo xmlinfo;

	
	//-------- constructors --------
	
	/**
	 *  Create an abstract OAV info.
	 */
	public AbstractInfo(XMLInfo xmlinfo)
	{
		this.xmlinfo = xmlinfo;
			
		if(xmlinfo!=null)
		{
			if(xmlinfo.getXmlPath()!=null)
			{
				this.xmlpath = xmlinfo.getXmlPath();
	
				StringTokenizer stok = new StringTokenizer(xmlpath, "/");
				this.xmlpathelements = new QName[stok.countTokens()];
				this.xmlpathelementswithouttag = new QName[stok.countTokens()-1];
				for(int i=0; stok.hasMoreTokens(); i++)
				{
					xmlpathelements[i] = QName.valueOf(stok.nextToken());//convertStringToQName(stok.nextToken());
					if(i<xmlpathelementswithouttag.length)
						xmlpathelementswithouttag[i] = xmlpathelements[i];
				}
			}
			else
			{
				// Only use local part
				StringBuffer buf = new StringBuffer();
				for(int i=0; i<xmlinfo.getXmlPathElements().length; i++)
				{
					if(i>0)
						buf.append("/");
					buf.append(xmlinfo.getXmlPathElements()[i].getLocalPart());
				}
				this.xmlpath = buf.toString();
				this.xmlpathelements = xmlinfo.getXmlPathElements();
				this.xmlpathelementswithouttag = new QName[xmlpathelements.length-1];
				System.arraycopy(xmlpathelements, 0, xmlpathelementswithouttag, 0, xmlpathelementswithouttag.length);
		
			}	
		}
		
		synchronized(AbstractInfo.class)
		{
			this.id = idcnt++;
		}
	}

	
	/**
	 *  Create an abstract OAV info.
	 * /
	public AbstractInfo(String xmlpath, IFilter filter)
	{
		this.xmlpath = xmlpath;
		if(xmlpath!=null)
		{
			StringTokenizer stok = new StringTokenizer(xmlpath, "/");
			this.xmlpathelements = new QName[stok.countTokens()];
			this.xmlpathelementswithouttag = new QName[stok.countTokens()-1];
			for(int i=0; stok.hasMoreTokens(); i++)
			{
				xmlpathelements[i] = QName.valueOf(stok.nextToken());//convertStringToQName(stok.nextToken());
				if(i<xmlpathelementswithouttag.length)
					xmlpathelementswithouttag[i] = xmlpathelements[i];
			}
		}
		
		this.filter = filter;
		synchronized(AbstractInfo.class)
		{
			this.id = idcnt++;
		}
	}*/
	
	/**
	 *  Create an abstract OAV info.
	 * /
	public AbstractInfo(QName[] xmlpath, IFilter filter)
	{
		if(xmlpath!=null)
		{
			// Only use local part
			StringBuffer buf = new StringBuffer();
			for(int i=0; i<xmlpath.length; i++)
			{
				if(i>0)
					buf.append("/");
				buf.append(xmlpath[i].getLocalPart());
			}
			this.xmlpath = buf.toString();
			this.xmlpathelements = xmlpath;
			this.xmlpathelementswithouttag = new QName[xmlpathelements.length-1];
			System.arraycopy(xmlpathelements, 0, xmlpathelementswithouttag, 0, xmlpathelementswithouttag.length);
		}
		
		this.filter = filter;
		synchronized(AbstractInfo.class)
		{
			this.id = idcnt++;
		}
	}*/
	
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
		return this.xmlpath;
	}

	/**
	 *  Get the xml tag
	 */
	public QName getXMLTag()
	{
		return xmlpathelements!=null? xmlpathelements[xmlpathelements.length-1]: null;
	}
	
	/**
	 *  Get the xmlpath as string array.
	 *  @return The xmlpath.
	 */
	public QName[] getXMLPathElements()
	{
		return xmlpathelements;
	}
	
	/**
	 *  Get the xml path without element.
	 */
	public QName[] getXMLPathElementsWithoutElement()
	{
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
		return xmlpathelements!=null? xmlpathelements.length-1: 0;
	}
	
	/**
	 *  Get the filter.
	 *  @return the filter
	 */
	public IFilter getFilter()
	{
		return xmlinfo.getFilter();
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
	 *  Convert a string to a qname.
	 *  @param s The string.
	 *  @return The qname.
	 * /
	public QName convertStringToQName(String s)
	{
		return QName.valueOf(s);
	}*/
	
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
	public static final class SpecificityComparator implements Comparator
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
				ret = m1.getFilter()!=null && m2.getFilter()==null? 1: 
					m1.getFilter()==null && m2.getFilter()!=null? -1: 
					m1.getFilter()!=null && m2.getFilter()!=null? m1.getId()-m2.getId()
					: 0;
			if(ret==0)
				throw new RuntimeException("Info should differ: "+m1+" "+m2);
			return ret;
		}
	}
}
