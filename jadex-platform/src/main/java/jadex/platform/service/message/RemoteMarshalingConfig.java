package jadex.platform.service.message;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import jadex.bridge.service.types.message.IBinaryCodec;
import jadex.bridge.service.types.message.ISerializer;
import jadex.commons.SReflect;
import jadex.commons.collection.SCollection;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.platform.service.message.transport.codecs.GZIPCodec;
import jadex.platform.service.message.transport.codecs.XZCodec;
import jadex.platform.service.message.transport.serializers.JadexBinarySerializer;
import jadex.platform.service.message.transport.serializers.JadexJsonSerializer;

/**
 *  Configuration providing the tools for marshalling messages (previously CodecFactory).
 */
public class RemoteMarshalingConfig
{
	//-------- attributes --------
	
	// Cannot be static because each platform should
	// have their own RemoteMarshalingConfig

	/** The codec cache (id -> codec instance). */
	protected Map<Byte, IBinaryCodec> codecs;

	/** The codec cache (id -> serializer instance). */
	protected Map<Byte, ISerializer> serializers;
	
	/** Serialization preprocessors. */
	protected List<ITraverseProcessor> preprocessors;
	
	/** Serialization postprocessors. */
	protected List<ITraverseProcessor> postprocessors;
	
	/** The default serializer id. */
	protected byte default_sid;
	
	/** The default binary codec id. */
	protected byte[] default_ids; 
	
	/** The default codecs. */
	protected volatile IBinaryCodec[] default_codecs;
	
	/** The serializer names, array index equals CODEC_ID. */
	public static String[] SERIALIZER_NAMES = {"JadexBinarySerializer"};
	
	/** The codec names, array index equals CODEC_ID. */
	public static String[] BINARYCODEC_NAMES = {"No Codec", "GZIPCodec"};
	
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
	public RemoteMarshalingConfig()
	{
		this(null, null, null, null);
	}
	
	/**
	 *  Create a new codec factory.
	 */
	public RemoteMarshalingConfig(Class<?>[] serializers, Class<?> defaultserializer, Class<?>[] codecs, Class<?>[] default_codecs)
	{
		preprocessors = new ArrayList<ITraverseProcessor>();
		postprocessors = new ArrayList<ITraverseProcessor>();
		this.codecs = SCollection.createHashMap();
		this.serializers = SCollection.createHashMap();
		
		if (codecs==null)
		{
			if (SReflect.isAndroid())
			{
				// Hard-code classes for Android since scanning may not work.
				codecs = new Class[] {GZIPCodec.class, XZCodec.class};
				serializers= new Class[] {JadexBinarySerializer.class, JadexJsonSerializer.class};
			}
			else
			{
				// TODO: Implement scan for available serializers and codecs, use hard-coding for now
//				codecs = new Class[] {GZIPCodec.class};
				codecs = new Class[] {GZIPCodec.class, XZCodec.class};
				
				String[] classnames = new String[]
				{
					"jadex.platform.service.message.transport.serializers.JadexBinarySerializer",
					"jadex.platform.service.message.transport.serializers.JadexJsonSerializer"
				};
				
				List<Class<?>> serlist = new ArrayList<Class<?>>();
				
				ClassLoader cl = this.getClass().getClassLoader();
				for (int i = 0; i < classnames.length; ++i)
				{
					Class<?> clazz = SReflect.classForName0(classnames[i], cl);
					if (clazz != null)
						serlist.add(clazz);
				}
				
				serializers = serlist.toArray(new Class[serlist.size()]);
			}
		}
		
		for (int i = 0; i < codecs.length; ++i)
		{
			try
			{
				this.codecs.put((Byte)codecs[i].getField(IBinaryCodec.CODEC_ID).get(null), (IBinaryCodec) codecs[i].newInstance());
			}
			catch (Exception e)
			{
			}
		}
		
		for (int i = 0; i < serializers.length; ++i)
		{
			try
			{
				this.serializers.put((Byte)serializers[i].getField(ISerializer.SERIALIZER_ID).get(null), (ISerializer) serializers[i].newInstance());
			}
			catch (Exception e)
			{
				e.printStackTrace();
			}
		}
		
		if(defaultserializer!=null)
		{
			try
			{
				default_sid = (Byte) defaultserializer.getField(ISerializer.SERIALIZER_ID).get(null);
			}
			catch (Exception e)
			{
			}
		}
		else
		{
			default_sid = JadexJsonSerializer.SERIALIZER_ID;
		}
		
		if(default_codecs!=null && default_codecs.length>0)
		{
			default_ids = getIds(default_codecs);
		}
		else
		{
			default_ids = new byte[]{GZIPCodec.CODEC_ID};
		}
	}
	
