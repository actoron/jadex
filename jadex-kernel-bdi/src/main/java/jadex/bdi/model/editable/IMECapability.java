package jadex.bdi.model.editable;

import jadex.bdi.model.IMCapability;
import jadex.bridge.modelinfo.ModelInfo;

/**
 *  Interface for editable version of capability.
 */
public interface IMECapability extends IMCapability, IMEElement
{
//	/**
//	 *  Set the package.
//	 *  @param The package.
//	 */
//	public void setPackage(String name);
//	
//	/**
//	 *  Set the imports.
//	 *  @param The imports.
//	 */
//	public void setImports(String[] imports);
	
	/**
	 *  Set if is abstract.
	 *  @param abs True, if is abstract.
	 */
	public void setAbstract(boolean abs);
	
	/**
	 *  Get the capability references.
	 */
	public void createCapabilityReference(String name, String file);
	
	/**
	 *  Create or get the beliefbase.
	 *  @return The belief base.
	 */
	public IMEBeliefbase createBeliefbase();
	
	/**
	 *  Create or get the beliefbase.
	 *  @return The goalbase.
	 */
	public IMEGoalbase createGoalbase();
	
	/**
	 *  Create or get the planbase.
	 *  @return The planbase.
	 */
	public IMEPlanbase createPlanbase();
	
	/**
	 *  Create or get the eventbase.
	 *  @return The eventbase.
	 */
	public IMEEventbase createEventbase();
	
	/**
	 *  Get the expressionbase.
	 *  @return The expressionbase.
	 */
	public IMEExpressionbase createExpressionbase();
	
//	/**
//	 *  Get the propertybase.
//	 *  @return The propertybase.
//	 */
//	public IMEPropertybase createPropertybase();
	
//	/**
//	 *  Add a service.
//	 *  @param name	The service name.
//	 *  @param clazz	The service type (for lookups).
//	 *  @param expression	The creation expression for the service object.
//	 *  @param language	The expression language (or null for default java-like language).
//	 *  @return The service expression object.
//	 */
//	public IMEExpression createService(String name, Class clazz, String expression, String language);
	
	/**
	 *  Create a configuration.
	 *  @param The name.
	 *  @return The configuration.
	 */
	public IMEConfiguration createConfiguration(String name);
	
	/**
	 *  Get the model info for editing component level settings.
	 */
	public ModelInfo	getModelInfo();
}
