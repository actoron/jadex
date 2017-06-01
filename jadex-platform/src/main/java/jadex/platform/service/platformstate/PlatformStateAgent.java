package jadex.platform.service.platformstate;

import java.util.Collections;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Reference;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.platformstate.IPlatformStateService;
import jadex.bridge.service.types.serialization.ISerializationServices;
import jadex.bridge.service.types.transport.ITransportService;
import jadex.commons.Tuple2;
import jadex.commons.collection.LRU;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.platform.service.serialization.SerializationServices;


/**
 *  Agent managing internal platform state.
 *
 */
@Agent
@Service
@ProvidedServices(
	@ProvidedService(
		type=IPlatformStateService.class,
		scope=RequiredServiceInfo.SCOPE_PLATFORM
	)
)
public class PlatformStateAgent implements IPlatformStateService
{
	/** Size of the transport cache, move to config/make configurable? */
	protected static final int TRANSPORT_CACHE_SIZE = 1000;
	
	/** The serialization services. */
	protected ISerializationServices serialservs;
	
	/** The transport cache. */
	protected Map<IComponentIdentifier, Tuple2<ITransportService, Integer>> transportcache;
	
	public PlatformStateAgent()
	{
		serialservs = new SerializationServices();
		
		transportcache = Collections.synchronizedMap(new LRU<IComponentIdentifier, Tuple2<ITransportService, Integer>>(TRANSPORT_CACHE_SIZE));
	}
	
	/**
	 *  Gets the serialization services.
	 *  
	 *  @return The serialization services.
	 */
	public @Reference ISerializationServices getSerializationServices()
	{
		return serialservs;
	}
	
	/**
	 *  Gets the transport cache.
	 *  
	 *  @return Transport cache.
	 */
	public @Reference Map<IComponentIdentifier, Tuple2<ITransportService, Integer>> getTransportCache()
	{
		return transportcache;
	}
}
