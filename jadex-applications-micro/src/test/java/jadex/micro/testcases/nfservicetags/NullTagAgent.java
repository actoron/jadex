package jadex.micro.testcases.nfservicetags;

import org.junit.Ignore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.nonfunctional.SNFPropertyProvider;
import jadex.bridge.nonfunctional.annotation.NFProperties;
import jadex.bridge.nonfunctional.annotation.NFProperty;
import jadex.bridge.sensor.service.TagProperty;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Argument;
import jadex.micro.annotation.Arguments;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Test an agent with a tag that resolves to null.
 */
@Agent
@Arguments(@Argument(name = TagProperty.NAME, clazz = String.class, defaultvalue="null"))
@NFProperties(@NFProperty(value = TagProperty.class))
@Results(@Result(name="testresults", description= "The test results.", clazz=Testcase.class))
@Ignore
public class NullTagAgent extends JunitAgentTest
{
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  The agent body. 
	 */
	@AgentBody
	public void body()
	{
		final List<TestReport> results = new ArrayList<TestReport>();
		
		TestReport tr1 = new TestReport("#1", "Test if tag null.");
		try
		{
			Object tagval = SNFPropertyProvider.getNFPropertyValue(agent.getExternalAccess(), TagProperty.NAME).get();
			if(tagval instanceof Collection && ((Collection)tagval).size()==1 && ((Collection)tagval).iterator().next()==null)
			{
				tr1.setSucceeded(true);
			}
			else
			{
				tr1.setFailed("Tag value was not null: "+tagval);
			}
		}
		catch(Exception e)
		{
			tr1.setReason("Exception occurred: "+e);
		}
		results.add(tr1);
		
		agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(results.size(), 
			(TestReport[])results.toArray(new TestReport[results.size()])));
		agent.killComponent();
	}
}
