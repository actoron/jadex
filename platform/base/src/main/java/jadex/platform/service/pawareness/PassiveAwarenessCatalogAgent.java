package jadex.platform.service.pawareness;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jadex.bridge.BasicComponentIdentifier;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.address.TransportAddress;
import jadex.bridge.service.types.pawareness.IPassiveAwarenessService;
import jadex.commons.Boolean3;
import jadex.commons.collection.MultiCollection;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.micro.annotation.AgentCreated;

/**
 *  Passive awareness based on a pre-defined catalog of platforms + addresses.
 *  Platforms are specified as URLs as defined in IPlatformCatalogService.
 *
 */
@Service
@Agent(autoprovide = Boolean3.TRUE,
	predecessors="jadex.platform.service.address.TransportAddressAgent",
	successors="jadex.platform.service.registryv2.SuperpeerClientAgent",
	autostart=Boolean3.TRUE
)
public class PassiveAwarenessCatalogAgent implements IPassiveAwarenessService
{
	protected static final String DEFAULT_URLS = "ws://ssp1@ngrelay1.actoron.com:80";
	
	/** Platform URL pattern. */
	protected static final Pattern URL_PATTERN = Pattern.compile("[a-zA-Z]+://[a-zA-Z0-9]+@.+:[0-9]+");
	
	/** The agent access. */
	@Agent
	protected IInternalAccess	agent;
	
	/** The internal catalog. */
	protected MultiCollection<IComponentIdentifier, TransportAddress> catalog = new MultiCollection<>();
	
	@AgentArgument
	protected String platformurls;
	
	/**
	 *  Creates the catalog agent empty.
	 */
	public PassiveAwarenessCatalogAgent()
	{
		
	}
	
	/**
	 *  Agent start.
	 *  
	 *  @return Null, when done.
	 */
	@AgentCreated
	public IFuture<Void> start()
	{
		if(platformurls==null)
			platformurls = DEFAULT_URLS;
		
		String[] spliturls = platformurls.split(",");
		for (int i = 0; i < spliturls.length; ++i)
		{
			addPlatform(spliturls[i].trim());
		}
		return IFuture.DONE;
	}
	
	/**
	 *  Adds a platform to the catalog.
	 *  
	 *  @param platformurl URL of the platform.
	 *  @return Null, when done.
	 */
	public IFuture<Void> addPlatform(String platformurl)
	{
		TransportAddress addr = parseUrl(platformurl);
		
		if (addr != null)
		{
			catalog.add(addr.getPlatformId(), addr);
		}
		
		return IFuture.DONE;
	}
	
	/**
	 *  Removes a platform from the catalog.
	 *  
	 *  @param name Name of the platform.
	 *  @return Null, when done.
	 */
	public IFuture<Void> removePlatform(String name)
	{
		IComponentIdentifier id = new BasicComponentIdentifier(name);
		Collection<TransportAddress> addrs = catalog.remove(id);
		if (addrs == null)
		{
			TransportAddress addr = parseUrl(name);
			if (addr != null)
			{
				catalog.removeObject(addr.getPlatformId(), addr);
			}
		}
		return IFuture.DONE;
	}
	
	/**
	 *  Try to find other platforms and finish after timeout.
	 *  Immediately returns known platforms and concurrently issues a new search, waiting for replies until the timeout.
	 */
	public IIntermediateFuture<IComponentIdentifier> searchPlatforms()
	{
		IntermediateFuture<IComponentIdentifier> ret = new IntermediateFuture<>();
		for (IComponentIdentifier id : catalog.keySet())
			ret.addIntermediateResult(id);
		return ret;
	}
	
	/**
	 *  Gets the address for a platform ID using the awareness mechanism.
	 * 
	 *  @param platformid The platform ID.
	 *  @return The transport addresses or null if not available.
	 */
	public IFuture<List<TransportAddress>> getPlatformAddresses(IComponentIdentifier platformid)
	{
		Future<List<TransportAddress>> ret = new Future<>();
		Collection<TransportAddress> addrs = catalog.get(platformid);
		if (addrs != null)
			ret.setResult(new ArrayList<>(addrs));
		else
			ret.setResult(null);
		return ret;
	}
	
	/**
	 *  Parse a platform URL.
	 *  
	 *  @param url The URL.
	 *  @return The transport address.
	 */
	protected TransportAddress parseUrl(String url)
	{
		TransportAddress ret = null;
		Matcher m = URL_PATTERN.matcher(url);
		if (m.matches())
		{
			int protend = url.indexOf(':');
			String prot = url.substring(0, protend);
			int nameend = url.indexOf('@', protend + 1);
			String name = url.substring(protend + 3, nameend);
			String addr = url.substring(nameend + 1);
			
			IComponentIdentifier relayid = new BasicComponentIdentifier(name);
			if (!agent.getId().getRoot().equals(relayid))
			{
				ret = new TransportAddress(new BasicComponentIdentifier(name), prot, addr);
			}
			// else ignore self
		}
		else
		{
			agent.getLogger().warning("Invalid platform URL: " + url + ". Format: <transport>://<platformname>@<hostname>:<port>");
		}

		return ret;
	}
}
