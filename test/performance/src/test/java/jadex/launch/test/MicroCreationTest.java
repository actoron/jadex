package jadex.launch.test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;

import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.Tuple;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;

/**
 *  Test if the platform terminates itself.
 */
public class MicroCreationTest //extends TestCase
{
	/**
	 *  Test method.
	 */
	@Test
	public void	testMicroCreation() throws Exception
	{
		long timeout	= Starter.getDefaultTimeout(null);
//		ISuspendable	sus	= 	new ThreadSuspendable();
//		IExternalAccess	platform	= (IExternalAccess)Starter.createPlatform(new String[]{"-platformname", "benchmarks_*",
////			"-kernels", "\"micro\"",
////			"-logging_level", "java.util.logging.Level.INFO",
//			//"-libpath", "new String[]{\""+new File("../jadex-applications-micro/target/classes").toURI().toURL().toString()+"\"}",
//			"-libpath", SUtil.getOutputDirsExpression("jadex-integration-performance-test", true),
//			"-awareness", "false",	// otherwise influences performance measure
//			"-gui", "false",
//			"-saveonexit", "false",
//			"-welcome", "false",
//			"-extensions", "null",
//			"-chat", "false",
//			"-cli", "false",
////			"-autoshutdown", "true",
////			"-componentfactory", "jadex.component.ComponentComponentFactory",
////			"-conf", "jadex.standalone.Platform.component.xml",
////			"-deftimeout", "-1",
//			"-printsecret", "false"}).get(timeout);
		IExternalAccess	platform	= Starter.createPlatform(STest.getLocalTestConfig(getClass())).get(timeout);
		timeout	= Starter.getDefaultTimeout(platform.getId());
		
		Future<Map<String, Object>> fut = new Future<Map<String, Object>>();
		final Future<Map<String, Object>> ffut = fut;
		Map<String, Object>	args = new HashMap<String, Object>();
		args.put("max", Integer.valueOf(3000));
//		cms.createComponent(null, "jadex/micro/benchmarks/ParallelAgentCreationAgent.class", new CreationInfo(args), new DelegationResultListener<Collection<Tuple2<String, Object>>>(fut))
//		cms.createComponent(null, "jadex/micro/benchmarks/PojoAgentCreationAgent.class", new CreationInfo(args), new DelegationResultListener<Collection<Tuple2<String, Object>>>(fut))
		platform.createComponent(new CreationInfo(args).setFilename("jadex/micro/benchmarks/BlockingAgentCreationAgent.class"))
//		cms.createComponent(null, "jadex/micro/benchmarks/AgentCreationAgent.class", new CreationInfo(args), new DelegationResultListener<Collection<Tuple2<String, Object>>>(fut))
			.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Map<String, Object>>(fut)
		{
			public void customResultAvailable(IExternalAccess result)
			{
				// Agent created. Kill listener waits for result.
				result.waitForTermination().addResultListener(new DelegationResultListener<Map<String, Object>>(ffut));
			}
		});
		
		// 2 times timeout should do on all build servers. if test fails, check if platform has become slower ;-)
		Map<String, Object> results = fut.get(20*timeout);

		// Write values to property files for hudson plot plugin.
		for(Iterator<Map.Entry<String, Object>> it=results.entrySet().iterator(); it.hasNext(); )
		{
			Map.Entry<String, Object> tup = it.next();
//			String	key	= it.next();
//			Tuple	value	= (Tuple)results.get(key);
			// Collect benchmark results (name/value tuple)
			if(tup.getValue() instanceof Tuple)
			{
				try
				{
//					FileWriter	fw	= new FileWriter(new File("../"+tup.getFirstEntity()+".properties"));
					File file = new File("../"+tup.getKey()+".properties");
					FileOutputStream fileout = new FileOutputStream(file);
					Properties	props	=	new Properties();
					props.setProperty("YVALUE", ""+((Tuple)tup.getValue()).get(0));
					props.store(fileout, null);
					fileout.close();
				}
				catch(IOException e)
				{
					System.out.println("Warning: could not save value: "+e);
				}
			}
		}
		
		try
		{
			platform.killComponent().get(timeout);
		}
		catch(Exception e)
		{
			// Platform autoshutdown already finished.
			if(!(e instanceof ComponentTerminatedException))
			{
				e.printStackTrace();
			}
		}
		
//		sus	= null;
		platform	= null;
		fut	= null;
		
//		try
//		{
//			Thread.sleep(3000000);
//		}
//		catch(InterruptedException e)
//		{
//		}
	}
}
