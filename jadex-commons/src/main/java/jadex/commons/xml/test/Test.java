package jadex.commons.xml.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.commons.SReflect;
import jadex.commons.xml.AttributeInfo;
import jadex.commons.xml.SubobjectInfo;
import jadex.commons.xml.TypeInfo;
import jadex.commons.xml.bean.BeanAttributeInfo;
import jadex.commons.xml.bean.BeanObjectReaderHandler;
import jadex.commons.xml.bean.BeanObjectWriterHandler;
import jadex.commons.xml.reader.Reader;
import jadex.commons.xml.writer.Writer;

public class Test
{
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		try
		{
			B b1 = new B("test b1");
			B b2 = new B("test b2");
			B b3 = new B("test b3");
			B b4 = new B("test b4");
			A a = new A(10, "test a", b1, new B[]{b1, b2, b3, b4});
			
			Set typeinfos = new HashSet();
			
			typeinfos.add(new TypeInfo(null, "java.util.HashMap", HashMap.class, null, null, null, null, null,
				new SubobjectInfo[]{
				new SubobjectInfo(new BeanAttributeInfo("entries", "entrySet", 
					null, null, null, null, null, HashMap.class.getMethod("entrySet", new Class[0]), null))
			}));
			typeinfos.add(new TypeInfo(null, "entry", "java.util.HashMap$Entry", null, null, null, null, null,
				new SubobjectInfo[]{
				new SubobjectInfo(new BeanAttributeInfo("key", "key", 
					null, null, null, null, null, Map.Entry.class.getMethod("getKey", new Class[0]), null)),
				new SubobjectInfo(new BeanAttributeInfo("value", "value", 
					null, null, null, null, null, Map.Entry.class.getMethod("getValue", new Class[0]), null))
			}));
			
			typeinfos.add(new TypeInfo(null, "java.util.ArrayList", ArrayList.class, null, null, null, null, null,
				new SubobjectInfo[]{
				new SubobjectInfo(new BeanAttributeInfo("entries", AttributeInfo.THIS,
					null, null, null, null, null, null, ArrayList.class.getMethod("add", new Class[]{Object.class})))
			}));
			
			Map map = new HashMap();
			map.put("$", "A");
			map.put(new Integer(2), new Integer(22));
			map.put("obja", a);
			for(Iterator it=map.entrySet().iterator(); it.hasNext(); )
			{
				Map.Entry e = (Map.Entry)it.next();
				System.out.println("key="+e.getKey()+" value="+e.getValue());
			}
			
			List list = new ArrayList();
			list.add("str_a");
			list.add(new Integer(2));
			list.add(a);
			
//			TypeInfo tia = new TypeInfo("a", A.class);
//			TypeInfo tib = new TypeInfo("b", B.class);

			System.out.println("Write: "+a);
			FileOutputStream fos = new FileOutputStream("test.xml");
			Writer w = new Writer(new BeanObjectWriterHandler(true), typeinfos);
//			w.write(a, fos, null, null);
			w.write(map, fos, null, null);
			fos.close();
//			System.out.println(Nuggets.objectToXML(a, null));
			
			
			FileInputStream fis = new FileInputStream("test.xml");
			Reader r = new Reader(new BeanObjectReaderHandler(), typeinfos);
			Object obj = r.read(fis, null, null);
			System.out.println("Read: "+obj+" / class="+obj.getClass());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
