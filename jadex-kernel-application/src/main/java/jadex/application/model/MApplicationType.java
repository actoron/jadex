package jadex.application.model;

import jadex.bridge.AbstractErrorReportBuilder;
import jadex.bridge.modelinfo.Argument;
import jadex.bridge.modelinfo.ModelInfo;
import jadex.bridge.modelinfo.ModelValueProvider;
import jadex.bridge.service.ProvidedServiceImplementation;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.ICacheableModel;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.IFuture;
import jadex.javaparser.IParsedExpression;
import jadex.xml.StackElement;

import java.util.ArrayList;
import java.util.List;

import javax.xml.namespace.QName;

/**
 *  Application type representation.
 */
public class MApplicationType extends MStartable implements ICacheableModel
{
	//-------- attributes --------
	
	/** The imports. */
	protected List imports;
	
	/** The list of contained space types. */
	protected List spacetypes;
	
	/** The list of contained component types. */
	protected List componenttypes;
	
	/** The list of contained application descriptions. */
	protected List applications;
		
	/** The service container. */
	protected MExpressionType container;
	
//	/** The name. */
//	protected String name;
//	
//	/** The package. */
//	protected String packagename;
//	
//	/** The description. */
//	protected String description;
//	
//	/** The arguments. */
//	protected List arguments;
//	
//	/** The results. */
//	protected List results;
//	
//	/** The properties. */
//	protected List properties;
	
	/** The last modified date. */
	protected long lastmodified;
	
	/** The last check date. */
	protected long lastchecked;

	/** The property list. */
	protected List propertylist;
	
	/** The model info. */
	protected ModelInfo modelinfo;
	
	/** The provided services. */
	protected List providedservices;

	/** The required services. */
	protected List requiredservices;

	
	//-------- constructors --------
	
	/**
	 *  Create a new application type.
	 */
	public MApplicationType()
	{
		this.imports = new ArrayList();
		this.spacetypes = new ArrayList();
		this.componenttypes = new ArrayList();
		this.applications = new ArrayList();
//		this.arguments = new ArrayList();
//		this.results = new ArrayList();
		this.providedservices = new ArrayList();
		this.requiredservices = new ArrayList();
//		this.properties = new ArrayList();
		this.modelinfo = new ModelInfo();
	}
	
