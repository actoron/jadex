package jadex.bdiv3;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bdiv3.annotation.BDIConfiguration;
import jadex.bdiv3.annotation.BDIConfigurations;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Body;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Deliberation;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalContextCondition;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.GoalDropCondition;
import jadex.bdiv3.annotation.GoalInhibit;
import jadex.bdiv3.annotation.GoalMaintainCondition;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalRecurCondition;
import jadex.bdiv3.annotation.GoalServiceParameterMapping;
import jadex.bdiv3.annotation.GoalServiceResultMapping;
import jadex.bdiv3.annotation.GoalTargetCondition;
import jadex.bdiv3.annotation.Goals;
import jadex.bdiv3.annotation.Mapping;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.RawEvent;
import jadex.bdiv3.annotation.ServicePlan;
import jadex.bdiv3.annotation.ServiceTrigger;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.features.impl.BDIAgentFeature;
import jadex.bdiv3.model.BDIModel;
import jadex.bdiv3.model.ConstructorInfo;
import jadex.bdiv3.model.IBDIModel;
import jadex.bdiv3.model.MBelief;
import jadex.bdiv3.model.MBody;
import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MCondition;
import jadex.bdiv3.model.MConfigBeliefElement;
import jadex.bdiv3.model.MConfigParameterElement;
import jadex.bdiv3.model.MConfiguration;
import jadex.bdiv3.model.MDeliberation;
import jadex.bdiv3.model.MElement;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MParameter;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.model.MProcessableElement;
import jadex.bdiv3.model.MServiceCall;
import jadex.bdiv3.model.MTrigger;
import jadex.bdiv3.model.SBDIModel;
import jadex.bdiv3.runtime.impl.IServiceParameterMapper;
import jadex.bridge.ClassInfo;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.LocalResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.component.IComponentFeatureFactory;
import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.ModelInfo;
import jadex.bridge.modelinfo.NFRPropertyInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.nonfunctional.annotation.SNameValue;
import jadex.bridge.service.ProvidedServiceImplementation;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.RequiredServiceBinding;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Value;
import jadex.bridge.service.component.BasicServiceInvocationHandler;
import jadex.commons.FieldInfo;
import jadex.commons.MethodInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.micro.MicroClassReader;
import jadex.micro.MicroModel;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.Publish;
import jadex.micro.annotation.RequiredService;
import jadex.rules.eca.EventType;

/**
 *  Reads micro agent classes and generates a model from metainfo and annotations.
 */
public class BDIClassReader extends MicroClassReader
{
	/** The class generator. */
	protected IBDIClassGenerator gen;
	
	/** The model loader for subcapabilities. */
	protected BDIModelLoader	loader;
	
	/**
	 *  Create a new bdi class reader.
	 */
	public BDIClassReader(BDIModelLoader loader)
	{
		this.gen = BDIClassGeneratorFactory.getInstance().createBDIClassGenerator();
		this.loader	= loader;
	}
	
	/**
	 *  Load a  model.
	 *  @param model The model (e.g. file name).
	 *  @param The imports (if any).
	 *  @return The loaded model.
	 */
	@Override
	public MicroModel read(String model, String[] imports, ClassLoader classloader, IResourceIdentifier rid, 
		IComponentIdentifier root, List<IComponentFeatureFactory> features)
	{
		// use dummy classloader that will not be visisble outside
		List<URL> urls = SUtil.getClasspathURLs(classloader, false);
		DummyClassLoader cl = createDummyClassLoader(classloader, null, urls);
		return super.read(model, imports, cl, rid, root, features);
	}

	/**
	 *  Create a throw away class loader.
	 */
	protected DummyClassLoader createDummyClassLoader(ClassLoader original, ClassLoader parent, List<URL> urls)
	{
		return new DummyClassLoader((URL[])urls.toArray(new URL[urls.size()]), parent, original);
	}
	
