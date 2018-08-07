package jadex.platform;

import static jadex.base.IPlatformConfiguration.LOGGING_LEVEL;
import static jadex.base.IPlatformConfiguration.UNIQUEIDS;

import java.net.URL;
import java.net.URLClassLoader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
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
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
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
	@ProvidedService(type=IThreadPoolService.class, scope=RequiredService.SCOPE_PLATFORM, implementation=@Implementation(expression="new jadex.platform.service.threadpool.ThreadPoolService($args.threadpoolclass!=null ? jadex.commons.SReflect.classForName0($args.threadpoolclass, jadex.commons.SReflect.class.getClassLoader()).newInstance() : new jadex.commons.concurrent.JavaThreadPool(false), $component.getId())", proxytype=Implementation.PROXYTYPE_RAW)),
	// hack!!! no daemon here (possibly fixed?)
	@ProvidedService(type=IDaemonThreadPoolService.class, scope=RequiredService.SCOPE_PLATFORM, implementation=@Implementation(expression="new jadex.platform.service.threadpool.ThreadPoolService($args.threadpoolclass!=null ? jadex.commons.SReflect.classForName0($args.threadpoolclass, jadex.commons.SReflect.class.getClassLoader()).newInstance() : new jadex.commons.concurrent.JavaThreadPool(true), $component.getId())", proxytype=Implementation.PROXYTYPE_RAW)),
	@ProvidedService(type=IExecutionService.class, scope=RequiredService.SCOPE_PLATFORM, implementation=@Implementation(expression="($args.asyncexecution!=null && !$args.asyncexecution.booleanValue()) || ($args.asyncexecution==null && $args.simulation!=null && $args.simulation.booleanValue())? new jadex.platform.service.execution.SyncExecutionService($component): new jadex.platform.service.execution.AsyncExecutionService($component)", proxytype=Implementation.PROXYTYPE_RAW)),
	@ProvidedService(type=IComponentManagementService.class, name="cms", implementation=@Implementation(expression="new jadex.platform.service.cms.ComponentManagementService($platformaccess, $bootstrapfactory, $args.uniqueids)"))
})

@RequiredServices(
{
	@RequiredService(name="factoryservices", type=IComponentFactory.class, multiple=true)//, binding=@Binding(scope=Binding.SCOPE_PLATFORM))
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
//		System.out.println("Start scanning...");
		long start = System.currentTimeMillis();
		
		// Class name -> instance name
		Map<String, String> names = new HashMap<String, String>();
		DependencyResolver<String> dr = new DependencyResolver<String>();

		URL[] urls = new URL[0];
		ClassLoader classloader = PlatformAgent.class.getClassLoader();
		if(classloader instanceof URLClassLoader)
			urls = ((URLClassLoader)classloader).getURLs();
		
		Set<ClassInfo> cis = SReflect.scanForClassInfos(urls, null, new IFilter<SClassReader.ClassInfo>()
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
//						if(val.toBoolean()!=null)
//							ret = val.toBoolean().booleanValue();
						ret = val==null? false: "true".equals(val.toLowerCase()) || "false".equals(val.toLowerCase()); // include all which define the value (false can be overrided from args)
					}
				}
				return ret;
			}
		});
		
		for(ClassInfo ci: cis)
		{
//			System.out.println("Found: "+ci.getClassname());
//			Class<?> clazz = SReflect.findClass0(ci.getClassname(), null, classloader);
//			if(clazz==null)
//				agent.getLogger().warning("Could not load agent class: "+ci.getClassname());
//			else
				addComponentToLevels(dr, ci, names);
		}
		
//		System.out.println("cls: "+files.size()+" "+components.size());
//		System.out.println("Scanning files needed: "+(System.currentTimeMillis()-start)/1000);
		
		Collection<Set<String>> levels = dr.resolveDependenciesWithLevel();
		
		IComponentManagementService cms = agent.getFeature(IRequiredServicesFeature.class).getLocalService(IComponentManagementService.class);
		
		return startComponents(cms, levels.iterator(), names);
	}
	
	/**
	 *  Add a components to the dependency resolver to build start levels.
	 *  Components of the same level can be started in parallel.
	 */
	protected void addComponentToLevels(DependencyResolver<String> dr, ClassInfo ci, Map<String, String> names)
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
				
				if(ok)
				{
					String cname = ci.getClassName();
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
				else
				{
//					System.out.println("Not starting: "+name);
				}
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
	
	// enable startup chaos monkey
//	boolean CHAOSMONKEY_STARTUP	= true;
	
	/**
	 *  Start components in levels.
	 */
	protected IFuture<Void> startComponents(IComponentManagementService cms, Iterator<Set<String>> levels, Map<String, String> names)
	{
		// Totally broken, does not wait for startups,
		// also, why? levels are already Set?
//		if(CHAOSMONKEY_STARTUP)
//		{
//			return startComponentsDebug(cms, levels, null, names);
//		}
		
		final Future<Void> ret = new Future<>();
		
		if(levels.hasNext())
		{
//			System.out.println("---------- LEVEL --------------");
			Set<String> level = levels.next();
			CounterResultListener<Void> lis = new CounterResultListener<>(level.size(), new IResultListener<Void>()
			{
				public void resultAvailable(Void result)
				{
					startComponents(cms, levels, names).addResultListener(new DelegationResultListener<>(ret));
				}

				public void exceptionOccurred(Exception exception)
				{
					agent.getLogger().warning(SUtil.getExceptionStacktrace(exception));
					startComponents(cms, levels, names).addResultListener(new DelegationResultListener<>(ret));
				}
			});
			
			for(String c: level)
			{
				//ITuple2Future<IComponentIdentifier, Map<String, Object>> fut = cms.createComponent(names.get(c), c.getName()+".class", (CreationInfo)null);
				//fut.addTuple2ResultListener(res -> {lis.resultAvailable(null);}, res -> {});
				
				IFuture<IComponentIdentifier> fut = cms.createComponent(names.get(c), c+".class", null, null);
				fut.addResultListener(
					res -> {lis.resultAvailable(null);},
					exception -> {lis.exceptionOccurred(new RuntimeException("Cannot autostart "+c+".class", exception));});
				
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
	 *  Start components synhcronized using random order to find implicit dependencies. 
	 */
//	protected IFuture<Void> startComponentsDebug(IComponentManagementService cms, Iterator<Set<String>> levels, Iterator<String> level, Map<String, String> names)
//	{
//		// Initial level or finished with last level -> start next level
//		if(level==null || !level.hasNext())
//		{
//			if(levels.hasNext())
//			{
//				//			System.out.println("---------- LEVEL --------------");
//				// Chaos monkey -> randomize list of components to find implicit dependencies
//				List<String>	list	= new ArrayList<>(levels.next());
//				Collections.shuffle(list, SSecurity.getSecureRandom());
//				return startComponentsDebug(cms, levels, list.iterator(), names);
//			}
//			else
//			{
//				return IFuture.DONE;
//			}
//		}
//		
//		// level!=null && level.hasNext() -> Start next component in level
//		else
//		{
//			Future<Void>	ret	= new Future<>();
//			
//			String	c	= level.next();
//			IFuture<IComponentIdentifier> fut = cms.createComponent(names.get(c), c+".class", null, null);
//			fut.addResultListener(
//				res -> {startComponentsDebug(cms, levels, level, names).addResultListener(new DelegationResultListener<>(ret));},
//				exception -> {ret.setException(new RuntimeException("Cannot autostart "+c+".class", exception));});
//			return ret;
//		}
//	}

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
