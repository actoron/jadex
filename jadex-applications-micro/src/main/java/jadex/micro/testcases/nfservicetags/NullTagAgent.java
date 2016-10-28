package jadex.micro.testcases.nfservicetags;

import java.util.ArrayList;
import java.util.List;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IInternalAccess;
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
public class NullTagAgent
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
			if(tagval==null)
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
		
		
		agent.killComponent();
	}
}