	/**
	 *  Load the model.
	 */
	@Override
	protected BDIModel read(String model, Class<?> cma, ClassLoader cl, IResourceIdentifier rid, IComponentIdentifier root,
		List<IComponentFeatureFactory> features)
	{
		ClassLoader classloader = ((DummyClassLoader)cl).getOriginal();
		
		ModelInfo modelinfo = new ModelInfo();
		BDIModel ret = new BDIModel(modelinfo, new MCapability(cma.getName()));
		modelinfo.internalSetRawModel(ret);
		ret.setPojoClass(new ClassInfo(cma.getName()));
		
		String name = SReflect.getUnqualifiedClassName(cma);
		if(name.endsWith(BDIModelLoader.FILE_EXTENSION_BDIV3_FIRST))
			name = name.substring(0, name.lastIndexOf(BDIModelLoader.FILE_EXTENSION_BDIV3_FIRST));
		String packagename = cma.getPackage()!=null? cma.getPackage().getName(): null;
//		modelinfo.setName(name+"BDI");
		modelinfo.setName(name);
		modelinfo.setPackage(packagename);
		modelinfo.setFilename(getFileName(cma, model));
//		modelinfo.setStartable(!Modifier.isAbstract(cma.getModifiers()));
//		modelinfo.setStartable(cma.getName().endsWith(BDIModelLoader.FILE_EXTENSION_BDIV3_FIRST));
		modelinfo.setType(BDIAgentFactory.FILETYPE_BDIAGENT);
		modelinfo.setResourceIdentifier(rid);
		modelinfo.setClassloader(classloader);
		ret.setClassloader(classloader); // use parent
		if(features!=null)
			modelinfo.setFeatures((IComponentFeatureFactory[])features.toArray(new IComponentFeatureFactory[features.size()]));
		
//		System.out.println("filename: "+modelinfo.getFilename());
		
		if(rid==null)
		{
			URL url = cma.getProtectionDomain().getCodeSource().getLocation();
			rid = new ResourceIdentifier(new LocalResourceIdentifier(root, url), null);
		}
		modelinfo.setResourceIdentifier(rid);
		
		fillMicroModelFromAnnotations(ret, model, cma, cl);
		
		fillBDIModelFromAnnotations(ret, model, cma, cl, rid, root, features);
		
		Class<?> genclass = SReflect.findClass0(cma.getName(), null, classloader);
		modelinfo.setStartable(!Modifier.isAbstract(genclass.getModifiers()));
		
		initBDIModelAfterClassLoading(ret, classloader);
		
		return ret;
	}

	/**
	 * Gets the filename for a class.
	 * @param cma
	 * @param model 
	 * @return filename
	 */
	protected String getFileName(Class< ? > cma, String model)
	{
		String src = SUtil.convertURLToString(cma.getProtectionDomain().getCodeSource().getLocation());
		return src+File.separator+SReflect.getClassName(cma)+".class";
	}
	
