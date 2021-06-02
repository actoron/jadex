package jadex.platform.service.serialization.serializers;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import jadex.binary.IDecoderHandler;
import jadex.binary.SBinarySerializer;
import jadex.binary.SerializationConfig;
import jadex.bridge.component.IMsgHeader;
import jadex.bridge.component.impl.RemoteExecutionComponentFeature;
import jadex.bridge.service.types.message.ISerializer;
import jadex.commons.SUtil;
import jadex.commons.transformation.traverser.IErrorReporter;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.platform.service.serialization.serializers.JadexJsonSerializer.JsonByteArrayReadProcessor;
import jadex.platform.service.serialization.serializers.JadexJsonSerializer.JsonByteArrayWriteProcessor;
import jadex.platform.service.serialization.serializers.jsonread.JsonComponentIdentifierProcessor;
import jadex.platform.service.serialization.serializers.jsonwrite.JsonResourceIdentifierProcessor;
import jadex.platform.service.serialization.serializers.jsonwrite.JsonServiceIdentifierProcessor;
import jadex.platform.service.serialization.serializers.jsonwrite.JsonServiceProcessor;
import jadex.transformation.jsonserializer.JsonTraverser;

/**
 *  The Jadex Binary serializer. Codec supports parallel
 *  calls of multiple concurrent clients (no method
 *  synchronization necessary).
 *  
 *  Converts object -> byte[] and byte[] -> object.
 */
public class JadexBinarySerializer implements ISerializer
{
	//-------- constants --------
	
	/** The JadexBinary serializer id. */
	public static final int SERIALIZER_ID = 0;
	
	/** The debug flag. */
	protected boolean DEBUG = false;
	
	/**
	 *  Config with pre-defined Strings for faster encoding/decoding.
	 */
	protected static SerializationConfig CONFIG = new SerializationConfig(new String[] {
		"jadex",
		"bridge",
		"platform",
		"service",
		"commons",
		"component",
		"impl",
		"remotecommands",
		"Tuple2",
		"IService",
		"ClassInfo",
		"ClassInfo[]",
		"MethodInfo",
		"MsgHeader",
		"ComponentIdentifier",
		"ServiceScope",
		"RemoteFinishedCommand",
		"RemoteForwardCmdCommand",
		"RemoteIntermediateResultCommand",
		"RemotePullCommand",
		"RemoteBackwardCommand",
		"RemoteResultCommand",
		"RemoteTerminationCommand",
		"RemoteMethodInvocationCommand",
		"java",
		"lang",
		"util",
		"String",
		"Boolean",
		"Integer",
		"ArrayList",
		"HashSet",
		"HashMap",
		"Collections$UnmodifiableSet",
		"Long",
		"FALSE",
		"TRUE",
		"0",
		"1",
		"name",
		"properties",
		"typeName",
		"serviceName",
		"serviceType",
		"networkNames",
		"scope",
		"security",
		"DEFAULT",
		"NONE",
		"COMPONENT_ONLY",
		"NETWORK",
		"APPLICATION_NETWORK",
		"APPLICATION_GLOBAL",
		"GLOBAL",
		RemoteExecutionComponentFeature.RX_ID,
		"__fw_sender__", //RelayTransportAgent.FORWARD_SENDER,
		"__fw_dest__", //RelayTransportAgent.FORWARD_DEST,
		IMsgHeader.SENDER,
		IMsgHeader.RECEIVER,
		IMsgHeader.CONVERSATION_ID,
		IMsgHeader.XID
	});
	
	/** The write processors. */
	public List<ITraverseProcessor> writeprocs;
	
	/** The read processors. */
	public List<IDecoderHandler> readprocs;
	
	/**
	 *  Create a new serializer.
	 */
	public JadexBinarySerializer()
	{
		writeprocs = Collections.synchronizedList(new ArrayList<ITraverseProcessor>());
		writeprocs.addAll(SBinarySerializer.ENCODER_HANDLERS);
		
		readprocs = Collections.synchronizedList(new ArrayList<IDecoderHandler>());
		readprocs.addAll(SBinarySerializer.DECODER_HANDLERS);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the serializer id.
	 *  @return The serializer id.
	 */
	public int getSerializerId()
	{
		return SERIALIZER_ID;
	}
	
	/**
	 *  Encode data with the serializer.
	 *  @param val The value.
	 *  @param classloader The classloader.
	 *  @param preproc The encoding preprocessors.
	 *  @return The encoded object.
	 */
	public byte[] encode(Object val, ClassLoader classloader, ITraverseProcessor[] preprocs, Object usercontext)
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		SBinarySerializer.writeObjectToStream(baos, val, preprocs!=null?Arrays.asList(preprocs):null, writeprocs, usercontext, classloader, CONFIG);
		
		byte[] ret = baos.toByteArray();
		
		if(DEBUG)
			System.out.println("encode message: "+(new String(ret, SUtil.UTF8)));
		return ret;
	}

	/**
	 *  Decode an object.
	 *  @return The decoded object.
	 *  @throws IOException
	 */
	public Object decode(byte[] bytes, ClassLoader classloader, ITraverseProcessor[] postprocs, IErrorReporter rep, Object usercontext)
	{
		if(DEBUG)
			System.out.println("decode message: "+(new String((byte[])bytes, SUtil.UTF8)));
		
		InputStream is = new ByteArrayInputStream((byte[]) bytes);
		
		return decode(is, classloader, postprocs, rep, usercontext);
	}
	
	/**
	 *  Decode an object.
	 *  @return The decoded object.
	 *  @throws IOException
	 */
	public Object decode(InputStream is, ClassLoader classloader, ITraverseProcessor[] postprocs, IErrorReporter rep)
	{
		return decode(is, classloader, postprocs, rep, null);
	}
	
	/**
	 *  Decode an object.
	 *  @return The decoded object.
	 *  @throws IOException
	 */
	public Object decode(InputStream is, ClassLoader classloader, ITraverseProcessor[] postprocs, IErrorReporter rep, Object usercontext)
	{
		Object ret = SBinarySerializer.readObjectFromStream(is, postprocs!=null?Arrays.asList(postprocs):null, usercontext, classloader, null, CONFIG, readprocs);
		
		try
		{
			is.close();
		}
		catch (IOException e)
		{
		}
		
		return ret;
	}
	
	/**
	 *  Add a processor pair.
	 */
	public void addProcessor(IDecoderHandler readproc, ITraverseProcessor writeproc)
	{
		readprocs.add(0, readproc);
		writeprocs.add(0, writeproc);
	}
}