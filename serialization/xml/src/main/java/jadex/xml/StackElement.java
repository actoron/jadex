package jadex.xml;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jadex.commons.SUtil;
import jadex.xml.reader.IObjectReaderHandler;
import jadex.xml.stax.ILocation;
import jadex.xml.stax.QName;

/**
 *  A struct to represent an element on the stack while parsing.
 */
public class StackElement
{
	//-------- attributes --------
	
	/** The reader handler (if any). */
	protected IObjectReaderHandler	readerhandler;

	/** The xml tag. */
	protected QName tag;
	
	/** The created object. */
	protected Object object;
	
	/** The collected content. */
	protected String content;
	
	/** Collecting content. */
//	protected StringBuffer cbuf;
	protected List<String> parts;
	
	// todo: remove rest somehow
	
	/** The raw attributes. */
	protected Map<String, String> rawattrs;
	
	/** The type info. */
	protected TypeInfo typeinfo;
	
	/** The location of the start tag. */
	protected ILocation	location;
	
	//-------- constructors --------
	
	/**
	 *  Create a new stack element.
	 */
	// Used for writing, thus no reader handler
	public StackElement(QName tag, Object object)
	{
		this(null, tag, object, null);
	}
	
	/**
	 *  Create a new stack element.
	 */
	public StackElement(IObjectReaderHandler readerhandler, QName tag, Object object)
	{
		this(readerhandler, tag, object, null);
	}
	
	/**
	 *  Create a new stack element.
	 */
	public StackElement(IObjectReaderHandler readerhandler, QName tag, Object object, Map<String, String> rawattrs)
	{
		this(readerhandler, tag, object, rawattrs, null, null);
	}
	
	/**
	 *  Create a new stack element.
	 */
	public StackElement(IObjectReaderHandler readerhandler, QName tag, Object object, Map<String, String> rawattrs, TypeInfo typeinfo, ILocation location)
	{
		this.readerhandler	= readerhandler;
		this.tag = tag;
		this.object = object;
		this.rawattrs = rawattrs;
		this.typeinfo = typeinfo;
		this.location	= location;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the tag.
	 *  @return The tag.
	 */
	public QName getTag()
	{
		return this.tag;
	}

	/**
	 *  Get the object.
	 *  @return The object.
	 */
	public Object getObject()
	{
		return this.object;
	}
	
	/**
	 *  Set the object.
	 *  @param object The object to set.
	 */
	public void setObject(Object object)
	{
		this.object = object;
	}

	/**
	 *  Get the content (if any).
	 *  @return The content or null for empty elements.
	 */
	public String getContent()
	{
		if(parts!=null)
		{
			assert content==null;
			
			if(parts.size()==1)
			{
				content = (String)parts.get(0);
			}
			else
			{
				int len = 0;
				for(int i=0; i<parts.size(); i++)
				{
					String tmp = (String)parts.get(i);
					len += tmp.length();
				}
				StringBuilder builder = new StringBuilder(len);
				for(int i=0; i<parts.size(); i++)
				{
					builder.append(parts.get(i));
				}
				content	= builder.toString();
			}
			parts = null;
		}
		return content;
	}
	
	/**
	 *  Get the raw attributes.
	 *  @return The raw attributes.
	 */
	public Map<String, String> getRawAttributes()
	{
		return this.rawattrs;
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
	 *  Get the location.
	 *  @return The location.
	 */
	public ILocation getLocation()
	{
		return this.location;
	}
	
	/**
	 *  Add content to the already collected content (if any).
	 *  @param content	The content to add.
	 */
	public void	addContent(String content)
	{
		assert content!=null;
		
		if(parts==null)
			parts = new ArrayList<String>();
//		System.out.println("Added content: '"+content+"'");

		parts.add(content);
	}

	/**
	 *  Get the string representation.
	 *  @return The string representation.
	 */
	public String toString()
	{
		return "StackElement(tag="+this.tag+", object=" + this.object + ")";
	}

	/**
	 *  Get the hash code.
	 */
	public int hashCode()
	{
		// Content/object set afterwards, cannot use for hashcode!
		int result = 31 + ((rawattrs==null) ? 0 : rawattrs.hashCode());
		result = 31*result + ((tag==null) ? 0 : tag.hashCode());
		result = 31*result + ((typeinfo==null) ? 0 : typeinfo.hashCode());
		result = 31*result + ((location==null) ? 0 : location.hashCode());
		return result;
	}

	/**
	 *  Test if two stack elements are equal.
	 */
	public boolean equals(Object obj)
	{
		boolean	ret	= this==obj;
		if(!ret && obj instanceof StackElement)
		{
			// Content/object set afterwards, cannot use for equals!
			StackElement other = (StackElement)obj;
			ret	= SUtil.equals(rawattrs, other.rawattrs)
				&& SUtil.equals(tag, other.tag)
				&& SUtil.equals(typeinfo, other.typeinfo)
				&& SUtil.equals(location, other.location);
		}
		return ret;
	}

	/**
	 *  Get the current reader handler.
	 */
	public IObjectReaderHandler getReaderHandler()
	{
		return readerhandler;
	}
}
