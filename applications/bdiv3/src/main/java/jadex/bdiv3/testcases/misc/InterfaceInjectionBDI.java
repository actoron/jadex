package jadex.bdiv3.testcases.misc;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.IBDIAgent;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

@Agent(type=BDIAgentFactory.TYPE, keepalive=Boolean3.FALSE)
@Results(@Result(name="testresults", clazz=Testcase.class))
public abstract class InterfaceInjectionBDI implements IBDIAgent
{
	/**
	 *  Agent body.
	 */
	@AgentBody
	public void	body(IInternalAccess ia)
	{
		TestReport tr1 = new TestReport("#1", "Test if interface injection works.");
		if(getId()!=null)
		{
			tr1.setSucceeded(true);
		}
		else
		{
			tr1.setFailed("Problem with agent api.");
		}
		System.out.println("my name is: "+getId());

		TestReport tr2 = new TestReport("#2", "Test if platform access interface injection works.");
		try
		{
			IComponentManagementService	cms	= this.getFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM));
			if(cms!=null)
			{
				tr2.setSucceeded(true);
			}
			else
			{
				tr2.setFailed("Problem with agent api.");
			}
		}
		catch(Exception e)
		{
			tr2.setFailed(e);			
		}
		
		getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(2, new TestReport[]{tr1, tr2}));
	}
}
