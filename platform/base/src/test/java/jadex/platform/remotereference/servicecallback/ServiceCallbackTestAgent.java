package jadex.platform.remotereference.servicecallback;

import java.util.Map;

import org.junit.Ignore;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.Boolean3;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.ITuple2Future;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.RequiredService;
import jadex.platform.TestAgent;

/**
 *  Agent to test authentication checks for service invocation. 
 */
@Ignore	// TODO: implement (de)referencing of services as arguments
@Agent(autoprovide=Boolean3.TRUE)
// Larger timeout to capture service search/call timeout
@Properties({@NameValue(name=Testcase.PROPERTY_TEST_TIMEOUT, value="jadex.base.Starter.getScaledDefaultTimeout(null, 1.5)")}) // cannot use $component.getId() because is extracted from test suite :-(
@Service
public class ServiceCallbackTestAgent extends TestAgent	implements ICalledService
{
	//-------- callback impl --------

	/**
	 *  "Reply" from test service.
	 */
	public IFuture<Void> call()
	{
		return IFuture.DONE;
	}

	//-------- test impl --------

	/**
	 *  Execute test locally and remote.
	 */
	@Override
	protected IFuture<TestReport> test(IExternalAccess platform, boolean local)
	{
		Future<TestReport>	ret	= new Future<>(new TestReport(local?"#1":"#2", local?"Test local callback":"Test remote callback"));

		try
		{
			CreationInfo ci = new CreationInfo().setFilename(ServiceCallbackProviderAgent.class.getName()+".class");
			if(local) 
				ci.setParent(agent.getId());
			
			IFuture<IExternalAccess> fut = platform.createComponent(ci);	// Start as subcomponent in local case
			IComponentIdentifier provider = fut.get().getId();
			ICallerService	service	= local ? agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( ICallerService.class)).get()
				: agent.getFeature(IRequiredServicesFeature.class).searchService(new ServiceQuery<>( ICallerService.class, RequiredService.SCOPE_GLOBAL)).get(); // Search globally in remote case.
			service.doCall(this).get();
			platform.getExternalAccess(provider).killComponent().get();
			ret.get().setSucceeded(true);
		}
		catch(Exception e)
		{
			ret.get().setFailed(e);
		}
			
		return ret;
	}
	
//	@Override
//	public IPlatformConfiguration getConfig()
//	{
//		IPlatformConfiguration	config	= super.getConfig();
//		config.setLogging(true);
//		return config;
//	}
}
