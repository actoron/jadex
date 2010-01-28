package jadex.xml;

import jadex.commons.IFilter;
import jadex.xml.reader.IBulkObjectLinker;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import javax.xml.namespace.QName;

/**
 *  Mapping from tag (or path fragment) to OAV type.
 */
public class TypeInfo	extends AbstractInfo
{
	/** Constant indicating that the type is not creatable from tag information. */
	public static String NOT_CREATEDABLE_FROM_TAG = "not_creatable_from_tag";
	
	//-------- attributes -------- 
	
	// read + write 
	
	/** The supertype. */
	protected TypeInfo supertype;
	
	/** The object type. */
	protected Object typeinfo; // (if not Ibeancreator)
	
	/** The comment info. */
	protected Object commentinfo;
	
	/** The content info. */
	protected Object contentinfo;
	
	/** The attributes info (xmlname -> attrinfo). */
	protected Map attributeinfos;
	
	/** The include fields flag. */
	protected boolean includefields;
	
	// read
	
	/** The post processor (if any). */
	protected IPostProcessor postproc;
	
	/** Create from tag flag. */
	protected boolean createfromtag;
	
	/** The linker. */
	protected Object linker;
	
	/** The link mode (determined by the linker if present). */
	protected boolean bulklink;
	
	// todo: IPreWriter for doing sth with the object before writing?
	
	// write
	
	/** The sub objects (non-xml name -> subobject info). */
	protected Map subobjectinfoswrite;
	
	/** The sub objects (xmlpath -> subobject info). */ 
	protected Map subobjectinfosread;
	
	//-------- constructors --------
	
	/**
	 *  Create a new type info.
	 *  @param xmlpath The path or tag.
	 *  @param typeinfo The type of object to create.
	 */
	public TypeInfo(TypeInfo supertype, String xmlpath, Object typeinfo)
	{
		this(supertype, xmlpath, typeinfo, null, null);
	}
	
