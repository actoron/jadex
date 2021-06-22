package jadex.platform;

import static jadex.base.IPlatformConfiguration.LOGGING_LEVEL;
import static jadex.base.IPlatformConfiguration.PLATFORMPROXIES;
import static jadex.base.IPlatformConfiguration.UNIQUEIDS;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.logging.Level;
import java.util.stream.Collectors;

import jadex.base.IPlatformConfiguration;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.VersionInfo;
import jadex.bridge.component.DependencyResolver;
import jadex.bridge.component.ISubcomponentsFeature;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.annotation.OnInit;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.search.ServiceQuery.Multiplicity;
import jadex.bridge.service.types.clock.IClock;
import jadex.bridge.service.types.clock.IClockService;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.execution.IExecutionService;
import jadex.bridge.service.types.factory.IComponentFactory;
import jadex.bridge.service.types.library.IDependencyService;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.bridge.service.types.registry.ISearchQueryManagerService;
import jadex.bridge.service.types.settings.ISettingsService;
import jadex.bridge.service.types.threadpool.IDaemonThreadPoolService;
import jadex.bridge.service.types.threadpool.IThreadPoolService;
import jadex.commons.IFilter;
import jadex.commons.SClassReader;
import jadex.commons.SClassReader.AnnotationInfo;
import jadex.commons.SClassReader.ClassInfo;
import jadex.commons.SClassReader.EnumInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.concurrent.IThreadPool;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.IntermediateEmptyResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.platform.service.clock.ClockCreationInfo;
import jadex.platform.service.clock.ClockService;
import jadex.platform.service.execution.AsyncExecutionService;
import jadex.platform.service.execution.SyncExecutionService;
import jadex.platform.service.security.SecurityAgent;
import jadex.platform.service.settings.SettingsService;
import jadex.platform.service.threadpool.ThreadPoolService;

/**
 *	Basic standalone platform services provided as a micro agent. 
 */
@Arguments(
{
	@Argument(name=LOGGING_LEVEL, clazz=Level.class, defaultvalue="java.util.logging.Level.SEVERE"),
	@Argument(name=UNIQUEIDS, clazz=boolean.class, defaultvalue="true"),
	@Argument(name=PLATFORMPROXIES, clazz=boolean.class, defaultvalue="true"),
	@Argument(name="simulation", clazz=boolean.class, defaultvalue="false"),
	
	// Added as argument here, due to cross cutting concern (hack?)
	@Argument(name="showversion", clazz=boolean.class, defaultvalue="true", description="Expose the Jadex version where appropriate (e.g. REST headers).")
})

@ProvidedServices({
	@ProvidedService(type=IThreadPoolService.class, scope=ServiceScope.PLATFORM, implementation=@Implementation(expression="PlatformAgent.createThreadPoolServiceImpl($component)", proxytype=Implementation.PROXYTYPE_RAW)),
	// hack!!! no daemon here (possibly fixed?)
	@ProvidedService(type=IDaemonThreadPoolService.class, scope=ServiceScope.PLATFORM, implementation=@Implementation(expression="new jadex.platform.service.threadpool.ThreadPoolService($args.threadpoolclass!=null ? jadex.commons.SReflect.classForName0($args.threadpoolclass, jadex.commons.SReflect.class.getClassLoader()).newInstance() : new jadex.commons.concurrent.JavaThreadPool(true), $component.getId())", proxytype=Implementation.PROXYTYPE_RAW)),
	@ProvidedService(type=IExecutionService.class, scope=ServiceScope.PLATFORM, implementation=@Implementation(expression="PlatformAgent.createExecutionServiceImpl($args.asyncexecution, $args.simulation, $component)", proxytype=Implementation.PROXYTYPE_RAW)),
	@ProvidedService(type=IClockService.class, scope=ServiceScope.PLATFORM, implementation=@Implementation(expression="PlatformAgent.createClockServiceImpl($component)", proxytype=Implementation.PROXYTYPE_RAW)),
	@ProvidedService(type=ISettingsService.class, scope=ServiceScope.PLATFORM, implementation=@Implementation(SettingsService.class)),
	@ProvidedService(type=ILibraryService.class, scope=ServiceScope.PLATFORM, implementation=@Implementation(expression="$args.libpath==null? new jadex.platform.service.library.LibraryService(): new jadex.platform.service.library.LibraryService(new java.net.URLClassLoader(jadex.commons.SUtil.toURLs($args.libpath), $args.baseclassloader==null ? jadex.platform.service.library.LibraryService.class.getClassLoader() : $args.baseclassloader))")),
	@ProvidedService(type=IDependencyService.class, scope=ServiceScope.PLATFORM, implementation=@Implementation(expression="$args.maven_dependencies? jadex.platform.service.dependency.maven.MavenDependencyResolverService.class.newInstance(): new jadex.platform.service.library.BasicDependencyService()"))

//	@ProvidedService(type=IComponentManagementService.class, name="cms", implementation=@Implementation(expression="new jadex.bridge.service.types.cms.ComponentManagementService($platformaccess, $bootstrapfactory, $args.uniqueids)"))
})

