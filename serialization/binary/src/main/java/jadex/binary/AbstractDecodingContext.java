package jadex.binary;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;

import jadex.commons.SUtil;
import jadex.commons.collection.BiHashMap;
import jadex.commons.transformation.STransformation;
import jadex.commons.transformation.traverser.IErrorReporter;
import jadex.commons.transformation.traverser.ITraverseProcessor;

/**
 *  Abstract base class for decoding context.
 */
public abstract class AbstractDecodingContext implements IDecodingContext
{
	/** The classloader */
	protected ClassLoader classloader;
	
	/** The decoder handlers. */
	protected List<IDecoderHandler> decoderhandlers;
	
	/** Handler for versioned operations. */
	protected IVersionedHandler versionedhandler = new VersionedHandler3();
	
	/** The String pool. */
	protected List<String> stringpool;
	
	/** The class name pool. */
	protected List<String> classnamepool;
	
	/** The package fragment pool. */
	protected List<String> pkgpool;
	
	/** A user context. */
	protected Object usercontext;

	/** The postprocessors. */
	protected List<ITraverseProcessor> postprocessors;
	
	/** The last decoded object */
	protected Object lastobject;
	
	/** The current class name. */
	protected String currentclassname;
	
	/** Error Reporter */
	protected IErrorReporter errorreporter;
	
	protected BiHashMap<Long, Object> knownobjects;
	
	/** The serialization config. */
	protected SerializationConfig config;
	
	/**
	 *  Initializes the context.
	 */
	public AbstractDecodingContext(List<IDecoderHandler> decoderhandlers, List<ITraverseProcessor> postprocessors, Object usercontext, ClassLoader classloader, IErrorReporter errorreporter, SerializationConfig config)
	{
//		if (decoderhandlers == null)
//			System.out.println("FAL");
		this.decoderhandlers = decoderhandlers;
		this.postprocessors = postprocessors;
		this.usercontext = usercontext;
		this.classloader = classloader;
		this.errorreporter = errorreporter;
		this.config = config;
		this.knownobjects = new BiHashMap<Long, Object>(new HashMap<Long, Object>(), new IdentityHashMap<Object, Long>());
		if (config==null)
		{
			this.stringpool = new ArrayList<String>();
			this.classnamepool = new ArrayList<String>();
			this.pkgpool = new ArrayList<String>();
		}
		else
		{
			this.stringpool = config.createDecodingStringPool3();
			this.classnamepool = config.createDecodingClassnamePool3();
			this.pkgpool = config.createDecodingFragPool3();
		}
	}
	
	/**
	 * Gets the classloader.
	 * @return The classloader.
	 */
	public ClassLoader getClassloader()
	{
		return classloader;
	}
	
	/**
	 *  Returns the handlers used to decode objects.
	 *  
	 *  @return The handlers.
	 */
	public List<IDecoderHandler> getDecoderHandlers()
	{
		return decoderhandlers;
	}
	
	/**
	 *  Returns the handlers used for post-processing.
	 *  @return Post-processing handlers.
	 */
	public List<ITraverseProcessor> getPostProcessors()
	{
		return postprocessors;
	}
	
	/**
	 *  Creates ID for an object.
	 *  
	 *  @param object The object
	 *  @return The ID.
	 */
	public long createObjectId(Object object)
	{
		long id = knownobjects.size();
		knownobjects.put(id, object);
		return id;
	}
	
	/**
	 *  Gets a known object by ID.
	 *  
	 *  @param id The ID.
	 *  @return The object or null.
	 */
	public Object getObjectForId(long id)
	{
		return knownobjects.get(id);
	}
	
	/**
	 *  Sets a known object by ID.
	 *  
	 *  @param id The ID.
	 *  @param object The object..
	 */
	public void setObjectForId(long id, Object object)
	{
		knownobjects.put(id, object);
	}
	
