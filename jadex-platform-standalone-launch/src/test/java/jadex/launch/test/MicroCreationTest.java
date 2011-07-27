package jadex.launch.test;

import jadex.base.Starter;
import jadex.bridge.CreationInfo;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.SServiceProvider;
import jadex.bridge.service.library.ILibraryService;
import jadex.commons.Tuple;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.ISuspendable;
import jadex.commons.future.ThreadSuspendable;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import junit.framework.TestCase;

/**
 *  Test if the platform terminates itself.
 */
public class MicroCreationTest extends TestCase
{
	public void	testMicroCreation()
	{
		long timeout	= 300000;
		ISuspendable	sus	= 	new ThreadSuspendable();
		IExternalAccess	platform	= (IExternalAccess)Starter.createPlatform(new String[]{"-platformname", "testcases",
			"-configname", "allkernels", "-gui", "false", "-saveonexit", "false", "-welcome", "false", "-autoshutdown", "true"}).get(sus, timeout);
		IComponentManagementService cms = (IComponentManagementService)SServiceProvider.getServiceUpwards(platform.getServiceProvider(), IComponentManagementService.class).get(sus, timeout);
		ILibraryService libsrv	= (ILibraryService)SServiceProvider.getService(platform.getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(sus, timeout);
		
		try
		{
			File	root	= new File("../jadex-applications-micro/target/classes");
			URL url = root.toURI().toURL();
			libsrv.addURL(url);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
		final Future	fut	= new Future();
		Map	args	= new HashMap();
		args.put("max", new Integer(10000));
		cms.createComponent(null, "jadex/micro/benchmarks/AgentCreationAgent.class", new CreationInfo(args), new DelegationResultListener(fut))
			.addResultListener(new DelegationResultListener(fut)
		{
			public void customResultAvailable(Object result)
			{
				// Agent created. Kill listener waits for result.
			}
		});
		
		// Write values to property files for hudson plot plugin.
		Map	results	= (Map) fut.get(sus, timeout);
		for(Iterator it=results.keySet().iterator(); it.hasNext(); )
		{
			String	key	= (String) it.next();
			Tuple	value	= (Tuple) results.get(key);
			try
			{
				FileWriter	fw	= new FileWriter(new File("../"+key+".properties"));
				Properties	props	=	new Properties();
				props.setProperty("YVALUE", ""+value.get(0));
				props.store(fw, null);
				fw.close();
			}
			catch(IOException e)
			{
				System.out.println("Warning: could not save value: "+e);
			}
		}
	}
}
