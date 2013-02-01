package jadex.bdiv3;

import jadex.bdiv3.model.BDIModel;

/**
 * 
 */
public interface IBDIClassGenerator
{
	public final static String DYNAMIC_BELIEF_UPDATEMETHOD_PREFIX = "__update";
	
	/**
	 *  Generate class.
	 */
	public Class<?> generateBDIClass(String clname, BDIModel micromodel, ClassLoader cl);
}
