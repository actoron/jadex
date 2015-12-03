package jadex.micro.examples.mandelbrot;

import java.util.Random;
import java.util.StringTokenizer;

import jadex.commons.Base64;
import jadex.xml.bean.JavaReader;
import jadex.xml.bean.JavaWriter;

/**
 *  Test ways of transferring short[][] arrays.
 *
 */
public class ArrayTest
{
	/**
	 * Get the data as a transferable string.
	 * 
	 * @return the data string.
	 */
	public static String shortToString(short[][] data)
	{
		String	ret	= null;
		if(data!=null)
		{
			// create string in form of "rows cols\n1 2\n4 5\n7 8"
			StringBuffer	sbuf	= new StringBuffer();
			sbuf.append(data.length);
			sbuf.append(" ");
			sbuf.append(data[0].length);
			sbuf.append("\n");
			for(short i=0; i<data.length; i++)
			{
				if(i>0)
					sbuf.append("\n");
				for(short j=0; j<data[i].length; j++)
				{
					if(j>0)
						sbuf.append(" ");
					sbuf.append(data[i][j]);
				}
			}
			ret	= sbuf.toString();
		}
		return ret;
	}

	/**
	 * Set the data.
	 * 
	 * @param data The data to set.
	 */
	public static short[][] stringToshort(String sdata)
	{
		StringTokenizer	stok	= new StringTokenizer(sdata);
		short	rows	= Short.parseShort(stok.nextToken());
		short	cols	= Short.parseShort(stok.nextToken());
		short[][]	data	= new short[rows][cols];
		for(short i=0; i<data.length; i++)
		{
			for(short j=0; j<data[i].length; j++)
			{
				data[i][j]	= Short.parseShort(stok.nextToken());
			}
		}
		
		return data;
	}
	
	/**
	 * Get the data as a transferable string.
	 * 
	 * @return the data string.
	 */
	public static byte[] shortToByte(short[][] data)
	{
		byte[]	ret	= null;
		if(data!=null)
		{
			short	rows	= (short)data.length;
			short	cols	= (short)data[0].length;
			ret	= new byte[rows*cols*2+4];
			int	offset	= 0;
			
			shortToBytes(rows, ret, offset);
			shortToBytes(cols, ret, offset+=2);
			
			for(short i=0; i<data.length; i++)
			{
				for(short j=0; j<data[i].length; j++)
				{
					shortToBytes(data[i][j], ret, offset+=2);
				}
			}
		}
		
		return ret;
	}
	
	/**
	 * Set the data.
	 * 
	 * @param data The data to set.
	 */
	public static short[][] byteToshort(byte[] sdata)
	{
		int	offset	= 0;
		short	rows	= bytesToshort(sdata, offset);
		short	cols	= bytesToshort(sdata, offset+=2);
		short[][]	data	= new short[rows][cols];
		for(short i=0; i<data.length; i++)
		{
			for(short j=0; j<data[i].length; j++)
			{
				data[i][j]	= bytesToshort(sdata, offset+=2);
			}
		}
		
		return data;
	}

	
	/**
	 *  Convert bytes to a short.
	 */
	protected static short bytesToshort(byte[] buffer, int offset)
	{
		short value = (short)((0xFF & buffer[offset+0]) << 8);
		value |= (0xFF & buffer[offset+1]);

		return value;
	}

	/**
	 *  Convert a short to bytes.
	 */
	protected static byte[] shortToBytes(short val, byte[] buffer, int offset)
	{
		buffer[offset+0] = (byte)(val >>> 8);
		buffer[offset+1] = (byte)val;

		return buffer;
	}