	/**
	 *  Gets the ID of a known object.
	 *  
	 *  @param object The object
	 *  @return The ID.
	 */
	public Long getObjectId(Object object)
	{
		return knownobjects.rget(object);
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
	 *  Gets the error reporter.
	 *  @return The error reporter.
	 */
	public IErrorReporter getErrorReporter()
	{
		return errorreporter;
	}
	
	/**
	 *  Returns the last object decoded.
	 *  @return The last object decoded.
	 */
	public Object getLastObject()
	{
		return lastobject;
	}
	
	/**
	 *  Sets the last object decoded.
	 *  @param lastobject The last object decoded.
	 */
	public void setLastObject(Object lastobject)
	{
		this.lastobject = lastobject;
	}
	
	/**
	 *  Reads a boolean value from the buffer.
	 *  @return Boolean value.
	 */
	public boolean readBoolean()
	{
		return readByte() > 0;
	}
	
	/**
	 *  Helper method for decoding a string.
	 *  @return String encoded at the current position.
	 */
	public String readString()
	{
		return pooledRead(stringpool);
	}
	
	/**
	 *  Helper method for decoding a variable-sized integer (VarInt).
	 *  @return The decoded value.
	 */
	public long readVarInt()
	{
		byte fb = readByte(); //VarInt.getExtensionSize(content, offset);
		byte ext = VarInt.getExtensionSize(fb);
		byte[] content = new byte[ext + 1];
		content[0] = fb;
		
		read(content, 1, -1);
		
		return VarInt.decodeWithKnownSize(content, 0, ext);
	}
	
	/**
	 *  Helper method for decoding a signed variable-sized integer (VarInt).
	 *  @return The decoded value.
	 */
	public long readSignedVarInt()
	{
		long ret = readVarInt();
		long mask = Long.highestOneBit(ret);
		mask |= mask >> 1;
		boolean neg = (Long.bitCount(ret & mask)) > 1;
		ret = ret & (~mask);
		
		if (neg)
			ret = -ret;
		return ret;
	}
	
	/**
	 *  Helper method for decoding a class name.
	 *  @return String encoded at the current position.
	 */
	public String readClassname()
	{
		return versionedhandler.readClassname();
	}
	
	/**
	 *  Readsa a string using a pool.
	 * @param pool The pool.
	 * @return The string.
	 */
	public String pooledRead(List<String> pool)
	{
		int strid = (int) readVarInt();
		String ret = null;
		if (strid >= pool.size())
		{
			long rawlen = readVarInt();
			if (rawlen <= Integer.MAX_VALUE)
			{
				int length = (int) rawlen;
				ret = new String(read(length), SUtil.UTF8);
				pool.add(ret);
			}
			else
			{
				ret = null;
				pool.add(ret);
			}
		}
		else
		{
			ret = pool.get(strid);
		}
		return ret;
	}
	
	/**
	 *  Gets the current class name.
	 *  @return The current class name.
	 */
	public String getCurrentClassName()
	{
		return this.currentclassname;
	}
	
	/**
	 *  Sets the current class name.
	 *  @return The current class name.
	 */
	protected void setCurrentClassName(String currentclassname)
	{
		this.currentclassname = currentclassname;
	}
	
	/**
	 *  Sets the format version.
	 *  @param version The version.
	 */
	public void setVersion(int version)
	{
		if (version == 2)
		{
			versionedhandler = new VersionedHandler2();
			if (config!=null)
			{
				this.stringpool = config.createDecodingStringPool2();
				this.classnamepool = config.createDecodingClassnamePool2();
				this.pkgpool = config.createDecodingFragPool2();
			}
		}
		else if (version != 3)
			throw new IllegalArgumentException("Binary format version is not supported: " + version);
	}
	
	/**
	 *  Reads a number of bytes from the buffer.
	 *  
	 *  @param count Number of bytes.
	 *  @return Byte array with the bytes.
	 */
	public abstract byte[] read(int count);
	
	/**
	 *  Reads a number of bytes from the buffer and fills the array.
	 *  
	 *  @param array The byte array.
	 *  @return The byte array for convenience.
	 */
	public abstract byte[] read(byte[] array);
	
	/**
	 *  Reads a number of bytes from the buffer and fills the array.
	 *  
	 *  @param array The byte array.
	 *  @param woffset write offset.
	 *  @param wlength length to read.
	 *  @return The byte array for convenience.
	 */
	public abstract byte[] read(byte[] array, int woffset, int wlength);
	
	/** Interface for versioned operations. */
	protected interface IVersionedHandler
	{
		/**
		 *  Helper method for decoding a class name.
		 *  @return String encoded at the current position.
		 */
		public String readClassname();
	}
	
	/** Operations for format version 3. */
	protected class VersionedHandler3 implements IVersionedHandler
	{
		/**
		 *  Helper method for decoding a class name.
		 *  @return String encoded at the current position.
		 */
		 public String readClassname()
		{
			String ret = null;
			
			int classid = (int) readVarInt();
			if (classid >= classnamepool.size())
			{
				int count = (int) readVarInt();
				StringBuilder cnb = new StringBuilder();
				for (int i = 0; i < count; ++i)
				{
					cnb.append(pooledRead(pkgpool));
					cnb.append(".");
				}
				
				cnb.append(pooledRead(pkgpool));
				ret = cnb.toString();
				classnamepool.add(ret);
			}
			else
			{
				ret = classnamepool.get(classid);
			}
			
			ret	= STransformation.getClassname(ret);
				
			setCurrentClassName(ret);
			return ret;
		}
	}
	
	/** Operations for format version 2. */
	protected class VersionedHandler2 implements IVersionedHandler
	{
		/**
		 *  Helper method for decoding a class name.
		 *  @return String encoded at the current position.
		 */
		 public String readClassname()
		{
			String ret = null;
			
			int classid = (int) readVarInt();
			if (classid >= classnamepool.size())
			{
				int count = (int) readVarInt();
				StringBuilder cnb = new StringBuilder();
				for (int i = 0; i < count; ++i)
				{
					cnb.append(pooledRead(pkgpool));
					cnb.append(".");
				}
				cnb.append(readString());
				ret = cnb.toString();
				classnamepool.add(ret);
			}
			else
			{
				ret = classnamepool.get(classid);
			}
			
			ret	= STransformation.getClassname(ret);
				
			setCurrentClassName(ret);
			return ret;
		}
		
		
	}
}
