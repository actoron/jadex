package jadex.platform.service.wrapper;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.CollectionResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentService;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.ComponentType;
import jadex.micro.annotation.ComponentTypes;
import jadex.micro.annotation.CreationInfo;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

import java.util.Collection;

/**
 *  Agent that tests the rule and timer monitoring of initial events in bpmn processes.
 */
@RequiredServices(
{
	@RequiredService(name="wrapperservice", type=IJavaWrapperService.class, 
		binding=@Binding(create=true, creationinfo=@CreationInfo(type="wrapagent")))
})
@ComponentTypes(@ComponentType(name="wrapagent", filename="jadex/platform/service/wrapper/JavaWrapperAgent.class"))
@Agent
@Results(@Result(name="testresults", clazz=Testcase.class))
public class JavaWrapperTestAgent
{
	//-------- attributes --------
	
	/** The wrapper service. */
	@AgentService
	protected IJavaWrapperService	wrapperservice;
	
	//-------- methods --------
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public IFuture<Void> body(final IInternalAccess agent)
	{
		final Future<Void> ret = new Future<Void>();
		
		CollectionResultListener<TestReport>	crl	= new CollectionResultListener<TestReport>(2, false,
			new ExceptionDelegationResultListener<Collection<TestReport>, Void>(ret)
		{
			public void customResultAvailable(Collection<TestReport> results)
			{
				agent.setResultValue("testresults", new Testcase(results.size(), results.toArray(new TestReport[results.size()])));
				ret.setResult(null);
			}
		});
		
		testMainClassSuccess().addResultListener(crl);
		testMainClassFailure().addResultListener(crl);
		
		return ret;
	}
	
	/**
	 *  Test successful main class invocation.
	 */
	protected IFuture<TestReport>	testMainClassSuccess()
	{	
		final Future<TestReport> ret = new Future<TestReport>();
		final TestReport	rep	= new TestReport("#1", "Test successful execution of main class");
		
		wrapperservice.executeJava(TestMain.class, null)
			.addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				rep.setSucceeded(true);
				ret.setResult(rep);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				rep.setFailed("Failed due to "+exception);
				ret.setResult(rep);
			}
		});
		
		return ret;
	}
	
	/**
	 *  Test main class invocation with exception.
	 */
	protected IFuture<TestReport>	testMainClassFailure()
	{	
		final Future<TestReport> ret = new Future<TestReport>();
		final TestReport	rep	= new TestReport("#2", "Test failed execution of main class");
		
		wrapperservice.executeJava(TestMain.class, new String[]{"fail"})
			.addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				rep.setFailed("Failed due to missing exception");
				ret.setResult(rep);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				if(exception instanceof TestMain.TestException)
				{
					rep.setSucceeded(true);
					ret.setResult(rep);
				}
				else
				{
					rep.setFailed("Failed due to wrong exception: "+exception);
					ret.setResult(rep);					
				}
			}
		});
		
		return ret;
	}
}
