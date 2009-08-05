package jadex.commons.xml;

import java.util.Map;

/**
 *  A struct to represent an element on the stack while parsing.
 */
public class StackElement
{
	//-------- attributes --------
	
	/** The xml tag. */
	protected String tag;
	
	/** The created object. */
	protected Object object;
	
	/** The collected content. */
	protected String content;
	
	// todo: remove rest somehow
	
	/** The raw attributes. */
	protected Map rawattrs;
	
	/** The type info. */
	protected TypeInfo typeinfo;
	
	//-------- constructors --------
	
	/**
	 *  Create a new stack element.
	 */
	public StackElement(String tag, Object object)
	{
		this(tag, object, null);
	}
	
	/**
	 *  Create a new stack element.
	 */
	public StackElement(String tag, Object object, Map rawattrs)
	{
		this.tag = tag;
		this.object = object;
		this.rawattrs = rawattrs;
	}
	
	/**
	 *  Create a new stack element.
	 */
	public StackElement(String tag, Object object, Map rawattrs, TypeInfo typeinfo)
	{
		this.tag = tag;
		this.object = object;
		this.rawattrs = rawattrs;
		this.typeinfo = typeinfo;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the tag.
	 *  @return The tag.
	 */
	public String getTag()
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
	 *  Get the content (if any).
	 *  @return The content or null for empty elements.
	 */
	public String getContent()
	{
		return this.content;
	}
	
	/**
	 *  Get the raw attributes.
	 *  @return The raw attributes.
	 */
	public Map getRawAttributes()
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
	 *  Add content to the already collected content (if any).
	 *  @param content	The content to add.
	 */
	public void	addContent(String content)
	{
		if(this.content==null)
			this.content	= content;
		else
			this.content	+= content;
	}
}