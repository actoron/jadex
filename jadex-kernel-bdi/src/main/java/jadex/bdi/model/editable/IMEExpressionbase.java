package jadex.bdi.model.editable;

import jadex.bdi.model.IMExpression;
import jadex.bdi.model.IMExpressionReference;
import jadex.bdi.model.IMExpressionbase;

/**
 * 
 */
public interface IMEExpressionbase extends IMExpressionbase, IMEElement
{
	/**
	 *  Create a expression with a name.
	 *  @param name	The expression name.
	 *  @param content The expression content.
	 *  @param lang The language.
	 */
	public IMExpression createExpression(String name, String content, String lang);

	/**
	 *  Create an expression reference with a name.
	 *  @param name	The expression name.
	 *  @param ref The reference element name.
	 */
	public IMExpressionReference createExpressionReference(String name, String ref);
}
