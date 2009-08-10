package jadex.commons.xml.test;

import java.io.FileInputStream;
import java.io.FileOutputStream;

import nuggets.Nuggets;

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
			
//			TypeInfo tia = new TypeInfo("a", A.class);
//			TypeInfo tib = new TypeInfo("b", B.class);

			System.out.println("Write: "+a);
			FileOutputStream fos = new FileOutputStream("test.xml");
			Writer w = new Writer(new BeanObjectWriterHandler(), null);
			w.write(a, fos, null, null);
			fos.close();
			System.out.println(Nuggets.objectToXML(a, null));
			
			
			FileInputStream fis = new FileInputStream("test.xml");
			Reader r = new Reader(new BeanObjectReaderHandler(), null);
			Object obj = r.read(fis, null, null);
			System.out.println("Read: "+obj);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
}
