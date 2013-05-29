package jadex.bdiv3;

import jadex.bdiv3.annotation.BDIConfiguration;
import jadex.bdiv3.annotation.BDIConfigurations;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Deliberation;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalInhibit;
import jadex.bdiv3.annotation.Goals;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.ServicePlan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.model.BDIModel;
import jadex.bdiv3.model.MBelief;
import jadex.bdiv3.model.MBody;
import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MConfiguration;
import jadex.bdiv3.model.MDeliberation;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.model.MTrigger;
import jadex.bdiv3.model.MethodInfo;
import jadex.bdiv3.runtime.impl.GoalDelegationHandler;
import jadex.bdiv3.runtime.impl.IServiceParameterMapper;
import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.LocalResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.ModelInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.ProvidedServiceImplementation;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Value;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.commons.FieldInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.micro.MicroClassReader;
import jadex.micro.MicroModel;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.Publish;
import jadex.micro.annotation.RequiredService;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *  Reads micro agent classes and generates a model from metainfo and annotations.
 */
public class BDIClassReader extends MicroClassReader
{
	/** The class generator. */
	protected IBDIClassGenerator gen;
	
	/**
	 *  Create a new bdi class reader.
	 */
	public BDIClassReader()
	{
//		this.gen = new JavassistBDIClassGenerator();
		this.gen = new ASMBDIClassGenerator();
	}
	
	/**
	 *  Load a  model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return The loaded model.
	 */
	public MicroModel read(String model, String[] imports, ClassLoader classloader, IResourceIdentifier rid, IComponentIdentifier root)
	{
		// use dummy classloader that will not be visisble outside
		List<URL> urls = SUtil.getClasspathURLs(classloader, false);
		DummyClassLoader cl = new DummyClassLoader((URL[])urls.toArray(new URL[urls.size()]), null, classloader);
		return super.read(model, imports, cl, rid, root);
	}
	
	/**
	 *  Load the model.
	 */
	protected BDIModel read(String model, Class<?> cma, ClassLoader cl, IResourceIdentifier rid, IComponentIdentifier root)
	{
		ClassLoader classloader = ((DummyClassLoader)cl).getOriginal();
		
		ModelInfo modelinfo = new ModelInfo();
		BDIModel ret = new BDIModel(modelinfo, new MCapability(cma.getName()));
		
		String name = SReflect.getUnqualifiedClassName(cma);
		if(name.endsWith(BDIModelLoader.FILE_EXTENSION_BDIV3_FIRST))
			name = name.substring(0, name.lastIndexOf(BDIModelLoader.FILE_EXTENSION_BDIV3_FIRST));
		String packagename = cma.getPackage()!=null? cma.getPackage().getName(): null;
//		modelinfo.setName(name+"BDI");
		modelinfo.setName(name);
		modelinfo.setPackage(packagename);
//		modelinfo.setFilename(model);
		String src = SUtil.convertURLToString(cma.getProtectionDomain().getCodeSource().getLocation());
		modelinfo.setFilename(src+File.separator+SReflect.getClassName(cma)+".class");
		modelinfo.setStartable(!Modifier.isAbstract(cma.getModifiers()));
		modelinfo.setType(BDIAgentFactory.FILETYPE_BDIAGENT);
		modelinfo.setResourceIdentifier(rid);
		ret.setClassloader(classloader); // use parent
		
//		System.out.println("filename: "+modelinfo.getFilename());
		
		if(rid==null)
		{
			URL url = cma.getProtectionDomain().getCodeSource().getLocation();
			rid = new ResourceIdentifier(new LocalResourceIdentifier(root, url), null);
		}
		modelinfo.setResourceIdentifier(rid);
		
		fillMicroModelFromAnnotations(ret, model, cma, cl);
		
		fillBDIModelFromAnnotations(ret, model, cma, cl);
		
		return ret;
	}
	
