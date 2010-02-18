package jadex.xml.bean;

import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.xml.AccessInfo;
import jadex.xml.AttributeInfo;
import jadex.xml.BasicTypeConverter;
import jadex.xml.IStringObjectConverter;
import jadex.xml.ISubObjectConverter;
import jadex.xml.ObjectInfo;
import jadex.xml.SXML;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.TypeInfoPathManager;
import jadex.xml.TypeInfoTypeManager;
import jadex.xml.reader.IObjectReaderHandler;
import jadex.xml.reader.LinkData;
import jadex.xml.reader.ReadContext;
import jadex.xml.reader.Reader;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.WeakHashMap;

import javax.xml.namespace.QName;

/**
 *  Handler for reading XML into Java beans.
 */
public class BeanObjectReaderHandler implements IObjectReaderHandler
{
	//-------- constants --------
	
	/** The null object. */
	public static final Object NULL = new Object();
	
	//-------- attributes --------
	
	/** The type info path manager. */
	protected TypeInfoPathManager tipmanager;
	
	/** The type info manager. */
	// For special case that an object is created via the built-in 
	// tag mechanism and there is a type info for that kind of created
	// object. Allows specifying generic type infos with interfaces.
	protected TypeInfoTypeManager titmanager;
	
	/** No type infos. */
	protected Set no_typeinfos;
	
	/** The bean introspector. */
//	protected IBeanIntrospector introspector = new BeanReflectionIntrospector();
	protected IBeanIntrospector introspector = new BeanInfoIntrospector();
	
	// Hack!!!
	protected Map arraycounter;
	
	//-------- constructors --------
	
	/**
	 *  Create a new handler.
	 */
	public BeanObjectReaderHandler(Set typeinfos)
	{
		this.tipmanager = new TypeInfoPathManager(typeinfos);
		this.titmanager = new TypeInfoTypeManager(typeinfos);
	}
	
	//-------- methods --------

	/**
	 *  Get the most specific mapping info.
	 *  @param tag The tag.
	 *  @param fullpath The full path.
	 *  @return The most specific mapping info.
	 */
	public TypeInfo getTypeInfo(QName tag, QName[] fullpath, Map rawattributes)
	{
		return tipmanager.getTypeInfo(tag, fullpath, rawattributes);
	}
	
