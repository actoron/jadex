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
 *  Test if the bdi v3 creation test works.
 */
//@Ignore
public class BDIV3CreationTest //extends TestCase
{
	/**
	 *  Test bdi v3 creation.
	 */
	@Test
	public void	testBDICreation()
	{
//		ISuspendable	sus	= 	new ThreadSuspendable();
		IExternalAccess	platform	= (IExternalAccess)Starter.createPlatform(STest.getLocalTestConfig(getClass()),
			new String[]{
//				"-platformname", "benchmarks_*",
//			"-kernels", "\"micro\"",
//			"-logging", "true",
//			"-libpath", SUtil.getOutputDirsExpression("jadex-integration-performance-test", true),
//			"-awareness", "false",	// otherwise influences performance measure
//			"-gui", "false", "-saveonexit", "false", "-welcome", "false", //"-autoshutdown", "true",
////			"-componentfactory", "jadex.component.ComponentComponentFactory",
////			"-conf", "jadex.standalone.Platform.component.xml",
//			"-printsecret", "false"
			}
			).get();
		
		final Future<Map<String, Object>> fut = new Future<Map<String, Object>>();
		Map<String, Object>	args	= new HashMap<String, Object>();
		args.put("max", Integer.valueOf(2000));
		platform.createComponent(new CreationInfo(args).setFilename("jadex.bdiv3.benchmarks.CreationBDI.class"))
			.addResultListener(new ExceptionDelegationResultListener<IExternalAccess, Map<String, Object>>(fut)
		{
			public void customResultAvailable(IExternalAccess result)
			{
				// Agent created. Kill listener waits for result.
				result.waitForTermination().addResultListener(new DelegationResultListener<>(fut));
			}
		});
		
		// timeout should do on all build servers. if test fails, check if platform has become slower ;-)
//		Collection<Tuple2<String, Object>>	results	= 
			fut.get();
		
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
			platform.killComponent().get();
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
//		fut	= null;
		
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
		BDIV3CreationTest test = new BDIV3CreationTest();
		test.testBDICreation();
	}
}
