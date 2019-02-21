package jadex.platform;

import static jadex.base.IPlatformConfiguration.LOGGING_LEVEL;
import static jadex.base.IPlatformConfiguration.PLATFORMPROXIES;
import static jadex.base.IPlatformConfiguration.UNIQUEIDS;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
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
import jadex.bridge.VersionInfo;
import jadex.bridge.component.DependencyResolver;
import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceQuery.Multiplicity;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.registryv2.ISearchQueryManagerService;
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
	protected static IFilter<SClassReader.ClassInfo> filter = new IFilter<SClassReader.ClassInfo>()
	{
		public boolean filter(ClassInfo ci)
		{
			boolean ret = false;
			AnnotationInfo ai = ci.getAnnotation(Agent.class.getName());
			if(ai!=null)
			{
				EnumInfo ei = (EnumInfo)ai.getValue("autostart");
				if(ei!=null)
				{
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
	 * 
	 * @return
	 */
	public static URL[] getClasspathUrls(ClassLoader classloader)
	{
		Set<URL> urlset = new HashSet<>();
		String[] cpaths = System.getProperty("java.class.path").split(System.getProperty("path.separator"));
		if(cpaths != null)
		{
			for(String cpath : cpaths)
			{
				try
				{
					File file = new File(cpath);
					if (file.exists())
					{
						URL url = file.toURI().toURL();
						urlset.add(url);
					}
				}
				catch(Exception e)
				{
				}
			}
		}
		
		if(classloader instanceof URLClassLoader)
			urlset.addAll(Arrays.asList(((URLClassLoader)classloader).getURLs()));
		URL[] urls = urlset.toArray(new URL[urlset.size()]);
		return urls;
	}
	
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

		URL[] urls = getClasspathUrls(PlatformAgent.class.getClassLoader());
		// Remove JVM jars
		urls = SUtil.removeSystemUrls(urls);
		
		Set<ClassInfo> cis = SReflect.scanForClassInfos(urls, null, filter);
		
		List<CreationInfo> infos = new ArrayList<>();
		for(ClassInfo ci : cis)
		{
			if(ci.getLastModified() != null && ci.getClassName().startsWith("jadex."))
			{
				VersionInfo vinfo = VersionInfo.getInstance();
				synchronized(vinfo)
				{
					if(vinfo.getBuildTime().before(ci.getLastModified()))
						vinfo.setBuildTime(ci.getLastModified());
				}
			}
			isSystemComponent(ci, PlatformAgent.class.getClassLoader());
			AnnotationInfo ai = ci.getAnnotation(Agent.class.getName());
			EnumInfo ei = (EnumInfo)ai.getValue("autostart");
			String val = ei.getValue();
			boolean ok = "true".equals(val.toLowerCase());
			String name = ai.getValue("name")==null || ((String)ai.getValue("name")).length()==0? null: (String)ai.getValue("name");
			if(name!=null)
			{
				Boolean agentstart = getAgentStart(name);
				if(agentstart!=null)
					ok = agentstart.booleanValue();
			}
			else
			{
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
			
			if (ok)
			{
				CreationInfo info = new CreationInfo();
				info.setName(name);
				info.setFilename(ci.getClassName()+".class");
				
				infos.add(info);
			}
		}
		agent.getFeature(ISubcomponentsFeature.class).createComponents(infos.toArray(new CreationInfo[infos.size()])).addResultListener(new IResultListener<Collection<IExternalAccess>>()
		{
			public void resultAvailable(Collection<IExternalAccess> result)
			{
				if(platformproxies)
					addQueryForPlatformProxies();
				
				ret.setResult(null);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setException(exception);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Add query for creating platform proxies.
	 */
	protected void addQueryForPlatformProxies()
	{
		// No query when no search query manager service
		if(agent.searchLocalService(new ServiceQuery<>(ISearchQueryManagerService.class).setMultiplicity(Multiplicity.ZERO_ONE))==null)
		{
			return;
		}
		
//		System.out.println("creating platform proxies for remote platforms");
		
		// scope network or global?!
		ISubscriptionIntermediateFuture<IExternalAccess> query = agent.addQuery(new ServiceQuery<>(IExternalAccess.class)
			.setScope(ServiceScope.NETWORK));
//			.setScope(ServiceScope.GLOBAL));
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
				
				String name = ai.getValue("name")==null || ((String)ai.getValue("name")).length()==0? null: (String)ai.getValue("name");
//				String name = autostart.name().length()==0? null: autostart.name();
				
				EnumInfo ei = (EnumInfo)ai.getValue("autostart");
				
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
					
					Object[] pres = (Object[])ai.getValue("predecessors");
					if(pres!=null)
					{
						for(Object pre: pres)
						{
							// Object as placeholder for no deps, because no entries should not mean no deps
							if(!Object.class.getName().equals(pre))
								dr.addDependency(cname, (String)pre);
						}
					}
					
					Object[] sucs = (Object[])ai.getValue("successors");
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
		Map<String, Object> argsmap = (Map<String, Object>)Starter.getPlatformValue(agent.getId(), IPlatformConfiguration.PLATFORMARGS);
		if(argsmap.containsKey(name))
			return (Boolean)argsmap.get(name);
		return null;
	}
	
	/**
	 *  Statically checks a class if it is a system component.
	 *  
	 *  @param ci The class info from SClassReader
	 *  @param cl The class loader.
	 *  @return True, if the component is a system component.
	 */
	protected static final boolean isSystemComponent(ClassInfo ci, ClassLoader cl)
	{
		AnnotationInfo provservsinfo = ci.getAnnotation(ProvidedServices.class.getName());
		if (provservsinfo != null)
		{
			Object[] provservs = (Object[]) provservsinfo.getValue("value");
			if (provservs != null)
			{
				for (Object provserv : provservs)
				{
					AnnotationInfo provservinfo = (AnnotationInfo) provserv;
					String ifacename = ((ClassInfo) provservinfo.getValue("type")).getClassName();
					if (isSystemInterface(ifacename, cl))
					{
	//					System.out.println("System because of provided service declaration: " + ci);
						return true;
					}
				}
			}
		}
		
		boolean autoprovide = false;
		ClassInfo curci = ci;
		while (curci != null)
		{
			AnnotationInfo agentinfo = curci.getAnnotation(Agent.class.getName());
			if (agentinfo != null)
			{
				EnumInfo ap = (EnumInfo) agentinfo.getValue("autoprovide");
				if (ap != null && !"NULL".equals(ap.getValue()))
				{
					autoprovide = "TRUE".equals(ap.getValue()) ? true : false;
					break;
				}
				
			}
			String scn = curci.getSuperClassName();
			if (scn != null)
				curci = SClassReader.getClassInfo(scn, cl);
			else
				curci = null;
		}
		
		if (autoprovide)
		{
			curci = ci;
			while (curci != null)
			{
				List<String> ifaces = curci.getInterfaceNames();
				for (String ifacename : SUtil.notNull(ifaces))
				{
					if (isSystemInterface(ifacename, cl))
					{
//						System.out.println("System because of autoprovide: " + ci);
						return true;
					}
				}
				String scn = curci.getSuperClassName();
				if (scn != null)
					curci = SClassReader.getClassInfo(scn, cl);
				else
					curci = null;
			}
		}
		
		return false;
	}
	
	/**
	 *  Checks if an interface or any superinterface has the system property.
	 *  
	 *  @param ifacename Interface name.
	 *  @param cl Class loader.
	 *  @return True, if system.
	 */
	protected static final boolean isSystemInterface(String ifacename, ClassLoader cl)
	{
		ClassInfo iface = SClassReader.getClassInfo(ifacename, cl);
		AnnotationInfo sinfo = iface.getAnnotation(Service.class.getName());
		if (sinfo != null)
		{
			Boolean sys = (Boolean) sinfo.getValue("system");
			if (Boolean.TRUE.equals(sys))
				return true;
		}
		
		List<String> superifaces = iface.getInterfaceNames();
		for (String superiface : SUtil.notNull(superifaces))
		{
			if (isSystemInterface(superiface, cl))
				return true;
		}
		
		return false;
	}
}
