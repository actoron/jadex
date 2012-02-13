package jadex.extension.rs.publish;

import jadex.bridge.service.IService;

import java.util.List;
import java.util.Map;

/**
 * 
 */
public interface IRestMethodGenerator
{
	/**
	 * 
	 * @param service
	 * @param classloader
	 * @param baseclass
	 * @param mapprops
	 * @return
	 * @throws Exception
	 */
	public List<RestMethodInfo> generateRestMethodInfos(IService service, ClassLoader classloader, 
		Class<?> baseclass, Map<String, Object> mapprops) throws Exception;

}
