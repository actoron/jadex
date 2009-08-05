package jadex.commons.xml.writer;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *  Info for writing an object.
 */
public class WriteObjectInfo
{
	//-------- attributes --------
	
	/** The comment. */
	protected String comment;
	
	/** The attribute values. */
	protected Map attributes;

	/** The content. */
	protected String content;
	
	/** The subobjects map. */
	protected Map subobjects;
	
	//-------- methods --------

	/**
	 *  Get the comment.
	 *  @return The comment.
	 */
	public String getComment()
	{
		return this.comment;
	}

	/**
	 *  Set the comment.
	 *  @param comment The comment to set.
	 */
	public void setComment(String comment)
	{
		this.comment = comment;
	}
	
	/**
	 *  Get the attributes.
	 *  @return The attributes.
	 */
	public Map getAttributes()
	{
		return this.attributes;
	}

	/**
	 *  Add an attribute.
	 *  @param name The name.
	 *  @param value The value.
	 */
	public void addAttribute(String name, String value)
	{
		if(attributes==null)
			attributes = new LinkedHashMap();
		else if(attributes.containsKey(name))
			throw new RuntimeException("Duplicate attribute: "+name);
		attributes.put(name, value);
	}

	/**
	 *  Get the content.
	 *  @return The content.
	 */
	public String getContent()
	{
		return this.content;
	}
	
	/**
	 *  Set the content.
	 *  @param content The content to set.
	 */
	public void setContent(String content)
	{
		this.content = content;
	}

	/**
	 *  Get the subobjects.
	 *  @return The subobjects.
	 */
	public Map getSubobjects()
	{
		return this.subobjects;
	}
	
	/**
	 *  Add a subobject.
	 */
	public void addSubobject(String tagname, Object subobject)
	{
		if(subobjects==null)
			subobjects = new LinkedHashMap();
		else if(subobjects.containsKey(tagname))
			throw new RuntimeException("Duplicate subobject: "+tagname);
		subobjects.put(tagname, subobject);
	}
}
