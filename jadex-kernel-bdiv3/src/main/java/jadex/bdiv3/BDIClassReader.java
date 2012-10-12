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
import jadex.micro.annotation.AgentBody;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javassist.CannotCompileException;
import javassist.ClassClassPath;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtField;
import javassist.CtMethod;
import javassist.bytecode.ClassFile;
import javassist.expr.ExprEditor;
import javassist.expr.FieldAccess;

/**
 *  Reads micro agent classes and generates a model from metainfo and annotations.
 */
public class BDIClassReader extends MicroClassReader
{
	
	/**
	 *  Load the model.
	 */
	protected BDIModel read(String model, Class cma, ClassLoader classloader, IResourceIdentifier rid, IComponentIdentifier root)
	{
		ModelInfo modelinfo = new ModelInfo();
		BDIModel ret = new BDIModel(modelinfo, new MCapability(cma.getName()));
		
		String name = SReflect.getUnqualifiedClassName(cma);
		if(name.endsWith(BDIModelLoader.FILE_EXTENSION_BDIV3_FIRST))
			name = name.substring(0, name.lastIndexOf(BDIModelLoader.FILE_EXTENSION_BDIV3_FIRST));
		String packagename = cma.getPackage()!=null? cma.getPackage().getName(): null;
		modelinfo.setName(name+"BDI");
		modelinfo.setPackage(packagename);
//		modelinfo.setFilename(model);
		String src = SUtil.convertURLToString(cma.getProtectionDomain().getCodeSource().getLocation());
		modelinfo.setFilename(src+File.separator+SReflect.getClassName(cma)+".class");
		modelinfo.setStartable(true);
		modelinfo.setResourceIdentifier(rid);
		ret.setClassloader(classloader);
		
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
		final Set<String> beliefnames = new HashSet<String>();
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
					beliefnames.add(fields[i].getName());
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
						MGoal mgoal = new MGoal(gs[j]);
						tr.addGoal(mgoal);
						if(!micromodel.getCapability().getGoals().contains(mgoal))
						{
							micromodel.getCapability().addGoal(mgoal);
						}
					}
					MPlan mplan = new MPlan(methods[i], tr, p.priority());
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
		String clname = cma.getName()+BDIModelLoader.FILE_EXTENSION_BDIV3_FIRST;
		try
		{
			classloader.loadClass(clname);
			return;
		}
		catch(ClassNotFoundException e)
		{
			try
			{
				// todo: cannot use default pool as model is loaded 2 times with different classloaders ?
//				ClassPool pool = ClassPool.getDefault();
				ClassPool pool = new ClassPool(null);
				pool.insertClassPath(new ClassClassPath(cma));
//				pool.appendSystemPath();
				CtClass clazz = pool.getAndRename(cma.getName(), clname);
				clazz.setSuperclass(pool.getCtClass(cma.getName()));
				clazz.addField(new CtField(getCtClass(BDIAgent.class, pool), "__agent", clazz));
				
				CtMethod[] methods = clazz.getDeclaredMethods();
				Field[] agents = micromodel.getAgentInjections();
			
				// rewrite methods in which beliefs are written
				for(int i=0; i<methods.length; i++)
				{
					// todo: all methods except those that throw events themselves
					if(methods[i].hasAnnotation(AgentBody.class))
					{
						try
						{
							methods[i].instrument(new ExprEditor()
							{
								public void edit(FieldAccess f) throws CannotCompileException
								{
									if(f.isWriter() && beliefnames.contains(f.getFieldName()))
									{
//										f.replace("{System.out.println($1); $_ = $proceed($$);}");
										String rep = "{((jadex.bdiv3.BDIAgent)$0.getClass().getDeclaredField(\"__agent\").get($0)).writeField(jadex.commons.SReflect.wrapValue($1), \""+f.getFieldName()+"\", $0);}"; // $_ = $proceed($$)
//										System.out.println("replace: "+rep);
										f.replace(rep); // $_ = $proceed($$)
									}
								}
							});
						}
						catch(CannotCompileException ex)
						{
							ex.printStackTrace();
						}
					}
				}
				
				ClassFile cf = clazz.getClassFile();
				
				Class ret = clazz.toClass(classloader, cma.getProtectionDomain());
//				System.out.println("fields: "+SUtil.arrayToString(ret.getDeclaredFields()));
//				System.out.println("created: "+ret+" "+classloader);
				clazz.freeze();
		//		System.out.println("name: "+ret.getName()+" "+ret.getPackage()+" "+proxyclazz.getPackageName());
			}
			catch(Exception ex)
			{
				ex.printStackTrace();
			}
		}
	}
	
	/**
	 *  Get a ctclass for a Java class from the pool.
	 *  @param clazz The Java class.
	 *  @param pool The class pool.
	 *  @return The ctclass.
	 */
	protected static CtClass getCtClass(Class clazz, ClassPool pool)
	{
		CtClass ret = null;
		try
		{
			ret = pool.get(clazz.getName());
		}
		catch(Exception e)
		{
			try
			{
				
				ClassPath cp = new ClassClassPath(clazz);
				pool.insertClassPath(cp);
				ret = pool.get(clazz.getName());
			}
			catch(Exception e2)
			{
				throw new RuntimeException(e2);
			}
		}
		return ret;
	}
}
