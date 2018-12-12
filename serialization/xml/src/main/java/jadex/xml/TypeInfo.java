package jadex.xml;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import jadex.commons.Tuple;
import jadex.xml.reader.IObjectReaderHandler;
import jadex.xml.stax.QName;
/**
 *  Mapping from tag (or path fragment) to object.
 */
public class TypeInfo extends AbstractInfo
{	
	//-------- attributes -------- 
	
	// todo: IPreWriter for doing sth with the object before writing?
	
	/** The reader handler (if any). */
	protected IObjectReaderHandler	readerhandler;
	
	/** The object info for storing info about the object side, e.g. the object type. */
	protected ObjectInfo objectinfo;
	
	/** The map info contains all mapping relevant data, e.g. for attributes, subobjects etc. */
	protected MappingInfo mapinfo;
	
	/** The link info . */
	protected LinkingInfo linkinfo;

	
	/** The attributes info (xmlname -> attrinfo). */
	protected Map<Object, AttributeInfo> attributeinfos;
	
	/** The sub objects (non-xml name -> subobject info). */
	protected Map<AccessInfo, SubobjectInfo> subobjectinfoswrite;
	
	/** The sub objects (xmlpath -> subobject info). */ 
	protected Map<QName, TreeSet<SubobjectInfo>> subobjectinfosread;
	
	//-------- constructors --------
	
	/**
	 *  Create a new type info.
	 */
	public TypeInfo(XMLInfo xmlinfo, ObjectInfo objectinfo)
	{
		this(xmlinfo, objectinfo, null);
	}
	
	/**
	 *  Create a new type info.
	 */
	public TypeInfo(XMLInfo xmlinfo, ObjectInfo objectinfo, MappingInfo mapinfo)
	{
		this(xmlinfo, objectinfo, mapinfo, null);
	}
	
	/**
	 *  Create a new type info.
	 */
	public TypeInfo(XMLInfo xmlinfo, ObjectInfo objectinfo, MappingInfo mapinfo, LinkingInfo linkinfo)
	{
		this(xmlinfo, objectinfo, mapinfo, linkinfo, null);
	}
	
	/**
	 *  Create a new type info.
	 */
	public TypeInfo(XMLInfo xmlinfo, ObjectInfo objectinfo, MappingInfo mapinfo, LinkingInfo linkinfo, IObjectReaderHandler readerhandler)
	{
		super(xmlinfo);
		this.objectinfo = objectinfo;
		this.mapinfo = mapinfo;
		this.linkinfo = linkinfo;
		this.readerhandler	= readerhandler;
		
		if(mapinfo!=null && mapinfo.getAttributeInfos()!=null)
			this.attributeinfos = createAttributeInfos(mapinfo.getAttributeInfos());
		
		if(mapinfo!=null && mapinfo.getSubobjectInfos()!=null)
			this.subobjectinfoswrite = createSubobjectInfosWrite(mapinfo.getSubobjectInfos());
		this.subobjectinfosread = createSubobjectInfosRead(mapinfo==null? null: mapinfo.getSubobjectInfos());
	}
	
	//-------- methods --------
	
	/**
	 *  Get the type info.
	 *  @return The type.
	 */
	public Object getTypeInfo()
	{
		return getObjectInfo()!=null && getObjectInfo().getTypeInfo()!=null ? getObjectInfo().getTypeInfo()
			: mapinfo!=null && mapinfo.getSupertype()!=null ? mapinfo.getSupertype().getTypeInfo() : null;
	}

	/**
	 *  Get the objectinfo.
	 *  @return The objectinfo.
	 */
	public ObjectInfo getObjectInfo()
	{
		return objectinfo!=null ? objectinfo :
			mapinfo!=null && mapinfo.getSupertype()!=null ? mapinfo.getSupertype().getObjectInfo() : null;
	}

	/**
	 *  Get the mapping info.
	 *  @return The mapinfo.
	 */
	public MappingInfo getMappingInfo()
	{
		return mapinfo;
	}

	/**
	 *  Get the linkinfo.
	 *  @return The linkinfo.
	 */
	public LinkingInfo getLinkInfo()
	{
		return linkinfo;
	}

	/**
	 *  Get the supertype.
	 *  @return The super type.
	 */
	public TypeInfo getSupertype()
	{
		return mapinfo!=null? mapinfo.getSupertype(): null;
	}

	/**
	 *  Get the comment info.
	 *  @return The comment
	 */
	public Object getCommentInfo()
	{
		Object cominfo = mapinfo!=null? mapinfo.getCommentInfo(): null;
		return cominfo!=null? cominfo: getSupertype()!=null? getSupertype().getCommentInfo(): null;
	}

