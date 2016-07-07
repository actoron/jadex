package jadex.bridge.service.types.message;

import java.util.List;

import jadex.commons.transformation.binaryserializer.IDecoderHandler;
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
	public byte getSerializerId();
	
	/**
	 *  Configures the preprocessor stage of the encoding.
	 *  @param processors The preprocessors.
	 */
	public void setPreprocessors(ITraverseProcessor[] processors);
	
	/**
	 *  Configures the postprocessor stage of the encoding.
	 *  @param processors The postprocessors.
	 */
	public void setPostprocessors(ITraverseProcessor[] processors);
	
	/**
	 *  Encode data with the serializer.
	 *  @param val The value.
	 *  @param classloader The classloader.
	 *  @param preproc The encoding preprocessors.
	 *  @return The encoded object.
	 */
	public byte[] encode(Object val, ClassLoader classloader, ITraverseProcessor[] preprocs);
	
	/**
	 *  Decode data with the serializer.
	 *  @param bytes The value bytes as byte array or input stream.
	 *  @return The decoded object.
	 */
//	public Object decode(byte[] bytes, ClassLoader classloader);
	public Object decode(Object bytes, ClassLoader classloader, IDecoderHandler[] postprocs, IErrorReporter rep);
	
}