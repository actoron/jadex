package jadex.commons.collection;

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
import jadex.commons.transformation.binaryserializer.BinarySerializer;
import jadex.commons.transformation.binaryserializer.VarInt;

/**
 *  A map implementation supporting automatic serialization its data
 *  and persisting it on disk using a shadow map during compaction.
 */
public class ShadowedPersistentMap<K, V> implements Map<K, V>
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
	
	/** Lock for the compaction shadow map. */
	protected Object shadowlock = new Object();
	
	/** Object counter for shadow map. */
	protected int shadowcounter = 0;
	
	/** Shadow map used during compaction. */
	protected volatile ShadowedPersistentMap<Object, Object> compactionshadowmap;
	
	/**
	 *  Creates the map.
	 * 
	 *  @param file File used for persistent data.
	 */
	public ShadowedPersistentMap(File file, boolean synchronous, ClassLoader classloader)
	{
		dirtybytes = 0;
		autocompactionthreshold = Long.MAX_VALUE;
		compactionshadowmap = null;
		indexmap = new HashMap<K, ValueInfo>();
		this.classloader = classloader != null? classloader : ShadowedPersistentMap.class.getClassLoader();
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
		ShadowedPersistentMap<String, String> pm = new ShadowedPersistentMap<String, String>(file, false, null);
//		PersistentMap<String, List<URL>> pm = new PersistentMap<String, List<URL>>(file, false, null);
		
//		pm.setAutoCompactionThreshold(1000);
		
		String key = "This is a test key";
		String value = "This is a test value";
//		List<URL> value = new ArrayList<URL>();
//		for (int i = 0; i < 100; ++i)
//		{
//			try
//			{
//				value.add(new URL("http://www.google.com/"));
//			}
//			catch (MalformedURLException e)
//			{
//				e.printStackTrace();
//			}
//		}
		
		int writes = 1000000;
		long ts = System.currentTimeMillis();
		for (int i = 0; i < writes ; ++i)
		{
//			pm.put(key + i, value + (int)(Math.random() * 100000000));
			pm.put(key + i, value);
		}
		long delta = System.currentTimeMillis() - ts;
		System.out.println(delta);
		System.out.println((double) writes / delta * 1000);
		
		System.out.println("Dirty: " + pm.getDirtyBytes());
		
		for (int i = 0; i < 10000; ++i)
		{
			if (i % 100 == 0)
			{
				System.out.println(i);
//				pm.get(key + i);
				String val = pm.get(key + i);
				System.out.println(val);
				pm.remove(key + i);
			}
		}
		
		System.out.println("Dirty: " + pm.getDirtyBytes());
		
		ts = System.currentTimeMillis();
		pm.compact();
		pm.waitForCompaction();
		System.out.println("Compaction took: " + (System.currentTimeMillis() - ts));
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
	}
	
	/**
     *  Returns the size of the map.
     */
    public int size()
    {
    	return indexmap.size() + shadowcounter;
    }

    /**
     * Returns whether this map is empty.
     *
     */
    public boolean isEmpty()
    {
    	if (compactionshadowmap != null)
    	{
    		synchronized (shadowlock)
			{
				if (compactionshadowmap != null)
				{
					return compactionshadowmap.isEmpty() && indexmap.isEmpty();
				}
			}
    	}
    	
    	return indexmap.isEmpty();
    }

    /**
     *  Returns whether this map contains the specified
     *  key.
     */
    public boolean containsKey(Object key)
    {
    	if (compactionshadowmap != null)
    	{
    		synchronized (shadowlock)
			{
				if (compactionshadowmap != null)
				{
					return indexmap.containsKey(key) || compactionshadowmap.containsKey(key);
				}
			}
    	}
    	
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
    	if (compactionshadowmap != null)
    	{
    		synchronized (shadowlock)
			{
				if (compactionshadowmap != null)
				{
					waitForCompaction();
				}
			}
    	}
    	
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
    	
    	boolean deleted = false;
    	
    	if (compactionshadowmap != null)
    	{
    		synchronized (shadowlock)
			{
				if (compactionshadowmap != null)
				{
					if (compactionshadowmap.containsKey(new DeletedKey(key)))
					{
						deleted = true;
					}
					else
					{
						ret = (V) compactionshadowmap.get(key);
					}
				}
			}
    	}
    	
    	if (ret == null && !deleted)
    	{
	    	ValueInfo vinfo = indexmap.get(key);
	    	
	    	if (vinfo != null)
	    	{
		    	try
		    	{
		    		byte[] buf = new byte[vinfo.getSize()];
		    		raf.seek(vinfo.getPosition());
		    		raf.readFully(buf);
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
		    		
		    		ret = (V) BinarySerializer.objectFromByteArray(buf, null, null, classloader, null);
		    	}
		    	catch (IOException e)
		    	{
		    		throw new RuntimeException(e);
		    	}
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
    	if (compactionshadowmap != null)
    	{
    		synchronized(shadowlock)
    		{
    			if (compactionshadowmap != null)
    			{
    				++shadowcounter;
    				return (V) compactionshadowmap.put(key, value);
    			}
    		}
    	}
    	
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
    	if (compactionshadowmap != null)
    	{
    		synchronized (shadowlock)
			{
    			if (compactionshadowmap != null)
    			{
    				V ret = get(key);
    				compactionshadowmap.put(new DeletedKey(key), null);
    				--shadowcounter;
    				return ret;
    			}
			}
    	}
    	
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
    	waitForCompaction();
    	
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
    	if (compactionshadowmap != null)
    	{
    		synchronized (shadowlock)
			{
    			if (compactionshadowmap != null)
    			{
    				Set<K> ret = new HashSet(indexmap.keySet());
    				Set<Object> shadowkeys = compactionshadowmap.keySet();
    				for (Object okey : shadowkeys)
    				{
    					if (okey instanceof ShadowedPersistentMap.DeletedKey)
    					{
    						ret.remove(((DeletedKey) okey).getKey());
    					}
    					else
    					{
    						ret.add((K) okey);
    					}
    				}
    				return ret;
    			}
			}
    	}
    	
    	return indexmap.keySet();
    }

    /**
     *  Returns the values of the map.
     *  
     *  NOTE: This is very slow, use with caution.
     */
    public Collection<V> values()
    {
    	waitForCompaction();
    	
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
    	
    	waitForCompaction();
    	
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
					return ShadowedPersistentMap.this.get(key);
				}
				
				/**
    			 *  Sets the value.
    			 */
				public V setValue(V value)
				{
					V ret = getValue();
					ShadowedPersistentMap.this.put(key, value);
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
    	synchronized (this)
    	{
    		if (compactionshadowmap != null)
    		{
    			synchronized(shadowlock)
    			{
    				// Ignore spurious compaction requests?
    				return;
    			}
    		}
    		final File shadowfile = new File(file.getAbsolutePath() + ".shadow");
    		compactionshadowmap = new ShadowedPersistentMap<Object, Object>(shadowfile, mode.endsWith("d"), classloader);
    	}
    	Thread compactionthread = new Thread(new Runnable()
		{
			public void run()
			{
		    	File oldfile = new File(file.getAbsolutePath() + ".old");
		    	File compactfile = new File(file.getAbsolutePath() + ".compact");
		    	
//		    	PersistentMap<K, V> compactmap = new PersistentMap<K, V>(compactfile, false, classloader);
//		    	compactmap.putAll(this);
//		    	compactmap.close();
//		    	compactmap = null;
		    	try
		    	{
			    	FileOutputStream fos = new FileOutputStream(compactfile);
			    	
			    	Map<K, ValueInfo> newindexmap = new HashMap<K, ShadowedPersistentMap<K,V>.ValueInfo>();
			    	long pos = 0;
			    	for (Map.Entry<K, ValueInfo> entry : indexmap.entrySet())
			    	{
			    		ValueInfo info = entry.getValue();
			    		raf.seek(info.getKvPosition());
			    		byte[] buf = new byte[info.kvsize];
			    		raf.readFully(buf);
			    		fos.write(buf);
			    		
			    		int keylength = (int) (info.getPosition() - info.getKvPosition());
			    		
			    		ValueInfo nvinfo = new ValueInfo(pos + keylength, info.getSize(), pos, info.getKvSize());
			    		pos += buf.length;
			    		newindexmap.put(entry.getKey(), nvinfo);
			    	}
			    	
			    	synchronized(shadowlock)
			    	{
				    	fos.close();
			    		raf.close();
						SUtil.moveFile(file, oldfile);
						SUtil.moveFile(compactfile, file);
						oldfile.delete();
						raf = new RandomAccessFile(file, mode);
						dirtybytes = 0;
						indexmap.clear();
//	//					buildIndex();
						ShadowedPersistentMap.this.indexmap = newindexmap;
						
						for (Object key : compactionshadowmap.keySet())
						{
							if (key instanceof ShadowedPersistentMap.DeletedKey)
							{
								doRemove(((DeletedKey) key).getKey());
							}
							else
							{
								doPut((K) key, (V) compactionshadowmap.get(key));
							}
						}
						compactionshadowmap = null;
						shadowcounter = 0;
						shadowlock.notifyAll();
			    	}
				}
				catch (IOException e)
				{
					throw new RuntimeException(e);
				}
			}
		});
    	compactionthread.start();
    }
    
    /**
     *  Waits for a background compaction to finish (if running).
     */
    public void waitForCompaction()
    {
    	if (compactionshadowmap != null)
    	{
    		synchronized (shadowlock)
    		{
    			while (compactionshadowmap != null)
    			{
    				try
					{
						shadowlock.wait();
					}
					catch (InterruptedException e)
					{
					}
    			}
    		}
    	}
    }
    
    /**
     *  Closes the persistence file.
     *  The map ceases to function after calling this.
     */
    public void close()
    {
    	synchronized (shadowlock)
    	{
	    	waitForCompaction();
	    	
	    	try
			{
				raf.close();
			}
			catch (IOException e)
			{
				throw new RuntimeException(e);
			}
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
				
				buf = new byte[entrysize];
				raf.readFully(buf);
				pos += buf.length;
				
				Object okey = BinarySerializer.objectFromByteArray(buf, null, null, classloader, null);
				if (okey instanceof ShadowedPersistentMap.DeletedKey)
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
    
    /** Commits a value to the map */
    protected V doPut(K key, V value)
    {
    	V ret = get(key);
    	
    	ValueInfo oldvalinfo = indexmap.get(key);
    	
    	try
    	{
    		byte[] kbuf = BinarySerializer.objectToByteArray(key, classloader);
//    		ByteArrayOutputStream buffer = new ByteArrayOutputStream();
//    		GZIPOutputStream gzipout = new GZIPOutputStream(buffer);
//    		gzipout.write(kbuf);
//    		gzipout.close();
//    		kbuf = buffer.toByteArray();
    		byte[] klbuf = VarInt.encode(kbuf.length);
	    	byte[] vbuf = BinarySerializer.objectToByteArray(value, classloader);
//	    	buffer = new ByteArrayOutputStream();
//    		gzipout = new GZIPOutputStream(buffer);
//    		gzipout.write(vbuf);
//    		gzipout.close();
//    		vbuf = buffer.toByteArray();
	    	byte[] vlbuf = VarInt.encode(vbuf.length);
	    	long pos = raf.length();
	    	int length = klbuf.length + kbuf.length + vlbuf.length;
	    	ValueInfo vinfo = new ValueInfo(pos + length, vbuf.length, pos, length + vbuf.length);
	    	length += vbuf.length;
	    	raf.setLength(pos + length);
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
        		byte[] kbuf = BinarySerializer.objectToByteArray(dk, classloader);
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
    protected class DeletedKey
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