	/**
	 *  Init the model info.
	 */
	public void initModelInfo(MultiCollection entries)
	{
		// todo: breakpoints?!
//		List names = new ArrayList();
//		modelinfo.addProperty("debugger.breakpoints", names);

		List apps = getMApplicationInstances();
		String[] configs = new String[apps.size()];
		for(int i=0; i<configs.length; i++)
		{
			configs[i] = ((MApplicationInstance)apps.get(i)).getName();
		}
		modelinfo.setConfigurations(configs);

		// Init flags.
		ModelValueProvider suspend = new ModelValueProvider();
		ModelValueProvider master = new ModelValueProvider();
		ModelValueProvider daemon = new ModelValueProvider();
		ModelValueProvider autoshutdown = new ModelValueProvider();
		modelinfo.setSuspend(suspend);
		modelinfo.setMaster(master);
		modelinfo.setDaemon(daemon);
		modelinfo.setAutoShutdown(autoshutdown);
		if(getSuspend()!=null)
			suspend.setValue(getSuspend());
		if(getMaster()!=null)
			master.setValue(getMaster());
		if(getDaemon()!=null)
			daemon.setValue(getDaemon());
		if(getAutoShutdown()!=null)
			autoshutdown.setValue(getAutoShutdown());
		
		for(int i=0; i<apps.size(); i++)
		{
			MApplicationInstance mapp = (MApplicationInstance)apps.get(i);
			List instargs = mapp.getArguments();
			for(int j=0; j<instargs.size(); j++)
			{
				MExpressionType arg = (MExpressionType)instargs.get(j);
				try
				{
					Argument rarg = (Argument)getModelInfo().getArgument(arg.getName());
					
					Object val = arg.getParsedValue().getValue(null);
					rarg.setDefaultValue(mapp.getName(), val);
				}
				catch(Exception e)
				{
					Tuple	se	= new Tuple(new Object[]{
						new StackElement(new QName("applicationtype"), this, null),
						new StackElement(new QName("applications"), null, null),
						new StackElement(new QName("application"), mapp, null),
						new StackElement(new QName("arguments"), null, null),
						new StackElement(new QName("argument"), arg, null)});
					entries.put(se, e.toString()+": "+arg.getValue());
				}
			}
			
			Object val = mapp.getSuspend();
			if(val!=null)
			{
				suspend.setValue(mapp.getName(), val);
//				System.out.println("suspend: "+val+" "+mapp.getName());
			}
			val = mapp.getMaster();
			if(val!=null)
			{
				master.setValue(mapp.getName(), val);
//				System.out.println("master: "+val+" "+mapp.getName());
			}
			val = mapp.getDaemon();
			if(val!=null)
			{
				daemon.setValue(mapp.getName(), val);
//				System.out.println("daemon: "+val+" "+mapp.getName());
			}
			val = mapp.getAutoShutdown();
			if(val!=null)
			{
				autoshutdown.setValue(mapp.getName(), val);
//				System.out.println("autoshutdown: "+val+" "+mapp.getName());
			}
		}
		
		if(propertylist!=null)
		{
			for(int i=0; i<propertylist.size(); i++)
			{
				MExpressionType	mexp	= (MExpressionType)propertylist.get(i);
				Class	clazz	= mexp.getClazz();
				// Ignore future properties, which are evaluated at component instance startup time.
				if(clazz==null || !SReflect.isSupertype(IFuture.class, clazz))
				{
					IParsedExpression	pex = mexp.getParsedValue();
					try
					{
						Object	value	= pex.getValue(null);
						modelinfo.addProperty(mexp.getName(), value);
					}
					catch(Exception e)
					{
						Tuple	se	= new Tuple(new Object[]{
							new StackElement(new QName("applicationtype"), this, null),
							new StackElement(new QName("properties"), null, null),
							new StackElement(new QName("property"), mexp, null)});
						entries.put(se, e.toString()+": "+pex.getExpressionText());
					}
				}
			}
		}
		
		modelinfo.setStartable(true);
		
		List reqs = getRequiredServices();
		if(reqs!=null && reqs.size()>0)
		{
			RequiredServiceInfo[] tmp = new RequiredServiceInfo[reqs.size()];
			for(int i=0; i<reqs.size(); i++)
			{
				MRequiredServiceType ser = (MRequiredServiceType)reqs.get(i);
				tmp[i] = new RequiredServiceInfo(ser.getName(), ser.getClazz(),
					ser.isMultiple(), ser.getBinding());
			}
			
			modelinfo.setRequiredServices(tmp);
		}
		
		List provs = getProvidedServices();
		if(provs!=null && provs.size()>0)
		{
			ProvidedServiceInfo[] tmp = new ProvidedServiceInfo[provs.size()];
			for(int i=0; i<provs.size(); i++)
			{
				MProvidedServiceType ser = (MProvidedServiceType)provs.get(i);
				Class type = ser.getClazz()!=null? ser.getClazz(): 
					tmp[i]==null && ser.getParsedValue()!=null? 
					ser.getParsedValue().getStaticType(): null;
				tmp[i] = new ProvidedServiceInfo(type, new ProvidedServiceImplementation(ser.getImplementation(), ser.getValue(), ser.isDirect(), null));
			}
			
			modelinfo.setProvidedServices(tmp);
		}
				
		// Build error report.
		modelinfo.setReport(new AbstractErrorReportBuilder(modelinfo.getName(), modelinfo.getFilename(),
			new String[]{"Space", "Component", "Application"}, entries, null)
		{
			public boolean isInCategory(Object obj, String category)
			{
				return "Space".equals(category) && obj instanceof MSpaceType
					|| "Component".equals(category) && obj instanceof MComponentType
					|| "Application".equals(category) && obj instanceof MApplicationInstance;
			}
			
			public Object getPathElementObject(Object element)
			{
				return ((StackElement)element).getObject();
			}
			
			public String getObjectName(Object obj)
			{
				String	name	= null;
				String	type	= obj!=null ? SReflect.getInnerClassName(obj.getClass()) : null;
				if(obj instanceof MSpaceType)
				{
					name	= ((MSpaceType)obj).getName();
				}
				else if(obj instanceof MComponentType)
				{
					name	= ((MComponentType)obj).getName();
				}
				else if(obj instanceof MApplicationInstance)
				{
					name	= ((MApplicationInstance)obj).getName();
					type	= "Application";
				}
				else if(obj instanceof MExpressionType)
				{
					IParsedExpression	pexp	= ((MExpressionType)obj).getParsedValue();
					String	exp	= pexp!=null ? pexp.getExpressionText() : null;
					name	= exp!=null ? ""+exp : null;
				}
				
				if(type!=null && type.startsWith("M") && type.endsWith("Type"))
				{
					type	= type.substring(1, type.length()-4);
				}
				
				return type!=null ? name!=null ? type+" "+name : type : name!=null ? name : "";
			}
		}.buildErrorReport());
	}
	
