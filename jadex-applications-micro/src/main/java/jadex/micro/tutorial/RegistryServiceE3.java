package jadex.micro.tutorial;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.util.HashMap;
import java.util.Map;

/**
 *  Service for registering a nickname with component identifier.
 */
@Service
public class RegistryServiceE3 implements IRegistryServiceE3
{
	//-------- attributes --------
	
	/** The entries map. */
	protected Map entries = new HashMap();
	
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
	public IFuture getChatters()
	{
		return new Future(entries);
	}
}
