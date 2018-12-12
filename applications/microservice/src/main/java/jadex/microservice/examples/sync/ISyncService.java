package jadex.microservice.examples.sync;

import jadex.bridge.service.annotation.Service;

/**
 *  Example interface for service.
 */
@Service
public interface ISyncService
{
	/**
	 *  Say hello method.
	 *  @param name The name.
	 */
	public String sayHello(String name);
}
