package jadex.commons.transformation;

import junit.framework.TestCase;

import org.spongycastle.x509.X509V1CertificateGenerator;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.net.InetAddress;
import java.net.URI;
import java.net.URL;
import java.security.GeneralSecurityException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.KeyStore;
import java.security.SecureRandom;
import java.security.cert.Certificate;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import javax.security.auth.x500.X500Principal;

import jadex.bridge.ClassInfo;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple;
import jadex.commons.Tuple2;
import jadex.commons.collection.ILRUEntryCleaner;
import jadex.commons.collection.LRU;
import jadex.commons.collection.MultiCollection;
import jadex.commons.transformation.annotations.Classname;

/**
 *  Testcases for writer and reader.
 */
public abstract class Test extends TestCase
{
	/**
	 * 
	 */
	public void performTests()
	{
		performTests(1000);
	}
	
	/**
	 * 
	 */
	public void performTests(int cnt)
	{
//		try
//		{
//			InputStream is = SUtil.getResource("jadex/xml/test.xml", null);
//			Object object = JavaReader.getInstance().read(is, null, null);
//			System.out.println("read: "+object);
//			String xml = JavaWriter.objectToXML(object, null);
//			System.out.println("wrote: "+xml);
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}

		try
		{

//			int cnt = 1000;
			long start = System.currentTimeMillis();
			for(int i=0; i<cnt; i++)
			{
//			while(true)
//			{
				testMultiCollection();

				testByte();

				testCalendar();

				testException();

				testCertificate();

				testTimestamp();

				testEnum();

				testByte();
				testDouble();
				testBigInteger();
//				testBigData();

				testByteArray();
				testBByteArray();
				testIntArray();
				testIntegerArray();
				testDoubleArray();
				testBDoubleArray();
				testFloatArray();
				testBFloatArray();
				testLongArray();
				testBLongArray();
				testCharArray();
				testCharacterArray();
				testShortArray();
				testBShortArray();
				testBooleanArray();
				testBBooleanArray();

				testVectorModel();
				testEmptySet();
				testEmptyList();
				testEmptyMap();
				testArray();
				testList();
				testSet();
				testEmptyArray();
				testArrayOrder();
				testMultiArray();
				testMultiArray2();
				testMultiArrayAttribute();
				testByteArrayAttribute();

				testClass();
				testClassInfo();
				testDate();
				testUUID();
				testInnerClass();
				testURL();
				testURI();
				testLoggingLevel();
				testLogRecord();
				testInetAddress();
				testTuple();
				testTuple2();
				testTimestamp();

				testBean();
				testExcluded();

				testAnonymousInnerClass();
				testAnonymousInnerClassWithSimpleTypes();

				testColor();
				testImage();
				testRectangle();
				testMap();
				testLRU();

				testSpecialCharacter();
				testBeanWithPublicFields();
				testBeanWithIncludedFields();
				testBeanWithIncludedPrivateFields();
				testBeanWithIncludedSinglePrivateFields();
				testBeanWithIncludedInheritedPrivateFields();
				testBeanWithIncludedStaticFinalField();
				testSelfReferenceBean();

				testOptionalsPrimitive();
				testOptionalsComplex();
				testOptionalsCollection();

				testDateArray();
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
	protected Object doWriteAndRead(Object wo) throws Exception
	{
		return doWriteAndRead(wo, null);
	}
	
	/**
	 *  Method for writing and reading an object.
	 */
	protected Object doWriteAndRead(Object wo, Comparator comp) throws Exception
	{
		//(new RuntimeException()).printStackTrace();
		Object written = doWrite(wo);
		
//		System.out.println("written is:"+new String((byte[])written));
		
		Object ro = doRead(written);
		
//		String xml = JavaWriter.objectToXML(wo, null);
		
//		System.out.println("xml is:"+xml);
		
//		Object ro = JavaReader.objectFromXML(xml, null);
		
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
		compare(wo, ro, written, comp);
		
//		assertEquals("Written and read objects should be equal:", wo, ro);
		
		return ro;
	}
	
	/**
	 * 
	 */
	public abstract Object doWrite(Object wo);
	
	/**
	 * 
	 */
	public abstract Object doRead(Object ro);
	
	/**
	 * 
	 */
	public void compare(Object wo, Object ro, Object written, Comparator comp)
	{
		if(comp!=null)
		{
			if(comp.compare(wo, ro)!=0)
			{
				throw new RuntimeException("Not equal: "+wo+", "+ro+"\n"
					+wo.getClass()+" \n"+ro.getClass()+" \n"+written);
			}
		}
		else
		{
			if(!wo.equals(ro) && !(wo.getClass().isArray() && Arrays.deepEquals((Object[])wo, (Object[])ro)))
			{
				if(wo instanceof String && ro instanceof String)
				{
					char[]	woc	= ((String)wo).toCharArray();
					StringBuffer	wocs	= new StringBuffer();
					wocs.append("[");
					for(int i=0; i<woc.length; i++)
					{
						wocs.append(Integer.toHexString(woc[i] | 0x10000).substring(1));
						if(i<woc.length-1)
						{
							wocs.append(",");
						}
					}
					wocs.append("]");

					char[]	roc	= ((String)ro).toCharArray();
					StringBuffer	rocs	= new StringBuffer();
					rocs.append("[");
					for(int i=0; i<roc.length; i++)
					{
						rocs.append(Integer.toHexString(roc[i] | 0x10000).substring(1));
						if(i<roc.length-1)
						{
							rocs.append(",");
						}
					}
					rocs.append("]");

					throw new RuntimeException("Strings not equal2: "+wo+", "+ro+"\n"
						+wocs+", "+rocs+" \n"
						+SUtil.arrayToString(written)+", "+new String((byte[])written));
				}
				else
				{
					throw new RuntimeException("Not equal: "+wo+", "+ro+"\n"
						+wo.getClass()+" \n"+ro.getClass()+" \n"+written);
				}
			}
		}
	}
	
	/**
	 * 
	 */
	public void testCalendar() throws Exception
	{
//		System.out.println("test enum: "+(TestEnum.A instanceof Enum));
		GregorianCalendar gc = new GregorianCalendar(1999, 12, 12, 12, 12);
		doWriteAndRead(gc);
	}
	
	/**
	 * 
	 */
	public void testBigInteger() throws Exception
	{
//		System.out.println("test enum: "+(TestEnum.A instanceof Enum));
		BigInteger bi = new BigInteger("123456789");
		doWriteAndRead(bi);
	}
	
	/**
	 * 
	 */
	public void testUUID() throws Exception
	{
//		System.out.println("test enum: "+(TestEnum.A instanceof Enum));
		doWriteAndRead(UUID.randomUUID());
	}
	
	/**
	 * 
	 */
	public void testEnum() throws Exception
	{
//		System.out.println("test enum: "+(TestEnum.A instanceof Enum));
		doWriteAndRead(TestEnum.A);
	}
	
	/**
	 * 
	 */
	public void testByte() throws Exception
	{
		byte data = 55;
		
		doWriteAndRead(data);
	}
	
	/**
	 * 
	 */
	public void testDouble() throws Exception
	{
		double data = 1E6*1.00001;
		
		doWriteAndRead(data);
	}
	
	/**
	 * 
	 */
	public void testByteArray() throws Exception
	{
		byte[] data = "hello world".getBytes("UTF-8");
		
		doWriteAndRead(data, new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				return Arrays.equals((byte[])o1, (byte[])o2)? 0: -1;
			}
		});
	}
	