	/**
	 *  Get the content info.
	 *  @return The content info.
	 */
	public Object getContentInfo()
	{
		Object coninfo = mapinfo!=null? mapinfo.getContentInfo(): null;
		return coninfo!=null? coninfo: getSupertype()!=null? getSupertype().getContentInfo(): null;
	}
	
	/**
	 *  Get the includefields.
	 *  @return The includefields.
	 */
	public boolean isIncludeFields()
	{
		return mapinfo!=null? mapinfo.getIncludeFields()!=null && mapinfo.getIncludeFields().booleanValue(): false;
	}
	
	/**
	 *  Get the includemethods.
	 *  @return The includemethods.
	 */
	public boolean isIncludeMethods()
	{
		return mapinfo!=null? mapinfo.getIncludeMethods()!=null && mapinfo.getIncludeMethods().booleanValue(): false;
	}
	
	/**
	 *  Get the attribute info.
	 *  @param xmlname The xml name of the attribute.
	 *  @return The attribute info.
	 */
	public Object getAttributeInfo(Object xmlname)
	{
		Object ret = attributeinfos==null? null: attributeinfos.get(xmlname);
		if(ret==null && getSupertype()!=null)
			ret = getSupertype().getAttributeInfo(xmlname);
		return ret;
	}
	
	/**
	 *  Get the xml attribute names.
	 *  @return The attribute names.
	 */
	public Set<Object> getXMLAttributeNames()
	{
		Set<Object> ret = attributeinfos==null? new HashSet<Object>(): new HashSet<Object>(attributeinfos.keySet());
		if(getSupertype()!=null)
			ret.addAll(getSupertype().getXMLAttributeNames());
		return ret;
	}
	
	/**
	 *  Get the attribute infos.
	 *  @return The attribute infos.
	 */
	public Collection<AttributeInfo> getAttributeInfos()
	{
		Collection<AttributeInfo> ret = attributeinfos==null? new HashSet<AttributeInfo>(): new HashSet<AttributeInfo>(attributeinfos.values());
		if(getSupertype()!=null)
			ret.addAll(getSupertype().getAttributeInfos());
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
		IPostProcessor ret = objectinfo!=null? objectinfo.getPostProcessor(): null;
		return ret!=null? ret: getSupertype()!=null? getSupertype().getPostProcessor(): null;
	}
	
	/**
	 *  Get the pre-processor.
	 *  @return The pre-processor
	 */
	public IPreProcessor getPreProcessor()
	{
		IPreProcessor ret = xmlinfo!=null? xmlinfo.getPreProcessor(): null;
		return ret!=null? ret: getSupertype()!=null? getSupertype().getPreProcessor(): null;
	}

	/**
	 *  Get the subobject infos. 
	 *  @return The subobject infos.
	 */
	public Collection<SubobjectInfo> getSubobjectInfos()
	{
		Collection<SubobjectInfo> ret = subobjectinfoswrite!=null? new LinkedHashSet<SubobjectInfo>(subobjectinfoswrite.values()): new LinkedHashSet<SubobjectInfo>();
		if(getSupertype()!=null)
			ret.addAll(getSupertype().getSubobjectInfos());
		return ret;
	}
	
	/**
	 *  Get the subobject infos. 
	 *  @return The subobject info.
	 */
	public SubobjectInfo getSubobjectInfoWrite(Object attr)
	{
		SubobjectInfo ret = subobjectinfoswrite!=null? (SubobjectInfo)subobjectinfoswrite.get(attr): null;
		if(ret==null && getSupertype()!=null)
			ret = getSupertype().getSubobjectInfoWrite(attr);
		return ret;
	}
	
	/**
	 *  Get the most specific subobject info.
	 *  @param tag The tag.
	 *  @param fullpath The full path.
	 *  @return The most specific subobject info.
	 */
	public SubobjectInfo getSubobjectInfoRead(QName tag, QName[] fullpath, Map<String, String> rawattributes)
	{
		SubobjectInfo ret = null;
		
		Set<SubobjectInfo> subobjects = subobjectinfosread!=null? (Set<SubobjectInfo>)subobjectinfosread.get(tag): null;
		ret = findSubobjectInfo(subobjects, fullpath, rawattributes);
		
		return ret;
	}
	
	/**
	 *  Test if object should be created from tag name.
	 */
	public boolean isCreateFromTag()
	{
		return xmlinfo!=null? xmlinfo.isCreateFromTag(): false;
	}
	
	/**
	 *  Get the linker.
	 *  @return The linker.
	 */
	public Object getLinker()
	{
		return linkinfo!=null? linkinfo.getLinker(): mapinfo!=null && mapinfo.getSupertype()!=null ? mapinfo.getSupertype().getLinker() : null;
	}

	/**
	 *  Test if the object should be bulk linked. 
	 */
	public boolean isBulkLink()
	{
		return linkinfo!=null? linkinfo.isBulkLink(): LinkingInfo.DEFAULT_BULKLINK_MODE;
	}
	
