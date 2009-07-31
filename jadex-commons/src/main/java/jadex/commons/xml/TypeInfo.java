package jadex.commons.xml;

import jadex.commons.IFilter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
	
	/** The sub objects (non-xml name -> subobject info). */ // todo: unify with link infos?!
	protected Map subobjectsinfo;
	
	//-------- constructors --------
	
	/**
	 *  Create a new type info.
	 *  @param xmlpath The path or tag.
	 *  @param typeinfo The type of object to create.
	 */
	public TypeInfo(String xmlpath, Object typeinfo)
	{
		this(xmlpath, typeinfo, null, null);
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
		this(xmlpath, typeinfo, commentinfo, contentinfo, null, null);
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
		this(xmlpath, typeinfo, commentinfo, contentinfo, attributesinfo, postproc, null, null);
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
		Map attributesinfo, IPostProcessor postproc, IFilter filter, SubobjectInfo[] subobjectsinfo)
	{
		super(xmlpath, filter);
		this.typeinfo = typeinfo;
		this.commentinfo = commentinfo;
		this.contentinfo = contentinfo;
		this.attributesinfo = attributesinfo;
		this.postproc = postproc;
		
		if(subobjectsinfo!=null)
		{
			this.subobjectsinfo = new HashMap();
			for(int i=0; i<subobjectsinfo.length; i++)
			{
				this.subobjectsinfo.put(subobjectsinfo[i].getAttribute(), subobjectsinfo[i]);
			}
		}
		
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
		return attributesinfo==null? null: attributesinfo.values();
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
	public void addSubobjectInfo(Object nonxmlname, SubobjectInfo info)
	{
		if(subobjectsinfo==null)
			subobjectsinfo = new HashMap();
		subobjectsinfo.put(nonxmlname, info);
	}
	
	/**
	 *  Get the subobject infos. 
	 *  @return The subobject infos.
	 */
	public Map getSubobjectInfos()
	{
		return subobjectsinfo;
	}
	
	/**
	 *  Get the subobject infos. 
	 *  @return The subobject info.
	 */
	public SubobjectInfo getSubobjectInfo(Object attr)
	{
		return (SubobjectInfo)subobjectsinfo.get(attr);
	}
}
