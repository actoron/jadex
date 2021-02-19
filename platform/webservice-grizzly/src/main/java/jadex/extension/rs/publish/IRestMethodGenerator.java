package jadex.extension.rs.publish;

import java.util.List;
import java.util.Map;

import jadex.bridge.service.IServiceIdentifier;

/**
 *  Rest method generator interface.
 *  The publish service uses the generator to create detailed
 *  information about the methods to publish. The generator
 *  already includes all rest details into the rest method info
 *  that is returned.
 */
public interface IRestMethodGenerator
{
	/**
	 *  Generate the rest method infos.
	 *  @param service The Jadex service. 
	 *  @param classloader The classloader.
	 *  @param baseclass The (abstract or concrete) baseclass or interface.
	 *  @param mapprops Additional mapping properties.
	 *  @return The method infos.
	 *  @throws Exception
	 */
	public List<RestMethodInfo> generateRestMethodInfos(IServiceIdentifier serviceid, ClassLoader classloader, 
		Class<?> baseclass, Map<String, Object> mapprops) throws Exception;
	
}
