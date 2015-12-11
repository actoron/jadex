package jadex.bridge.modelinfo;

import java.util.List;
import java.util.Map;

import jadex.bridge.IErrorReport;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;


/**
 *  This model interface represents the common properties
 *  of all component models. The common properties are
 *  transferable across platforms.
 *  
 *  Kernel-specific properties of locally loaded models
 *  can be accessed by fetching the raw model and casting
 *  it to the corresponding type (e.g. MBpmnModel).
 */
@Reference(remote=false)	// Don't copy locally
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
	 *  Get the imports.
	 *  @return The imports.
	 */
	public String[] getImports();
	
	/**
	 *  Get the imports including the package.
	 *  @return The imports.
	 */
	public String[] getAllImports();
	
	/**
	 *  Get the report.
	 *  @return The report.
	 */
	public IErrorReport getReport();
	
	/**
	 *  Get the configurations.
	 *  @return The configuration.
	 */
	public String[] getConfigurationNames();
	
	/**
	 *  Get the configurations.
	 *  @return The configuration.
	 */
	public ConfigurationInfo[] getConfigurations();
	
	/**
	 *  Get the configurations.
	 *  @return The configuration.
	 */
	public ConfigurationInfo getConfiguration(String name);
	
	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public IArgument[] getArguments();
	
	/**
	 *  Get the argument.
	 *  @return The argument.
	 */
	public IArgument getArgument(String name);
	
	/**
	 *  Get the results.
	 *  @return The results.
	 */
	public IArgument[] getResults();
	
	/**
	 *  Get the results.
	 *  @param name The name.
	 *  @return The results.
	 */
	public IArgument getResult(String name);
	
	/**
	 *  Is the model startable.
	 *  @return True, if startable.
	 */
	public boolean isStartable();
	
	/**
	 *  Get the component type (i.e. kernel).
	 *  @return The component type.
	 */
	public String getType();
	
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
	public Map<String, Object>	getProperties();

	/**
	 *  Get a parsed property.
	 *  Unlike raw properties, which may be parsed or unparsed,
	 *  this method always returns parsed property values.
	 *  @param	name	The property name.  
	 *  @return The property value.
	 */
	public Object	getProperty(String name, ClassLoader cl);
	
	/**
	 *  Get the nf properties.
	 *  @return The nf properties.
	 */
	public List<NFPropertyInfo> getNFProperties();
	
//	/**
//	 *  Return the class loader corresponding to the model.
//	 *  @return The class loader corresponding to the model.
//	 */
//	public ClassLoader getClassLoader();
	
	/**
	 *  Return the resource identifier.
	 *  @return The resource identifier.
	 */
	public IResourceIdentifier getResourceIdentifier();
	
	/**
	 *  Get the required services.
	 *  @return The required services.
	 */
	public RequiredServiceInfo[] getRequiredServices();

	/**
	 *  Get the required service.
	 *  @return The required service.
	 */
	public RequiredServiceInfo getRequiredService(String name);
	
	/**
	 *  Get the provided services.
	 *  @return The provided services.
	 */
	public ProvidedServiceInfo[] getProvidedServices();
	
	/**
	 *  Get the suspend flag.
	 *  @param configname The configname.
	 *  @return The suspend flag value.
	 */
	public Boolean getSuspend(String configname);
	
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

	/**
	 *  Get the synchronous flag.
	 *  @param configname The configname.
	 *  @return The synchronous flag value.
	 */
	public Boolean getSynchronous(String configname);
	
	/**
	 *  Get the persistable flag.
	 *  @param configname The configname.
	 *  @return The persistable flag value.
	 */
	public Boolean getPersistable(String configname);
	
	/**
	 *  Get the keepalive flag.
	 *  @param configname The configname.
	 *  @return The keepalive flag value.
	 */
	public Boolean getKeepalive(String configname);
	
	/**
	 *  Get the monitoring flag.
	 *  @param monitoring The monitoring.
	 *  @return The monitoring flag value.
	 */
	public PublishEventLevel getMonitoring(String configname);
	
	/**
	 *  Get the subcomponent names. 
	 */
	public SubcomponentTypeInfo[] getSubcomponentTypes();
	
	/**
	 *  Get the possible breakpoint places in that model.
	 *  @return The breakpoints.
	 */
	public String[] getBreakpoints();
	
	/**
	 *  Get the kernel-specific model.
	 *  @return The kernel-specific model when loaded locally, null for remote models.
	 */
	public Object	getRawModel();
	
	/**
	 *  Get the features.
	 *  @return The features
	 */
	public IComponentFeatureFactory[] getFeatures();
}
