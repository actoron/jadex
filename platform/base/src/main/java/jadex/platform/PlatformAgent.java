package jadex.platform;

import static jadex.base.IPlatformConfiguration.LOGGING_LEVEL;
import static jadex.base.IPlatformConfiguration.UNIQUEIDS;

import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.logging.Level;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.DependencyResolver;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.commons.FileFilter;
import jadex.commons.SClassReader;
import jadex.commons.SClassReader.ClassInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Autostart;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.platform.service.security.SecurityAgent;

/**
 *	Basic standalone platform services provided as a micro agent. 
 */
@Arguments(
{
	@Argument(name=LOGGING_LEVEL, clazz=Level.class, defaultvalue="java.util.logging.Level.SEVERE"),
	@Argument(name=UNIQUEIDS, clazz=boolean.class, defaultvalue="true"),
})

@ProvidedServices({
	@ProvidedService(type=IThreadPoolService.class, scope=Binding.SCOPE_PLATFORM, implementation=@Implementation(expression="new jadex.platform.service.threadpool.ThreadPoolService($args.threadpoolclass!=null ? jadex.commons.SReflect.classForName0($args.threadpoolclass, jadex.commons.SReflect.class.getClassLoader()).newInstance() : new jadex.commons.concurrent.JavaThreadPool(false), $component.getId())", proxytype=Implementation.PROXYTYPE_RAW)),
	// hack!!! no daemon here (possibly fixed?)
	@ProvidedService(type=IDaemonThreadPoolService.class, scope=Binding.SCOPE_PLATFORM, implementation=@Implementation(expression="new jadex.platform.service.threadpool.ThreadPoolService($args.threadpoolclass!=null ? jadex.commons.SReflect.classForName0($args.threadpoolclass, jadex.commons.SReflect.class.getClassLoader()).newInstance() : new jadex.commons.concurrent.JavaThreadPool(true), $component.getId())", proxytype=Implementation.PROXYTYPE_RAW)),
	@ProvidedService(type=IExecutionService.class, scope=Binding.SCOPE_PLATFORM, implementation=@Implementation(expression="($args.asyncexecution!=null && !$args.asyncexecution.booleanValue()) || ($args.asyncexecution==null && $args.simulation!=null && $args.simulation.booleanValue())? new jadex.platform.service.execution.SyncExecutionService($component): new jadex.platform.service.execution.AsyncExecutionService($component)", proxytype=Implementation.PROXYTYPE_RAW)),
	@ProvidedService(type=IComponentManagementService.class, name="cms", implementation=@Implementation(expression="new jadex.platform.service.cms.ComponentManagementService($platformaccess, $bootstrapfactory, $args.uniqueids)"))
})

@RequiredServices(
{
	@RequiredService(name="factoryservices", type=IComponentFactory.class, multiple=true, binding=@Binding(scope=Binding.SCOPE_PLATFORM))
})

@Properties(
{
	@NameValue(name="componentviewer.viewerclass", value="jadex.commons.SReflect.classForName0(\"jadex.base.gui.componentviewer.DefaultComponentServiceViewerPanel\", jadex.platform.service.library.LibraryService.class.getClassLoader())"),
	@NameValue(name="logging.level", value="$args.logging ? java.util.logging.Level.INFO : $args.logging_level")
})
@Agent
public class PlatformAgent
{
	@Agent
	protected IInternalAccess agent;
	
	// where should the defaults be defined (here or in the config)
//	@Arguments
//	public static jadex.bridge.modelinfo.Argument[] getArguments()
//	{
//		return PlatformConfigurationHandler.getArguments();
//	}
	
	/**
	 *  Called when platform startup finished.
	 */
	@AgentCreated
	public IFuture<Void> init()
	{
		System.out.println("Start scanning...");
		long start = System.currentTimeMillis();
		
		Map<Class<?>, String> names = new HashMap<Class<?>, String>();
		DependencyResolver<Class<?>> dr = new DependencyResolver<Class<?>>();

		URL[] urls = new URL[0];
		ClassLoader classloader = PlatformAgent.class.getClassLoader();
		if(classloader instanceof URLClassLoader)
			urls = ((URLClassLoader)classloader).getURLs();
		
		Set<Class<?>> components = scanForComponents(urls, classloader);

		for(Class<?> cl: components)
			addComponentToLevels(dr, cl, names);
		
//		System.out.println("cls: "+files.size()+" "+components.size());
		System.out.println("Scanning files needed: "+(System.currentTimeMillis()-start)/1000);
		
		Collection<Set<Class<?>>> levels = dr.resolveDependenciesWithLevel();
		
		IComponentManagementService cms = agent.getFeature(IRequiredServicesFeature.class).getLocalService(IComponentManagementService.class);
		
		return startComponents(cms, levels.iterator(), names);
	}
	
