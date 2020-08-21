package jadex.bridge.service.types.publish;

import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 *  Publish something to the internal or external http server.
 */
@Service(system=true)
public interface IWebPublishService extends IPublishService
{
	/**
	 *  Publish a static page (without ressources).
	 */
	public IFuture<Void> publishHMTLPage(String uri, String vhost, String html);
	
	/**
	 *  Publish file resources from the classpath.
	 */
	public IFuture<Void> publishResources(String uri, String rootpath);
	
	/**
	 *  Turn on or off the login security.
	 *  If true one has to log in with platform secret before using published services.
	 *  @param sec On or off.
	 */
	public IFuture<Void> setLoginSecurity(boolean sec);
	
	/**
	 *  Log in to the platform.
	 *  @param request The request.
	 *  @param secret The platform secret.
	 *  @return True, if login was successful.
	 * /
	public IFuture<Boolean> login(HttpServletRequest request, String secret);*/
	
	/**
	 *  Logout from the platform.
	 *  @param secret The platform secret.
	 *  @return True, if login was successful.
	 * /
	public IFuture<Boolean> logout(HttpServletRequest request);*/
	
	/**
	 *  Log in to the platform.
	 *  @param platformpass The platform password.
	 *  @return True, if login was successful.
	 * /
	public IFuture<Boolean> logIn(String platformpass);*/
	
	//-------- old (currently unsupported!) --------
	
//	/**
//	 *  Publish a servlet (without ressources).
//	 */
//	public IFuture<Void> publishServet(URI uri, Object servlet);
//	
//	/**
//	 *  Publish permanent redirect.
//	 */
//	public IFuture<Void> publishRedirect(URI uri, final String html);
//	
//	/**
//	 *  Publish file resources from the file system.
//	 */
//	public IFuture<Void> publishExternal(URI uri, String rootpath);
//	
//	/**
//	 *  Unpublish a service.
//	 *  @param sid The service identifier.
//	 */
//	public IFuture<Void> unpublishService(IServiceIdentifier sid);
//	
//	/**
//	 *  Unpublish an already-published handler.
//	 *  @param vhost The virtual host, if any, null for general.
//	 *  @param uti The uri being unpublished.
//	 */
//	public IFuture<Void> unpublish(String vhost, URI uri);
//	
//	/**
//	 *  Mirror an existing http server.
//	 *  @param sourceserveruri The URI of the server being mirrored.
//	 *  @param targetserveruri The URI of the mirror server.
//	 *  @param info Publish infos for the mirror server.
//	 */
//	public IFuture<Void> mirrorHttpServer(URI sourceserveruri, URI targetserveruri, PublishInfo info);
//	
//	/**
//	 *  Explicitely terminated an existing http server.
//	 *  @param uri URI of the server.
//	 */
//	public IFuture<Void> shutdownHttpServer(URI uri);
}
