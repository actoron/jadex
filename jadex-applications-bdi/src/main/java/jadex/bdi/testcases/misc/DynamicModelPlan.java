package jadex.bdi.testcases.misc;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;

/**
 *  Create and test a hello world agent.
 */
public class DynamicModelPlan extends Plan
{
	//-------- attributes --------
	
	/** The test report. */
	protected TestReport	tr	= new TestReport("#1", "Test dynamic model creation.");
	
	//-------- methods --------
	
	/**
	 *  Perform the test.
	 */
	public void body()
	{
		IDynamicBDIFactory	fac	= (IDynamicBDIFactory)getInterpreter().getComponentFeature(IRequiredServicesFeature.class).getRequiredService("factory").get();
		IMECapability agent = fac.createAgentModel("HelloWorld", "jadex.bdi.examples.helloworld", null, getScope().getModel().getResourceIdentifier()).get();
			
		IMEBelief	msgbelief	= agent.createBeliefbase().createBelief("msg");
		msgbelief.createFact("\"Welcome to editable models!\"", null);
			
		IMEPlan helloplan = agent.createPlanbase().createPlan("hello");
		helloplan.createBody("HelloWorldPlan", null);
		IMEConfiguration conf = agent.createConfiguration("default");
		conf.createInitialPlan("hello");
			
		fac.registerAgentModel(agent, "helloagent.agent.xml").get();

		IComponentManagementService cms	= (IComponentManagementService)getInterpreter().getComponentFeature(IRequiredServicesFeature.class).getRequiredService("cms").get();

		Future	finished	= new Future();
		IComponentIdentifier hwc = (IComponentIdentifier)cms.createComponent("hw1", "helloagent.agent.xml", new CreationInfo(getComponentIdentifier()), new DelegationResultListener(finished)).get();

		finished.get();
		
		tr.setSucceeded(true);
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
	
	/**
	 *  Test failed.
	 */
	public void failed()
	{
		getException().printStackTrace();
		tr.setFailed(""+getException());
		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
	}
}
