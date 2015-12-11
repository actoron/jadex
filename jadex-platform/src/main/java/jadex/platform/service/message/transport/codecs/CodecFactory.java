package jadex.platform.service.message.transport.codecs;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import jadex.bridge.service.types.message.ICodec;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.collection.SCollection;

/**
 *  The factory for codecs.
 */
public class CodecFactory
{
	//-------- attributes --------
	
	// Cannot be static because each platform should
	// have their own codec factory
	/** The codec factory instance. */
//	protected static CodecFactory instance;
	
	/** The mapping (id -> codec class). */
	protected Map codecclasses;

	/** The codec cache (id -> codec instance). */
	protected Map codeccache;

	/** The default codec id. */
	protected byte[] default_ids; 
	
	/** The default codecs. */
	protected volatile ICodec[] default_codecs; 
	
	/** The codec names, array index equals CODEC_ID. */
	public static String[] CODEC_NAMES = {"No Codec","SerialCodec","NuggetsCodec", "XMLCodec", "JadexXMLCodec", "GZIPCodec", "JadexBinaryCodec", "JadexBinaryCodec2"};
	
	//-------- constructors --------

//	/**
//	 *  Get the singleton instance.
//	 *  @return The codec factory.
//	 */
//	public static CodecFactory getInstance()
//	{
//		if(instance==null)
//			instance = new CodecFactory();
//		return instance;
//	}
	
	/**
	 *  Create a new codec factory.
	 */
	public CodecFactory()
	{
		this(null, null);
	}
	
	/**
	 *  Create a new codec factory.
	 */
	public CodecFactory(Class[] codecs, Class[] default_codecs)
	{
		codecclasses = SCollection.createHashMap();
		codeccache = SCollection.createHashMap();
		if(codecs==null)
		{
			// dynamically decide if JadexXMLCodec is available
			codecs = !SReflect.isAndroid()
				? new Class[]{SerialCodec.class, NuggetsCodec.class, XMLCodec.class, JadexXMLCodec.class, GZIPCodec.class, JadexBinaryCodec.class, JadexBinaryCodec2.class,}
				: SUtil.androidUtils().hasXmlSupport()
						? new Class[]{SerialCodec.class, GZIPCodec.class, JadexBinaryCodec2.class, JadexBinaryCodec.class, JadexXMLCodec.class}
						: new Class[]{SerialCodec.class, GZIPCodec.class, JadexBinaryCodec2.class, JadexBinaryCodec.class};
//			codecs = !SReflect.isAndroid()
//					? new Class[]{SerialCodec.class, NuggetsCodec.class, XMLCodec.class, JadexXMLCodec.class, GZIPCodec.class, JadexBinaryCodec.class}
//					: SUtil.androidUtils().hasXmlSupport()
//							? new Class[]{SerialCodec.class, GZIPCodec.class, JadexBinaryCodec.class, JadexXMLCodec.class}
//							: new Class[]{SerialCodec.class, GZIPCodec.class, JadexBinaryCodec.class};
		}
		for(int i=0; i<codecs.length; i++)
		{
			// Add codec classes
			addCodec(codecs[i]);
		}
		
		if(default_codecs!=null && default_codecs.length>0)
		{
			default_ids = getIds(default_codecs);
		}
		else
		{
//			default_ids = !SReflect.isAndroid()
//					? new byte[]{JadexXMLCodec.CODEC_ID, GZIPCodec.CODEC_ID}
//					: new byte[]{JadexBinaryCodec.CODEC_ID, GZIPCodec.CODEC_ID};
			default_ids = new byte[]{JadexBinaryCodec2.CODEC_ID, GZIPCodec.CODEC_ID};
		}
	}
	
	/**
	 *  Get the default codec ids.
	 */
	protected static byte[] getIds(Class[] default_codecs)
	{
		byte[] ret = new byte[default_codecs.length];
		for(int i=0; i<ret.length; i++)
		{
			ret[i] = getCodecId(default_codecs[i]);
		}
		return ret;
	}
	
	//-------- methods --------

//	/**
//	 *  Create a new default encoder.
//	 *  @return The new encoder.
//	 */
//	public ICodec[] getDefaultCodecs()
//	{
//		return getCodec(default_id);
//	}

