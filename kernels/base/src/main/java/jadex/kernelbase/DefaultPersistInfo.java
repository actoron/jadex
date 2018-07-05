package jadex.kernelbase;

import java.util.Map;

import jadex.bridge.IInternalAccess;
import jadex.bridge.modelinfo.IPersistInfo;
import jadex.bridge.service.types.cms.IComponentDescription;

/**
 *  Interpreter state information used to persist components.
 *  Kernels must subclass this state to include kernel-specific
 *  state information.
 *
 */
public class DefaultPersistInfo	implements IPersistInfo
{
	//-------- attributes --------
	
	/** File name of the model. */
	protected String modelfilename;
	
	/** The component description. */
	protected IComponentDescription	desc;
	
	/** The application configuration. */
	protected String config;
	
	/** The arguments. */
	protected Map<String, Object> arguments;
	
	/** The results. */
	protected Map<String, Object> results;
	
	/** The properties. */
	protected Map<String, Object> properties;

	/**	The service container. */
	//protected ServiceContainerPersistInfo	servicecontainer;
	
	//-------- constructors --------
	
	/**
	 *  Empty constructor for bean compatibility.
	 */
	public DefaultPersistInfo()
	{
	}
	
	/**
	 *  Creates the state info object.
	 */
	public DefaultPersistInfo(IInternalAccess interpreter)
	{
		modelfilename	= interpreter.getModel().getFilename();
		desc = interpreter.getDescription();
		config = interpreter.getConfiguration();
//		arguments = interpreter.getArguments();
//		results = interpreter.getResults();
//		properties = interpreter.getProperties();
		//servicecontainer	= interpreter.getServiceContainer().getPersistInfo();

	}
	
	//-------- methods --------

	/**
	 *  Gets the model file name.
	 *
	 *  @return The model file name.
	 */
	public String getModelFileName()
	{
		return modelfilename;
	}

	/**
	 *  Sets the model file name.
	 *
	 *  @param modelfilename The model file name to set.
	 */
	public void setModelFileName(String modelfilename)
	{
		this.modelfilename = modelfilename;
	}
	
	/**
	 *  Get the component description.
	 *
	 *  @return The component description
	 */
	public IComponentDescription getComponentDescription()
	{
		return desc;
	}

	/**
	 *  Set the component description.
	 *
	 *  @param desc	The component description
	 */
	public void	setComponentDescription(IComponentDescription desc)
	{
		this.desc	= desc;
	}

	/**
	 *  Gets the config.
	 *
	 *  @return The config.
	 */
	public String getConfig()
	{
		return config;
	}

	/**
	 *  Sets the config.
	 *
	 *  @param config The config to set.
	 */
	public void setConfig(String config)
	{
		this.config = config;
	}

	/**
	 *  Gets the arguments.
	 *
	 *  @return The arguments.
	 */
	public Map<String, Object> getArguments()
	{
		return arguments;
	}

	/**
	 *  Sets the arguments.
	 *
	 *  @param arguments The arguments to set.
	 */
	public void setArguments(Map<String, Object> arguments)
	{
		this.arguments = arguments;
	}

	/**
	 *  Gets the results.
	 *
	 *  @return The results.
	 */
	public Map<String, Object> getResults()
	{
		return results;
	}

	/**
	 *  Sets the results.
	 *
	 *  @param results The results to set.
	 */
	public void setResults(Map<String, Object> results)
	{
		this.results = results;
	}

	/**
	 *  Gets the properties.
	 *
	 *  @return The properties.
	 */
	public Map<String, Object> getProperties()
	{
		return properties;
	}

	/**
	 *  Sets the properties.
	 *
	 *  @param properties The properties to set.
	 */
	public void setProperties(Map<String, Object> properties)
	{
		this.properties = properties;
	}

//	/**
//	 *  Get service state.
//	 *  @return The service container info.
//	 */
//	public ServiceContainerPersistInfo getServiceContainer()
//	{
//		return servicecontainer;
//	}
//	
//	/**
//	 *  Set service state.
//	 *  @param container	The service container info.
//	 */
//	public void	setServiceContainer(ServiceContainerPersistInfo container)
//	{
//		this.servicecontainer	= container;
//	}
}
