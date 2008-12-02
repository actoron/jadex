package jadex.rules.state.io.xml;

import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;

import java.util.List;

/**
 *  Interface for converters used by XML reader
 *  to convert XML string values to actual
 *  OAV object values.
 */
public interface IValueConverter
{
	/**
	 *  Flag to indicate that the converter requires two-pass
	 *  processing, i.e. attribute values are evaluated in 2nd pass.
	 */
	public boolean	isTwoPass();
	
	/**
	 *	Convert the given XML string value to an
	 *  OAV object value.
	 *
	 *  @param state	The current OAV state.
	 *  @param stack	The current stack of OAV objects, created from XML.
	 *  @param attribute	The OAV attribute type.
	 *  @param value	The XML string value.
	 *  @return	The OAV object value.
	 */
	public Object convertValue(IOAVState state, List stack, OAVAttributeType attribute, String value);
}
