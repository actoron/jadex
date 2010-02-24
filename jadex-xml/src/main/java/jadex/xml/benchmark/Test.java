package jadex.xml.benchmark;

import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  Testcases for writer and reader.
 */
public class Test //extends TestCase
{
	//-------- methods --------
	
	/**
	 *  Main for testing single methods.
	 */
	public static void main(String[] args)
	{
		Test t = new Test();
		
		try
		{
			int cnt = 1;
			long start = System.currentTimeMillis();
			for(int i=0; i<cnt; i++)
//			while(true)
			{
//				t.testBean();
//				t.testArrayOrder();
//				t.testMultiArray();
//				t.testVectorModel();
//				t.testClass();
//				t.testDate();
//				t.testColor();
//				t.testArray();
//				t.testList();
//				t.testSet();
				t.testMap();
//				t.testInnerClass();
			}
			long dur = System.currentTimeMillis()-start;
			
			System.out.println("Needed: "+dur+" for cnt="+cnt);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	//-------- test methods --------
	
	/**
	 *  Method for writing and reading an object.
	 */
	protected void doWriteAndRead(Object wo) throws Exception
	{
		String xml = JavaWriter.objectToXML(wo, null);
		
//		System.out.println("xml is:"+xml);
		
		Object ro = JavaReader.objectFromXML(xml, null);
		
//		System.out.println("Write: "+wo);
//		FileOutputStream fos = new FileOutputStream("test.xml");
//		writer.write(wo, fos, null, null);
//		fos.close();
//		
//		FileInputStream fis = new FileInputStream("test.xml");
//		Object ro = reader.read(fis, null, null);
//		fis.close();
//		System.out.println("Read: "+ro+" / class="+ro.getClass());
		
//		System.out.println("equals: "+wo.equals(ro));
		if(!wo.equals(ro) && !(wo.getClass().isArray() && Arrays.deepEquals((Object[])wo, (Object[])ro)))
			System.out.println("Not equal: "+wo.getClass()+" \n"+ro.getClass()+" \n"+xml);
		
//		assertEquals("Written and read objects should be equal:", wo, ro);
	}
	
	/**
	 * Test if the order of an array is preserved when (de-)serializing.
	 */
	private void testArrayOrder() throws Exception
	{
		Object[][] data = new Object[1][8];

		data[0][0] = new Long(1);
		data[0][1] = "A";
		data[0][2] = "";
		data[0][3] = "";
		data[0][4] = "B";
		data[0][5] = null;
		data[0][6] = "";
		data[0][7] = new Long(2);
        
		doWriteAndRead(data);
	}

	
	/**
	 *  Test if multi array transfer works.
	 */
	public void testMultiArray() throws Exception
	{
//		String[][] array = new String[3][2]; 
//		array[0][0] = "a";
//		array[1][0] = "b";
//		array[2][0] = "c";
		
		Object[][] data = new Object[1][8];

		data[0][0] = new Long(1);
		data[0][1] = "Hallo";
		data[0][2] = "";
		data[0][3] = "";
		data[0][4] = "Moin";
		data[0][5] = null;
		data[0][6] = "";
		data[0][7] = new Long(2);
		
		doWriteAndRead(data);
	}
	
	/**
	 *  Test if vector model transfer works.
	 */
	public void testVectorModel() throws Exception
	{
		VectorModel vm = new VectorModel();
//		vm.addToV1("a");
		doWriteAndRead(vm);
	}
	
	/**
	 *  Test if class transfer works.
	 */
	public void testClass() throws Exception
	{
		doWriteAndRead(boolean.class);
	}
	
	/**
	 *  Test if date transfer works.
	 */
	public void testDate() throws Exception
	{
		doWriteAndRead(new java.util.Date());
	}
	
	/**
	 *  Test if color transfer works.
	 */
	public void testColor() throws Exception
	{
		doWriteAndRead(new java.awt.Color(200, 100, 50));
	}
	
	/**
	 *  Test if array transfer works.
	 */
	public void testArray() throws Exception
	{
		Object[] array = new Object[6]; 
		array[0] = new Integer(0);
//		array[1] = new Integer(1);
		array[2] = new Integer(2);
		array[3] = new Integer(3);
		array[4] = new Integer(4);
		array[5] = new Integer(5);
		
		doWriteAndRead(array);
	}
	
	/**
	 *  Test if bean transfer works.
	 */
	public void testBean() throws Exception
	{
		doWriteAndRead(getABean());
	}
	
	/**
	 *  Test list transfer works.
	 */
	public void testList() throws Exception
	{
		List list = new ArrayList();
		list.add("str_a");
		list.add(new Integer(2));
		list.add(getABean());
		
		doWriteAndRead(list);
	}
	
	/**
	 *  Test if set transfer works.
	 */
	public void testSet() throws Exception
	{
		Set set = new HashSet();
		set.add("str_a");
		set.add(new Integer(2));
		set.add(getABean());
		
		doWriteAndRead(getABean());
	}
	
	/**
	 *  Test if map transfer works.
	 */
	public void testMap() throws Exception
	{
		Map map = new LinkedHashMap();
		map.put("$", "A");
		map.put(new Integer(2), new Integer(22));
		map.put("obja", getABean());
//		for(Iterator it=map.entrySet().iterator(); it.hasNext(); )
//		{
//			Map.Entry e = (Map.Entry)it.next();
//			System.out.println("key="+e.getKey()+" value="+e.getValue());
//		}
		
		doWriteAndRead(map);
	}
	
	/**
	 *  Test if inner class transfer works.
	 */
	public void testInnerClass() throws Exception
	{
		InnerClass ic = new InnerClass("some string");
		
		doWriteAndRead(ic);
	}
	
	/**
	 *  Get some bean.
	 */
	protected Object getABean()
	{
		B b1 = new B("test b1");
		B b2 = new B("test b2");
		B b3 = new B("test b3");
		B b4 = new B("test b4");
		A a = new A(10, "test a", b1, new B[]{b1, b2, b3, b4});
		return a;
	}
	
	public static class InnerClass
	{
		protected String string;
		
		public InnerClass()
		{
		}
		
		public InnerClass(String string)
		{
			this.string = string;
		}

		public String getString() 
		{
			return string;
		}

		public void setString(String string) 
		{
			this.string = string;
		}

		public int hashCode() 
		{
			final int prime = 31;
			int result = 1;
			result = prime * result + ((string == null) ? 0 : string.hashCode());
			return result;
		}

		public boolean equals(Object obj) 
		{
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			InnerClass other = (InnerClass) obj;
			if (string == null) {
				if (other.string != null)
					return false;
			} else if (!string.equals(other.string))
				return false;
			return true;
		}
	}
}
