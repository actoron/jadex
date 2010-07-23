package jadex.bdi.model;

/**
 *  Interface for expression models.
 */
public interface IMExpression
{
	/**
	 *  Get the expression language.
	 *  @return The language.
	 */
	public String getLanguage();
	
	/**
	 *  Get the expression content.
	 *  @return The content.
	 */
	public Object getContent();
	
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