	/**
	 *  Returns the current set of preprocessors.
	 *  @return The current set of preprocessors.
	 */
	public ITraverseProcessor[] getPreprocessors()
	{
		return preprocessors.toArray(new ITraverseProcessor[preprocessors.size()]);
	}
	
	/**
	 *  Returns the current set of postprocessors.
	 *  @return The current set of postprocessors.
	 */
	public ITraverseProcessor[] getPostprocessors()
	{
		return postprocessors.toArray(new ITraverseProcessor[postprocessors.size()]);
	}
	
	/**
	 *  Get the default codec ids.
	 */
	protected static byte[] getIds(Class<?>[] default_codecs)
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
	 *  Adds preprocessors to the encoding stage.
	 *  @param Preprocessors.
	 */
	public void addPreprocessors(ITraverseProcessor[] processors)
	{
		preprocessors.addAll(Arrays.asList(processors));
	}
	
	/** 
	 *  Adds postprocessors to the encoding stage.
	 *  @param Postprocessors.
	 */
	public void addPostprocessors(ITraverseProcessor[] processors)
	{
		postprocessors.addAll(Arrays.asList(processors));
	}
	
	/**
	 *  Adds a new serializer.
	 *  @param codec The serializer.
	 */
	public void addSerializer(ISerializer serializer)
	{
		serializers.put(serializer.getSerializerId(), serializer);
	}
	
	/**
	 *  Removes a serializer.
	 *  @param codec The serializer.
	 */
	public void removeSerializer(ISerializer serializer)
	{
		removeSerializer(serializer.getSerializerId());
	}
	
	/**
	 *  Removes a serializer.
	 *  @param codecid The serializer id.
	 */
	public void removeSerializer(byte serializerid)
	{
		serializers.remove(serializerid);
	}

	/**
	 *  Gets a specific serializer.
	 *  @return The serializer.
	 */
	public ISerializer getSerializer(byte id)
	{
		ISerializer ret = serializers.get(id);
		if (ret == null)
			throw new IllegalArgumentException("Unknown serializer id: "+id);
		
		return ret;
	}

	/**
	 *  Get all codecs.
	 */
	public Map<Byte, ISerializer> getAllSerializers()
	{
		Map<Byte, ISerializer> ret = new HashMap<Byte, ISerializer>(serializers);
		
		return ret;
	}
	
	/**
	 *  Get the default serializer id. 
	 *  @return The id.
	 */
	public byte getDefaultSerializerId()
	{
		return default_sid;
	}
	
	/**
	 *  Get the default serializer. 
	 *  @return The serializer.
	 */
	public ISerializer getDefaultSerializer()
	{
		return getSerializer(default_sid);
	}
	
	/**
	 *  Adds a new binary codec.
	 *  @param codec The codec.
	 */
	public void addCodec(IBinaryCodec codec)
	{
		codecs.put(codec.getCodecId(), codec);
	}
	
	/**
	 *  Removes a new binary codec.
	 *  @param codec The codec.
	 */
	public void removeCodec(IBinaryCodec codec)
	{
		removeCodec(codec.getCodecId());
	}
	
	/**
	 *  Removes a new binary codec.
	 *  @param codecid The codec id.
	 */
	public void removeCodec(byte codecid)
	{
		codecs.remove(codecid);
	}

	/**
	 *  Create a new default encoder.
	 *  @return The new encoder.
	 */
	public IBinaryCodec getCodec(byte id)
	{
		IBinaryCodec ret = codecs.get(id);
		if (ret == null)
			throw new IllegalArgumentException("Unknown codec id: "+id);
		
		return ret;
	}

	/**
	 *  Get all codecs.
	 */
	public Map<Byte, IBinaryCodec> getAllCodecs()
	{
		Map<Byte, IBinaryCodec> ret = new HashMap<Byte, IBinaryCodec>(codecs);
		
		return ret;
	}
	
	/**
	 *  Get the default codecs.
	 *  @return The default codecs.
	 */
	public IBinaryCodec[] getDefaultCodecs()
	{
		if(default_codecs==null)
		{
			synchronized(this)
			{
				if(default_codecs==null)
				{
					byte[] defids = getDefaultCodecIds();
					default_codecs = new IBinaryCodec[defids.length];
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
	 *  Get the codec id for a codec class.
	 */
	public static byte getCodecId(Class<?> codec_class)
	{
		byte ret = -1;
		try
		{
			Field f = codec_class.getDeclaredField(IBinaryCodec.CODEC_ID);
			ret = f.getByte(null);
		}
		catch(Exception e)
		{
			throw new RuntimeException("Codec id not found. Needs explicit CODEC_ID spec: "+codec_class);
		}
		return ret;
	}
}
