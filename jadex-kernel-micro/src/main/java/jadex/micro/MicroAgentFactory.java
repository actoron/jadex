package jadex.micro;

import jadex.bridge.IComponentAdapterFactory;
import jadex.bridge.IComponentDescription;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IErrorReport;
import jadex.bridge.IExternalAccess;
import jadex.bridge.modelinfo.ComponentInstanceInfo;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.IModelValueProvider;
import jadex.bridge.modelinfo.ModelInfo;
import jadex.bridge.modelinfo.ModelValueProvider;
import jadex.bridge.modelinfo.SubcomponentTypeInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceProvider;
import jadex.bridge.service.ProvidedServiceImplementation;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.GuiClass;
import jadex.bridge.service.annotation.GuiClassName;
import jadex.commons.ByteClassLoader;
import jadex.commons.SReflect;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.gui.SGUI;
import jadex.javaparser.IValueFetcher;
import jadex.javaparser.SJavaParser;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.Configuration;
import jadex.micro.annotation.Configurations;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.swing.UIDefaults;

/**
 *  Factory for creating micro agents.
 */
public class MicroAgentFactory extends BasicService implements IComponentFactory
{
	//-------- constants --------
	
	/** The micro agent file type. */
	public static final String	FILETYPE_MICROAGENT	= "Micro Agent";
	
	/** The image icons. */
	protected static final UIDefaults icons = new UIDefaults(new Object[]
	{
		"micro_agent",	SGUI.makeIcon(MicroAgentFactory.class, "/jadex/micro/images/micro_agent.png"),
	});

	//-------- attributes --------
	
	/** The platform. */
//	protected IServiceProvider provider;
	
	/** The properties. */
	protected Map properties;
	
	//-------- constructors --------
	
	/**
	 *  Create a new agent factory.
	 */
	public MicroAgentFactory(IServiceProvider provider, Map properties)
	{
		super(provider.getId(), IComponentFactory.class, null);

//		this.provider = provider;
		this.properties = properties;
	}
	
	/**
	 *  Create a new agent factory for startup.
	 *  @param platform	The platform.
	 */
	// This constructor is used by the Starter class and the ADFChecker plugin. 
	public MicroAgentFactory(String providerid)
	{
		super(providerid, IComponentFactory.class, null);
	}
	
	//-------- IAgentFactory interface --------
	
	/**
	 *  Load a  model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return The loaded model.
	 */
	public IModelInfo loadModel(InputStream in, String[] imports, ClassLoader classloader)
	{
		IModelInfo ret = null;
		
		ByteClassLoader cl = new ByteClassLoader(classloader);
		BufferedInputStream bin = new BufferedInputStream(in);
		ByteArrayOutputStream bos = new ByteArrayOutputStream();

		try
		{
			int d;
			while((d = bin.read())!=-1)
				bos.write(d);
		
			Class cma = cl.loadClass(null, bos.toByteArray(), true);
			String model = cma.getName().replace('.', '/');
			
			ret = loadModel(model, cma, cl);
		}
		catch(Exception e)
		{
		}
		
		try
		{
			bin.close();
		}
		catch(IOException e)
		{
		}
		try
		{
			bos.close();
		}
		catch(IOException e)
		{
		}
		
		return ret;
	}
	
	/**
	 *  Load a  model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return The loaded model.
	 */
	public IFuture loadModel(String model, String[] imports, ClassLoader classloader)
	{
//		System.out.println("loading micro: "+model);
		String clname = model;
		
		// Hack! for extracting clear classname
		if(clname.endsWith(".class"))
			clname = model.substring(0, model.indexOf(".class"));
		clname = clname.replace('\\', '.');
		clname = clname.replace('/', '.');
		
		Class cma = getMicroAgentClass(clname, imports, classloader);
		
		Future	ret	= new Future();
		try
		{
			ret.setResult(loadModel(model, cma, classloader));
		}
		catch(RuntimeException e)
		{
			ret.setException(new RuntimeException("Error loading model: "+model, e));
		}
		
		return ret;
	}
	
