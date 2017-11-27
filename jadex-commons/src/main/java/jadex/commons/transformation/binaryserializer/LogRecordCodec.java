package jadex.commons.transformation.binaryserializer;

import java.util.List;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import jadex.commons.SReflect;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.commons.transformation.traverser.Traverser;
import jadex.commons.transformation.traverser.Traverser.MODE;

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
	public boolean isApplicable(Class<?> clazz)
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
	public Object createObject(Class<?> clazz, IDecodingContext context)
	{
		Level level = (Level) SBinarySerializer.decodeObject(context);
		String msg = context.readString();
		long millis = context.readSignedVarInt();
		
		LogRecord ret = new LogRecord(level, msg);
		ret.setMillis(millis);
		
		return ret;
	}

	/**
	 *  Encode the object.
	 */
	public Object encode(Object object, Class<?> clazz, List<ITraverseProcessor> preprocessors, List<ITraverseProcessor> processors, MODE mode, Traverser traverser, ClassLoader targetcl, IEncodingContext ec)
	{
		LogRecord rec = (LogRecord) object;
		Level level = rec.getLevel();
		traverser.doTraverse(level, level.getClass(), preprocessors, processors, mode, targetcl, ec);
		ec.writeString(rec.getMessage());
		ec.writeSignedVarInt(rec.getMillis());
		
		return object;
	}
}
