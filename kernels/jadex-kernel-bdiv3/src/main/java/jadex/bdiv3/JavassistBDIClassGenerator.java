package jadex.bdiv3;


/**
 * 
 */
public class JavassistBDIClassGenerator //implements IBDIClassGenerator
{
	
}
//{
//	/**
//	 *  Generate class.
//	 */
//	public Class<?> generateBDIClass(Class<?> cma, final BDIModel micromodel, ClassLoader cl)
//	{
//		Class<?> ret = null;
//		
//		try
//		{
//			String clname = cma.getName()+BDIModelLoader.FILE_EXTENSION_BDIV3_FIRST;
//			// todo: cannot use default pool as model is loaded 2 times with different classloaders ?
////			ClassPool pool = ClassPool.getDefault();
//			ClassPool pool = new ClassPool(null);
//			pool.insertClassPath(new ClassClassPath(cma));
////			pool.appendSystemPath();
//			CtClass clazz = pool.getAndRename(cma.getName(), clname);
//			clazz.setSuperclass(pool.getCtClass(cma.getName()));
//			clazz.addField(new CtField(getCtClass(BDIAgent.class, pool), "__agent", clazz));
//			
//			CtMethod[] methods = clazz.getDeclaredMethods();
//			Field[] agents = micromodel.getAgentInjections();
//		
//			// rewrite methods in which beliefs are written
//			for(int i=0; i<methods.length; i++)
//			{
//				// todo: all methods except those that throw events themselves
////				if(methods[i].hasAnnotation(AgentBody.class))
//				{
//					try
//					{
//						methods[i].instrument(new ExprEditor()
//						{
//							public void edit(FieldAccess f) throws CannotCompileException
//							{
//								if(f.isWriter() && micromodel.getCapability().hasBelief(f.getFieldName()))
//								{
////									f.replace("{System.out.println($1); $_ = $proceed($$);}");
//									String rep = "{((jadex.bdiv3.BDIAgent)$0.getClass().getDeclaredField(\"__agent\").get($0)).writeField(jadex.commons.SReflect.wrapValue($1), \""+f.getFieldName()+"\", $0);}"; // $_ = $proceed($$)
////									System.out.println("replace: "+rep);
//									f.replace(rep); // $_ = $proceed($$)
//								}
//							}
//						});
//					}
//					catch(CannotCompileException ex)
//					{
//						ex.printStackTrace();
//					}
//				}
//			}
//			
//			ClassFile cf = clazz.getClassFile();
//			
//			ret = clazz.toClass(cl, cma.getProtectionDomain());
////			System.out.println("fields: "+SUtil.arrayToString(ret.getDeclaredFields()));
////			System.out.println("created: "+ret+" "+classloader);
//			clazz.freeze();
//	//		System.out.println("name: "+ret.getName()+" "+ret.getPackage()+" "+proxyclazz.getPackageName());
//		}
//		catch(Exception ex)
//		{
//			ex.printStackTrace();
//		}
//		
//		return ret;
//	}
//	
//	/**
//	 *  Get a ctclass for a Java class from the pool.
//	 *  @param clazz The Java class.
//	 *  @param pool The class pool.
//	 *  @return The ctclass.
//	 */
//	protected static CtClass getCtClass(Class clazz, ClassPool pool)
//	{
//		CtClass ret = null;
//		try
//		{
//			ret = pool.get(clazz.getName());
//		}
//		catch(Exception e)
//		{
//			try
//			{
//				
//				ClassPath cp = new ClassClassPath(clazz);
//				pool.insertClassPath(cp);
//				ret = pool.get(clazz.getName());
//			}
//			catch(Exception e2)
//			{
//				throw new RuntimeException(e2);
//			}
//		}
//		return ret;
//	}
//}