	/**
	 *  Find a subobject info.
	 */
	protected SubobjectInfo findSubobjectInfo(Set<SubobjectInfo> soinfos, QName[] fullpath, Map<String, String> rawattributes)
	{
		SubobjectInfo ret = null;
		if(soinfos!=null)
		{
			for(Iterator<SubobjectInfo> it=soinfos.iterator(); ret==null && it.hasNext(); )
			{
				SubobjectInfo si = (SubobjectInfo)it.next();
				QName[] tmp = si.getXMLPathElementsWithoutElement();
				boolean ok = (si.getFilter()==null || si.getFilter().filter(rawattributes)) && 
					(tmp==null || tmp.length<=fullpath.length);
				for(int i=1; ok && tmp!=null && i<=tmp.length; i++)
				{
					ok = tmp[tmp.length-i].equals(fullpath[fullpath.length-i-1]);
				}
				if(ok)
				{
					ret = si;
				}
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
	protected Map<AccessInfo, SubobjectInfo> createSubobjectInfosWrite(SubobjectInfo[] subobjectinfos)
	{
		Map<AccessInfo, SubobjectInfo> ret = new LinkedHashMap<AccessInfo, SubobjectInfo>();
		for(int i=0; i<subobjectinfos.length; i++)
		{
			ret.put(subobjectinfos[i].getAccessInfo(), subobjectinfos[i]);
		}
		return ret;
	}
	
	/**
	 *  Create subobject infos for each tag sorted by specificity.
	 *  @param subobjectinfos The subobject infos.
	 *  @return Map of subobject infos.
	 */
	protected Map<QName, TreeSet<SubobjectInfo>> createSubobjectInfosRead(SubobjectInfo[] subobjectinfos)
	{
		Map<QName, TreeSet<SubobjectInfo>> ret = new HashMap<QName, TreeSet<SubobjectInfo>>();
		
		if(subobjectinfos!=null)
		{
			for(int i=0; i<subobjectinfos.length; i++)
			{
				TreeSet<SubobjectInfo> subobjects = (TreeSet<SubobjectInfo>)ret.get(subobjectinfos[i].getXMLTag());
				if(subobjects==null)
				{
					subobjects = new TreeSet<SubobjectInfo>(new AbstractInfo.SpecificityComparator());
					ret.put(subobjectinfos[i].getXMLTag(), subobjects);
				}
				subobjects.add(subobjectinfos[i]);
			}
		}
		
		if(getSupertype()!=null)
		{
			Collection<SubobjectInfo> soinfos = getSupertype().getSubobjectInfos();
			for(Iterator<SubobjectInfo> it=soinfos.iterator(); it.hasNext(); )
			{
				SubobjectInfo soinfo = it.next();
				TreeSet<SubobjectInfo> subobjects = (TreeSet<SubobjectInfo>)ret.get(soinfo.getXMLTag());
				if(subobjects==null)
				{
					subobjects = new TreeSet<SubobjectInfo>(new AbstractInfo.SpecificityComparator());
					ret.put(soinfo.getXMLTag(), subobjects);
				}
				subobjects.add(soinfo);
			}
		}
		
		return ret;
	}
	
	/**
	 *  Create the attribute info map (xml name(s) -> attribute info).
	 */
	protected Map<Object, AttributeInfo> createAttributeInfos(AttributeInfo[] attributeinfos)
	{
		Map<Object, AttributeInfo> ret = new LinkedHashMap<Object, AttributeInfo>();
		for(int i=0; i<attributeinfos.length; i++)
		{
			QName[] xmlnames = attributeinfos[i].getXMLAttributeNames();
			if(xmlnames!=null)
			{
				List<QName>	key	= new LinkedList<QName>();
				for(int j=xmlnames.length-1; j>=0; j--)
				{
					key.add(0, xmlnames[j]);
				}
				ret.put(key.size()==1 ? key.get(0) : new Tuple(key.toArray()), attributeinfos[i]);
			}
			else
			{
				Object attrid = attributeinfos[i].getAttributeIdentifier();
				if(attrid!=null)
				{
//					System.out.println("Warning, no xml name for attribute:"+attrid);
					ret.put(new QName(attrid.toString()), attributeinfos[i]);
				}
			}
		}
		return ret;
	}

	/**
	 *  Set the reader handler associated to the object type (if any).
	 */
	public void	setReaderHandler(IObjectReaderHandler readerhandler)
	{
		this.readerhandler	= readerhandler;
	}

	/**
	 *  Get the reader handler associated to the object type (if any).
	 */
	public IObjectReaderHandler	getReaderHandler()
	{
		return readerhandler!=null ? readerhandler : getSupertype()!=null ? getSupertype().getReaderHandler() : null;
	}
}