	/**
	 *  Load the model.
	 */
	protected IModelInfo loadModel(String model, Class cma, ClassLoader classloader)
	{
		// Try to read meta information from class.
		MicroAgentMetaInfo metainfo = null;
		String[] imports = new String[]{cma.getClass().getPackage().getName()+".*"};
		
		if(cma.isAnnotationPresent(Imports.class))
		{
			String[] tmp = ((Imports)cma.getAnnotation(Imports.class)).value();
			String[] imp = new String[tmp.length+1];
			imp[0] = imports[0];
			System.arraycopy(tmp, 0, imp, 1, tmp.length);
			imports = imp;
		}
		if(cma.isAnnotationPresent(Description.class))
		{
			if(metainfo==null)
				metainfo = new MicroAgentMetaInfo();
			Description val = (Description)cma.getAnnotation(Description.class);
			metainfo.setDescription(val.value());
		}
		if(cma.isAnnotationPresent(Properties.class))
		{
			if(metainfo==null)
				metainfo = new MicroAgentMetaInfo();
			Properties val = (Properties)cma.getAnnotation(Properties.class);
			NameValue[] vals = val.value();
			Map props = new HashMap();
			for(int i=0; i<vals.length; i++)
			{
				props.put(vals[i].name(), vals[i].value());
			}
			metainfo.setProperties(props);
		}
		if(cma.isAnnotationPresent(RequiredServices.class))
		{
			if(metainfo==null)
				metainfo = new MicroAgentMetaInfo();
			RequiredServices val = (RequiredServices)cma.getAnnotation(RequiredServices.class);
			RequiredService[] vals = val.value();
			RequiredServiceInfo[] rsis = new RequiredServiceInfo[vals.length];
			for(int i=0; i<vals.length; i++)
			{
				Binding bd = vals[i].binding();
				RequiredServiceBinding binding = new RequiredServiceBinding(vals[i].name(), 
					bd.componentname().length()==0? null: bd.componentname(), bd.componenttype().length()==0? null: bd.componenttype(), 
					bd.dynamic(), bd.scope(), bd.create(), bd.recover());
				rsis[i] = new RequiredServiceInfo(vals[i].name(), vals[i].type(), 
					vals[i].multiple(), binding);
			}
			metainfo.setRequiredServices(rsis);
		}
		if(cma.isAnnotationPresent(ProvidedServices.class))
		{
			if(metainfo==null)
				metainfo = new MicroAgentMetaInfo();
			ProvidedServices val = (ProvidedServices)cma.getAnnotation(ProvidedServices.class);
			ProvidedService[] vals = val.value();
			ProvidedServiceInfo[] psis = new ProvidedServiceInfo[vals.length];
			for(int i=0; i<vals.length; i++)
			{
				Implementation im = vals[i].implementation();
				Binding bd = im.binding();
				RequiredServiceBinding bind = bd==null? null: new RequiredServiceBinding(bd.name(), 
					bd.componentname().length()==0? null: bd.componentname(), bd.componenttype().length()==0? null: bd.componenttype(), 
					bd.dynamic(), bd.scope(), bd.create(), bd.recover());
				ProvidedServiceImplementation impl = new ProvidedServiceImplementation(!im.value().equals(Object.class)? im.value(): null, 
					im.expression().length()>0? im.expression(): null, im.direct(), bind);
				psis[i] = new ProvidedServiceInfo(vals[i].type(), impl);
			}
			metainfo.setProvidedServices(psis);
		}
		Map argsmap = new HashMap();
		if(cma.isAnnotationPresent(Arguments.class))
		{
			if(metainfo==null)
				metainfo = new MicroAgentMetaInfo();
			Arguments val = (Arguments)cma.getAnnotation(Arguments.class);
			Argument[] vals = val.value();
			IArgument[] tmpargs = new IArgument[vals.length];
			for(int i=0; i<vals.length; i++)
			{
//				Object arg = SJavaParser.evaluateExpression(vals[i].defaultvalue(), imports, null, classloader);
				Object arg = evaluateExpression(vals[i].defaultvalue(), imports, null, classloader);
				tmpargs[i] = new jadex.bridge.modelinfo.Argument(vals[i].name(), 
					vals[i].description(), vals[i].typename(), arg);
				argsmap.put(tmpargs[i].getName(), tmpargs[i]);
			}
			metainfo.setArguments(tmpargs);
		}
		Map resmap = new HashMap();
		if(cma.isAnnotationPresent(Results.class))
		{
			if(metainfo==null)
				metainfo = new MicroAgentMetaInfo();
			Results val = (Results)cma.getAnnotation(Results.class);
			Result[] vals = val.value();
			IArgument[] tmpresults = new IArgument[vals.length];
			for(int i=0; i<vals.length; i++)
			{
//				Object res = evaluateExpression(vals[i].defaultvalue(), imports, null, classloader);
				tmpresults[i] = new jadex.bridge.modelinfo.Argument(vals[i].name(), 
					vals[i].description(), vals[i].typename(), null);
				resmap.put(tmpresults[i].getName(), tmpresults[i]);
			}
			metainfo.setResults(tmpresults);
		}
		Configuration[] configs = null;
		if(cma.isAnnotationPresent(Configurations.class))
		{
			if(metainfo==null)
				metainfo = new MicroAgentMetaInfo();
			Configurations val = (Configurations)cma.getAnnotation(Configurations.class);
			configs = val.value();
		}
		else if(cma.isAnnotationPresent(Configuration.class))
		{
			if(metainfo==null)
				metainfo = new MicroAgentMetaInfo();
			Configuration val = (Configuration)cma.getAnnotation(Configuration.class);
			configs = new Configuration[]{val};
		}
		
		ConfigurationInfo[] cinfos = null;
		if(configs!=null)
		{
			if(metainfo==null)
				metainfo = new MicroAgentMetaInfo();
			
			List configinfos = new ArrayList();
			
			String[] confignames = new String[configs.length];
			Map master = new HashMap();
			Map daemon = new HashMap();
			Map autosd = new HashMap();
			for(int i=0; i<configs.length; i++)
			{
				confignames[i] = configs[i].name();
				master.put(confignames[i], configs[i].master());
				daemon.put(confignames[i], configs[i].daemon());
				autosd.put(confignames[i], configs[i].autoshutdown());
				
				NameValue[] argvals = configs[i].arguments();
				for(int j=0; j<argvals.length; j++)
				{
					jadex.bridge.modelinfo.Argument arg = (jadex.bridge.modelinfo.Argument)argsmap.get(argvals[j].name());
//					Object val = SJavaParser.evaluateExpression(argvals[j].value(), imports, null, classloader);
					Object val = evaluateExpression(argvals[j].value(), imports, null, classloader);
					arg.setDefaultValue(confignames[i], val);
				}
				NameValue[] resvals = configs[i].results();
				for(int j=0; j<resvals.length; j++)
				{
					jadex.bridge.modelinfo.Argument arg = (jadex.bridge.modelinfo.Argument)resmap.get(resvals[j].name());
//					Object val = SJavaParser.evaluateExpression(resvals[j].value(), imports, null, classloader);
					Object val = evaluateExpression(resvals[j].value(), imports, null, classloader);
					arg.setDefaultValue(confignames[i], val);
				}
				
				// todo: store arguments in config not in valueprovider
				
				ConfigurationInfo configinfo = new ConfigurationInfo(confignames[i]);
				configinfos.add(configinfo);
				Component[] comps = configs[i].components();
				for(int j=0; j<comps.length; j++)
				{
					ComponentInstanceInfo comp = new ComponentInstanceInfo();
					
					comp.setSuspend(comps[j].suspend());
					comp.setMaster(comps[j].master());
					comp.setDaemon(comps[j].daemon());
					comp.setAutoShutdown(comps[j].autoshutdown());
					
					if(comps[j].name().length()>0)
						comp.setName(comps[j].name());
					if(comps[j].type().length()>0)
						comp.setTypename(comps[j].type());
					if(comps[j].configuration().length()>0)
						comp.setConfiguration(comps[j].configuration());
					if(comps[j].number().length()>0)
						comp.setNumber(comps[j].number());
					
					NameValue[] args = comps[j].arguments();
					if(args.length>0)
					{
						UnparsedExpression[] exps = new UnparsedExpression[args.length];
						for(int k=0; k<args.length; k++)
						{
							exps[k] = new UnparsedExpression(args[k].name(), null, args[i].value(), null);
						}
						comp.setArguments(exps);
					}
					
					Binding[] binds = comps[j].bindings();
					if(binds.length>0)
					{
						RequiredServiceBinding[] bds = new RequiredServiceBinding[binds.length];
						for(int k=0; k<binds.length; k++)
						{
							bds[k] = new RequiredServiceBinding(binds[k].name(), 
								binds[k].componentname().length()==0? null: binds[k].componentname(), binds[k].componenttype().length()==0? null: binds[k].componenttype(), 
								binds[k].dynamic(), binds[k].scope(), binds[k].create(), binds[k].recover());
						}
						comp.setBindings(bds);
					}
					
					configinfo.addComponentInstance(comp);
				}
			}
			
			metainfo.setMaster(new ModelValueProvider(master));
			metainfo.setDaemon(new ModelValueProvider(daemon));
			metainfo.setAutoShutdown(new ModelValueProvider(autosd));
			metainfo.setConfigs(confignames);
			
			cinfos = (ConfigurationInfo[])configinfos.toArray(new ConfigurationInfo[configinfos.size()]);
		}
		
		// Determine subcomponent types
		SubcomponentTypeInfo[] subinfos = null;
		if(cma.isAnnotationPresent(ComponentTypes.class))
		{
			ComponentTypes tmp = (ComponentTypes)cma.getAnnotation(ComponentTypes.class);
			ComponentType[] ctypes = tmp.value();
			subinfos = new SubcomponentTypeInfo[ctypes.length];
			for(int i=0; i<ctypes.length; i++)
			{
				subinfos[i] = new SubcomponentTypeInfo(ctypes[i].name(), ctypes[i].filename());
			}
		}
		
		// todo: move to be able to use the constant
		// jadex.base.gui.componentviewer.IAbstractViewerPanel.PROPERTY_VIEWERCLASS
		if(cma.isAnnotationPresent(GuiClass.class))
		{
			if(metainfo==null)
				metainfo = new MicroAgentMetaInfo();
			GuiClass gui = (GuiClass)cma.getAnnotation(GuiClass.class);
			Class clazz = gui.value();
			metainfo.putPropertyValue("componentviewer.viewerclass", clazz);
		}
		else if(cma.isAnnotationPresent(GuiClassName.class))
		{
			if(metainfo==null)
				metainfo = new MicroAgentMetaInfo();
			GuiClassName gui = (GuiClassName)cma.getAnnotation(GuiClassName.class);
			String clazzname = gui.value();
			metainfo.putPropertyValue("componentviewer.viewerclass", clazzname);
		}

		if(metainfo==null)
		{
			try
			{
				Method m = cma.getMethod("getMetaInfo", new Class[0]);
				if(m!=null)
					metainfo = (MicroAgentMetaInfo)m.invoke(null, new Object[0]);
			}
			catch(Exception e)
			{
	//			e.printStackTrace();
			}
		}
		
		String name = SReflect.getUnqualifiedClassName(cma);
		if(name.endsWith("Agent"))
			name = name.substring(0, name.lastIndexOf("Agent"));
		String packagename = cma.getPackage()!=null? cma.getPackage().getName(): null;
		String description = metainfo!=null && metainfo.getDescription()!=null? metainfo.getDescription(): null;
		IErrorReport report = null;
		String[] configurations = metainfo!=null? metainfo.getConfigurations(): null;
		IArgument[] arguments = metainfo!=null? metainfo.getArguments(): null;
		IArgument[] results = metainfo!=null? metainfo.getResults(): null;
		Map properties = metainfo!=null && metainfo.getProperties()!=null? new HashMap(metainfo.getProperties()): new HashMap();
		RequiredServiceInfo[] required = metainfo!=null? metainfo.getRequiredServices(): null;
		ProvidedServiceInfo[] provided = metainfo!=null? metainfo.getProvidedServices(): null;
		IModelValueProvider master = metainfo!=null? metainfo.getMaster(): null;
		IModelValueProvider daemon= metainfo!=null? metainfo.getDaemon(): null;
		IModelValueProvider autosd = metainfo!=null? metainfo.getAutoShutdown(): null;
		
		// Add debugger breakpoints
		List names = new ArrayList();
		for(int i=0; metainfo!=null && i<metainfo.getBreakpoints().length; i++)
			names.add(metainfo.getBreakpoints()[i]);
		properties.put("debugger.breakpoints", names);
		
		IModelInfo ret = new ModelInfo(name, packagename, description, report, 
			configurations, arguments, results, true, model, properties, classloader, required, provided,
			master, daemon, autosd, cinfos, subinfos, imports);
		
		return ret;
	}
	
