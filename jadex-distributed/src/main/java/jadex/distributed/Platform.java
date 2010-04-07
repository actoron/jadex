package jadex.distributed;

import jadex.commons.Properties;
import jadex.service.IServiceContainer;

/**
 *  Built-in standalone component platform.
 */
public class Platform extends jadex.base.Platform
{
	//-------- constants --------

	/** The fallback configuration for basic services. */
	public static final String FALLBACK_SERVICES_CONFIGURATION = "jadex/standalone/services_conf.xml";

	/** The fallback configuration for standard components (cms/df/jcc). */
	public static final String FALLBACK_STANDARDCOMPONENTS_CONFIGURATION = "jadex/standalone/platformcomponents_conf.xml";

	//-------- constructors --------

	/**
	 *  Create a new Platform.
	 */
	public Platform(String conffile, ClassLoader classloader) throws Exception
	{
		this(new String[]{conffile}, classloader);
	}
	
	/**
	 *  Create a new Platform.
	 */
	public Platform(String[] conffiles, ClassLoader classloader) throws Exception
	{
		this(readProperties(conffiles, classloader));
	}
	
	/**
	 *  Create a new Platform.
	 */
	public Platform(Properties[] configurations)
	{
		this(configurations, null);
	}
	
	/**
	 *  Create a new Platform.
	 */
	public Platform(Properties[] configurations, IServiceContainer parent)
	{
		super(configurations, parent);
	}

	//-------- methods --------
	
	/**
	 *  Keep platform from being garbage collected, when created using main().
	 *  Useful for debugging, profiling etc.
	 */
	private static Platform	platform;
	
	/**
	 *  Main for starting the platform (with meaningful fallbacks)
	 *  @param args The arguments.
	 *  @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		//System.out.println(System.getProperty("user.dir"));
		/* TODO der Pfad zur management.properties für JMX durch die JVM Option -D in der Client.launch
		      funktioniert zwar in diesem develope environment, muss aber noch für ein production
		      environment angepasst werden; das mit target/... ist einfach nur ugly
		      
		      mit diesem ugly weg gibt es zwei möglichkeiten der JVM die JMX config file zu geben
		       - -Dcom.sun.management.config.file=target/classes/jadex/distributed/config/jmx/management.properties
		       - -Dcom.sun.management.config.file=src/main/java/jadex/distributed/config/jmx/management.properties
		       
		       die management.properties kümmert sich um die komplette konfiguration von JMX,
		       inklusive -Dcom.sun.management.jmxremote.port=<port-number> und andere
		   
		   TODO die zweite:
		   man kann bei JMX die Passwortabfrage über SSL deaktivieren, was ich hier in dem
		   development environment nun auch getan habe
		*/
		
		// Absolute start time (for testing and benchmarking).
		long starttime = System.currentTimeMillis();
		
		// Initialize platform configuration from args.
		String[] conffiles;
		if(args.length>0 && args[0].equals("-"+CONFIGURATION))
		{
			conffiles = new String[args.length-1];
			System.arraycopy(args, 1, conffiles, 0, args.length-1);
		}
		else if(args.length>0)
		{
			conffiles = args;
		}
		else
		{
			conffiles = new String[]
			{
				FALLBACK_SERVICES_CONFIGURATION,
				FALLBACK_STANDARDCOMPONENTS_CONFIGURATION,
				FALLBACK_APPLICATION_CONFIGURATION,
				FALLBACK_BDI_CONFIGURATION,
				FALLBACK_MICRO_CONFIGURATION,
				FALLBACK_BPMN_CONFIGURATION,
				FALLBACK_BDIBPMN_CONFIGURATION
			};
		}
		
		// Create an instance of the platform.
		// Hack as long as no loader is present.
		ClassLoader cl = Platform.class.getClassLoader();
		platform = new Platform(conffiles, cl);
		platform.start();
		
		long startup = System.currentTimeMillis() - starttime;
		platform.logger.info("Platform startup time: " + startup + " ms.");
	}
}

