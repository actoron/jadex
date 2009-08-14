package jadex.commons.xml.bean;

import jadex.commons.xml.AttributeInfo;
import jadex.commons.xml.SubobjectInfo;
import jadex.commons.xml.TypeInfo;
import jadex.commons.xml.reader.Reader;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * 
 */
public class JavaReader extends Reader
{
	/** The reader. */
	protected static Reader reader;
	
	/**
	 *  Create a new reader.
	 *  @param handler The handler.
	 */
	public JavaReader(Set typeinfos)
	{
		super(new BeanObjectReaderHandler(), joinTypeInfos(typeinfos));
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
		Set typeinfosr = new HashSet();
		try
		{
			// java.util.HashMap
			
			TypeInfo ti_hashmapr = new TypeInfo(null, "java.util.HashMap", Map.class, null, null, null, null, null,
				new SubobjectInfo[]{
				new SubobjectInfo(new BeanAttributeInfo("entry", null, 
					null, new MapEntryConverter(), null, "", null, null, Map.class.getMethod("put", new Class[]{Object.class, Object.class}), MapEntry.class.getMethod("getKey", new Class[0])))
			});
			typeinfosr.add(ti_hashmapr);
			TypeInfo ti_linkedhashmapr = new TypeInfo(null, "java.util.LinkedHashMap", Map.class, null, null, null, null, null,
				new SubobjectInfo[]{
				new SubobjectInfo(new BeanAttributeInfo("entry", null, 
					null, new MapEntryConverter(), null, "", null, null, Map.class.getMethod("put", new Class[]{Object.class, Object.class}), MapEntry.class.getMethod("getKey", new Class[0])))
			});
			typeinfosr.add(ti_linkedhashmapr);
			
			TypeInfo ti_hashmapentryr = new TypeInfo(null, "entry", MapEntry.class, null, null, null, null, null,
				new SubobjectInfo[]{
				new SubobjectInfo(new BeanAttributeInfo("key", "key", 
					null, null, null, null, null, Map.Entry.class.getMethod("getKey", new Class[0]), null)),
				new SubobjectInfo(new BeanAttributeInfo("value", "value", 
					null, null, null, null, null, Map.Entry.class.getMethod("getValue", new Class[0]), null))
			});
			typeinfosr.add(ti_hashmapentryr);
			
			// java.util.ArrayList
			
			TypeInfo ti_arraylist = new TypeInfo(null, "java.util.ArrayList", List.class, null, null, null, null, null,
				new SubobjectInfo[]{
				new SubobjectInfo(new BeanAttributeInfo("entries", AttributeInfo.THIS,
					null, null, null, null, null, null, ArrayList.class.getMethod("add", new Class[]{Object.class})))
			});
			typeinfosr.add(ti_arraylist);
			
			// java.util.HashSet
			
			TypeInfo ti_hashset = new TypeInfo(null, "java.util.HashSet", Set.class, null, null, null, null, null,
				new SubobjectInfo[]{
				new SubobjectInfo(new BeanAttributeInfo("entries", AttributeInfo.THIS,
					null, null, null, null, null, null, HashSet.class.getMethod("add", new Class[]{Object.class})))
			});
			typeinfosr.add(ti_hashset);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		return typeinfosr;
	}
	
	/**
	 *  @param val The string value.
	 *  @return The encoded object.
	 */
	public static Object objectFromXML(String val, ClassLoader classloader)
	{
		if(reader==null)
			reader = new JavaReader(null);
		return Reader.objectFromXML(reader, val, classloader);
	}
	
	/**
	 * 
	 */
	public static Object objectFromByteArray(byte[] val, ClassLoader classloader)
	{
		if(reader==null)
			reader = new JavaReader(null);
		return Reader.objectFromByteArray(reader, val, classloader);
	}
}
