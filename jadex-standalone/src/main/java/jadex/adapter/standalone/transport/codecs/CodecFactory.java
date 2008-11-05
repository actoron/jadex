package jadex.adapter.standalone.transport.codecs;

import jadex.commons.SReflect;
import jadex.commons.collection.SCollection;

import java.lang.reflect.Field;
import java.util.Map;

/**
 *  The factory for codecs.
 */
public class CodecFactory
{
	//-------- attributes --------
	
	/** The codec factory instance. */
	//protected static CodecFactory instance;
	
	/** The mapping (id -> codec class). */
	protected Map codecclasses;

	/** The codec cache (id -> codec instance). */
	protected Map codeccache;

	/** The default codec id. */
	protected byte default_id; 
	
	//-------- constructors --------

	/**
	 *  Get the singleton instance.
	 *  @return The codec factory.
	 * /
	public static CodecFactory getInstance()
	{
		if(instance==null)
			instance = new CodecFactory();
		return instance;
	}*/
	
	/**
	 *  Create a new codec factory.
	 */
	public CodecFactory()
	{
		default_id = NuggetsCodec.CODEC_ID;
		codecclasses = SCollection.createHashMap();
		codeccache = SCollection.createHashMap();
		addCodec(SerialCodec.class);
		addCodec(NuggetsCodec.class);
		addCodec(XMLCodec.class);
	}
	
	//-------- methods --------

	/**
	 *  Create a new default encoder.
	 *  @return The new encoder.
	 */
	public IEncoder getDefaultEncoder()
	{
		return getEncoder(default_id);
	}

	/**
	 *  Create a new default decoder.
	 *  @return The new decoder.
	 */
	public IDecoder getDefaultDecoder()
	{
		return getDecoder(default_id);
	}
	
	/**
	 *  Create a new default encoder.
	 *  @return The new encoder.
	 */
	public IEncoder getEncoder(byte id)
	{
		Byte idd = new Byte(id);
		IEncoder ret = (IEncoder)codeccache.get(idd);
		if(!codecclasses.containsKey(idd))
			throw new IllegalArgumentException("Unknown codec id: "+id);
	
		if(ret==null)
		{
			Class cc = (Class)codecclasses.get(idd);
		
			try
			{
				ret = (IEncoder)cc.newInstance();
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
	 *  Create a new default decoder.
	 *  @return The new decoder.
	 */
	public IDecoder getDecoder(byte id)
	{
		Byte idd = new Byte(id);
		IDecoder ret = (IDecoder)codeccache.get(idd);
		if(!codecclasses.containsKey(idd))
			throw new IllegalArgumentException("Unknown codec id: "+id);
	
		if(ret==null)
		{
			Class cc = (Class)codecclasses.get(idd);
		
			try
			{
				ret = (IDecoder)cc.newInstance();
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
	 *  Set the default decoder/encoder id. 
	 *  @param id The id.
	 */
	public void setDefaultCodec(Object codec)
	{
		default_id = getCodecId(codec.getClass());
	}
	
	/**
	 *  Get the default decoder/encoder id. 
	 *  @param id The id.
	 */
	public byte getDefaultCodecId()
	{
		return default_id;
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
			Field f = codec_class.getDeclaredField("CODEC_ID");
			Byte codec_id = new Byte(f.getByte(null));
			codecclasses.put(codec_id, codec_class);
		}
		catch(Exception e)
		{
			throw new RuntimeException("Codec id not found. Needs explicit CODEC_ID spec: "+codec_class);
		}
	}
	
	/**
	 *  Remove a codec via its id.
	 *  @param id The codec id.
	 */
	public void removeCodec(Class codec_class)
	{
		try
		{
			Field f = codec_class.getDeclaredField("CODEC_ID");
			Byte codec_id = new Byte(f.getByte(null));
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
	public byte getCodecId(Class codec_class)
	{
		byte ret = -1;
		try
		{
			Field f = codec_class.getDeclaredField("CODEC_ID");
			ret = f.getByte(null);
		}
		catch(Exception e)
		{
			throw new RuntimeException("Codec id not found. Needs explicit CODEC_ID spec: "+codec_class);
		}
		return ret;
	}
}
