package jadex.extension;

import javassist.ClassClassPath;
import javassist.ClassPath;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtMethod;
import javassist.bytecode.ConstPool;
import javassist.bytecode.MethodInfo;
import javassist.bytecode.ParameterAnnotationsAttribute;
import javassist.bytecode.annotation.Annotation;

/**
 * 
 */
public class SJavassist
{
	/**
	 * 
	 * @param method
	 * @param annotations
	 * @param pool
	 * @throws Exception
	 */
//	public static void addMethodParameterAnnotation(final CtMethod method, 
//		Class[][] annotations, ClassPool pool) throws Exception
	public static void addMethodParameterAnnotation(final CtMethod method, 
		Annotation[][] annotations, ClassPool pool) throws Exception
	{
		MethodInfo mi = method.getMethodInfo();
		ParameterAnnotationsAttribute attribute = (ParameterAnnotationsAttribute)mi
			.getAttribute(ParameterAnnotationsAttribute.visibleTag);
		ConstPool cp = method.getMethodInfo().getConstPool();
		if(attribute == null) 
			attribute = new ParameterAnnotationsAttribute(cp, ParameterAnnotationsAttribute.visibleTag);
//		javassist.bytecode.annotation.Annotation[][] res = new javassist.bytecode.annotation.Annotation[annotations.length][];
		
//		for(int i=0; i<annotations.length; i++) 
//		{
//			javassist.bytecode.annotation.Annotation[] pa = new javassist.bytecode.annotation.Annotation[annotations[i].length];
//			for(int j=0; j<annotations[i].length; j++) 
//			{
//				pa[j] = new Annotation(cp, getCtClass(annotations[i][j], pool));
//			}
//			res[i] = pa;
//		}
		
		attribute.setAnnotations(annotations);
		mi.addAttribute(attribute);
	}
	
	// Does not work, why?
//	ParameterAnnotationsAttribute pai = new ParameterAnnotationsAttribute(mcp, AnnotationsAttribute.visibleTag);
//	annot = new Annotation(mcp, getCtClass(Reference.class, pool));
//	annot.addMemberValue("local", new BooleanMemberValue(true, mcp));
//	annot.addMemberValue("remote", new BooleanMemberValue(true, mcp));
//	annot.addMemberValue("value", new StringMemberValue("arg", constpool));
//	Annotation[][] dest = new Annotation[pcnt][1];
//	dest[0] = new Annotation[0];//{annot};
//	pai.setAnnotations(dest);
	
	/**
	 *  Get a ctclass for a Java class from the pool.
	 *  @param clazz The Java class.
	 *  @param pool The class pool.
	 *  @return The ctclass.
	 */
	public static CtClass getCtClass(Class clazz, ClassPool pool)
	{
		if(clazz==null)
			throw new IllegalArgumentException("Class must not null.");
		
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
	
	/**
	 *  Get a ctclass for a Java class from the pool.
	 *  @param clazz The Java class.
	 *  @param pool The class pool.
	 *  @return The ctclass.
	 */
	public static CtClass getCtClass(String clname, ClassPool pool)
	{
		if(clname==null)
			throw new IllegalArgumentException("Class must not null.");
		
		CtClass ret = null;
		try
		{
			ret = pool.get(clname);
		}
		catch(Exception e)
		{
			try
			{
//				ClassPath cp = new ClassClassPath(clazz);
//				pool.insertClassPath(cp);
				ret = pool.get(clname);
			}
			catch(Exception e2)
			{
				throw new RuntimeException(e2);
			}
		}
		return ret;
	}
	
	/**
	 *  Get a ctclass array for a class array.
	 *  @param classes The classes.
	 *  @param pool The pool.
	 *  @return The ctclass array.
	 */
	public static CtClass[] getCtClasses(Class[] classes, ClassPool pool)
	{
		CtClass[] ret = new CtClass[classes.length];
		for(int i=0; i<classes.length; i++)
		{
			ret[i] = getCtClass(classes[i], pool);
		}
		return ret;	
	}
	
}
