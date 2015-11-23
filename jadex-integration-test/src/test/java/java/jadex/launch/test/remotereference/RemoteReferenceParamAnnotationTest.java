package jadex.launch.test.remotereference;

import jadex.base.Starter;
import jadex.bridge.IComponentStep;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;


/**
 * Test if a remote references are correctly transferred and mapped back. On
 * platform1 there is a serviceA provider. On platform2 there is a search
 * component which searches for serviceA and return it as result. Tests if the
 * result of the remote search yields the same local service proxy.
 */
public class RemoteReferenceParamAnnotationTest // extends TestCase
{
	Future<Boolean> successIndicator;

	private IExternalAccess	platform2;

	private IExternalAccess	platform1;

	long timeout = Starter.getLocalDefaultTimeout(null);

	@Before
	public void initPlatforms()
	{
		// reset indicator
		successIndicator = new Future<Boolean>();

		platform1 = Starter.createPlatform(
			new String[]{"-platformname", "testcases_*", "-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-gui", "false", "-awareness", "false", "-printpass", "false",
				"-component", "jadex/launch/test/remotereference/LocalServiceProviderAgent.class"}).get(timeout);
		timeout	= Starter.getLocalDefaultTimeout(platform1.getComponentIdentifier());
		
		// Find local service (as local provided service proxy).
		ILocalService service1 = SServiceProvider.getService(platform1, ILocalService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(timeout);

		platform2 = Starter.createPlatform(
			new String[]{"-platformname", "testcases_*", "-saveonexit", "false", "-welcome", "false", "-autoshutdown", "false", "-gui", "false", "-awareness", "false", "-printpass", "false",
				"-component", "jadex/launch/test/remotereference/SearchServiceProviderAgent.class"}).get(timeout);

		// Connect platforms by creating proxy agents.
		// Connect platforms by creating proxy agents.
		Starter.createProxy(platform1, platform2).get(timeout);
		Starter.createProxy(platform2, platform1).get(timeout);
	}

	@Test
	public void testRemoteReferenceCall_withTypeAnnotation()
	{
		// schedule on platform, so platform2platform communication is used.
		platform2.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				Future<Void> ret = new Future<Void>();
				ILocalService locService = SServiceProvider.getService(ia.getExternalAccess(), ILocalService.class, RequiredServiceInfo.SCOPE_GLOBAL).get();
				// call service with @Reference Object
				locService.executeCallback(new MyCallbackReference()).addResultListener(new DelegationResultListener<Void>(ret));
				;
				return ret;
			}
		}).get();

		Assert.assertTrue(successIndicator.isDone());
		Assert.assertTrue(successIndicator.get());
	}

	// @Test
	public void testRemoteReferenceCall_withMethodAnnotation()
	{
		platform2.scheduleStep(new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				Future<Void> ret = new Future<Void>();
				ILocalService locService = SServiceProvider.getService(ia.getExternalAccess(), ILocalService.class, RequiredServiceInfo.SCOPE_GLOBAL).get();
				// call service without @Reference Object
				locService.executeCallback(new MyCallback()).addResultListener(new DelegationResultListener<Void>(ret));
				;
				return ret;
			}
		}).get();

		Assert.assertTrue(successIndicator.isDone());
		Assert.assertTrue(successIndicator.get());

	}

	@After
	public void shutdownPlatforms()
	{
		// Kill platforms and end test case.
		platform1.killComponent().get(timeout);
		platform2.killComponent().get(timeout);
	}

	public class MyCallback implements ICallback
	{
		public IFuture<Void> call()
		{
			System.out.println("setting result");
			successIndicator.setResult(true);
			return Future.DONE;
		}
	}

	public class MyCallbackReference implements ICallbackReference
	{
		public IFuture<Void> call()
		{
			System.out.println("setting result");
			successIndicator.setResult(true);
			return Future.DONE;
		}
	}

	/**
	 * Execute in main to have no timeouts.
	 */
	public static void main(String[] args)
	{
		RemoteReferenceTest test = new RemoteReferenceTest();
		test.testRemoteReference();
	}


}