	/**
	 *  Scan for component classes in the classpath.
	 */
	protected Set<Class<?>> scanForComponents(URL[] urls, ClassLoader classloader)
	{
		Map<String, Set<String>> files = SReflect.scanForFiles2(urls, new FileFilter("$", false, ".class"));
		
		Set<Class<?>> components = new HashSet<>();
		
//		int cnt = 0;
		for(Map.Entry<String, Set<String>> entry: files.entrySet())
		{
			String jarname = entry.getKey();
			if(jarname!=null)
			{
				try(JarFile jar	= new JarFile(jarname))
				{
					for(String jename: entry.getValue())
					{
						JarEntry je = jar.getJarEntry(jename);
						InputStream is = jar.getInputStream(je);
						ClassInfo ci = SClassReader.getClassInfo(is);
						if(ci.hasAnnotation( Agent.class.getName()))
						{
//							System.out.println("Found candidate: "+tup.getSecondEntity());
							Class<?> clazz = SReflect.findClass0(ci.getClassname(), null, classloader);
							if(clazz==null)
								throw new RuntimeException("Could not load class: "+ci.getClassname());
							components.add(clazz);
						}
//						System.out.println(cnt++);
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
			else
			{
				for(String filename: entry.getValue())
				{
					try(FileInputStream is = new FileInputStream(filename))
					{
						ClassInfo ci = SClassReader.getClassInfo(is);
						if(ci.hasAnnotation( Agent.class.getName()))
						{
	//						System.out.println("Found candidate: "+tup.getSecondEntity());
							Class<?> clazz = SReflect.findClass0(ci.getClassname(), null, classloader);
							if(clazz==null)
								throw new RuntimeException("Could not load class: "+ci.getClassname());
							components.add(clazz);
						}
//						System.out.println(cnt++);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		
		return components;
	}
	
	
	/**
	 *  Add a components to the dependency resolver to build start levels.
	 *  Components of the same level can be started in parallel.
	 */
	protected void addComponentToLevels(DependencyResolver<Class<?>> dr, Class<?> cl, Map<Class<?>, String> names)
	{
		try
		{
//			System.out.println("Found Agent annotation on class: "+ cl.getName());
			Agent aan = cl.getAnnotation(Agent.class);
			Autostart autostart = aan.autostart();
			if(autostart.value().toBoolean()!=null)
			{		
				Map<String, Object> argsmap = (Map<String, Object>)Starter.getPlatformValue(agent.getId(), IPlatformConfiguration.PLATFORMARGS);
				
				String name = autostart.name().length()==0? null: autostart.name();
				
				boolean ok = autostart.value().toBoolean().booleanValue();
				if(name!=null)
				{
					if(argsmap.containsKey(name))
						ok = (boolean)argsmap.get(name);
				}
				else
				{
					// check classname as parameter
					name = SReflect.getInnerClassName(cl);
					if(argsmap.containsKey(name.toLowerCase()))
					{	
						ok = (boolean)argsmap.get(name.toLowerCase());
					}
					else
					{
						// check classname - suffix (BDI/Agent etc) in lowercase
						int suf = SUtil.inndexOfLastUpperCaseCharacter(name);
						if(suf>0)
						{
							name = name.substring(0, suf).toLowerCase();
							if(argsmap.containsKey(name))
							{	
								ok = (boolean)argsmap.get(name);
							}
						}
					}
				}
				
				if(ok)
				{
					dr.addNode(cl);
					for(Class<?> pre: autostart.predecessors())
					{
						// Object as placeholder for no deps, because no entries should not mean no deps
						if(!Object.class.equals(pre))
							dr.addDependency(cl, pre);
					}
					for(Class<?> suc: autostart.successors())
						dr.addDependency(suc, cl);
					
					// if no predecessors are defined add SecurityAgent
					if(autostart.predecessors().length==0)
						dr.addDependency(cl, SecurityAgent.class);
					
					names.put(cl, name);
				}
				else
				{
//					System.out.println("Not starting: "+name);
				}
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 *  Start components in levels.
	 */
	protected IFuture<Void> startComponents(IComponentManagementService cms, Iterator<Set<Class<?>>> levels, Map<Class<?>, String> names)
	{
		final Future<Void> ret = new Future<>();
		
		if(levels.hasNext())
		{
//			System.out.println("---------- LEVEL --------------");
			Set<Class<?>> level = levels.next();
			CounterResultListener<Void> lis = new CounterResultListener<>(level.size(), new IResultListener<Void>()
			{
				public void resultAvailable(Void result)
				{
					startComponents(cms, levels, names).addResultListener(new DelegationResultListener<>(ret));
				}

				public void exceptionOccurred(Exception exception)
				{
					System.out.println("ex: "+exception);
					startComponents(cms, levels, names).addResultListener(new DelegationResultListener<>(ret));
				}
			});
			for(Class<?> c: level)
			{
				//ITuple2Future<IComponentIdentifier, Map<String, Object>> fut = cms.createComponent(names.get(c), c.getName()+".class", (CreationInfo)null);
				//fut.addTuple2ResultListener(res -> {lis.resultAvailable(null);}, res -> {});
				
				IFuture<IComponentIdentifier> fut = cms.createComponent(names.get(c), c.getName()+".class", null, null);
				fut.addResultListener(res -> {lis.resultAvailable(null);});
				
//				System.out.println("Auto starting: "+names.get(c));
			}
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Called when platform startup finished.
	 */
	// BUG: currently not called because CMS calls it and platform is not created via cms
//	@AgentBody
//	public void body()
//	{
//		System.out.println("Start scanning...");
//	}
}
