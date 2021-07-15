package jadex.extension.rs.publish.mapper;

import java.util.List;
import java.util.Map;

import jadex.commons.Tuple2;

/**
 *  The parameter mapper interface is used for mappers that
 *  take as input a parameter array and produce a converted one
 *  as output (n:m).
 */
public interface IParameterMapper2
{
	/**
	 *  Convert parameters.
	 *  @param values The values map to convert.
	 *  @param pinfos The parameter infos (i.e. annotation meta info). 
	 *  				List<Tuple2<String, String>>: says "kind of param" name, path form, query, no and name of parameter 
	 *  				Map<String, Class<?>>: says for this named param use this type (from method param)
	 *  @param context The context (could be the http servlet request or a custom container request).
	 *  @return The converted parameters.
	 */
	public Object[] convertParameters(Map<String, Object> values, Tuple2<List<Tuple2<String, String>>, Map<String, Class<?>>> pinfos, Object request) throws Exception;
}

