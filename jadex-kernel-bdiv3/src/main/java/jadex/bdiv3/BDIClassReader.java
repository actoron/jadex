package jadex.bdiv3;

import jadex.bdiv3.annotation.BDIConfiguration;
import jadex.bdiv3.annotation.BDIConfigurations;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Plans;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.model.BDIModel;
import jadex.bdiv3.model.MBelief;
import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MConfiguration;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.model.MTrigger;
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
import jadex.commons.FieldInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.micro.MicroClassReader;
import jadex.micro.MicroModel;
import jadex.micro.annotation.Component;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.NameValue;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.Publish;
import jadex.micro.annotation.RequiredService;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

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
		
		Class<?> clazz = cma;
		while(clazz!=null && !clazz.equals(Object.class) && !clazz.equals(getClass(BDIAgent.class, cl)))
		{
			// Find beliefs
			Field[] fields = clazz.getDeclaredFields();
			for(int i=0; i<fields.length; i++)
			{
				if(isAnnotationPresent(fields[i], Belief.class, cl))
				{
//					System.out.println("found belief: "+fields[i].getName());
					Belief bel = getAnnotation(fields[i], Belief.class, cl);
					bdimodel.getCapability().addBelief(new MBelief(new FieldInfo(fields[i]), 
						bel.implementation().getName().equals(Object.class.getName())? null: bel.implementation().getName()));
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
					MTrigger mtr = buildPlanTrigger(bdimodel, p, cl);
					MPlan mplan = new MPlan(methods[i].getName(), new jadex.bdiv3.model.MethodInfo(methods[i]), mtr, p.priority());
					bdimodel.getCapability().addPlan(mplan);
				}
			}
			
			// Find external plans
			if(isAnnotationPresent(clazz, Plans.class, cl))
			{
				Plan[] plans = getAnnotation(clazz, Plans.class, cl).value();
				for(Plan p: plans)
				{
					Class<?> bodycl = p.body();
					MTrigger mtr = buildPlanTrigger(bdimodel, p, cl);
					MPlan mplan = new MPlan(SReflect.getInnerClassName(bodycl), new ClassInfo(bodycl.getName()), mtr, p.priority());
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

		ModelInfo modelinfo = (ModelInfo)bdimodel.getModelInfo();
		if(confs.size()>0)
		{
			modelinfo.setConfigurations((ConfigurationInfo[])confs.values().toArray(new ConfigurationInfo[confs.size()]));
			bdimodel.getCapability().setConfigurations(bdiconfs);
		}

		// Create enhanced class if not already present.
		
		ClassLoader classloader = ((DummyClassLoader)cl).getOriginal();
		Class<?> genclazz = gen.generateBDIClass(cma.getName(), bdimodel, classloader);
//		System.out.println("genclazz: "+genclazz);
	}
	
	/**
	 * 
	 */
	protected MTrigger buildPlanTrigger(BDIModel bdimodel, Plan p, ClassLoader cl)
	{
		MTrigger tr = new MTrigger();
		Trigger trigger = p.trigger();
		Class<?>[] gs = trigger.goals();
		for(int j=0; j<gs.length; j++)
		{
			Goal ga = getAnnotation(gs[j], Goal.class, cl);
			MGoal mgoal = new MGoal(gs[j].getName(), ga.posttoall(), ga.randomselection(), ga.excludemode(), 
				ga.retry(), ga.recur(), ga.retrydelay(), ga.recurdelay(), ga.succeedonpassed(), ga.unique());
			tr.addGoal(mgoal);
			
			if(!bdimodel.getCapability().getGoals().contains(mgoal))
			{	
				bdimodel.getCapability().addGoal(mgoal);
			}
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
}
