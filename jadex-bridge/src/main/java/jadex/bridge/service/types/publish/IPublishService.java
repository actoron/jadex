package jadex.bridge.service.types.publish;

import jadex.bridge.service.IService;
import jadex.bridge.service.IServiceIdentifier;
import jadex.bridge.service.PublishInfo;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.IFuture;

/**
 *  Service for publishing services in other technologies such as web services.
 */
@Service
public interface IPublishService
{
	/** The publish type web service. */
	public static final String PUBLISH_WS = "ws";
	
	/** The publish type rest service. */
	public static final String PUBLISH_RS = "rs";
	
	/** The default publish implementations for rest. */
	public static final String[] DEFAULT_RSPUBLISH_COMPONENTS = new String[]
	{
		"/jadex/extension/rs/publish/JettyRSPublishAgent.class",
		"/jadex/extension/rs/publish/GrizzlyRSPublishAgent.class",
		"/jadex/extension/rs/publish/LegacyGrizzlyRSPublishAgent.class",
		"/jadex/extension/rs/publish/ExternalRSPublishAgent.class"
	};
	
	/**
	 *  Test if publishing a specific type is supported (e.g. web service).
	 *  @param publishtype The type to test.
	 *  @return True, if can be published.
	 */
	public IFuture<Boolean> isSupported(String publishtype);
	
	/**
	 *  Publish a service.
	 *  @param service The original service.
	 *  @param pid The publish id (e.g. url or name).
	 */
	public IFuture<Void> publishService(IServiceIdentifier serviceid, PublishInfo pi);
	
	/**
	 *  Unpublish a service.
	 *  @param sid The service identifier.
	 */
	public IFuture<Void> unpublishService(IServiceIdentifier sid);
}
