package jadex.commons.xml.bean;

import jadex.commons.xml.AttributeInfo;
import jadex.commons.xml.QName;
import jadex.commons.xml.SubobjectInfo;
import jadex.commons.xml.TypeInfo;
import jadex.commons.xml.writer.Writer;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Java specific reader that supports collection classes and arrays.
 */
public class JavaWriter extends Writer
{
	//-------- attributes --------
	
	/** The static writer instance. */
	protected static Writer writer;
	
	//-------- constructors --------
	
	/**
	 *  Create a new reader.
	 *  @param handler The handler.
	 */
	public JavaWriter(Set typeinfos)
	{
		super(new BeanObjectWriterHandler(true, joinTypeInfos(typeinfos)));
	}

	//-------- methods --------
	
	/**
	 *  Join sets of typeinfos.
	 *  @param typeinfos The user specific type infos. 
	 *  @return The joined type infos.
	 */
	public static Set joinTypeInfos(Set typeinfos)
	{
		Set ret = getTypeInfos();
		if(typeinfos!=null)
			ret.addAll(typeinfos);
		return ret;
	}
	
	/**
	 *  Get the java type infos.
	 */
	public static Set getTypeInfos()
	{
		Set typeinfos = new HashSet();
		
		try
		{
			// java.util.Map
			
			TypeInfo ti_map = new TypeInfo(null, (String)null, Map.class, null, null, null, null, null,
				new SubobjectInfo[]{
				new SubobjectInfo(new BeanAttributeInfo(new QName("entries"), "entrySet", 
					null, null, null, null, null, Map.class.getMethod("entrySet", new Class[0]), null), null, null, true)
			});
			typeinfos.add(ti_map);
			
			// Cannot let xmltag be null, because class name then contains $ which is not allowed in a tag
			TypeInfo ti_mapentry = new TypeInfo(null, "entry", Map.Entry.class, null, null, null, null, null,
				new SubobjectInfo[]{
				new SubobjectInfo(new BeanAttributeInfo(new QName("key"), "key", 
					null, null, null, null, null, Map.Entry.class.getMethod("getKey", new Class[0]), null)),
				new SubobjectInfo(new BeanAttributeInfo(new QName("value"), "value", 
					null, null, null, null, null, Map.Entry.class.getMethod("getValue", new Class[0]), null))
			});
			typeinfos.add(ti_mapentry);
			
			// java.util.List
			
			TypeInfo ti_list = new TypeInfo(null, (String)null, List.class, null, null, null, null, null,
				new SubobjectInfo[]{
				new SubobjectInfo(new BeanAttributeInfo(new QName("entries"), AttributeInfo.THIS), null, null, true)
			});
			typeinfos.add(ti_list);
			
			// java.util.Set
			
			TypeInfo ti_set = new TypeInfo(null, (String)null, Set.class, null, null, null, null, null,
				new SubobjectInfo[]{
				new SubobjectInfo(new BeanAttributeInfo(new QName("entries"), AttributeInfo.THIS), null, null, true)
			});
			typeinfos.add(ti_set);
			
			// Array
			
			TypeInfo ti_array = new TypeInfo(null, (String)null, Object[].class, null, null, null, null, null,
				new SubobjectInfo[]{
				new SubobjectInfo(new BeanAttributeInfo(new QName("entries"), AttributeInfo.THIS), null, null, true)
			});
			typeinfos.add(ti_array);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		return typeinfos;
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
