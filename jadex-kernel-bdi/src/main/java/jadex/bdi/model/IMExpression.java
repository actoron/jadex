package jadex.bdi.model;

/**
 *  Interface for expression models.
 */
public interface IMExpression extends IMElement
{
	/**
	 *  Get the expression language.
	 *  @return The language.
	 */
	public String getLanguage();
	
	/**
	 *  Get the expression text.
	 *  @return The text.
	 */
	public String	getText();
	
	/**
	 *  Get the parsed expression.
	 *  @return The parsed expression.
	 */
	public Object getParsedExpression();
	
	/**
	 *  Get the clazz.
	 *  @return The clazz. 
	 */
	public Class getClazz();
	
	/**
	 *  Get the class name.
	 *  @return The class name. 
	 */
	public String getClassname();
	
	/**
	 *  Get the variable name.
	 *  @return The variable name.
	 */
	public String getVariable();
	
}