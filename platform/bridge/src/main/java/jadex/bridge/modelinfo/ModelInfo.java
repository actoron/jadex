package jadex.bridge.modelinfo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import jadex.bridge.IErrorReport;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.types.monitoring.IMonitoringService.PublishEventLevel;
import jadex.commons.SUtil;
import jadex.commons.transformation.annotations.Exclude;
import jadex.javaparser.SJavaParser;


/**
 *  Public model information that is provided as result from 
 *  component factories when a model is loaded.
 */
public class ModelInfo extends Startable implements IModelInfo
{
	//-------- attributes --------
	
	/** The name. */
	protected String name;
	
	/** The package. */
	protected String packagename;
	
	/** The imports. */
	protected List<String> imports;
	
	/** All imports (cached for speed). */
	protected String[]	allimports;
	
	/** The report. */
	protected IErrorReport report;
	
	/** The configurations. */
	protected List<ConfigurationInfo> configurations;
	
	/** The arguments. */
	protected List<IArgument> arguments;
	
	/** The results. */
	protected List<IArgument> results;
	
	/** Flag if startable. */
	protected boolean startable;
	
	/** The filename. */
	protected String filename;
	
	/** The type. */
	protected String type;
	
	/** The full name (cached for speed). */
	protected String fullname;
	
	/** The properties. */
	protected Map<String, Object> properties;
	
	/** The nf properties. */
	protected List<NFPropertyInfo> nfproperties;
	
	/** The classloader. */
	// only locally available
	protected ClassLoader classloader;
	
	/** The required services. */
	protected Map<String, RequiredServiceInfo> requiredservices;
	
	/** The provided services. */
	protected List<ProvidedServiceInfo> providedservices;
	
	/** The subcomponent types. */
	protected List<SubcomponentTypeInfo> subcomponents;
	
	/** The resource identifier. */
	protected IResourceIdentifier rid;
	
	/** The breakpoints. */
	protected String[] breakpoints;
	
	/** The raw model. */
	protected Object rawmodel;
	
	/** The component features. */
	protected IComponentFeatureFactory[] features;
	
	/** The name hint for instances of this model. */
	protected String namehint;
	
	//-------- constructors --------
	
	/**
	 *  Create a new model info.
	 */
	public ModelInfo()
	{
		this(null, null, null, null, null, null, 
			false, null, null, null, null, null, null, null, null, null, null, null);
	}
	
	/**
	 *  Create a new model info.
	 */
	public ModelInfo(String name, String packagename,
		String description, IErrorReport report,
		IArgument[] arguments, IArgument[] results, boolean startable,
		String filename, Map<String, Object> properties, ClassLoader classloader, 
		RequiredServiceInfo[] requiredservices, ProvidedServiceInfo[] providedservices, 
		ConfigurationInfo[] configurations, SubcomponentTypeInfo[] subcomponents, String[] imports,
		IResourceIdentifier rid, Object rawmodel, IComponentFeatureFactory[] features)
	{
		this.name = name;
		this.packagename = packagename;
		this.description = description;
		this.report = report;//!=null? report: new ErrorReport();
		if(arguments!=null)
			this.arguments = SUtil.arrayToList(arguments);
		if(results!=null)
			this.results = SUtil.arrayToList(results);
		this.startable = startable;
		this.filename = filename;
		this.properties = properties!=null? properties: new HashMap<String, Object>();
		this.classloader = classloader;
		if(providedservices!=null)
			this.providedservices = SUtil.arrayToList(providedservices);
		if(configurations!=null)
			this.configurations = SUtil.arrayToList(configurations);
		if(subcomponents!=null)
			this.subcomponents = SUtil.arrayToList(subcomponents);
		if(imports!=null)
			this.imports = SUtil.arrayToList(imports);
		if(features!=null)
			this.features = features;
		setRequiredServices(requiredservices);
		this.rid = rid;
		this.rawmodel	= rawmodel;
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
		if(fullname==null)
		{
			String pkg = getPackage();
			fullname	= pkg!=null && pkg.length()>0? pkg+"."+getName(): getName();
		}
		return fullname;
	}
	
	/**
	 *  Get the imports.
	 *  @return The imports.
	 */
	public String[] getImports()
	{
		return imports==null? SUtil.EMPTY_STRING_ARRAY: (String[])imports.toArray(new String[imports.size()]);
	}
	
