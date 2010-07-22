package jadex.bdi.model;


/**
 *  Interface for belief model.
 */
public interface IMBelief extends IMElement 
{
	/**
	 *  Get the clazz.
	 *  @return The clazz. 
	 */
	public Class getClazz();
	
	/**
	 *  Get the clazz.
	 *  @return The clazz. 
	 */
//	public IParsedExpression getFactExpression();
	
	/**
	 *  Get the evaluation mode.
	 *  @return The evaluation mode. 
	 */
	public String getEvaluationMode();
	
	/**
	 *  Test if the element is exported.
	 *  @return True if exported. 
	 */
	public String isExported();
	
	/**
	 *  Test if the belief is used as argument.
	 *  @return True if used as argument. 
	 */
	public boolean isArgument();
	
	/**
	 *  Test if the belief is used as result.
	 *  @return True if used as result. 
	 */
	public boolean isResult();

}
