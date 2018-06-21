package jadex.xml.bean;

import java.lang.reflect.Array;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import jadex.commons.IFilter;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.transformation.BasicTypeConverter;
import jadex.commons.transformation.BeanIntrospectorFactory;
import jadex.commons.transformation.IStringObjectConverter;
import jadex.commons.transformation.STransformation;
import jadex.commons.transformation.annotations.Classname;
import jadex.commons.transformation.traverser.BeanProperty;
import jadex.commons.transformation.traverser.IBeanIntrospector;
import jadex.xml.AccessInfo;
import jadex.xml.AttributeInfo;
import jadex.xml.IPostProcessor;
import jadex.xml.IReturnValueCommand;
import jadex.xml.ISubObjectConverter;
import jadex.xml.ObjectInfo;
import jadex.xml.SXML;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.TypeInfoTypeManager;
import jadex.xml.reader.AReadContext;
import jadex.xml.reader.AReader;
import jadex.xml.reader.IObjectReaderHandler;
import jadex.xml.reader.LinkData;
import jadex.xml.stax.QName;

/**
 *  Handler for reading XML into Java beans.
 */
// Todo: report warnings when method invocations fail?
public class BeanObjectReaderHandler implements IObjectReaderHandler
{
	//-------- attributes --------
	
	/** The type info manager. */
	// For special case that an object is created via the built-in 
	// tag mechanism and there is a type info for that kind of created
	// object. Allows specifying generic type infos with interfaces.
	protected TypeInfoTypeManager titmanager;
	
	/** No type infos. */
	protected Set<Object> no_typeinfos;
	
	/** The bean introspector. */
	protected IBeanIntrospector introspector = BeanIntrospectorFactory.getInstance().getBeanIntrospector();
	
	/** The filter based post processors. */
	protected Map<IFilter<Object>, IPostProcessor> postprocessors;
	
	//-------- constructors --------
	
	/**
	 *  Create a new handler for custom XML formats.
	 */
	public BeanObjectReaderHandler()
	{
	}
	
	/**
	 *  Create a new handler for Java XML supporting on-the-fly
	 *  type info creation for arrays.
	 */
	public BeanObjectReaderHandler(Set<TypeInfo> typeinfos)
	{
		this.titmanager = new TypeInfoTypeManager(typeinfos);
	}
	
	//-------- methods --------

