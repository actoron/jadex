package jadex.bridge.service.types.persistence;

import jadex.bridge.IComponentIdentifier;

/**
 *  Called when a component becomes idle.
 *  Runs on thread of persistence service
 *  (i.e. platform thread).
 */
public interface IIdleHook
{
	/**
	 *  Called when a component becomes active.
	 */
	public void	componentActive(IComponentIdentifier cid);
	
	/**
	 *  Called when a component becomes idle.
	 */
	public void	componentIdle(IComponentIdentifier cid);
}