package jadex.application.bootstrap;

import jadex.application.ApplicationComponentFactory;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentInstance;
import jadex.bridge.ILoadableComponentModel;

/**
 *  Starter class for  
 */
public class Starter
{
	/**
	 *  Main for starting the platform (with meaningful fallbacks)
	 *  @param args The arguments.
	 *  @throws Exception
	 */
	public static void main(String[] args) throws Exception
	{
		// Absolute start time (for testing and benchmarking).
		long starttime = System.currentTimeMillis();
		
		// Initialize platform configuration from args.
//		String[] conffiles;
//		if(args.length>0 && args[0].equals("-"+CONFIGURATION))
//		{
//			conffiles = new String[args.length-1];
//			System.arraycopy(args, 1, conffiles, 0, args.length-1);
//		}
//		else if(args.length>0)
//		{
//			conffiles = args;
//		}
//		else
//		{
//			conffiles = new String[]
//			{
//				FALLBACK_SERVICES_CONFIGURATION,
//				FALLBACK_STANDARDCOMPONENTS_CONFIGURATION,
//				FALLBACK_APPLICATION_CONFIGURATION,
//				FALLBACK_BDI_CONFIGURATION,
//				FALLBACK_MICRO_CONFIGURATION,
//				FALLBACK_BPMN_CONFIGURATION,
//				FALLBACK_BDIBPMN_CONFIGURATION
//			};
//		}
		
		// Create an instance of the platform.
		// Hack as long as no loader is present.
		ClassLoader cl = Starter.class.getClassLoader();

		IComponentFactory fac = new ApplicationComponentFactory(null);
		ILoadableComponentModel model = fac.loadModel("jadex.application.bootstrap.Platform", null, null);
		System.out.println("Model: "+model);
		IComponentInstance instance = fac.createComponentInstance(null, model, null, null, null);
		System.out.println("Instance: "+instance);
		
		long startup = System.currentTimeMillis() - starttime;
		System.out.println("Platform startup time: " + startup + " ms.");
//		platform.logger.info("Platform startup time: " + startup + " ms.");
	}
}
