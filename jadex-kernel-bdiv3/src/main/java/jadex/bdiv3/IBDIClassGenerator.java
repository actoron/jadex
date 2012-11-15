package jadex.bdiv3;

import jadex.bdiv3.model.BDIModel;

/**
 * 
 */
public interface IBDIClassGenerator
{
	/**
	 *  Generate class.
	 */
	public Class<?> generateBDIClass(Class<?> cma, BDIModel micromodel, ClassLoader cl);
}
