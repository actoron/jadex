package jadex.bridge;

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
	 *  Get the required services.
	 *  @return The required services.
	 */
	public Class[] getRequiredServices();

	/**
	 *  Get the provided services.
	 *  @return The provided services.
	 */
	public Class[] getProvidedServices();
	
	/**
	 *  Get the master flag.
	 *  @param configname The configname.
	 *  @return The master flag value.
	 */
	public Boolean getMaster(String configname);
	
	/**
	 *  Get the daemon flag.
	 *  @param configname The configname.
	 *  @return The daemon flag value.
	 */
	public Boolean getDaemon(String configname);
	
	/**
	 *  Get the autoshutdown flag.
	 *  @param configname The configname.
	 *  @return The autoshutdown flag value.
	 */
	public Boolean getAutoShutdown(String configname);

}
