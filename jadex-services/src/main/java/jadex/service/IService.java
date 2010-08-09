package jadex.service;


/**
 *  The interface for platform services.
 */
public interface IService
{
	//-------- constants --------
	
	/** The from proxy excluded methods. */
	public static String REMOTE_EXCLUDED = "remote_excluded";
	
//	/** The from proxy unsupported methods. */
//	public static String UNSUPPORTED = "unsupported";
	
	/** Empty service array. */
	public static IService[] EMPTY_SERVICES = new IService[0];
	
	/**
	 *  Get the service identifier.
	 *  @return The service identifier.
	 */
	public IServiceIdentifier getServiceIdentifier();
	
	/**
	 *  Get a service property.
	 *  @return The service property (if any).
	 */
	public Object getProperty(String name);
}
