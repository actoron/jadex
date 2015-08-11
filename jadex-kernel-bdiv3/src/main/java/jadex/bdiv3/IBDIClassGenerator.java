package jadex.bdiv3;

import java.util.List;

import jadex.bdiv3.model.BDIModel;

/**
 * 
 */
public interface IBDIClassGenerator
{
	public final static String DYNAMIC_BELIEF_UPDATEMETHOD_PREFIX = "__update";
	
	public final static String INIT_EXPRESSIONS_METHOD_PREFIX = "__init_expressions";
	
	/**
	 *  Generate class, including inner classes.
	 *  @return the List of classes generated.
	 */
	public List<Class<?>> generateBDIClass(String clname, BDIModel micromodel, ClassLoader cl);
}