	/**
	 *  Get the most specific mapping info.
	 *  @param tag The tag.
	 *  @param fullpath The full path.
	 *  @return The most specific mapping info.
	 */
	public TypeInfo getTypeInfo(Object object, QName[] fullpath, ReadContext context)
	{
		Object type = getObjectType(object, context);
		if(no_typeinfos!=null && no_typeinfos.contains(type))
			return null;
			
		TypeInfo ret = titmanager.getTypeInfo(type, fullpath);
		// Hack! due to HashMap.Entry is not visible as class
		if(ret==null)
		{
			if(type instanceof Class)
			{
				// Try if interface or supertype is registered
				List tocheck = new ArrayList();
				tocheck.add(type);
				
				for(int i=0; i<tocheck.size() && ret==null; i++)
				{
					Class clazz = (Class)tocheck.get(i);
					Set tis = titmanager.getTypeInfosByType(clazz);
					ret = titmanager.findTypeInfo(tis, fullpath);
					if(ret==null)
					{
						Class[] interfaces = clazz.getInterfaces();
						for(int j=0; j<interfaces.length; j++)
							tocheck.add(interfaces[j]);
						clazz = clazz.getSuperclass();
						if(clazz!=null)
							tocheck.add(clazz);
					}
				}
				
				// Special case array
				// Requires Object[].class being registered 
				if(ret==null && ((Class)type).isArray())
				{
					ret = titmanager.findTypeInfo(titmanager.getTypeInfosByType(Object[].class), fullpath);
				}
				
				// Add concrete class for same info if it is used
				if(ret!=null)
				{
					ObjectInfo cri =ret.getObjectInfo();
					ObjectInfo cricpy = cri!=null? new ObjectInfo(type, cri.getPostProcessor()): new ObjectInfo(type);
					
					TypeInfo ti = new TypeInfo(ret.getXMLInfo(),
						cricpy, ret.getMappingInfo(), ret.getLinkInfo());
					
//					TypeInfo ti = new TypeInfo(ret.getSupertype(), ret.getXMLPath(), 
//						type, ret.getCommentInfo(), ret.getContentInfo(), 
//						ret.getDeclaredAttributeInfos(), ret.getPostProcessor(), ret.getFilter(), 
//						ret.getDeclaredSubobjectInfos());
					
					titmanager.addTypeInfo(ti);
				}
				else
				{
					if(no_typeinfos==null)
						no_typeinfos = new HashSet();
					no_typeinfos.add(type);
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Create an object for the current tag.
	 *  @param type The object type to create.
	 *  @param root Flag, if object should be root object.
	 *  @param context The context.
	 *  @return The created object (or null for none).
	 */
	public Object createObject(Object type, boolean root, ReadContext context, Map rawattributes) throws Exception
	{
		Object ret = null;
		
		if(type instanceof QName)
		{
			QName tag = (QName)type;
			if(tag.equals(SXML.NULL))
			{	
				ret = NULL;
			}
			else
			{
	//			System.out.println("here: "+typeinfo);
				
				String pck = tag.getNamespaceURI().substring(SXML.PROTOCOL_TYPEINFO.length());
				String clazzname = pck+"."+tag.getLocalPart().replace("-", "$");
//				System.out.println("Clazzname: "+clazzname);
				
				// Special case array
				int idx = clazzname.indexOf("__");
				int[] lens = null;
				if(idx!=-1)
				{
					String strlens = clazzname.substring(idx+2);
					clazzname = clazzname.substring(0, idx);
					StringTokenizer stok = new StringTokenizer(strlens, "_");
					lens =  new int[stok.countTokens()];
					for(int i=0; stok.hasMoreTokens(); i++)
					{
						lens[i] = Integer.parseInt(stok.nextToken());
					}
				}
				
				Class clazz = SReflect.classForName0(clazzname, context.getClassLoader());
				
				if(clazz!=null)
				{
					if(lens!=null)
					{
						ret = Array.newInstance(clazz, lens);
					}
					else if(!BasicTypeConverter.isBuiltInType(clazz))
					{
						// Must have empty constructor.
						ret = clazz.newInstance();
					}
					else if(String.class.equals(clazz))
					{
						ret = Reader.STRING_MARKER;
					}
				}
			}
		}
		else if(type instanceof TypeInfo)
		{
			Object ti =  ((TypeInfo)type).getTypeInfo();
			if(ti instanceof Class && ((Class)ti).isInterface())
			{
				type = ((TypeInfo)type).getXMLTag();
			}
			else
			{
				type = ti;
			}
		}	
		
		if(type instanceof Class)
		{
			Class clazz = (Class)type;
			if(!BasicTypeConverter.isBuiltInType(clazz))
			{
				// Must have empty constructor.
				ret = clazz.newInstance();
			}
		}
		else if(type instanceof IBeanObjectCreator)
		{
			ret = ((IBeanObjectCreator)type).createObject(context, rawattributes);
		}
		
		
		return ret;
	}
	
	/**
	 *  Get the object type
	 *  @param object The object.
	 *  @return The object type.
	 */
	public Object getObjectType(Object object, ReadContext context)
	{
		return object.getClass();
	}
	
	/**
	 *  Convert an object to another type of object.
	 */
	public Object convertContentObject(String value, QName tag, ReadContext context)
	{
		Object ret = value;
		if(tag.getNamespaceURI().startsWith(SXML.PROTOCOL_TYPEINFO))
		{
			String clazzname = tag.getNamespaceURI().substring(SXML.PROTOCOL_TYPEINFO.length())+"."+tag.getLocalPart();
			Class clazz = SReflect.classForName0(clazzname, context.getClassLoader());
			if(clazz!=null)
			{
				if(!BasicTypeConverter.isBuiltInType(clazz))
					throw new RuntimeException("No converter known for: "+clazz);
				ret = BasicTypeConverter.getBasicStringConverter(clazz).convertString(value, context);
			}
		}
		return ret;
	}

	
	/**
	 *  Handle the attribute of an object.
	 *  @param object The object.
	 *  @param attrname The attribute name.
	 *  @param attrval The attribute value.
	 *  @param attrinfo The attribute info.
	 *  @param context The context.
	 */
	public void handleAttributeValue(Object object, QName xmlattrname, List attrpath, String attrval, 
		Object attrinfo, ReadContext context) throws Exception
	{
		// Hack!
		Object converter = attrinfo instanceof AttributeInfo? ((AttributeInfo)attrinfo).getConverter(): null;
		String id = attrinfo instanceof AttributeInfo? ((AttributeInfo)attrinfo).getId(): null;
		Object accessinfo = attrinfo instanceof AttributeInfo? ((AttributeInfo)attrinfo).getAccessInfo(): attrinfo;
		
		if(attrval!=null)
		{
			setElementValue(accessinfo, xmlattrname, object, attrval, converter, id, context);
		}
		else if(accessinfo instanceof AccessInfo && ((AccessInfo)accessinfo).getDefaultValue()!=null)
		{
			setElementValue(accessinfo, xmlattrname, object, ((AccessInfo)accessinfo).getDefaultValue(), converter, id, context);
		}
	}
		

	/**
	 *  Link an object to its parent.
	 *  @param object The object.
	 *  @param parent The parent object.
	 *  @param linkinfo The link info.
	 *  @param tagname The current tagname (for name guessing).
	 *  @param context The context.
	 */
	public void linkObject(Object object, Object parent, Object linkinfo, 
		QName[] pathname, ReadContext context) throws Exception
	{
		QName tag = pathname[pathname.length-1];
		
		// Add object to its parent.
		boolean	linked	= false;
		
		if(linkinfo instanceof SubobjectInfo)
		{
			SubobjectInfo sinfo = (SubobjectInfo)linkinfo;
			setElementValue(sinfo.getAccessInfo(), tag, parent, object, sinfo.getConverter(), null, context);
			linked = true;
		}
		
		// Special case array
		
		else if(parent.getClass().isArray())
		{
			Integer cnt = null;
			if(arraycounter==null)
			{
				arraycounter = new WeakHashMap();
			}
			else
			{
				cnt = (Integer)arraycounter.get(parent);
			}
			if(cnt==null)
				cnt = new Integer(0);
			
			if(!NULL.equals(object))
				Array.set(parent, cnt.intValue(), object);
			
			arraycounter.put(parent, new Integer(cnt.intValue()+1));
			
			linked = true;
		}
		else
		{
			List classes	= new LinkedList();
			classes.add(object.getClass());
			
			String[] plunames = new String[pathname.length];
			String[] sinnames = new String[pathname.length];
			String[] fieldnames = new String[pathname.length];
			for(int i=0; i<pathname.length; i++)
			{
				String name = pathname[i].getLocalPart().substring(0, 1).toUpperCase()+pathname[i].getLocalPart().substring(1);
				plunames[i] = name;
				sinnames[i] = SUtil.getSingular(name);
				fieldnames[i] = pathname[i].getLocalPart();
			}
			
			// Try via fieldname
			
			for(int i=0; i<fieldnames.length && !linked; i++)
			{
				linked = setField(fieldnames[i], parent, object, null, context, null);
			}
			
			// Try name guessing via class/superclass/interface names of object to add
			while(!linked && !classes.isEmpty())
			{
				Class clazz = (Class)classes.remove(0);
				
				for(int i=0; i<plunames.length && !linked; i++)
				{
					linked = internalLinkObjects(clazz, "set"+plunames[i], object, parent, context);
					if(!linked)
					{
						linked = internalLinkObjects(clazz, "add"+sinnames[i], object, parent, context);
						if(!linked && !sinnames[i].equals(plunames[i]))
						{	
							linked = internalLinkObjects(clazz, "add"+plunames[i], object, parent, context);
						}
					}
				}
				
				// Try classname of object to add
				if(!linked && !BasicTypeConverter.isBuiltInType(clazz))
				{
					String name = SReflect.getInnerClassName(clazz);
					linked = internalLinkObjects(clazz, "set"+name, object, parent, context);
					if(!linked)
					{
						linked = internalLinkObjects(clazz, "add"+name, object, parent, context);
						if(!linked)
						{
							String sinname = SUtil.getSingular(name);
							if(!name.equals(sinname))
								linked = internalLinkObjects(clazz, "add"+sinname, object, parent, context);
						}
					}
				}
				
				if(!linked)
				{
					if(clazz.getSuperclass()!=null)
						classes.add(clazz.getSuperclass());
					Class[]	ifs	= clazz.getInterfaces();
					for(int i=0; i<ifs.length; i++)
					{
						classes.add(ifs[i]);
					}
				}
			}
		}
		
		if(!linked)
			throw new RuntimeException("Could not link: "+object+" "+parent);
	}
	
	/**
	 *  Link an object to its parent.
	 *  @param object The object.
	 *  @param parent The parent object.
	 *  @param linkinfo The link info.
	 *  @param tagname The current tagname (for name guessing).
	 *  @param context The context.
	 */
	public void bulkLinkObjects(List childs, Object parent, Object linkinfo, 
		QName[] pathname, ReadContext context) throws Exception
	{
		QName tag = pathname[pathname.length-1];
		
		// Add object to its parent.
		boolean	linked	= false;
		
		if(linkinfo!=null)
		{
			// converter and id null?!
			setBulkAttributeValues(linkinfo, tag, parent, childs, null, null, context);
			linked = true;
		}
		
		// Special case array
		else if(parent.getClass().isArray())
		{
			Integer cnt = null;
			if(arraycounter==null)
			{
				arraycounter = new WeakHashMap();
			}
			else
			{
				cnt = (Integer)arraycounter.get(parent);
			}
			if(cnt==null)
				cnt = new Integer(0);
			
			for(int i=0; i<childs.size(); i++)
			{
				Object object = childs.get(i);
				if(!NULL.equals(object))
					Array.set(parent, cnt.intValue()+i, object);
			}
			
			arraycounter.put(parent, new Integer(cnt.intValue()+childs.size()));
			
			linked = true;
		}
		
		// Try linking via field/method searching by name guessing.
		else
		{
			List classes	= new LinkedList();
			classes.add(childs.get(0).getClass());
			
			String[] orignames = new String[pathname.length];
			String[] plunames = new String[pathname.length];
			String[] origfieldnames = new String[pathname.length];
			String[] plufieldnames = new String[pathname.length];
			for(int i=0; i<pathname.length; i++)
			{
				String origname = pathname[i].getLocalPart();
				String pluname =  SUtil.getPlural(pathname[i].getLocalPart());
				plunames[i] = pluname.substring(0, 1).toUpperCase()+pluname.substring(1);
				orignames[i] = origname.substring(0, 1).toUpperCase()+origname.substring(1);
				plufieldnames[i] = pluname;
				origfieldnames[i] = origname;
			}
			
			// Try via fieldname
			for(int i=0; i<plufieldnames.length && !linked; i++)
			{
				linked = setBulkField(plufieldnames[i], parent, childs, null, context, null);
				if(!linked && !origfieldnames[i].equals(plufieldnames[i]))
				{
					linked = setBulkField(origfieldnames[i], parent, childs, null, context, null);
				}
			}
			
			// Try name guessing via class/superclass/interface names of object to add
			while(!linked && !classes.isEmpty())
			{
				Class clazz = (Class)classes.remove(0);
				
				for(int i=0; i<plunames.length && !linked; i++)
				{
					linked = internalBulkLinkObjects(clazz, "set"+plunames[i], childs, parent, context);
					if(!linked && !orignames[i].equals(plunames[i]))
					{
						linked = internalBulkLinkObjects(clazz, "set"+orignames[i], childs, parent, context);
					}
				}
				
				// Try classname of object to add
				if(!linked && !BasicTypeConverter.isBuiltInType(clazz))
				{
					String name = SReflect.getInnerClassName(clazz);
					linked = internalBulkLinkObjects(clazz, "set"+name, childs, parent, context);
				}
				
				if(!linked)
				{
					if(clazz.getSuperclass()!=null)
						classes.add(clazz.getSuperclass());
					Class[]	ifs	= clazz.getInterfaces();
					for(int i=0; i<ifs.length; i++)
					{
						classes.add(ifs[i]);
					}
				}
			}
		}
		
		if(!linked)
			throw new RuntimeException("Could not bulk link: "+childs+" "+parent);
	}
	
	/**
	 *  Bulk link an object to its parent.
	 *  @param parent The parent object.
	 *  @param children The children objects (link datas).
	 *  @param context The context.
	 *  @param classloader The classloader.
	 *  @param root The root object.
	 * /
	public void bulkLinkObjects(Object parent, List children, Object context, 
		ClassLoader classloader, Object root) throws Exception
	{
//		System.out.println("bulk link for: "+parent+" "+children);
		for(int i=0; i<children.size(); i++)
		{
			LinkData linkdata = (LinkData)children.get(i);
			
			linkObject(linkdata.getChild(), parent, linkdata.getLinkinfo(), 
				linkdata.getPathname(), context, classloader, root);
		}
	}*/
	
	/**
	 *  Bulk link chilren to its parent.
	 *  @param parent The parent object.
	 *  @param children The children objects (link datas).
	 *  @param context The context.
	 *  @param classloader The classloader.
	 *  @param root The root object.
	 */
	public void bulkLinkObjects(Object parent, List children, ReadContext context) throws Exception
	{
//		System.out.println("bulk link for: "+parent+" "+children);
		
		// The default bulk strategy is as follows:
		// Linear scan the subpaths(tags) of the parent
		// As long as the path is the same remember as bulk
		// Whenever a new path/tag is used the last bulk is considered finished

		LinkData linkdata = (LinkData)children.get(0);
		List childs = new ArrayList();
		childs.add(linkdata.getChild());
		QName[] pathname = linkdata.getPathname();
		int startidx = 0;
		for(int i=1; i<children.size(); i++)
		{
			LinkData ld = (LinkData)children.get(i);
			QName[] pn = ld.getPathname();
			if(!Arrays.equals(pathname, pn))
			{
				handleBulkLinking(childs, parent, context, pathname, children, startidx);
				
				pathname = pn;
				linkdata = ld;
				childs.clear();
				startidx = i;
			}
			childs.add(ld.getChild());
		}
		handleBulkLinking(childs, parent, context, pathname, children, startidx);
		
	}
	
	/**
	 *  Initiate the bulk link calls.
	 */
	protected void handleBulkLinking(List childs, Object parent, ReadContext context, QName[] pathname, List linkdatas, int startidx) throws Exception
	{
		if(childs.size()>1)
		{
			try
			{
				bulkLinkObjects(childs, parent, ((LinkData)linkdatas.get(startidx)).getLinkinfo(), 
					pathname, context);
			}
			catch(Exception e)
			{
				System.out.println("Warning. Bulk link initiated but not successful: "+childs+" "+parent+" "+e);
			
				for(int i=0; i<childs.size(); i++)
				{
					linkObject(childs.get(i), parent, ((LinkData)linkdatas.get(startidx+i)).getLinkinfo(), 
						pathname, context);
				}
			}
		}
		else
		{
			linkObject(childs.get(0), parent, ((LinkData)linkdatas.get(startidx)).getLinkinfo(), 
				pathname, context);
		}
	}
	
	//-------- helper methods --------
	
	/**
	 *  Set an attribute value.
	 *  Similar to handleAttributValue but allows objects as attribute values (for linking).
	 *  @param attrinfo The attribute info.
	 *  @param xmlattrname The xml attribute name.
	 *  @param object The object.
	 *  @param val The attribute value.
	 *  @param root The root object.
	 *  @param classloader The classloader.
	 */
	protected void setElementValue(Object accessinfo, QName xmlname, Object object, Object val, Object converter, String id, ReadContext context)
	{
		if(NULL.equals(val))
			return;
		
		boolean set = false;
		
		// Write to a map.
		if(accessinfo instanceof AccessInfo && ((AccessInfo)accessinfo).getExtraInfo() instanceof BeanAccessInfo)
		{	
			AccessInfo ai = (AccessInfo)accessinfo;
			BeanAccessInfo bai = (BeanAccessInfo)ai.getExtraInfo();
			
			// Put value in map 1) fetch key 2) set value in map
			if(bai.getMapName()!=null)
			{
				String mapname = bai.getMapName().length()==0? bai.getMapName(): bai.getMapName().substring(0,1).toUpperCase()+bai.getMapName().substring(1);
//				String jattrname = bai.getAttributeName()!=null? bai.getAttributeName(): xmlattrname;
				
				// fetch the key value
				Object key = null;
				if(bai.getKeyHelp()!=null)
				{
					Object kh = bai.getKeyHelp();
					Object targetobj = bai.isKeyFromParent()? object: val; 
					
					if(kh instanceof Method)
					{
						try
						{
							key = ((Method)kh).invoke(targetobj, new Object[0]);
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
					else if(kh instanceof Field)
					{
						try
						{
							key = ((Field)kh).get(targetobj);
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
					else
					{
						throw new RuntimeException("Unknown key help: "+kh); 
					}
				}
				else
				{
					key =  ai.getObjectIdentifier()!=null? ai.getObjectIdentifier(): xmlname;
				}
				
				// Set map value with predefined read method or field.
				if(bai.getStoreHelp()!=null)
				{
					Object sh = bai.getStoreHelp();
										
					if(sh instanceof Method)
					{
						try
						{
							Method m = (Method)sh;
							Class[] ps = m.getParameterTypes();
							Object arg = convertValue(val, ps[1], converter, context, id);
							m.invoke(object, new Object[]{key, arg});
							set = true;
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
					else if(sh instanceof Field)
					{
						try
						{
							Field f = (Field)sh;
							Object map = f.get(object);
							// Hack?! create on demand, should be customizable.
							if(map==null)
							{
								map = new HashMap();
								f.set(object, map);
							}
							Object arg = convertValue(val, null, converter, context, id);
							((Map)map).put(key, arg);
							set = true;
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
						
					}
					else
					{
						throw new RuntimeException("Unknown map store help: "+sh);
					}
				}
				// Set map value with guessing method name.
				else
				{
					String[] prefixes = new String[]{"put", "set", "add"};
					for(int i=0; i<prefixes.length && !set; i++)
					{
						Method[] ms = SReflect.getMethods(object.getClass(), prefixes[i]+mapname);
						for(int j=0; j<ms.length && !set; j++)
						{
							Class[] ps = ms[j].getParameterTypes();
							if(ps.length==2)
							{
								Object arg = convertValue(val, ps[1], converter, context, id);
								
								try
								{
									ms[j].invoke(object, new Object[]{key, arg});
									set = true;
								}
								catch(Exception e)
								{
									e.printStackTrace();
								}
							}
						}
					}
				}
			}
			
			// Fetch value using predefined read method.
			else if(bai.getStoreHelp()!=null)
			{
				Object sh = bai.getStoreHelp();
				
				if(sh instanceof Method)
				{
					Method m = (Method)sh;
					Class[] ps = m.getParameterTypes();
					if(ps.length==1)
					{
						Object arg = convertValue(val, ps[0], converter, context, id);
						try
						{
							m.invoke(object, new Object[]{arg});
							set = true;
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
					else
					{
						throw new RuntimeException("Read method should have one parameter: "+bai+" "+m);
					}
				}
				else if(sh instanceof Field)
				{
					try
					{
						Field f = (Field)sh;
						Object arg = convertValue(val, f.getType(), converter, context, id);
						f.set(object, arg);
						set = true;
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					throw new RuntimeException("Unknown store help: "+sh);
				}
			}
		}
	
		// Try using object identifier from access info
		if(!set && accessinfo instanceof AccessInfo)
		{
			try
			{
			AccessInfo ai = (AccessInfo)accessinfo;
			
			String fieldname = ai.getObjectIdentifier()!=null? ((String)ai.getObjectIdentifier()): xmlname.getLocalPart();
			set = setField(fieldname, object, val, converter, context, id);

			if(!set)
			{
				String postfix = ai.getObjectIdentifier()!=null? ((String)ai.getObjectIdentifier())
					.substring(0,1).toUpperCase()+((String)ai.getObjectIdentifier()).substring(1)
					: xmlname.getLocalPart().substring(0,1).toUpperCase()+xmlname.getLocalPart().substring(1);
					
				set = invokeSetMethod(new String[]{"set", "add"}, postfix, val, object, context, converter, id);
			
				if(!set)
				{
					String oldpostfix = postfix;
					postfix = SUtil.getSingular(postfix);
					if(!postfix.equals(oldpostfix))
					{
						// First try add, as set might also be there and used for a non-multi attribute.
						set = invokeSetMethod(new String[]{"set", "add"}, postfix, val, object, context, converter, id);
					}
				}
			}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		else if(!set) // attribute info is null or string
		{
			// Write as normal bean attribute.
			
			// Try to find bean class information
			
			Map props = introspector.getBeanProperties(object.getClass(), true);
			BeanProperty prop = (BeanProperty)props.get(accessinfo instanceof String? accessinfo: xmlname.getLocalPart());
			if(prop!=null)
			{
				Object arg = convertValue(val, prop.getSetterType(), converter, context, id);

				try
				{
					if(prop.getSetter()!=null)
						prop.getSetter().invoke(object, new Object[]{arg});
					else
						prop.getField().set(object, arg);
					set = true;
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
			if(!set)
			{
				String postfix = accessinfo instanceof String? ((String)accessinfo).substring(0,1).toUpperCase()+((String)accessinfo).substring(1)
					: xmlname.getLocalPart().substring(0,1).toUpperCase()+xmlname.getLocalPart().substring(1);
				set = invokeSetMethod(new String[]{"set", "add"}, postfix, val, object, context, null, null);
			
				if(!set)
				{
					String oldpostfix = postfix;
					postfix = SUtil.getSingular(postfix);
					if(!postfix.equals(oldpostfix))
					{
						set = invokeSetMethod(new String[]{"set", "add"}, postfix, val, object, context, null, null);
					}
				}
			}
		}
		
		if(!set)
			throw new RuntimeException("Failure in setting attribute: "+xmlname+" on object: "+object);
	}
	
	/**
	 *  Set an attribute value.
	 *  Similar to handleAttributValue but allows objects as attribute values (for linking).
	 *  @param attrinfo The attribute info.
	 *  @param xmlattrname The xml attribute name.
	 *  @param object The object.
	 *  @param attrval The attribute value.
	 *  @param root The root object.
	 *  @param classloader The classloader.
	 */
	protected void setBulkAttributeValues(Object accessinfo, QName xmlattrname, Object object, 
		List vals, Object converter, String id, ReadContext context)
	{
		boolean set = false;
		
		// Write to a map.
		if(accessinfo instanceof AccessInfo && ((AccessInfo)accessinfo).getExtraInfo() instanceof BeanAccessInfo)
		{	
			AccessInfo ai = (AccessInfo)accessinfo;
			BeanAccessInfo bai = (BeanAccessInfo)ai.getExtraInfo();
					
			// todo: support map?
			
			// Fetch value using predefined read method.
			if(bai.getStoreHelp()!=null)
			{
				Object sh = bai.getStoreHelp();
				
				if(sh instanceof Method)
				{
					Method m = (Method)sh;
					Class[] ps = m.getParameterTypes();
					if(ps.length==1)
					{
						Object arg = convertBulkValues(vals, ps[0], converter, context, id);
						try
						{
							m.invoke(object, new Object[]{arg});
							set = true;
						}
						catch(Exception e)
						{
							e.printStackTrace();
						}
					}
					else
					{
						throw new RuntimeException("Read method should have one parameter: "+bai+" "+m);
					}
				}
				else if(sh instanceof Field)
				{
					try
					{
						Field f = (Field)sh;
						Object arg = convertBulkValues(vals, f.getType(), converter, context, id);
						f.set(object, arg);
						set = true;
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				else
				{
					throw new RuntimeException("Unknown store help: "+sh);
				}
			}
		}
	
		// Try 
		if(!set && accessinfo instanceof AccessInfo)
		{
			AccessInfo ai = (AccessInfo)accessinfo;
			
			String fieldname = ai.getObjectIdentifier()!=null? ((String)ai.getObjectIdentifier()): xmlattrname.getLocalPart();
			set = setBulkField(fieldname, object, vals, converter, context, id);

			if(!set)
			{
				String postfix = ai.getObjectIdentifier()!=null? ((String)ai.getObjectIdentifier())
					.substring(0,1).toUpperCase()+((String)ai.getObjectIdentifier()).substring(1)
					: xmlattrname.getLocalPart().substring(0,1).toUpperCase()+xmlattrname.getLocalPart().substring(1);
					
				set = invokeBulkSetMethod(new String[]{"set"}, postfix, vals, object, context, converter, id);
			}
		}
		else if(!set) // attribute info is null or string
		{
			// Write as normal bean attribute.
			
			// Try to find bean class information
			
			Map props = introspector.getBeanProperties(object.getClass(), true);
			BeanProperty prop = (BeanProperty)props.get(accessinfo instanceof String? accessinfo: xmlattrname.getLocalPart());
			if(prop!=null)
			{
				Object arg = convertBulkValues(vals, prop.getSetterType(), null, context, null);

				try
				{
					if(prop.getSetter()!=null)
						prop.getSetter().invoke(object, new Object[]{arg});
					else
						prop.getField().set(object, arg);
					set = true;
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			
			if(!set)
			{
				String postfix = accessinfo instanceof String? ((String)accessinfo).substring(0,1).toUpperCase()+((String)accessinfo).substring(1)
					: xmlattrname.getLocalPart().substring(0,1).toUpperCase()+xmlattrname.getLocalPart().substring(1);
				set = invokeBulkSetMethod(new String[]{"set"}, postfix, vals, object, context, null, null);
			}
		}
		
		if(!set)
			throw new RuntimeException("Failure in setting bulk values: "+xmlattrname+" on object: "+object);
	}
	
	/**
	 *  Set a value directly on a Java bean.
	 *  @param prefixes The method prefixes.
	 *  @param postfix The mothod postfix.
	 *  @param value The attribute value.
	 *  @param object The object.
	 *  @param root The root.
	 *  @param classloader The classloader.
	 *  @param converter The converter.
	 */
	protected boolean invokeSetMethod(String[] prefixes, String postfix, Object value, Object object, 
		ReadContext context, Object converter, String idref)
	{
		boolean set = false;
				
		for(int i=0; i<prefixes.length && !set; i++)
		{
			try
			{
				Method[] ms = SReflect.getMethods(object.getClass(), prefixes[i]+postfix);
				
				for(int j=0; j<ms.length && !set; j++)
				{
					Class[] ps = ms[j].getParameterTypes();
					if(ps.length==1)
					{
						Object arg = convertValue(value, ps[0], converter, context, idref);
						
						try
						{
							ms[j].invoke(object, new Object[]{arg});
							set = true;
						}
						catch(Exception e)
						{
						}
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return set;
	}
	
	/**
	 *  Set a value directly on a Java bean.
	 *  @param prefixes The method prefixes.
	 *  @param postfix The mothod postfix.
	 *  @param attrval The attribute value.
	 *  @param object The object.
	 *  @param root The root.
	 *  @param classloader The classloader.
	 *  @param converter The converter.
	 */
	protected boolean invokeBulkSetMethod(String[] prefixes, String postfix, List vals, Object object, 
		ReadContext context, Object converter, String idref)
	{
		boolean set = false;
				
		for(int i=0; i<prefixes.length && !set; i++)
		{
			try
			{
				Method[] ms = SReflect.getMethods(object.getClass(), prefixes[i]+postfix);
				
				for(int j=0; j<ms.length && !set; j++)
				{
					Class[] ps = ms[j].getParameterTypes();
					if(ps.length==1)
					{
						Object arg = convertBulkValues(vals, ps[0], converter, context, idref);
						
						try
						{
							ms[j].invoke(object, new Object[]{arg});
							set = true;
						}
						catch(Exception e)
						{
						}
					}
				}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		
		return set;
	}
	
	/**
	 *  Directly access a field for setting/(adding) the object.
	 */
	protected boolean setField(String fieldname, Object parent, Object object, Object converter, 
		ReadContext context, String idref)
	{
		boolean set = false;
		try
		{
			Field field = parent.getClass().getField(fieldname);
			Class type = field.getType();
			
			Object val = object;
			val = convertValue(object, type, converter, context, idref);
			
			if(SReflect.isSupertype(type, val.getClass()))
			{
				field.set(parent, val);
				set = true;
			}
		}
		catch(Exception e)
		{
		}
		
		return set;
	}
	
	/**
	 *  Directly access a field for setting the objects.
	 */
	protected boolean setBulkField(String fieldname, Object parent, List objects, Object converter,
		ReadContext context, String idref)
	{
		boolean set = false;
		try
		{
			Field field = parent.getClass().getField(fieldname);
			Class type = field.getType();
			
//			object = convertAttributeValue(object, type, converter, root, classloader, idref, readobjects);
			
			Object arg = convertBulkValues(objects, type, converter, context, idref);
			
			field.set(parent, arg);
			set = true;
		}
		catch(Exception e)
		{
		}
		
		return set;
	}
	
	/**
	 *  Internal link objects method.
	 *  @param clazz The clazz.
	 *  @param name The name.
	 *  @param object The object.
	 *  @param parent The parent.
	 *  @param root The root.
	 *  @param classloader classloader.
	 */
	protected boolean internalLinkObjects(Class clazz, String name, Object object, 
		Object parent, ReadContext context) throws Exception
	{
		boolean ret = false;
			
		Method[] ms = SReflect.getMethods(parent.getClass(), name);
		for(int i=0; !ret && i<ms.length; i++)
		{
			Class[] ps = ms[i].getParameterTypes();
			if(ps.length==1)
			{
				if(SReflect.getWrappedType(ps[0]).isAssignableFrom(clazz))
				{
					try
					{
						ms[i].invoke(parent, new Object[]{object});
						ret	= true;
					}
					catch(Exception e)
					{
//						e.printStackTrace();
					}
				}
				else if(object instanceof String)
				{
					IStringObjectConverter converter = BasicTypeConverter.getBasicStringConverter(ps[0]);
					if(converter != null)
					{
						try
						{
							object = converter.convertString((String)object, context);
							ms[i].invoke(parent, new Object[]{object});
							ret	= true;
						}
						catch(Exception e)
						{
						}
					}
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Internal bulk link objects method.
	 *  @param clazz The clazz.
	 *  @param name The name.
	 *  @param object The object.
	 *  @param parent The parent.
	 *  @param root The root.
	 *  @param classloader classloader.
	 */
	protected boolean internalBulkLinkObjects(Class clazz, String name, List childs, 
		Object parent, ReadContext context) throws Exception
	{
		boolean ret = false;
			
		Method[] ms = SReflect.getMethods(parent.getClass(), name);
		for(int i=0; !ret && i<ms.length; i++)
		{
			Class[] ps = ms[i].getParameterTypes();
			if(ps.length==1)
			{
				try
				{
					Object arg = convertBulkValues(childs, ps[0], null, context, null);
					ms[i].invoke(parent, new Object[]{arg});
					ret	= true;
				}	
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Convert a value by using a converter.
	 *  @param val The attribute value.
	 *  @param targetcalss The target class.
	 *  @param converter The converter.
	 *  @param root The root.
	 *  @param classloader The classloader.
	 */
	protected Object convertValue(Object val, Class targetclass, Object converter, 
		ReadContext context, String id)
	{
		Object ret = val;

		// When 'id' is idref interpret value as key for stored object.
		if(AttributeInfo.IDREF.equals(id))
		{
			ret = context.getReadObjects().get(val);
		}
		else if(converter instanceof ISubObjectConverter)
		{
			ret = ((ISubObjectConverter)converter).convertObjectForRead(val, context);
		}
		// If a string converter is available
		else if(val instanceof String)
		{
			if(converter instanceof IStringObjectConverter)
			{
				ret = ((IStringObjectConverter)converter).convertString((String)val, context);
			}
			else if(!String.class.isAssignableFrom(targetclass))
			{
				IStringObjectConverter conv = BasicTypeConverter.getBasicStringConverter(targetclass);
				if(conv!=null)
					ret = conv.convertString((String)val, context);
			}
		}
		
		
		return ret;
	}
	
	/**
	 *  Convert a list of values into the target format (list, set, collection, array).
	 */
	protected Object convertBulkValues(List vals, Class targetclass, Object converter, 
		ReadContext context, String id)
	{
		// todo: use converter?!
		
		Object ret = vals;
//		object = convertAttributeValue(object, type, converter, root, classloader, idref, readobjects);
			
		if(SReflect.isSupertype(Set.class, targetclass))
		{
			ret = new HashSet(vals);
		}
		else if(targetclass.isArray())
		{
//			if(SReflect.isSupertype(type.getComponentType(), clazz))
			{
				ret = Array.newInstance(targetclass.getComponentType(), vals.size());
				for(int j=0; j<vals.size(); j++)
				{
					Array.set(ret, j, vals.get(j));
				}
			}
		}
		else if(SReflect.isSupertype(Collection.class, targetclass))
		{
			// When collection list is ok.
		}
		else
		{
			throw new RuntimeException("Converion to target no possible: "+targetclass+" "+vals);
		}
		
		return ret;
	}
}
