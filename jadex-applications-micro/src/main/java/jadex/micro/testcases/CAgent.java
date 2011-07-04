package jadex.micro.testcases;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

import java.util.ArrayList;
import java.util.List;

/**
 *  Simple test agent with one service.
 */
@ProvidedServices(@ProvidedService(type=ICService.class, implementation=@Implementation(expression="$component")))
@RequiredServices(@RequiredService(name="cservice", type=ICService.class, binding=@Binding(scope=RequiredServiceInfo.SCOPE_LOCAL)))
@Results(@Result(name="testresults", clazz=Testcase.class))
public class CAgent extends MicroAgent implements ICService
{
	/**
	 *  Test if copy parameters work.
	 */
	public void executeBody()
	{
		final List testcases = new ArrayList();
		
		getRequiredService("cservice").addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				final ICService cservice = (ICService)result;
				final Object arg = new String("arg");
				cservice.testNoCopy(arg, arg.hashCode()).addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object result)
					{
						TestReport tr = new TestReport("#1", "Test if argument is not copied.");
						if(((Boolean)result).booleanValue())
						{
							tr.setSucceeded(true);
						}
						else
						{
							tr.setReason("Hashcode is not equal.");
						}
						testcases.add(tr);
						
						cservice.testCopy(arg, arg.hashCode()).addResultListener(new DefaultResultListener()
						{
							public void resultAvailable(Object result)
							{
								TestReport tr = new TestReport("#2", "Test if argument is copied.");
								if(((Boolean)result).booleanValue())
								{
									tr.setSucceeded(true);
								}
								else
								{
									tr.setReason("Hashcode is equal.");
								}
								testcases.add(tr);
						
								setResultValue("testcases", new Testcase(testcases.size(), (TestReport[])testcases.toArray(new TestReport[testcases.size()])));
							}
						});
					}
				});
			}
		});
	}
	
	/**
	 *  Test if no copy works.
	 */
	public IFuture testNoCopy(Object arg, int hash)
	{
//		System.out.println("called service");
		return new Future(arg.hashCode()==hash? Boolean.TRUE: Boolean.FALSE);
	}
	
	/**
	 *  Test if no copy works.
	 */
	public IFuture testCopy(Object arg, int hash)
	{
		return new Future(arg.hashCode()!=hash? Boolean.TRUE: Boolean.FALSE);
	}
}