	//-------- methods --------
	
	/**
	 *  Get the name.
	 *  @return The name.
	 * /
	public String getName()
	{
		return this.name;
	}*/

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		modelinfo.setName(name);
	}
	
	/**
	 *  Get the package name.
	 *  @return The package name.
	 * /
	public String getPackage()
	{
		return this.packagename;
	}*/

	/**
	 *  Set the package.
	 *  @param packagename The package name to set.
	 */
	public void setPackage(String packagename)
	{
		modelinfo.setPackage(packagename);
	}
	
	/**
	 *  Get the model description.
	 *  @return The model description.
	 * /
	public String getDescription()
	{
		return description;
	}*/
	
	/**
	 *  Set the description.
	 *  @param desc The description.
	 */
	public void setDescription(String description)
	{
		modelinfo.setDescription(description);
	}
	
	/**
	 *  Set the filename.
	 *  @param filename The filename.
	 */
	public void setFilename(String filename)
	{
		modelinfo.setFilename(filename);
	}
	
	/**
	 *  Get the propertylist.
	 *  @return the propertylist.
	 */
	public List getPropertyList()
	{
		return propertylist;
	}

	/**
	 *  Get the lastmodified.
	 *  @return the lastmodified.
	 */
	public long getLastModified()
	{
		return lastmodified;
	}

	/**
	 *  Set the lastmodified.
	 *  @param lastmodified The lastmodified to set.
	 */
	public void setLastModified(long lastmodified)
	{
		this.lastmodified = lastmodified;
	}

	/**
	 *  Get the lastchecked.
	 *  @return the lastchecked.
	 */
	public long getLastChecked()
	{
		return lastchecked;
	}

	/**
	 *  Set the lastchecked.
	 *  @param lastchecked The lastchecked to set.
	 */
	public void setLastChecked(long lastchecked)
	{
		this.lastchecked = lastchecked;
	}

	/**
	 *  Add an import.
	 *  @param import The import.
	 */
	public void addImport(String importstr)
	{
		this.imports.add(importstr);
	}
	
	/**
	 *  Add an component type.
	 *  @param componenttype The component type.
	 */
	public void addMComponentType(MComponentType componenttype)
	{
		this.componenttypes.add(componenttype);
	}
	
	/**
	 *  Add a space type.
	 *  @param componenttype The space type.
	 */
	public void addMSpaceType(MSpaceType spacetype)
	{
		this.spacetypes.add(spacetype);
	}

	/**
	 *  Add an application.
	 *  @param application The application.
	 */
	public void addMApplicationInstance(MApplicationInstance application)
	{
		this.applications.add(application);
	}

	/**
	 *  Add an argument.
	 *  @param argument The argument.
	 */
	public void addArgument(Argument argument)
	{
		modelinfo.addArgument(argument);
	}
	
	/**
	 *  Add a result.
	 *  @param result The result.
	 */
	public void addResult(Argument result)
	{
		modelinfo.addResult(result);
	}
	
	/**
	 *  Add a service.
	 *  @param service The service.
	 */
	public void addMProvidedServiceType(MProvidedServiceType service)
	{
		this.providedservices.add(service);
	}
	
	/**
	 *  Add a required service.
	 *  @param service The required service.
	 */
	public void addMRequiredServiceType(MRequiredServiceType service)
	{
		this.requiredservices.add(service);
	}
	
	/**
	 *  Add a property.
	 *  @param property The property.
	 */
	public void addProperty(MExpressionType property)
	{
		if(propertylist==null)
			propertylist = new ArrayList();
		propertylist.add(property);
	}
	
	/**
	 *  Get the arguments.
	 *  @return The arguments.
	 * /
	public List getArguments()
	{
		return this.arguments;
	}*/
	
	/**
	 *  Get an argument per name.
	 *  @param name The name.
	 *  @return The argument.
	 * /
	public Argument getArgument(String name)
	{
		Argument ret = null;
		for(int i=0; i<arguments.size() && ret==null; i++)
		{
			Argument tmp = (Argument)arguments.get(i);
			if(tmp.getName().equals(name))
				ret = tmp;
		}
		return ret;
	}*/

	/**
	 *  Get the results.
	 *  @return The results.
	 * /
	public List getResults()
	{
		return this.results;
	}*/

	/**
	 *  Get the imports.
	 *  @return The imports.
	 */
	public List getImports()
	{
		return this.imports;
	}
	
	/**
	 *  Get the componenttypes.
	 *  @return The componenttypes.
	 */
	public List getMComponentTypes()
	{
		return this.componenttypes;
	}
	
	/**
	 *  Get the space types.
	 *  @return The spacetypes.
	 */
	public List getMSpaceTypes()
	{
		return this.spacetypes;
	}

	/**
	 *  Get a named componenttype.
	 *  @param name The component type name.
	 *  @return The componenttype (if any).
	 */
	public MComponentType getMComponentType(String name)
	{
		MComponentType	ret	= null;
		for(int i=0; ret==null && i<componenttypes.size(); i++)
		{
			MComponentType	at	= (MComponentType)componenttypes.get(i);
			if(at.getName().equals(name))
				ret	= at;
		}
		return ret;
	}

	/**
	 *  Get a named spacetype.
	 *  @param name The space type name.
	 *  @return The spacetype (if any).
	 */
	public MSpaceType getMSpaceType(String name)
	{
		MSpaceType ret	= null;
		for(int i=0; ret==null && i<spacetypes.size(); i++)
		{
			MSpaceType st = (MSpaceType)spacetypes.get(i);
			if(st.getName().equals(name))
				ret	= st;
		}
		return ret;
	}
	
	/**
	 *  Get the applications.
	 *  @return The applications.
	 */
	public List getMApplicationInstances()
	{
		return this.applications;
	}
	
	/**
	 *  Get complete imports (including own package).
	 *  @return An array of imports;
	 */
	public String[] getAllImports()
	{
		String[] ret = SUtil.EMPTY_STRING_ARRAY;
		
		if(imports!=null)
		{
			if(modelinfo.getPackage()!=null)
			{
				ret = new String[imports.size()+1];
				ret[imports.size()] = modelinfo.getPackage()+".*";
			}
			else
			{
				ret = new String[imports.size()];
			}
			for(int i=0; i<imports.size(); i++)
				ret[i] = (String)imports.get(i);
		}
		else if(modelinfo.getPackage()!=null)
		{
			ret = new String[]{modelinfo.getPackage()+".*"};
		}
		
		return ret;
	}
	
	/**
	 *  Get the provided services.
	 *  @return The services.
	 */
	public List getProvidedServices()
	{
		return this.providedservices;
	}
	
	/**
	 *  Get the required services.
	 *  @return The services.
	 */
	public List getRequiredServices()
	{
		return this.requiredservices;
	}
	
	/**
	 *  Get the container.
	 *  @return the container.
	 */
	public MExpressionType getContainer()
	{
		return container;
	}

	/**
	 *  Set the container.
	 *  @param container The container to set.
	 */
	public void setContainer(MExpressionType container)
	{
		this.container = container;
	}

	/**
	 *  Get the properties.
	 *  @return The properties.
	 * /
	public List getProperties()
	{
		return this.properties;
	}*/
	
	/**
	 *  Set the classloader.
	 *  @param classloader The classloader.
	 */
	public void setClassloader(ClassLoader classloader)
	{
		modelinfo.setClassloader(classloader);
	}
	
	/**
	 *  Get the modelinfo.
	 *  @return the modelinfo.
	 */
	public ModelInfo getModelInfo()
	{
		return modelinfo;
	}
}
