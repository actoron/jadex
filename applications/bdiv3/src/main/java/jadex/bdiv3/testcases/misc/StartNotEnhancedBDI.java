package jadex.bdiv3.testcases.misc;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IInternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.search.ServiceQuery;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.Boolean3;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test using injected values in init expressions or constructors.
 */
@Agent(keepalive=Boolean3.FALSE)
@Results(@Result(name="testresults", clazz=Testcase.class))
public class StartNotEnhancedBDI	extends ConstructorsSuper
{
	/** The agent. */
	@Agent
	protected IInternalAccess	agent;
	
	/**
	 *  Agent body.
	 */
	@AgentBody//(keepalive=false)
	public void	body()
	{
		TestReport	tr	= new TestReport("#1", "Test if constructor calls work.");
		IComponentManagementService cms = agent.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM));
		try
		{
			Class<?> cl = NotEnhancedBDI.class;
			cl.getField("__agent");
			// if field is found test cannot be performed because class was already loaded
			tr.setSucceeded(true);
		}
		catch(Exception ex)
		{
			try
			{
				IResourceIdentifier rid = agent.getModel().getResourceIdentifier();
				IComponentIdentifier cid = cms.createComponent(NotEnhancedBDI.class.getName()+".class", new CreationInfo(rid)).getFirstResult();
				System.out.println("cid: "+cid);
				tr.setFailed("BDI agent was created although class was not enhanced.");
			}
			catch(Exception e)
			{
				tr.setSucceeded(true);
	//			e.printStackTrace();
			}
		}
		agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
	}
}
