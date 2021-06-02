package jadex.binary;

import java.lang.reflect.Type;
import java.util.List;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;

/**
 * 
 */
public abstract class AbstractCodec implements ITraverseProcessor, IDecoderHandler
{
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @return The processed object.
	 */
	public Object process(Object object, Type type, Traverser traverser, List<ITraverseProcessor> conversionprocessors, List<ITraverseProcessor> processors, MODE mode, ClassLoader targetcl, Object context)
	{
		IEncodingContext ec = (IEncodingContext) context;
		
		Class<?> clazz = SReflect.getClass(type);
		
//		if(canReference(orig, clazz, ec))
//			ec.createObjectId(object);
		if(canReference(object, clazz, ec))
			ec.createObjectId();
		
//		object = runPreProcessors(object, clazz, processors, traverser, traversed, clone, context);
		if (clazz == null || !clazz.equals(object.getClass()))
			clazz = object == null? null : object.getClass();
		
		ec.writeClass(clazz);
		
		ec.startObjectFrame(isFixedFrame());
		
		object = encode(object, clazz, conversionprocessors, processors, mode, traverser, targetcl, ec);
		
		ec.stopObjectFrame();
		
		return object;
	}
	
	// Moved to Traverser.
	/**
	 *  Runs the preprocessors.
	 */
//	protected Object runPreProcessors(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
//		Traverser traverser, Map<Object, Object> traversed, boolean clone, Object context)
//	{
//		List<ITraverseProcessor> preprocessors = ((IEncodingContext) context).getPreprocessors();
//		//System.out.println(preprocessors);
//		if (preprocessors != null)
//		{
//			for (ITraverseProcessor preproc : preprocessors)
//			{
//				if (preproc.isApplicable(object, clazz, clone, null))
//				{
//					object = preproc.process(object, clazz, processors, traverser, traversed, clone, null, context);
//				}
//			}
//		}
//		return object;
//	}
	
	/**
	 *  Test if the codec allows referencing.
	 *  
	 *  @param object The object.
	 *  @param clazz The class.
	 *  @param ec The encoding context.
	 *  @return True, if the codec allows referencing.
	 */
	public boolean canReference(Object object, Class<?> clazz, IEncodingContext ec)
	{
		return true;
	}
	
	/**
	 *  Attempts to encode a reference to an already-encoded object, overwrite for different behavior.
	 *  
	 *  @param object The current object.
	 *  @param clazz The class.
	 *  @param ec The encoding context.
	 *  @return True, if a reference has been encoded, false otherwise.
	 */
//	public int encodeReference(Object object, Class clazz, IEncodingContext ec)
//	{
//		Integer ref = ec.getKnownObjects().get(object);
//		if (ref != null)
//		{
//			//ec.writeString("R");
//			//ec.writeVarInt(ref.intValue());
//			//return true;
//			return 1;
//		}
//		
//		ec.getKnownObjects().put(object, ec.getKnownObjects().size());
//		
//		return false;
//	}
	
	/**
	 *  Encode the object.
	 */
	public abstract Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> preprocessors, List<ITraverseProcessor> processors, MODE mode, Traverser traverser, ClassLoader targetcl, IEncodingContext ec);
	
	/**
	 *  Decodes an object.
	 *  @param clazz The class of the object.
	 *  @param context The decoding context.
	 *  @return The decoded object.
	 */
	public Object decode(Class<?> clazz, IDecodingContext context)
	{
		context.startObjectFrame(isFixedFrame());
		
		Object ret = createObject(clazz, context);
		// Remap class in case there was a search for the correct inner class.
		if(ret != null)
		{
			clazz = ret.getClass();
		}
		recordKnownDecodedObject(ret, context);
		ret = decodeSubObjects(ret, clazz, context);
		
		context.stopObjectFrame();
		
		return ret;
	}
	
	/**
	 *  Creates the object during decoding.
	 *  
	 *  @param clazz The class of the object.
	 *  @param context The decoding context.
	 *  @return The created object.
	 */
	public abstract Object createObject(Class<?> clazz, IDecodingContext context);
	
	/**
	 *  Record object as known during decoding, allows different behavior if needed.
	 */
	public void recordKnownDecodedObject(Object object, IDecodingContext context)
	{
		context.createObjectId(object);
	}
	
	/**
	 *  Decodes and adds sub-objects during decoding.
	 *  
	 *  @param object The instantiated object.
	 *  @param clazz The class of the object.
	 *  @param context The decoding context.
	 *  @return The finished object.
	 */
	public Object decodeSubObjects(Object object, Class<?> clazz, IDecodingContext context)
	{
		return object;
	}
	
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @param targetcl	If not null, the traverser should make sure that the result object is compatible with the class loader,
	 *    e.g. by cloning the object using the class loaded from the target class loader.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Type type, ClassLoader targetcl, Object context)
	{
		Class<?> clazz = SReflect.getClass(type);
		return isApplicable(clazz);
	}
	
	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public abstract boolean isApplicable(Class<?> clazz);
	
	/**
	 *  Declares if the codec should use fixed or variable framing in framing mode.
	 *  Variable framing tends to be more space-efficient especially for small object
	 *  but can be more costly to encode when the object is larger than 127 bytes.
	 *  
	 *  Addendum: Testing seems to suggest that the performance impact is below
	 *  measuring noise, therefore variable-size framing is now enabled for all codecs.
	 *  
	 *  Default is false (variable encoding).
	 *  
	 *  @return True, if fixed size framing should be used.
	 */
	protected boolean isFixedFrame()
	{
		return false;
	}
}
