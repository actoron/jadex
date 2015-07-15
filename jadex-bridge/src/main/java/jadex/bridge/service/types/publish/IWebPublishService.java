package jadex.bridge.service.types.publish;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

import java.net.URI;

/**
 * 
 */
@Service
public interface IWebPublishService extends IPublishService
{
//	/**
//	 *  Publish a servlet (without ressources).
//	 */
//	public IFuture<Void> publishServet(URI uri, Object servlet);
//	
	/**
	 *  Publish permanent redirect.
	 */
	public IFuture<Void> publishRedirect(URI uri, final String html);
	
	/**
	 *  Publish a static page (without ressources).
	 */
	public IFuture<Void> publishHMTLPage(URI uri, String vhost, String html);
	
	/**
	 *  Publish file resources from the classpath.
	 */
	public IFuture<Void> publishResources(URI uri, String rootpath);
	
	/**
	 *  Publish file resources from the file system.
	 */
	public IFuture<Void> publishExternal(URI uri, String rootpath);
}
