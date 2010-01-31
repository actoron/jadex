package jadex.xml;

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

	
	/**
	 * @param xmlpath
	 */
	public XMLInfo(String xmlpath)
	{
		this.xmlpath = xmlpath;
	}

	/**
	 * @param xmlpathelements
	 */
	public XMLInfo(QName[] xmlpathelements)
	{
		this.xmlpathelements = xmlpathelements;
	}

	/**
	 * @param xmlpath
	 * @param filter
	 */
	public XMLInfo(String xmlpath, IFilter filter)
	{
		this.xmlpath = xmlpath;
		this.filter = filter;
	}
	
	/**
	 * @param xmlpathelements
	 * @param filter
	 */
	public XMLInfo(QName[] xmlpathelements, IFilter filter)
	{
		this.xmlpathelements = xmlpathelements;
		this.filter = filter;
	}

	/**
	 * @param xmlpath
	 * @param filter
	 * @param createfromtag
	 */
	public XMLInfo(String xmlpath, IFilter filter, boolean createfromtag)
	{
		this.xmlpath = xmlpath;
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
		this.xmlpathelements = xmlpathelements;
		this.filter = filter;
		this.createfromtag = createfromtag;
	}

	/**
	 *  Get the xmlpath.
	 *  @return The xmlpath.
	 */
	public String getXmlPath()
	{
		return xmlpath;
	}

	/**
	 *  Set the xmlpath.
	 *  @param xmlpath The xmlpath to set.
	 * /
	public void setXmlPath(String xmlpath)
	{
		this.xmlpath = xmlpath;
	}*/

	/**
	 *  Get the xmlpathelements.
	 *  @return The xmlpathelements.
	 */
	public QName[] getXmlPathElements()
	{
		return xmlpathelements;
	}

	/**
	 *  Set the xmlpathelements.
	 *  @param xmlpathelements The xmlpathelements to set.
	 * /
	public void setXmlPathElements(QName[] xmlpathelements)
	{
		this.xmlpathelements = xmlpathelements;
	}*/

	/**
	 *  Get the filter.
	 *  @return The filter.
	 */
	public IFilter getFilter()
	{
		return filter;
	}

	/**
	 *  Set the filter.
	 *  @param filter The filter to set.
	 * /
	public void setFilter(IFilter filter)
	{
		this.filter = filter;
	}*/
	
	/**
	 *  Get the createfromtag.
	 *  @return The createfromtag.
	 */
	public boolean isCreateFromTag()
	{
		return createfromtag;
	}
}
