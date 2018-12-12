package jadex.xml.bean;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.commons.IFilter;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.transformation.BasicTypeConverter;
import jadex.commons.transformation.BeanIntrospectorFactory;
import jadex.commons.transformation.STransformation;
import jadex.commons.transformation.annotations.Classname;
import jadex.commons.transformation.traverser.BeanProperty;
import jadex.commons.transformation.traverser.IBeanIntrospector;
import jadex.xml.AccessInfo;
import jadex.xml.AttributeInfo;
import jadex.xml.IAttributeConverter;
import jadex.xml.IContext;
import jadex.xml.IPreProcessor;
import jadex.xml.ISubObjectConverter;
import jadex.xml.Namespace;
import jadex.xml.ObjectInfo;
import jadex.xml.SXML;
import jadex.xml.SubobjectInfo;
import jadex.xml.TypeInfo;
import jadex.xml.stax.QName;
import jadex.xml.writer.AWriteContext;
import jadex.xml.writer.AbstractObjectWriterHandler;


/**
 *  Java bean version for fetching write info for an object. 
 */
public class BeanObjectWriterHandler extends AbstractObjectWriterHandler
{
	//-------- attributes --------
	
	/** The bean introspector (also scans for public fields). */
	protected IBeanIntrospector introspector = BeanIntrospectorFactory.getInstance().getBeanIntrospector();
	
	/** The namespaces by package. */
//	protected Map namespacebypackage = new HashMap();
//	protected int nscnt;
		
	/** No type infos. */
	protected Set<Class<?>> no_typeinfos;
		
	/** The filter based post processors. */
	protected Map<IFilter<Object>, IPreProcessor> preprocessors;
	
	//-------- constructors --------
	
	/**
	 *  Create a new writer (gentypetags=false, prefertags=true, flattening=true).
	 */
	public BeanObjectWriterHandler(Set<TypeInfo> typeinfos)
	{
		this(typeinfos, false);
	}
	
	/**
	 *  Create a new writer (prefertags=true, flattening=true).
	 */
	public BeanObjectWriterHandler(Set<TypeInfo> typeinfos, boolean gentypetags)
	{
		this(typeinfos, gentypetags, false);
	}
	
	/**
	 *  Create a new writer (flattening=true).
	 */
	public BeanObjectWriterHandler(Set<TypeInfo> typeinfos, boolean gentypetags, boolean prefertags)
	{
		this(typeinfos, gentypetags, prefertags, true);
	}
	
	/**
	 *  Create a new writer.
	 */
	public BeanObjectWriterHandler(Set<TypeInfo> typeinfos, boolean gentypetags, boolean prefertags ,boolean flattening)
	{
		super(gentypetags, prefertags, flattening, typeinfos);
		this.no_typeinfos = Collections.synchronizedSet(new HashSet());
	}
	
	//-------- methods --------
	
	/**
	 *  Get the most specific mapping info.
	 *  @param tag The tag.
	 *  @param fullpath The full path.
	 *  @return The most specific mapping info.
	 */
	public synchronized TypeInfo getTypeInfo(Object object, QName[] fullpath, IContext context)
	{
		Object type = getObjectType(object, context);
		if(no_typeinfos.contains(type))
			return null;
			
		TypeInfo ret = super.getTypeInfo(object, fullpath, context);
		
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
					Class<?> clazz = (Class<?>)tocheck.get(i);
//					Set tis = getTypeInfoManager().getTypeInfosByType(clazz);
//					ret = getTypeInfoManager().findTypeInfo(tis, fullpath);
					ret = getTypeInfoManager().getTypeInfo(clazz, fullpath);
					
//					Set tis = getTypeInfoManager().getTypeInfosByType(clazz);
//					if(tis.size()==1)
//						ret = (TypeInfo)tis.iterator().next();
					
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
//					System.out.println("array: "+type);
//					ret = getTypeInfoManager().findTypeInfo(getTypeInfoManager().getTypeInfosByType(Object[].class), fullpath);
					ret = getTypeInfoManager().getTypeInfo(Object[].class, fullpath);
				}
				
