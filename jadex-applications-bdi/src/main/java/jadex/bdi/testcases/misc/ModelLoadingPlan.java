package jadex.bdi.testcases.misc;

import jadex.base.test.TestReport;
import jadex.bdiv3x.runtime.Plan;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;

/**
 *  todo: ? dynamic model loading not supported
 *  Create and test a hello world agent.
 */
public class ModelLoadingPlan //extends Plan
{
//	//-------- attributes --------
//	
//	/** The test report. */
//	protected TestReport	tr	= new TestReport("#1", "Test external model loading.");
//	
//	//-------- methods --------
//	
//	/**
//	 *  Perform the test.
//	 */
//	public void body()
//	{
//		InputStream	input;
//		try
//		{
//			input = new FileInputStream("../jadex-applications-bdi/src/main/java/jadex/bdi/examples/helloworld/HelloWorld.agent.xml");
//		}
//		catch(FileNotFoundException fnfe)
//		{
//			throw new RuntimeException(fnfe);
//		}
//
//		IDynamicBDIFactory	fac	= (IDynamicBDIFactory)getInterpreter().getComponentFeature(IRequiredServicesFeature.class).getRequiredService("factory").get();
//		fac.loadAgentModel("helloworld", input, "helloagent.agent.xml", getInterpreter().getModel().getResourceIdentifier()).get();
//
//		IComponentManagementService cms	= (IComponentManagementService)getInterpreter().getComponentFeature(IRequiredServicesFeature.class).getRequiredService("cms").get();
//		Future<Collection<Tuple2<String, Object>>>	finished	= new Future<Collection<Tuple2<String, Object>>>();
//		cms.createComponent("hw1", "helloagent.agent.xml", new CreationInfo(getComponentIdentifier()), new DelegationResultListener<Collection<Tuple2<String, Object>>>(finished)).get();
//
//		finished.get();
//		
//		tr.setSucceeded(true);
//		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
//	}
//	
//	/**
//	 *  Test failed.
//	 */
//	public void failed()
//	{
//		getException().printStackTrace();
//		tr.setFailed(""+getException());
//		getBeliefbase().getBeliefSet("testcap.reports").addFact(tr);
//	}
}
