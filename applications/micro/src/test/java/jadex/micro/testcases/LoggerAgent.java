package jadex.micro.testcases;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.base.test.impl.JunitAgentTest;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.nonfunctional.annotation.NameValue;
import jadex.bridge.service.ServiceScope;
import jadex.bridge.service.component.IRequiredServicesFeature;
import jadex.bridge.service.types.clock.IClock;
import jadex.bridge.service.types.clock.IClockService;
import jadex.commons.SUtil;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.Imports;
import jadex.micro.annotation.Properties;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;
import jadex.micro.testcases.LoggerAgent.TestLogHandler;

/**
 *  A minimal test case agent serving as a demonstrator.
 */
@Imports({"java.util.logging.*"})
@Description("Tests the logger.")
@Results(@Result(name="testresults", description= "The test results.", clazz=Testcase.class))
@RequiredServices({@RequiredService(name="clockservice", type=IClockService.class, scope=ServiceScope.PLATFORM)})
@Properties({
	@NameValue(name="logging.level", value="Level.FINEST"),
//	@NameValue(name="logging.useParentHandlers", value="true"),
//	@NameValue(name="logging.addConsoleHandler", value="true"),
//	@NameValue(name="logging.file", value="log.txt"),
	@NameValue(name="logging.handlers", clazz=TestLogHandler.class)
//	@NameValue(name="logging.handlers", value="new LoggerAgent$TestLogHandler()")
})
@Agent
public class LoggerAgent extends JunitAgentTest
{
	@Agent
	protected IInternalAccess agent;
	
	/**
	 *  Just finish the test by setting the result and killing the agent.
	 */
	@AgentBody
	public IFuture<Void> executeBody()
	{
		final Future<Void> ret = new Future<Void>();
		
		agent.getFeature(IRequiredServicesFeature.class).getService("clockservice").addResultListener(new ExceptionDelegationResultListener<Object, Void>(ret)
		{
			public void customResultAvailable(Object result)
			{
				final IClockService clock = (IClockService)result;
				final long start = clock.getTime();
				String ct = clock.getClockType();
				final boolean simclock = IClock.TYPE_EVENT_DRIVEN.equals(ct) || IClock.TYPE_TIME_DRIVEN.equals(ct);
				
				List<TestReport> reports = new ArrayList<TestReport>();
				
				final TestReport tr = new TestReport("#1", "Test logging.");
				agent.getLogger().setLevel(Level.FINEST);
				agent.getLogger().addHandler(new Handler()
				{
					public void publish(LogRecord record)
					{
//						System.out.println("log: "+record.getMillis());
						long end = clock.getTime();
						long diff = end-start;
						
						if(simclock && diff==0 || !simclock && diff<1000)
						{
							tr.setSucceeded(true);
						}
						else
						{
							tr.setReason("Time in log record differs substantially: "+end+" "+start+" "+diff);
						}
					}
					
					public void flush()
					{
					}
					
					public void close() throws SecurityException
					{
					}
				});
				reports.add(tr);
				
				agent.getLogger().info("test log message");
				
				TestReport tr2 = new TestReport("#2", "Test logging handler.");
				
				Handler[] handlers = agent.getLogger().getHandlers();
				for(int i=0; i<handlers.length; i++)
				{
					if(handlers[i] instanceof TestLogHandler)
					{
						tr2.setSucceeded(true);
						break;
					}
				}
				if(!tr2.isSucceeded())
					tr2.setReason("TestLogHandler was not found: "+SUtil.arrayToString(handlers));
				reports.add(tr2);
				
				agent.getFeature(IArgumentsResultsFeature.class).getResults().put("testresults", new Testcase(reports.size(), (TestReport[])reports.toArray(new TestReport[reports.size()])));
				ret.setResult(null);
			}
		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	public static class TestLogHandler extends Handler
	{
		public void publish(LogRecord record)
		{
//			System.out.println("Received log record: "+record);
		}
		
		public void close() throws SecurityException
		{
		}
		
		public void flush()
		{
		}
	}
}


