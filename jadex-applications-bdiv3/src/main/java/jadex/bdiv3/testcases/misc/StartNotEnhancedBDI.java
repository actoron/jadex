package jadex.bdiv3.testcases.misc;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.BDIAgent;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test using injected values in init expressions or constructors.
 */
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class StartNotEnhancedBDI	extends ConstructorsSuper
{
	/** The agent. */
	@Agent
	protected BDIAgent	agent;
	
	/**
	 *  Agent body.
	 */
	@AgentBody(keepalive=false)
	public void	body()
	{
		TestReport	tr	= new TestReport("#1", "Test if constructor calls work.");
		IComponentManagementService cms = SServiceProvider.getLocalService(agent.getServiceProvider(), IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM);
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
		agent.setResultValue("testresults", new Testcase(1, new TestReport[]{tr}));
	}
}