	/**
	 *  Fill the model details using annotation.
	 *  // called with dummy classloader (that was used to load cma first time)
	 */
	protected void fillBDIModelFromAnnotations(BDIModel bdimodel, String model, final Class<?> cma, ClassLoader cl)
	{
//		ModelInfo modelinfo = (ModelInfo)micromodel.getModelInfo();
		
//		System.out.println("todo: read bdi");
		
//		List<Field> beliefs = new ArrayList<Field>();
//		final Set<String> beliefnames = new HashSet<String>();
//		List<Class> goals = new ArrayList<Class>();
//		List<Method> plans = new ArrayList<Method>();
		
		Map<String, ConfigurationInfo> confs = new LinkedHashMap<String, ConfigurationInfo>();
		List<MConfiguration> bdiconfs = new ArrayList<MConfiguration>();
		boolean confdone = false;
		
		Map<ClassInfo, List<Tuple2<MGoal, String>>> pubs = new HashMap<ClassInfo, List<Tuple2<MGoal, String>>>();
		
		List<Class<?>> agtcls = new ArrayList<Class<?>>();
		Class<?> clazz = cma;
		while(clazz!=null && !clazz.equals(Object.class) && !clazz.equals(getClass(BDIAgent.class, cl)))
		{
			if(isAnnotationPresent(clazz, Agent.class, cl))
			{
				agtcls.add(0, clazz);
			}
			
			// Find external goals
			if(isAnnotationPresent(clazz, Goals.class, cl))
			{
				Goal[] goals = getAnnotation(clazz, Goals.class, cl).value();
				for(Goal goal: goals)
				{
					getMGoal(bdimodel, goal, goal.clazz(), cl, pubs);
				}
			}
			
			// Find inner goal and plan classes
			Class<?>[] cls = clazz.getDeclaredClasses();
			for(int i=0; i<cls.length; i++)
			{
				if(isAnnotationPresent(cls[i], Goal.class, cl))
				{
//					System.out.println("found belief: "+fields[i].getName());
					Goal goal = getAnnotation(cls[i], Goal.class, cl);
					getMGoal(bdimodel, goal, cls[i], cl, pubs);
				}
				if(isAnnotationPresent(cls[i], Plan.class, cl))
				{
//					System.out.println("found belief: "+fields[i].getName());
					Plan plan = getAnnotation(cls[i], Plan.class, cl);
					getMPlan(bdimodel, plan, null, new ClassInfo(cls[i].getName()), cl, pubs);
				}
			}
			
			// Find capabilities
			Field[] fields = clazz.getDeclaredFields();
			for(int i=0; i<fields.length; i++)
			{
				if(isAnnotationPresent(fields[i], Capability.class, cl))
				{
					System.out.println("found capability: "+fields[i].getName());
					agtcls.add(0, fields[i].getType());
				}
			}

			// Find beliefs
			for(int i=0; i<fields.length; i++)
			{
				if(isAnnotationPresent(fields[i], Belief.class, cl))
				{
//					System.out.println("found belief: "+fields[i].getName());
					Belief bel = getAnnotation(fields[i], Belief.class, cl);
					bdimodel.getCapability().addBelief(new MBelief(new FieldInfo(fields[i]), 
						bel.implementation().getName().equals(Object.class.getName())? null: bel.implementation().getName(),
						bel.dynamic(), bel.events().length==0? null: bel.events()));
//					beliefs.add(fields[i]);
//					beliefnames.add(fields[i].getName());
				}
			}
			
			// Find method plans
			Method[] methods = clazz.getDeclaredMethods();
			for(int i=0; i<methods.length; i++)
			{
				if(isAnnotationPresent(methods[i], Plan.class, cl))
				{
//					System.out.println("found plan: "+methods[i].getName());
					Plan p = getAnnotation(methods[i], Plan.class, cl);
					getMPlan(bdimodel, p, new MethodInfo(methods[i]), null, cl, pubs);
				}
				else if(isAnnotationPresent(methods[i], Belief.class, cl))
				{
					Belief bel = getAnnotation(methods[i], Belief.class, cl);
					
					String name = methods[i].getName().substring(3);
					name = name.substring(0, 1).toLowerCase()+name.substring(1);
					
					MBelief mbel = bdimodel.getCapability().getBelief(name);
					if(mbel!=null)
					{
						if(methods[i].getName().startsWith("get"))
						{
							mbel.setGetter(new MethodInfo(methods[i]));
						}
						else
						{
							mbel.setSetter(new MethodInfo(methods[i]));
						}
					}
					else
					{
						bdimodel.getCapability().addBelief(new MBelief(new MethodInfo(methods[i]), 
							bel.implementation().getName().equals(Object.class.getName())? null: bel.implementation().getName(),
							bel.dynamic(), bel.events().length==0? null: bel.events()));
					}
				}
			}
			
			// Find external plans
			if(isAnnotationPresent(clazz, Plans.class, cl))
			{
				Plan[] plans = getAnnotation(clazz, Plans.class, cl).value();
				for(Plan p: plans)
				{
					getMPlan(bdimodel, p, null, null, cl, pubs);
				}
			}
			
			if(!confdone && isAnnotationPresent(clazz, BDIConfigurations.class, cl))
			{
				BDIConfigurations val = (BDIConfigurations)getAnnotation(clazz, BDIConfigurations.class, cl);
				BDIConfiguration[] configs = val.value();
				confdone = val.replace();
				
				for(int i=0; i<configs.length; i++)
				{
					if(!confs.containsKey(configs[i].name()))
					{
						MConfiguration bdiconf = new MConfiguration(configs[i].name());
						bdiconfs.add(bdiconf);
							
						List<UnparsedExpression> ibels = createUnparsedExpressionsList(configs[i].initialbeliefs());
						if(ibels!=null)
							bdiconf.setInitialBeliefs(ibels);
						List<UnparsedExpression> iplans = createUnparsedExpressionsList(configs[i].initialplans());
						if(iplans!=null)
							bdiconf.setInitialPlans(iplans);
						List<UnparsedExpression> igoals = createUnparsedExpressionsList(configs[i].initialgoals());
						if(igoals!=null)
							bdiconf.setInitialGoals(igoals);
						
						// Need to repeat the code as annotation type BDIConfiguration is different :-(
						
						ConfigurationInfo configinfo = new ConfigurationInfo(configs[i].name());
						confs.put(configs[i].name(), configinfo);
						
						configinfo.setMaster(configs[i].master());
						configinfo.setDaemon(configs[i].daemon());
						configinfo.setAutoShutdown(configs[i].autoshutdown());
						configinfo.setSuspend(configs[i].suspend());
						
						NameValue[] argvals = configs[i].arguments();
						for(int j=0; j<argvals.length; j++)
						{
							configinfo.addArgument(new UnparsedExpression(argvals[j].name(), argvals[j].clazz().getName(), argvals[j].value(), null));
						}
						NameValue[] resvals = configs[i].results();
						for(int j=0; j<resvals.length; j++)
						{
							configinfo.addResult(new UnparsedExpression(resvals[j].name(), resvals[j].clazz().getName(), resvals[j].value(), null));
						}
						
						ProvidedService[] provs = configs[i].providedservices();
						ProvidedServiceInfo[] psis = new ProvidedServiceInfo[provs.length];
						for(int j=0; j<provs.length; j++)
						{
							Implementation im = provs[j].implementation();
							Value[] inters = im.interceptors();
							UnparsedExpression[] interceptors = null;
							if(inters.length>0)
							{
								interceptors = new UnparsedExpression[inters.length];
								for(int k=0; k<inters.length; k++)
								{
									interceptors[k] = new UnparsedExpression(null, inters[k].clazz().getName(), inters[k].value(), null);
								}
							}
							RequiredServiceBinding bind = createBinding(im.binding());
							ProvidedServiceImplementation impl = new ProvidedServiceImplementation(!im.value().equals(Object.class)? im.value(): null, 
								im.expression().length()>0? im.expression(): null, im.proxytype(), bind, interceptors);
							Publish p = provs[j].publish();
							PublishInfo pi = p.publishid().length()==0? null: new PublishInfo(p.publishid(), p.publishtype(), 
								p.mapping(), createUnparsedExpressions(p.properties()));
							psis[j] = new ProvidedServiceInfo(provs[j].name().length()>0? provs[j].name(): null, provs[j].type(), impl, pi);
							configinfo.setProvidedServices(psis);
						}
						
						RequiredService[] reqs = configs[i].requiredservices();
						RequiredServiceInfo[] rsis = new RequiredServiceInfo[reqs.length];
						for(int j=0; j<reqs.length; j++)
						{
							RequiredServiceBinding binding = createBinding(reqs[j].binding());
							rsis[j] = new RequiredServiceInfo(reqs[j].name(), reqs[j].type(), reqs[j].multiple(), 
								Object.class.equals(reqs[j].multiplextype())? null: reqs[j].multiplextype(), binding);
							configinfo.setRequiredServices(rsis);
						}
						
						Component[] comps = configs[i].components();
						for(int j=0; j<comps.length; j++)
						{
							configinfo.addComponentInstance(createComponentInstanceInfo(comps[j]));
						}
					}
				}
			}
			
			clazz = clazz.getSuperclass();
		}
		
		// init deliberation of goals
		List<MGoal> mgoals = bdimodel.getCapability().getGoals();
		for(MGoal mgoal: mgoals)
		{
			MDeliberation delib = mgoal.getDeliberation();
			if(delib!=null)
			{
				delib.init(bdimodel.getCapability());
			}
		}
		
		ModelInfo modelinfo = (ModelInfo)bdimodel.getModelInfo();
		if(confs.size()>0)
		{
			modelinfo.setConfigurations((ConfigurationInfo[])confs.values().toArray(new ConfigurationInfo[confs.size()]));
			bdimodel.getCapability().setConfigurations(bdiconfs);
		}

		// Evaluate the published goals and create provided services for them
		for(Iterator<ClassInfo> it = pubs.keySet().iterator(); it.hasNext(); )
		{
			ClassInfo key = it.next();
			List<Tuple2<MGoal, String>> vals = pubs.get(key);
			Map<String, String> goalnames = new LinkedHashMap<String, String>();
			for(Tuple2<MGoal, String> val: vals)
			{
				goalnames.put(val.getSecondEntity(), val.getFirstEntity().getName());
			}
	//		System.out.println("found goal publish: "+key);
			
			StringBuffer buf = new StringBuffer();
			buf.append("jadex.bdiv3.BDIClassReader.createServiceImplementation($component, ");
			buf.append(key.getTypeName()+".class, ");
			buf.append("new String[]{");
			for(Iterator<String> it2=goalnames.keySet().iterator(); it2.hasNext(); )
			{
				buf.append("\"").append(it2.next()).append("\"");
				if(it2.hasNext())
					buf.append(", ");
			}
			buf.append("}, ");
			buf.append("new String[]{");
			for(Iterator<String> it2=goalnames.keySet().iterator(); it2.hasNext(); )
			{
				buf.append("\"").append(goalnames.get(it2.next())).append("\"");
				if(it2.hasNext())
					buf.append(", ");
			}
			buf.append("}");
			buf.append(")");
			
	//		System.out.println("service creation expression: "+buf.toString());
			
			ProvidedServiceImplementation psi = new ProvidedServiceImplementation(null, buf.toString(), 
				BasicServiceInvocationHandler.PROXYTYPE_DECOUPLED, null, null);
			modelinfo.addProvidedService(new ProvidedServiceInfo(null, key, psi, null));
		}
		
		// Create enhanced classes if not already present.
		ClassLoader classloader = ((DummyClassLoader)cl).getOriginal();
		for(Class<?> agcl: agtcls)
		{
//			Class<?> acl =
			gen.generateBDIClass(agcl.getName(), bdimodel, classloader);
//			System.out.println("genclazz: "+acl.hashCode()+" "+acl.getClassLoader());
		}
		
//		System.out.println("genclazz: "+genclazz);
	}
	
