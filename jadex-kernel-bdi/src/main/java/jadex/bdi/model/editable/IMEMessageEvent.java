package jadex.bdi.model.editable;

import jadex.bdi.model.IMMessageEvent;

/**
 * 
 */
public interface IMEMessageEvent extends IMMessageEvent, IMEProcessableElement
{
	/**
	 *  Set the parameter direction.
	 *  @param dir The direction.
	 */
	public void setDirection(String dir);
	
	/**
	 *  Set the message type.
	 *  @param type The message type name.
	 */
	public void setType(String type);
	
	/**
	 *  Create a match expression.
	 *  @param content The content.
	 *  @param lang The language.
	 */
	public IMEExpression createMatchExpression(String content, String lang);
}