	/**
	 *  Evaluate an expression string (using "" -> null mapping) as annotations
	 *  do not support null values.
	 */
	protected Object evaluateExpression(String exp, String[] imports, IValueFetcher fetcher, ClassLoader classloader)
	{
		return exp.length()==0? null: SJavaParser.evaluateExpression(exp, imports, null, classloader);
	}
	
	/**
	 *  Test if a model can be loaded by the factory.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if model can be loaded.
	 */
	public IFuture isLoadable(String model, String[] imports, ClassLoader classloader)
	{
		boolean ret = model.toLowerCase().endsWith("agent.class");
//		if(model.toLowerCase().endsWith("Agent.class"))
//		{
//			ILibraryService libservice = (ILibraryService)platform.getService(ILibraryService.class);
//			String clname = model.substring(0, model.indexOf(".class"));
//			Class cma = SReflect.findClass0(clname, null, libservice.getClassLoader());
//			ret = cma!=null && cma.isAssignableFrom(IMicroAgent.class);
//			System.out.println(clname+" "+cma+" "+ret);
//		}
		return new Future(ret);
	}
	
	/**
	 *  Test if a model is startable (e.g. an component).
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return True, if startable (and loadable).
	 */
	public IFuture isStartable(String model, String[] imports, ClassLoader classloader)
	{
		return isLoadable(model, imports, classloader);
	}