	/**
	 * 
	 */
	protected MTrigger buildPlanTrigger(BDIModel bdimodel, Trigger trigger, ClassLoader cl, Map<ClassInfo, List<Tuple2<MGoal, String>>> pubs)
	{
		MTrigger tr = null;
		
		Class<?>[] gs = trigger.goals();
		String[] fas = trigger.factaddeds();
		String[] frs = trigger.factremoveds();
		String[] fcs = trigger.factchangeds();
		
		if(gs.length>0 || fas.length>0 || frs.length>0 || fcs.length>0)
		{
			tr = new MTrigger();
			
			for(int j=0; j<gs.length; j++)
			{
				Goal ga = getAnnotation(gs[j], Goal.class, cl);
				MGoal mgoal = getMGoal(bdimodel, ga, gs[j], cl, pubs);
				tr.addGoal(mgoal);
			}
			
			for(int j=0; j<fas.length; j++)
			{
				tr.addFactAdded(fas[j]);
			}
			
			for(int j=0; j<frs.length; j++)
			{
				tr.addFactRemoved(frs[j]);
			}
			
			for(int j=0; j<fcs.length; j++)
			{
				tr.addFactChangeds(fcs[j]);
			}
		}
		
		return tr;
	}
	
	/**
	 * 
	 */
	protected MPlan getMPlan(BDIModel bdimodel, Plan p, MethodInfo mi, ClassInfo ci,
		ClassLoader cl, Map<ClassInfo, List<Tuple2<MGoal, String>>> pubs)
	{
		String name = null;
		Body body = p.body();
		ServicePlan sp = body.service();
		String	component	= body.component();
		if(mi!=null)
		{
			name = mi.getName();
		}
		else if(ci!=null)
		{
			name = ci.getTypeName();
		}
		else if(!Object.class.equals(body.value()))
		{
			name = SReflect.getInnerClassName(body.value());
		}
		else if(sp.name().length()>0)
		{
			name = sp.name()+"_"+sp.method();
		}
		else if(component.length()>0)
		{
			name = component;
			if(name.indexOf("/")!=-1)
			{
				name	= name.substring(name.lastIndexOf("/")+1);
			}
			if(name.indexOf(".")!=-1)
			{
				name	= name.substring(0, name.lastIndexOf("."));
			}
		}
		else
		{
			throw new RuntimeException("Plan body not found: "+p);
		}
		
		MPlan mplan = bdimodel.getCapability().getPlan(name);
		
		if(mplan==null)
		{
			mplan = createMPlan(bdimodel, p, mi, name, ci, cl, pubs);
			bdimodel.getCapability().addPlan(mplan);
		}
		
		return mplan;
	}
	
