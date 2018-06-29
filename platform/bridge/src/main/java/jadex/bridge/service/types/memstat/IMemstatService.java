package jadex.bridge.service.types.memstat;

import java.util.Map;

import jadex.commons.future.IFuture;

/**
 *  Service for providing debugging infos for a component or service,
 *  e.g. for detecting memory leaks.
 */
public interface IMemstatService
{
	/**
	 *  Get info about stored data like connections and listeners.
	 */
	// For detecting/debugging memory leaks
	public IFuture<Map<String, Object>>	getMemInfo();
}
