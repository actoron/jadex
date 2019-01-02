package jadex.binary;

import java.util.HashMap;
import java.util.HashSet;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import jadex.commons.SUtil;
import jadex.commons.transformation.STransformation;
import jadex.commons.transformation.traverser.ITraverseProcessor;

/**
 *  Abstract encoding context that provides base functionality.
 *
 */
public abstract class AbstractEncodingContext implements IEncodingContext
{
	/** Map with known objects and their IDs */
	protected Map<Object, Long> knownobjects;
	
	/** The last input object. */
	protected Object lastinputobject;
	
	/** The preprocessors. */
	protected List<ITraverseProcessor> preprocessors;
	
	/** The classloader */
	protected ClassLoader classloader;
	
	/** The root object. */
	protected Object rootobject;
	
	/** A user context. */
	protected Object usercontext;
	
	/** Flag indicating class names should not be written (can be temporarily disabled for one write). */
	protected boolean ignorewriteclass;
	
	/** The cache for non-inner classes. */
	protected Set<Class<?>> nonanonclasscache = new HashSet<Class<?>>();
	
	/** The string pool. */
	protected Map<String, Integer> stringpool;
	
	/** Cache for class IDs. */
	protected Map<Class<?>, Integer> classidcache;
	
	/** The class name pool. */
	protected Map<String, Integer> classnamepool;
	
	/** The package fragment pool. */
	protected Map<String, Integer> fragpool;
	
	/** The bytes written to the output. */
	protected long writtenbytes;
	
	public AbstractEncodingContext(Object rootobject, Object usercontext, List<ITraverseProcessor> preprocessors, ClassLoader classloader, SerializationConfig config)
	{
		this.knownobjects = new IdentityHashMap<Object, Long>();
		this.rootobject = rootobject;
		this.usercontext = usercontext;
		this.preprocessors = preprocessors;
		this.classloader = classloader;
		this.ignorewriteclass = false;
		classidcache = new HashMap<Class<?>, Integer>();
		stringpool = config==null?new HashMap<String, Integer>():config.createEncodingStringPool();
		classnamepool = config==null?new HashMap<String, Integer>():config.createEncodingClassnamePool();
		fragpool = config==null?new HashMap<String, Integer>():config.createEncodingFragPool();
	}
	
	/**
	 *  Returns the preprocessors.
	 *  @return The preprocessors
	 */
	public List<ITraverseProcessor> getPreprocessors()
	{
		return preprocessors;
	}
	
	/**
	 * Gets the classloader.
	 * @return The classloader.
	 */
	public ClassLoader getClassLoader()
	{
		return classloader;
	}
	
	/**
	 *  Get the rootobject.
	 *  @return the rootobject.
	 */
	public Object getRootObject()
	{
		return rootobject;
	}
	
	/**
	 *  Returns the number of bytes written.
	 *  
	 *  @return The number of bytes written.
	 */
	public long getWrittenBytes()
	{
		return writtenbytes;
	}
	
	/**
	 *  Returns the user context.
	 *  @return The user context.
	 */
	public Object getUserContext()
	{
		return usercontext;
	}
	
	/**
	 *  Returns the non-inner class cache.
	 *  @return The non-inner class cache.
	 */
	public Set<Class<?>> getNonInnerClassCache()
	{
		return nonanonclasscache;
	}
	
	/**
	 *  Creates ID for an object.
	 *  
	 *  @param object The object
	 *  @return The ID.
	 */
	public long createObjectId()
	{
		long ret = knownobjects.size();
		knownobjects.put(lastinputobject, ret);
		return ret;
	}
	
	/**
	 *  Sets the object for which the next createObjectId() call creates an ID.
	 * @param object The object.
	 */
	public void setInputObject(Object object)
	{
		lastinputobject = object;
	}
	
	/**
	 *  Gets the ID of a known object.
	 *  
	 *  @param object The object
	 *  @return The ID.
	 */
	public Long getObjectId(Object object)
	{
		return knownobjects.get(object);
	}
	
	/**
	 *  Puts the context in a state where the next call to
	 *  writeClass is ignored.
	 *  
	 *  @param state If true, the next class write will be ignored and the state reset.
	 */
	public void setIgnoreNextClassWrite(boolean state)
	{
		ignorewriteclass = state;
	}
	
	protected boolean isIgnoreNextClassWrite()
	{
		return ignorewriteclass;
	}
	
	/**
	 *  Writes a VarInt.
	 *  
	 *  @param value Value being written.
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
	 *  Writes a signed VarInt.
	 *  
	 *  @param value Value being written.
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
	 * 
	 * @param string
	 */
	public void writeString(String string)
	{
		pooledWrite(stringpool, string);
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
	
	/**
	 *  Writes a boolean.
	 *  @param bool The boolean.
	 */
	public void writeBoolean(boolean bool)
	{
		writeByte((byte) (bool? 1 : 0));
	}
	
	/**
	 *  Starts an object frame
	 *  when using a context with framing support.
	 */
	public void startObjectFrame()
	{
		startObjectFrame(false);
	}
	
	/**
	 *  Starts an object frame
	 *  when using a context with framing support.
	 *  
	 *  @param fixedsize If true, use fixed-size (integer) framing.
	 *  				 Set true if the object being framed is expected
	 *  				 to be larger than 127 bytes (same type of object MUST use
	 *  				 either fixed OR variable framing).
	 */
	public void startObjectFrame(boolean fixedsize)
	{
		// default no framing
	}
	
	/**
	 *  Stops an object frame
	 *  when using a context with framing support.
	 */
	public void stopObjectFrame()
	{
		// default no framing
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