	/**
	 *  Get the most specific mapping info.
	 *  @param tag The tag.
	 *  @param fullpath The full path.
	 *  @return The most specific mapping info.
	 */
	public synchronized TypeInfo getTypeInfo(Object object, QName[] fullpath, AReadContext context)
	{
		TypeInfo	ret	= null;
		if(titmanager!=null)
		{
			Object type = getObjectType(object, context);
			if(no_typeinfos==null || !no_typeinfos.contains(type))
			{
				ret = titmanager.getTypeInfo(type, fullpath);
				// Hack! due to HashMap.Entry is not visible as class
				if(ret==null)
				{
					if(type instanceof Class)
					{
						// Try if interface or supertype is registered
						List<Class<?>> tocheck = new ArrayList<Class<?>>();
						tocheck.add((Class<?>)type);
						
						for(int i=0; i<tocheck.size() && ret==null; i++)
						{
							Class<?> clazz = tocheck.get(i);
		//					Set tis = titmanager.getTypeInfosByType(clazz);
		//					ret = titmanager.findTypeInfo(tis, fullpath);
							ret = titmanager.getTypeInfo(clazz, fullpath);
							if(ret==null)
							{
								Class<?>[] interfaces = clazz.getInterfaces();
								for(int j=0; j<interfaces.length; j++)
									tocheck.add(interfaces[j]);
								clazz = clazz.getSuperclass();
								if(clazz!=null)
									tocheck.add(clazz);
							}
						}
						
						// Special case array
						// Requires Object[].class being registered 
						if(ret==null && ((Class<?>)type).isArray())
						{
		//					ret = titmanager.findTypeInfo(titmanager.getTypeInfosByType(Object[].class), fullpath);
							ret = titmanager.getTypeInfo(Object[].class, fullpath);
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
								no_typeinfos = new HashSet<Object>();
							no_typeinfos.add(type);
						}
					}
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
	public Object createObject(Object type, boolean root, AReadContext context, Map<String, String> rawattributes) throws Exception
	{
		Object ret = null;
		
		if(type instanceof QName)
		{
			QName tag = (QName)type;
			if(tag.equals(SXML.NULL))
			{	
				ret = AReader.NULL;
			}
			else
			{
	//			System.out.println("here: "+typeinfo);
				
				String pck = tag.getNamespaceURI().substring(SXML.PROTOCOL_TYPEINFO.length());
				String clazzname = pck.length()>0? pck+"."+tag.getLocalPart().replace("-", "$"): tag.getLocalPart().replace("-", "$");
//				System.out.println("Clazzname: "+clazzname);
	
//				if(clazzname.indexOf("jadex.bridge.Cause")!=-1)
//					System.out.println("hererer");
				
				// Special case array
				int idx = clazzname.indexOf("__");
				int dim = 0;
				int len = 0;
				if(idx!=-1)
				{
					String strlens = clazzname.substring(idx+2);
					clazzname = clazzname.substring(0, idx);
					clazzname	= STransformation.getClassname(clazzname);
					StringTokenizer stok = new StringTokenizer(strlens, "__");
					dim = Integer.parseInt(stok.nextToken());	
					for(int i=0; i<dim-1; i++)
						clazzname+="[]";
					len = Integer.parseInt((String)rawattributes.get(SXML.ARRAYLEN));
				}
				else
				{
					clazzname	= STransformation.getClassname(clazzname);
				}
				
				Class<?> clazz = SReflect.classForName(clazzname, context.getClassLoader());
				
				if(dim>0)
				{
					ret = Array.newInstance(clazz, len);
				}
				else if(!BasicTypeConverter.isBuiltInType(clazz))
				{
					if(clazz.isAnonymousClass())
					{
						// Create anonymous class object by supplying null values
//							System.out.println("Anonymous: "+clazz);
						
						// Problem: 
						clazz = getCorrectAnonymousInnerClass(clazz, rawattributes, context.getClassLoader());
							
						if(clazz!=null)
						{
							try
							{
								Constructor<?>	c	= clazz.getDeclaredConstructors()[0];
								c.setAccessible(true);
								Class<?>[] paramtypes = c.getParameterTypes();
								Object[] paramvalues = new Object[paramtypes.length];
								for(int i=0; i<paramtypes.length; i++)
								{
									if(paramtypes[i].equals(boolean.class))
									{
										paramvalues[i] = Boolean.FALSE;
									}
									else if(SReflect.isBasicType(paramtypes[i]))
									{
										paramvalues[i] = 0;
									}
								}
								
								ret	= c.newInstance(paramvalues);
							}
							catch(Exception e)
							{
								e.printStackTrace();
							}
						}
						else
						{
							context.getReporter().report("Anonymous class problem.", "Creation problem.", context, context.getLocation());
						}
					}
					else
					{
						// Must have empty constructor.
						// Allow non-public bean constructors
						Constructor<?>	c	= clazz.getDeclaredConstructor();
						if(!Modifier.isPublic(c.getModifiers()) || !Modifier.isPublic(clazz.getModifiers()))
						{
							c.setAccessible(true);
						}
						ret = c.newInstance();
					}
				}
				else if(String.class.equals(clazz))
				{
					ret = AReader.STRING_MARKER;
				}
			}
		}
		else if(type instanceof TypeInfo)
		{
			Object ti =  ((TypeInfo)type).getTypeInfo();
			if(ti instanceof Class && ((Class<?>)ti).isInterface())
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
			Class<?> clazz = (Class<?>)type;
			if(!BasicTypeConverter.isBuiltInType(clazz))
			{
				// Must have empty constructor.
				try
				{
					ret = clazz.newInstance();
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		else if(type instanceof IBeanObjectCreator)
		{
			ret = ((IBeanObjectCreator)type).createObject(context, rawattributes);
		}
		
		
		return ret;
	}
	
	/**
	 *  Bug with Java compilers that enumerate anonymous inner classes as they like. 
	 */
	protected Class<?> getCorrectAnonymousInnerClass(Class<?> clazz, Map<String, String> rawattributes, ClassLoader classloader)
	{
		Class<?> ret = isCorrectAnonymousInnerClass(clazz, rawattributes)? clazz: null;
		
		if(ret==null)
		{
			String name = clazz.getName();
			int	idx	= name.lastIndexOf('$');
			String start = name.substring(0, idx+1);
			String end = name.substring(idx+1);
			int num = Integer.parseInt(end);
			for(int i=1; ret==null; i++)
			{
				if(i!=num)
				{
					String clazzname = start+Integer.toString(i);
					clazz = SReflect.classForName0(clazzname, classloader);
					if(clazz==null)
						break; // Break as soon as no further inner class could be found anymore.
					if(isCorrectAnonymousInnerClass(clazz, rawattributes))
						ret = clazz;
				}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Test if a class is the correct inner class.
	 */
	protected boolean isCorrectAnonymousInnerClass(Class<?> clazz, Map<String, String> rawattributes)
	{
		boolean ret = true;
		
		String rawclname = (String)rawattributes.get(SXML.XML_CLASSNAME);
		if(rawclname!=null)
		{
			try
			{
				Field f = clazz.getField(SXML.XML_CLASSNAME);
				f.setAccessible(true);
				String clname = (String)f.get(null);
				ret = rawclname.equals(clname);
			}
//			catch(NoSuchFieldException e)
//			{
//				// no class field declared
//			}
			catch(Exception e)
			{
				ret = false;
			}
			
			if(!ret)
			{
            	Classname xmlc = SXML.getXMLClassnameAnnotation(clazz);
            	if(xmlc!=null)
            	{
    				ret = rawclname.equals(xmlc.value());	
            	}
			}
		}
		
		return ret;
	}
	
	/**
	 *  Get the object type
	 *  @param object The object.
	 *  @return The object type.
	 */
	public Object getObjectType(Object object, AReadContext context)
	{
		return object.getClass();
	}
	
	/**
	 *  Convert an object to another type of object.
	 */
	public Object convertContentObject(String value, QName tag, AReadContext context) throws Exception
	{
		Object ret = value;
		if(tag.getNamespaceURI().startsWith(SXML.PROTOCOL_TYPEINFO))
		{
			String clazzname = tag.getNamespaceURI().substring(SXML.PROTOCOL_TYPEINFO.length())+"."+tag.getLocalPart();
			Class<?> clazz = SReflect.classForName0(clazzname, context.getClassLoader());
			if(clazz!=null)
			{
				if(BasicTypeConverter.isBuiltInType(clazz))
				{
					ret = BasicTypeConverter.getBasicStringConverter(clazz).convertString(value, context);
				}
				else
				{
					ret	= null;
					context.getReporter().report("No converter known for: "+clazz, "content error", context, context.getLocation());
				}
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
	public void handleAttributeValue(Object object, QName xmlattrname, List<QName> attrpath, String attrval, 
		Object attrinfo, AReadContext context) throws Exception
	{
		// Hack!
		Object converter = attrinfo instanceof AttributeInfo? ((AttributeInfo)attrinfo).getConverter(): null;
		String id = attrinfo instanceof AttributeInfo? ((AttributeInfo)attrinfo).getId(): null;
		Object accessinfo = attrinfo instanceof AttributeInfo? ((AttributeInfo)attrinfo).getAccessInfo(): attrinfo;
		
		// Try to convert strings before trying setter variations to obtain useful error message if conversion not possible.
		Object val	= attrval;
		boolean	done	= false;
		if(val!=null && converter instanceof IStringObjectConverter)
		{
			try
			{
				val = ((IStringObjectConverter)converter).convertString((String)val, context);
			}
			catch(Exception e)
			{
				done	= true;
				context.getReporter().report("Failure in parsing attribute: "+xmlattrname+" of object "+object+": "+e,
					"attribute error", context, context.getLocation());				
			}
			converter	= null;
		}
		
		if(!done)
		{
			if(attrval!=null)	// allow 'null' as actual value.
			{
				boolean	set	= setElementValue(accessinfo, xmlattrname, object, val, converter, id, context);
				if(!set)
				{
					context.getReporter().report("Failure in setting attribute: "+(xmlattrname!=null?xmlattrname:attrinfo)+" on object: "+object+" (unknown attribute?)",
						"attribute error", context, context.getLocation());
				}
			}
			else if(accessinfo instanceof AccessInfo && ((AccessInfo)accessinfo).getDefaultValue()!=null)
			{
				boolean	set	= setElementValue(accessinfo, xmlattrname, object, ((AccessInfo)accessinfo).getDefaultValue(), converter, id, context);
				if(!set)
				{
					context.getReporter().report("Failure in setting attribute: "+(xmlattrname!=null?xmlattrname:attrinfo)+" on object: "+object+" (unknown attribute?)",
						"attribute error", context, context.getLocation());
				}
			}
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
		QName[] pathname, AReadContext context) throws Exception
	{
		QName tag = pathname[pathname.length-1];
		
		// Add object to its parent.
		boolean	linked	= false;
		
		if(linkinfo instanceof SubobjectInfo)
		{
			SubobjectInfo sinfo = (SubobjectInfo)linkinfo;
			linked	= setElementValue(sinfo.getAccessInfo(), tag, parent, object, sinfo.getConverter(), null, context);
		}
		
		// Special case array
		
		else if(parent.getClass().isArray())
		{
			int cnt = context.getArrayCount(parent);
			
			if(!AReader.NULL.equals(object))
				Array.set(parent, cnt, object);
			
			linked = true;
		}
		else
		{
			// Try linking via tag name. Todo: order to try tags?
			// for(int i=pathname.length-1; !linked && i>=0; i--)
			for(int i=0; !linked && i<pathname.length; i++)	// This order works for discovery info (setProxy instead of setComponentIdentifier)
			{
				linked	= setElementValue(null, pathname[i], parent, object, null, null, context);
			}
			
			// Try name guessing via class/superclass/interface names of object to add
			if(!linked)
			{
				List<Class<?>> classes	= new LinkedList<Class<?>>();
				classes.add(object.getClass());
			
				while(!linked && !classes.isEmpty())
				{
					Class<?> clazz = classes.remove(0);
					if(!BasicTypeConverter.isBuiltInType(clazz))
					{
						String name = SReflect.getInnerClassName(clazz);
						linked	= setElementValue(name, null, parent, object, null, null, context);
					}
					
					if(!linked)
					{
						if(clazz.getSuperclass()!=null)
							classes.add(clazz.getSuperclass());
						Class<?>[]	ifs	= clazz.getInterfaces();
						for(int i=0; i<ifs.length; i++)
						{
							classes.add(ifs[i]);
						}
					}
				}
			}
		}
		
		if(!linked)
		{
			context.getReporter().report("Could not link: "+object+" "+parent, "link error", context, context.getLocation());
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
	public void bulkLinkObjects(List<Object> childs, Object parent, Object linkinfo, 
		QName[] pathname, AReadContext context) throws Exception
	{
		QName tag = pathname[pathname.length-1];
		
		// Add object to its parent.
		boolean	linked	= false;
		
		if(linkinfo!=null)
		{
			// converter and id null?!
			linked	= setBulkAttributeValues(linkinfo, tag, parent, childs, null, null, context);
		}
		
		// Special case array
		else if(parent.getClass().isArray())
		{
			for(int i=0; i<childs.size(); i++)
			{
				int cnt = context.getArrayCount(parent);
				
				Object object = childs.get(i);
				if(!AReader.NULL.equals(object))
					Array.set(parent, cnt, object);
			}
			
			linked = true;
		}
		else
		{
			// Try linking via tag name. Todo: order to try tags?
			// for(int i=pathname.length-1; !linked && i>=0; i--)
			for(int i=0; !linked && i<pathname.length; i++)	// This order works for discovery info (setProxy instead of setComponentIdentifier)
			{
				linked	= setBulkAttributeValues(null, pathname[i], parent, childs, null, null, context);
			}
			
			// Try name guessing via class/superclass/interface names of objects to add
			if(!linked)
			{
				List<Class<?>> classes	= new LinkedList<Class<?>>();
				classes.add(childs.get(0).getClass());
			
				while(!linked && !classes.isEmpty())
				{
					Class<?> clazz = (Class<?>)classes.remove(0);
					if(!BasicTypeConverter.isBuiltInType(clazz))
					{
						String name = SReflect.getInnerClassName(clazz);
						linked	= setBulkAttributeValues(name, null, parent, childs, null, null, context);
					}
					
					if(!linked)
					{
						if(clazz.getSuperclass()!=null)
							classes.add(clazz.getSuperclass());
						Class<?>[]	ifs	= clazz.getInterfaces();
						for(int i=0; i<ifs.length; i++)
						{
							classes.add(ifs[i]);
						}
					}
				}
			}
			
		}

		
//		// Try linking via field/method searching by name guessing.
//		else
//		{
//			List classes	= new LinkedList();
//			classes.add(childs.get(0).getClass());
//			
//			String[] orignames = new String[pathname.length];
//			String[] plunames = new String[pathname.length];
//			String[] origfieldnames = new String[pathname.length];
//			String[] plufieldnames = new String[pathname.length];
//			for(int i=0; i<pathname.length; i++)
//			{
//				String origname = pathname[i].getLocalPart();
//				String pluname =  SUtil.getPlural(pathname[i].getLocalPart());
//				plunames[i] = pluname.substring(0, 1).toUpperCase()+pluname.substring(1);
//				orignames[i] = origname.substring(0, 1).toUpperCase()+origname.substring(1);
//				plufieldnames[i] = pluname;
//				origfieldnames[i] = origname;
//			}
//			
//			// Try via fieldname
//			for(int i=0; i<plufieldnames.length && !linked; i++)
//			{
//				linked = setBulkField(plufieldnames[i], parent, childs, null, context, null);
//				if(!linked && !origfieldnames[i].equals(plufieldnames[i]))
//				{
//					linked = setBulkField(origfieldnames[i], parent, childs, null, context, null);
//				}
//			}
//			
//			// Try name guessing via class/superclass/interface names of object to add
//			while(!linked && !classes.isEmpty())
//			{
//				Class clazz = (Class)classes.remove(0);
//				
//				for(int i=0; i<plunames.length && !linked; i++)
//				{
//					linked = internalBulkLinkObjects(clazz, "set"+plunames[i], childs, parent, context);
//					if(!linked && !orignames[i].equals(plunames[i]))
//					{
//						linked = internalBulkLinkObjects(clazz, "set"+orignames[i], childs, parent, context);
//					}
//				}
//				
//				// Try classname of object to add
//				if(!linked && !BasicTypeConverter.isBuiltInType(clazz))
//				{
//					String name = SReflect.getInnerClassName(clazz);
//					linked = internalBulkLinkObjects(clazz, "set"+name, childs, parent, context);
//				}
//				
//				if(!linked)
//				{
//					if(clazz.getSuperclass()!=null)
//						classes.add(clazz.getSuperclass());
//					Class[]	ifs	= clazz.getInterfaces();
//					for(int i=0; i<ifs.length; i++)
//					{
//						classes.add(ifs[i]);
//					}
//				}
//			}
//		}
		
		if(!linked)
		{
			context.getReporter().report("Could not bulk link: "+childs+" "+parent, "link error", context, context.getLocation());
		}
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
	public void bulkLinkObjects(Object parent, List<LinkData> children, AReadContext context) throws Exception
	{
//		System.out.println("bulk link for: "+parent+" "+children);
		
		// The default bulk strategy is as follows:
		// Linear scan the subpaths(tags) of the parent
		// As long as the path is the same remember as bulk
		// Whenever a new path/tag is used the last bulk is considered finished

		LinkData linkdata = (LinkData)children.get(0);
		List<Object> childs = new ArrayList<Object>();
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
	protected void handleBulkLinking(List<Object> childs, Object parent, AReadContext context, QName[] pathname, List<LinkData> linkdatas, int startidx) throws Exception
	{
		if(childs.size()>1)
		{
			try
			{
				bulkLinkObjects(childs, parent, ((LinkData)linkdatas.get(startidx)).getLinkinfo(), pathname, context);
			}
			catch(Exception e)
			{
				context.getReporter().report("Warning. Bulk link initiated but not successful: "+childs+" "+parent+" "+e, "warning", context, context.getLocation());
			
				for(int i=0; i<childs.size(); i++)
				{
					linkObject(childs.get(i), parent, ((LinkData)linkdatas.get(startidx+i)).getLinkinfo(), pathname, context);
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
	protected boolean setElementValue(Object accessinfo, QName xmlname, Object object, Object val, Object converter, String id, AReadContext context) throws Exception
	{
		boolean	set	= false;
		
		if(AReader.NULL.equals(val))
		{
			set	= true;
		}
		
		// Write to a map.
		else if(accessinfo instanceof AccessInfo && ((AccessInfo)accessinfo).getExtraInfo() instanceof BeanAccessInfo)
		{	
			AccessInfo ai = (AccessInfo)accessinfo;
			BeanAccessInfo bai = (BeanAccessInfo)ai.getExtraInfo();
			
			// Put value in map 1) fetch key 2) set value in map
			if(bai.getMapName()!=null)
			{				
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
						catch(InvocationTargetException e)
						{
							// Ignore -> try other way of setting attribute
//							context.getReporter().report("Failure invoking key getter method: "+e.getTargetException(),
//								"attribute error", context, context.getLocation());
						}
						catch(Exception e)
						{
							// Ignore -> try other way of setting attribute
//							context.getReporter().report("Failure invoking key getter method: "+e,
//								"attribute error", context, context.getLocation());
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
							// Ignore -> try other way of setting attribute
//							context.getReporter().report("Failure getting key field: "+e,
//								"attribute error", context, context.getLocation());
						}
					}
					else if(kh instanceof IReturnValueCommand)
					{
						key = ((IReturnValueCommand)kh).execute(targetobj);
					}
					else
					{
						context.getReporter().report("Unknown key help: "+kh,
							"attribute error", context, context.getLocation());
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
							Class<?>[] ps = m.getParameterTypes();
							Object arg = convertValue(val, ps[1], converter, context, id);
							m.invoke(object, new Object[]{key, arg});
							set = true;
						}
						catch(InvocationTargetException e)
						{
							// Ignore -> try other way of setting attribute
//							context.getReporter().report("Failure invoking setter method: "+e.getTargetException(),
//								"attribute error", context, context.getLocation());
						}
						catch(Exception e)
						{
							// Ignore -> try other way of setting attribute
//							context.getReporter().report("Failure invoking setter method: "+e,
//								"attribute error", context, context.getLocation());
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
								map = new HashMap<Object, Object>();
								f.set(object, map);
							}
							Object arg = convertValue(val, null, converter, context, id);
							((Map<Object, Object>)map).put(key, arg);
							set = true;
						}
						catch(Exception e)
						{
							// Ignore -> try other way of setting attribute
//							context.getReporter().report("Failure setting field: "+e,
//								"attribute error", context, context.getLocation());
						}
					}
					else if(sh instanceof IReturnValueCommand)
					{
						Object arg = convertValue(val, null, converter, context, id);
						((IReturnValueCommand)sh).execute(new Object[]{object, arg});
					}
					else
					{
						context.getReporter().report("Unknown map store help: "+sh,
							"attribute error", context, context.getLocation());
					}
				}
				// Set map value with guessing method name.
				else
				{
					String mapname = bai.getMapName().length()==0 || AccessInfo.THIS.equals(bai.getMapName())? ""
						: bai.getMapName().substring(0,1).toUpperCase()+bai.getMapName().substring(1);
					
					String[] prefixes = new String[]{"put", "set", "add"};
					for(int i=0; i<prefixes.length && !set; i++)
					{
						Method[] ms = SReflect.getMethods(object.getClass(), prefixes[i]+mapname);
						for(int j=0; j<ms.length && !set; j++)
						{
							Class<?>[] ps = ms[j].getParameterTypes();
							if(ps.length==2)
							{
								Object arg = convertValue(val, ps[1], converter, context, id);
								
								try
								{
									ms[j].invoke(object, new Object[]{key, arg});
									set = true;
								}
								catch(InvocationTargetException e)
								{
									// Ignore -> try other way of setting attribute
//									context.getReporter().report("Failure invoking setter method: "+e.getTargetException(),
//										"attribute error", context, context.getLocation());
								}
								catch(Exception e)
								{
									// Ignore -> try other way of setting attribute
//									context.getReporter().report("Failure invoking setter method: "+e,
//										"attribute error", context, context.getLocation());
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
					Class<?>[] ps = m.getParameterTypes();
					if(ps.length==1)
					{
						Object arg = convertValue(val, ps[0], converter, context, id);
						try
						{
							m.invoke(object, new Object[]{arg});
							set = true;
						}
						catch(InvocationTargetException e)
						{
							// Ignore -> try other way of setting attribute
//							context.getReporter().report("Failure invoking setter method: "+e.getTargetException(),
//								"attribute error", context, context.getLocation());
						}
						catch(Exception e)
						{
							// Ignore -> try other way of setting attribute
//							context.getReporter().report("Failure invoking setter method: "+e,
//								"attribute error", context, context.getLocation());
						}
					}
					else
					{
						context.getReporter().report("Read method should have one parameter: "+bai+" "+m,
							"attribute error", context, context.getLocation());
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
						// Ignore -> try other way of setting attribute
//						context.getReporter().report("Failure setting field: "+e,
//							"attribute error", context, context.getLocation());
					}
				}
				else if(sh instanceof IReturnValueCommand)
				{
					Object arg = convertValue(val, null, converter, context, id);
					((IReturnValueCommand)sh).execute(new Object[]{object, arg});
				}
				else
				{
					context.getReporter().report("Unknown store help: "+sh,
						"attribute error", context, context.getLocation());
				}
			}
		}
	
		// Try using object identifier from access info
		if(!set && accessinfo instanceof AccessInfo)
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
		else if(!set) // attribute info is null or string
		{
			// Write as normal bean attribute.
			// Try to find bean class information
			
			Map<String, BeanProperty> props = introspector.getBeanProperties(object.getClass(), true, true);
			Object prop = props.get(accessinfo instanceof String? accessinfo: xmlname.getLocalPart());
			if(prop instanceof BeanProperty && !((BeanProperty)prop).isWritable())
			{
				// Ignore properties marked as not writeable.
				set	= true;
			}
			else if(prop instanceof BeanProperty && ((BeanProperty)prop).isWritable())
			{
				BeanProperty	bprop	= (BeanProperty)prop;
				Object arg = convertValue(val, bprop.getSetterType(), converter, context, id);

				try
				{
					if(!SXML.XML_CLASSNAME.equals(bprop.getName()))
					{
						bprop.setPropertyValue(object, arg);
					}
					set = true;
//					if(bprop.getSetter()!=null)
//					{
//						bprop.getSetter().invoke(object, new Object[]{arg});
//					}
//					else
//					{
//						if((bprop.getField().getModifiers()&Field.PUBLIC)==0)
//						{
//							if(SXML.XML_CLASSNAME.equals(bprop.getName()))
//							{
//								set = true;
//							}
//							else
//							{
//								bprop.getField().setAccessible(true);
//							}
//						}
//						if(!set)
//							bprop.getField().set(object, arg);
//					}
					
				}
//				catch(InvocationTargetException e)
//				{
//					// Ignore -> try other way of setting attribute
//					context.getReporter().report("Failure invoking setter method: "+e.getTargetException(),
//						"attribute error", context, context.getLocation());
//				}
				catch(Exception e)
				{
//					e.printStackTrace();
					// Ignore -> try other way of setting attribute
//					context.getReporter().report("Failure setting attribute: "+e,
//						"attribute error", context, context.getLocation());
				}
			}
			else if(prop instanceof Classname)
			{
				// Annotation needs not to be set.
				set	= true;
			}
			
			// Try to guess field or method.
			if(!set)
			{
				String fieldname = accessinfo instanceof String? (String)accessinfo : xmlname.getLocalPart();
				set	= setField(fieldname, object, val, converter, context, id);
				
				if(!set)
				{
					String postfix = fieldname.substring(0,1).toUpperCase()+fieldname.substring(1);
					set = invokeSetMethod(new String[]{"set", "add"}, postfix, val, object, context, converter, id);
					
					if(!set)
					{
						String oldpostfix = postfix;
						postfix = SUtil.getSingular(postfix);
						if(!postfix.equals(oldpostfix))
						{
							set = invokeSetMethod(new String[]{"set", "add"}, postfix, val, object, context, converter, id);
						}
					}
				}
			}
		}
		
		return set;
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
	protected boolean	setBulkAttributeValues(Object accessinfo, QName xmlattrname, Object object, 
		List<Object> vals, Object converter, String id, AReadContext context) throws Exception
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
					Class<?>[] ps = m.getParameterTypes();
					if(ps.length==1)
					{
						Object arg = convertBulkValues(vals, ps[0], converter, context, id);
						try
						{
							m.invoke(object, new Object[]{arg});
							set = true;
						}
						catch(InvocationTargetException e)
						{
							// Ignore -> try other way of setting attribute
//							context.getReporter().report("Failure invoking setter method: "+e.getTargetException(),
//								"attribute error", context, context.getLocation());
						}
						catch(Exception e)
						{
							// Ignore -> try other way of setting attribute
//							context.getReporter().report("Failure invoking setter method: "+e,
//								"attribute error", context, context.getLocation());
						}
					}
					else
					{
						context.getReporter().report("Read method should have one parameter: "+bai+" "+m,
							"attribute error", context, context.getLocation());
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
						// Ignore -> try other way of setting attribute
//						context.getReporter().report("Failure setting field: "+e,
//							"attribute error", context, context.getLocation());
					}
				}
				else
				{
					context.getReporter().report("Unknown store help: "+sh,
						"attribute error", context, context.getLocation());
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
			
			Map<String, BeanProperty> props = introspector.getBeanProperties(object.getClass(), true, true);
			BeanProperty prop = (BeanProperty)props.get(accessinfo instanceof String? accessinfo: xmlattrname.getLocalPart());
			if(prop!=null)
			{
				Object arg = convertBulkValues(vals, prop.getSetterType(), null, context, null);

				try
				{
					if(prop.isWritable())
					{
						prop.setPropertyValue(object, arg);
					}
//					else
//						prop.getField().set(object, arg);
					set = true;
				}
				catch(Exception e)
				{
					// Ignore -> try other way of setting attribute
//					context.getReporter().report("Failure setting attribute: "+e,
//						"attribute error", context, context.getLocation());
				}
			}
			
			// Try to guess field or method name.
			if(!set)
			{
				String fieldname = accessinfo instanceof String? (String)accessinfo: xmlattrname.getLocalPart();
				set	= setBulkField(fieldname, object, vals, converter, context, id);
				if(!set)
				{
					String	plufieldname	= SUtil.getPlural(fieldname);
					if(!fieldname.equals(plufieldname))
					{
						set	= setBulkField(plufieldname, object, vals, converter, context, id);
					}
				}
				
				if(!set)
				{
					String postfix = fieldname.substring(0,1).toUpperCase()+fieldname.substring(1);
					set = invokeBulkSetMethod(new String[]{"set"}, postfix, vals, object, context, null, null);

					if(!set)
					{
						String	plupostfix	= SUtil.getPlural(postfix);
						if(!postfix.equals(plupostfix))
						{
							set = invokeBulkSetMethod(new String[]{"set"}, plupostfix, vals, object, context, null, null);
						}
					}
				}
			}
		}
		
		return set;
	}
	
	/**
	 *  Set a value directly on a Java bean.
	 *  @param prefixes The method prefixes.
	 *  @param postfix The method postfix.
	 *  @param value The attribute value.
	 *  @param object The object.
	 *  @param root The root.
	 *  @param classloader The classloader.
	 *  @param converter The converter.
	 */
	protected boolean invokeSetMethod(String[] prefixes, String postfix, Object value, Object object, 
		AReadContext context, Object converter, String idref) throws Exception
	{
		boolean set = false;
				
		for(int i=0; i<prefixes.length && !set; i++)
		{
			try
			{
				Method[] ms = SReflect.getMethods(object.getClass(), prefixes[i]+postfix);
				
				for(int j=0; j<ms.length && !set; j++)
				{
					Class<?>[] ps = ms[j].getParameterTypes();
					if(ps.length==1)
					{
						Object arg = convertValue(value, ps[0], converter, context, idref);
						ms[j].invoke(object, new Object[]{arg});
						set = true;
					}
				}
			}
			catch(InvocationTargetException e)
			{
//				if("setClazz".equals(prefixes[i]+postfix))
//					System.out.println("here");
				// Ignore -> try other way of setting attribute
//				context.getReporter().report("Failure invoking setter method: "+e.getTargetException(),
//					"attribute error", context, context.getLocation());
			}
			catch(Exception e)
			{
//				 Ignore -> try other way of setting attribute
//				context.getReporter().report("Failure invoking setter method: "+e,
//					"attribute error", context, context.getLocation());
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
	protected boolean invokeBulkSetMethod(String[] prefixes, String postfix, List<Object> vals, Object object, 
		AReadContext context, Object converter, String idref) throws Exception
	{
		boolean set = false;
				
		for(int i=0; i<prefixes.length && !set; i++)
		{
			try
			{
				Method[] ms = SReflect.getMethods(object.getClass(), prefixes[i]+postfix);
				
				for(int j=0; j<ms.length && !set; j++)
				{
					Class<?>[] ps = ms[j].getParameterTypes();
					if(ps.length==1)
					{
						Object arg = convertBulkValues(vals, ps[0], converter, context, idref);
						
						ms[j].invoke(object, new Object[]{arg});
						set = true;
					}
				}
			}
			catch(InvocationTargetException e)
			{
				// Ignore -> try other way of setting attribute
//				context.getReporter().report("Failure invoking setter method: "+e.getTargetException(),
//					"attribute error", context, context.getLocation());
			}
			catch(Exception e)
			{
				// Ignore -> try other way of setting attribute
//				context.getReporter().report("Failure invoking setter method: "+e,
//					"attribute error", context, context.getLocation());
			}
		}
		
		return set;
	}
	
	/**
	 *  Directly access a field for setting/(adding) the object.
	 */
	protected boolean setField(String fieldname, Object parent, Object object, Object converter, 
		AReadContext context, String idref) throws Exception
	{
		boolean set = false;
		try
		{
			Field field = parent.getClass().getField(fieldname);
			Class<?> type = field.getType();
			
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
			// Ignore -> try other way of setting attribute
		}
		
		return set;
	}
	
	/**
	 *  Directly access a field for setting the objects.
	 */
	protected boolean setBulkField(String fieldname, Object parent, List<Object> objects, Object converter,
		AReadContext context, String idref) throws Exception
	{
		boolean set;
		try
		{
			Field field = parent.getClass().getField(fieldname);
			Class<?> type = field.getType();
			
//			object = convertAttributeValue(object, type, converter, root, classloader, idref, readobjects);
			
			Object arg = convertBulkValues(objects, type, converter, context, idref);
			
			field.set(parent, arg);
			set = true;
		}
		catch(Exception e)
		{
			// Ignore -> try other way of setting attribute
			set	= false;
		}
		
		return set;
	}
	
//	/**
//	 *  Internal link objects method.
//	 *  @param clazz The clazz.
//	 *  @param name The name.
//	 *  @param object The object.
//	 *  @param parent The parent.
//	 *  @param root The root.
//	 *  @param classloader classloader.
//	 */
//	protected boolean internalLinkObjects(Class clazz, String name, Object object, 
//		Object parent, ReadContext context) throws Exception
//	{
//		boolean ret = false;
//			
//		Method[] ms = SReflect.getMethods(parent.getClass(), name);
//		for(int i=0; !ret && i<ms.length; i++)
//		{
//			Class[] ps = ms[i].getParameterTypes();
//			if(ps.length==1)
//			{
//				if(SReflect.getWrappedType(ps[0]).isAssignableFrom(clazz))
//				{
//					try
//					{
//						ms[i].invoke(parent, new Object[]{object});
//						ret	= true;
//					}
//					catch(InvocationTargetException e)
//					{
//						// Ignore -> try other way of setting attribute
////						context.getReporter().report("Failure invoking link method: "+e.getTargetException(),
////							"link error", context, context.getLocation());
//					}
//					catch(Exception e)
//					{
//						// Ignore -> try other way of setting attribute
////						context.getReporter().report("Failure invoking link method: "+e,
////							"link error", context, context.getLocation());
//					}
//				}
//				else if(object instanceof String)
//				{
//					IStringObjectConverter converter = BasicTypeConverter.getBasicStringConverter(ps[0]);
//					if(converter != null)
//					{
//						try
//						{
//							object = converter.convertString((String)object, context);
//							ms[i].invoke(parent, new Object[]{object});
//							ret	= true;
//						}
//						catch(InvocationTargetException e)
//						{
//							// Ignore -> try other way of setting attribute
////							context.getReporter().report("Failure invoking link method: "+e.getTargetException(),
////								"link error", context, context.getLocation());
//						}
//						catch(Exception e)
//						{
//							// Ignore -> try other way of setting attribute
////							context.getReporter().report("Failure invoking link method: "+e,
////								"link error", context, context.getLocation());
//						}
//					}
//				}
//			}
//		}
//		
//		return ret;
//	}
	
//	/**
//	 *  Internal bulk link objects method.
//	 *  @param clazz The clazz.
//	 *  @param name The name.
//	 *  @param object The object.
//	 *  @param parent The parent.
//	 *  @param root The root.
//	 *  @param classloader classloader.
//	 */
//	protected boolean internalBulkLinkObjects(Class clazz, String name, List childs, 
//		Object parent, ReadContext context) throws Exception
//	{
//		boolean ret = false;
//			
//		Method[] ms = SReflect.getMethods(parent.getClass(), name);
//		for(int i=0; !ret && i<ms.length; i++)
//		{
//			Class[] ps = ms[i].getParameterTypes();
//			if(ps.length==1)
//			{
//				try
//				{
//					Object arg = convertBulkValues(childs, ps[0], null, context, null);
//					ms[i].invoke(parent, new Object[]{arg});
//					ret	= true;
//				}	
//				catch(InvocationTargetException e)
//				{
//					// Ignore -> try other way of setting attribute
////					context.getReporter().report("Failure invoking link method: "+e.getTargetException(),
////						"link error", context, context.getLocation());
//				}
//				catch(Exception e)
//				{
//					// Ignore -> try other way of setting attribute
////					context.getReporter().report("Failure invoking link method: "+e,
////						"link error", context, context.getLocation());
//				}
//			}
//		}
//		
//		return ret;
//	}
	
	/**
	 *  Convert a value by using a converter.
	 *  @param val The attribute value.
	 *  @param targetcalss The target class.
	 *  @param converter The converter.
	 *  @param root The root.
	 *  @param classloader The classloader.
	 */
	protected Object convertValue(Object val, Class<?> targetclass, Object converter, 
		AReadContext context, String id) throws Exception
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
			else if(targetclass!=null && !String.class.isAssignableFrom(targetclass))
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
	protected Object convertBulkValues(List<Object> vals, Class<?> targetclass, Object converter, 
		AReadContext context, String id) throws Exception
	{
		// todo: use converter?!
		
		Object ret = vals;
//		object = convertAttributeValue(object, type, converter, root, classloader, idref, readobjects);
			
		if(SReflect.isSupertype(Set.class, targetclass))
		{
			ret = new HashSet<Object>(vals);
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
			context.getReporter().report("Conversion to target no possible: "+targetclass+" "+vals,
				"convert error", context, context.getLocation());
		}
		
		return ret;
	}
	
	/**
	 *  Get the post-processor.
	 *  @return The post-processor
	 */
	public synchronized IPostProcessor[] getPostProcessors(Object object, Object typeinfo)
	{
		List<IPostProcessor> ret = new ArrayList<IPostProcessor>();
		IPostProcessor tiproc = typeinfo instanceof TypeInfo? ((TypeInfo)typeinfo).getPostProcessor(): null;
		if(tiproc!=null)
			ret.add(tiproc);
		
		if(postprocessors!=null)
		{
			for(Iterator<IFilter<Object>> it = postprocessors.keySet().iterator(); it.hasNext(); )
			{
				IFilter<Object> fil = it.next();
				if(fil.filter(object))
				{
					ret.add(postprocessors.get(fil));
				}
			}
		}
		
		return ret.toArray(new IPostProcessor[ret.size()]);
	}
	
	/**
	 *  Add a post processor.
	 *  @param filter The filter.
	 *  @param processor The post processor.
	 */
	public synchronized void addPostProcessor(IFilter<Object> filter, IPostProcessor processor)
	{
		if(postprocessors==null)
			postprocessors = new LinkedHashMap<IFilter<Object>, IPostProcessor>();
		postprocessors.put(filter, processor);
	}
	/**
	 *  Remove a post processor.
	 *  @param filter The filter.
	 *  @param processor The post processor.
	 */
	public synchronized void removePostProcessor(IFilter<Object> filter)
	{
		if(postprocessors!=null)
			postprocessors.remove(filter);
	}
	
}