	/**
	 *  Get the imports including the package.
	 *  @return The imports.
	 */
	public String[] getAllImports()
	{
		if(allimports==null)
		{
			String[] ret = getImports();
			if(packagename!=null && packagename.length()>0)
			{
				String[] tmp = new String[ret.length+1];
				if(ret.length>0)
					System.arraycopy(ret, 0, tmp, 1, ret.length);
				tmp[0] = getPackage()+".*";
				ret = tmp;
			}
			allimports	= ret;
		}
		return allimports;
	}
	
	/**
	 *  Add an import statement.
	 */
	public void addImport(String imp)
	{
		allimports	= null;	// reset cached imports in case element is not first in xml.
		
		if(imports==null)
			imports = new ArrayList<String>();
		imports.add(imp);
	}
	
	/**
	 *  Get the report.
	 *  @return The report.
	 */
	public IErrorReport getReport()
	{
		return report;
	}
	
	/**
	 *  Get the configurations.
	 *  @return The configuration.
	 */
	public String[] getConfigurationNames()
	{
//		String[] ret = configurationnames!=null? configurationnames: SUtil.EMPTY_STRING_ARRAY;
		String[] ret = SUtil.EMPTY_STRING_ARRAY;
		
		if(configurations!=null)
		{
			ret = new String[configurations.size()];
			for(int i=0; i<configurations.size(); i++)
			{
				ret[i] = ((ConfigurationInfo)configurations.get(i)).getName();
			}
			return ret;
		}
		
		return ret;
	}
	
	/**
	 *  Get the configurations.
	 *  @return The configuration.
	 */
	public ConfigurationInfo[] getConfigurations()
	{
		return configurations!=null? configurations.toArray(new ConfigurationInfo[configurations.size()]): new ConfigurationInfo[0];
	}
	
