package jadex.platform.service.servicepool;

import java.util.ArrayList;
import java.util.List;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.Boolean3;
import jadex.commons.DefaultPoolStrategy;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

@Agent(autoprovide=Boolean3.TRUE)
@Results(@Result(name="testresults", description= "The test results.", clazz=Testcase.class))
@Service
public class CreationTest extends JunitAgentTest
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  The agent body. 
	 */
	@AgentBody
	public void body()
	{
		final List<TestReport> results = new ArrayList<TestReport>();
		
		TestReport tr1 = new TestReport("#1", "Test service pool creation with serviceinfo works.");
		try
		{
			PoolServiceInfo spi = new PoolServiceInfo(AAgent.class.getName()+".class", IAService.class, new DefaultPoolStrategy(10, 10000, 20));
			IExternalAccess ea = agent.createComponent(new CreationInfo().setFilenameClass(ServicePoolAgent.class).addArgument("serviceinfos", new PoolServiceInfo[]{spi})).get();
			//System.out.println("Created: "+ea);
			ea.killComponent().get();
			tr1.setSucceeded(true);
		}
		catch(Exception e)
		{
			tr1.setReason("Exception occurred: "+e);
		}
		results.add(tr1);
		
		TestReport tr2 = new TestReport("#2", "Test service pool creation with service tags works.");
		try
		{
			PoolServiceInfo spi = new PoolServiceInfo(CAgent.class.getName()+".class", ICService.class, new DefaultPoolStrategy(10, 10000, 20));
			IExternalAccess ea = agent.createComponent(new CreationInfo().setFilenameClass(ServicePoolAgent.class).addArgument("serviceinfos", new PoolServiceInfo[]{spi})).get();
			
//			IExternalAccess ea = agent.createComponent(new CreationInfo().setFilenameClass(CAgent.class)).get();
			//System.out.println("Created: "+ea);
			
			// Should be able to find pool service with tag
			// there was a bug in ServiceRegistry.checkRestrictions() in publication scope check
			// did not resolve ServiceScope.DEFAULT to PLATFORM
			ICService c = agent.searchService(new ServiceQuery<>(ICService.class).setServiceTags("tag1")).get();
			
			if(c!=null)
				tr2.setSucceeded(true);
			else
				tr2.setFailed("service not found");
		}
		catch(Exception e)
		{
			tr2.setReason("Exception occurred: "+e);
		}
		results.add(tr2);
		
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(results.size(), 
			(TestReport[])results.toArray(new TestReport[results.size()])));
		agent.killComponent();
	}
	
}
