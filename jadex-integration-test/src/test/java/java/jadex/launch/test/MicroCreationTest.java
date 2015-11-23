package jadex.launch.test;

import jadex.base.Starter;
import jadex.bridge.ComponentTerminatedException;
import jadex.bridge.IComponentIdentifier;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.CreationInfo;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;
import jadex.commons.Tuple;
import jadex.commons.Tuple2;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.ISuspendable;
import jadex.commons.future.ThreadSuspendable;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import org.junit.Test;

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
		long timeout	= Starter.getLocalDefaultTimeout(null);
//		ISuspendable	sus	= 	new ThreadSuspendable();
		IExternalAccess	platform	= (IExternalAccess)Starter.createPlatform(new String[]{"-platformname", "benchmarks_*",
//			"-kernels", "\"micro\"",
//			"-logging_level", "java.util.logging.Level.INFO",
			//"-libpath", "new String[]{\""+new File("../jadex-applications-micro/target/classes").toURI().toURL().toString()+"\"}",
			"-libpath", "new String[]{\""+SUtil.findBuildDir(new File("../jadex-applications-micro")).toURI().toURL().toString()+"\"}", // only works maven 
			"-awareness", "false",	// otherwise influences performance measure
			"-gui", "false",
			"-saveonexit", "false",
			"-welcome", "false",
			"-extensions", "null",
			"-chat", "false",
			"-cli", "false",
//			"-autoshutdown", "true",
//			"-componentfactory", "jadex.component.ComponentComponentFactory",
//			"-conf", "jadex.standalone.Platform.component.xml",
//			"-deftimeout", "-1",
			"-printpass", "false"}).get(timeout);
		timeout	= Starter.getLocalDefaultTimeout(platform.getComponentIdentifier());
		IComponentManagementService cms = (IComponentManagementService)SServiceProvider.getService(platform, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(timeout);
		
		Future<Collection<Tuple2<String, Object>>>	fut	= new Future<Collection<Tuple2<String, Object>>>();
		Map<String, Object>	args	= new HashMap<String, Object>();
		args.put("max", Integer.valueOf(10000));
//		cms.createComponent(null, "jadex/micro/benchmarks/ParallelAgentCreationAgent.class", new CreationInfo(args), new DelegationResultListener<Collection<Tuple2<String, Object>>>(fut))
//		cms.createComponent(null, "jadex/micro/benchmarks/PojoAgentCreationAgent.class", new CreationInfo(args), new DelegationResultListener<Collection<Tuple2<String, Object>>>(fut))
		cms.createComponent(null, "jadex/micro/benchmarks/BlockingAgentCreationAgent.class", new CreationInfo(args), new DelegationResultListener<Collection<Tuple2<String, Object>>>(fut))
//		cms.createComponent(null, "jadex/micro/benchmarks/AgentCreationAgent.class", new CreationInfo(args), new DelegationResultListener<Collection<Tuple2<String, Object>>>(fut))
			.addResultListener(new ExceptionDelegationResultListener<IComponentIdentifier, Collection<Tuple2<String, Object>>>(fut)
		{
			public void customResultAvailable(IComponentIdentifier result)
			{
				// Agent created. Kill listener waits for result.
			}
		});
		
		// 2 times timeout should do on all build servers. if test fails, check if platform has become slower ;-)
		Collection<Tuple2<String, Object>>	results	= fut.get(2*timeout);

		// Write values to property files for hudson plot plugin.
		for(Iterator<Tuple2<String, Object>> it=results.iterator(); it.hasNext(); )
		{
			Tuple2<String, Object> tup = it.next();
//			String	key	= it.next();
//			Tuple	value	= (Tuple)results.get(key);
			// Collect benchmark results (name/value tuple)
			if(tup.getSecondEntity() instanceof Tuple)
			{
				try
				{
//					FileWriter	fw	= new FileWriter(new File("../"+tup.getFirstEntity()+".properties"));
					File file = new File("../"+tup.getFirstEntity()+".properties");
					FileOutputStream fileout = new FileOutputStream(file);
					Properties	props	=	new Properties();
					props.setProperty("YVALUE", ""+((Tuple)tup.getSecondEntity()).get(0));
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
		cms	= null;
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
