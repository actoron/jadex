package jadex.commons.xml;

import jadex.commons.IFilter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  Mapping from tag (or path fragment) to OAV type.
 */
public class TypeInfo	extends AbstractInfo
{
	//-------- attributes -------- 
	
	// read + write (if not Ibeancreator)
	
	/** The object type. */
	protected Object typeinfo;
	
	/** The comment info. */
	protected Object commentinfo;
	
	/** The content info. */
	protected Object contentinfo;
	
	// read
	
	/** The attributes info (xmlname -> attrinfo). */
	protected Map attributesinfo;
	
	/** The post processor (if any). */
	protected IPostProcessor postproc;
	
	// write
	
	/** The sub objects. */ // todo: unify with link infos?!
	protected List subobjectsinfo;
	
	//-------- constructors --------
	
	/**
	 *  Create a new type info.
	 *  @param xmlpath The path or tag.
	 *  @param typeinfo The type of object to create.
	 */
	public TypeInfo(String xmlpath, Object typeinfo)
	{
		this(xmlpath, typeinfo, null, null, null, null, null);
	}
	
	/**
	 *  Create a new type info.
	 *  @param xmlpath The path or tag.
	 *  @param type The type of object to create.
	 *  @param commentinfo The commnentinfo.
	 *  @param contentinfo The contentinfo.
	 */
	public TypeInfo(String xmlpath, Object typeinfo, Object commentinfo, Object contentinfo)
	{
		this(xmlpath, typeinfo, commentinfo, contentinfo, null, null, null);
	}
	
	/**
	 *  Create a new type info.
	 *  @param xmlpath The path or tag.
	 *  @param typeinfo The type of object to create.
	 *  @param commentinfo The commnent.
	 *  @param contentinfo The content.
	 *  @param attributesinfo The attributes map.
	 *  @param postproc The post processor. 
	 */
	public TypeInfo(String xmlpath, Object typeinfo, Object commentinfo, Object contentinfo, 
		Map attributesinfo, IPostProcessor postproc)
	{
		this(xmlpath, typeinfo, commentinfo, contentinfo, attributesinfo, postproc, null);
	}
	
	/**
	 *  Create a new type info.
	 *  @param xmlpath The path or tag.
	 *  @param typeinfo The type of object to create.
	 *  @param commentinfo The commnent.
	 *  @param contentinfo The content.
	 *  @param attributesinfo The attributes map.
	 *  @param postproc The post processor. 
	 */
	public TypeInfo(String xmlpath, Object typeinfo, Object commentinfo, Object contentinfo, 
		Map attributesinfo, IPostProcessor postproc, IFilter filter)
	{
		super(xmlpath, filter);
		this.typeinfo = typeinfo;
		this.commentinfo = commentinfo;
		this.contentinfo = contentinfo;
		this.attributesinfo = attributesinfo;
		this.postproc = postproc;
	}
	
	//-------- methods --------

	/**
	 *  Get the type info.
	 *  @return The type.
	 */
	public Object getTypeInfo()
	{
		return this.typeinfo;
	}

	/**
	 *  Set the type info.
	 *  @param type The type to set.
	 */
	public void setTypeInfo(Object typeinfo)
	{
		this.typeinfo = typeinfo;
	}

	/**
	 *  Get the comment info.
	 *  @return The comment
	 */
	public Object getCommentInfo()
	{
		return this.commentinfo;
	}

	/**
	 *  Set the comment info.
	 *  @param commentinfo The comment to set.
	 */
	public void setCommentInfo(Object commentinfo)
	{
		this.commentinfo = commentinfo;
	}

	/**
	 *  Get the content info.
	 *  @return The content info.
	 */
	public Object getContentInfo()
	{
		return this.contentinfo;
	}

	/**
	 *  Set the content info.
	 *  @param contentinfo The content info to set.
	 */
	public void setContentInfo(Object content)
	{
		this.contentinfo = contentinfo;
	}
	
	/**
	 *  Add an attribute info.
	 *  @param xmlname The xml attribute name.
	 *  @param attrinfo The attribute info.
	 */
	public void addAttributeInfo(String xmlname, Object attrinfo)
	{
		if(attributesinfo==null)
			attributesinfo = new HashMap();
		attributesinfo.put(xmlname, attrinfo);
	}
	
	/**
	 *  Get the attribute info.
	 *  @param xmlname The xml name of the attribute.
	 *  @return The attribute info.
	 */
	public Object getAttributeInfo(String xmlname)
	{
		return attributesinfo==null? null: attributesinfo.get(xmlname);
	}
	
	/**
	 *  Get the attribute info.
	 *  @param name The name of the attribute.
	 *  @return The attribute info.
	 * /
	public Object getAttributeInfo2(String name)
	{
		Object ret = null;
		
		// todo: use map
		if(attributesinfo!=null)
		{
			for(Iterator it=attributesinfo.values().iterator(); it.hasNext(); )
			{
				BeanAttributeInfo attrinfo = (BeanAttributeInfo)it.next();
				if(name.equals(attrinfo.getAttributeName()))
				{
					ret = attrinfo;
					break;
				}
			}
		}
		return ret;
	}*/
	
	/**
	 *  Get the xml attribute names.
	 *  @return The attribute names.
	 */
	public Set getXMLAttributeNames()
	{
		return attributesinfo==null? Collections.EMPTY_SET: new HashSet(attributesinfo.keySet());
	}
	
	/**
	 *  Get the attribute infos.
	 *  @return The attribute infos.
	 */
	public Collection getAttributeInfos()
	{
		return attributesinfo.values();
	}
	
	/**
	 *  Get the post-processor.
	 *  @return The post-processor
	 */
	public IPostProcessor getPostProcessor()
	{
		return this.postproc;
	}

	/**
	 *  Set the post-processor.
	 *  @param pproc The post-processor.
	 */
	public void setPostProcessor(IPostProcessor pproc)
	{
		this.postproc = pproc;
	}
	
	/**
	 *  Add a subobjects info.
	 *  @param info The subobjects info.
	 */
	public void addSubobjectInf(Object info)
	{
		if(subobjectsinfo==null)
			subobjectsinfo = new ArrayList();
		subobjectsinfo.add(info);
	}
	
	/**
	 *  Get the subobject infos. 
	 *  @return The subobject infos.
	 */
	public List getSubobjectInfos()
	{
		return subobjectsinfo;
	}
}
