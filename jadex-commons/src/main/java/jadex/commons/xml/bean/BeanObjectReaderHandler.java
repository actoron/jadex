package jadex.commons.xml.bean;

import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.xml.AttributeInfo;
import jadex.commons.xml.BasicTypeConverter;
import jadex.commons.xml.ITypeConverter;
import jadex.commons.xml.TypeInfo;
import jadex.commons.xml.reader.IObjectReaderHandler;

import java.lang.reflect.Method;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

/**
 *  Handler for reading XML into Java beans.
 */
public class BeanObjectReaderHandler implements IObjectReaderHandler
{
	//-------- methods --------

	/**
	 *  Create an object for the current tag.
	 *  @param type The object type to create.
	 *  @param root Flag, if object should be root object.
	 *  @param context The context.
	 *  @return The created object (or null for none).
	 */
	public Object createObject(Object type, boolean root, Object context, Map rawattributes, ClassLoader classloader) throws Exception
	{
		Object ret = null;
		if(type instanceof TypeInfo)
			type =  ((TypeInfo)type).getTypeInfo();
			
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
			ret = ((IBeanObjectCreator)type).createObject(context, rawattributes, classloader);
		}
		else if(type instanceof String)
		{
//			System.out.println("here: "+typeinfo);
			Class clazz = SReflect.classForName0((String)type, classloader);
			if(clazz!=null && !BasicTypeConverter.isBuiltInType(clazz))
			{
				// Must have empty constructor.
				ret = clazz.newInstance();
			}
		}
		return ret;
	}
	
	/**
	 *  Convert an object to another type of object.
	 */
	public Object convertContentObject(Object object, String tagname, Object context, ClassLoader classloader)
	{
		Object ret = object;
		Class clazz = SReflect.classForName0(tagname, classloader);
		if(clazz!=null)
		{
			if(!BasicTypeConverter.isBuiltInType(clazz))
				throw new RuntimeException("No converter known for: "+clazz);
			ret = BasicTypeConverter.getBasicConverter(clazz).convertObject(object, null, classloader, context);
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
	public void handleAttributeValue(Object object, String xmlattrname, List attrpath, String attrval, 
		Object attrinfo, Object context, ClassLoader classloader, Object root) throws Exception
	{
		// Hack!
		if(attrval!=null)
			setAttributeValue(attrinfo, xmlattrname, object, attrval, root, classloader);
		else if(attrinfo instanceof BeanAttributeInfo && ((BeanAttributeInfo)attrinfo).getDefaultValue()!=null)
			setAttributeValue(attrinfo, xmlattrname, object, ((BeanAttributeInfo)attrinfo).getDefaultValue(), root, classloader);
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
		String pathname, Object context, ClassLoader classloader, Object root) throws Exception
	{
		int idx = pathname.lastIndexOf("/");
		String tagname = idx!=-1? pathname.substring(idx+1): pathname;
//		pathname = idx!=-1? pathname.substring(0, idx-1): null;
		
		// Add object to its parent.
		boolean	linked	= false;
		
		if(linkinfo!=null)
		{
			setAttributeValue(linkinfo, tagname, parent, object, root, classloader);
			linked = true;
		}
		List classes	= new LinkedList();
		classes.add(object.getClass());
		
		StringTokenizer stok = new StringTokenizer(pathname, "/");
		String[] plunames = new String[stok.countTokens()];
		String[] sinnames = new String[stok.countTokens()];
		for(int i=0; stok.hasMoreTokens(); i++)
		{
			String tok = stok.nextToken();
			String name = tok.substring(0, 1).toUpperCase()+tok.substring(1);
			plunames[i] = name;
			sinnames[i] = SUtil.getSingular(name);
		}
		
		// Try name guessing via class/superclass/interface names of object to add
		while(!linked && !classes.isEmpty())
		{
			Class clazz = (Class)classes.remove(0);
			
			for(int i=0; i<plunames.length && !linked; i++)
			{
				linked = internalLinkObjects(clazz, "set"+plunames[i], object, parent, root, classloader);
				if(!linked)
				{
					linked = internalLinkObjects(clazz, "add"+sinnames[i], object, parent, root, classloader);
					if(!linked && sinnames[i].equals(plunames[i]))
						linked = internalLinkObjects(clazz, "add"+plunames[i], object, parent, root, classloader);
				}
			}
			
			if(!linked && !BasicTypeConverter.isBuiltInType(clazz))
			{
				String name = SReflect.getInnerClassName(clazz);
				linked = internalLinkObjects(clazz, "set"+name, object, parent, root, classloader);
				if(!linked)
				{
					linked = internalLinkObjects(clazz, "add"+name, object, parent, root, classloader);
					if(!linked)
					{
						String sinname = SUtil.getSingular(name);
						if(!name.equals(sinname))
							linked = internalLinkObjects(clazz, "add"+sinname, object, parent, root, classloader);
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
		
		if(!linked)
			throw new RuntimeException("Could not link: "+object+" "+parent);
	}
	
	//-------- helper methods --------
	
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
	protected void setAttributeValue(Object attrinfo, String xmlattrname, Object object, 
		Object attrval, Object root, ClassLoader classloader)
	{
		boolean set = false;
		
		// Write to a map.
		if(attrinfo instanceof BeanAttributeInfo)
		{	
			BeanAttributeInfo bai = (BeanAttributeInfo)attrinfo;
			
			if(bai.getMapName()!=null)
			{
				String mapname = bai.getMapName().length()==0? bai.getMapName(): bai.getMapName().substring(0,1).toUpperCase()+bai.getMapName().substring(1);
				String jattrname = bai.getAttributeName()!=null? bai.getAttributeName(): xmlattrname;
				
				if(bai.getReadMethod()!=null)
				{
					Method m = bai.getReadMethod();
					Class[] ps = m.getParameterTypes();
					Object arg = convertAttributeValue(attrval, ps[1], bai.getConverterRead(), root, classloader);
					
					try
					{
						m.invoke(object, new Object[]{jattrname, arg});
						set = true;
					}
					catch(Exception e)
					{
					}
				}
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
								Object arg = convertAttributeValue(attrval, ps[1], bai.getConverterRead(), root, classloader);
								
								try
								{
									ms[j].invoke(object, new Object[]{jattrname, arg});
									set = true;
								}
								catch(Exception e)
								{
								}
							}
						}
					}
				}
			}
			else if(bai.getWriteMethod()!=null)
			{
				Method m = bai.getWriteMethod();
				Class[] ps = m.getParameterTypes();
				if(ps.length==1)
				{
					Object arg = convertAttributeValue(attrval, ps[0], bai.getConverterRead(), root, classloader);
					
					try
					{
						m.invoke(object, new Object[]{arg});
						set = true;
					}
					catch(Exception e)
					{
					}
				}
				else
				{
					throw new RuntimeException("Read method should have one parameter: "+bai+" "+m);
				}
			}
			else if(attrinfo instanceof AttributeInfo)
			{
				AttributeInfo ai = (AttributeInfo)attrinfo;
				
				String postfix = ai.getAttributeIdentifier()!=null? ((String)ai.getAttributeIdentifier())
					.substring(0,1).toUpperCase()+((String)ai.getAttributeIdentifier()).substring(1)
					: xmlattrname.substring(0,1).toUpperCase()+xmlattrname.substring(1);
					
				set = setDirectValue(new String[]{"set", "add"}, postfix, attrval, object, root, classloader, 
					ai instanceof BeanAttributeInfo? ((BeanAttributeInfo)ai).getConverterRead(): null);
			
				if(!set)
				{
					String oldpostfix = postfix;
					postfix = SUtil.getSingular(postfix);
					if(!postfix.equals(oldpostfix))
					{
						// First try add, as set might also be there and used for a non-multi attribute.
						set = setDirectValue(new String[]{"set", "add"}, postfix, attrval, object, root, classloader, 
							ai instanceof BeanAttributeInfo? ((BeanAttributeInfo)ai).getConverterRead(): null);
					}
				}
			}
		}
		else
		{
			// Write as normal bean attribute.
			
			String postfix = attrinfo instanceof String? ((String)attrinfo).substring(0,1).toUpperCase()+((String)attrinfo).substring(1)
				: xmlattrname.substring(0,1).toUpperCase()+xmlattrname.substring(1);
			
			set = setDirectValue(new String[]{"set", "add"}, postfix, attrval, object, root, classloader, null);
		
			if(!set)
			{
				String oldpostfix = postfix;
				postfix = SUtil.getSingular(postfix);
				if(!postfix.equals(oldpostfix))
				{
					set = setDirectValue(new String[]{"set", "add"}, postfix, attrval, object, root, classloader, null);
				}
			}
		}
		
		if(!set)
			throw new RuntimeException("Failure in setting attribute: "+xmlattrname+" on object: "+object);
	}
	
	/**
	 *  Convert an attribute value by using a converter.
	 *  @param attrval The attribute value.
	 *  @param targetcalss The target class.
	 *  @param converter The converter.
	 *  @param root The root.
	 *  @param classloader The classloader.
	 */
	protected Object convertAttributeValue(Object attrval, Class targetclass, ITypeConverter converter, Object root, ClassLoader classloader)
	{
		Object ret = attrval;

		if(converter!=null && converter.acceptsInputType(attrval.getClass()))
		{
			ret = converter.convertObject(attrval, root, classloader, null);
		}
		else if(!String.class.isAssignableFrom(targetclass))
		{
			ITypeConverter conv = BasicTypeConverter.getBasicConverter(targetclass);
			if(conv!=null && conv.acceptsInputType(attrval.getClass()))
				ret = conv.convertObject(attrval, root, classloader, null);
		}
	
		return ret;
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
	protected boolean setDirectValue(String[] prefixes, String postfix, Object attrval, Object object, Object root, ClassLoader classloader, ITypeConverter converter)
	{
		boolean set = false;
		
		for(int i=0; i<prefixes.length && !set; i++)
		{
			Method[] ms = SReflect.getMethods(object.getClass(), prefixes[i]+postfix);
			
			for(int j=0; j<ms.length && !set; j++)
			{
				Class[] ps = ms[j].getParameterTypes();
				if(ps.length==1)
				{
					Object arg = convertAttributeValue(attrval, ps[0], converter, root, classloader);
					
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
	protected boolean internalLinkObjects(Class clazz, String name, Object object, Object parent, Object root, ClassLoader classloader) throws Exception
	{
		boolean ret = false;
			
		Method[] ms = SReflect.getMethods(parent.getClass(), name);
		for(int i=0; !ret && i<ms.length; i++)
		{
			Class[] ps = ms[i].getParameterTypes();
			if(ps.length==1)
			{
				if(ps[0].isAssignableFrom(clazz))
				{
					try
					{
						ms[i].invoke(parent, new Object[]{object});
						ret	= true;
					}
					catch(Exception e)
					{
					}
				}
				else if(object instanceof String)
				{
					ITypeConverter converter = BasicTypeConverter.getBasicConverter(ps[0]);
					if(converter != null)
					{
						try
						{
							object = converter.convertObject((String)object, root, classloader, null);
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
}
