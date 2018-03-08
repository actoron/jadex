package jadex.binary;

import java.util.List;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 *  Codec for encoding and decoding stacktrace element.
 */
public class StackTraceElementCodec extends AbstractCodec
{
	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class<?> clazz)
	{
		return SReflect.isSupertype(StackTraceElement.class, clazz);
	}
	
	/**
	 *  Creates the object during decoding.
	 *  
	 *  @param clazz The class of the object.
	 *  @param context The decoding context.
	 *  @return The created object.
	 */
	public Object createObject(Class<?> clazz, IDecodingContext context)
	{
		return new StackTraceElement((String)SBinarySerializer.decodeObject(context), (String)SBinarySerializer.decodeObject(context), 
				(String)SBinarySerializer.decodeObject(context), (int)context.readSignedVarInt());
	}

	/**
	 *  Encode the object.
	 */
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> preprocessors, List<ITraverseProcessor> processors, MODE mode, Traverser traverser, ClassLoader targetcl, IEncodingContext ec)
	{
		StackTraceElement ste = (StackTraceElement)object;
		traverser.doTraverse(ste.getClassName(), String.class, preprocessors, processors, mode, ec.getClassLoader(), ec);
		traverser.doTraverse(ste.getMethodName(), String.class, preprocessors, processors, mode, ec.getClassLoader(), ec);
		traverser.doTraverse(ste.getFileName(), String.class, preprocessors, processors, mode, ec.getClassLoader(), ec);
		ec.writeSignedVarInt(ste.getLineNumber());
		
		return object;
	}
}