	/**
	 *  Get a configuration.
	 */
	public ConfigurationInfo getConfiguration(String name)
	{
		ConfigurationInfo ret = null;
		if(name!=null)
		{
			if(configurations!=null)
			{
				for(int i=0; i<configurations.size(); i++)
				{
					ConfigurationInfo ci = (ConfigurationInfo)configurations.get(i);
					if(name.equals(ci.getName()))
					{
						ret = ci;
						break;
					}
				}
			}
		}
		return ret;
	}	
	
	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 */
	public IArgument[] getArguments()
	{
		return arguments!=null? arguments.toArray(new IArgument[arguments.size()]): new IArgument[0];
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
	public String getType()
	{
		return type;
	}
	
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
	public Map<String, Object>	getProperties()
	{
		return properties;
	}
	
	/**
	 *  Get a parsed property.
	 *  Unlike raw properties, which may be parsed or unparsed,
	 *  this method always returns parsed property values.
	 *  @param	name	The property name.  
	 *  @return The property value or null if property not defined.
	 */
	public Object	getProperty(String name, ClassLoader cl)
	{
		// Todo: caching of parsed values?
		return SJavaParser.getProperty(getProperties(), name, getAllImports(), null, cl);
	}

	/**
	 *  Return the class loader corresponding to the model.
	 *  @return The class loader corresponding to the model.
	 */
	@Exclude
	public ClassLoader getClassLoader()
	{
		return classloader;
	}
	
	/**
	 *  Get the nfproperties.
	 *  @return The nfproperties.
	 */
	public List<NFPropertyInfo> getNFProperties()
	{
		return nfproperties;
	}

	/**
	 *  Set the nfproperties.
	 *  @param nfproperties The nfproperties to set.
	 */
	public void setNFProperties(List<NFPropertyInfo> nfproperties)
	{
		this.nfproperties = nfproperties;
	}
	
	/**
	 *  Add a non functional property.
	 */
	public void addNFProperty(NFPropertyInfo pi)
	{
		if(nfproperties==null)
			nfproperties = new ArrayList<NFPropertyInfo>();
		nfproperties.add(pi);
	}

	/**
	 *  Return the resource identifier.
	 *  @return The resource identifier.
	 */
	public IResourceIdentifier getResourceIdentifier()
	{
		assert rid!=null;
		return rid;
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
	 *  Set the report.
	 *  @param report The report to set.
	 */
	public void setReport(IErrorReport report)
	{
		this.report = report;
	}
	
//	/**
//	 *  Set the configurations.
//	 *  @param configurations The configurations to set.
//	 */
//	public void setConfigurationNames(String[] configurationnames)
//	{
//		this.configurationnames = configurationnames;
//	}
	
	/**
	 *  Set the imports.
	 *  @param imports The imports to set.
	 */
	public void setImports(String[] imports)
	{
		if(imports!=null)
			this.imports	= SUtil.arrayToList(imports);
		else
			this.imports	= null;
	}

	/**
	 *  Set the configurations.
	 *  @param configurations The configurations to set.
	 */
	public void setConfigurations(ConfigurationInfo[] configurations)
	{
		this.configurations = SUtil.arrayToList(configurations);
	}
	
	/**
	 *  Add a configuration.
	 *  @param configuration The configuration.
	 */
	public void addConfiguration(ConfigurationInfo configuration)
	{
		if(configurations==null)
			configurations = new ArrayList<ConfigurationInfo>();
		configurations.add(configuration);
	}

	/**
	 *  Set the arguments.
	 *  @param arguments The arguments to set.
	 */
	public void setArguments(IArgument[] arguments)
	{
		if(arguments!=null)
			this.arguments	= SUtil.arrayToList(arguments);
		else
			this.arguments	= null;
	}
	
	/**
	 *  Add an argument.
	 *  @param argument The argument.
	 */
	public void addArgument(IArgument argument)
	{
		if(arguments==null)
			arguments = new ArrayList<IArgument>();
		arguments.add(argument);
	}

	/**
	 *  Set the results.
	 *  @param results The results to set.
	 */
	public void setResults(IArgument[] results)
	{
		if(results!=null)
			this.results	= SUtil.arrayToList(results);
		else
			this.results	= null;
	}
	
	/**
	 *  Add a result.
	 *  @param result The result.
	 */
	public void addResult(IArgument result)
	{
		if(results==null)
			results = new ArrayList<IArgument>();
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
	 *  Set the component type.
	 *  @param type The component type to set.
	 */
	public void setType(String type)
	{
		this.type = type;
	}

	/**
	 *  Set the properties.
	 *  @param properties The properties to set.
	 */
	public void setProperties(Map<String, Object> properties)
	{
		this.properties = properties;
	}
	
	/**
	 *  Add a property.
	 */
	public void	addProperty(String name, Object value)
	{
		if(properties==null)
			properties = new HashMap<String, Object>();
		properties.put(name, value);
	}
	
	/**
	 *  Add a property.
	 */
	public void	addProperty(UnparsedExpression unexp)
	{
		if(properties==null)
			properties = new HashMap<String, Object>();
		properties.put(unexp.getName(), unexp);
	}
	
	/**
	 *  Set the classloader.
	 *  @param classloader The classloader to set.
	 */
	@Exclude
	public void setClassloader(ClassLoader classloader)
	{
		this.classloader = classloader;
	}
	
	/**
	 *  Get the required services.
	 *  @return The required services.
	 */
	public RequiredServiceInfo[] getServices()
	{
		return requiredservices==null? new RequiredServiceInfo[0]: 
			requiredservices.values().toArray(new RequiredServiceInfo[requiredservices.size()]);
	}

	/**
	 *  Set the resource identifier.
	 *  @param rid The resource identifier to set.
	 */
	public void setResourceIdentifier(IResourceIdentifier rid)
	{
//		System.out.println("rid: "+rid+" "+getName());
		this.rid = rid;
	}

	/**
	 *  Set the required services.
	 *  @param required services The required services to set.
	 */
	public void setRequiredServices(RequiredServiceInfo[] requiredservices)
	{
		if(requiredservices!=null && requiredservices.length>0)
		{
			this.requiredservices = new LinkedHashMap<String, RequiredServiceInfo>();
			for(int i=0; i<requiredservices.length; i++)
			{
				this.requiredservices.put(requiredservices[i].getName(), requiredservices[i]);
			}
		}
	}
	
	/**
	 *  Get the required service.
	 *  @return The required service.
	 */
	public RequiredServiceInfo getService(String name)
	{
		return requiredservices!=null? requiredservices.get(name): null;
	}
	
	/**
	 *  Add a required service.
	 *  @param requiredservice The required service.
	 */
	public void addRequiredService(RequiredServiceInfo requiredservice)
	{
		if(requiredservices==null)
			requiredservices = new LinkedHashMap<String, RequiredServiceInfo>();
		requiredservices.put(requiredservice.getName(), requiredservice);
	}
	
	/**
	 *  Remove a required service.
	 *  @param requiredservice The required service.
	 */
	public void removeRequiredService(RequiredServiceInfo requiredservice)
	{
		if(requiredservices!=null)
		{
			requiredservices.remove(requiredservice.getName());
		}
	}

	/**
	 *  Get the provided services.
	 *  @return The provided services.
	 */
	public ProvidedServiceInfo[] getProvidedServices()
	{
		return providedservices==null? new ProvidedServiceInfo[0]: 
			providedservices.toArray(new ProvidedServiceInfo[providedservices.size()]);
	}

	/**
	 *  Set the provided services.
	 *  @param provided services The provided services to set.
	 */
	public void setProvidedServices(ProvidedServiceInfo[] providedservices)
	{
		this.providedservices = SUtil.arrayToList(providedservices);
	}
	
	/**
	 *  Add a provided service.
	 *  @param providedservice The provided service.
	 */
	public void addProvidedService(ProvidedServiceInfo providedservice)
	{
		if(providedservices==null)
			providedservices = new ArrayList<ProvidedServiceInfo>();
		providedservices.add(providedservice);
	}
	
	/**
	 *  Remove a provided service.
	 *  @param providedservice The provided service.
	 */
	public void removeProvidedService(ProvidedServiceInfo providedservice)
	{
		if (providedservices!=null)
		{
			providedservices.remove(providedservice);
		}
	}
	
//	/**
//	 *  Get the master flag.
//	 *  @param configname The configname.
//	 *  @return The master flag value.
//	 */
//	public Boolean getMaster(String configname)
//	{
//		Boolean ret = null;
//		ConfigurationInfo config = getConfiguration(configname);
//		if(config!=null)
//			ret = config.getMaster();
//		if(ret==null)
//			ret = super.getMaster();
//		return ret;
//		
////		return master==null? null: (Boolean)master.getValue(configname);
//	}
	
//	/**
//	 *  Get the daemon flag.
//	 *  @param configname The configname.
//	 *  @return The daemon flag value.
//	 */
//	public Boolean getDaemon(String configname)
//	{
//		Boolean ret = null;
//		ConfigurationInfo config = getConfiguration(configname);
//		if(config!=null)
//			ret = config.getDaemon();
//		if(ret==null)
//			ret = super.getDaemon();
//		return ret;
//		
////		return daemon==null? null: (Boolean)daemon.getValue(configname);
//	}
	
//	/**
//	 *  Get the autoshutdown flag.
//	 *  @param configname The configname.
//	 *  @return The autoshutdown flag value.
//	 */
//	public Boolean getAutoShutdown(String configname)
//	{
//		Boolean ret = null;
//		ConfigurationInfo config = getConfiguration(configname);
//		if(config!=null)
//			ret = config.getAutoShutdown();
//		if(ret==null)
//			ret = super.getAutoShutdown();
//		return ret;
////		return autoshutdown==null? null: (Boolean)autoshutdown.getValue(configname);
//	}
	
	/**
	 *  Get the synchronous flag.
	 *  @param synchronous The synchronous.
	 *  @return The synchronous flag value.
	 */
	public Boolean getSynchronous(String configname)
	{
		Boolean ret = null;
		ConfigurationInfo config = getConfiguration(configname);
		if(config!=null)
			ret = config.getSynchronous();
		if(ret==null)
			ret = super.getSynchronous();
		return ret;
	}
	
//	/**
//	 *  Get the persistable flag.
//	 *  @param persistable The persistable.
//	 *  @return The persistable flag value.
//	 */
//	public Boolean getPersistable(String configname)
//	{
//		Boolean ret = null;
//		ConfigurationInfo config = getConfiguration(configname);
//		if(config!=null)
//			ret = config.getPersistable();
//		if(ret==null)
//			ret = super.getPersistable();
//		return ret;		
//	}

	/**
	 *  Get the suspend flag.
	 *  @param configname The configname.
	 *  @return The suspend flag value.
	 */
	public Boolean getSuspend(String configname)
	{
		Boolean ret = null;
		ConfigurationInfo config = getConfiguration(configname);
		if(config!=null)
			ret = config.getSuspend();
		if(ret==null)
			ret = super.getSuspend();
		return ret;
		
//		return suspend==null? null: (Boolean)suspend.getValue(configname);
	}
	
	/**
	 *  Get the keepalive flag.
	 *  @param configname The configname.
	 *  @return The keepalive flag value.
	 */
	public Boolean getKeepalive(String configname)
	{
		Boolean ret = null;
		ConfigurationInfo config = getConfiguration(configname);
		if(config!=null)
		{
			ret = config.getKeepalive();
		}
		if(ret==null)
		{
			ret = super.getKeepalive();
		}
		
		// Auto terminate on default, when there are initial steps.
		if(ret==null && config!=null && config.getInitialSteps().length>0)
		{
			ret	= Boolean.FALSE;
		}
		
		return ret;
	}

//	/**
//	 *  Get the monitoring flag.
//	 *  @param configname The configname.
//	 *  @return The monitoring flag value.
//	 */
//	public Boolean getMonitoring(String configname)
//	{
//		Boolean ret = null;
//		ConfigurationInfo config = getConfiguration(configname);
//		if(config!=null)
//			ret = config.getMonitoring();
//		if(ret==null)
//			ret = super.getMonitoring();
//		return ret;
//	}
	
	/**
	 *  Get the monitoring flag.
	 *  @param configname The configname.
	 *  @return The monitoring flag value.
	 */
	public PublishEventLevel getMonitoring(String configname)
	{
		PublishEventLevel ret = null;
		ConfigurationInfo config = getConfiguration(configname);
		if(config!=null)
			ret = config.getMonitoring();
		if(ret==null)
			ret = super.getMonitoring();
		return ret;
	}
	
	/**
	 *  Get the subcomponent names. 
	 */
	public SubcomponentTypeInfo[] getSubcomponentTypes()
	{
		return subcomponents!=null? subcomponents.toArray(new SubcomponentTypeInfo[subcomponents.size()]): new SubcomponentTypeInfo[0];
	}
	
	/**
	 *  Set the subcomponent types.
	 */
	public void setSubcomponentTypes(SubcomponentTypeInfo[] subcomponents)
	{
		this.subcomponents = SUtil.arrayToList(subcomponents);
	}
	
	/**
	 *  Add a subcomponent type.
	 *  @param subcomponent The subcomponent type.
	 */
	public void addSubcomponentType(SubcomponentTypeInfo subcomponent)
	{
		if(subcomponents==null)
			subcomponents = new ArrayList<SubcomponentTypeInfo>();
		subcomponents.add(subcomponent);
	}
	
	/**
	 *  Get the possible breakpoint places in that model.
	 *  @return The breakpoints.
	 */
	public String[] getBreakpoints()
	{
		return breakpoints;
	}
	
	/**
	 *  Set the breakpoints.
	 *  @param breakpoints The breakpoints to set.
	 */
	public void setBreakpoints(String[] breakpoints)
	{
		this.breakpoints = breakpoints;
	}

	/**
	 *  Check if the specified name matches the file name.
	 */
	public boolean checkName()
	{
		boolean	check	= name!=null;
		if(check && filename!=null)
		{
			String	test	= filename;
			int index	= Math.max(test.lastIndexOf("\\"), test.lastIndexOf("/"));
			if(index>0)
			{
				test	= test.substring(index+1);
			}
			check	= test.startsWith(name);
		}
		return check;
	}


	/**
	 *  Check if the specified package matches the file name.
	 */
	public boolean checkPackage()
	{
		boolean	check	= true;
		if(filename!=null && packagename!=null)
		{
			String	test	= filename;
			int index	= Math.max(filename.lastIndexOf("\\"), filename.lastIndexOf("/"));
			if(index==-1)
			{
				check	= "".equals(packagename);
			}
			else
			{
				test	= test.substring(0, index);
				String	testpackage	= packagename;
				while(check && test!=null && testpackage!=null)
				{
					index	= Math.max(test.lastIndexOf("\\"), test.lastIndexOf("/"));
					String	test1	= index==-1 ? test : test.substring(index+1); 
					test	= index!=-1 ? test.substring(0, index) : null;

					int	index2	= testpackage.lastIndexOf(".");
					String	test2	= index2==-1 ? testpackage : testpackage.substring(index2+1);
					testpackage	= index2!=-1 ? testpackage.substring(0, index2) : null;
					
					check	= SUtil.equals(test1, test2) && (test!=null || testpackage==null);
				}
			}
		}
		return check;
	}
	
	/**
	 *  Get the kernel-specific model.
	 *  @return The kernel-specific model when loaded locally, null for remote models.
	 */
	public Object	getRawModel()
	{
		return rawmodel;
	}

	/**
	 *  Set the kernel-specific model.
	 *  @param rawmodel The kernel-specific model when loaded locally, null for remote models.
	 */
	// Not bean-compliant to avoid raw model being transferred.
	public void	internalSetRawModel(Object rawmodel)
	{
		this.rawmodel	= rawmodel;
	}

	/**
	 *  Get the features.
	 *  @return The features
	 */
	public IComponentFeatureFactory[] getFeatures()
	{
		return features!=null? features: new IComponentFeatureFactory[0];
	}

	/**
	 *  The features to set.
	 *  @param features The features to set
	 */
	public void setFeatures(IComponentFeatureFactory[] features)
	{
		this.features = features;
	}

	/**
	 *  Get the namehint.
	 *  @return the namehint
	 */
	public String getNameHint()
	{
		return namehint;
	}

	/**
	 *  Set the namehint.
	 *  @param namehint The namehint to set
	 */
	public void setNameHint(String namehint)
	{
		this.namehint = namehint;
	}
}