	public static void	main(String[] args)
	{
		short[][]	data = new short[1234][1357];
		
		System.out.println("filling array...");
		Random	r	= new Random();
		for(short i=0; i<data.length; i++)
		{
			for(short j=0; j<data[i].length; j++)
			{
				data[i][j]	= (short)(r.nextInt(2000)-1);	// -1 - 1998
			}
		}
		
		// Custom string format.
		System.out.println("encoding array...");
		long	start	= System.nanoTime();
		String	str	= shortToString(data);
		long	time	= System.nanoTime() - start;
		System.out.println("encoding array took "+(time/1000000)+" milliseconds");
		System.out.println("encoded array is "+str.length()+" bytes");
		
		System.out.println("decoding array...");
		start	= System.nanoTime();
		short[][]	data2	= stringToshort(str);
		time	= System.nanoTime() - start;
		System.out.println("decoding array took "+(time/1000000)+" milliseconds");
	
		System.out.println("Comparing arrays...");
		boolean	equal	= data.length==data2.length;
		for(short i=0; equal && i<data.length; i++)
		{
			equal	= data[i].length==data2[i].length;
			for(short j=0; j<data[i].length; j++)
			{
				equal	= data[i][j]==data2[i][j];
			}
		}
		System.out.println("Arrays are "+(equal?"equal":"different!")+"\n");
		
		// Byte array.
		System.out.println("encoding array to bytes...");
		start	= System.nanoTime();
		byte[]	bytes	= shortToByte(data);
		time	= System.nanoTime() - start;
		System.out.println("encoding to bytes took "+(time/1000000)+" milliseconds");
		System.out.println("encoded array is "+bytes.length+" bytes");
		
		System.out.println("decoding bytes...");
		start	= System.nanoTime();
		data2	= byteToshort(bytes);
		time	= System.nanoTime() - start;
		System.out.println("decoding bytes took "+(time/1000000)+" milliseconds");
	
		System.out.println("Comparing arrays...");
		equal	= data.length==data2.length;
		for(short i=0; equal && i<data.length; i++)
		{
			equal	= data[i].length==data2[i].length;
			for(short j=0; j<data[i].length; j++)
			{
				equal	= data[i][j]==data2[i][j];
			}
		}
		System.out.println("Arrays are "+(equal?"equal":"different!")+"\n");
		
		// Base 64.
		System.out.println("encoding array to base64...");
		start	= System.nanoTime();
		str	= new String(Base64.encode(shortToByte(data)));
		time	= System.nanoTime() - start;
		System.out.println("encoding to base64 took "+(time/1000000)+" milliseconds");
		System.out.println("encoded array is "+str.length()+" bytes");
		
		System.out.println("decoding base64...");
		start	= System.nanoTime();
		data2	= byteToshort(Base64.decode(str.getBytes()));
		time	= System.nanoTime() - start;
		System.out.println("decoding base64 took "+(time/1000000)+" milliseconds");
	
		System.out.println("Comparing arrays...");
		equal	= data.length==data2.length;
		for(short i=0; equal && i<data.length; i++)
		{
			equal	= data[i].length==data2[i].length;
			for(short j=0; j<data[i].length; j++)
			{
				equal	= data[i][j]==data2[i][j];
			}
		}
		System.out.println("Arrays are "+(equal?"equal":"different!")+"\n");

		// XML
		System.out.println("encoding array to XML...");
		start	= System.nanoTime();
		str	= JavaWriter.objectToXML(data, ArrayTest.class.getClassLoader());
		time	= System.nanoTime() - start;
		System.out.println("encoding to XML took "+(time/1000000)+" milliseconds");
		System.out.println("encoded array is "+str.length()+" bytes");
		
		System.out.println("decoding XML...");
		start	= System.nanoTime();
		data2	= (short[][])JavaReader.objectFromXML(str, ArrayTest.class.getClassLoader());
		time	= System.nanoTime() - start;
		System.out.println("decoding XML took "+(time/1000000)+" milliseconds");
	
		System.out.println("Comparing arrays...");
		equal	= data.length==data2.length;
		for(short i=0; equal && i<data.length; i++)
		{
			equal	= data[i].length==data2[i].length;
			for(short j=0; j<data[i].length; j++)
			{
				equal	= data[i][j]==data2[i][j];
			}
		}
		System.out.println("Arrays are "+(equal?"equal":"different!")+"\n");
	}
	
//	encoding array...
//	encoding array took 15 milliseconds
//	encoded array is 177795 bytes
//	decoding array...
//	decoding array took 13 milliseconds
//	Comparing arrays...
//	Arrays are equal
//
//	encoding array to bytes...
//	encoding to bytes took 3 milliseconds
//	encoded array is 80004 bytes
//	decoding bytes...
//	decoding bytes took 3 milliseconds
//	Comparing arrays...
//	Arrays are equal
//
//	encoding array to base64...
//	encoding to base64 took 12 milliseconds
//	encoded array is 106672 bytes
//	decoding base64...
//	decoding base64 took 12 milliseconds
//	Comparing arrays...
//	Arrays are equal
//
//	encoding array to XML...
//	encoding to XML took 778 milliseconds
//	encoded array is 3383020 bytes
//	decoding XML...
//	decoding XML took 500 milliseconds
//	Comparing arrays...
//	Arrays are equal

//	encoding array to XML...
//	encoding to XML took 117 milliseconds
//	encoded array is 188992 bytes
//	decoding XML...
//	decoding XML took 121 milliseconds
//	Comparing arrays...
//	Arrays are equal

}
