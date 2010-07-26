package jadex.bdi.model.editable;

import jadex.bdi.model.IMExpression;

/**
 * 
 */
public interface IMEExpression extends IMExpression, IMEElement
{
	/**
	 *  Get the expression language.
	 *  @param lang The language.
	 */
	public void setLanguage(String lang);
	
	/**
	 *  Set the expression content.
	 *  @param content The content.
	 */
	public void setContent(Object content);
	
	/**
	 *  Set the clazz.
	 *  @param clazz The clazz. 
	 */
	public void setClazz(Class clazz);
	
	/**
	 *  Set the class name.
	 *  @param name The class name. 
	 */
	public void setClassname(String name);
	
	/**
	 *  Set the variable name.
	 *  @param var The variable name.
	 */
	public void setVariable(String var);
}
