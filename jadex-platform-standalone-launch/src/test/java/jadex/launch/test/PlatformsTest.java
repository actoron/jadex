package jadex.launch.test;

import jadex.base.Starter;
import jadex.bridge.IExternalAccess;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.ProvidedServiceImplementation;
import jadex.bridge.service.ProvidedServiceInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.ICMSComponentListener;
import jadex.bridge.service.types.cms.IComponentDescription;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.javaparser.SJavaParser;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

/**
 *  Test if the platforms can be started and terminate correctly.
 */
public class PlatformsTest //extends TestCase
{
	// The platforms to test as pairs of componentfactory and model.
	String[]	PLATFORMS	= new String[]
	{
		"jadex.component.ComponentComponentFactory", "jadex.platform.Platform.component.xml",
		"jadex.micro.MicroAgentFactory", "jadex.platform.PlatformAgent"
//		"jadex.bpmn.BpmnFactory", "jadex.platform.Platform.bpmn2"
	};
	// Base arguments used for every platform.
	String[]	BASEARGS	= new String[]
    {
//		"-logging", "true",
//		"-debugfutures", "true",
//		"-nostackcompaction", "true",
		"-platformname", "testcases_*",
		"-gui", "false",
		"-saveonexit", "false",
		"-welcome", "false",
		"-autoshutdown", "false",
		"-printpass", "false",
//		"-deftimeout", ""+TIMEOUT
	};
	
	/** Arguments to exclude from comparison. */
	protected static final Set<String>	EXCLUDEARGS	= new HashSet<String>(Arrays.asList(new String[]
	{
		"extensions"	// different because of component.xml vs. Agent.class
	}));

	/** Platforms to exclude from comparison. */
	protected static final Set<String>	EXCLUDEPLATFORMS	= new HashSet<String>(Arrays.asList(new String[]
	{
		"jadex.platform.Platform.bpmn2"	// BPMN platform is just proof of concept. 
	}));

	/**
	 *  Main for testing.
	 */
	public static void main(String[] args)
	{
		System.out.println("guiclass: "+ jadex.commons.SReflect.classForName0("jadex.base.gui.componentviewer.DefaultComponentServiceViewerPanel",
		   	jadex.platform.service.library.LibraryService.class.getClassLoader()));
		
		PlatformsTest test = new PlatformsTest();
		for(int i=0; i<10000; i++)
		{
			System.out.println("Run: "+i);
			test.testPlatforms();
		}
	}
	
