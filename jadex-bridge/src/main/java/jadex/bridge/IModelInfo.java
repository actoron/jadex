package jadex.bridge;

import jadex.commons.SUtil;

import java.util.Map;


/**
 *  This model interface to be used (invoked) by tools and adapters.
 *  Can represent an application or an component (also capability).
 *  Applications can be loaded by the application factory.
 *  @link{IApplicationFactory}
 *  Component types can be loaded by the kernel's component factory
 *  @link{IComponentFactory}. 
 */
public interface IModelInfo
{
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName();
	
	/**
	 *  Get the package name.
	 *  @return The package name.
	 */
	public String getPackage();
	
	/**
	 *  Get the full model name (package.name)
	 *  @return The full name.
	 */
	public String getFullName();
	
	/**
	 *  Get the model description.
	 *  @return The model description.
	 */
	public String getDescription();
	
	/**
	 *  Get the report.
	 *  @return The report.
	 */
	public IErrorReport getReport();
	
	/**
	 *  Get the configurations.
	 *  @return The configuration.
	 */
	public String[] getConfigurations();
	
	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public IArgument[] getArguments();
	
	/**
	 *  Get the results.
	 *  @return The results.
	 */
	public IArgument[] getResults();
	
	/**
	 *  Is the model startable.
	 *  @return True, if startable.
	 */
	public boolean isStartable();
	
	/**
	 *  Get the model type.
	 *  @return The model type (kernel specific).
	 */
//	public String getType();
	
	/**
	 *  Get the filename.
	 *  @return The filename.
	 */
	public String getFilename();

	/**
	 *  Get the properties.
	 *  Arbitrary properties that can e.g. be used to
	 *  define model-specific settings to configure tools. 
	 *  @return The properties.
	 */
	public Map	getProperties();

	/**
	 *  Return the class loader corresponding to the model.
	 *  @return The class loader corresponding to the model.
	 */
	public ClassLoader getClassLoader();
	
	/**
	 *  Get the usedservices.
	 *  @return The usedservices.
	 */
	public Class[] getUsedServices();

	/**
	 *  Get the offeredservices.
	 *  @return The offeredservices.
	 */
	public Class[] getOfferedServices();
}
