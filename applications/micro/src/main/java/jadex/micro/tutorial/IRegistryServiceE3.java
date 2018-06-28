package jadex.micro.tutorial;

import java.util.Map;

import jadex.bridge.IComponentIdentifier;
import jadex.commons.future.IFuture;

/**
 *  Simple registry service.
 */
public interface IRegistryServiceE3
{
	/** 
	 *  Register a chatter. 
	 */
	public void register(IComponentIdentifier cid, String nickname);
	
	/**
	 *  Get the registered chatters.
	 */
	public IFuture<Map<String, IComponentIdentifier>> getChatters();
}

