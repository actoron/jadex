package jadex.micro.testcases.prepostconditions;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.component.IProvidedServicesFeature;
import jadex.commons.SReflect;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IIntermediateFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.future.IntermediateFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Implementation;
import jadex.micro.annotation.ProvidedService;
import jadex.micro.annotation.ProvidedServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

/**
 *  Agent that tests if contracts, i.e. pre- and postconditions on services work.
 */
@Agent
@Service
@ProvidedServices(@ProvidedService(type=IContractService.class, implementation=@Implementation(expression="$pojoagent")))
@Results(@Result(name="testresults", description= "The test results.", clazz=Testcase.class))
public class ConditionAgent implements IContractService
{
	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  The body.
	 */
	@AgentBody
	public IFuture<Void> body()
	{
		final Future<Void> ret = new Future<Void>();
		
		final List<TestReport> results = new ArrayList<TestReport>();
		
//		IContractService ts = (IContractService)agent.getServiceContainer().getProvidedServices(IContractService.class)[0];
		IContractService ts = (IContractService)agent.getComponentFeature(IProvidedServicesFeature.class).getProvidedServices(IContractService.class)[0];
		
		CounterResultListener<Void> lis = new CounterResultListener<Void>(12, new DefaultResultListener<Void>()
		{
			public void resultAvailable(Void result)
			{
				agent.getComponentFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(results.size(), 
					(TestReport[])results.toArray(new TestReport[results.size()])));
				ret.setResult(null);
			}
		});
		
		// all ok
		TestReport tr = new TestReport("#1", "Normal call.");
		results.add(tr);
		ts.doSomething("hi", 6, 2).addResultListener(
			new DetectionListener<Integer>(tr, null, lis));

		// a!=null violated
		tr = new TestReport("#2", "Test if null argument is detected");
		results.add(tr);
		ts.doSomething(null, 6, 2).addResultListener(
			new DetectionListener<Integer>(tr, IllegalArgumentException.class, lis));
		
		// c>0 violated
		tr = new TestReport("#3", "Test if arg>0 is detected");
		results.add(tr);
		ts.doSomething("hi", 6, -1).addResultListener(
			new DetectionListener<Integer>(tr, IllegalStateException.class, lis));

		// result!=null violated
		tr = new TestReport("#4", "Test if null result is detected.");
		results.add(tr);
		ts.doSomething("null", 1, 1).addResultListener(
			new DetectionListener<Integer>(tr, IllegalArgumentException.class, lis));

		// result <100 violated
		tr = new TestReport("#5", "Test if result <100 is detected.");
		results.add(tr);
		ts.doSomething("hi", 1000, 1).addResultListener(
			new DetectionListener<Integer>(tr, IllegalStateException.class, lis));
		
		List<String> names = new ArrayList<String>();
		names.add("Alfons");
		names.add("Berta");
		names.add("Charlie");

		// all ok
		tr = new TestReport("#6", "Normal call.");
		results.add(tr);
		ts.getName(0, names).addResultListener(new DetectionListener<String>(tr, null, lis));
		
		// all ok
		tr = new TestReport("#7", "Normal call.");
		results.add(tr);
		ts.getName(1, names).addResultListener(new DetectionListener<String>(tr, null, lis));
		
		// all ok
		tr = new TestReport("#8", "Normal call.");
		results.add(tr);
		ts.getName(2, names).addResultListener(new DetectionListener<String>(tr, null, lis));

		// index<0
		tr = new TestReport("#9", "Test if index<0 is detected.");
		results.add(tr);
		ts.getName(-1, names).addResultListener(new DetectionListener<String>(tr, IndexOutOfBoundsException.class, lis));
		
		// index>size
		tr = new TestReport("#10", "Test if index>size is detected.");
		results.add(tr);
		ts.getName(3, names).addResultListener(new DetectionListener<String>(tr, IndexOutOfBoundsException.class, lis));
		
		// delivers one value out of range
		tr = new TestReport("#11", "Test if intermediate result checks work with keep.", false, "hangs");
		lis.resultAvailable(null);
//		results.add(tr);
//		ts.getIncreasingValue().addResultListener(new DetectionListener<Collection<Integer>>(tr, IllegalStateException.class, lis));
		
		// delivers one value out of range
		tr = new TestReport("#12", "Test if intermediate result checks work without keep.", false, "hangs");
		lis.resultAvailable(null);
//		results.add(tr);
//		ts.getIncreasingValue2().addResultListener(new DetectionListener<Collection<Integer>>(tr, IllegalStateException.class, lis));

		
		return ret;
	}
	
	/**
	 *  Test method for @CheckNotNull and @CheckState.
	 */
	public IFuture<Integer> doSomething(String a, int x, int y)
	{
//		System.out.println("invoked: "+a);
		return "null".equals(a)? new Future((Object)null): new Future<Integer>(Integer.valueOf(x/y));
	}
	
	/**
	 *  Test method for @CheckIndex.
	 */
	public IFuture<String> getName(int idx, List<String> names)
	{
		return new Future<String>(names.get(idx));
	}
	
	/**
	 *  Test method for @CheckState with intermediate results.
	 *  
	 *  Will automatically try to determine the number of intermediate results to keep.
	 */
	public IIntermediateFuture<Integer> getIncreasingValue()
	{
		final IntermediateFuture<Integer> ret = new IntermediateFuture<Integer>();
		
		ret.addIntermediateResult(Integer.valueOf(1));
		ret.addIntermediateResult(Integer.valueOf(2));
		ret.addIntermediateResult(Integer.valueOf(3));
		ret.addIntermediateResult(Integer.valueOf(4));
		ret.addIntermediateResult(Integer.valueOf(0));
		
		return ret;
	}
	
	/**
	 *  Test method for @CheckState with intermediate results.
	 */
	public IIntermediateFuture<Integer> getIncreasingValue2()
	{
		return getIncreasingValue();
	}

	/**
	 *  Helper class for interpreting results.
	 */
	public static class DetectionListener<E> implements IResultListener<E>
	{
		/** The test report. */
		protected TestReport tr;
		
		/** The expected exception type. */
		protected Class<? extends RuntimeException> expected;
		
		/** The delegation listener. */
		protected IResultListener<?> delegate;
		
		/**
		 *  Creata a new listener.
		 */
		public DetectionListener(TestReport tr, Class<? extends RuntimeException> expected, IResultListener<?> delegate)
		{
			this.tr = tr;
			this.expected = expected;
			this.delegate = delegate;
		}
		
		/**
		 *  Called when result is available.
		 */
		public void resultAvailable(Object result)
		{
			if(expected==null)
			{
				tr.setSucceeded(true);
			}
			else
			{
				tr.setFailed("Expected exception: "+expected+" but no exception was thrown.");
			}
			delegate.resultAvailable(null);
		}
		
		/**
		 *  Called when exception occurred.
		 */
		public void exceptionOccurred(Exception exception)
		{
			if(expected==null)
			{
				tr.setFailed("No exception expected, but exception was thrown: "+exception);
			}
			else if(SReflect.isSupertype(expected, exception.getClass()))
			{
				tr.setSucceeded(true);
			}
			else
			{
				tr.setFailed("Wrong exception type received: "+exception);
			}
			delegate.resultAvailable(null);
		}
	}
}
