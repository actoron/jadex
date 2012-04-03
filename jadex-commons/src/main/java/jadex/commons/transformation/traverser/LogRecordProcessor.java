package jadex.commons.transformation.traverser;

import java.util.List;
import java.util.Map;
import java.util.logging.LogRecord;

public class LogRecordProcessor implements ITraverseProcessor
{
	/**
	 *  Test if the processor is appliable.
	 *  @param object The object.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object, Class<?> clazz, boolean clone)
	{
		return object instanceof LogRecord;
	}
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @return The processed object.
	 */
	public Object process(Object object, Class<?> clazz, List<ITraverseProcessor> processors, 
		Traverser traverser, Map<Object, Object> traversed, boolean clone, Object context)
	{
		Object ret = object;
		if(clone)
		{
			LogRecord oldrec = (LogRecord)object;
			LogRecord newrec = new LogRecord(oldrec.getLevel(), oldrec.getMessage());
			newrec.setMillis(oldrec.getMillis());
		}
		return ret;
	}
}
