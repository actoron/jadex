package jadex.micro;

import jadex.bridge.modelinfo.ComponentInstanceInfo;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.modelinfo.IModelValueProvider;
import jadex.bridge.modelinfo.ModelInfo;
import jadex.bridge.modelinfo.SubcomponentTypeInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.ProvidedServiceImplementation;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.GuiClass;
import jadex.bridge.service.annotation.GuiClassName;
import jadex.commons.SReflect;
import jadex.javaparser.IValueFetcher;
import jadex.javaparser.SJavaParser;
import jadex.kernelbase.CacheableKernelModel;
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

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  Reads micro agent classes and generates a model from metainfo and annotations.
 */
public class MicroClassReader
{
	/**
	 *  Load a  model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return The loaded model.
	 */
	public CacheableKernelModel read(String model, String[] imports, ClassLoader classloader)
	{
//		System.out.println("loading micro: "+model);
		String clname = model;
		
		// Hack! for extracting clear classname
		if(clname.endsWith(".class"))
			clname = model.substring(0, model.indexOf(".class"));
		clname = clname.replace('\\', '.');
		clname = clname.replace('/', '.');
		
		Class cma = getMicroAgentClass(clname, imports, classloader);
		
		return read(model, cma, classloader);
	}
	
	/**
	 *  Load the model.
	 */
	protected CacheableKernelModel read(String model, Class cma, ClassLoader classloader)
	{
		ModelInfo modelinfo = new ModelInfo();
		CacheableKernelModel ret = new CacheableKernelModel(modelinfo);
		
		String name = SReflect.getUnqualifiedClassName(cma);
		if(name.endsWith("Agent"))
			name = name.substring(0, name.lastIndexOf("Agent"));
		String packagename = cma.getPackage()!=null? cma.getPackage().getName(): null;
		modelinfo.setName(name);
		modelinfo.setPackage(packagename);
		modelinfo.setFilename(model);
		modelinfo.setClassloader(classloader);
		modelinfo.setStartable(true);
		
		try
		{
			Method m = cma.getMethod("getMetaInfo", new Class[0]);
			if(m!=null)
			{
				MicroAgentMetaInfo metainfo = (MicroAgentMetaInfo)m.invoke(null, new Object[0]);
				fillMicroModelFromMetaInfo(ret, model, cma, classloader, metainfo);
			}
		}
		catch(Exception e)
		{
		}
		
		fillMicroModelFromAnnotations(ret, model, cma, classloader);
		
		return ret;
	}
	
	/**
	 *  Fill the model details using meta info.
	 */
	protected void fillMicroModelFromMetaInfo(CacheableKernelModel micromodel, String model, Class cma, ClassLoader classloader, MicroAgentMetaInfo metainfo)
	{
		try
		{
			ModelInfo modelinfo = (ModelInfo)micromodel.getModelInfo();
			Method m = cma.getMethod("getMetaInfo", new Class[0]);
			if(m!=null)
				metainfo = (MicroAgentMetaInfo)m.invoke(null, new Object[0]);
			
			String description = metainfo!=null && metainfo.getDescription()!=null? metainfo.getDescription(): null;
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
			
			ConfigurationInfo[] cinfo = null;
			if(configurations!=null)
			{
				cinfo = new ConfigurationInfo[configurations.length];
				for(int i=0; i<configurations.length; i++)
				{
					cinfo[i] = new ConfigurationInfo(configurations[i]);
					cinfo[i].setMaster((Boolean)master.getValue(configurations[i]));
					cinfo[i].setDaemon((Boolean)daemon.getValue(configurations[i]));
					cinfo[i].setAutoShutdown((Boolean)autosd.getValue(configurations[i]));
					// suspend?
					// todo
//					cinfo[i].addArgument(argument)
				}
			}
			modelinfo.setDescription(description);
			modelinfo.setArguments(arguments);
			modelinfo.setResults(results);
			modelinfo.setProperties(properties);
			modelinfo.setRequiredServices(required);
			modelinfo.setProvidedServices(provided);
			modelinfo.setConfigurations(cinfo);
		}
		catch(Exception e)
		{
//			e.printStackTrace();
		}
	}
	
