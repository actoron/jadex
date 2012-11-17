package jadex.bdiv3;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.model.BDIModel;
import jadex.bdiv3.model.MBelief;
import jadex.bdiv3.model.MCapability;
import jadex.bdiv3.model.MGoal;
import jadex.bdiv3.model.MPlan;
import jadex.bdiv3.model.MTrigger;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.LocalResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.modelinfo.ModelInfo;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.micro.MicroClassReader;
import jadex.micro.MicroModel;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;

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
	protected BDIModel read(String model, Class cma, ClassLoader cl, IResourceIdentifier rid, IComponentIdentifier root)
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
	protected void fillBDIModelFromAnnotations(BDIModel micromodel, String model, final Class<?> cma, ClassLoader cl)
	{
//		ModelInfo modelinfo = (ModelInfo)micromodel.getModelInfo();
		
//		System.out.println("todo: read bdi");
		
//		List<Field> beliefs = new ArrayList<Field>();
//		final Set<String> beliefnames = new HashSet<String>();
//		List<Class> goals = new ArrayList<Class>();
//		List<Method> plans = new ArrayList<Method>();
		
		Class<?> clazz = cma;
		while(clazz!=null && !clazz.equals(Object.class) && !clazz.equals(getClass(BDIAgent.class, cl)))
		{
			Field[] fields = clazz.getDeclaredFields();
			for(int i=0; i<fields.length; i++)
			{
				if(isAnnotationPresent(fields[i], Belief.class, cl))
				{
//					System.out.println("found belief: "+fields[i].getName());
					micromodel.getCapability().addBelief(new MBelief(fields[i]));
//					beliefs.add(fields[i]);
//					beliefnames.add(fields[i].getName());
				}
			}
			
			Method[] methods = clazz.getDeclaredMethods();
			for(int i=0; i<methods.length; i++)
			{
				if(isAnnotationPresent(methods[i], Plan.class, cl))
				{
//					System.out.println("found plan: "+methods[i].getName());
					MTrigger tr = new MTrigger();
					Plan p = getAnnotation(methods[i], Plan.class, cl);
					Trigger trigger = p.trigger();
					Class<?>[] gs = trigger.goals();
					for(int j=0; j<gs.length; j++)
					{
						Goal ga = getAnnotation(gs[j], Goal.class, cl);
						MGoal mgoal = new MGoal(gs[j].getName(), ga.posttoall(), ga.randomselection(), ga.excludemode(), 
							ga.retry(), ga.recur(), ga.retrydelay(), ga.recurdelay());
						tr.addGoal(mgoal);
						
						if(!micromodel.getCapability().getGoals().contains(mgoal))
						{	
							micromodel.getCapability().addGoal(mgoal);
						}
					}
					MPlan mplan = new MPlan(clazz.getName(), methods[i].getName(),  tr, p.priority());
					micromodel.getCapability().addPlan(mplan);
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
			
			clazz = clazz.getSuperclass();
		}

		// Create enhanced class if not already present.
		
		ClassLoader classloader = ((DummyClassLoader)cl).getOriginal();
		Class<?> genclazz = gen.generateBDIClass(cma.getName(), micromodel, classloader);
//		System.out.println("genclazz: "+genclazz);
	}
}
