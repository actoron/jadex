package jadex.platform;

import static jadex.base.IPlatformConfiguration.LOGGING_LEVEL;
import static jadex.base.IPlatformConfiguration.PLATFORMPROXIES;
import static jadex.base.IPlatformConfiguration.UNIQUEIDS;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.DependencyResolver;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.commons.IFilter;
import jadex.commons.SClassReader;
import jadex.commons.SClassReader.AnnotationInfo;
import jadex.commons.SClassReader.ClassInfo;
import jadex.commons.SClassReader.EnumInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.security.SSecurity;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.platform.service.execution.AsyncExecutionService;
import jadex.platform.service.execution.BisimExecutionService;
import jadex.platform.service.execution.SyncExecutionService;
import jadex.platform.service.security.SecurityAgent;

/**
 *	Basic standalone platform services provided as a micro agent. 
 */
@Arguments(
{
	@Argument(name=LOGGING_LEVEL, clazz=Level.class, defaultvalue="java.util.logging.Level.SEVERE"),
	@Argument(name=UNIQUEIDS, clazz=boolean.class, defaultvalue="true"),
	@Argument(name=PLATFORMPROXIES, clazz=boolean.class, defaultvalue="true")
})

@ProvidedServices({
	@ProvidedService(type=IThreadPoolService.class, scope=ServiceScope.PLATFORM, implementation=@Implementation(expression="new jadex.platform.service.threadpool.ThreadPoolService($args.threadpoolclass!=null ? jadex.commons.SReflect.classForName0($args.threadpoolclass, jadex.commons.SReflect.class.getClassLoader()).newInstance() : new jadex.commons.concurrent.JavaThreadPool(false), $component.getId())", proxytype=Implementation.PROXYTYPE_RAW)),
	// hack!!! no daemon here (possibly fixed?)
	@ProvidedService(type=IDaemonThreadPoolService.class, scope=ServiceScope.PLATFORM, implementation=@Implementation(expression="new jadex.platform.service.threadpool.ThreadPoolService($args.threadpoolclass!=null ? jadex.commons.SReflect.classForName0($args.threadpoolclass, jadex.commons.SReflect.class.getClassLoader()).newInstance() : new jadex.commons.concurrent.JavaThreadPool(true), $component.getId())", proxytype=Implementation.PROXYTYPE_RAW)),
	@ProvidedService(type=IExecutionService.class, scope=ServiceScope.PLATFORM, implementation=@Implementation(expression="PlatformAgent.createExecutionServiceImpl($args.asyncexecution, $args.simulation, $args.bisimulation, $component)", proxytype=Implementation.PROXYTYPE_RAW)),
//	@ProvidedService(type=IComponentManagementService.class, name="cms", implementation=@Implementation(expression="new jadex.bridge.service.types.cms.ComponentManagementService($platformaccess, $bootstrapfactory, $args.uniqueids)"))
})

@RequiredServices(
{
	@RequiredService(name="factoryservices", type=IComponentFactory.class, multiple=true)//, binding=@Binding(scope=Binding.SCOPE_PLATFORM))
})

@Properties(
{
	@NameValue(name="componentviewer.viewerclass", value="jadex.commons.SReflect.classForName0(\"jadex.base.gui.componentviewer.DefaultComponentServiceViewerPanel\", jadex.platform.service.library.LibraryService.class.getClassLoader())"),
	@NameValue(name="logging.level", value="$args.logging ? java.util.logging.Level.INFO : $args.logginglevel")
})
@Agent
public class PlatformAgent
{
	/** Boolean if platform proxies should be created. */
	@AgentArgument
	protected boolean platformproxies;
	
	//-------- service creation helpers --------
	
	/** Create execution service. */
	public static synchronized IExecutionService	createExecutionServiceImpl(Object asyncexecution, Object simulation, Object bisimulation, IInternalAccess component)
	{
		if(Boolean.TRUE.equals(bisimulation))
		{
			return BisimExecutionService.getInstance(component);
		}
		else
		{
			boolean	sync	= Boolean.FALSE.equals(asyncexecution) || Boolean.TRUE.equals(simulation);
			return sync ? new SyncExecutionService(component) : new AsyncExecutionService(component);
		}
	}
	