	/**
	 *  Get the names of ADF file types supported by this factory.
	 */
	public String[] getComponentTypes()
	{
		return new String[]{FILETYPE_MICROAGENT};
	}

	/**
	 *  Get a default icon for a file type.
	 */
	public IFuture getComponentTypeIcon(String type)
	{
		return new Future(type.equals(FILETYPE_MICROAGENT) ? icons.getIcon("micro_agent") : null);
	}

	/**
	 *  Get the component type of a model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 */
	public IFuture getComponentType(String model, String[] imports, ClassLoader classloader)
	{
		return new Future(model.toLowerCase().endsWith("agent.class") ? FILETYPE_MICROAGENT: null);
	}
	
	/**
	 * Create a component instance.
	 * @param adapter The component adapter.
	 * @param model The component model.
	 * @param config The name of the configuration (or null for default configuration) 
	 * @param arguments The arguments for the agent as name/value pairs.
	 * @param parent The parent component (if any).
	 * @return An instance of a component.
	 */
	public Object[] createComponentInstance(IComponentDescription desc, IComponentAdapterFactory factory, IModelInfo model, 
		String config, Map arguments, IExternalAccess parent, RequiredServiceBinding[] binding, Future ret)
	{
		MicroAgentInterpreter mai = new MicroAgentInterpreter(desc, factory, model, getMicroAgentClass(model.getFullName()+"Agent", 
			null, model.getClassLoader()), arguments, config, parent, binding, ret);
		return new Object[]{mai, mai.getAgentAdapter()};
	}
	