	/**
	 *  Fill the model details using annotation.
	 *  // called with dummy classloader (that was used to load cma first time)
	 */
	protected void fillBDIModelFromAnnotations(BDIModel bdimodel, String model, Class<?> cma, ClassLoader cl,  IResourceIdentifier rid, IComponentIdentifier root, List<IComponentFeatureFactory> features)
	{
//		ModelInfo modelinfo = (ModelInfo)micromodel.getModelInfo();
		
//		System.out.println("todo: read bdi");
		
//		List<Field> s = new ArrayList<Field>();
//		final Set<String> beliefnames = new HashSet<String>();
//		List<Class> goals = new ArrayList<Class>();
//		List<Method> plans = new ArrayList<Method>();
		
//		try
//		{
		
		Map<String, IBDIModel>	capas	= new LinkedHashMap<String, IBDIModel>();
		
		Map<String, ConfigurationInfo> confs = new LinkedHashMap<String, ConfigurationInfo>();
		List<MConfiguration> bdiconfs = new ArrayList<MConfiguration>();
		boolean confdone = false;
		
		Map<ClassInfo, List<Tuple2<MGoal, String>>> pubs = new HashMap<ClassInfo, List<Tuple2<MGoal, String>>>();
		
		List<Class<?>> agtcls = new ArrayList<Class<?>>();
		while(cma!=null && !cma.equals(Object.class))// && !cma.equals(getClass(BDIAgent.class, cl)))
		{
			if(isAnnotationPresent(cma, Agent.class, cl)
				|| isAnnotationPresent(cma, Capability.class, cl))
			{
				agtcls.add(0, cma);
			}
			cma = cma.getSuperclass();
		}
		
		for(Class<?> clazz: agtcls)
		{
			Field[] fields = clazz.getDeclaredFields();

			// Find capabilities
			for(int i=0; i<fields.length; i++)
			{
				if(isAnnotationPresent(fields[i], Capability.class, cl))
				{
					try
					{
						BDIModel cap = loader.loadComponentModel(fields[i].getType().getName()+".class", null, rid, ((DummyClassLoader)cl).getOriginal(), new Object[]{rid, root, features});
//						System.out.println("found capability: "+fields[i].getName()+", "+cap);
						capas.put(fields[i].getName(), cap);
						
						Capability acap	= getAnnotation(fields[i], Capability.class, cl);
						for(Mapping mapping : acap.beliefmapping())
						{
							String	source	= mapping.value();
							String	target	= mapping.target().equals("") ? source : mapping.target();
							if(cap.getCapability().getBelief(target)==null)
							{
								throw new RuntimeException("No such belief for mapping from "+source+" to "+fields[i].getName()+MElement.CAPABILITY_SEPARATOR+target);
							}
							bdimodel.getCapability().addBeliefReference(fields[i].getName()+MElement.CAPABILITY_SEPARATOR+target, source);	// Store inverse mapping (inner abstract reference -> outer concrete belief)
						}
						
						bdimodel.addSubcapability(new FieldInfo(fields[i]), cap);
					}
					catch(Exception e)
					{
						throw e instanceof RuntimeException ? (RuntimeException)e : new RuntimeException(e);
					}
				}
			}
		}
		
		// Merge capabilities into flat agent model 
		SBDIModel.mergeSubcapabilities(bdimodel, capas, cl);
		
		for(Class<?> clazz: agtcls)
		{
			// Find beliefs
			Field[] fields = clazz.getDeclaredFields();
			for(int i=0; i<fields.length; i++)
			{
				if(isAnnotationPresent(fields[i], Belief.class, cl))
				{
//					System.out.println("found belief: "+fields[i].getName());
					Belief bel = getAnnotation(fields[i], Belief.class, cl);
					
					Set<EventType> rawevents = null;
					if(bel.rawevents().length>0)
					{
						rawevents = new HashSet<EventType>();
						RawEvent[] rawevs = bel.rawevents();
						for(RawEvent rawev: rawevs)
						{
							rawevents.add(BDIAgentFeature.createEventType(rawev)); 
						}
					}

					boolean	dynamic	= bel.dynamic() || bel.updaterate()>0;// || bel.beliefs().length>0 || bel.rawevents().length>0;
					bdimodel.getCapability().addBelief(new MBelief(new FieldInfo(fields[i]), 
						bel.implementation().getName().equals(Object.class.getName())? null: bel.implementation().getName(),
						dynamic, bel.updaterate(), bel.beliefs().length==0? null: bel.beliefs(), rawevents));
//					beliefs.add(fields[i]);
//					beliefnames.add(fields[i].getName());
				}
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
			
			// Find method plans or beliefs
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
					
					String name = methods[i].getName().substring(methods[i].getName().startsWith("is") ? 2 : 3);
					name = name.substring(0, 1).toLowerCase()+name.substring(1);
					
					MBelief mbel = bdimodel.getCapability().getBelief(name);
					if(mbel!=null)
					{
						if(methods[i].getName().startsWith("get") || methods[i].getName().startsWith("is"))
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
						Set<EventType> rawevents = null;
						if(bel.rawevents().length>0)
						{
							rawevents = new HashSet<EventType>();
							RawEvent[] rawevs = bel.rawevents();
							for(RawEvent rawev: rawevs)
							{
								rawevents.add(BDIAgentFeature.createEventType(rawev)); 
							}
						}
						
						boolean	dynamic	= bel.dynamic() || bel.updaterate()>0;// || rawevents!=null || bel.beliefs().length>0;
						bdimodel.getCapability().addBelief(new MBelief(new MethodInfo(methods[i]), 
							bel.implementation().getName().equals(Object.class.getName())? null: bel.implementation().getName(),
							dynamic, bel.updaterate(), bel.beliefs().length==0? null: bel.beliefs(), rawevents));
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
							
						bdiconf.setInitialBeliefs(createConfigBeliefsList(configs[i].initialbeliefs()));
						bdiconf.setEndBeliefs(createConfigBeliefsList(configs[i].endbeliefs()));
						
						bdiconf.setInitialPlans(createConfigParamElementsList(configs[i].initialplans()));
						bdiconf.setEndPlans(createConfigParamElementsList(configs[i].endplans()));
						
						bdiconf.setInitialGoals(createConfigParamElementsList(configs[i].initialgoals()));
						bdiconf.setEndGoals(createConfigParamElementsList(configs[i].endgoals()));
						
						// Need to repeat the code as annotation type BDIConfiguration is different :-(
						
						ConfigurationInfo configinfo = new ConfigurationInfo(configs[i].name());
						confs.put(configs[i].name(), configinfo);
						
						configinfo.setMaster(configs[i].master());
						configinfo.setDaemon(configs[i].daemon());
						configinfo.setAutoShutdown(configs[i].autoshutdown());
						configinfo.setSynchronous(configs[i].synchronous());
						configinfo.setPersistable(configs[i].persistable());
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
								p.mapping(), SNameValue.createUnparsedExpressions(p.properties()));
							
							NameValue[] props = provs[j].properties();
							List<UnparsedExpression> serprops = (props != null && props.length > 0) ? new ArrayList<UnparsedExpression>(Arrays.asList(SNameValue.createUnparsedExpressions(props))) : null;
							
							psis[j] = new ProvidedServiceInfo(provs[j].name().length()>0? provs[j].name(): null, provs[j].type(), impl, provs[j].scope(), pi, serprops);
							configinfo.setProvidedServices(psis);
						}
						
						RequiredService[] reqs = configs[i].requiredservices();
						RequiredServiceInfo[] rsis = new RequiredServiceInfo[reqs.length];
						for(int j=0; j<reqs.length; j++)
						{
							RequiredServiceBinding binding = createBinding(reqs[j].binding());
							List<NFRPropertyInfo> nfprops = createNFRProperties(reqs[j].nfprops());
							rsis[j] = new RequiredServiceInfo(reqs[j].name(), reqs[j].type(), reqs[j].multiple(), 
								Object.class.equals(reqs[j].multiplextype())? null: reqs[j].multiplextype(), binding, nfprops, Arrays.asList(reqs[j].tags()));
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
		}
		
//		// init deliberation of goals
//		List<MGoal> mgoals = bdimodel.getCapability().getGoals();
//		for(MGoal mgoal: mgoals)
//		{
//			MDeliberation delib = mgoal.getDeliberation();
//			if(delib!=null)
//			{
//				delib.init(bdimodel.getCapability());
//			}
//		}
		
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
			buf.append("jadex.bdiv3.runtime.impl.GoalDelegationHandler.createServiceImplementation($component, ");
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
			
			// todo: allow specifying scope
			modelinfo.addProvidedService(new ProvidedServiceInfo(null, key, psi, null, null, null));
		}
		
		// Create enhanced classes if not already present.
		ClassLoader classloader = ((DummyClassLoader)cl).getOriginal();
		for(Class<?> agcl: agtcls)
		{
//			Class<?> acl =
			gen.generateBDIClass(agcl.getName(), bdimodel, classloader);
//			System.out.println("genclazz: "+agcl.getName()+" "+agcl.hashCode()+" "+agcl.getClassLoader());
		}
		
		// Sort the plans according to their declaration order in the source file
		// Must be done after class enhancement to contain the "__getLineNumber()" method
		bdimodel.getCapability().sortPlans(classloader);
		
//		System.out.println("genclazz: "+genclazz);
		
//		System.out.println("endend");
		
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}
	}	
	
	/**
	 * 
	 */
	protected MTrigger buildPlanTrigger(BDIModel bdimodel, Trigger trigger, ClassLoader cl, Map<ClassInfo, List<Tuple2<MGoal, String>>> pubs)
	{
		MTrigger tr = null;
		
		Class<?>[] gs = trigger.goals();
		Class<?>[] gfs = trigger.goalfinisheds();
		String[] fas = trigger.factaddeds();
		String[] frs = trigger.factremoveds();
		String[] fcs = trigger.factchangeds();
		ServiceTrigger st = trigger.service();
		
		if(gs.length>0 || gfs.length>0 || fas.length>0 || frs.length>0 || fcs.length>0 
			|| st.name().length()>0 || !Object.class.equals(st.type()))
		{
			tr = new MTrigger();
			
			for(int j=0; j<gs.length; j++)
			{
				Goal ga = getAnnotation(gs[j], Goal.class, cl);
				MGoal mgoal = getMGoal(bdimodel, ga, gs[j], cl, pubs);
				tr.addGoal(mgoal);
			}
			
			for(int j=0; j<gfs.length; j++)
			{
				Goal ga = getAnnotation(gfs[j], Goal.class, cl);
				MGoal mgoal = getMGoal(bdimodel, ga, gfs[j], cl, pubs);
				tr.addGoalFinished(mgoal);
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
				tr.addFactChanged(fcs[j]);
			}
			
			MServiceCall sc = getServiceCall(bdimodel, st);
			if(sc!=null)
			{
				tr.addService(sc);
			}
		}
		
		return tr;
	}
	
	/**
	 * 
	 */
	protected MServiceCall getServiceCall(BDIModel bdimodel, ServiceTrigger st)
	{
		String sn = st.name().length()>0? st.name(): null;
		Class<?> sty = !Object.class.equals(st.type())? st.type(): null;
		String mn = st.method().length()>0? st.method(): null;

		Class<?> stype = sty;
		if(sty==null && sn!=null)
		{
			ProvidedServiceInfo[] provs = bdimodel.getModelInfo().getProvidedServices();
			for(ProvidedServiceInfo prov: provs)
			{
				if(prov.getName().equals(sn))
				{
					stype = prov.getType().getType(bdimodel.getClassloader(), bdimodel.getModelInfo().getAllImports());
					break;
				}
			}
		}
		
		Method m = null;
		if(stype!=null)
		{
			if(mn!=null)
			{
				Method[] ms = SReflect.getMethods(stype, mn);
				if(ms.length>0)
				{
					m = ms[0];
				}
			}
			else
			{
				Method[] ms = stype.getDeclaredMethods();
				if(ms.length>0)
				{
					m = ms[0];
				}
			}
		}
		
		MServiceCall ret = null;
		if(m!=null)
		{
			ret = bdimodel.getCapability().getService(m.toString());
			if(ret==null)
			{
				ret = new MServiceCall(m.toString(), false, false, MProcessableElement.ExcludeMode.WhenTried);
				bdimodel.getCapability().addservice(ret);
			}
		}
		
		return ret;
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
		
		// Generate the plan name:
		
		if(mi!=null)
		{
			// Method name if plan is method
			name = mi.getName();
		}
		else if(ci!=null)
		{
			// Class name if is class
			name = ci.getTypeName();
		}
		else if(!Object.class.equals(body.value()))
		{
			// Class name if is class 
//			name = SReflect.getInnerClassName(body.value());
			name = body.value().getName();
		}
		else if(sp.name().length()>0)
		{
			// Service plan name if is service
			name = sp.name()+"_"+sp.method();
		}
		else if(component.length()>0)
		{
			// Plan is subcomponent
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
			
			// Check if external plan has a trigger defined
			if(mtr==null && !Object.class.equals(body.value()))
			{
				Class<?> bcl = body.value();
				Plan pl = getAnnotation(bcl, Plan.class, cl);
				mtr = buildPlanTrigger(bdimodel, pl.trigger(), cl, pubs);
				
				if(wmtr==null)
				{
					wmtr = buildPlanTrigger(bdimodel, pl.waitqueue(), cl, pubs);
				}
			}
			
			if(ci==null)
				ci = Object.class.equals(body.value())? null: new ClassInfo(body.value().getName());
//			Class<? extends IServiceParameterMapper<Object>> mapperclass = (Class<? extends IServiceParameterMapper<Object>>)(IServiceParameterMapper.class.equals(sp.mapper())? null: sp.mapper());
			Class<? extends IServiceParameterMapper<Object>> mapperclass = (Class<? extends IServiceParameterMapper<Object>>)(IServiceParameterMapper.class.getName().equals(sp.mapper().getName())? null: sp.mapper());
			MBody mbody = new MBody(mi, ci, sp.name().length()==0? null: sp.name(), sp.method().length()==0? null: sp.method(), 
				mapperclass==null? null: new ClassInfo(mapperclass), body.component().length()==0 ? null : body.component());
			mplan = new MPlan(name, mbody, mtr, wmtr, p.priority());
		}
		
		return mplan;
	}
	
