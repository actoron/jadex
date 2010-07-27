package jadex.bdi.model;

/**
 *  Interface for message event model.
 */
public interface IMMessageEvent extends IMProcessableElement
{
	/**
	 *  Get the parameter direction.
	 *  @return The direction.
	 */
	public String getDirection();
	
	/**
	 *  Get the message type.
	 *  @return The message type.
	 */
	public String getType();
	
	/**
	 *  Get the match expression.
	 *  @return The match expression.
	 */
	public IMExpression getMatchExpression();
}
