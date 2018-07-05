package jadex.bdiv3.testcases.beliefs;


import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.IComponentStep;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentKilled;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;
import jadex.rules.eca.ChangeInfo;

/**
 *  Agent that has two beliefs. 
 *  num2 belief depends on num1 and a plan depends on changes of num2.
 */
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class InnerClassBeliefChangeBDI
{
	/** The test report. */
	protected TestReport	tr	= new TestReport("#1", "Test if dynamic belief in inner class works.");
	
	/** The agent (injected). */
	@Agent
	protected IInternalAccess agent;

	@Belief
	protected String ack;

	@AgentCreated
	public void init()
	{
		Future<String> fut = new Future<String>();
		
	    fut.addResultListener(new IResultListener<String>()
	    {
	        public void resultAvailable(String message)
	        {
	            ack = message;
//	        	setAck(message);
	        }
	        
	        public void exceptionOccurred(Exception exception)
	        {
	        	exception.printStackTrace();
	        }
	    });
	    
	    fut.setResult("hello");
	}
	
	@AgentBody
	public void body()
	{
		agent.getFeature(IExecutionFeature.class).waitForDelay(3000, new IComponentStep<Void>()
		{
			public IFuture<Void> execute(IInternalAccess ia)
			{
				agent.killComponent();
				return IFuture.DONE;
			}
		});
	}
	
	/**
	 *  Called when agent is killed.
	 */
	@AgentKilled
	public void	destroy(IInternalAccess agent)
	{
		agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(1, new TestReport[]{tr}));
	}
	
//	@Belief
//	public String getAck()
//	{
//		return ack;
//	}
//	
//	@Belief
//	public void setAck(String ack)
//	{
//		this.ack = ack;
//	}
	
	@Plan(trigger=@Trigger(factchangeds="ack"))
	private class AckPlan
	{
	    @PlanAPI
	    IPlan api;

	    @PlanBody
	    public void body(ChangeInfo<?> ci)
	    {
//	    	System.out.println("before");
//	    	ChangeInfo<?> ci = api.waitForFactChanged("ack").get();
	    	System.out.println("after "+ci.getValue());
	    	if("hello".equals(ci.getValue()))
	    	{
	    		tr.setSucceeded(true);
	    	}
	    	else
	    	{
	    		tr.setFailed("Wrong value: "+ci.getValue());
	    	}
	    	agent.killComponent();
	   	}
	}
}
	