//	/**
//	 *  Create a wrapper service implementation based on a published goal.
//	 */
//	public static Object createServiceImplementation(IInternalAccess agent, Class<?> type, String[] methodnames, String[] goalnames)
//	{
////		if(methodnames==null || methodnames.length==0)
////			throw new IllegalArgumentException("At least one method-goal mapping must be given.");
//		Map<String, String> gn = new HashMap<String, String>();
//		for(int i=0; i<methodnames.length; i++)
//		{
//			gn.put(methodnames[i], goalnames[i]);
//		}
//		return Proxy.newProxyInstance(agent.getClassLoader(), new Class[]{type}, 
//			new GoalDelegationHandler(agent, gn));
//	}
	
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
		
//		System.out.println("name: "+gcl.getName());
		
		Map<String, MethodInfo> spmappings = new HashMap<String, MethodInfo>();
		Map<String, MethodInfo> srmappings = new HashMap<String, MethodInfo>();
		
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
		boolean cardinalityone = del.cardinalityone();
		// If cardinality is one ensure that its own goal type is also inhibited
		if(cardinalityone)
		{
			if(inhnames==null)
			{
				inhnames = new HashSet<String>();
			}
			if(!inhnames.contains(gcl.getName()))
			{
//				System.out.println("Added own goal type to inhibitions due to cardinalityone: "+gcl.getName());
				inhnames.add(gcl.getName());
			}
		}
		MDeliberation mdel = null;
		if(inhnames!=null || cardinalityone)
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
				
				if(isAnnotationPresent(m, GoalServiceParameterMapping.class, cl))
				{
					GoalServiceParameterMapping gsm = getAnnotation(m, GoalServiceParameterMapping.class, cl);
					spmappings.put(gsm.name(), new MethodInfo(m));
				}
				
				if(isAnnotationPresent(m, GoalServiceResultMapping.class, cl))
				{
					GoalServiceResultMapping gsm = getAnnotation(m, GoalServiceResultMapping.class, cl);
					srmappings.put(gsm.name(), new MethodInfo(m));
				}
			}
			mdel = new MDeliberation(inhnames, inhms.isEmpty()? null: inhms, cardinalityone);
		}
		
		List<MParameter> params = new ArrayList<MParameter>();
		Class<?> tmpcl = gcl;
		while(!Object.class.equals(tmpcl))
		{
			Field[] fields = gcl.getDeclaredFields();
			for(Field f: fields)
			{
				if(isAnnotationPresent(f, GoalParameter.class, cl))
				{
//					GoalParameter gp = getAnnotation(f, GoalParameter.class, cl);
					MParameter param = new MParameter(new FieldInfo(f));
					params.add(param);
				}
			}
			tmpcl = tmpcl.getSuperclass(); 
		}
		
		MTrigger mtr = null;
		List<String> triggergoals = null;
		Class<?>[] trgoals = goal.triggergoals();
		if(trgoals!=null)
		{
			mtr = new MTrigger();
//			triggergoals = new ArrayList<String>();
			
			for(Class<?> trgoal: trgoals)
			{
//				triggergoals.add(trgoal.getName());
				
				Goal ga = getAnnotation(trgoal, Goal.class, cl);
				MGoal mgoal = getMGoal(model, ga, trgoal, cl, pubs);
				mtr.addGoal(mgoal);
			}

//			mtr.setGoalNames(triggergoals);
		}
		
		MGoal mgoal = new MGoal(gcl.getName(), gcl.getName(), goal.posttoall(), goal.rebuild(), goal.randomselection(), goal.excludemode(), 
			goal.retry(), goal.recur(), goal.retrydelay(), goal.recurdelay(), goal.orsuccess(), goal.unique(), mdel, params,
			spmappings.size()>0? spmappings: null, srmappings.size()>0? srmappings: null, mtr);
		
		jadex.bdiv3.annotation.Publish pub = goal.publish();
		if(!Object.class.equals(pub.type()))
		{
			ClassInfo ci = new ClassInfo(pub.type().getName());
			String method = pub.method().length()>0? pub.method(): null;
			
			// Just use first method if no name is given
			if(method==null)
				method = pub.type().getDeclaredMethods()[0].getName();
			
			List<Tuple2<MGoal, String>> tmp = pubs.get(ci);
			if(tmp==null)
			{
				tmp = new ArrayList<Tuple2<MGoal, String>>();
				pubs.put(ci, tmp);
			}
			tmp.add(new Tuple2<MGoal, String>(mgoal, method));
		}
		
		Constructor<?>[] cons = gcl.getConstructors();
		for(final Constructor<?> c: cons)
		{
			if(isAnnotationPresent(c, GoalCreationCondition.class, cl))
			{
				GoalCreationCondition gc = getAnnotation(c, GoalCreationCondition.class, cl);
				String[] evs = gc.beliefs();
				RawEvent[] rawevs = gc.rawevents();
				String[] paramevs = gc.parameters();
				List<EventType> events = readAnnotationEvents(model.getCapability(), getParameterAnnotations(c, cl), cl);
				for(String ev: evs)
				{
					BDIAgentFeature.addBeliefEvents(model.getCapability(), events, ev, cl);
				}
				for(RawEvent rawev: rawevs)
				{
					events.add(BDIAgentFeature.createEventType(rawev)); 
				}
				for(String pev: paramevs)
				{
					BDIAgentFeature.addParameterEvents(mgoal, model.getCapability(), events, pev, cl);
				}
				MCondition cond = new MCondition("creation_"+c.toString(), events);
				cond.setConstructorTarget(new ConstructorInfo(c));
				mgoal.addCondition(MGoal.CONDITION_CREATION, cond);
			}
		}
		
		Method[] ms = gcl.getDeclaredMethods();
		for(final Method m: ms)
		{
			if(isAnnotationPresent(m, GoalCreationCondition.class, cl))
			{
				GoalCreationCondition c = getAnnotation(m, GoalCreationCondition.class, cl);
				addMethodCondition(mgoal, MGoal.CONDITION_CREATION, 
					c.beliefs(), c.rawevents(), c.parameters(), model, m, cl);
			}
			else if(isAnnotationPresent(m, GoalDropCondition.class, cl))
			{
				GoalDropCondition c = getAnnotation(m, GoalDropCondition.class, cl);
				addMethodCondition(mgoal, MGoal.CONDITION_DROP, 
					c.beliefs(), c.rawevents(), c.parameters(), model, m, cl);
			}
			else if(isAnnotationPresent(m, GoalMaintainCondition.class, cl))
			{
				GoalMaintainCondition c = getAnnotation(m, GoalMaintainCondition.class, cl);
				addMethodCondition(mgoal, MGoal.CONDITION_MAINTAIN, 
					c.beliefs(), c.rawevents(), c.parameters(), model, m, cl);
			}
			else if(isAnnotationPresent(m, GoalTargetCondition.class, cl))
			{
				GoalTargetCondition c = getAnnotation(m, GoalTargetCondition.class, cl);
				addMethodCondition(mgoal, MGoal.CONDITION_TARGET, 
					c.beliefs(), c.rawevents(), c.parameters(), model, m, cl);
			}
			else if(isAnnotationPresent(m, GoalContextCondition.class, cl))
			{
				GoalContextCondition c = getAnnotation(m, GoalContextCondition.class, cl);
				addMethodCondition(mgoal, MGoal.CONDITION_CONTEXT, 
					c.beliefs(), c.rawevents(), c.parameters(), model, m, cl);
			}
			else if(isAnnotationPresent(m, GoalRecurCondition.class, cl))
			{
				GoalRecurCondition c = getAnnotation(m, GoalRecurCondition.class, cl);
				addMethodCondition(mgoal, MGoal.CONDITION_RECUR, 
					c.beliefs(), c.rawevents(), c.parameters(), model, m, cl);
			}
		}
		
		return mgoal;
	}
	
	/**
	 * 
	 */
	protected void addMethodCondition(MGoal mgoal, String condtype, String[] evs, RawEvent[] rawevs, String[] paramevs, 
		BDIModel model, Method m, ClassLoader cl)
	{
		List<EventType> events = readAnnotationEvents(model.getCapability(), getParameterAnnotations(m, cl), cl);
		for(String ev: evs)
		{
			BDIAgentFeature.addBeliefEvents(model.getCapability(), events, ev, cl);
		}
		for(RawEvent rawev: rawevs)
		{
			events.add(BDIAgentFeature.createEventType(rawev)); 
		}
		for(String pev: paramevs)
		{
			BDIAgentFeature.addParameterEvents(mgoal, model.getCapability(), events, pev, cl);
		}
		MCondition cond = new MCondition(condtype+"_"+m.toString(), events);
		cond.setMethodTarget(new MethodInfo(m));
		mgoal.addCondition(condtype, cond);
	}
	
	/**
	 *  Read the annotation events from method annotations.
	 */
	public static List<EventType> readAnnotationEvents(MCapability capa, Annotation[][] annos, ClassLoader cl)
	{
		List<EventType> events = new ArrayList<EventType>();
		if(annos!=null)
		{
			for(Annotation[] ana: annos)
			{
				for(Annotation an: ana)
				{
					if(an instanceof jadex.rules.eca.annotations.Event)
					{
						jadex.rules.eca.annotations.Event ev = (jadex.rules.eca.annotations.Event)an;
						String name = ev.value();
						String type = ev.type();
						if(type.length()==0)
						{
							BDIAgentFeature.addBeliefEvents(capa, events, name, cl);
						}
						else
						{
							events.add(new EventType(new String[]{type, name}));
						}
					}
				}
			}
		}
		return events;
	}
	
