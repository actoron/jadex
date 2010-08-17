package jadex.bridge;

import jadex.commons.SUtil;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Public model information that is provided as result from 
 *  component factories when a model is loaded.
 */
public class ModelInfo implements IModelInfo
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;
	
	/** The package. */
	protected String packagename;
	
	/** The description. */
	protected String description;
	
	/** The report. */
	protected IReport report;
	
	/** The configurations. */
	protected String[] configurations;
	
	/** The arguments. */
	protected List arguments;
	
	/** The results. */
	protected List results;
	
	/** Flag if startable. */
	protected boolean startable;
	
	/** The filename. */
	protected String filename;
	
	/** The properties. */
	protected Map properties;
	
	/** The classloader. */
	protected ClassLoader classloader;
	
	//-------- constructors --------
	
	/**
	 *  Create a new model info.
	 */
	public ModelInfo()
	{
		this(null, null, null, null, null, null, null, false, null, null, null);
	}
	
	/**
	 *  Create a new model info.
	 */
	public ModelInfo(String name, String packagename,
			String description, IReport report, String[] configurations,
			IArgument[] arguments, IArgument[] results, boolean startable,
			String filename, Map properties, ClassLoader classloader)
	{
		this.name = name;
		this.packagename = packagename;
		this.description = description;
		this.report = report!=null? report: new BasicReport();
		this.configurations = configurations;
		this.arguments = arguments!=null? SUtil.arrayToList(arguments): null;
		this.results = results!=null? SUtil.arrayToList(results): null;
		this.startable = startable;
		this.filename = filename;
		this.properties = properties!=null? properties: new HashMap();
		this.classloader = classloader;
	}

	//-------- methods --------
	
	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return name;
	}
	
	/**
	 *  Get the package name.
	 *  @return The package name.
	 */
	public String getPackage()
	{
		return packagename;
	}
	
	/**
	 *  Get the full model name (package.name)
	 *  @return The full name.
	 */
	public String getFullName()
	{
		String pkg = getPackage();
		return pkg!=null && pkg.length()>0? pkg+"."+getName(): getName();
	}
	
	/**
	 *  Get the model description.
	 *  @return The model description.
	 */
	public String getDescription()
	{
		return description;
	}
	
	/**
	 *  Get the report.
	 *  @return The report.
	 */
	public IReport getReport()
	{
		return report;
	}
	
	/**
	 *  Get the configurations.
	 *  @return The configuration.
	 */
	public String[] getConfigurations()
	{
		return configurations!=null? configurations: SUtil.EMPTY_STRING_ARRAY;
	}
	
	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public IArgument[] getArguments()
	{
		return arguments!=null? (IArgument[])arguments.toArray(new IArgument[arguments.size()]): new IArgument[0];
	}
	
	/**
	 *  Get an argument per name.
	 *  @param name The name.
	 *  @return The argument.
	 */
	public IArgument getArgument(String name)
	{
		Argument ret = null;
		if(arguments!=null)
		{
			for(int i=0; i<arguments.size() && ret==null; i++)
			{
				Argument tmp = (Argument)arguments.get(i);
				if(tmp.getName().equals(name))
					ret = tmp;
			}
		}
		return ret;
	}
	
	/**
	 *  Get the results.
	 *  @return The results.
	 */
	public IArgument[] getResults()
	{
		return results!=null? (IArgument[])results.toArray(new IArgument[results.size()]): new IArgument[0];
	}
	
	/**
	 *  Get a result per name.
	 *  @param name The name.
	 *  @return The result.
	 */
	public IArgument getResult(String name)
	{
		Argument ret = null;
		if(results!=null)
		{
			for(int i=0; i<results.size() && ret==null; i++)
			{
				Argument tmp = (Argument)results.get(i);
				if(tmp.getName().equals(name))
					ret = tmp;
			}
		}
		return ret;
	}
	
	/**
	 *  Is the model startable.
	 *  @return True, if startable.
	 */
	public boolean isStartable()
	{
		return startable;
	}
	
	/**
	 *  Get the model type.
	 *  @return The model type (kernel specific).
	 */
//	public String getType();
	
	/**
	 *  Get the filename.
	 *  @return The filename.
	 */
	public String getFilename()
	{
		return filename;
	}

	/**
	 *  Get the properties.
	 *  Arbitrary properties that can e.g. be used to
	 *  define model-specific settings to configure tools. 
	 *  @return The properties.
	 */
	public Map	getProperties()
	{
		return properties;
	}

	/**
	 *  Return the class loader corresponding to the model.
	 *  @return The class loader corresponding to the model.
	 */
	public ClassLoader getClassLoader()
	{
		return classloader;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Set the packagename.
	 *  @param packagename The packagename to set.
	 */
	public void setPackage(String packagename)
	{
		this.packagename = packagename;
	}

	/**
	 *  Set the description.
	 *  @param description The description to set.
	 */
	public void setDescription(String description)
	{
		this.description = description;
	}

	/**
	 *  Set the report.
	 *  @param report The report to set.
	 */
	public void setReport(IReport report)
	{
		this.report = report;
	}

	/**
	 *  Set the configurations.
	 *  @param configurations The configurations to set.
	 */
	public void setConfigurations(String[] configurations)
	{
		this.configurations = configurations;
	}

	/**
	 *  Set the arguments.
	 *  @param arguments The arguments to set.
	 */
	public void setArguments(IArgument[] arguments)
	{
		this.arguments = arguments!=null? SUtil.arrayToList(arguments): null;
	}
	
	/**
	 *  Add an argument.
	 *  @param argument The argument.
	 */
	public void addArgument(IArgument argument)
	{
		if(arguments==null)
			arguments = new ArrayList();
		arguments.add(argument);
	}

	/**
	 *  Set the results.
	 *  @param results The results to set.
	 */
	public void setResults(IArgument[] results)
	{
		this.results = results!=null? SUtil.arrayToList(results): null;
	}
	
	/**
	 *  Add a result.
	 *  @param result The result.
	 */
	public void addResult(IArgument result)
	{
		if(results==null)
			results = new ArrayList();
		results.add(result);
	}

	/**
	 *  Set the startable.
	 *  @param startable The startable to set.
	 */
	public void setStartable(boolean startable)
	{
		this.startable = startable;
	}

	/**
	 *  Set the filename.
	 *  @param filename The filename to set.
	 */
	public void setFilename(String filename)
	{
		this.filename = filename;
	}

	/**
	 *  Set the properties.
	 *  @param properties The properties to set.
	 */
	public void setProperties(Map properties)
	{
		this.properties = properties;
	}
	
	/**
	 *  Add a property.
	 */
	public void	addProperty(String name, Object value)
	{
		if(properties==null)
			properties = new HashMap();
		properties.put(name, value);
	}

	// Exclude from transfer?!
	/**
	 *  Set the classloader.
	 *  @param classloader The classloader to set.
	 */
	public void setClassloader(ClassLoader classloader)
	{
		this.classloader = classloader;
	}
}