	/**
	 * 
	 */
	protected MPlan createMPlan(BDIModel bdimodel, Plan p, MethodInfo mi, String name,
		ClassInfo ci, ClassLoader cl, Map<ClassInfo, List<Tuple2<MGoal, String>>> pubs)
	{
		Body body = p.body();
		ServicePlan sp = body.service();
		
		MPlan mplan = bdimodel.getCapability().getPlan(name);
		
		if(mplan==null)
		{
			MTrigger mtr = buildPlanTrigger(bdimodel, p.trigger(), cl, pubs);
			MTrigger wmtr = buildPlanTrigger(bdimodel, p.waitqueue(), cl, pubs);
			
			if(ci==null)
				ci = Object.class.equals(body.value())? null: new ClassInfo(body.value().getName());
			Class<? extends IServiceParameterMapper<Object>> mapperclass = (Class<? extends IServiceParameterMapper<Object>>)(IServiceParameterMapper.class.equals(sp.mapper())? null: sp.mapper());
			MBody mbody = new MBody(mi, ci, sp.name().length()==0? null: sp.name(), sp.method().length()==0? null: sp.method(), 
				Object.class.equals(sp.mapper())? null: new ClassInfo(sp.mapper().getName()),
				body.component().length()==0 ? null : body.component());
			mplan = new MPlan(name, mbody, mtr, wmtr, p.priority());
		}
		
		return mplan;
	}
	
