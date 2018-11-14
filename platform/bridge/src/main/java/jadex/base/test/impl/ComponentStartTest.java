package jadex.base.test.impl;


import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

import jadex.base.Starter;
import jadex.base.test.IAbortableTestSuite;
import jadex.base.test.util.STest;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.LocalResourceIdentifier;
import jadex.bridge.ResourceIdentifier;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.types.factory.SComponentFactory;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.ITuple2Future;
import junit.framework.TestResult;


/**
 * Test if a component can be started.
 */
public class ComponentStartTest extends ComponentTest
{
	// -------- attributes --------

	/** The delay after which the started component is stopped again. */
	// Extra to super.timeout, because timeout is used by super class also to
	// stop init...
	public long delay;

	// -------- constructors --------

	public ComponentStartTest()
	{
		Logger.getLogger("ComponentStartTest").log(Level.SEVERE, "ComponentSTartTest empty constructor called");
	}

	/**
	 * Create a component test.
	 */
	public ComponentStartTest(IExternalAccess platform, IModelInfo comp, IAbortableTestSuite suite)
	{
		super(platform, comp, suite);
		// Hack???
		delay = 500; // Do not use scaled default timeout, because delay of
						// Timeout.NONE makes no sense.
	}

	// -------- methods --------

	/**
	 * Called when a component has been started.
	 * 
	 * @param cid The cid, set as soon as known.
	 */
	protected void componentStarted(ITuple2Future<IComponentIdentifier, Map<String, Object>> fut)
	{
		try
		{
			final IComponentIdentifier cid = fut.getFirstResult();

			// Wait some time (simulation and real time) and kill the component
			// afterwards.
			final IResultListener<Void> lis = new CounterResultListener<Void>(1, new DefaultResultListener<Void>()
			{
				public synchronized void resultAvailable(Void result)
				{
					// if(cid.getName().indexOf("ParentProcess")!=-1)
					// System.out.println("destroying "+cid);
					if(platform != null)
					{
						// if(cid.getName().indexOf("ParentProcess")!=-1)
						// System.out.println("destroying1 "+cid);
						try
						{
							platform.killComponent(cid).get();
						}
						catch(ComponentTerminatedException e)
						{
							// ignore, if agent killed itself already
						}
						// if(cid.getName().indexOf("ParentProcess")!=-1)
						// System.out.println("destroying2 "+cid);
					}
				}

				@Override
				public void exceptionOccurred(Exception exception)
				{
					System.err.println("COULD NOT STOP COMPONENT!! Exception:");
					super.exceptionOccurred(exception);
				}
			});
			// {
			// @Override
			// public void resultAvailable(Void result)
			// {
			//// if(cid.getName().indexOf("ParentProcess")!=-1)
			// System.out.println("waiting returned for "+cid);
			// super.resultAvailable(result);
			// }
			// };

			platform.scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					// if(cid.getName().indexOf("ParentProcess")!=-1)
					// System.out.println("waiting false for "+cid);
					return ia.getFeature(IExecutionFeature.class).waitForDelay(delay, false);
				}
			}).addResultListener(lis);
			
			platform.scheduleStep(new IComponentStep<Void>()
			{
				public IFuture<Void> execute(IInternalAccess ia)
				{
					// if(cid.getName().indexOf("ParentProcess")!=-1)
					// System.out.println("waiting true for "+cid);
					return ia.getFeature(IExecutionFeature.class).waitForDelay(delay, true);
				}
			}).addResultListener(lis);
		}
		catch(ComponentTerminatedException cte)
		{
			// Ignore if component already terminated.
		}
		catch(RuntimeException e)
		{
			// Ignore if component already terminated.
			if(!(e.getCause() instanceof ComponentTerminatedException))
			{
				throw e;
			}
		}
	}

	/**
	 * Optional checking after component has finished.
	 * 
	 * @param res The results.
	 */
	protected void checkTestResults(Map<String, Object> res)
	{
		// Nop.
	}

	/**
	 * Get a string representation of this test.
	 */
	public String toString()
	{
		return "start: " + super.toString();
	}

	/**
	 * Command line test: start the given agent in the given rid
	 * 
	 * @param args Two arguments representing the component file name and the
	 *        rid file name.
	 */
	public static void main(String[] args) throws IOException
	{
		IExternalAccess platform = Starter.createPlatform(STest.getDefaultTestConfig()).get();
//		IComponentManagementService cms = platform.searchService(new ServiceQuery<>(IComponentManagementService.class)).get();

		String filename = null;
		String ridname = null;
		if(args.length == 2)
		{
			filename = args[0];
			ridname = args[1];
		}
		else if(args.length == 0)
		{
			filename = "jadex/bdi/tutorial/TranslationA1.agent.xml";
			ridname = "../jadex-applications-bdi/target/classes";
		}
		else
		{
			System.out.println("Usage: ComponentStartTest <model file name> <RID file/directory name>");
			System.exit(0);
		}

		IResourceIdentifier rid = new ResourceIdentifier(new LocalResourceIdentifier(platform.getId(), new File(ridname).getCanonicalFile().toURI()), null);
//		IModelInfo model = cms.loadComponentModel(filename, rid).get();
		IModelInfo model = SComponentFactory.loadModel(platform, filename, rid).get();

		ComponentStartTest test = new ComponentStartTest(platform, model, null);

		TestResult result = test.run();

		System.out.println("Result (run/error/failure): " + result.runCount() + "/" + result.errorCount() + "/" + result.failureCount());

		platform.killComponent().get();
	}
}