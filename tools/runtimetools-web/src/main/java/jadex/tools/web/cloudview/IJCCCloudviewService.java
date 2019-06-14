package jadex.tools.web.cloudview;

import java.util.Map;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;
import jadex.tools.web.jcc.IJCCPluginService;

@Service(system=true)
public interface IJCCCloudviewService extends IJCCPluginService
{
	/**
	 *  Get the networks of platforms.
	 *  @return The networks.
	 */
	public IFuture<Map<String, String[]>> getPlatformNetworks(String cid);
}
