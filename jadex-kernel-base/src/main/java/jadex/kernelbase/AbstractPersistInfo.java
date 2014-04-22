package jadex.kernelbase;

import java.util.Map;
import jadex.bridge.modelinfo.IPersistInfo;

/**
 *  Interpreter state information used to persist components.
 *  Kernels must subclass this state to include kernel-specific
 *  state information.
 *
 */
public abstract class AbstractPersistInfo implements IPersistInfo
{
	/** File name of the model. */
	protected String modelfilename;
	
	/** The application configuration. */
	protected String config;
	
	/** The arguments. */
	private Map<String, Object> arguments;
	
	/** The results. */
	protected Map<String, Object> results;
	
	/** The properties. */
	protected Map<String, Object> properties;
	
	/**
	 *  Empty constructor for bean compatibility.
	 */
	public AbstractPersistInfo()
	{
	}
	
	/**
	 *  Creates the state info object.
	 */
	public AbstractPersistInfo(AbstractInterpreter interpreter)
	{
		modelfilename = interpreter.getModel().getFilename();
		config = interpreter.getConfiguration();
		arguments = interpreter.getArguments();
		results = interpreter.getResults();
		properties = interpreter.getProperties();
	}

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
}