	/**
	 *  Create a new default encoder.
	 *  @return The new encoder.
	 */
	public ICodec getCodec(byte id)
	{
		Byte idd = Byte.valueOf(id);
		ICodec ret = (ICodec)codeccache.get(idd);
		if(!codecclasses.containsKey(idd))
			throw new IllegalArgumentException("Unknown codec id: "+id);
	
		if(ret==null)
		{
			Class cc = (Class)codecclasses.get(idd);
		
			try
			{
				ret = (ICodec)cc.newInstance();
				codeccache.put(idd, ret);
			}
			catch(Exception e)
			{
				throw new RuntimeException("Decoder not found: "+id);
			}
		}
			
		return ret;
	}

	/**
	 *  Get all codecs.
	 */
	public Map<Byte, ICodec> getAllCodecs()
	{
		Map<Byte, ICodec> ret = new HashMap<Byte, ICodec>();
		
		for(Iterator<Byte> it = codecclasses.keySet().iterator(); it.hasNext(); )
		{
			Byte id = it.next();
			ret.put(id, getCodec(id.byteValue()));
		}
		
		return ret;
	}
	
	/**
	 *  Get the default codecs.
	 *  @return The default codecs.
	 */
	public ICodec[] getDefaultCodecs()
	{
		if(default_codecs==null)
		{
			synchronized(this)
			{
				if(default_codecs==null)
				{
					byte[] defids = getDefaultCodecIds();
					default_codecs = new ICodec[defids.length];
					for(int i=0; i<defids.length; i++)
					{
						default_codecs[i] = getCodec(defids[i]);
					}
				}
			}
		}
		
		return default_codecs;
	}
	
	/**
	 *  Set the default decoder/encoder id. 
	 *  @param chainid The id.
	 */
	public void setDefaultCodecIds(byte[] codecids)
	{
		this.default_ids = codecids.clone();
	}
	
	/**
	 *  Get the default decoder/encoder id. 
	 *  @param chainid The id.
	 */
	public byte[] getDefaultCodecIds()
	{
		return default_ids;
	}
	
	/**
	 *  Add a new codec.
	 *  @param codec_id The codec_id.
	 *  @param codec_clname The codec class name (fully qualified).
	 * /
	public void addCodec(String codec_clname) throws ClassNotFoundException
	{
		addCodec(SReflect.classForName(codec_clname));
	}*/
	
	/**
	 *  Remove a codec.
	 *  @param codec_clname The codec class name (fully qualified).
	 * /
	public void removeCodec(String codec_clname) throws ClassNotFoundException
	{
		removeCodec(SReflect.classForName(codec_clname));
	}*/
	
	/**
	 *  Add a new codec.
	 *  @param codec_id The codec_id.
	 *  @param codec_class The codec class.
	 */
	public void addCodec(Class codec_class)
	{
		try
		{
			Field f = codec_class.getDeclaredField(ICodec.CODEC_ID);
			Byte codec_id = Byte.valueOf(f.getByte(null));
			codecclasses.put(codec_id, codec_class);
		}
		catch(Exception e)
		{
			throw new RuntimeException("Codec id not found. Needs explicit CODEC_ID spec: "+codec_class);
		}
	}
	
	/**
	 *  Remove a codec via its id.
	 *  @param chainid The codec id.
	 */
	public void removeCodec(Class codec_class)
	{
		try
		{
			Field f = codec_class.getDeclaredField(ICodec.CODEC_ID);
			Byte codec_id = Byte.valueOf(f.getByte(null));
			codecclasses.remove(codec_id);
			codeccache.remove(codec_id);
		}
		catch(Exception e)
		{
			throw new RuntimeException("Codec id not found. Needs explicit CODEC_ID spec: "+codec_class);
		}
	}
	
	/**
	 *  Get the codec id for a codec class.
	 */
	public static byte getCodecId(Class codec_class)
	{
		byte ret = -1;
		try
		{
			Field f = codec_class.getDeclaredField(ICodec.CODEC_ID);
			ret = f.getByte(null);
		}
		catch(Exception e)
		{
			throw new RuntimeException("Codec id not found. Needs explicit CODEC_ID spec: "+codec_class);
		}
		return ret;
	}
}
