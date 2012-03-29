package jadex.commons.transformation.binaryserializer;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;

import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 *  Codec for encoding and decoding LogRecord objects.
 *
 */
public class LogRecordCodec extends AbstractCodec
{
	/**
	 *  Tests if the decoder can decode the class.
	 *  @param clazz The class.
	 *  @return True, if the decoder can decode this class.
	 */
	public boolean isApplicable(Class clazz)
	{
		return SReflect.isSupertype(LogRecord.class, clazz);
	}
	
	/**
	 *  Creates the object during decoding.
	 *  
	 *  @param clazz The class of the object.
	 *  @param context The decoding context.
	 *  @return The created object.
	 */
	public Object createObject(Class clazz, DecodingContext context)
	{
		Level level = (Level) BinarySerializer.decodeObject(context);
		String msg = context.readString();
		long millis = context.readSignedVarInt();
		
		LogRecord ret = new LogRecord(level, msg);
		ret.setMillis(millis);
		
		return ret;
	}
	
	/**
	 *  Test if the processor is applicable.
	 *  @param object The object.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Class<?> clazz, boolean clone)
	{
		return isApplicable(clazz);
	}
	
	/**
	 *  Encode the object.
	 */
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
			Traverser traverser, Map<Object, Object> traversed, boolean clone, EncodingContext ec)
	{
		LogRecord rec = (LogRecord) object;
		Level level = rec.getLevel();
		traverser.traverse(level, level.getClass(), traversed, processors, clone, ec);
		ec.writeString(rec.getMessage());
		ec.writeSignedVarInt(rec.getMillis());
		
		return object;
	}
}
