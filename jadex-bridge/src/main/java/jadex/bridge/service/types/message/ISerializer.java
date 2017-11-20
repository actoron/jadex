package jadex.bridge.service.types.message;

import java.io.IOException;
import java.io.InputStream;

import jadex.commons.transformation.binaryserializer.IErrorReporter;
import jadex.commons.transformation.traverser.ITraverseProcessor;

/**
 *  Encode and decode an object from a byte representation.
 */
public interface ISerializer
{
	/** Constant for accessing the serializer id. */
	public static final String SERIALIZER_ID = "SERIALIZER_ID";
	
	/**
	 *  Get the serializer id.
	 *  @return The serializer id.
	 */
	public int getSerializerId();
	
	/**
	 *  Encode data with the serializer.
	 *  @param val The value.
	 *  @param classloader The classloader.
	 *  @param preproc The encoding preprocessors.
	 *  @return The encoded object.
	 */
	public byte[] encode(Object val, ClassLoader classloader, ITraverseProcessor[] preprocs, Object usercontext);
	
	/**
	 *  Decode data with the serializer.
	 *  @param bytes The value bytes as byte array or input stream.
	 *  @return The decoded object.
	 */
//	public Object decode(byte[] bytes, ClassLoader classloader);
	public Object decode(byte[] bytes, ClassLoader classloader, ITraverseProcessor[] postprocs, IErrorReporter rep, Object usercontext);
	
	/**
	 *  Decode an object.
	 *  @return The decoded object.
	 *  @throws IOException
	 */
	public Object decode(InputStream is, ClassLoader classloader, ITraverseProcessor[] postprocs, IErrorReporter rep, Object usercontext);
	
}