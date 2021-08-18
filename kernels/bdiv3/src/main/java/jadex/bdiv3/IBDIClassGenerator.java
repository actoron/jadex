package jadex.bdiv3;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.Field;
import java.util.List;

import jadex.bdiv3.exceptions.JadexBDIGenerationException;
import jadex.bdiv3.model.BDIModel;
import jadex.commons.SClassReader;
import jadex.commons.SClassReader.ClassFileInfo;
import jadex.commons.SClassReader.ClassInfo;
import jadex.commons.SClassReader.FieldInfo;
import jadex.commons.SUtil;

/**
 *  Interface for BDI class enhancement/generation.
 */
public interface IBDIClassGenerator
{
	/** Name of the field that is injected for agent name. */
	public final static String AGENT_FIELD_NAME = "__agent";

	/** Name of the field that is injected for globalname. */
	public final static String GLOBALNAME_FIELD_NAME = "__globalname";
	
	/** Name of the field in which the initargs are injected. */
	public final static String INITARGS_FIELD_NAME = "__initargs";

	public final static String DYNAMIC_BELIEF_UPDATEMETHOD_PREFIX = "__update";
	
	public final static String INIT_EXPRESSIONS_METHOD_PREFIX = "__init_expressions";
	
	/**
	 *  Generate class, including inner classes.
	 *  @return the List of classes generated.
	 */
	public List<Class<?>> generateBDIClass(String clname, BDIModel micromodel, ClassLoader dummycl) throws JadexBDIGenerationException;

	/**
	 * Returns whether a class is already enhanced.
	 * @param clazz
	 * @return true, if already enhanced, else false.
	 */
	public static boolean isEnhanced(Class<?> clazz)
	{
		boolean isEnhanced = false;
		try 
		{
//			Field field = clazz.getField(AGENT_FIELD_NAME);
			Field field = clazz.getField(GLOBALNAME_FIELD_NAME);
			isEnhanced = true;
		} 
		catch (NoSuchFieldException ex) 
		{
		}
		return isEnhanced;
	}
	
	/**
	 * Returns whether a class is already enhanced.
	 * @param clazz The clazz info.
	 * @return true, if already enhanced, else false.
	 */
	public static boolean isEnhanced(ClassFileInfo clazzfileinfo)
	{
		boolean ret = false;
		ClassInfo clazz = clazzfileinfo.getClassInfo();
		List<FieldInfo> fis = clazz.getFieldInfos();
		if(fis == null)
		{
			try
			{
				clazz = SClassReader.getClassInfo(new FileInputStream(clazzfileinfo.getFilename()), true, false);
			}
			catch (FileNotFoundException e)
			{
				SUtil.throwUnchecked(e);
			}
			fis = clazz.getFieldInfos();
		}
		for(FieldInfo fi: SUtil.notNull(fis))
		{
			if(GLOBALNAME_FIELD_NAME.equals(fi.getFieldName()))
			{
				ret = true;
				break;
			}
		}
		return ret;
	}
	
	/**
	 *  Check if a bdi agent class was enhanced.
	 *  @throws RuntimeException if was not enhanced.
	 */
	public static void checkEnhanced(Class<?> clazz)
	{
		// check if agentclass is bytecode enhanced
		try
		{
			clazz.getField(AGENT_FIELD_NAME);
		}
		catch(Exception e)
		{
			throw new RuntimeException("BDI agent class was not bytecode enhanced: " + clazz.getName() + " This may happen if the class is accessed directly in application code before loadModel() was called.");
		}
	}
}