	//-------- static part --------
	
	/** Filter for finding agents to be auto-started. */
	protected static IFilter<SClassReader.ClassInfo>	filter	= new IFilter<SClassReader.ClassInfo>()
	{
		public boolean filter(ClassInfo ci)
		{
			boolean ret = false;
			AnnotationInfo ai = ci.getAnnotation(Agent.class.getName());
			if(ai!=null)
			{
				AnnotationInfo aai = (AnnotationInfo)ai.getValue("autostart");
				if(aai!=null)
				{
					EnumInfo ei = (EnumInfo)aai.getValue("value");
					String val = ei.getValue();
					ret = val==null? false: "true".equals(val.toLowerCase()) || "false".equals(val.toLowerCase()); // include all which define the value (false can be overridden from args)
				}
			}
			return ret;
		}
	};
	
	@Agent
	protected IInternalAccess agent;
	
	// enable startup monkey for randomized sequential component startup (dependency testing).
	boolean STARTUP_MONKEY	= false;
	
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
		Future<Void> ret = new Future<>();
//		System.out.println("Start scanning...");
		long start = System.currentTimeMillis();
				
		// Class name -> instance name
		Map<String, String> names = new HashMap<String, String>();
		DependencyResolver<String> dr = new DependencyResolver<String>();

		URL[] urls = new URL[0];
		ClassLoader classloader = PlatformAgent.class.getClassLoader();
		if(classloader instanceof URLClassLoader)
			urls = ((URLClassLoader)classloader).getURLs();
//		System.out.println("urls: "+urls.length);
		
		Set<ClassInfo> cis = SReflect.scanForClassInfos(urls, null, filter);
		Set<String>	comps	= new LinkedHashSet<>();
		
		for(ClassInfo ci: cis)
		{
//			System.out.println("Found: "+ci.getClassname());
//			Class<?> clazz = SReflect.findClass0(ci.getClassname(), null, classloader);
//			if(clazz==null)
//				agent.getLogger().warning("Could not load agent class: "+ci.getClassname());
//			else
				addComponentToLevels(dr, ci, names, comps);
		}
		
//		System.out.println("cls: "+files.size()+" "+components.size());
//		System.out.println("Scanning files needed: "+(System.currentTimeMillis()-start)/1000);
		
		Collection<Set<String>> levels = dr.resolveDependenciesWithLevel();
//		System.out.println("levels: "+levels);
//		System.out.println("names: "+names);
		
