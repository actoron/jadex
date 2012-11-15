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
import jadex.micro.annotation.Agent;

import java.io.File;
import java.lang.annotation.Annotation;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.URL;
import java.net.URLClassLoader;
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
	protected BDIModel read(String model, Class cma, ClassLoader classloader, IResourceIdentifier rid, IComponentIdentifier root)
	{
		classloader = ((DummyClassLoader)classloader).getOriginal();
		
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
		
		if(rid==null)
		{
			URL url = cma.getProtectionDomain().getCodeSource().getLocation();
			rid = new ResourceIdentifier(new LocalResourceIdentifier(root, url), null);
		}
		modelinfo.setResourceIdentifier(rid);
		
		fillMicroModelFromAnnotations(ret, model, cma, classloader);
		
		fillBDIModelFromAnnotations(ret, model, cma, classloader);
		
		return ret;
	}
	
	/**
	 *  Fill the model details using annotation.
	 */
	protected void fillBDIModelFromAnnotations(BDIModel micromodel, String model, final Class<?> cma, ClassLoader classloader)
	{
//		ModelInfo modelinfo = (ModelInfo)micromodel.getModelInfo();
		
//		System.out.println("todo: read bdi");
		
//		List<Field> beliefs = new ArrayList<Field>();
//		final Set<String> beliefnames = new HashSet<String>();
//		List<Class> goals = new ArrayList<Class>();
//		List<Method> plans = new ArrayList<Method>();
		
		Class<?> cl = cma;
		while(cl!=null && !cl.equals(Object.class) && !cl.equals(BDIAgent.class))
		{
			Field[] fields = cl.getDeclaredFields();
			for(int i=0; i<fields.length; i++)
			{
				if(fields[i].isAnnotationPresent(Belief.class))
				{
//					System.out.println("found belief: "+fields[i].getName());
					micromodel.getCapability().addBelief(new MBelief(fields[i]));
//					beliefs.add(fields[i]);
//					beliefnames.add(fields[i].getName());
				}
			}
			
			Method[] methods = cl.getDeclaredMethods();
			for(int i=0; i<methods.length; i++)
			{
				if(methods[i].isAnnotationPresent(Plan.class))
				{
//					System.out.println("found plan: "+methods[i].getName());
					MTrigger tr = new MTrigger();
					Plan p = methods[i].getAnnotation(Plan.class);
					Trigger trigger = p.trigger();
					Class<?>[] gs = trigger.goals();
					for(int j=0; j<gs.length; j++)
					{
						Goal ga = gs[j].getAnnotation(Goal.class);
						MGoal mgoal = new MGoal(gs[j], ga.posttoall(), ga.randomselection(), ga.excludemode(), 
							ga.retry(), ga.recur(), ga.retrydelay(), ga.recurdelay());
						tr.addGoal(mgoal);
						
						if(!micromodel.getCapability().getGoals().contains(mgoal))
						{	
							micromodel.getCapability().addGoal(mgoal);
						}
					}
					MPlan mplan = new MPlan(methods[i].getName(), methods[i], tr, p.priority());
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
			
			cl = cl.getSuperclass();
		}

		// Create enhanced class if not already present.
		
//		String origclname = cma.getPackage().getName()+"."+micromodel.getModelInfo().getName();
		String clname = cma.getName();//+BDIModelLoader.FILE_EXTENSION_BDIV3_FIRST;
		try
		{
			classloader.getParent().loadClass(clname);
			return;
		}
		catch(ClassNotFoundException e)
		{
			gen.generateBDIClass(cma, micromodel, classloader.getParent());
		}
	}
}
