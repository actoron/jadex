package jadex.bytecode.invocation;

import jadex.commons.Tuple2;

/**
 *  Interface for generated bean extractors.
 *
 */
public interface IExtractor
{
	/**
	 *  Extract the pre-defined bean properties and return them in the matching arrays.
	 * 
	 *  @param target Target object.
	 *  @return Names and values of the properties.
	 */
	public Tuple2<String[], Object[]> extract(Object target);
}
