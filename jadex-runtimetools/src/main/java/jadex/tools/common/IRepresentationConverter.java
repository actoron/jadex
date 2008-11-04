package jadex.tools.common;

import java.util.Map;

/**
 *  Support the conversion of an element (as encoded representation
 *  resp. map) to a string representation, e.g plain text or html.
 */
public interface IRepresentationConverter
{
	/**
	 *  Convert an element representation.
	 *  @param elem The element.
	 */
	public String convert(Map elem);
}