	/**
	 *  Test the platforms.
	 */
	@Test
	public void	testPlatforms()
	{
		long timeout = Starter.getLocalDefaultTimeout(null);
		long[] starttimes = new long[PLATFORMS.length/2+1];
		long[] shutdowntimes = new long[PLATFORMS.length/2+1];
		IModelInfo	defmodel	= null;	// Model of default platform to compare others to.
		
		for(int i=0; i<=PLATFORMS.length/2; i++)
		{
			String[]	args	= BASEARGS;
			if(i>0)	// First run with standard platform
			{
				args	= (String[])SUtil.joinArrays(args, new String[]
				{
					"-componentfactory", PLATFORMS[(i-1)*2],
					"-conf", PLATFORMS[(i-1)*2+1]
				});
			}
			
			long start = System.currentTimeMillis();
			IExternalAccess	platform = (IExternalAccess)Starter.createPlatform(args).get(timeout);
			timeout = Starter.getLocalDefaultTimeout(platform.getComponentIdentifier());
			starttimes[i] = System.currentTimeMillis()-start;
//			System.out.println("Started platform: "+i);
			
			if(defmodel==null)
			{
				defmodel	= platform.getModel();
			}
			else if(!EXCLUDEPLATFORMS.contains(PLATFORMS[(i-1)*2+1]))
			{
				compareModels(defmodel, platform.getModel());
			}
			
			final Future<Void>	fut	= new Future<Void>();
			IComponentManagementService cms = SServiceProvider.getService(platform, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(timeout);
			cms.addComponentListener(platform.getComponentIdentifier(), new ICMSComponentListener()
			{
				public IFuture<Void> componentRemoved(IComponentDescription desc, Map<String, Object> results)
				{
					fut.setResult(null);
					return IFuture.DONE;
				}
				public IFuture<Void> componentAdded(IComponentDescription desc)
				{
					return IFuture.DONE;
				}
				public IFuture<Void> componentChanged(IComponentDescription desc)
				{
					return IFuture.DONE;
				}
			}).get(timeout);
			
//			// Test CTRL-C shutdown behavior.
//			Timer	timer	= new Timer();
//			timer.schedule(new TimerTask()
//			{
//				public void run()
//				{
//					System.exit(0);
//				}
//			}, 3000);
			
			start = System.currentTimeMillis();
			platform.killComponent().get(timeout);
			fut.get(timeout);
			shutdowntimes[i] = System.currentTimeMillis()-start;
//			System.out.println("Stopped platform: "+i);
		}
		
//		System.out.println("Startup times: "+SUtil.arrayToString(starttimes));
//		System.out.println("Sutdown times: "+SUtil.arrayToString(shutdowntimes));
		
//		try
//		{
//			Thread.sleep(3000000);
//		}
//		catch(InterruptedException e)
//		{
//		}
	}

	/**
	 *  Compare two platform models.
	 *  Causes test case to fail if models are not equal.
	 */
	protected void compareModels(IModelInfo defmodel, IModelInfo model)
	{
		// Compare arguments.
		IArgument[]	defargs	= defmodel.getArguments();
		for(IArgument defarg: defargs)
		{
			IArgument	arg	= model.getArgument(defarg.getName());
			Assert.assertNotNull("Argument not found: "+defarg.getName()+", "+model.getFilename(), arg);
			if(arg!=null)
			{
				Class<?>	defclazz	= defarg.getClazz().getType(getClass().getClassLoader(), defmodel.getAllImports());
				Assert.assertEquals("Argument types differ: "+defarg.getName()+", "+model.getFilename(), defclazz,
					arg.getClazz().getType(getClass().getClassLoader(), model.getAllImports()));
				
				if(!EXCLUDEARGS.contains(defarg.getName()))
				{
					compareValues(defmodel, model, defarg.getDefaultValue(), arg.getDefaultValue(), defarg.getName(), "Argument");
				}
			}
		}
		
		// Compare results.
		IArgument[]	defress	= defmodel.getResults();
		for(IArgument defres: defress)
		{
			IArgument	res	= model.getResult(defres.getName());
			Assert.assertNotNull("Result not found: "+defres.getName()+", "+model.getFilename(), res);
			if(res!=null)
			{
				Class<?>	defclazz	= defres.getClazz().getType(getClass().getClassLoader(), defmodel.getAllImports());
				Assert.assertEquals("Result types differ: "+defres.getName()+", "+model.getFilename(), defclazz,
					res.getClazz().getType(getClass().getClassLoader(), model.getAllImports()));
				
				if(!EXCLUDEARGS.contains(defres.getName()))
				{
					compareValues(defmodel, model, defres.getDefaultValue(), res.getDefaultValue(), defres.getName(), "Result");
				}
			}
		}
		
		// Compare properties.
		Map<String, Object>	defprops	= defmodel.getProperties();
		for(String defprop: defprops.keySet())
		{
			Assert.assertTrue("Property not found: "+defprop+", "+model.getFilename(), model.getProperties().containsKey(defprop));
			compareValues(defmodel, model, defmodel.getProperties().get(defprop), model.getProperties().get(defprop), defprop, "Property");
		}
		
		// Compare provided services.
		ProvidedServiceInfo[]	defpservs	= defmodel.getProvidedServices();
		for(ProvidedServiceInfo defpserv: defpservs)
		{
			ProvidedServiceInfo	pserv	= null;
			for(ProvidedServiceInfo psi: model.getProvidedServices())
			{
				if(defpserv.getName()!=null)
				{
					if(defpserv.getName().equals(psi.getName()))
					{
						pserv	= psi;
						break;						
					}
				}
				else if(psi.getType().getType(getClass().getClassLoader(), model.getAllImports()).equals(
					defpserv.getType().getType(getClass().getClassLoader(), defmodel.getAllImports())))
				{
					pserv	= psi;
					break;
				}
			}
			Assert.assertNotNull("Service not found: "+defpserv.getType().getTypeName()+", "+model.getFilename(), pserv);
			if(pserv!=null)
			{
				Assert.assertEquals("Service name differs: "+defpserv.getType().getTypeName()+", "+model.getFilename(), defpserv.getName(), pserv.getName());
				
				ProvidedServiceImplementation	defimpl	= defpserv.getImplementation();
				ProvidedServiceImplementation	impl	= pserv.getImplementation();
				compareValues(defmodel, model, defimpl, impl, defpserv.getType().getTypeName(), "Service implementation");
				Assert.assertEquals("Service proxy type differs: "+defpserv.getType().getTypeName()+", "+model.getFilename(), defimpl.getProxytype(), impl.getProxytype());
			}
			
//			defimpl.getBinding();
//			defimpl.getInterceptors();
		}

		
//		defmodel.getSubcomponentTypes();
//		defmodel.getExtensionTypes();
//		
//		defmodel.getRequiredServices();
//
//		defmodel.getAutoShutdown(null);
//		defmodel.getDaemon(null);
//		defmodel.getSuspend(null);
//		defmodel.getMaster(null);
//
//		ConfigurationInfo[]	defconfigs	= defmodel.getConfigurations();
	}
	
	/**
	 *  Compare two values that may be unparsed expressions, in which case the expressions are compared after parsing.
	 */
	protected void	compareValues(IModelInfo defmodel, IModelInfo model, Object defval, Object val, String name, String type)
	{
//		System.out.println(type+" '"+name+"' of: "+model.getFilename());
		
		if(defval!=null)
		{
			Assert.assertNotNull(type+" '"+name+"' has no default value: "+model.getFilename(), val);
			if(defval instanceof UnparsedExpression)
			{
				Assert.assertTrue(type+" '"+name+"' should be expression (inconsistent model loaders?): "+model.getFilename(), val instanceof UnparsedExpression);
				
				defval	= SJavaParser.parseExpression((UnparsedExpression)defval, defmodel.getAllImports(), getClass().getClassLoader());
				val	= SJavaParser.parseExpression((UnparsedExpression)val, model.getAllImports(), getClass().getClassLoader());
			}
			Assert.assertEquals(type+" '"+name+"' default value differs: "+model.getFilename(), defval, val);
		}
		else
		{
			Assert.assertNull(type+" '"+name+"' should have no default value: "+model.getFilename(), val);
		}
	}
	
	//-------- uncomment below to switch from test failure to print out --------
	
//	public static void assertNotNull(String message, Object val)
//	{
//		if(val==null)
//			System.out.println(message);
//	}
//	
//	public static void assertEquals(String message, Object val1, Object val2)
//	{
//		if(!SUtil.equals(val1, val2))
//			System.out.println(message+" Expected "+val1+" but was "+val2);
//	}
//	
//	public static void assertEquals(String message, String val1, String val2)
//	{
//		if(!SUtil.equals(val1, val2))
//			System.out.println(message+" Expected "+val1+" but was "+val2);
//	}
}
