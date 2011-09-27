package jadex.commons;

import java.util.List;

/**
 *  Processor for intercepting 
 */
public interface ICloneProcessor
{
	/**
	 *  Test if the processor is appliable.
	 *  @param object The object.
	 *  @return True, if is applicable. 
	 */
	public boolean isApplicable(Object object);
	
	/**
	 *  Process an object.
	 *  @param object The object.
	 *  @return The processed object.
	 */
	public Object process(Object object, List processors);
}