	/**
	 *  Create a wrapper service implementation based on a published goal.
	 */
	public static Object createServiceImplementation(BDIAgent agent, Class<?> type, String[] methodnames, String[] goalnames)
	{
//		if(methodnames==null || methodnames.length==0)
//			throw new IllegalArgumentException("At least one method-goal mapping must be given.");
		Map<String, String> gn = new HashMap<String, String>();
		for(int i=0; i<methodnames.length; i++)
		{
			gn.put(methodnames[i], goalnames[i]);
		}
		return Proxy.newProxyInstance(agent.getClassLoader(), new Class[]{type}, 
			new GoalDelegationHandler(agent, gn));
	}
	
	/**
	 * 
	 */
	protected MGoal getMGoal(BDIModel model, Goal goal, Class<?> gcl, ClassLoader cl, Map<ClassInfo, List<Tuple2<MGoal, String>>> pubs)
	{
		MGoal mgoal = model.getCapability().getGoal(gcl.getName());
		
		if(mgoal==null)
		{
			mgoal = createMGoal(model, goal, gcl, cl, pubs);
			model.getCapability().addGoal(mgoal);
		}
		
		return mgoal;
	}
	
	/**
	 * 
	 */
	protected MGoal createMGoal(BDIModel model, Goal goal, Class<?> gcl, ClassLoader cl, Map<ClassInfo, List<Tuple2<MGoal, String>>> pubs)
	{
		assert model.getCapability().getGoal(gcl.getName())==null;
		
		Deliberation del = goal.deliberation();
		Class<?>[] inh = del.inhibits();
		Set<String> inhnames = null;
		if(inh.length>0)
		{
			inhnames = new HashSet<String>();
			for(Class<?> icl: inh)
			{
				inhnames.add(icl.getName());
			}
		}
		MDeliberation mdel = null;
//		if(del.cardinality()>0 || inhnames!=null)
		if(inhnames!=null)
		{
			// scan for instance delib methods
			Map<String, MethodInfo> inhms = new HashMap<String, MethodInfo>();
			Method[] ms = gcl.getDeclaredMethods();
			for(Method m: ms)
			{
				if(isAnnotationPresent(m, GoalInhibit.class, cl))
				{
					GoalInhibit ginh = getAnnotation(m, GoalInhibit.class, cl);
					Class<?> icl = ginh.value();
					inhms.put(icl.getName(), new MethodInfo(m));
				}
			}
//			mdel = new MDeliberation(del.cardinality(), inhnames, inhms.isEmpty()? null: inhms);
			mdel = new MDeliberation(inhnames, inhms.isEmpty()? null: inhms);
		}

		MGoal mgoal = new MGoal(gcl.getName(), goal.posttoall(), goal.randomselection(), goal.excludemode(), 
			goal.retry(), goal.recur(), goal.retrydelay(), goal.recurdelay(), goal.succeedonpassed(), goal.unique(), mdel);
		
		jadex.bdiv3.annotation.Publish pub = goal.publish();
		if(!Object.class.equals(pub.type()))
		{
			ClassInfo ci = new ClassInfo(pub.type().getName());
			String method = pub.method().length()>0? pub.method(): null;
			
			List<Tuple2<MGoal, String>> tmp = pubs.get(ci);
			if(tmp==null)
			{
				tmp = new ArrayList<Tuple2<MGoal, String>>();
				pubs.put(ci, tmp);
			}
			tmp.add(new Tuple2<MGoal, String>(mgoal, method));
		}
		return mgoal;
	}
}
