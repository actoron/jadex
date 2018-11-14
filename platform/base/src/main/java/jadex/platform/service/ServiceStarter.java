package jadex.platform.service;

import org.apache.commons.daemon.Daemon;
import org.apache.commons.daemon.DaemonContext;
import org.apache.commons.daemon.DaemonInitException;

import jadex.base.IPlatformConfiguration;
import jadex.base.PlatformConfigurationHandler;
import jadex.base.Starter;
import jadex.bridge.IExternalAccess;


/**
 * Class for starting/stopping jadex as a service / daemon.
 */
public class ServiceStarter implements Daemon
{
	// -------- static part --------

	/** Singleton of service handler. */
	protected static ServiceStarter	instance;

	/**
	 *  Static method called by prunsrv to start the Windows service.
	 */
	public static void start(String args[])
	{
		instance	= new ServiceStarter(args);
		instance.doStart();
		
//		// Wait for platform to exit.
//		final Future<Void> exit = new Future<Void>();
//		
//		IComponentManagementService cms = instance.platform.getServiceProvider().searchService( new ServiceQuery<>( IComponentManagementService.class))
//			.get(new ThreadSuspendable());
//		cms.addComponentResultListener(new IResultListener<Collection<Tuple2<String, Object>>>()
//		{
//			public void resultAvailable(Collection<Tuple2<String, Object>> result)
//			{
//				exit.setResult(null);
//			}
//
//			public void exceptionOccurred(Exception exception)
//			{
//				exit.setException(exception);
//			}
//		}, instance.platform.getComponentIdentifier());
//		
//		exit.get(new ThreadSuspendable());
	}

	/**
	 *  Static method called by prunsrv to stop the Windows service.
	 */
	public static void stop(String args[])
	{
		instance.doStop();
	}

	// -------- attributes --------

	/** The jadex platform. */
	protected IExternalAccess	platform;
	
	/** The arguments for starting jadex. */
	protected String[]	args;
	
	//-------- constructors --------
	
	/**
	 * Bean constructor for Unix daemon.
	 */
	public ServiceStarter()
	{
		// args are initialized in init below
	}
	
	/**
	 *  Constructor for Windows service.
	 */
	public ServiceStarter(String[] args)
	{
		this.args	= args;
	}
	
	//-------- Daemon interface --------

	/**
	 * Initializes this <code>Daemon</code> instance.
	 * <p>
	 * This method gets called once the JVM process is created and the
	 * <code>Daemon</code> instance is created thru its empty public
	 * constructor.
	 * </p>
	 * <p>
	 * Under certain operating systems (typically Unix based operating systems)
	 * and if the native invocation framework is configured to do so, this
	 * method might be called with <i>super-user</i> privileges.
	 * </p>
	 * <p>
	 * For example, it might be wise to create <code>ServerSocket</code>
	 * instances within the scope of this method, and perform all operations
	 * requiring <i>super-user</i> privileges in the underlying operating
	 * system.
	 * </p>
	 * <p>
	 * Apart from set up and allocation of native resources, this method must
	 * not start the actual operation of the <code>Daemon</code> (such as
	 * starting threads calling the <code>ServerSocket.accept()</code> method)
	 * as this would impose some serious security hazards. The start of
	 * operation must be performed in the <code>start()</code> method.
	 * </p>
	 * 
	 * @param context A <code>DaemonContext</code> object used to communicate with the container.
	 * @exception DaemonInitException An exception that prevented initialization
	 *            where you want to display a nice message to the user, rather than a stack trace.
	 * @exception Exception Any exception preventing a successful initialization.
	 */
	public void init(DaemonContext context) throws DaemonInitException, Exception
	{
		this.args	= context.getArguments();
	}

	/**
	 * Starts the operation of this <code>Daemon</code> instance. This method is
	 * to be invoked by the environment after the init() method has been
	 * successfully invoked and possibly the security level of the JVM has been
	 * dropped. Implementors of this method are free to start any number of
	 * threads, but need to return control after having done that to enable
	 * invocation of the stop()-method.
	 */
	public void start() throws Exception
	{
		doStart();
	}

	/**
	 * Stops the operation of this <code>Daemon</code> instance. Note that the
	 * proper place to free any allocated resources such as sockets or file
	 * descriptors is in the destroy method, as the container may restart the
	 * Daemon by calling start() after stop().
	 */
	public void stop() throws Exception
	{
		doStop();
	}


	/**
	 * Frees any resources allocated by this daemon such as file descriptors or
	 * sockets. This method gets called by the container after stop() has been
	 * called, before the JVM exits. The Daemon can not be restarted after this
	 * method has been called without a new call to the init() method.
	 */
	public void destroy()
	{
	}
	
	/**
	 *  Do the actual starting.
	 *  Separate method to get rid of exception.
	 */
	protected void	doStart()
	{
		if(platform==null)
		{
			IPlatformConfiguration	config	= getConfig();
//			config.setProgramArguments(args);
//			Starter.parseArgs(args, config);
			
			platform = Starter.createPlatform(config, args).get();
		}		
	}
	
	/**
	 *  Get the platform configuration.
	 *  Subclass and override for specific use cases.
	 */
	protected IPlatformConfiguration	getConfig()
	{
		return PlatformConfigurationHandler.getDefaultNoGui();
	}

	/**
	 *  Do the actual starting.
	 *  Separate method to get rid of exception.
	 */
	protected void	doStop()
	{
		if(platform!=null)
		{
			platform.killComponent();	// Todo: wait for killing to complete?
			platform	= null;
		}		
	}
}
