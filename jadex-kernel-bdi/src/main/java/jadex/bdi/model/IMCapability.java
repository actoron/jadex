package jadex.bdi.model;


/**
 *  Interface for capability model.
 */
public interface IMCapability extends IMElement
{
//	/**
//	 *  Get the package.
//	 *  @return The package.
//	 */
//	public String getPackage();
//	
//	/**
//	 *  Get the imports.
//	 *  @return The imports.
//	 */
//	public String[] getImports();
	
	/**
	 *  Test if is abstract.
	 *  @return True, if is abstract.
	 */
	public boolean isAbstract();
	
	/**
	 *  Get the capability references.
	 *  @return The capability references.
	 */
	public IMCapabilityReference[] getCapabilityReferences();
	
	/**
	 *  Get the beliefbase.
	 *  @return The belief base.
	 */
	public IMBeliefbase getBeliefbase();
	
	/**
	 *  Get the beliefbase.
	 *  @return The goalbase.
	 */
	public IMGoalbase getGoalbase();
	
	/**
	 *  Get the planbase.
	 *  @return The planbase.
	 */
	public IMPlanbase getPlanbase();
	
	/**
	 *  Get the eventbase.
	 *  @return The eventbase.
	 */
	public IMEventbase getEventbase();
	
	/**
	 *  Get the expressionbase.
	 *  @return The expressionbase.
	 */
	public IMExpressionbase getExpressionbase();
	
//	/**
//	 *  Get the propertybase.
//	 *  @return The propertybase.
//	 */
//	public IMPropertybase getPropertybase();
	
//	/**
//	 *  Get the services.
//	 *  @return The services.
//	 */
//	public IMExpression[] getServices();
	
	/**
	 *  Get the configurations.
	 *  @return The configurations.
	 */
	public IMConfiguration[] getConfigurations();
	
	/**
	 *  Get the default configuration.
	 *  @return The default configuration.
	 */
	public String getDefaultConfiguration();
}
