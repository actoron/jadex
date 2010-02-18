package jadex.xml;

import java.util.StringTokenizer;

import javax.xml.namespace.QName;

import jadex.commons.IFilter;

/**
 * 
 */
public class XMLInfo
{
	/** The xml tag/path. */
	protected String xmlpath;
	
	/** The xml path elements. */
	protected QName[] xmlpathelements;

	/** The procedural filter. */
	protected IFilter filter;
	
	/** Create from tag flag. */
	protected boolean createfromtag;

	/** The preprocessor. */
//	protected IPreProcessor preprocessor;
	
	/**
	 * @param xmlpath
	 */
	public XMLInfo(String xmlpath)
	{
		this(xmlpath, null);
	}

	/**
	 * @param xmlpathelements
	 */
	public XMLInfo(QName[] xmlpathelements)
	{
		this(xmlpathelements, null);
	}

	/**
	 * @param xmlpath
	 * @param filter
	 */
	public XMLInfo(String xmlpath, IFilter filter)
	{
		this(xmlpath, filter, false);
	}
	
	/**
	 * @param xmlpathelements
	 * @param filter
	 */
	public XMLInfo(QName[] xmlpathelements, IFilter filter)
	{
		this(xmlpathelements, filter, false);
	}

	/**
	 * @param xmlpath
	 * @param filter
	 * @param createfromtag
	 */
	public XMLInfo(String xmlpath, IFilter filter, boolean createfromtag)
	{
		setXMLPath(xmlpath);
		this.filter = filter;
		this.createfromtag = createfromtag;
	}

	/**
	 * @param xmlpathelements
	 * @param filter
	 * @param createfromtag
	 */
	public XMLInfo(QName[] xmlpathelements, IFilter filter, boolean createfromtag)
	{
		setXMLPathElements(xmlpathelements);
		this.filter = filter;
		this.createfromtag = createfromtag;
	}

	/**
	 *  Get the xmlpath.
	 *  @return The xmlpath.
	 */
	public String getXMLPath()
	{
		return xmlpath;
	}

	/**
	 *  Set the xmlpath.
	 *  @param xmlpath The xmlpath to set.
	 */
	protected void setXMLPath(String xmlpath)
	{
		this.xmlpath = xmlpath;
		
		StringTokenizer stok = new StringTokenizer(xmlpath, "/");
		this.xmlpathelements = new QName[stok.countTokens()];
		for(int i=0; stok.hasMoreTokens(); i++)
		{
			xmlpathelements[i] = QName.valueOf(stok.nextToken());
		}
	}

	/**
	 *  Get the xmlpathelements.
	 *  @return The xmlpathelements.
	 */
	public QName[] getXMLPathElements()
	{
		return xmlpathelements;
	}
	
	/**
	 *  Set the xmlpathelements.
	 *  @param xmlpathelements The xmlpathelements to set.
	 */
	protected void setXMLPathElements(QName[] xmlpathelements)
	{
		this.xmlpathelements = xmlpathelements;
		
		// Only use local part
		StringBuffer buf = new StringBuffer();
		for(int i=0; i<xmlpathelements.length; i++)
		{
			if(i>0)
				buf.append("/");
			buf.append(xmlpathelements[i].getLocalPart());
		}
		this.xmlpath = buf.toString();
	}

	/**
	 *  Get the filter.
	 *  @return The filter.
	 */
	public IFilter getFilter()
	{
		return filter;
	}

	/**
	 *  Get the createfromtag.
	 *  @return The createfromtag.
	 */
	public boolean isCreateFromTag()
	{
		return createfromtag;
	}
}
