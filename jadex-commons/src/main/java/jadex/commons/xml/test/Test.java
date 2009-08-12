package jadex.commons.xml.test;

import jadex.commons.xml.AttributeInfo;
import jadex.commons.xml.ITypeConverter;
import jadex.commons.xml.SubobjectInfo;
import jadex.commons.xml.TypeInfo;
import jadex.commons.xml.bean.BeanAttributeInfo;
import jadex.commons.xml.bean.BeanObjectReaderHandler;
import jadex.commons.xml.bean.BeanObjectWriterHandler;
import jadex.commons.xml.reader.Reader;
import jadex.commons.xml.writer.Writer;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import junit.framework.TestCase;

/**
 * 
 */
public class Test extends TestCase
{
	protected Writer writer;
	
	protected Reader reader;
	
	public static void main(String[] args)
	{
		Test t = new Test();
		try
		{
			t.setUp();
			t.testList();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 *  Test setup.
	 */
	protected void setUp() throws Exception
	{	
		Set typeinfosr = new HashSet();
		Set typeinfosw = new HashSet();
		
		// java.util.HashMap
		
		TypeInfo ti_hashmapr = new TypeInfo(null, "java.util.HashMap", HashMap.class, null, null, null, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo("entry", null, 
				null, new MapEntryConverter(), null, "", null, null, HashMap.class.getMethod("put", new Class[]{Object.class, Object.class}), MapEntry.class.getMethod("getKey", new Class[0])))
		});
		TypeInfo ti_hashmapw = new TypeInfo(null, "java.util.HashMap", HashMap.class, null, null, null, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo("entries", "entrySet", 
				null, null, null, null, null, HashMap.class.getMethod("entrySet", new Class[0]), null))
		});
		typeinfosw.add(ti_hashmapw);
		typeinfosr.add(ti_hashmapr);
		
		TypeInfo ti_hashmapentryw = new TypeInfo(null, "entry", "java.util.HashMap$Entry", null, null, null, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo("key", "key", 
				null, null, null, null, null, Map.Entry.class.getMethod("getKey", new Class[0]), null)),
			new SubobjectInfo(new BeanAttributeInfo("value", "value", 
				null, null, null, null, null, Map.Entry.class.getMethod("getValue", new Class[0]), null))
		});
		TypeInfo ti_hashmapentryr = new TypeInfo(null, "entry", MapEntry.class, null, null, null, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo("key", "key", 
				null, null, null, null, null, Map.Entry.class.getMethod("getKey", new Class[0]), null)),
			new SubobjectInfo(new BeanAttributeInfo("value", "value", 
				null, null, null, null, null, Map.Entry.class.getMethod("getValue", new Class[0]), null))
		});
		typeinfosw.add(ti_hashmapentryw);
		typeinfosr.add(ti_hashmapentryr);
		
		// java.util.ArrayList
		
		TypeInfo ti_arraylist = new TypeInfo(null, "java.util.ArrayList", ArrayList.class, null, null, null, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo("entries", AttributeInfo.THIS,
				null, null, null, null, null, null, ArrayList.class.getMethod("add", new Class[]{Object.class})))
		});
		typeinfosw.add(ti_arraylist);
		typeinfosr.add(ti_arraylist);
		
		// java.util.HashSet
		
		TypeInfo ti_hashset = new TypeInfo(null, "java.util.HashSet", ArrayList.class, null, null, null, null, null,
			new SubobjectInfo[]{
			new SubobjectInfo(new BeanAttributeInfo("entries", AttributeInfo.THIS,
				null, null, null, null, null, null, ArrayList.class.getMethod("add", new Class[]{Object.class})))
		});
		typeinfosw.add(ti_hashset);
		typeinfosr.add(ti_hashset);

		writer = new Writer(new BeanObjectWriterHandler(true), typeinfosw);	
		reader = new Reader(new BeanObjectReaderHandler(), typeinfosr);
	}
	
	//-------- test methods --------
	
	/**
	 * 
	 */
	protected void doWriteAndRead(Object wo) throws Exception
	{
		System.out.println("Write: "+wo);
		FileOutputStream fos = new FileOutputStream("test.xml");
		writer.write(wo, fos, null, null);
		fos.close();
		
		FileInputStream fis = new FileInputStream("test.xml");
		Object ro = reader.read(fis, null, null);
		fis.close();
		System.out.println("Read: "+ro+" / class="+ro.getClass());
		
		System.out.println("equals: "+wo.equals(ro));
		
//		assertEquals("Written and read objects should be equal:", wo, ro);
	}
	
	/**
	 * 
	 */
	public void testBean() throws Exception
	{
		B b1 = new B("test b1");
		B b2 = new B("test b2");
		B b3 = new B("test b3");
		B b4 = new B("test b4");
		A a = new A(10, "test a", b1, new B[]{b1, b2, b3, b4});
		
		doWriteAndRead(a);
	}
	
	/**
	 *  
	 */
	public void testList() throws Exception
	{
		B b1 = new B("test b1");
		B b2 = new B("test b2");
		B b3 = new B("test b3");
		B b4 = new B("test b4");
		A a = new A(10, "test a", b1, new B[]{b1, b2, b3, b4});
		
		List list = new ArrayList();
		list.add("str_a");
		list.add(new Integer(2));
		list.add(a);
		
		doWriteAndRead(list);
	}
	
	/**
	 *   
	 */
	public void testSet() throws Exception
	{
		B b1 = new B("test b1");
		B b2 = new B("test b2");
		B b3 = new B("test b3");
		B b4 = new B("test b4");
		A a = new A(10, "test a", b1, new B[]{b1, b2, b3, b4});
		
		Set set = new HashSet();
		set.add("str_a");
		set.add(new Integer(2));
		set.add(a);
		
		doWriteAndRead(set);
	}
	
	/**
	 * 
	 */
	public void testMap() throws Exception
	{
		B b1 = new B("test b1");
		B b2 = new B("test b2");
		B b3 = new B("test b3");
		B b4 = new B("test b4");
		A a = new A(10, "test a", b1, new B[]{b1, b2, b3, b4});
		
		Map map = new HashMap();
		map.put("$", "A");
		map.put(new Integer(2), new Integer(22));
		map.put("obja", a);
		for(Iterator it=map.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry e = (Map.Entry)it.next();
			System.out.println("key="+e.getKey()+" value="+e.getValue());
		}
		
		doWriteAndRead(a);
	}

}

class MapEntryConverter implements ITypeConverter
{
	public Object convertObject(Object val, Object root, ClassLoader classloader, Object context)
	{
		return ((MapEntry)val).getValue();
	}

	public boolean acceptsInputType(Class inputtype)
	{
		return true;
	}
}
