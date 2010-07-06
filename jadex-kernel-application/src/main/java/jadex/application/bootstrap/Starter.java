package jadex.application.bootstrap;

import jadex.application.ApplicationComponentFactory;
import jadex.application.runtime.impl.Application;
import jadex.base.fipa.CMSComponentDescription;
import jadex.base.fipa.ComponentIdentifier;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentAdapter;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.ILoadableComponentModel;
import jadex.service.IServiceContainer;
import jadex.service.IServiceProvider;
import jadex.standalone.StandaloneComponentAdapter;

import java.util.logging.Logger;

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
		ILoadableComponentModel model = fac.loadModel("jadex/application/bootstrap/Platform.application.xml", null, null);
		System.out.println("Model: "+model);
//		DummyAdapter da = new DummyAdapter(new IComponentIdentifier()
//		{
//			public String getPlatformName()
//			{
//				return "horst";
//			}
//			
//			public String getName()
//			{
//				return "root@horst";
//			}
//			
//			public String getLocalName()
//			{
//				return "root";
//			}
//			
//			public String[] getAddresses()
//			{
//				return SUtil.EMPTY_STRING_ARRAY;
//			}
//		});
		IComponentIdentifier cid = new ComponentIdentifier("root@platform");
		CMSComponentDescription desc = new CMSComponentDescription(cid, null, null, false, false);
		StandaloneComponentAdapter adapter = new StandaloneComponentAdapter(desc);
		IComponentInstance instance = fac.createComponentInstance(adapter, model, null, null, null);
		adapter.setComponent(instance, model);
//		IApplicationExternalAccess ea = new ExternalAccess((Application)instance);
		System.out.println("Instance: "+instance);
		
		// Initiate first step of root component (i.e. platform).
		instance.executeStep();
		
		long startup = System.currentTimeMillis() - starttime;
		System.out.println("Platform startup time: " + startup + " ms.");
//		platform.logger.info("Platform startup time: " + startup + " ms.");
	}
}

