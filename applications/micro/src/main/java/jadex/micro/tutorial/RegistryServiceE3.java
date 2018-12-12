package jadex.micro.tutorial;

import java.util.HashMap;
import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

/**
 *  Service for registering a nickname with component identifier.
 */
@Service
public class RegistryServiceE3 implements IRegistryServiceE3
{
	//-------- attributes --------
	
	/** The entries map. */
	protected Map<String, IComponentIdentifier> entries = new HashMap<String, IComponentIdentifier>();
	
	//-------- methods --------

	/** 
	 *  Register a chatter. 
	 */
	public void register(IComponentIdentifier cid, String nickname)
	{
		entries.put(nickname, cid);
	}
	
	/**
	 *  Get the registered chatters.
	 */
	public IFuture<Map<String, IComponentIdentifier>> getChatters()
	{
		return new Future<Map<String, IComponentIdentifier>>(entries);
	}
}
