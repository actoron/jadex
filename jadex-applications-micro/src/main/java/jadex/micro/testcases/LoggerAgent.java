package jadex.micro.testcases;

import jadex.base.test.TestReport;
import jadex.base.test.Testcase;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.clock.IClock;
import jadex.bridge.service.clock.IClockService;
import jadex.commons.future.DefaultResultListener;
import jadex.micro.MicroAgent;
import jadex.micro.annotation.Binding;
import jadex.micro.annotation.Description;
import jadex.micro.annotation.RequiredService;
import jadex.micro.annotation.RequiredServices;
import jadex.micro.annotation.Result;
import jadex.micro.annotation.Results;

import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;

/**
 *  A minimal test case agent serving as a demonstrator.
 */
@Description("Tests the logger.")
@Results(@Result(name="testresults", description= "The test results.", clazz=Testcase.class))
@RequiredServices({@RequiredService(name="clockservice", type=IClockService.class, 
	binding=@Binding(scope=RequiredServiceInfo.SCOPE_GLOBAL))})
public class LoggerAgent extends MicroAgent
{
	/**
	 *  Just finish the test by setting the result and killing the agent.
	 */
	public void executeBody()
	{
		getRequiredService("clockservice").addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object result)
			{
				final IClockService clock = (IClockService)result;
				final long start = clock.getTime();
				String ct = clock.getClockType();
				final boolean simclock = IClock.TYPE_EVENT_DRIVEN.equals(ct) || IClock.TYPE_TIME_DRIVEN.equals(ct);
				
				final TestReport tr = new TestReport("#1", "Test logging.");
				getLogger().setLevel(Level.FINEST);
				getLogger().addHandler(new Handler()
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
				
				getLogger().info("test log message");
				
				setResultValue("testresults", new Testcase(1, new TestReport[]{tr}));
				killAgent();
			}
		});
	}
}