	/**
	 *  Fill the model details using annotation.
	 */
	protected void fillMicroModelFromAnnotations(CacheableKernelModel micromodel, String model, Class cma, ClassLoader classloader)
	{
		ModelInfo modelinfo = (ModelInfo)micromodel.getModelInfo();
		
		if(cma.isAnnotationPresent(Imports.class))
		{
			String[] tmp = ((Imports)cma.getAnnotation(Imports.class)).value();
			modelinfo.setImports(tmp);
		}
		if(cma.isAnnotationPresent(Description.class))
		{
			Description val = (Description)cma.getAnnotation(Description.class);
			modelinfo.setDescription(val.value());
		}
		if(cma.isAnnotationPresent(Properties.class))
		{
			Properties val = (Properties)cma.getAnnotation(Properties.class);
			NameValue[] vals = val.value();
			Map props = new HashMap();
			for(int i=0; i<vals.length; i++)
			{
				// Todo: clazz, language
				props.put(vals[i].name(), new UnparsedExpression(vals[i].name(), null, vals[i].value(), null) );
			}
			modelinfo.setProperties(props);
		}
		if(cma.isAnnotationPresent(RequiredServices.class))
		{
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
			modelinfo.setRequiredServices(rsis);
		}
		if(cma.isAnnotationPresent(ProvidedServices.class))
		{
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
					im.expression().length()>0? im.expression(): null, im.proxytype(), bind);
				psis[i] = new ProvidedServiceInfo(vals[i].name().length()>0? vals[i].name(): null, vals[i].type(), impl);
			}
			modelinfo.setProvidedServices(psis);
		}
		Map argsmap = new HashMap();
		if(cma.isAnnotationPresent(Arguments.class))
		{
			Arguments val = (Arguments)cma.getAnnotation(Arguments.class);
			Argument[] vals = val.value();
			IArgument[] tmpargs = new IArgument[vals.length];
			for(int i=0; i<vals.length; i++)
			{
//				Object arg = SJavaParser.evaluateExpression(vals[i].defaultvalue(), imports, null, classloader);
				Object defval = evaluateExpression(vals[i].defaultvalue(), modelinfo.getAllImports(), null, classloader);
				tmpargs[i] = new jadex.bridge.modelinfo.Argument(vals[i].name(), 
					vals[i].description(), vals[i].typename(), defval);
				argsmap.put(tmpargs[i].getName(), tmpargs[i]);
			}
			modelinfo.setArguments(tmpargs);
		}
		Map resmap = new HashMap();
		if(cma.isAnnotationPresent(Results.class))
		{
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
			modelinfo.setResults(tmpresults);
		}
		Configuration[] configs = null;
		if(cma.isAnnotationPresent(Configurations.class))
		{
			Configurations val = (Configurations)cma.getAnnotation(Configurations.class);
			configs = val.value();
		}
		else if(cma.isAnnotationPresent(Configuration.class))
		{
			Configuration val = (Configuration)cma.getAnnotation(Configuration.class);
			configs = new Configuration[]{val};
		}
		
		if(configs!=null)
		{
			List configinfos = new ArrayList();
			
			String[] confignames = new String[configs.length];
//			Map master = new HashMap();
//			Map daemon = new HashMap();
//			Map autosd = new HashMap();
			for(int i=0; i<configs.length; i++)
			{
				confignames[i] = configs[i].name();
				
				ConfigurationInfo configinfo = new ConfigurationInfo(confignames[i]);
				configinfos.add(configinfo);
				
				configinfo.setMaster(configs[i].master());
				configinfo.setDaemon(configs[i].daemon());
				configinfo.setAutoShutdown(configs[i].autoshutdown());
				configinfo.setSuspend(configs[i].suspend());
				
				NameValue[] argvals = configs[i].arguments();
				for(int j=0; j<argvals.length; j++)
				{
					jadex.bridge.modelinfo.Argument arg = (jadex.bridge.modelinfo.Argument)argsmap.get(argvals[j].name());
//					Object val = SJavaParser.evaluateExpression(argvals[j].value(), imports, null, classloader);
					Object val = evaluateExpression(argvals[j].value(), modelinfo.getAllImports(), null, classloader);
					arg.setDefaultValue(confignames[i], val);
				}
				NameValue[] resvals = configs[i].results();
				for(int j=0; j<resvals.length; j++)
				{
					jadex.bridge.modelinfo.Argument arg = (jadex.bridge.modelinfo.Argument)resmap.get(resvals[j].name());
//					Object val = SJavaParser.evaluateExpression(resvals[j].value(), imports, null, classloader);
					Object val = evaluateExpression(resvals[j].value(), modelinfo.getAllImports(), null, classloader);
					arg.setDefaultValue(confignames[i], val);
				}
				
				ProvidedService[] provs = configs[i].providedservices();
				ProvidedServiceInfo[] psis = new ProvidedServiceInfo[provs.length];
				for(int j=0; j<provs.length; j++)
				{
					Implementation im = provs[j].implementation();
					Binding bd = im.binding();
					RequiredServiceBinding bind = bd==null? null: new RequiredServiceBinding(bd.name(), 
						bd.componentname().length()==0? null: bd.componentname(), bd.componenttype().length()==0? null: bd.componenttype(), 
						bd.dynamic(), bd.scope(), bd.create(), bd.recover());
					ProvidedServiceImplementation impl = new ProvidedServiceImplementation(!im.value().equals(Object.class)? im.value(): null, 
						im.expression().length()>0? im.expression(): null, im.proxytype(), bind);
					psis[j] = new ProvidedServiceInfo(provs[j].name().length()>0? provs[j].name(): null, provs[j].type(), impl);
					configinfo.setProvidedServices(psis);
				}
				
				RequiredService[] reqs = configs[i].requiredservices();
				RequiredServiceInfo[] rsis = new RequiredServiceInfo[reqs.length];
				for(int j=0; j<reqs.length; j++)
				{
					Binding bd = reqs[j].binding();
					RequiredServiceBinding binding = new RequiredServiceBinding(reqs[j].name(), 
						bd.componentname().length()==0? null: bd.componentname(), bd.componenttype().length()==0? null: bd.componenttype(), 
						bd.dynamic(), bd.scope(), bd.create(), bd.recover());
					rsis[j] = new RequiredServiceInfo(reqs[j].name(), reqs[j].type(), 
						reqs[j].multiple(), binding);
					configinfo.setRequiredServices(rsis);
				}
				
				// todo: store arguments in config not in valueprovider
				
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
						comp.setTypeName(comps[j].type());
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
							exps[k] = new UnparsedExpression(args[k].name(), null, args[k].value(), null);
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
			
//			metainfo.setMaster(new ModelValueProvider(master));
//			metainfo.setDaemon(new ModelValueProvider(daemon));
//			metainfo.setAutoShutdown(new ModelValueProvider(autosd));
//			metainfo.setConfigs(confignames);
			
			modelinfo.setConfigurations((ConfigurationInfo[])configinfos.toArray(new ConfigurationInfo[configinfos.size()]));
		}
		
		// Determine subcomponent types
		if(cma.isAnnotationPresent(ComponentTypes.class))
		{
			SubcomponentTypeInfo[] subinfos = null;
			ComponentTypes tmp = (ComponentTypes)cma.getAnnotation(ComponentTypes.class);
			ComponentType[] ctypes = tmp.value();
			subinfos = new SubcomponentTypeInfo[ctypes.length];
			for(int i=0; i<ctypes.length; i++)
			{
				subinfos[i] = new SubcomponentTypeInfo(ctypes[i].name(), ctypes[i].filename());
			}
			modelinfo.setSubcomponentTypes(subinfos);
		}
		
		// todo: move to be able to use the constant
		// jadex.base.gui.componentviewer.IAbstractViewerPanel.PROPERTY_VIEWERCLASS
		if(cma.isAnnotationPresent(GuiClass.class))
		{
			GuiClass gui = (GuiClass)cma.getAnnotation(GuiClass.class);
			Class clazz = gui.value();
			modelinfo.addProperty("componentviewer.viewerclass", clazz);
		}
		else if(cma.isAnnotationPresent(GuiClassName.class))
		{
			GuiClassName gui = (GuiClassName)cma.getAnnotation(GuiClassName.class);
			String clazzname = gui.value();
			modelinfo.addProperty("componentviewer.viewerclass", clazzname);
		}
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
}