		startComponents(levels.iterator(), names, comps).addResultListener(new DelegationResultListener<Void>(ret)
		{
			@Override
			public void customResultAvailable(Void result)
			{
				if(platformproxies)
					addQueryForPlatformProxies();
				super.customResultAvailable(result);
			}
		});
		return ret;
	}
	
	/**
	 *  Add query for creating platform proxies.
	 */
	protected void addQueryForPlatformProxies()
	{
//		System.out.println("creating platform proxies for remote platforms");
		
		// scope network or global?!
		ISubscriptionIntermediateFuture<IExternalAccess> query = agent.addQuery(new ServiceQuery<>(IExternalAccess.class)
			.setScope(ServiceScope.NETWORK));
//					.setScope(ServiceScope.GLOBAL));
		query.addResultListener(new IIntermediateResultListener<IExternalAccess>()
		{
			public void intermediateResultAvailable(IExternalAccess result)
			{
				try
				{
					if(!result.getId().getRoot().equals(agent.getId().getRoot()))
					{
						System.out.println("found platform: "+result.getId());//+" "+SComponentManagementService.containsComponent(result.getId()));
						Map<String, Object> args = new HashMap<>();
						args.put("component", result.getId());
						agent.createComponent(new CreationInfo().setFilename("jadex.platform.service.remote.ProxyAgent.class")
							.setArguments(args).setName(result.getId().toString()));
					}
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}

			public void finished()
			{
			}
			
			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
			}
			
			public void resultAvailable(Collection<IExternalAccess> result)
			{
			}
		});
	}
	
	/**
	 *  Add a components to the dependency resolver to build start levels.
	 *  Components of the same level can be started in parallel.
	 */
	protected void addComponentToLevels(DependencyResolver<String> dr, ClassInfo ci, Map<String, String> names, Set<String> comps)
	{
		try
		{
//			System.out.println("Found Agent annotation on class: "+ cl.getName());
//			Agent aan = cl.getAnnotation(Agent.class);
//			Autostart autostart = aan.autostart();
//			if(autostart.value().toBoolean()!=null)
//			{		
				AnnotationInfo ai = ci.getAnnotation(Agent.class.getName());
				AnnotationInfo autostart = (AnnotationInfo)ai.getValue("autostart");
				
				String name = autostart.getValue("name")==null || ((String)autostart.getValue("name")).length()==0? null: (String)autostart.getValue("name");
//				String name = autostart.name().length()==0? null: autostart.name();
				
				AnnotationInfo aai = (AnnotationInfo)ai.getValue("autostart");
				
				EnumInfo ei = (EnumInfo)aai.getValue("value");
				String val = ei.getValue();
				boolean ok = "true".equals(val.toLowerCase()); 
				if(name!=null)
				{
					Boolean start = getAgentStart(name);
					if(start!=null)
						ok = start.booleanValue();
				}
				else
				{
					// check classname as parameter
//					name = SReflect.getInnerClassName(cl);
					name = SReflect.getUnqualifiedTypeName(ci.getClassName());
					
					if(getAgentStart(name.toLowerCase())!=null)
					{	
						ok = getAgentStart(name.toLowerCase());
					}
					else
					{
						// check classname - suffix (BDI/Agent etc) in lowercase
						int suf = SUtil.inndexOfLastUpperCaseCharacter(name);
						if(suf>0)
						{
							name = name.substring(0, suf).toLowerCase();
							if(getAgentStart(name)!=null)
							{	
								ok = getAgentStart(name);
							}
						}
					}
				}
				
//				if(ci.getClassName().toLowerCase().indexOf("super")!=-1)
//				{
//					System.out.println("deac: "+ci.getClassName());
//					ok = false;
//				}
				
				if(ok)
				{
					String cname = ci.getClassName();
					comps.add(cname);
					dr.addNode(cname);
					
					Object[] pres = (Object[])autostart.getValue("predecessors");
					if(pres!=null)
					{
						for(Object pre: pres)
						{
							// Object as placeholder for no deps, because no entries should not mean no deps
							if(!Object.class.getName().equals(pre))
								dr.addDependency(cname, (String)pre);
						}
					}
					
					Object[] sucs = (Object[])autostart.getValue("successors");
					if(sucs!=null)
					{
						for(Object suc: sucs)
							dr.addDependency((String)suc, cname);
					}
					
					// if no predecessors are defined add SecurityAgent
					if(pres==null || pres.length==0)
						dr.addDependency(cname, SecurityAgent.class.getName());
					
					names.put(cname, name);
				}
//				else
//				{
//					System.out.println("Not starting: "+name);
//				}
//			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	/**
	 *  Get the config/argument value of an agent name.
	 */
	protected Boolean getAgentStart(String name)
	{		
//		Boolean ret = null;
//		IPlatformConfiguration config = (IPlatformConfiguration)Starter.getPlatformValue(agent.getId().getRoot(), IPlatformConfiguration.PLATFORMCONFIG);
//		if(config.getValue(name, agent.getModel())!=null)
//			ret = (boolean)config.getValue(name, agent.getModel());
//		
//		if(name.indexOf("jcc")!=-1)
//			System.out.println("getAgentStart: "+name+" "+ret);
//		
//		return ret;
		
		Map<String, Object> argsmap = (Map<String, Object>)Starter.getPlatformValue(agent.getId(), IPlatformConfiguration.PLATFORMARGS);
		if(argsmap.containsKey(name))
			return (Boolean)argsmap.get(name);
		return null;
	}
		
	/**
	 *  Start components in levels.
	 */
	protected IFuture<Void> startComponents(Iterator<Set<String>> levels, Map<String, String> names, Set<String> comps)
	{
		if(STARTUP_MONKEY)
			return startComponentsDebug(levels, null, names, comps);
		
		final Future<Void> ret = new Future<>();
		
		if(levels.hasNext())
		{
//			System.out.println("---------- LEVEL --------------");
			Set<String> level = levels.next();
			CounterResultListener<Void> lis = new CounterResultListener<>(level.size(), new IResultListener<Void>()
			{
				public void resultAvailable(Void result)
				{
					startComponents(levels, names, comps).addResultListener(new DelegationResultListener<>(ret));
				}

				public void exceptionOccurred(Exception exception)
				{
					agent.getLogger().warning(SUtil.getExceptionStacktrace(exception));
					startComponents(levels, names, comps).addResultListener(new DelegationResultListener<>(ret));
				}
			});
			
			for(String c: level)
			{
				if(comps.contains(c))
				{
					//ITuple2Future<IComponentIdentifier, Map<String, Object>> fut = cms.createComponent(names.get(c), c.getName()+".class", (CreationInfo)null);
					//fut.addTuple2ResultListener(res -> {lis.resultAvailable(null);}, res -> {});
					
					CreationInfo ci = new CreationInfo();
					ci.setName(names.get(c));
					ci.setFilename(c+".class");
					IFuture<IExternalAccess> fut = agent.createComponent(ci, null);
					fut.addResultListener(
						res -> {
//							System.out.println("Auto started: "+c+", "+names.get(c));
							lis.resultAvailable(null);
						},
						exception -> {
//							System.out.println("Auto start failed: "+c+", "+names.get(c)+", "+exception);
							lis.exceptionOccurred(new RuntimeException("Cannot autostart "+c+".class", exception));
						});
					
//					System.out.println("Auto starting: "+c+", "+names.get(c));
				}
				else
				{
					lis.resultAvailable(null);					
				}
			}
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}

	/**
	 *  Start components synchronized using random order to find implicit dependencies. 
	 */
	protected IFuture<Void> startComponentsDebug(Iterator<Set<String>> levels, Iterator<String> level, Map<String, String> names, Set<String> comps)
	{
		// Initial level or finished with last level -> start next level
		if(level==null || !level.hasNext())
		{
			if(levels.hasNext())
			{
				//			System.out.println("---------- LEVEL --------------");
				// Chaos monkey -> randomize list of components to find implicit dependencies
				List<String>	list	= new ArrayList<>(levels.next());
				Collections.shuffle(list, SSecurity.getSecureRandom());
				return startComponentsDebug(levels, list.iterator(), names, comps);
			}
			else
			{
				return IFuture.DONE;
			}
		}
		
		// level!=null && level.hasNext() -> Start next component in level
		else
		{
			Future<Void>	ret	= new Future<>();
			
			String	c	= level.next();
			if(comps.contains(c))
			{
				IFuture<IExternalAccess> fut = agent.createComponent(new CreationInfo().setName(names.get(c)).setFilename(c+".class"), null);
				fut.addResultListener(
					res -> startComponentsDebug(levels, level, names, comps).addResultListener(new DelegationResultListener<>(ret)),
					exception -> ret.setException(new RuntimeException("Cannot autostart "+c+".class", exception)));
			}
			else
			{
				startComponentsDebug(levels, level, names, comps).addResultListener(new DelegationResultListener<>(ret));
			}
			return ret;
		}
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

	// todo?! remove platform proxy query on termination 
	// BUG: currently not called
//	@AgentTerminated
//	public void terminated()
//	{
//		if(query!=null)
//			query.terminate();
//	}
}
