package jadex.xml;

import java.util.StringTokenizer;

import jadex.commons.IFilter;
import jadex.xml.stax.QName;

/**
 *  Info for an xml element, i.e. identifying tag(path), filter etc. 
 */
public class XMLInfo
{
	//-------- attributes --------

	/** The xml tag/path. */
	protected String xmlpath;
	
	/** The xml path elements. */
	protected QName[] xmlpathelements;

	/** The procedural filter. */
	protected IFilter filter;
	
	/** Create from tag flag. */
	protected boolean createfromtag;

	/** The preprocessor. */
	protected IPreProcessor preprocessor;
	
	//-------- constructors --------
	
	/**
	 *  Create a new xml info.
	 */
	public XMLInfo(String xmlpath)
	{
		this(xmlpath, (IFilter)null);
	}
	
	/**
	 *  Create a new xml info.
	 */
	public XMLInfo(QName xmltag)
	{
		this(new QName[]{xmltag}, (IFilter)null);
	}
	
	/**
	 *  Create a new xml info.
	 */
	public XMLInfo(QName[] xmlpathelements)
	{
		this(xmlpathelements, (IFilter)null);
	}

	/**
	 *  Create a new xml info.
	 */
	public XMLInfo(String xmlpath, IFilter filter)
	{
		this(xmlpath, filter, false);
	}
	
	/**
	 *  Create a new xml info.
	 */
	public XMLInfo(QName xmltag, IFilter filter)
	{
		this(new QName[]{xmltag}, filter, false);
	}
	
	/**
	 *  Create a new xml info.
	 */
	public XMLInfo(QName[] xmlpathelements, IFilter filter)
	{
		this(xmlpathelements, filter, false);
	}
	
	/**
	 *  Create a new xml info.
	 */
	public XMLInfo(String xmlpath, IPreProcessor preprocessor)
	{
		this(xmlpath, null, false, preprocessor);
	}
	
	/**
	 *  Create a new xml info.
	 */
	public XMLInfo(QName[] xmlpathelements, IPreProcessor preprocessor)
	{
		this(xmlpathelements, null, false, preprocessor);
	}

	/**
	 *  Create a new xml info.
	 */
	public XMLInfo(String xmlpath, IFilter filter, boolean createfromtag)
	{
		this(xmlpath, filter, createfromtag, null);
	}
	
	/**
	 *  Create a new xml info.
	 */
	public XMLInfo(String xmlpath, IFilter filter, boolean createfromtag, IPreProcessor preprocessor)
	{
		setXMLPath(xmlpath);
		this.filter = filter;
		this.createfromtag = createfromtag;
		this.preprocessor = preprocessor;
	}

	/**
	 *  Create a new xml info.
	 */
	public XMLInfo(QName xmltag, IFilter filter, boolean createfromtag)
	{
		this(new QName[]{xmltag}, filter, createfromtag);
	}
	
	/**
	 *  Create a new xml info.
	 */
	public XMLInfo(QName[] xmlpathelements, IFilter filter, boolean createfromtag)
	{
		this(xmlpathelements, filter, createfromtag, null);
	}
	
	/**
	 *  Create a new xml info.
	 */
	public XMLInfo(QName[] xmlpathelements, IFilter filter, boolean createfromtag, IPreProcessor preprocessor)
	{
		setXMLPathElements(xmlpathelements);
		this.filter = filter;
		this.createfromtag = createfromtag;
		this.preprocessor = preprocessor;
	}

	//-------- methods --------
	
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

	/**
	 *  Get the preprocessor.
	 *  @return the preprocessor.
	 */
	public IPreProcessor getPreProcessor()
	{
		return preprocessor;
	}
	
//	public static void main(String[] args)
//	{
//		RuntimeException e = new RuntimeException();
//		try
//		{
//			e.fillInStackTrace();
//			throw e;
//		}
//		catch(Exception ex)
//		{
//			ex.printStackTrace();
//		}
//	}
}
