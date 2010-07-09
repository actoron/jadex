package jadex.application.bootstrap;

import jadex.application.ApplicationComponentFactory;
import jadex.base.fipa.CMSComponentDescription;
import jadex.base.fipa.ComponentIdentifier;
import jadex.bridge.IComponentFactory;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentInstance;
import jadex.bridge.ILoadableComponentModel;
import jadex.standalone.StandaloneComponentAdapter;

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
		
		// Create an instance of the platform.
		IComponentFactory fac = new ApplicationComponentFactory(null);
		ILoadableComponentModel model = fac.loadModel("jadex/application/bootstrap/Platform.application.xml", null, Starter.class.getClassLoader());
//		System.out.println("Model: "+model);
		IComponentIdentifier cid = new ComponentIdentifier("root@platform");
		CMSComponentDescription desc = new CMSComponentDescription(cid, null, null, false, false);
		StandaloneComponentAdapter adapter = new StandaloneComponentAdapter(desc, null);
		IComponentInstance instance = fac.createComponentInstance(adapter, model, null, null, null);
		adapter.setComponent(instance, model);
//		System.out.println("Instance: "+instance);
		
		// Initiate first step of root component (i.e. platform).
		adapter.execute();
		
		long startup = System.currentTimeMillis() - starttime;
		System.out.println("Platform startup time: " + startup + " ms.");
//		platform.logger.info("Platform startup time: " + startup + " ms.");
	}
}

