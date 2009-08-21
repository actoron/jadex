package jadex.commons.xml;

import java.util.StringTokenizer;

import javax.xml.namespace.QName;

import jadex.commons.IFilter;

/**
 *  Info object for subobjects, i.e. objects that are contained in another object.
 */
public class SubobjectInfo extends AbstractInfo
{
	//-------- attributes --------
	
	// read + write
	
	/** The link info. */
	protected AttributeInfo linkinfo;
	
	/** The type info of the subobjects. */
	// E.g. used for write check, i.e. is it the object of the right type
	protected TypeInfo typeinfo;
	
	/** The multiplicity. */
	protected boolean multi;

	//-------- constructors --------
	
	/**
	 *  Create a link info. 
	 *  @param xmlpath The xmlpath.
	 *  @param linkinfo The link info.
	 */
	public SubobjectInfo(AttributeInfo linkinfo)
	{
		this(linkinfo, null);
	}
	
	/**
	 *  Create a link info. 
	 *  @param xmlpath The xmlpath.
	 *  @param linkinfo The link info.
	 */
	public SubobjectInfo(AttributeInfo linkinfo, IFilter filter)
	{
		this(linkinfo, filter, null);
	}
	
	/**
	 *  Create a link info. 
	 *  @param xmlpath The xmlpath.
	 *  @param linkinfo The link info.
	 */
	public SubobjectInfo(AttributeInfo linkinfo, IFilter filter, TypeInfo typeinfo)
	{
		this(linkinfo, filter, typeinfo, false);
	}
	
	/**
	 *  Create a link info. 
	 *  @param xmlpath The xmlpath.
	 *  @param linkinfo The link info.
	 */
	public SubobjectInfo(AttributeInfo linkinfo, IFilter filter, TypeInfo typeinfo, boolean multi)
	{
		this((String)null, linkinfo, filter, typeinfo, multi);
	}
	
	/**
	 *  Create a link info. 
	 *  @param xmlpath The xmlpath.
	 *  @param linkinfo The link info.
	 */
	public SubobjectInfo(QName[] path, AttributeInfo linkinfo, IFilter filter, TypeInfo typeinfo, boolean multi)
	{
		super(createFullpath(path, linkinfo.getXMLAttributeName()), filter);
		this.linkinfo = linkinfo;
		this.typeinfo = typeinfo;
		this.multi = multi;
	}
	
	/**
	 *  Create a link info. 
	 *  @param xmlpath The xmlpath.
	 *  @param linkinfo The link info.
	 */
	public SubobjectInfo(QName[] path, AttributeInfo linkinfo)
	{
		this(path, linkinfo, null, null, false);
	}
	
	
	
	/**
	 *  Create a link info. 
	 *  @param xmlpath The xmlpath.
	 *  @param linkinfo The link info.
	 */
	public SubobjectInfo(String path, AttributeInfo linkinfo)
	{
		this(path, linkinfo, null, null, false);
	}
	
	/**
	 *  Create a link info. 
	 *  @param xmlpath The xmlpath.
	 *  @param linkinfo The link info.
	 */
	public SubobjectInfo(String path, AttributeInfo linkinfo, IFilter filter)
	{
		this(path, linkinfo, filter, null);
	}
	
	/**
	 *  Create a link info. 
	 *  @param xmlpath The xmlpath.
	 *  @param linkinfo The link info.
	 */
	public SubobjectInfo(String path, AttributeInfo linkinfo, IFilter filter, TypeInfo typeinfo)
	{
		this(path, linkinfo, filter, typeinfo, false);
	}
	
	/**
	 *  Create a link info. 
	 *  @param xmlpath The xmlpath.
	 *  @param linkinfo The link info.
	 */
	public SubobjectInfo(String path, AttributeInfo linkinfo, IFilter filter, TypeInfo typeinfo, boolean multi)
	{
		this(createPath(path), linkinfo, filter, typeinfo, multi);
	}
	
	/**
	 *  Create full path. 
	 */
	public static QName[] createPath(String path)
	{
		QName[] ret = null;
		if(path!=null)
		{
			StringTokenizer stok = new StringTokenizer(path, "/");
			ret = new QName[stok.countTokens()];
			for(int i=0; stok.hasMoreTokens(); i++)
			{
				ret[i] = QName.valueOf(stok.nextToken());
			}
		}
		return ret;
	}
	
	/**
	 *  Create full path. 
	 */
	public static QName[] createFullpath(QName[] path, QName name)
	{
		QName[] ret;
		if(path==null)
		{
			ret = new QName[]{name};
		}
		else
		{
			ret = new QName[path.length+1];
			System.arraycopy(path, 0, ret, 0, path.length);
			ret[path.length] = name;
		}
		
		return ret;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the link info.
	 *  @return The link info.
	 */
	public AttributeInfo getLinkInfo()
	{
		return this.linkinfo;
	}

	/**
	 *  Get the typeinfo.
	 *  @return The typeinfo.
	 */
	public TypeInfo getTypeInfo()
	{
		return this.typeinfo;
	}

	/**
	 *  Test if it is a multi subobject.
	 */
	public boolean isMulti()
	{
		return multi;
	}
		
}