	/**
	 * 
	 */
	public void testBByteArray() throws Exception
	{
		byte[] tmp = "hello world".getBytes("UTF-8");
		Byte[] data = new Byte[tmp.length];
		for(int i=0; i<tmp.length; i++)
			data[i] = Byte.valueOf(tmp[i]);
		
		doWriteAndRead(data);
	}
	
	/**
	 * 
	 */
	public void testIntArray() throws Exception
	{
		int[] data = new int[]{1,2,3,4,5,6};
		
		doWriteAndRead(data, new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				return Arrays.equals((int[])o1, (int[])o2)? 0: -1;
			}
		});
	}
	
	/**
	 * 
	 */
	public void testIntegerArray() throws Exception
	{
		Integer[] data = new Integer[]{Integer.valueOf(1), Integer.valueOf(2), Integer.valueOf(3)};
		
		doWriteAndRead(data); 
	}
	
	/**
	 * 
	 */
	public void testDoubleArray() throws Exception
	{
		double[] data = new double[]{1E6*1.00001,1.0,3.0,4.00001,5.00002,6.99999};
		
		doWriteAndRead(data, new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				return Arrays.equals((double[])o1, (double[])o2)? 0: -1;
			}
		});
	}
	
	/**
	 * 
	 */
	public void testBDoubleArray() throws Exception
	{
		Double[] data = new Double[]{Double.valueOf(1), Double.valueOf(2), Double.valueOf(3)};
		
		doWriteAndRead(data); 
	}
	
	/**
	 * 
	 */
	public void testFloatArray() throws Exception
	{
		float[] data = new float[]{1.01f,1.0f,3.0f,4.00001f,5.00002f,6.99999f};
		
		doWriteAndRead(data, new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				return Arrays.equals((float[])o1, (float[])o2)? 0: -1;
			}
		});
	}
	
	/**
	 * 
	 */
	public void testBFloatArray() throws Exception
	{
		Float[] data = new Float[]{Float.valueOf(1), Float.valueOf(2), Float.valueOf(3)};
		
		doWriteAndRead(data); 
	}
	
	/**
	 * 
	 */
	public void testLongArray() throws Exception
	{
		long[] data = new long[]{1000000000,1,3,4,5,699999};
		
		doWriteAndRead(data, new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				return Arrays.equals((long[])o1, (long[])o2)? 0: -1;
			}
		});
	}
	
	/**
	 * 
	 */
	public void testBLongArray() throws Exception
	{
		Long[] data = new Long[]{Long.valueOf(1), Long.valueOf(2), Long.valueOf(3)};
		
		doWriteAndRead(data); 
	}
	
	/**
	 * 
	 */
	public void testCharArray() throws Exception
	{
		char[] data = new char[]{'a','b','c'};
		
		doWriteAndRead(data, new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				return Arrays.equals((char[])o1, (char[])o2)? 0: -1;
			}
		});
	}
	
	/**
	 * 
	 */
	public void testCharacterArray() throws Exception
	{
		Character[] data = new Character[]{Character.valueOf('a'), Character.valueOf('b'), Character.valueOf('c')};
		
		doWriteAndRead(data); 
	}
	
	/**
	 * 
	 */
	public void testShortArray() throws Exception
	{
		short[] data = new short[]{1,2,3,4,5,6};
		
		doWriteAndRead(data, new Comparator()
		{
			public int compare(Object o1, Object o2)
			{objs:
				return Arrays.equals((short[])o1, (short[])o2)? 0: -1;
			}
		});
	}
	
	/**
	 * 
	 */
	public void testBShortArray() throws Exception
	{
		Short[] data = new Short[]{Short.valueOf((short)1), Short.valueOf((short)2), Short.valueOf((short)3)};
		
		doWriteAndRead(data); 
	}
	
	/**
	 * 
	 */
	public void testBooleanArray() throws Exception
	{
		boolean[] data = new boolean[]{true,false,false,true,true,false};
		
		doWriteAndRead(data, new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				return Arrays.equals((boolean[])o1, (boolean[])o2)? 0: -1;
			}
		});
	}
	
	/**
	 * 
	 */
	public void testBBooleanArray() throws Exception
	{
		Boolean[] data = new Boolean[]{Boolean.TRUE, Boolean.FALSE, Boolean.TRUE};
		
		doWriteAndRead(data); 
	}
	