//	/**
//	 *  Create belief events from a belief name.
//	 *  For normal beliefs 
//	 *  beliefchanged.belname and factchanged.belname 
//	 *  and for multi beliefs additionally
//	 *  factadded.belname and factremoved 
//	 *  are created.
//	 */
//	public static void addBeliefEvents(MCapability mcapa, List<EventType> events, String belname, ClassLoader cl)
//	{
//		belname = belname.replace(".", "/");
//		MBelief mbel = mcapa.getBelief(belname);
//		if(mbel==null)
//			throw new RuntimeException("No such belief: "+belname);
//		
//		events.add(new EventType(new String[]{ChangeEvent.BELIEFCHANGED, belname})); // the whole value was changed
//		events.add(new EventType(new String[]{ChangeEvent.FACTCHANGED, belname})); // property change of a value
//		
//		if(mbel.isMulti(cl))
//		{
//			events.add(new EventType(new String[]{ChangeEvent.FACTADDED, belname}));
//			events.add(new EventType(new String[]{ChangeEvent.FACTREMOVED, belname}));
//		}
//	}
//	
//	/**
//	 *  Create parameter events from a belief name.
//	 */
//	public static void addParameterEvents(MGoal mgoal, MCapability mcapa, List<EventType> events, String paramname, String elemname, ClassLoader cl)
//	{
//		MParameter mparam = mgoal.getParameter(paramname);
//		
//		if(mparam==null)
//			throw new RuntimeException("No such parameter "+paramname+" in "+elemname);
//		
//		events.add(new EventType(new String[]{ChangeEvent.PARAMETERCHANGED, elemname, paramname})); // the whole value was changed
//		events.add(new EventType(new String[]{ChangeEvent.VALUECHANGED, elemname, paramname})); // property change of a value
//		
//		if(mparam.isMulti(cl))
//		{
//			events.add(new EventType(new String[]{ChangeEvent.VALUEADDED, elemname, paramname}));
//			events.add(new EventType(new String[]{ChangeEvent.VALUEREMOVED, elemname, paramname}));
//		}
//	}
	
	/**
	 * Get the mirco agent class.
	 */
	// todo: make use of cache
	protected Class getMicroAgentClass(String clname, String[] imports, ClassLoader classloader)
	{
		Class ret = SReflect.findClass0(clname, imports, classloader);
//		System.out.println(clname+" "+ret+" "+classloader);
		int idx;
		while(ret == null && (idx = clname.indexOf('.')) != -1)
		{
			clname = clname.substring(idx + 1);
			try
			{
				ret = SReflect.findClass0(clname, imports, classloader);
			}
			catch(IllegalArgumentException iae)
			{
				// Hack!!! Sun URL class loader doesn't like if classnames start
				// with (e.g.) 'C:'.
			}
			// System.out.println(clname+" "+cma+" "+ret);
		}
		if(ret == null)
		{
			throw new RuntimeException("BDI agent class not found: " + clname);
		}
		else //if(!MicroAgent.class.isAssignableFrom(ret))
		{
			boolean	found	= false;
			Class	cma	= ret;
			while(!found && cma!=null)
			{
				found = isAnnotationPresent(cma, Agent.class, classloader)
					|| isAnnotationPresent(cma, Capability.class, classloader); 
//				found	=  cma.isAnnotationPresent(Agent.class);
				cma	= cma.getSuperclass();
			}

			if(!found)
			{
				throw new RuntimeException("Not a BDI agent class: " + clname);
			}
		}
		return ret;
	}

	/**
	 *  Create config beliefs.
	 */
	protected List<MConfigBeliefElement> createConfigBeliefsList(NameValue[] values)
	{
		List<MConfigBeliefElement>  ret = null;
		if(values.length>0)
		{
			ret = new ArrayList<MConfigBeliefElement>();
			for(int i=0; i<values.length; i++)
			{
				MConfigBeliefElement	elm	= new MConfigBeliefElement();
				elm.setFlatName(values[i].name());
				elm.addFact(new UnparsedExpression(null, values[i].value()));
				ret.add(elm);
			}
		}
		return ret;
	}
	
	/**
	 *  Create config parameter elements.
	 */
	protected List<MConfigParameterElement> createConfigParamElementsList(NameValue[] values)
	{
		List<MConfigParameterElement>  ret = null;
		if(values.length>0)
		{
			ret = new ArrayList<MConfigParameterElement>();
			for(int i=0; i<values.length; i++)
			{
				String val = values[i].value();
				String clname = values[i].clazz().equals(Object.class) ? null : values[i].clazz().getName();
				String v = (val==null || val.length()==0) ? clname!=null? clname/*+".class"*/: null : val;
				MConfigParameterElement	elm	= new MConfigParameterElement();
				elm.setFlatRef(v==null ? values[i].name() : v);
				ret.add(elm);
			}
		}
		return ret;
	}
	
//	/**
//	 * 
//	 */
//	public static EventType createEventType(RawEvent rawev)
//	{
//		String[] p = new String[2];
//		p[0] = rawev.value();
//		p[1] = Object.class.equals(rawev.secondc())? rawev.second(): rawev.secondc().getName();
////		System.out.println("eveve: "+p[0]+" "+p[1]);
//		return new EventType(p);
//	}
	
	/**
	 *  Do model initialization that can only be done after class reading. 
	 */
	protected void	initBDIModelAfterClassLoading(BDIModel model, ClassLoader cl)
	{
		for(MBelief bel: SUtil.safeList(model.getCapability().getBeliefs()))
		{
			bel.initEvents(model, cl);
		}
		
		// Todo: parameters?
	}
}