				// Add concrete class for same info if it is used
				if(ret!=null)
				{
					ObjectInfo cri =ret.getObjectInfo();
					ObjectInfo cricpy = cri!=null? new ObjectInfo(type, cri.getPostProcessor()): new ObjectInfo(type);
					
					TypeInfo ti = new TypeInfo(ret.getXMLInfo(),
						cricpy, ret.getMappingInfo(), ret.getLinkInfo());
					
					getTypeInfoManager().addTypeInfo(ti);
				}
				else
				{
//					if(no_typeinfos==null)
//						no_typeinfos = new HashSet();
					no_typeinfos.add((Class<?>)type);
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
	public Object getObjectType(Object object, IContext context)
	{
		return object.getClass();
	}
	
	/**
	 *  Get the tag name for an object.
	 */
	public QName getTagName(Object object, IContext context)
	{
//		try
//		{
		String pck;
		String tag;
		if(object!=null)
		{
			Class<?> clazz = object.getClass();
			String clazzname = STransformation.registerClass(clazz);
//			if(clazzname.indexOf("IRemoteMessageListener")!=-1)
//			{
//				System.out.println("sdilfugkl");
//			}
			int idx = clazzname.lastIndexOf(".");
			pck = idx!=-1? SXML.PROTOCOL_TYPEINFO+clazzname.substring(0, idx): SXML.PROTOCOL_TYPEINFO;
			tag = idx!=-1? clazzname.substring(idx+1): clazzname;
			
			// Special case inner class, replace $ with -
			tag = tag.replace("$", "-");
			
			// Special case array, replace [] with __ and length 
			if(clazz.isArray())
			{
				int dim = SUtil.getArrayDimension(object);
				tag = tag.substring(0, tag.indexOf("["))+"__"+dim;//+"__"+Array.getLength(object);
//				for(int i=0; i<lens.length; i++)
//				{
//					tag += lens[i];
//					if(i+1<lens.length)
//						tag += "_";
//				}
			}
		}
		else
		{
			pck = SXML.PROTOCOL_TYPEINFO;
			tag = "null";
		}
		
		AWriteContext wc = (AWriteContext)context;
		Namespace ns = wc.getNamespace(pck);
		return new QName(ns.getURI(), tag, ns.getPrefix());
		
//		}
//		catch(RuntimeException e)
//		{
//			e.printStackTrace();
//			throw e;
//		}
	}
	
	/**
	 *  Get the tag with namespace.
	 */
	public QName getTagWithPrefix(QName tag, IContext context)
	{
		AWriteContext wc = (AWriteContext)context;
		Namespace ns = wc.getNamespace(tag.getNamespaceURI());
		return new QName(ns.getURI(), tag.getLocalPart(), ns.getPrefix());
	}
	
	/**
	 *  Get or create a namespace.
	 *  @param uri The namespace uri.
	 * /
	protected Namespace getNamespace(String uri)
	{
		Namespace ns = (Namespace)namespacebypackage.get(uri);
		if(ns==null)
		{
			String prefix = "p"+nscnt;
			ns = new Namespace(prefix, uri);
			namespacebypackage.put(uri, ns);
			nscnt++;
		}
		return ns;
	}*/

	/**
	 *  Get a value from an object.
	 */
	protected Object getValue(Object object, Object attr, IContext context, Object info) throws Exception
	{
		Object value = null;
		
		if(AccessInfo.THIS.equals(attr))
		{
			value = object;
		}
		else
		{
			Method method = null;
			Field field = null;
			Classname xmlc = null;
			boolean found = false;
			
			AccessInfo ai = info instanceof AttributeInfo? ((AttributeInfo)info).getAccessInfo(): 
				info instanceof SubobjectInfo? ((SubobjectInfo)info).getAccessInfo(): null;
			BeanAccessInfo bai = ai!=null && (ai.getExtraInfo() instanceof BeanAccessInfo)? 
				(BeanAccessInfo)ai.getExtraInfo(): null;
			
			if(bai!=null && bai.getFetchHelp()!=null)
			{
				Object tmp = bai.getFetchHelp();
				if(tmp instanceof Method)
					method = (Method)tmp;
				else //if(tmp instanceof Field)
					field = (Field)tmp;
			}
			else if(attr instanceof BeanProperty && ((BeanProperty) attr).isReadable())
			{
				value = ((BeanProperty)attr).getPropertyValue(object);
				found = true;
	//			method = ((BeanProperty)attr).getGetter();
	//			if(method==null)
	//			{
	//				field = ((BeanProperty)attr).getField();
	//			}
			}
			else if(attr instanceof BeanProperty && !((BeanProperty)attr).isReadable())
			{
				found = true;
			}
			else if(attr instanceof String)
			{
				method = findGetMethod(object, (String)attr, new String[]{"get", "is"});
				if(method==null)
				{
					try
					{
						field = object.getClass().getField((String)attr);
					}
					catch(Exception e)
					{
					}
				}
			}
			else if(attr instanceof Classname)
			{
				xmlc	= (Classname)attr;
			}
			else
			{
				throw new RuntimeException("Unknown attribute type: "+attr);
			}
			
			if(!found)
			{
				if(method!=null)
				{
					try
					{	
						if(!Modifier.isPublic(method.getModifiers()) || !Modifier.isPublic(object.getClass().getModifiers()))
						{
							method.setAccessible(true);
						}
						value = method.invoke(object, new Object[0]);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				else if(field!=null)
				{
					try
					{
						if(!Modifier.isPublic(field.getModifiers()) || !Modifier.isPublic(object.getClass().getModifiers()))
						{
							field.setAccessible(true);
						}
						value = field.get(object);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
				else if(xmlc!=null)
				{
					value = xmlc.value();
				}
				else
				{
					throw new RuntimeException("Could not fetch value: "+object+" "+attr);
				}
			}
		}
		
		// Convert values.
		if(info instanceof AttributeInfo)
		{
			IAttributeConverter conv = ((AttributeInfo)info).getConverter();
			if(conv!=null)
			{
				value = conv.convertObject(value, context);
			}
		}
		else if(info instanceof SubobjectInfo)
		{
			ISubObjectConverter conv = ((SubobjectInfo)info).getConverter();
			if(conv!=null)
			{
				value = conv.convertObjectForWrite(value, context);
			}
		}
		
		return value;
	}
	
	/**
	 *  Get the property.
	 */
	protected Object getProperty(Object info)
	{
		Object ret = null;
		if(info instanceof AttributeInfo)
		{
			info = ((AttributeInfo)info).getAccessInfo();
		}
		else if(info instanceof SubobjectInfo)
		{
			info = ((SubobjectInfo)info).getAccessInfo();
		}
		
		if(info instanceof AccessInfo)
		{
			ret = ((AccessInfo)info).getObjectIdentifier();
		}
		else if(info instanceof String)
		{
			ret = info;
		}
		
		return ret;
	}
	
	/**
	 *  Get the name of a property.
	 */
	protected String getPropertyName(Object property)
	{
		String ret;
		if(property instanceof BeanProperty)
			ret = ((BeanProperty)property).getName();
		else if(property instanceof String)
			ret = (String)property;
		else if(property instanceof QName)
			ret = ((QName)property).getLocalPart();
		else if(property instanceof Classname)
			ret = SXML.XML_CLASSNAME;
		else
			throw new RuntimeException("Unknown property type: "+property);
		return ret;
	}

	/**
	 *  Test if a value is a basic type.
	 */
	protected boolean isBasicType(Object property, Object value)
	{
//		if(value.getClass().equals(String.class))
//			System.out.println("string sdklhgb");
		return value==null? false: BasicTypeConverter.isBuiltInType(value.getClass());
	}
	
	/**
	 *  Get the properties of an object. 
	 */
	protected Collection<BeanProperty> getProperties(Object object, IContext context, boolean includemethods, boolean includefields)
	{
		return object==null? Collections.EMPTY_LIST: introspector.getBeanProperties(object.getClass(), includemethods, includefields).values();
	}

	/**
	 *  Find a get method with some prefix.
	 *  @param object The object.
	 *  @param name The name.
	 *  @param prefixes The prefixes to test.
	 */
	protected Method findGetMethod(Object object, String name, String[] prefixes)
	{
		Method method = null;
		for(int i=0; i<prefixes.length && method==null; i++)
		{
			String methodname = prefixes[i]+name.substring(0, 1).toUpperCase()+name.substring(1);
			try
			{
				method = object.getClass().getMethod(methodname, new Class[0]);
			}
			catch(Exception e)
			{
				// nop
			}
		}
//		if(method==null)
//			throw new RuntimeException("No getter found for: "+name);
		
		return method;
	}
	
	/**
	 *  Test if a value is compatible with the defined typeinfo.
	 */
	protected boolean isTypeCompatible(Object object, ObjectInfo info, IContext context)
	{
		boolean ret = true;
		if(info!=null && object!=null && info.getTypeInfo() instanceof Class)
		{
			Class<?> clazz = (Class<?>)info.getTypeInfo();
			ret = clazz.isAssignableFrom(object.getClass());
		}
		return ret;
	}
	
	/**
	 *  Test if a value is decodable to the same type.
	 *  Works for basic (final) types only and checks if the
	 *  two types are of same class.
	 */
	protected boolean isDecodableToSameType(Object property, Object value, IContext context)
	{
		boolean ret = true;
		if(value!=null)
		{
			ret	= false;
			if(property instanceof BeanProperty)
			{
				BeanProperty prop = (BeanProperty)property;
				// Do not allow strings -> avoids strings being written as attributes by default.
				ret = !(value instanceof String) && value.getClass().equals(SReflect.getWrappedType(prop.getSetterType()));
			}
			else if(property instanceof Classname)
			{
				// Allow XML class name as attribute
				ret	= true;
			}
		}
		return ret;
	}
	
	/**
	 *  Get the pre-processor.
	 *  @return The pre-processor
	 */
	public synchronized IPreProcessor[] getPreProcessors(Object object, Object typeinfo)
	{
		List<IPreProcessor> ret = new ArrayList<IPreProcessor>();
		
		IPreProcessor tiproc = typeinfo instanceof TypeInfo? ((TypeInfo)typeinfo).getPreProcessor(): null;
		if(tiproc!=null)
			ret.add(tiproc);
		
		if(preprocessors!=null)
		{
			for(Iterator<IFilter<Object>> it = preprocessors.keySet().iterator(); it.hasNext(); )
			{
				IFilter<Object> fil = it.next();
				if(fil.filter(object))
				{
					ret.add(preprocessors.get(fil));
				}
			}
		}
		
		return ret.toArray(new IPreProcessor[ret.size()]);
	}
	
	/**
	 *  Add a pre processor.
	 *  @param filter The filter.
	 *  @param processor The pre processor.
	 */
	public synchronized void addPreProcessor(IFilter<Object> filter, IPreProcessor processor)
	{
		if(preprocessors==null)
			preprocessors = new LinkedHashMap<IFilter<Object>, IPreProcessor>();
		preprocessors.put(filter, processor);
	}
	/**
	 *  Remove a pre processor.
	 *  @param filter The filter.
	 *  @param processor The pre processor.
	 */
	public synchronized void removePreProcessor(IFilter<Object> filter)
	{
		if(preprocessors!=null)
			preprocessors.remove(filter);
	}
}