	/**
	 *  Get the element type.
	 *  @return The element type (e.g. an agent, application or process).
	 * /
	public String getElementType()
	{
		return IComponentFactory.ELEMENT_TYPE_AGENT;
	}*/
	
	/**
	 *  Get the properties.
	 *  Arbitrary properties that can e.g. be used to
	 *  define kernel-specific settings to configure tools.
	 *  @param type	The component type. 
	 *  @return The properties or null, if the component type is not supported by this factory.
	 */
	public Map	getProperties(String type)
	{
		return FILETYPE_MICROAGENT.equals(type)
		? properties: null;
	}
	
	/**
	 *  Start the service.
	 * /
	public synchronized IFuture	startService()
	{
		return new Future(null);
	}*/
	
	/**
	 *  Shutdown the service.
	 *  @param listener The listener.
	 * /
	public synchronized IFuture	shutdownService()
	{
		return new Future(null);
	}*/
	
	/**
	 *  Get the mirco agent class.
	 */
	// todo: make use of cache
	protected Class getMicroAgentClass(String clname, String[] imports, ClassLoader classloader)
	{
		Class ret = SReflect.findClass0(clname, imports, classloader);
//		System.out.println(clname+" "+cma+" "+ret);
		int idx;
		while(ret==null && (idx=clname.indexOf('.'))!=-1)
		{
			clname	= clname.substring(idx+1);
			ret = SReflect.findClass0(clname, imports, classloader);
//			System.out.println(clname+" "+cma+" "+ret);
		}
		if(ret==null)// || !cma.isAssignableFrom(IMicroAgent.class))
			throw new RuntimeException("No micro agent file: "+clname);
		return ret;
	}
	
	/**
	 *  Add excluded methods.
	 */
	public static void addExcludedMethods(Map props, String[] excludes)
	{
		Object ex = props.get("remote_excluded");
		if(ex!=null)
		{
			List newex = new ArrayList();
			for(Iterator it=SReflect.getIterator(ex); it.hasNext(); )
			{
				newex.add(it.next());
			}
			for(int i=0; i<excludes.length; i++)
			{
				newex.add(excludes[i]);
			}
		}
		else
		{
			props.put("remote_excluded", excludes);
		}
	}
}
