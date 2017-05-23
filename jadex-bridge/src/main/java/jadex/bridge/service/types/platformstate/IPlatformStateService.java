package jadex.bridge.service.types.platformstate;

import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.types.serialization.ISerializationServices;
import jadex.bridge.service.types.transport.ITransportService;
import jadex.commons.Tuple2;

/**
 *  Service providing access to global platform state.
 *
 */
@Service
public interface IPlatformStateService
{
	/**
	 *  Gets the serialization services.
	 *  
	 *  @return The serialization services.
	 */
	public ISerializationServices getSerializationServices();
	
	/**
	 *  Gets the transport cache.
	 *  
	 *  @return Transport cache.
	 */
	public Map<IComponentIdentifier, Tuple2<ITransportService, Integer>> getTransportCache(); 
}
