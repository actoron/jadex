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
		StandaloneComponentAdapter adapter = new StandaloneComponentAdapter(null, desc);
		IComponentInstance instance = fac.createComponentInstance(adapter, model, null, null, null);
		IServiceContainer sc = ((Application)instance).internalGetServiceContainer();
		adapter.setComponent(instance, model);
		adapter.setContainer(sc);
//		IApplicationExternalAccess ea = new ExternalAccess((Application)instance);
		System.out.println("Instance: "+instance);
		
		while(instance.executeStep())
		{
			System.out.println("executing step");
		}
		
		long startup = System.currentTimeMillis() - starttime;
		System.out.println("Platform startup time: " + startup + " ms.");
//		platform.logger.info("Platform startup time: " + startup + " ms.");
	}
}

/**
 * 
 */
class DummyAdapter implements IComponentAdapter
{
	//-------- attributes --------

	/** The container. */
	protected transient IServiceProvider provider;

	/** The component identifier. */
	protected IComponentIdentifier cid;

	/** The component instance. */
	protected IComponentInstance component;
	
	/** The component model. */
	protected ILoadableComponentModel model;

	//-------- constructors --------

	/**
	 *  Create a new component adapter.
	 *  Uses the thread pool for executing the component.
	 */
	public DummyAdapter(IComponentIdentifier cid)
	{
		this.cid = cid;
	}
	
	/**
	 *  Set the component.
	 *  @param component The component to set.
	 */
	public void setComponent(IComponentInstance component, ILoadableComponentModel model, IServiceProvider provider)
	{
		this.component = component;
		this.model = model;
		this.provider = provider;
	}	
	
	/**
	 *  Called by the component when it probably awoke from an idle state.
	 *  The platform has to make sure that the component will be executed
	 *  again from now on.
	 *  Note, this method can be called also from external threads
	 *  (e.g. property changes). Therefore, on the calling thread
	 *  no component related actions must be executed (use some kind
	 *  of wake-up mechanism).
	 *  Also proper synchronization has to be made sure, as this method
	 *  can be called concurrently from different threads.
	 */
	public void	wakeup() throws ComponentTerminatedException
	{
		
	}
	
	/**
	 *  Execute an action on the component thread.
	 *  May be safely called from any (internal or external) thread.
	 *  The contract of this method is as follows:
	 *  The component adapter ensures the execution of the external action, otherwise
	 *  the method will throw a terminated exception.
	 *  @param action The action to be executed on the component thread.
	 */
	public void invokeLater(Runnable action)
	{
		System.out.println("invokeLater: "+action);
		action.run();
	}
	
	/**
	 *  Check if the external thread is accessing.
	 *  @return True, if called from an external (i.e. non-synchronized) thread.
	 */
	public boolean isExternalThread()
	{
		return false;
	}

	/**
	 *  Cause termination of the component.
	 *  IKernelComponent.killComponent(IResultListener) will be
	 *  called in turn.
	 * /
	public void killComponent()	throws ComponentTerminatedException;*/

	/**
	 *  Get the component platform.
	 *  @return The component platform.
	 */
	public IServiceProvider getRootServiceProvider()	throws ComponentTerminatedException
	{
		return provider;
	}

	/**
	 *  Return the native component-identifier that allows to send
	 *  messages to this component.
	 */
	public IComponentIdentifier getComponentIdentifier() throws ComponentTerminatedException
	{
		return cid;
	}
	
	/**
	 *  Get the component logger.
	 *  @return The logger.
	 */
	public Logger getLogger()
	{
		return Logger.getAnonymousLogger();
	}
}