@RequiredServices(
{
	@RequiredService(name="factoryservices", type=IComponentFactory.class)// multiple=true , binding=@Binding(scope=Binding.SCOPE_PLATFORM))
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
	
	protected static final String STARTUP_CACHE_FILE = "platform_auto.conf";
	
	//-------- service creation helpers --------
	
	/** Create threadpool service. */
	public static IThreadPoolService	createThreadPoolServiceImpl(IInternalAccess component)
	{
		return createMaybeSharedServiceImpl("threadpool", component, () ->
		{
			String	threadpoolclass	= (String) component.getArgument("threadpoolclass");
			IThreadPool pool;
			try
			{
				pool = threadpoolclass!=null
					? (IThreadPool)SReflect.classForName0(threadpoolclass, component.getClassLoader()).getConstructor().newInstance()
					: new jadex.commons.concurrent.JavaThreadPool(false);
				return new ThreadPoolService(pool, component.getId());
			}
			catch (Exception e)
			{
				throw SUtil.throwUnchecked(e);
			}
		});
	}
	
	/** Create execution service. */
	public static IExecutionService createExecutionServiceImpl(Object asyncexecution, Object simulation, IInternalAccess component)
	{
		return createMaybeSharedServiceImpl("exe", component, () ->
			Boolean.FALSE.equals(asyncexecution) || Boolean.TRUE.equals(simulation)
				? new SyncExecutionService(component)
				: new AsyncExecutionService(component));
	}
	
	/** Create clock service. */
	public static IClockService createClockServiceImpl(IInternalAccess component)
	{
		return createMaybeSharedServiceImpl("clock", component, () ->
		{
			boolean	simulation	= component.getArgument("simulation")!=null && Boolean.TRUE.equals(component.getArgument("simulation"));
			return new ClockService(new ClockCreationInfo(simulation?IClock.TYPE_EVENT_DRIVEN:IClock.TYPE_SYSTEM, simulation?"simulation_clock":"system_clock", System.currentTimeMillis(), 100), component);
		});
	}
	
	/**
	 *  Create a service that may be shared between platforms using a shared service factory. 
	 */
	public static <T>	T	createMaybeSharedServiceImpl(String name, IInternalAccess ia, Supplier<T> creator)
	{
		@SuppressWarnings("unchecked")
		Function<Supplier<T>, T>	fac	= (Function<Supplier<T>, T>) ia.getArgument(name+"factory");
		if(fac!=null)
		{
			// When factory exists -> use factory (only creates service on first access)
			return fac.apply(creator);
		}
		else
		{
			// otherwise create new service for each platform.
			return creator.get();
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
	public boolean STARTUP_RANDOM = false;
	
	// where should the defaults be defined (here or in the config)
//	@Arguments
//	public static jadex.bridge.modelinfo.Argument[] getArguments()
//	{
//		return PlatformConfigurationHandler.getArguments();
//	}
	
	/**
	 *  Get the classpath urls.
	 *  @return The classpath urls.
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
	//@AgentCreated
	@OnInit
	public IFuture<Void> init()
	{
		Future<Void> ret = new Future<>();
//		System.out.println("Start scanning...");
//		long start = System.currentTimeMillis();
		
		String file = (String)agent.getArgument("startconfig");
		Boolean rescan = (Boolean)agent.getArgument("rescan");
		if(rescan!=null && rescan)
		{
			file = null;
		}
		else if(file==null)
		{
			File cache = new File(STARTUP_CACHE_FILE);
			if(cache.exists())
				file = cache.getAbsolutePath();
		}
		
		HashMap<File, Set<ClassInfo>> codesources = new HashMap<>();
		HashMap<File, Long> moddates = new HashMap<>();
		
		if(file!=null)
		{
			InputStream is = SUtil.getResource0(file, agent.getClassLoader());
			if(is!=null)
			{
				String[] conflines = SUtil.readStreamLines(is);
				
				File codesource=null;
				
				for(String line : conflines)
				{
					String tok = line.trim();
					if(tok.startsWith("//"))
					{
						System.out.println("Skipping: "+tok);
						continue;
					}
					
					if(tok.startsWith("##"))
					{
						String metastr = tok.substring(2).trim();
						if(metastr.length() > 0)
						{
							String[] split = metastr.split("\\|");
							if (split.length==2)
							{
								String codesourcestr = split[0].trim();
								codesource = new File(codesourcestr);
								long moddate = Long.valueOf(split[1].trim());
								moddates.put(codesource, moddate);
								if (!codesources.containsKey(codesource))
									codesources.put(codesource, null);
							}
							else
							{
								System.out.println("Cannot read meta info: "+tok + " " + split.length);
							}
						}
						else
						{
							System.out.println("Cannot read meta info: "+metastr);
						}
					}
					else
					{
						ClassInfo ci = SClassReader.getClassInfo(tok, agent.getClassLoader(), true, true);
						if(ci!=null)
						{
							Set<ClassInfo> cis = codesources.get(codesource);
							if(cis==null)
							{
								cis = new HashSet<>();
								codesources.put(codesource, cis);
							}
							cis.add(ci);
						}
						else
						{
							System.out.println("Cannot read system agent: "+tok);
						}
					}
				}
			}
			else
			{
				System.out.println("Cannot read startconfig: "+file);
			}
		}
		
		URL[] urls = getClasspathUrls(PlatformAgent.class.getClassLoader());
		// Remove JVM jars
		urls = SUtil.removeSystemUrls(urls);
		Set<File> cpfiles = Arrays.stream(urls).filter(url -> "file".equals(url.getProtocol())).map(url -> new File(SUtil.toURI(url))).collect(Collectors.toSet());
		Set<File> scanfiles = new HashSet<>(cpfiles);
		
		boolean writecache = false;
		for(Iterator<Map.Entry<File, Set<ClassInfo>>> it = codesources.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry<File, Set<ClassInfo>> entry = it.next();
			if (!cpfiles.contains(entry.getKey()) ||
				!entry.getKey().exists())
			{
				it.remove();
				scanfiles.remove(entry.getKey());
				writecache = true;
			}
			else if (entry.getKey().lastModified() == moddates.get(entry.getKey()))
			{
				scanfiles.remove(entry.getKey());
			}
		}
		
		//System.out.println("scanning: "+scanfiles);
		
		//System.out.println("Scan size: " + scanfiles.size());
		
		for(File f : scanfiles)
		{
			writecache = true;
			Set<ClassInfo> infos = SReflect.scanForClassInfos(new URL[] {SUtil.toURL(f.toURI())}, null, filter);
			codesources.put(f, infos);
		}
		
		Set<ClassInfo> cis = codesources.values().stream().filter(cscis -> cscis != null).flatMap(cscis -> cscis.stream()).collect(Collectors.toSet());
		
		//System.out.println("cis: "+cis);
		
		if(writecache)
		{
			File cache = new File(STARTUP_CACHE_FILE);
			try(OutputStream os = new BufferedOutputStream(new FileOutputStream(cache)))
			{
				for(Map.Entry<File, Set<ClassInfo>> entry : codesources.entrySet())
				{
					if(entry.getValue() != null)
					{
						String line = "## " + entry.getKey().getAbsolutePath() + " | " + entry.getKey().lastModified() + "\n";
						os.write(line.getBytes(SUtil.UTF8));
						for (ClassInfo inf : entry.getValue())
						{
							line = inf.getClassName() + "\n";
							os.write(line.getBytes(SUtil.UTF8));
						}
					}
				}
			}
			catch (Exception e)
			{
				agent.getLogger().warning("Failed to write startup cache: " + STARTUP_CACHE_FILE);
			}
		}
		
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
			//if(ci.getClassName().indexOf("BDI")!=-1)
			//	System.out.println("hhhfhjsdf");
			isSystemComponent(ci, PlatformAgent.class.getClassLoader());
			AnnotationInfo ai = ci.getAnnotation(Agent.class.getName());
			if(ai==null)
			{
				System.out.println("Failed to load component: "+ci);
				continue;
			}
			EnumInfo ei = (EnumInfo)ai.getValue("autostart");
			if(ei==null)
				System.out.println("No autostart component: "+ci);

			String name = ai.getValue("name")==null || ((String)ai.getValue("name")).length()==0? null: (String)ai.getValue("name");
			
			boolean ok = ei==null? true: "true".equals(ei.getValue().toLowerCase());
			
			Boolean agentstart = null;
			if(name!=null)
			{
				agentstart = getAgentStart(name);
				if(agentstart!=null)
					ok = agentstart.booleanValue();
			}
			
			if(agentstart==null)
			{
				String typename = SReflect.getUnqualifiedTypeName(ci.getClassName());
				
				if(getAgentStart(typename.toLowerCase())!=null)
				{	
					ok = getAgentStart(typename.toLowerCase());
					if(name==null)
						name = typename;
				}
				else
				{
					// check classname - suffix (BDI/Agent etc) in lowercase
					int suf = SUtil.inndexOfLastUpperCaseCharacter(typename);
					if(suf>0)
					{
						typename = typename.substring(0, suf).toLowerCase();
						if(getAgentStart(typename)!=null)
						{	
							ok = getAgentStart(typename);
						}
					}
				}
				
				// only set name, if was not explicitly set
				if(name==null)
					name = typename;
			}
			
			if(ok)
			{
				CreationInfo info = new CreationInfo();
				info.setName(name);
				info.setFilename(ci.getClassName()+".class");
				infos.add(info);
				//System.out.println("added agent to start: "+info.getFilename());
			}
		}
		
		//for(CreationInfo ci: infos)
		//	System.out.println("creating: "+ci);
		
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
		if(agent.getLocalService(new ServiceQuery<>(ISearchQueryManagerService.class).setMultiplicity(Multiplicity.ZERO_ONE))==null)
		{
			return;
		}
		
//		System.out.println("creating platform proxies for remote platforms");
		
		// scope network or global?!
		ISubscriptionIntermediateFuture<IExternalAccess> query = agent.addQuery(new ServiceQuery<>(IExternalAccess.class)
			.setScope(ServiceScope.NETWORK));
//			.setScope(ServiceScope.GLOBAL));
		query.addResultListener(new IntermediateEmptyResultListener<IExternalAccess>()
		{
			public void intermediateResultAvailable(IExternalAccess result)
			{
				try
				{
					if(!agent.getId().getRoot().equals(result.getId().getRoot()))
					{
						//System.out.println("found platform: "+result.getId());//+" "+SComponentManagementService.containsComponent(result.getId()));
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

			public void exceptionOccurred(Exception exception)
			{
				exception.printStackTrace();
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
				Boolean start = null;
				if(name!=null)
				{
					start = getAgentStart(name);
					if(start!=null)
						ok = start.booleanValue();
				}
				
				if(start==null)
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
		@SuppressWarnings("unchecked")
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
