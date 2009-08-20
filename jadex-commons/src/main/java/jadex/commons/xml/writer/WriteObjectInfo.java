package jadex.commons.xml.writer;

import jadex.commons.SUtil;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.namespace.QName;

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
	public void addSubobject(QName[] pathname, Object subobject)
	{
//		System.out.println("added: "+SUtil.arrayToString(pathname)+" "+subobject);
		if(subobjects==null)
			subobjects = new LinkedHashMap();
		
		insertSubobject(subobjects, pathname, subobject, 0);
	}
	
	public static final String SUBTAGMAP = "subtagmap";

	/**
	 * 
	 */
	protected void insertSubobject(Map tagmap, QName[] tags, Object subob, int i)
	{
		if(i+2>=tags.length)
		{
			List elems = (List)tagmap.get(tags[i]);
			if(elems==null)
			{
				elems = new ArrayList();
				elems.add(SUBTAGMAP);
				tagmap.put(tags[i], elems);
			}
			elems.add(new Object[]{tags[i+1], subob});
		}
		else
		{
			Map subtagmap = (Map)tagmap.get(tags[i]);
			if(subtagmap==null)
			{
				subtagmap = new LinkedHashMap();
				subtagmap.put(SUBTAGMAP, SUBTAGMAP);
				tagmap.put(tags[i], subtagmap);
			}
			insertSubobject(subtagmap, tags, subob, i+1);
		}
	}	
	
//	/**
//	 * 
//	 */
//	protected void insertSubobject(Map tagmap, QName[] tags, Object subob, int i)
//	{
//		if(i+1==tags.length)
//		{
//			List elems = (List)tagmap.get(tags[i]);
//			if(elems==null)
//			{
//				elems = new ArrayList();
//				elems.add(SUBTAGMAP);
//			}
//			tagmap.put(tags[i], elems);
//			elems.add(subob);
//		}
//		else
//		{
//			Map subtagmap = (Map)tagmap.get(tags[i]);
//			if(subtagmap==null)
//			{
//				subtagmap = new LinkedHashMap();
//				subtagmap.put(SUBTAGMAP, SUBTAGMAP);
//				tagmap.put(tags[i], subtagmap);
//			}
//		
//			insertSubobject(subtagmap, tags, subob, i+1);
//		}
//	}

	
}
