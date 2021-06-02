package jadex.launch.test;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import jadex.base.Starter;
import jadex.base.test.util.STest;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;

/**
 *  Test if the bdi creation test works.
 */
public class BDICreationTest //extends TestCase
{
	/**
	 *  Test bdi v3 creation.
	 */
	@Test
	public void	testBDICreation()
	{
		long timeout	= -1;//BasicService.getDefaultTimeout();
//		String projectroot = new String("jadex-integration-performance-test");
//		System.out.println(resdir);
		IExternalAccess	platform	= (IExternalAccess)Starter.createPlatform(STest.createDefaultTestConfig(getClass()),
			new String[]{
//				"-platformname", "benchmarks_*", 
////			"-kernels", "\"micro\"",
//			"-logging", "true",
//			"-libpath", SUtil.getOutputDirsExpression(projectroot, true),
//			"-awareness", "false",	// otherwise influences performance measure
//			"-gui", "false", "-saveonexit", "false", "-welcome", "false", //"-autoshutdown", "true",
////			"-componentfactory", "jadex.component.ComponentComponentFactory",
////			"-conf", "jadex.standalone.Platform.component.xml",
//			"-printsecret", "false"
			}
			).get(timeout);
		
		Future<Map<String, Object>> fut = new Future<Map<String, Object>>();
		final Future<Map<String, Object>> ffut = fut;
		Map<String, Object>	args	= new HashMap<String, Object>();
		args.put("max", Integer.valueOf(1000));
		platform.createComponent(new CreationInfo(args).setFilename("jadex.bdi.benchmarks.AgentCreation.agent.xml"))
			.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Map<String, Object>>(fut)
		{
			public void customResultAvailable(IExternalAccess result)
			{
				// Agent created. Kill listener waits for result.
				result.waitForTermination().addResultListener(new DelegationResultListener<>(ffut));
			}
		});
		
		// 2 times timeout should do on all build servers. if test fails, check if platform has become slower ;-)
		fut.get(timeout*2);
		
//		// Write values to property files for hudson plot plugin.
//		Collection<Tuple2<String, Object>>	results	= fut.get(sus, timeout);
//		for(Iterator<Tuple2<String, Object>> it=results.iterator(); it.hasNext(); )
//		{
//			Tuple2<String, Object> tup = it.next();
////			String	key	= it.next();
////			Tuple	value	= (Tuple)results.get(key);
//			// Collect benchmark results (name/value tuple)
//			if(tup.getSecondEntity() instanceof Tuple)
//			{
//				try
//				{
////					FileWriter	fw	= new FileWriter(new File("../"+tup.getFirstEntity()+".properties"));
//					File file = new File("../"+tup.getFirstEntity()+".properties");
//					FileOutputStream fileout = new FileOutputStream(file);
//					Properties	props	=	new Properties();
//					props.setProperty("YVALUE", ""+((Tuple)tup.getSecondEntity()).get(0));
//					props.store(fileout, null);
//					fileout.close();
//				}
//				catch(IOException e)
//				{
//					System.out.println("Warning: could not save value: "+e);
//				}
//			}
//		}
		
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
	
	/**
	 *  Main for testing.
	 */
	public static void main(String[] args) throws Exception
	{
		BDICreationTest test = new BDICreationTest();
		test.testBDICreation();
	}
}
