package jadex.commons.collection;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.commons.SUtil;
import jadex.commons.transformation.binaryserializer.SBinarySerializer2;
import jadex.commons.transformation.binaryserializer.VarInt;

/**
 *  A map implementation supporting automatic serialization its data
 *  and persisting it on disk.
 */
public class PersistentMap2<K, V> implements Map<K, V>
{
	/** The index map, key to position and size of value. */
	protected Map<K, ValueInfo> indexmap;
	
	/** The persistence file. */
	protected File file;
	
	/** The file access mode. */
	protected String mode;
	
	/** Random access to the persistence file */
	protected RandomAccessFile raf;
	
	/** Bytes of dirty entries. */
	protected long dirtybytes;
	
	/** Dirty threshold for auto-compaction. */
	protected long autocompactionthreshold;
	
	/** Class loader used for serialization. */
	protected ClassLoader classloader;
	
	/**
	 *  Creates the map.
	 * 
	 *  @param file File used for persistent data.
	 */
	public PersistentMap2(File file, boolean synchronous, ClassLoader classloader)
	{
		dirtybytes = 0;
		autocompactionthreshold = Long.MAX_VALUE;
		indexmap = new HashMap<K, ValueInfo>();
		this.classloader = classloader != null? classloader : PersistentMap2.class.getClassLoader();
		if (!file.exists())
		{
			try
			{
				file.createNewFile();
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
		}
		this.file = file;
		
		mode = "rw";
		if (synchronous)
		{
			mode = "rwd";
		}
		
		try
		{
			this.raf = new RandomAccessFile(file, mode);
//			pfilestream = new BufferedOutputStream(new FileOutputStream(file));
		}
		catch (FileNotFoundException e)
		{
			throw new RuntimeException(e);
		}
		
		if (file.length() > 0)
		{
			buildIndex();
		}
		
		if (dirtybytes > 0)
		{
			compact();
		}
	}
	
	/**
	 *  Test main method.
	 * @param args Arguments.
	 */
	public static void main(String[] args)
	{
		String tmpdir = System.getProperty("java.io.tmpdir");
		File file = new File(tmpdir + File.separator + "pmaptest.map");
		file.delete();
		PersistentMap2<String, String> pm = new PersistentMap2<String, String>(file, false, null);
		
		String key = "This is a test key";
		String value = "This is a test value";
		
		int writes = 1000000;
		long ts = System.currentTimeMillis();
		for (int i = 0; i < writes ; ++i)
		{
//			pm.put(key + i, value + (int)(Math.random() * 100000000));
			pm.put(key + i, value + i);
		}
		String lastval = pm.get(key + (writes - 1));
		long delta = System.currentTimeMillis() - ts;
		System.out.println(delta);
		System.out.println((double) writes / delta * 1000);
		
		System.out.println("Dirty: " + pm.getDirtyBytes() + ", Map size: " + pm.size());
		
		for (int i = 0; i < 10000; ++i)
		{
			if (i % 100 == 0)
			{
				System.out.print(i);
//				pm.get(key + i);
				String val = pm.get(key + i);
				System.out.print(":" + val + ", ");
			}
			pm.remove(key + i);
		}
		System.out.println();
		System.out.println("Dirty: " + pm.getDirtyBytes() + ", Map size: " + pm.size());
		ts = System.currentTimeMillis();
		System.out.println("File Size: " + file.length());
		pm.compact();
		System.out.println("Compaction took: " + (System.currentTimeMillis() - ts));
		System.out.println("Dirty: " + pm.getDirtyBytes() + ", Map size: " + pm.size());
		System.out.println(lastval);
		System.out.println(pm.get(key + (writes - 1)));
		System.out.println(lastval.equals(pm.get(key + (writes - 1))));
//		for (int i = 0; i < 10000; ++i)
//		{
//			if (i % 50 == 0)
//			{
//				System.out.println(i);
//				String val = pm.get(key + i);
//				System.out.println(val);
//			}
//		}
		
		pm.close();
		
		pm = new PersistentMap2<String, String>(file, false, null);
		System.out.println(lastval);
		System.out.println(pm.get(key + (writes - 1)));
		System.out.println(lastval.equals(pm.get(key + (writes - 1))));
	}
	
	/**
     *  Returns the size of the map.
     */
    public int size()
    {
    	return indexmap.size();
    }

    /**
     * Returns whether this map is empty.
     *
     */
    public boolean isEmpty()
    {
    	return indexmap.isEmpty();
    }

    /**
     *  Returns whether this map contains the specified
     *  key.
     */
    public boolean containsKey(Object key)
    {
    	return indexmap.containsKey(key);
    }

    /**
     *  Returns whether the map contains the
     *  specified value.
     *  
     *  NOTE: This is very slow, use with caution.
     */
    public boolean containsValue(Object value)
    {
    	for (Map.Entry<K, ValueInfo> entry : indexmap.entrySet())
    	{
    		if (value != null)
    		{
    			if (value.equals(get(entry.getKey())))
    			{
    				return true;
    			}
    		}
    		else
    		{
    			if (value == get(entry.getKey()))
    			{
    				return true;
    			}
    		}
    	}
    	return false;
    }

    /**
     * Returns the value for a specified key.
     */
    public V get(Object key)
    {
    	V ret = null;
	    	ValueInfo vinfo = indexmap.get(key);
	    	
    	if (vinfo != null)
    	{
	    	try
	    	{
	    		raf.seek(vinfo.getPosition());
//	    		ByteArrayInputStream inbuffer = new ByteArrayInputStream(buf);
//	    		GZIPInputStream gzipinput = new GZIPInputStream(inbuffer);
//	    		ByteArrayOutputStream outbuffer = new ByteArrayOutputStream();
//	    		int read = 0;
//	    		byte[] tmpbuf = new byte[16384];
//	    		do
//	    		{
//	    			read = gzipinput.read(tmpbuf, 0, 16384);
//	    			if (read > 0)
//	    			{
//	    				outbuffer.write(tmpbuf, 0, read);
//	    			}
//	    		}
//	    		while (read != -1);
//	    		tmpbuf = null;
//	    		gzipinput.close();
//	    		outbuffer.close();
//	    		buf = outbuffer.toByteArray();
//	    		inbuffer = null;
//	    		gzipinput = null;
//	    		outbuffer = null;
	    		
	    		ret = (V) SBinarySerializer2.readObjectFromDataInput(raf, null, null, classloader, null);
//	    		ret = (V) BinarySerializer.objectFromByteArray(buf, null, null, classloader, null);
	    	}
	    	catch (IOException e)
	    	{
	    		throw new RuntimeException(e);
	    	}
    	}
    	
    	return ret;
    }

    // Modification Operations

    /**
     * Puts a new value in the map.
     */
    /**
     * Puts a new value in the map.
     */
    public V put(K key, V value)
    {
    	V ret = doPut(key, value);
    	if (dirtybytes > autocompactionthreshold)
    	{
    		compact();
    	}
    	return ret;
    }

    /**
     * Removes a map entry.
     */
    public V remove(Object key)
    {
    	V ret = doRemove(key);
    	if (dirtybytes > autocompactionthreshold)
    	{
    		compact();
    	}
    	return ret;
    }

    /**
     * Copies entries from the specified map to this map,
     */
    public void putAll(Map<? extends K, ? extends V> m)
    {
//    	for (Map.Entry<? extends K, ? extends V> entry : m.entrySet())
//    	{
//    		put(entry.getKey(), entry.getValue());
//    	}
    	for (K key : m.keySet())
    	{
    		put(key, m.get(key));
    	}
    }

    /**
     * Removes all of the entries from this map.
     */
    public void clear()
    {
    	indexmap.clear();
    	try
		{
			raf.close();
			file.delete();
			file.createNewFile();
			raf = new RandomAccessFile(file, mode);
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
    }

    /**
     *  Returns the keys of the map.
     *  
     *  Note: Unlike other maps, this is the preferred (fastest mode) for iteration.
     */
    public Set<K> keySet()
    {
    	return indexmap.keySet();
    }

    /**
     *  Returns the values of the map.
     *  
     *  NOTE: This is very slow, use with caution.
     */
    public Collection<V> values()
    {
    	List<V> ret = new ArrayList<V>(indexmap.size());
    	for (Map.Entry<K, ValueInfo> entry : indexmap.entrySet())
    	{
    		ret.add(get(entry.getKey()));
    	}
    	return ret;
    }

    /**
     *  Returns the map's entry set.
     *  
     *  Warning: SLOW!
     */
    public Set<Map.Entry<K, V>> entrySet()
    {
    	Set<Map.Entry<K, V>> ret = new HashSet<Map.Entry<K,V>>();
    	
    	for (Map.Entry<K, ValueInfo> entry : indexmap.entrySet())
    	{
    		final K key = entry.getKey();
    		ret.add(new Map.Entry<K, V>()
			{
    			/**
    			 *  Returns the key.
    			 */
				public K getKey()
				{
					return key;
				}
				
				/**
    			 *  Returns the value.
    			 */
				public V getValue()
				{
					return PersistentMap2.this.get(key);
				}
				
				/**
    			 *  Sets the value.
    			 */
				public V setValue(V value)
				{
					V ret = getValue();
					PersistentMap2.this.put(key, value);
					return ret;
				}
    			
			});
    	}
    	
    	return ret;
    }
    
    /**
     *  Removes stale entries and compacts the map.
     */
    public void compact()
    {
    	File oldfile = new File(file.getAbsolutePath() + ".old");
    	File compactfile = new File(file.getAbsolutePath() + ".compact");
    	
    	try
    	{
	    	FileOutputStream fos = new FileOutputStream(compactfile);
	    	
	    	Map<K, ValueInfo> newindexmap = new HashMap<K, PersistentMap2<K,V>.ValueInfo>();
	    	long pos = 0;
	    	byte[] mainbuf = new byte[262144];
	    	int mainbufsize = 0;
	    	for (Map.Entry<K, ValueInfo> entry : indexmap.entrySet())
	    	{
	    		ValueInfo info = entry.getValue();
	    		raf.seek(info.getKvPosition());
	    		byte[] buf = null;
	    		if (info.kvsize > mainbuf.length)
	    		{
	    			fos.write(mainbuf, 0, mainbufsize);
    				mainbufsize = 0;
	    			buf = new byte[info.kvsize];
	    			raf.readFully(buf);
	    			fos.write(buf);
	    		}
	    		else
	    		{
	    			if (info.kvsize > mainbuf.length - mainbufsize)
	    			{
	    				fos.write(mainbuf, 0, mainbufsize);
	    				mainbufsize = 0;
	    			}
	    			buf = mainbuf;
	    			raf.readFully(buf, mainbufsize, info.kvsize);
	    			mainbufsize += info.kvsize;
	    		}
	    		
	    		int keylength = (int) (info.getPosition() - info.getKvPosition());
	    		
	    		ValueInfo nvinfo = new ValueInfo(pos + keylength, info.getSize(), pos, info.getKvSize());
	    		pos += info.kvsize;
	    		newindexmap.put(entry.getKey(), nvinfo);
	    	}
	    	
	    	if (mainbufsize > 0)
	    	{
	    		fos.write(mainbuf, 0, mainbufsize);
	    	}
	    	
	    	fos.close();
    		raf.close();
			SUtil.moveFile(file, oldfile);
			SUtil.moveFile(compactfile, file);
			oldfile.delete();
			raf = new RandomAccessFile(file, mode);
			dirtybytes = 0;
			indexmap = newindexmap;
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
    }
    
    /**
     *  Closes the persistence file.
     *  The map ceases to function after calling this.
     */
    public void close()
    {
    	try
		{
			raf.close();
		}
		catch (IOException e)
		{
			throw new RuntimeException(e);
		}
    }
    
    /**
     *  Returns the bytes wasted due to stale entries.
     */
    public long getDirtyBytes()
    {
    	return dirtybytes;
    }
    
    /**
     *  Sets the threshold of stale bytes
     *  before auto-compaction is executed.
     *  Setting the threshold to Long.MAX_VALUE
     *  disables auto-compaction.
     */
    public void setAutoCompactionThreshold(long threshold)
    {
    	autocompactionthreshold = threshold;
    	if (dirtybytes > autocompactionthreshold)
    	{
    		compact();
    	}
    }
    
    /**
     *  Increments the dirty counter and performs
     *  auto-compaction if necessary.
     */
//    protected void incrementDirtyCounter()
//    {
//    	++dirtycounter;
//    	if (dirtycounter > autocompactionthreshold)
//    	{
//    		compact();
//    	}
//    }
    
    /**
     *  Builds the index.
     */
    protected void buildIndex()
    {
    	long pos = 0;
    	
    	long length = 0;
    	try
		{
			length = raf.length();
		}
		catch (IOException e1)
		{
			throw new RuntimeException(e1);
		}
    	
    	while(pos < length)
    	{
    		try
			{
    			long kvpos = pos;
				raf.seek(pos);
				byte firstbyte = raf.readByte();
				
				byte ext = VarInt.getExtensionSize(firstbyte);
				
				raf.seek(pos);
				byte[] buf = new byte[ext + 1];
				raf.readFully(buf);
				
				int entrysize = (int) VarInt.decodeWithKnownSize(buf, 0, ext);
				pos += buf.length;
				
				Object okey = SBinarySerializer2.readObjectFromDataInput(raf, null, null, classloader, null);
				pos += entrysize;
//				Object okey = BinarySerializer.objectFromByteArray(buf, null, null, classloader, null);
				if (okey instanceof PersistentMap2.DeletedKey)
				{
					ValueInfo valinfo = indexmap.remove(((DeletedKey) okey).getKey());
					if (valinfo != null)
					{
						dirtybytes += valinfo.getKvSize();
						dirtybytes = Math.max(dirtybytes, 0);
					}
				}
				else
				{
					K key = (K) okey;
					
					ValueInfo valinfo = indexmap.get(key);
					if (valinfo != null)
					{
						dirtybytes += valinfo.getKvSize();
						dirtybytes = Math.max(dirtybytes, 0);
					}
					
					firstbyte = raf.readByte();
					ext = VarInt.getExtensionSize(firstbyte);
					raf.seek(pos);
					buf = new byte[ext + 1];
					raf.readFully(buf);
					pos += buf.length;
					entrysize = (int) VarInt.decodeWithKnownSize(buf, 0, ext);
					
					int kvsize = (int) (pos - kvpos + entrysize);
					ValueInfo valinf = new ValueInfo(pos, entrysize, kvpos, kvsize);
					pos += entrysize;
					
					indexmap.put(key, valinf);
				}
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
    		
    	}
    }
    
    long raflength = 0;
    
    /** Commits a value to the map */
    protected V doPut(K key, V value)
    {
    	V ret = get(key);
    	
    	ValueInfo oldvalinfo = indexmap.get(key);
    	
    	try
    	{
//    		byte[] kbuf = BinarySerializer.objectToByteArray(key, classloader);
//    		byte[] klbuf = VarInt.encode(kbuf.length);
//	    	byte[] vbuf = BinarySerializer.objectToByteArray(value, classloader);
//	    	byte[] vlbuf = VarInt.encode(vbuf.length);
    		ByteArrayOutputStream baos = new ByteArrayOutputStream();
    		SBinarySerializer2.writeObjectToStream(baos, key, classloader);
    		byte[] kbuf = baos.toByteArray();
    		byte[] klbuf = VarInt.encode(kbuf.length);
    		baos = new ByteArrayOutputStream();
    		SBinarySerializer2.writeObjectToStream(baos, value, classloader);
    		byte[] vbuf = baos.toByteArray();
    		byte[] vlbuf = VarInt.encode(vbuf.length);
    		
	    	long pos = raf.length();
	    	int length = klbuf.length + kbuf.length + vlbuf.length;
	    	ValueInfo vinfo = new ValueInfo(pos + length, vbuf.length, pos, length + vbuf.length);
	    	length += vbuf.length;
	    	byte[] buf = new byte[length];
	    	int apos = 0;
	    	System.arraycopy(klbuf, 0, buf, apos, klbuf.length);
	    	apos += klbuf.length;
	    	System.arraycopy(kbuf, 0, buf, apos, kbuf.length);
	    	apos += kbuf.length;
	    	System.arraycopy(vlbuf, 0, buf, apos, vlbuf.length);
	    	apos += vlbuf.length;
	    	System.arraycopy(vbuf, 0, buf, apos, vbuf.length);
	    	apos += vbuf.length;
	    	raf.setLength(pos + length);
	    	raf.seek(pos);
	    	raf.write(buf);
	    	indexmap.put(key, vinfo);
    	}
    	catch (IOException e)
    	{
    		throw new RuntimeException(e);
    	}
    	
    	if (oldvalinfo != null)
    	{
    		dirtybytes += oldvalinfo.getKvSize();
    		dirtybytes = Math.max(dirtybytes, 0);
    	}
    	
    	return ret;
    }
    
    public V doRemove(Object key)
    {
    	V ret = null;
    	if (indexmap.containsKey(key))
    	{
    		ret = get(key);
    		ValueInfo oldvalinfo = indexmap.remove(key);
    		DeletedKey dk = new DeletedKey(key);
    		
    		try
        	{
    			ByteArrayOutputStream baos = new ByteArrayOutputStream();
    			SBinarySerializer2.writeObjectToStream(baos, dk, classloader);
        		byte[] kbuf = baos.toByteArray();
        		byte[] klbuf = VarInt.encode(kbuf.length);
    	    	long pos = raf.length();
    	    	raf.setLength(pos + klbuf.length + kbuf.length);
    	    	raf.seek(pos);
    	    	raf.write(klbuf);
    	    	raf.write(kbuf);
        	}
    		catch (IOException e)
        	{
        		throw new RuntimeException(e);
        	}
    		
    		if (oldvalinfo != null)
    		{
    			dirtybytes += oldvalinfo.getKvSize();
    			dirtybytes = Math.max(dirtybytes, 0);
    		}
    	}
    	return ret;
    }
    
    /**
     *  Information about a stored value.
     *
     */
    protected class ValueInfo
    {
    	/** Position of the value within the file. */
    	protected long position;
    	
    	/** Size of the value in the file. */
    	protected int size;
    	
    	/** Position of the whole key-value pair. */
    	protected long kvposition;
    	
    	/** Size of the whole key-value pair. */
    	protected int kvsize;
    	
    	/**
    	 *  Creates the info.
    	 */
    	public ValueInfo(long pos, int size, long kvpos, int kvsize)
		{
    		this.position = pos;
    		this.size = size;
    		this.kvposition = kvpos;
    		this.kvsize = kvsize;
		}
    	
    	/**
    	 *  Gets the position.
    	 *  
    	 *  @return The position.
    	 */
    	public long getPosition()
		{
			return position;
		}
    	
    	/**
    	 *  Gets the size.
    	 * 
    	 *  @return The size.
    	 */
    	public int getSize()
		{
			return size;
		}

		/**
		 *  Gets the kvposition.
		 *
		 *  @return The kvposition.
		 */
		public long getKvPosition()
		{
			return kvposition;
		}

		/**
		 *  Gets the kvsize.
		 *
		 *  @return The kvsize.
		 */
		public int getKvSize()
		{
			return kvsize;
		}
    }
    
    /**
     *  Marker for deleted entries.
     *
     */
    protected static class DeletedKey
    {
    	/** The key. */
    	protected Object key;
    	
    	/**
    	 *  Creates the marker.
    	 */
    	public DeletedKey()
		{
		}
    	
    	/**
    	 *  Creates the marker.
    	 */
    	public DeletedKey(Object key)
		{
    		this.key = key;
		}
    	
    	/**
    	 *  Gets the key.
    	 *  
    	 *  @return The key.
    	 */
    	public Object getKey()
		{
			return key;
		}
    	
    	/**
    	 *  Sets the key.
    	 *  
    	 *  @param key The key.
    	 */
    	public void setKey(Object key)
		{
			this.key = key;
		}
    }
}