	/**
	 *  Create a new type info.
	 *  @param xmlpath The path or tag.
	 *  @param type The type of object to create.
	 *  @param commentinfo The commnentinfo.
	 *  @param contentinfo The contentinfo.
	 */
	public TypeInfo(TypeInfo supertype, String xmlpath, Object typeinfo, Object commentinfo, Object contentinfo)
	{
		this(supertype, xmlpath, typeinfo, commentinfo, contentinfo, null, null);
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
	public TypeInfo(TypeInfo supertype, String xmlpath, Object typeinfo, Object commentinfo, Object contentinfo, 
		AttributeInfo[] attributesinfo, IPostProcessor postproc)
	{
		this(supertype, xmlpath, typeinfo, commentinfo, contentinfo, attributesinfo, postproc, null);
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
	public TypeInfo(TypeInfo supertype, String xmlpath, Object typeinfo, Object commentinfo, Object contentinfo, 
		AttributeInfo[] attributeinfos, IPostProcessor postproc, IFilter filter)
	{
		this(supertype, xmlpath, typeinfo, commentinfo, contentinfo, attributeinfos, postproc, filter, null);
	}
	
	/**
	 *  Create a new type info.
	 * @param xmlpath The path or tag.
	 * @param typeinfo The type of object to create.
	 * @param commentinfo The commnent.
	 * @param contentinfo The content.
	 * @param attributeinfos The attributes map.
	 * @param postproc The post processor. 
	 */
	public TypeInfo(TypeInfo supertype, String xmlpath, Object typeinfo, Object commentinfo, 
		Object contentinfo, AttributeInfo[] attributeinfos, IPostProcessor postproc, IFilter filter,
		SubobjectInfo[] subobjectinfos)
	{
		this(supertype, xmlpath, typeinfo, commentinfo, contentinfo, attributeinfos, postproc, filter, subobjectinfos, true, false);
	}
	
	/**
	 *  Create a new type info.
	 * @param xmlpath The path or tag.
	 * @param typeinfo The type of object to create.
	 * @param commentinfo The commnent.
	 * @param contentinfo The content.
	 * @param attributeinfos The attributes map.
	 * @param postproc The post processor. 
	 */
	public TypeInfo(TypeInfo supertype, String xmlpath, Object typeinfo, Object commentinfo, 
		Object contentinfo, AttributeInfo[] attributeinfos, IPostProcessor postproc, IFilter filter,
		SubobjectInfo[] subobjectinfos, boolean createfromtag, boolean includefields)
	{
		this(supertype, xmlpath, typeinfo, commentinfo, contentinfo, attributeinfos, postproc, filter, subobjectinfos, true, false, null);
	}
	
	/**
	 *  Create a new type info.
	 * @param xmlpath The path or tag.
	 * @param typeinfo The type of object to create.
	 * @param commentinfo The commnent.
	 * @param contentinfo The content.
	 * @param attributeinfos The attributes map.
	 * @param postproc The post processor. 
	 */
	public TypeInfo(TypeInfo supertype, String xmlpath, Object typeinfo, Object commentinfo, 
		Object contentinfo, AttributeInfo[] attributeinfos, IPostProcessor postproc, IFilter filter,
		SubobjectInfo[] subobjectinfos, boolean createfromtag, boolean includefields, Object linker)
	{
		this(supertype, xmlpath, typeinfo, commentinfo, contentinfo, attributeinfos, postproc, filter, subobjectinfos, true, false, null, false);
	}
	
	/**
	 *  Create a new type info.
	 * @param xmlpath The path or tag.
	 * @param typeinfo The type of object to create.
	 * @param commentinfo The commnent.
	 * @param contentinfo The content.
	 * @param attributeinfos The attributes map.
	 * @param postproc The post processor. 
	 */
	public TypeInfo(TypeInfo supertype, String xmlpath, Object typeinfo, Object commentinfo, 
		Object contentinfo, AttributeInfo[] attributeinfos, IPostProcessor postproc, IFilter filter,
		SubobjectInfo[] subobjectinfos, boolean createfromtag, boolean includefields, Object linker, boolean bulklink)
	{
		super(xmlpath, filter);
		this.supertype = supertype;
		this.typeinfo = typeinfo;
		this.commentinfo = commentinfo;
		this.contentinfo = contentinfo;
		this.postproc = postproc;
		this.createfromtag = createfromtag;
		this.includefields = includefields;
		this.linker = linker;
		this.bulklink = bulklink;
		
		if(attributeinfos!=null)
			this.attributeinfos = createAttributeInfos(attributeinfos);
		
		if(subobjectinfos!=null)
			this.subobjectinfoswrite = createSubobjectInfosWrite(subobjectinfos);
		this.subobjectinfosread = createSubobjectInfosRead(subobjectinfos);
	}
	
	//-------- all constructors also with xmlpath as QName[] :-(  --------
	
	/**
	 *  Create a new type info.
	 *  @param xmlpath The path or tag.
	 *  @param typeinfo The type of object to create.
	 */
	public TypeInfo(TypeInfo supertype, QName[] xmlpath, Object typeinfo)
	{
		this(supertype, xmlpath, typeinfo, null, null);
	}
	
	/**
	 *  Create a new type info.
	 *  @param xmlpath The path or tag.
	 *  @param type The type of object to create.
	 *  @param commentinfo The commnentinfo.
	 *  @param contentinfo The contentinfo.
	 */
	public TypeInfo(TypeInfo supertype, QName[] xmlpath, Object typeinfo, Object commentinfo, Object contentinfo)
	{
		this(supertype, xmlpath, typeinfo, commentinfo, contentinfo, null, null);
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
	public TypeInfo(TypeInfo supertype, QName[] xmlpath, Object typeinfo, Object commentinfo, Object contentinfo, 
		AttributeInfo[] attributesinfo, IPostProcessor postproc)
	{
		this(supertype, xmlpath, typeinfo, commentinfo, contentinfo, attributesinfo, postproc, null);
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
	public TypeInfo(TypeInfo supertype, QName[] xmlpath, Object typeinfo, Object commentinfo, Object contentinfo, 
		AttributeInfo[] attributeinfos, IPostProcessor postproc, IFilter filter)
	{
		this(supertype, xmlpath, typeinfo, commentinfo, contentinfo, attributeinfos, postproc, filter, null);
	}
	
	/**
	 *  Create a new type info.
	 * @param xmlpath The path or tag.
	 * @param typeinfo The type of object to create.
	 * @param commentinfo The commnent.
	 * @param contentinfo The content.
	 * @param attributeinfos The attributes map.
	 * @param postproc The post processor. 
	 */
	public TypeInfo(TypeInfo supertype, QName[] xmlpath, Object typeinfo, Object commentinfo, 
		Object contentinfo, AttributeInfo[] attributeinfos, IPostProcessor postproc, IFilter filter,
		SubobjectInfo[] subobjectinfos)
	{
		this(supertype, xmlpath, typeinfo, commentinfo, contentinfo, attributeinfos, postproc, filter, subobjectinfos, true);
	}
	
	/**
	 *  Create a new type info.
	 * @param xmlpath The path or tag.
	 * @param typeinfo The type of object to create.
	 * @param commentinfo The commnent.
	 * @param contentinfo The content.
	 * @param attributeinfos The attributes map.
	 * @param postproc The post processor. 
	 */
	public TypeInfo(TypeInfo supertype, QName[] xmlpath, Object typeinfo, Object commentinfo, 
		Object contentinfo, AttributeInfo[] attributeinfos, IPostProcessor postproc, IFilter filter,
		SubobjectInfo[] subobjectinfos, boolean createfromtag)
	{
		super(xmlpath, filter);
		this.supertype = supertype;
		this.typeinfo = typeinfo;
		this.commentinfo = commentinfo;
		this.contentinfo = contentinfo;
		this.postproc = postproc;
		this.createfromtag = createfromtag;
		
		if(attributeinfos!=null)
			this.attributeinfos = createAttributeInfos(attributeinfos);
		
		if(subobjectinfos!=null)
			this.subobjectinfoswrite = createSubobjectInfosWrite(subobjectinfos);
		this.subobjectinfosread = createSubobjectInfosRead(subobjectinfos);
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
	 *  Get the supertype.
	 *  @return The super type.
	 */
	public TypeInfo getSupertype()
	{
		return supertype;
	}

	/**
	 *  Get the comment info.
	 *  @return The comment
	 */
	public Object getCommentInfo()
	{
		return this.commentinfo!=null? commentinfo: supertype!=null? supertype.getCommentInfo(): null;
	}

	/**
	 *  Get the content info.
	 *  @return The content info.
	 */
	public Object getContentInfo()
	{
		return this.contentinfo!=null? contentinfo: supertype!=null? supertype.getContentInfo(): null;
	}
	
	/**
	 *  Get the includefields.
	 *  @return The includefields.
	 */
	public boolean isIncludeFields()
	{
		return this.includefields;
	}
	
	/**
	 *  Get the namespace.
	 *  @return The namespace.
	 * /
	public Namespace getNamespace()
	{
		return namespace!=null? namespace: supertype!=null? supertype.getNamespace(): null;
	}*/
	
	/**
	 *  Add an attribute info.
	 *  @param xmlname The xml attribute name.
	 *  @param attrinfo The attribute info.
	 * /
	public void addAttributeInfo(String xmlname, Object attrinfo)
	{
		if(attributeinfos==null)
			attributeinfos = new HashMap();
		attributeinfos.put(xmlname, attrinfo);
	}*/

	/**
	 *  Get the attribute info.
	 *  @param xmlname The xml name of the attribute.
	 *  @return The attribute info.
	 * /
	public Object getAttributeInfo(String xmlname)
	{
		Object ret = attributeinfos==null? null: attributeinfos.get(xmlname);
		if(ret==null && supertype!=null)
			ret = supertype.getAttributeInfo(xmlname);
		return ret;
	}*/

	/**
	 *  Get the attribute info.
	 *  @param xmlname The xml name of the attribute.
	 *  @return The attribute info.
	 */
	public Object getAttributeInfo(QName xmlname)
	{
		Object ret = attributeinfos==null? null: attributeinfos.get(xmlname);
		if(ret==null && supertype!=null)
			ret = supertype.getAttributeInfo(xmlname);
		return ret;
	}
	
	/**
	 *  Get the xml attribute names.
	 *  @return The attribute names.
	 */
	public Set getXMLAttributeNames()
	{
		Set ret = attributeinfos==null? new HashSet(): new HashSet(attributeinfos.keySet());
		if(supertype!=null)
			ret.addAll(supertype.getXMLAttributeNames());
		return ret;
	}
	
	/**
	 *  Get the attribute infos.
	 *  @return The attribute infos.
	 */
	public Collection getAttributeInfos()
	{
		Collection ret = attributeinfos==null? new HashSet(): attributeinfos.values();
		if(supertype!=null)
			ret.addAll(supertype.getAttributeInfos());
		return ret;
	}
	
	/**
	 *  Get the declared attribute infos.
	 */
	public AttributeInfo[] getDeclaredAttributeInfos()
	{
		return attributeinfos==null? null: (AttributeInfo[])attributeinfos.values().toArray(new AttributeInfo[0]);
	}
	
	/**
	 *  Get the declared subobject infos.
	 */
	public SubobjectInfo[] getDeclaredSubobjectInfos()
	{
		return subobjectinfoswrite==null? null: (SubobjectInfo[])subobjectinfoswrite.values().toArray(new SubobjectInfo[0]);
	}
	
	/**
	 *  Get the post-processor.
	 *  @return The post-processor
	 */
	public IPostProcessor getPostProcessor()
	{
		return this.postproc!=null? postproc: supertype!=null? supertype.getPostProcessor(): null;
	}

	
	/**
	 *  Add a subobjects info.
	 *  @param info The subobjects info.
	 * /
	public void addSubobjectInfo(Object nonxmlname, SubobjectInfo info)
	{
		if(subobjectinfoswrite==null)
			subobjectinfoswrite = new HashMap();
		subobjectinfoswrite.put(nonxmlname, info);
	}*/
	
	/**
	 *  Get the subobject infos. 
	 *  @return The subobject infos.
	 */
	public Collection getSubobjectInfos()
	{
		Collection ret = subobjectinfoswrite!=null? subobjectinfoswrite.values(): new LinkedHashSet();
		if(supertype!=null)
			ret.addAll(supertype.getSubobjectInfos());
		return ret;
	}
	
	/**
	 *  Get the subobject infos. 
	 *  @return The subobject info.
	 */
	public SubobjectInfo getSubobjectInfoWrite(Object attr)
	{
		SubobjectInfo ret = subobjectinfoswrite!=null? (SubobjectInfo)subobjectinfoswrite.get(attr): null;
		if(ret==null && supertype!=null)
			ret = supertype.getSubobjectInfoWrite(attr);
		return ret;
	}
	
	/**
	 *  Get the most specific subobject info.
	 *  @param tag The tag.
	 *  @param fullpath The full path.
	 *  @return The most specific subobject info.
	 */
	public SubobjectInfo getSubobjectInfoRead(QName tag, QName[] fullpath, Map rawattributes)
	{
		SubobjectInfo ret = null;
		
		// Hack exclude tag when classname :-(
//		if(tag.indexOf(".")!=-1)
//		{
//			tag = fullpath[fullpath.length-2];
//			String[] tmp = new String[fullpath.length-1];
//			System.arraycopy(fullpath, 0, tmp, 0, tmp.length);
//			fullpath = tmp;
//		}
		
		Set subobjects = subobjectinfosread!=null? (Set)subobjectinfosread.get(tag): null;
		ret = findSubobjectInfo(subobjects, fullpath, rawattributes);
		
//		Set subobjects = subobjectinfosread!=null? (Set)subobjectinfosread.get(tag): null;			
//		if(subobjects!=null)
//		{
//			for(Iterator it=subobjects.iterator(); ret==null && it.hasNext(); )
//			{
//				SubobjectInfo tmp = (SubobjectInfo)it.next();
//				if(fullpath.endsWith(tmp.getXMLPath()) && (tmp.getFilter()==null || tmp.getFilter().filter(rawattributes)))
//					ret = tmp;
//			}
//		}
		return ret;
	}
	
	/**
	 *  Test if object should be created from tag name.
	 */
	public boolean isCreateFromTag()
	{
		return createfromtag;
	}
	
	/**
	 *  Get the linker.
	 *  @return The linker.
	 */
	public Object getLinker()
	{
		return this.linker;
	}

	/**
	 *  Set the linker.
	 *  @param linker The linker to set.
	 */
	public void setLinker(Object linker)
	{
		this.linker = linker;
	}

	/**
	 *  Test if the object should be bulk linked. 
	 */
	public boolean isBulkLink()
	{
		return bulklink || linker instanceof IBulkObjectLinker;
	}
	
	/**
	 *  Find a subobject info.
	 */
	protected SubobjectInfo findSubobjectInfo(Set soinfos, QName[] fullpath, Map rawattributes)
	{
		SubobjectInfo ret = null;
		if(soinfos!=null)
		{
			for(Iterator it=soinfos.iterator(); ret==null && it.hasNext(); )
			{
				SubobjectInfo si = (SubobjectInfo)it.next();
				QName[] tmp = si.getXMLPathElementsWithoutElement();
				boolean ok = (si.getFilter()==null || si.getFilter().filter(rawattributes)) && 
					(tmp==null || tmp.length<=fullpath.length);
				for(int i=1; i<=tmp.length && ok; i++)
				{
					ok = tmp[tmp.length-i].equals(fullpath[fullpath.length-i-1]);
				}
				if(ok)
					ret = si;
//				if(fullpath.endsWith(tmp.getXMLPathWithoutElement())) // && (tmp.getFilter()==null || tmp.getFilter().filter(rawattributes)))
			}
		}
		return ret;
	}
	
	/**
	 *  Create subobject infos for each tag sorted by specificity.
	 *  @param subobjectinfos The subobject infos.
	 *  @return Map of subobject infos.
	 */
	protected Map createSubobjectInfosWrite(SubobjectInfo[] subobjectinfos)
	{
		Map ret = new LinkedHashMap();
		for(int i=0; i<subobjectinfos.length; i++)
		{
			ret.put(subobjectinfos[i].getLinkInfo(), subobjectinfos[i]);
		}
		return ret;
	}
	
	/**
	 *  Create subobject infos for each tag sorted by specificity.
	 *  @param subobjectinfos The subobject infos.
	 *  @return Map of subobject infos.
	 */
	protected Map createSubobjectInfosRead(SubobjectInfo[] subobjectinfos)
	{
		Map ret = new HashMap();
		
		if(subobjectinfos!=null)
		{
			for(int i=0; i<subobjectinfos.length; i++)
			{
				TreeSet subobjects = (TreeSet)ret.get(subobjectinfos[i].getXMLTag());
				if(subobjects==null)
				{
					subobjects = new TreeSet(new AbstractInfo.SpecificityComparator());
					ret.put(subobjectinfos[i].getXMLTag(), subobjects);
				}
				subobjects.add(subobjectinfos[i]);
			}
		}
		
		if(supertype!=null)
		{
			Collection soinfos = supertype.getSubobjectInfos();
			for(Iterator it=soinfos.iterator(); it.hasNext(); )
			{
				SubobjectInfo soinfo = (SubobjectInfo)it.next();
				TreeSet subobjects = (TreeSet)ret.get(soinfo.getXMLTag());
				if(subobjects==null)
				{
					subobjects = new TreeSet(new AbstractInfo.SpecificityComparator());
					ret.put(soinfo.getXMLTag(), subobjects);
				}
				subobjects.add(soinfo);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Create the attribute info map (xml name -> attribute info).
	 */
	protected Map createAttributeInfos(AttributeInfo[] attributeinfos)
	{
		Map ret = new HashMap();
		for(int i=0; i<attributeinfos.length; i++)
		{
			QName xmlname = attributeinfos[i].getXMLAttributeName();
			if(xmlname==null)
			{
				Object attrid = attributeinfos[i].getAttributeIdentifier();
				if(attrid!=null)
				{
					xmlname = new QName(attrid.toString());
//					System.out.println("Warning, no xml name for attribute:"+attrid);
				}
			}
			ret.put(xmlname, attributeinfos[i]);
		}
		return ret;
	}
}
