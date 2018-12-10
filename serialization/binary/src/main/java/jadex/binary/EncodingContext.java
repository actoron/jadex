package jadex.binary;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import jadex.commons.SUtil;
import jadex.commons.transformation.STransformation;
import jadex.commons.transformation.traverser.ITraverseProcessor;

/**
 *  Context for encoding (serializing) an object in a binary format.
 *
 */
public class EncodingContext extends AbstractEncodingContext
{
	/** Cache for class names. */
	protected Map<Class<?>, String> classnamecache = new HashMap<Class<?>, String>();
	
	/** The binary output */
	protected OutputStream os;
		
	/** The string pool. */
	protected Map<String, Integer> stringpool;
	
	/** Cache for class IDs. */
	protected Map<Class<?>, Integer> classidcache;
	
	/** The class name pool. */
	protected Map<String, Integer> classnamepool;
	
	/** The package fragment pool. */
	protected Map<String, Integer> fragpool;
	
	/**
	 *  Creates an encoding context.
	 *  @param usercontext A user context.
	 *  @param preprocessors The preprocessors.
	 *  @param classloader The classloader.
	 */
	public EncodingContext(OutputStream os, Object rootobject, Object usercontext, List<ITraverseProcessor> preprocessors, ClassLoader classloader, SerializationConfig config)
	{
		super(rootobject, usercontext, preprocessors, classloader);
		this.os = os;
		classidcache = new HashMap<Class<?>, Integer>();
		stringpool = config==null?new HashMap<String, Integer>():config.createEncodingStringPool();
		//for (int i = 0; i < BinarySerializer.DEFAULT_STRINGS.size(); ++i)
			//stringpool.put(BinarySerializer.DEFAULT_STRINGS.get(i), i);
		classnamepool = config==null?new HashMap<String, Integer>():config.createEncodingClassnamePool();
		fragpool = config==null?new HashMap<String, Integer>():config.createEncodingFragPool();
	}
	
	/**
	 *  Writes a byte.
	 *  @param b The byte.
	 */
	public void writeByte(byte b)
	{
		try
		{
			os.write(b);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 *  Writes a byte array, appending it to the buffer.
	 *  @param b The byte array.
	 */
	public void write(byte[] b)
	{
		try
		{
			os.write(b);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 * @param bool
	 */
	public void writeBoolean(boolean bool)
	{
		try
		{
			os.write(bool? 1 : 0);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 
	 * @param clazz
	 */
	public void writeClass(Class<?> clazz)
	{

		if(!isIgnoreNextClassWrite())
		{
			Integer classid = classidcache.get(clazz);
			if (classid == null)
			{
				String	classname	= STransformation.registerClass(clazz);
				classid = writeClassname(classname);
				classidcache.put(clazz, classid);
			}
			else
			{
				writeVarInt(classid.intValue());
			}
		}
		else
		{
			setIgnoreNextClassWrite(false);
		}
	}
	
	/**
	 * 
	 */
	public int writeClassname(String name)
	{
//		synchronized(cstat)
//		{
//			Long val = cstat.get(name);
//			if (val==null)
//				val = (long) name.length();
//			else
//				val+=name.length();
//			cstat.put(name, val);
//			
//			Tuple2<String, Long>[] stats = new Tuple2[cstat.size()];
//			int count=0;
//			for (Map.Entry<String, Long> entry : cstat.entrySet())
//			{
//				stats[count++] = new Tuple2<String, Long>(entry.getKey(), entry.getValue());
//			}
//			Arrays.sort(stats, new Comparator<Tuple2<String, Long>>()
//			{
//				public int compare(Tuple2<String, Long> o1, Tuple2<String, Long> o2)
//				{
//					return (int)(o1.getSecondEntity() - o2.getSecondEntity());
//				}
//			});
//			if (Math.random() < 0.1)
//			{
//				System.out.println("AAAAAAAAA");
//				for (int i = 0; i < stats.length; ++i)
//				{
//					if (stats[i].getSecondEntity() > 20)
//						System.out.println(stats[i].getFirstEntity() + "|" + stats[i].getSecondEntity());
//				}
//				System.out.println("ZZZZZZZZZ");
//			}
//		}
		
		Integer classid = classnamepool.get(name);
		if (classid == null)
		{
			classid = classnamepool.size();
			classnamepool.put(name, classid);
			writeVarInt(classid.intValue());
			
			int lppos = name.lastIndexOf('.');
			if (lppos < 0)
			{
				writeVarInt(0);
				pooledWrite(fragpool, name);
			}
			else
			{
				String pkgname = name.substring(0, lppos);
				String classname = name.substring(lppos + 1);
				
				StringTokenizer tok = new StringTokenizer(pkgname, ".");
				writeVarInt(tok.countTokens());
				while (tok.hasMoreElements())
				{
					String frag = tok.nextToken();
					pooledWrite(fragpool, frag);
				}
				pooledWrite(fragpool, classname);
			}
		}
		else
		{
			writeVarInt(classid.intValue());
		}
		
		
		return classid.intValue();
	}
	
//	static final Map<String, Long> cstat = new HashMap<String, Long>();
	
	/**
	 * 
	 * @param string
	 */
	public void writeString(String string)
	{
//		synchronized(cstat)
//		{
//			Long val = cstat.get(string);
//			if (val==null)
//				val = (long) string.length();
//			else
//				val+=string.length();
//			cstat.put(string, val);
//			
//			Tuple2<String, Long>[] stats = new Tuple2[cstat.size()];
//			int count=0;
//			for (Map.Entry<String, Long> entry : cstat.entrySet())
//			{
//				stats[count++] = new Tuple2<String, Long>(entry.getKey(), entry.getValue());
//			}
//			Arrays.sort(stats, new Comparator<Tuple2<String, Long>>()
//			{
//				public int compare(Tuple2<String, Long> o1, Tuple2<String, Long> o2)
//				{
//					return (int)(o1.getSecondEntity() - o2.getSecondEntity());
//				}
//			});
//			if (Math.random() < 0.1)
//			{
//				System.out.println("AAAAAAAAA");
//				for (int i = 0; i < stats.length; ++i)
//				{
//					if (stats[i].getSecondEntity() > 20)
//						System.out.println(stats[i].getFirstEntity() + "|" + stats[i].getSecondEntity());
//				}
//				System.out.println("ZZZZZZZZZ");
//			}
//		}
		pooledWrite(stringpool, string);
	}
	
	/**
	 * 
	 * @param value
	 */
	public void writeVarInt(long value)
	{
		try
		{
		write(VarInt.encode(value));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @param value
	 */
	public void writeSignedVarInt(long value)
	{
		boolean neg = value < 0;
		value = Math.abs(value);
		long mask = Long.highestOneBit(value) << 2;
		if (neg)
		{
			mask |= mask >> 1;
		}
		value = value | (mask);
		writeVarInt(value);
	}
	
	/**
	 * Writes a string using a pool.
	 * 
	 * @param string
	 */
	protected void pooledWrite(Map<String, Integer> pool, String string)
	{
		Integer sid = pool.get(string);
		if(sid == null)
		{
			sid = pool.size();
			pool.put(string, sid);
			writeVarInt(sid);
			if (string != null)
			{
				byte[] encodedString = string.getBytes(SUtil.UTF8);
				
				writeVarInt(encodedString.length);
				write(encodedString);
			}
			else
			{
				writeVarInt(Integer.MAX_VALUE + 1L);
			}
		}
		else
		{
			writeVarInt(sid);
		}
	}
}