//	/**
//	 * 
//	 */
//	public void testBigData() throws Exception
//	{
////		File f = new File("C:\\zips\\cd-ripper\\easy-cd-ripper.exe");
//		File f = new File("C:\\zips\\aida32ee_393.zip");
//		FileInputStream fis = new FileInputStream(f);
//		byte[] data = new byte[(int)f.length()];
//		fis.read(data);
//		String bd = new String(Base64.encode(data));
//		
//		doWriteAndRead(bd);
//	}
	
	/**
	 * 
	 */
	public void testMultiCollection() throws Exception
	{
		MultiCollection<String, String> col = new MultiCollection<String, String>();
		col.add("a", "a");
		col.add("a", "b");
		col.add("a", "c");
		col.add("b", "b");

		doWriteAndRead(col);
	}
	
	/**
	 * 
	 */
	public void testImage() throws Exception
	{
		BufferedImage bi = new BufferedImage(70,70,BufferedImage.TYPE_INT_ARGB);
		Graphics g = bi.getGraphics();
		g.setColor(Color.BLUE);
		g.draw3DRect(10, 10, 50, 50, true);

//		JFrame test = new JFrame();
//		test.setLayout(new FlowLayout());
//		test.add(new JLabel(new ImageIcon(bi)));
//		ByteArrayOutputStream baos = new ByteArrayOutputStream();
//		ImageIO.write(bi,"PNG", baos);
//		byte[] buf = baos.toByteArray();
//		Image img = ImageIO.read(new ByteArrayInputStream(buf));
//		test.add(new JLabel(new ImageIcon(img)));
//		test.pack();
//		test.setVisible(true);
//		System.out.println("buf: "+SUtil.arrayToString(buf));

		doWriteAndRead(bi, new Comparator()
		{
			public int compare(Object o1, Object o2)
			{
				BufferedImage	b1	= (BufferedImage)o1;
				BufferedImage	b2	= (BufferedImage)o2;
				boolean	equal	= b1.getWidth()==b2.getWidth() && b1.getHeight()==b2.getHeight();
				for(int x=0; equal && x<b1.getWidth(); x++)
				{
					for(int y=0; equal && y<b1.getHeight(); y++)
					{
						equal	= b1.getRGB(x, y)==b2.getRGB(x, y);
					}					
				}
				return equal ? 0 : 1;
			}
		});
	}
	
	/**
	 *  Test if awt rectangle works.
	 */
	public void testRectangle() throws Exception
	{
		doWriteAndRead(new Rectangle(new Dimension(20, 30)));
	}
	
	/**
	 *  Test if empty set constant works.
	 */
	public void testEmptySet() throws Exception
	{
		doWriteAndRead(Collections.EMPTY_SET);
	}
	
	/**
	 *  Test if empty list constant works.
	 */
	public void testEmptyList() throws Exception
	{
		doWriteAndRead(Collections.EMPTY_LIST);
	}
	
	/**
	 *  Test if empty map constant works.
	 */
	public void testEmptyMap() throws Exception
	{
		doWriteAndRead(Collections.EMPTY_MAP);
	}
	
	/**
	 *  Test if multi array transfer works.
	 */
	public void testEmptyArray() throws Exception
	{
		int[] data = new int[0];
		A a = new A();
		a.setInts(data);
		
		doWriteAndRead(a);
	}
	
	/**
	 * Test if the order of an array is preserved when (de-)serializing.
	 */
	public void testArrayOrder() throws Exception
	{
		Object[][] data = new Object[1][8];

		data[0][0] = Long.valueOf(1);
		data[0][1] = "A";
		data[0][2] = "";
		data[0][3] = "";
		data[0][4] = "B";
		data[0][5] = null;
		data[0][6] = "";
		data[0][7] = Long.valueOf(2);
        
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

		data[0][0] = Long.valueOf(1);
		data[0][1] = "Hallo";
		data[0][2] = "";
		data[0][3] = "";
		data[0][4] = "Moin";
		data[0][5] = null;
		data[0][6] = "";
		data[0][7] = Long.valueOf(2);
		
		doWriteAndRead(data);
	}
	
	/**
	 *  Test if multi array transfer works.
	 */
	public void testMultiArray2() throws Exception
	{
//		String[][] array = new String[3][2]; 
//		array[0][0] = "a";
//		array[1][0] = "b";
//		array[2][0] = "c";
		
		Object[][] data = new Object[2][];

		data[0] = new Object[]{1,2};
		data[1] = new Object[]{3,4,5};
		
		doWriteAndRead(data);
	}
	
	/**
	 *  Test if multi array attribute transfer works.
	 */
	public void testMultiArrayAttribute() throws Exception
	{
//		int[][]	data	= new int[][]
//		{
//			{1, 2, 3},
//			{4, 5, 6}
//		};
		ArrayHolder	ad	= new ArrayHolder();
		ad.setData(new int[]{1,2,3});
		
		doWriteAndRead(ad);
	}
	
	/**
	 *  Test if multi array attribute transfer works.
	 */
	public void testByteArrayAttribute() throws Exception
	{
		byte[]	data	= new byte[256];
		for(int b=0; b<256; b++)
		{
			data[b]	= (byte)b;
		}
		ByteArrayHolder	bah	= new ByteArrayHolder();
		bah.setData(data);
		
		doWriteAndRead(bah);
	}
	
	/**
	 *  Test if vector model transfer works.
	 */
	public void testVectorModel() throws Exception
	{
		VectorModel vm = new VectorModel();
		vm.addToV1("a");
		vm.addToV2("b");
		doWriteAndRead(vm);
	}
	
	/**
	 *  Test if class transfer works.
	 */
	public void testClass() throws Exception
	{
		doWriteAndRead(boolean.class);
		doWriteAndRead(InnerTestClass.class);
	}
	
	public static class InnerTestClass{}
	
	
	/**
	 *  Test if class info transfer works.
	 */
	public void testClassInfo() throws Exception
	{
		doWriteAndRead(new ClassInfo("boolean"));
		doWriteAndRead(new ClassInfo(InnerTestClass.class.getName()));
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
		array[0] = Integer.valueOf(0);
//		array[1] = Integer.valueOf(1);
		array[2] = Integer.valueOf(2);
		array[3] = Integer.valueOf(3);
		array[4] = Integer.valueOf(4);
		array[5] = Integer.valueOf(5);
		
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
	 * Test if excluded properties work.
	 */
	public void testExcluded() throws Exception
	{
		A	a	= getABean();
		a.setExcluded("dummy");
		a.setReadExcluded("dummy");
		a.setWriteExcluded("dummy");
		A	a2	= (A)doWriteAndRead(a);
		assertNull("Excluded property not null: "+a2.getExcluded(), a2.getExcluded());
		assertNull(a2.getReadExcluded());
		assertNull(a2.getWriteExcluded());
	}

	/**
	 *  Test if references work.
	 */
	public void testSelfReferenceBean() throws Exception
	{
		E e = new E();
		e.setSelfReference(e);
		doWriteAndRead(e, new Comparator<E>()
		{
			public int compare(E o1, E o2)
			{
				return o2 != o2.getSelfReference()? -1 : 0;
			}
		});
	}
	
	/**
	 *  Test list transfer works.
	 */
	public void testList() throws Exception
	{
		List list = new ArrayList();
		list.add("str_a");
		list.add(Integer.valueOf(2));
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
		set.add(Integer.valueOf(2));
		set.add(getABean());
		
		doWriteAndRead(getABean());
	}
	
	/**
	 *  Test if map transfer works.
	 */
	public void testLRU() throws Exception
	{
		LRU lru = new LRU(123);
		ILRUEntryCleaner cleaner = new ILRUEntryCleaner()
		{
			@Classname("LRUTestCleanerClass")
			public void cleanupEldestEntry(Entry eldest)
			{
			}
		};
		lru.setCleaner(cleaner);
		lru.put("$", "A");
		lru.put(Integer.valueOf(2), Integer.valueOf(22));
		lru.put("obja", getABean());
		
		doWriteAndRead(lru);
	}
	
	/**
	 *  Test if map transfer works.
	 */
	public void testMap() throws Exception
	{
		Map map = new LinkedHashMap();
		map.put("$", "A");
		map.put(Integer.valueOf(2), Integer.valueOf(22));
		map.put("obja", getABean());
//		for(Iterator it=map.entrySet().iterator(); it.hasNext(); )
//		{
//			Map.Entry e = (Map.Entry)it.next();
//			System.out.println("key="+e.getKey()+" value="+e.getValue());
//		}
		
		doWriteAndRead(map);
	}
	
	/**
	 *  Test if method info transfer works.
	 */
	public void testMethodInfo() throws Exception
	{
		MethodInfo	mi	= new MethodInfo(Object.class.getMethod("hashCode"));		
		doWriteAndRead(mi);
	}
	
	/**
	 *  Test if inner class transfer works.
	 */
	public void testInnerClass() throws Exception
	{
		StaticInnerClass ic = new StaticInnerClass("some string");
		
		doWriteAndRead(ic);
	}
	
	/**
	 *  Test if URL transfer works.
	 */
	public void testURL() throws Exception
	{
//		URL url = new URL("http", "host", 4711, "file");
		URL url = new URL("file:/C:/projects/jadex/jadex-applications-micro/target/classes/");
		
		doWriteAndRead(url);
	}
	
	/**
	 *  Test if URI transfer works.
	 */
	public void testURI() throws Exception
	{
//		URL url = new URL("http", "host", 4711, "file");
		URI url = new URI("file:/C:/projects/jadex/jadex-applications-micro/target/classes/");
		
		doWriteAndRead(url);
	}
	
	/**
	 *  Test if java.util.logging.Level transfer works.
	 */
	public void testLoggingLevel() throws Exception
	{
		Level level = Level.SEVERE;
		
		doWriteAndRead(level);
	}
	
	/**
	 *  Test if java.util.logging.LogRecord transfer works.
	 */
	public void testLogRecord() throws Exception
	{
		LogRecord lr = new LogRecord(Level.WARNING, "test message");
		
		doWriteAndRead(lr, new Comparator<LogRecord>()
		{
			public int compare(LogRecord o1, LogRecord o2)
			{
				return o1.getMessage().equals(o2.getMessage()) && o1.getLevel().equals(o2.getLevel())? 0: 1;
			}
		});
	}
	
	/**
	 *  Test if java.util.logging.Level transfer works.
	 */
	public void testInetAddress() throws Exception
	{
		InetAddress adr = InetAddress.getByName("127.0.0.1");
		
		doWriteAndRead(adr);
	}
	
	/**
	 *  Test if writer writes public bean fields (when XML_INCLUDE_FIELDS is set).
	 */
	public void testBeanWithPublicFields() throws Exception
	{
		C c = new C("test\n", 23);
		
		doWriteAndRead(c);
	}
	
	/**
	 *  Test if writer writes public bean fields (when XMLIncludeFields annotation is present).
	 */
	public void testBeanWithIncludedFields() throws Exception
	{
		D d = new D("test\n", 23);

		doWriteAndRead(d);
	}

	/**
	 *  Test if writer writes private bean fields (when includePrivate = true).
	 */
	public void testBeanWithIncludedPrivateFields() throws Exception
	{
		F f = new F("test\n", 23);
		f.excludeMe = 100;

		doWriteAndRead(f);
	}

	/**
	 *  Test if writer writes private bean fields (when @Include is used).
	 */
	public void testBeanWithIncludedSinglePrivateFields() throws Exception
	{
		G g = new G("test\n", 23);

		doWriteAndRead(g);
	}

	/**
	 *  Test if writer writes included private bean fields (when @Include is used).
	 */
	public void testBeanWithIncludedInheritedPrivateFields() throws Exception
	{
		H g = new H("test\n");

		doWriteAndRead(g);
	}

	/**
	 *  Test if writer writes included private bean fields (when @Include is used).
	 */
	public void testBeanWithIncludedStaticFinalField() throws Exception
	{
		I i = new I("test\n");

		doWriteAndRead(i);
	}

	/**
	 *  Test if special characters can be transferred.
	 */
	public void testSpecialCharacter() throws Exception
	{
		String str = "\u00DC\n";
		
		doWriteAndRead(str);
	}
	
	/**
	 *  Test if anonymous inner classes can be transferred.
	 */
	public void testAnonymousInnerClass() throws Exception
	{
		// Do not use final directly as compiler optimizes field away.
		String	tmp	= "hugo";
		final String	name	= tmp;
		Object	obj	= new Object()
		{
			@Classname("test")
			public boolean equals(Object obj)
			{
				String	othername	= null;
				try
				{
					Field	field	= SReflect.getField(obj.getClass(), "val$name");
					field.setAccessible(true);
					othername	= (String)field.get(obj);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
				return name.equals(othername);
			}
						
			public String toString()
			{
				return getClass().getName()+"("+name+")";
			}
		};
		
		doWriteAndRead(obj);
	}
	
	/**
	 *  Test if anonymous inner classes can be transferred.
	 */
	public void testAnonymousInnerClassWithSimpleTypes() throws Exception
	{
		// Do not use final directly as compiler optimizes field away.
		String	tmp	= "hugo";
		final String	name	= tmp;
		Boolean tmp2 = true;
		final boolean booli = tmp2;
		Object	obj	= new Object()
		{
			@Classname("test2")
			public boolean equals(Object obj)
			{
				String	othername	= null;
				Boolean otherbooli = null;
				try
				{
					Field	field	= SReflect.getField(obj.getClass(), "val$name");
					field.setAccessible(true);
					othername	= (String)field.get(obj);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				try
				{
					Field	field	= SReflect.getField(obj.getClass(), "val$booli");
					field.setAccessible(true);
					otherbooli	= (Boolean)field.get(obj);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				
				return name.equals(othername) && otherbooli!=null && otherbooli.booleanValue()==booli;
			}
						
			public String toString()
			{
				return getClass().getName()+"("+name+", "+booli+")";
			}
		};
		
		doWriteAndRead(obj);
	}
	
	/**
	 *  Test reading / writing tuple2.
	 */
	public void	testTimestamp() throws Exception
	{
		Timestamp ts = new Timestamp(System.currentTimeMillis());
		doWriteAndRead(ts);
	}
	
	/**
	 *  Test reading / writing exception.
	 */
	public void	testException() throws Exception
	{
		RuntimeException e = new RuntimeException("Some runtime reason.");
		SecurityException ex = new SecurityException("Some security concern.", e);
		doWriteAndRead(ex, new Comparator<Exception>()
		{
			public int compare(Exception e1, Exception e2)
			{
				int ret = -1;
				if(e1.getClass().equals(e2.getClass()))
				{
					ret = Arrays.equals(e1.getStackTrace(), e2.getStackTrace())? 0: -1;
				}
				return ret;
			}
		});
	}
	
	/**
	 *  Test reading / writing tuple2.
	 */
	public void	testTuple() throws Exception
	{
		Tuple	obj	= new Tuple("hello", "world");
		doWriteAndRead(obj);
		
		obj	= new Tuple(new String[]{"hello", "world", "!"});
		doWriteAndRead(obj);

	}
	
	/**
	 *  Test reading / writing tuple2.
	 */
	public void	testTuple2() throws Exception
	{
		Tuple2<String, String>	obj	= new Tuple2<String, String>("hello", "world");
		doWriteAndRead(obj);
	}
	
	/**
	 *  Test reading / writing tuple2.
	 */
	public void	testCertificate() throws Exception
	{
		KeyStore ks = getKeystore("./testkeystore", "pass", "pass", "test");
		Certificate cert = ks.getCertificate("test");
		doWriteAndRead(cert, new Comparator<Certificate>()
		{
			public int compare(Certificate o1, Certificate o2)
			{
				boolean ret = false;
				try
				{
					ret = Arrays.equals(o1.getEncoded(), o2.getEncoded());
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
				return ret? 0: -1;
			}
		});
	}

	/**
	 * Test reading/writing java optionals with primitive types.
	 * @throws Exception
	 */
	public void testOptionalsPrimitive() throws Exception {
		String className = "java.util.Optional";
		Class<?> optionalClass = SReflect.classForName0(className, null);

		if (optionalClass != null) {
			Method ofMethod = SReflect.getMethod(optionalClass, "of", new Class[]{Object.class});
			Method emptyMethod = SReflect.getMethod(optionalClass, "empty", new Class[]{});

			Object longOptional = ofMethod.invoke(optionalClass, (long) 10000);
			doWriteAndRead(longOptional);

			Object intOptional = ofMethod.invoke(optionalClass, 1000);
			doWriteAndRead(intOptional);

			Object stringOptional = ofMethod.invoke(optionalClass, "Test");
			doWriteAndRead(stringOptional);

			Object booleanOptional = ofMethod.invoke(optionalClass, true);
			doWriteAndRead(booleanOptional);

			Object nullOptional = emptyMethod.invoke(optionalClass, null);
			doWriteAndRead(nullOptional);
		}
	}

	/**
	 * Test reading/writing java optionals with complex types.
	 * @throws Exception
	 */
	public void testOptionalsComplex() throws Exception {
		String className = "java.util.Optional";
		Class<?> optionalClass = SReflect.classForName0(className, null);

		if (optionalClass != null) {
			Method ofMethod = SReflect.getMethod(optionalClass, "of", new Class[]{Object.class});
			final Method getMethod = SReflect.getMethod(optionalClass, "get", new Class[]{});

			Date value2 = new Date(10000);
			Object dateOptional = ofMethod.invoke(optionalClass, value2);
			doWriteAndRead(dateOptional);

			Object intArrayOptional = ofMethod.invoke(optionalClass, (Object) new Integer[]{1,2,3});
			doWriteAndRead(intArrayOptional, new Comparator() {
				@Override
				public int compare(Object o, Object o2) {
					Object array1 = null;
					Object array2 = null;
					try {
					 	array1 = getMethod.invoke(o);
						array2 = getMethod.invoke(o2);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					return Arrays.equals(((Integer[])array1),(Integer[])array2) ? 0 : -1;
				}
			});

			Object dateArrayOptional = ofMethod.invoke(optionalClass, (Object)new Date[]{new Date(1000), new Date(2000)});
			doWriteAndRead(dateArrayOptional, new Comparator() {
				@Override
				public int compare(Object o, Object o2) {
					Object date1 = null;
					Object date2 = null;
					try {
						date1 = getMethod.invoke(o);
						date2 = getMethod.invoke(o2);
					} catch (Exception e) {
						throw new RuntimeException(e);
					}
					return Arrays.equals(((Date[])date1),(Date[])date2) ? 0 : -1;
				}
			});

		}
	}

	/**
	 * Test reading/writing java optionals with collections.
	 * @throws Exception
	 */
	public void testOptionalsCollection() throws Exception {
		String className = "java.util.Optional";
		Class<?> optionalClass = SReflect.classForName0(className, null);

		if (optionalClass != null) {
			Method ofMethod = SReflect.getMethod(optionalClass, "of", new Class[]{Object.class});

			ArrayList<Date> list = new ArrayList<Date>();
			list.add(new Date(10000));
			list.add(new Date(20000));
			list.add(new Date(30000));
			Object dateOptional = ofMethod.invoke(optionalClass, list);
			doWriteAndRead(dateOptional);
		}
	}

	/**
	 * Test reading/writing data arrays.
	 * @throws Exception
	 */
	public void testDateArray() throws Exception {
		Date[] dates = {new Date(1000), new Date(2000)};
		doWriteAndRead(dates, new Comparator() {
			@Override
			public int compare(Object o, Object o2) {
				return Arrays.equals(((Date[]) o), ((Date[]) o2)) ? 0 : -1;
			}
		});

	}

	/**
	 *  Get some bean.
	 */
	protected A	getABean()
	{
		B b1 = new B("test b1");
		B b2 = new B("test b2");
		B b3 = new B("test b3");
		B b4 = new B("test b4");
		A a = new A(10, "test a", b1, new B[]{b1, b2, b3, b4});
		return a;
	}
	
	public static class StaticInnerClass
	{
		protected String string;
		
		public StaticInnerClass()
		{
		}
		
		public StaticInnerClass(String string)
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
			StaticInnerClass other = (StaticInnerClass) obj;
			if (string == null) {
				if (other.string != null)
					return false;
			} else if (!string.equals(other.string))
				return false;
			return true;
		}
	}

	// copied from SSecurity to avoid dependencies :-( 
	
	/**
	 *  Get keystore from a given file.
	 */
	public static KeyStore getKeystore(String storepath, String storepass, String keypass, String alias)
	{
		try
		{
			KeyStore ks = KeyStore.getInstance("JKS");
			FileInputStream fis = null;
			boolean loaded = false;
			try
			{
				File f = new File(storepath);
				if(f.exists())
				{
					fis = new FileInputStream(storepath);
					ks.load(fis, storepass.toCharArray());
					loaded = true;
				}
			}
			catch(Exception e)
			{
			}
			finally
			{
				if(fis!=null)
					fis.close();
				if(!loaded || !ks.containsAlias(alias))
					initKeystore(ks, storepath, storepass, keypass, alias);
			}
			return ks;
		}
		catch(Exception e)
		{
//			e.printStackTrace();
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Get keystore from a given file.
	 */
	public static void saveKeystore(KeyStore keystore, String storepath, String storepass)
	{
		FileOutputStream fos = null;
		try
		{
			fos = new FileOutputStream(storepath);
			keystore.store(fos, storepass.toCharArray());
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		finally
		{
			if(fos!=null)
			{
				try
				{
					fos.close();
				}
				catch(Exception e)
				{
				}
			}
		}
	}
	
	/**
	 *  Init keystore with a self-signed certificate.
	 */
	public static void initKeystore(KeyStore ks, String storepath, String storepass, String keypass, String alias)
	{
		try
    	{
	    	ks.load(null, null); // Must be called. 
	    	
//	    	RSAKeyPairGenerator r = new RSAKeyPairGenerator();
//	    	r.init(new KeyGenerationParameters(new SecureRandom(), 1024));
//	    	AsymmetricCipherKeyPair keys = r.generateKeyPair();
	    	
	    	KeyPairGenerator gen = KeyPairGenerator.getInstance("RSA");  
	 	    gen.initialize(1024);  
	 	    
//	 	    System.out.println("Generating key pair, this may take a short while.");
	 	    KeyPair keys = gen.generateKeyPair();
//	 	    System.out.println("Key generation finished.");
	 	    
		    Certificate c = generateCertificate("CN=CKS Self Signed Cert", keys, 1000, "MD5WithRSA");
		    
		    ks.setKeyEntry(alias, keys.getPrivate(), keypass.toCharArray(),  
		    	new java.security.cert.Certificate[]{c});  
		    
		    saveKeystore(ks, storepath, storepass);
    	}
		catch(RuntimeException e)
		{
			throw e;
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/** 
	 * Create a self-signed X.509 Certificate
	 * @param dn the X.509 Distinguished Name, eg "CN=Test, L=London, C=GB"
	 * @param pair the KeyPair
	 * @param days how many days from now the Certificate is valid for
	 * @param algorithm the signing algorithm, eg "SHA1withRSA"
	 */ 
	public static Certificate generateCertificate(String dn, KeyPair pair, int days, String algorithm) throws GeneralSecurityException, IOException
	{
		X509V1CertificateGenerator gen = new X509V1CertificateGenerator();
		X500Principal dnn = new X500Principal(dn); //"CN=Test CA Certificate"

		Date from = new Date();
		Date to = new Date(from.getTime() + days * 86400000l);
		BigInteger sn = new BigInteger(64, new SecureRandom());
		
		gen.setSerialNumber(sn);
		gen.setIssuerDN(dnn);
		gen.setNotBefore(from);
		gen.setNotAfter(to);
		gen.setSubjectDN(dnn);                       // note: same as issuer
		gen.setPublicKey(pair.getPublic());
		gen.setSignatureAlgorithm(algorithm);

		Certificate cert = gen.generate(pair.getPrivate());
		
		return cert;
	}   
	
}
