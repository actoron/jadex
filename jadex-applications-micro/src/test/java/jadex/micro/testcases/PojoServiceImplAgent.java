package jadex.micro.testcases;


import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Agent that uses agent class as implementation declaration for a service.
 */
@Description("Agent that uses agent class as implementation declaration for a service.")
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class)) 
@ProvidedServices(@ProvidedService(type=IAService.class))//, implementation=@Implementation(PojoServiceImplAgent.class)))
public class PojoServiceImplAgent implements IAService
{
	@Agent 
	protected IInternalAccess agent;
	
//	public PojoServiceImplAgent()
//	{
//		System.out.println("constructor called");
//	}
	
	/** 
	 * The body.
	 */
	@AgentBody
	public void body()
	{
		IProvidedServicesFeature psf = agent.getComponentFeature(IProvidedServicesFeature.class);
		Object serimpl = psf.getProvidedServiceRawImpl(IAService.class);
		TestReport	tr	= new TestReport("#1", "Test if impl is pojo object.");
		if(serimpl==this)
		{
			tr.setSucceeded(true);
		}
		else
		{
			tr.setFailed("Wrong service impl object: "+serimpl);
		}
		agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
		agent.killComponent();
	}
	
	/**
	 *  Dummy test method.
	 */
	public IFuture<Void> test()
	{
		return IFuture.DONE;
	}
}
