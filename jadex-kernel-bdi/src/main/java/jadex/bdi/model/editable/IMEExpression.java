package jadex.bdi.model.editable;

import jadex.bdi.model.IMExpression;

/**
 * 
 */
public interface IMEExpression extends IMExpression, IMEElement
{
	/**
	 *  Set the expression.
	 *  @param expression The expression.
	 *  @param language The language (null for default java-like language).
	 */
	public void setExpression(String expression, String language);
	
	/**
	 *  Set the expression content (i.e. parsed expression or condition).
	 *  @param content The content.
	 */
	public void setContent(Object content);
	
	/**
	 *  Set the clazz.
	 *  @param clazz The clazz. 
	 */
	public void setClazz(Class clazz);
	
//	/**
//	 *  Set the class name.
//	 *  @param name The class name. 
//	 */
//	public void setClassname(String name);
	
	/**
	 *  Set the variable name.
	 *  @param var The variable name.
	 */
	public void setVariable(String var);
}
