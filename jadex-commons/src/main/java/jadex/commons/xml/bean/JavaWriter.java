package jadex.commons.xml.bean;

import jadex.commons.xml.AttributeInfo;
import jadex.commons.xml.SubobjectInfo;
import jadex.commons.xml.TypeInfo;
import jadex.commons.xml.writer.Writer;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 */
public class JavaWriter extends Writer
{
	/** The static writer instance. */
	protected static Writer writer;
	
	/**
	 *  Create a new reader.
	 *  @param handler The handler.
	 */
	public JavaWriter(Set typeinfos)
	{
		super(new BeanObjectWriterHandler(true, joinTypeInfos(typeinfos)));
	}

	/**
	 * 
	 * @param typeinfos
	 * @return
	 */
	public static Set joinTypeInfos(Set typeinfos)
	{
		Set ret = getTypeInfos();
		if(typeinfos!=null)
			ret.addAll(typeinfos);
		return ret;
	}
	
	/**
	 * 
	 */
	public static Set getTypeInfos()
	{
		Set typeinfosw = new HashSet();
		
		try
		{
			// java.util.HashMap
			
//			TypeInfo ti_hashmapw = new TypeInfo(null, "java.util.HashMap", HashMap.class, null, null, null, null, null,
//				new SubobjectInfo[]{
//				new SubobjectInfo(new BeanAttributeInfo("entries", "entrySet", 
//					null, null, null, null, null, HashMap.class.getMethod("entrySet", new Class[0]), null), null, null, true)
//			});
			TypeInfo ti_hashmapw = new TypeInfo(null, null, Map.class, null, null, null, null, null,
				new SubobjectInfo[]{
				new SubobjectInfo(new BeanAttributeInfo("entries", "entrySet", 
					null, null, null, null, null, Map.class.getMethod("entrySet", new Class[0]), null), null, null, true)
			});

			typeinfosw.add(ti_hashmapw);
			
			// Cannot let xmltag be null, because class name then contains $ which is not allowed in a tag
			TypeInfo ti_hashmapentryw = new TypeInfo(null, "entry", Map.Entry.class, null, null, null, null, null,
				new SubobjectInfo[]{
				new SubobjectInfo(new BeanAttributeInfo("key", "key", 
					null, null, null, null, null, Map.Entry.class.getMethod("getKey", new Class[0]), null)),
				new SubobjectInfo(new BeanAttributeInfo("value", "value", 
					null, null, null, null, null, Map.Entry.class.getMethod("getValue", new Class[0]), null))
			});
			typeinfosw.add(ti_hashmapentryw);
			
			// java.util.ArrayList
			
			TypeInfo ti_arraylist = new TypeInfo(null, null, List.class, null, null, null, null, null,
				new SubobjectInfo[]{
				new SubobjectInfo(new BeanAttributeInfo("entries", AttributeInfo.THIS,
					null, null, null, null, null, null, ArrayList.class.getMethod("add", new Class[]{Object.class})), null, null, true)
			});
			typeinfosw.add(ti_arraylist);
			
			// java.util.HashSet
			
			TypeInfo ti_hashset = new TypeInfo(null, null, Set.class, null, null, null, null, null,
				new SubobjectInfo[]{
				new SubobjectInfo(new BeanAttributeInfo("entries", AttributeInfo.THIS,
					null, null, null, null, null, null, HashSet.class.getMethod("add", new Class[]{Object.class})), null, null, true)
			});
			typeinfosw.add(ti_hashset);
			
//			TypeInfo ti_array = new TypeInfo(null, null, Object[].class, null, null, null, null, null,
//				new SubobjectInfo[]{
//				new SubobjectInfo(new BeanAttributeInfo("entries", AttributeInfo.THIS,
//					null, null, null, null, null, null, HashSet.class.getMethod("add", new Class[]{Object.class})), null, null, true)
//			});
//			typeinfosw.add(ti_array);

		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		return typeinfosw;
	}
	
	/**
	 *  Convert to a string.
	 */
	public static String objectToXML(Object val, ClassLoader classloader)
	{
		if(writer==null)
			writer = new JavaWriter(null);
		return Writer.objectToXML(writer, val, classloader);
	}
	
	/**
	 *  Convert to a byte array.
	 */
	public static byte[] objectToByteArray(Object val, ClassLoader classloader)
	{
		if(writer==null)
			writer = new JavaWriter(null);
		return Writer.objectToByteArray(writer, val, classloader);
	}
}
