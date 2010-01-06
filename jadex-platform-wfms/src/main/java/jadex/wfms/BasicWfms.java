package jadex.wfms;

import java.util.logging.Logger;

import jadex.commons.Properties;
import jadex.commons.SUtil;
import jadex.javaparser.SimpleValueFetcher;
import jadex.service.PropertiesXMLHelper;
import jadex.service.PropertyServiceContainer;


/**
 *  Basic wfms implementation.
 */
public class BasicWfms extends PropertyServiceContainer
{
	//-------- constants --------
	
	/** The configuration file. */
	public static final String CONFIGURATION = "conf";
	
	/** The wfms. */
	public static final String WFMS = "wfms";
	
	/** The fallback configuration. */
	public static final String FALLBACK_CONFIGURATION = "jadex/wfms/wfms_conf.xml";
	
	/** The service(s). */
	public static final String SERVICES = "services";
	
	//-------- attributes --------
	
	/** The logger. */
	protected Logger logger;
	
	//-------- constructors --------
	
	/**
	 *  Create a new wfms.
	 */ 
	public BasicWfms(Properties props)
	{
		SimpleValueFetcher fetcher = new SimpleValueFetcher();
		fetcher.setValue("$wfms", this);
		init(props!=null? props.getSubproperty(WFMS).getSubproperties(SERVICES): null, fetcher, null);
	}
	
	//-------- methods --------
	
	/**
	 *  Get the platform logger.
	 *  @return The platform logger.
	 */
	public Logger getLogger()
	{
		if(logger==null)
			this.logger = Logger.getLogger("Wfms"); // + getName()
		return logger;
	}
	
	//-------- static part --------
	
	/**
	 *  Main for starting the wfms.
	 */
	public static void main(String[] args) throws Exception
	{
		// Absolute start time (for testing and benchmarking).
		long starttime = System.currentTimeMillis();
		
		// Initialize platform configuration from args.
		String conffile = FALLBACK_CONFIGURATION;
		if(args.length>0 && args[0].equals("-"+CONFIGURATION))
		{
			conffile = args[1];
			String[] tmp= new String[args.length-2];
			System.arraycopy(args, 2, tmp, 0, args.length-2);
			args = tmp;
		}
		// Create an instance of the platform.
		// Hack as long as no loader is present.
		ClassLoader cl = BasicWfms.class.getClassLoader();
		Properties configuration = (Properties)PropertiesXMLHelper.getPropertyReader().read(SUtil.getResource(conffile, cl), cl, null);
		BasicWfms wfms = new BasicWfms(configuration);
		wfms.start();
		
		long startup = System.currentTimeMillis() - starttime;
		wfms.getLogger().info("Wfms startup time: " + startup + " ms.");
		
	}
}
