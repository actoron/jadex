package jadex.bdiv3;

import java.util.List;

import jadex.bdiv3.exceptions.JadexBDIGenerationException;
import jadex.bdiv3.model.BDIModel;

/**
 *  Interface for BDI class enhancement/generation.
 */
public interface IBDIClassGenerator
{
	/**
	 * Name of the field that is injected for agent name
	 */
	public final static String AGENT_FIELD_NAME = "__agent";

	/**
	 * Name of the field that is injected for globalname
	 */
	public final static  String GLOBALNAME_FIELD_NAME = "__globalname";

	public final static String DYNAMIC_BELIEF_UPDATEMETHOD_PREFIX = "__update";
	
	public final static String INIT_EXPRESSIONS_METHOD_PREFIX = "__init_expressions";
	
	/**
	 *  Generate class, including inner classes.
	 *  @return the List of classes generated.
	 */
	public List<Class<?>> generateBDIClass(String clname, BDIModel micromodel, ClassLoader dummycl) throws JadexBDIGenerationException;
}
