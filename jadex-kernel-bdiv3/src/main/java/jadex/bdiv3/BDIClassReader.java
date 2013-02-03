package jadex.bdiv3;

import jadex.bdiv3.annotation.BDIConfiguration;
import jadex.bdiv3.annotation.BDIConfigurations;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Body;
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
import jadex.bdiv3.runtime.GoalDelegationHandler;
import jadex.bdiv3.runtime.IServiceParameterMapper;
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
	 * 
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
		List<URL> urls = SUtil.getClasspathURLs(classloader);
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
		modelinfo.setStartable(true);
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
					MGoal mgoal = createMGoal(goal, goal.clazz(), cl, pubs);
					bdimodel.getCapability().addGoal(mgoal);
				}
			}
			
			// Find goals
			Class<?>[] cls = clazz.getDeclaredClasses();
			for(int i=0; i<cls.length; i++)
			{
				if(isAnnotationPresent(cls[i], Goal.class, cl))
				{
//					System.out.println("found belief: "+fields[i].getName());
					Goal goal = getAnnotation(cls[i], Goal.class, cl);
					MGoal mgoal = createMGoal(goal, cls[i], cl, pubs);
					bdimodel.getCapability().addGoal(mgoal);
				}
			}
			
			// Find beliefs
			Field[] fields = clazz.getDeclaredFields();
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
					MPlan mplan = createMPlan(bdimodel, p, new MethodInfo(methods[i]), cl, pubs);
					bdimodel.getCapability().addPlan(mplan);
				}
			}
			
			// Find external plans
			if(isAnnotationPresent(clazz, Plans.class, cl))
			{
				Plan[] plans = getAnnotation(clazz, Plans.class, cl).value();
				for(Plan p: plans)
				{
					MPlan mplan = createMPlan(bdimodel, p, null, cl, pubs);
					bdimodel.getCapability().addPlan(mplan);
				}
			}
			
//			Class[] classes = cl.getDeclaredClasses();
//			for(int i=0; i<classes.length; i++)
//			{
//				if(classes[i].isAnnotationPresent(Goal.class))
//				{
////					System.out.println("found goal: "+classes[i].getName());
//					goals.add(classes[i]);
//				}
//			}
			
			if(!confdone && isAnnotationPresent(cma, BDIConfigurations.class, cl))
			{
				BDIConfigurations val = (BDIConfigurations)getAnnotation(cma, BDIConfigurations.class, cl);
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
							bdiconf.setInitialGoals(ibels);
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
//			System.out.println("genclazz: "+acl.hashCode()+" "+acl.getClassLoader());
			Class<?> acl = gen.generateBDIClass(agcl.getName(), bdimodel, classloader);
		}
		
//		System.out.println("genclazz: "+genclazz);
	}
	
	/**
	 * 
	 */
	protected MTrigger buildPlanTrigger(BDIModel bdimodel, Plan p, ClassLoader cl, Map<ClassInfo, List<Tuple2<MGoal, String>>> pubs)
	{
		MTrigger tr = new MTrigger();
		Trigger trigger = p.trigger();
		Class<?>[] gs = trigger.goals();
		for(int j=0; j<gs.length; j++)
		{
			Goal ga = getAnnotation(gs[j], Goal.class, cl);
			
			MGoal mgoal = bdimodel.getCapability().getGoal(gs[j].getName());
			
			if(mgoal==null)
			{	
				mgoal = createMGoal(ga, gs[j], cl, pubs);
				bdimodel.getCapability().addGoal(mgoal);
			}

			tr.addGoal(mgoal);
		}
		String[] fas = trigger.factaddeds();
		for(int j=0; j<fas.length; j++)
		{
			tr.addFactAdded(fas[j]);
		}
		String[] frs = trigger.factremoveds();
		for(int j=0; j<frs.length; j++)
		{
			tr.addFactRemoved(frs[j]);
		}
		String[] fcs = trigger.factchangeds();
		for(int j=0; j<fcs.length; j++)
		{
			tr.addFactChangeds(fcs[j]);
		}
		
		return tr;
	}
	
	/**
	 * 
	 */
	protected MPlan createMPlan(BDIModel bdimodel, Plan p, MethodInfo mi, ClassLoader cl, Map<ClassInfo, List<Tuple2<MGoal, String>>> pubs)
	{
		MTrigger mtr = buildPlanTrigger(bdimodel, p, cl, pubs);
		
		Body body = p.body();
		String name;
		ServicePlan sp = body.service();
		if(mi!=null)
		{
			mi.getName();
		}
		else if(!Object.class.equals(body.value()))
		{
			name = SReflect.getInnerClassName(body.value());
		}
		else
		{
			name = sp.name()+"_"+sp.method();
		}
		
		ClassInfo ci = Object.class.equals(body.value())? null: new ClassInfo(body.value().getName());
		Class<? extends IServiceParameterMapper<Object>> mapperclass = (Class<? extends IServiceParameterMapper<Object>>)(IServiceParameterMapper.class.equals(sp.mapper())? null: sp.mapper());
		MBody mbody = new MBody(mi, ci, sp.name().length()==0? null: sp.name(), sp.method().length()==0? null: sp.method(), 
			(Object.class.equals(sp.mapper())? null: new ClassInfo(sp.mapper().getName())));
		MPlan mplan = new MPlan(SReflect.getInnerClassName(body.value()), mbody, mtr, p.priority());
		
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
	protected MGoal createMGoal(Goal goal, Class<?> gcl, ClassLoader cl, Map<ClassInfo, List<Tuple2<MGoal, String>>> pubs)
	{
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
