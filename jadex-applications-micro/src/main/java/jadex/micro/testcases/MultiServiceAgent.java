package jadex.micro.testcases;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.service.annotation.Service;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;
import jadex.micro.testcases.semiautomatic.compositeservice.IAddService;
import jadex.micro.testcases.semiautomatic.compositeservice.ISubService;

/**
 *  Test implementation of multiple services in a single agent.
 */
@ProvidedServices({
	@ProvidedService(type=IAddService.class, implementation=@Implementation(expression="$component")),
	@ProvidedService(type=ISubService.class, implementation=@Implementation(expression="$component"))
})
@RequiredServices({
	@RequiredService(name="add", type=IAddService.class),
	@RequiredService(name="sub", type=ISubService.class)
})
@Results(@Result(name="testresults", clazz=Testcase.class))
@Service(IAddService.class) // todo: multi interfaces?
public class MultiServiceAgent	extends MicroAgent	implements IAddService, ISubService
{
	//-------- testcase implementation --------
	
	/**
	 *  Search for the two services and check if they work as expected.
	 */
	public IFuture<Void> executeBody()
	{
		final Future<Void>	ret	= new Future<Void>();
		final Future fut = new Future();
		
		getRequiredService("add").addResultListener(new DelegationResultListener(fut)
		{
			public void customResultAvailable(Object result)
			{
				final IAddService	add	= (IAddService)result;
				getRequiredService("sub").addResultListener(new DelegationResultListener(fut)
				{
					public void customResultAvailable(Object result)
					{
						final ISubService	sub	= (ISubService)result;
						add.add(17, 4).addResultListener(new DelegationResultListener(fut)
						{
							public void customResultAvailable(Object result)
							{
								Double	val	= (Double)result;
								sub.sub(val.doubleValue(), 12).addResultListener(new DelegationResultListener(fut));
							}
						});
					}
				});
			}
		});
		
		// Check result of service execution and store test result.
		final TestReport	tr	= new TestReport("#1", "Test if multiple services can be used.");
		fut.addResultListener(createResultListener(new IResultListener()
		{
			public void resultAvailable(Object result)
			{
				tr.setSucceeded(true);
				setResultValue("testresults", new Testcase(1, new TestReport[]{tr}));
//				killAgent();
				ret.setResult(null);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				tr.setFailed(exception.toString());
				setResultValue("testresults", new Testcase(1, new TestReport[]{tr}));
//				killAgent();
				ret.setResult(null);
			}
		}));
		
		return ret;
	}

	
	//---------- service implementations --------
	
	/**
	 *  Add two numbers.
	 */
	public IFuture add(double a, double b)
	{
		return new Future(new Double(a+b));
	}

	/**
	 *  Subtract two numbers.
	 */
	public IFuture sub(double a, double b)
	{
		return new Future(new Double(a-b));
	}	
}
