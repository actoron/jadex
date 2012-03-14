package jadex.base.test;

import jadex.base.Starter;
import jadex.bridge.IErrorReport;
import jadex.bridge.IExternalAccess;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.factory.SComponentFactory;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.SUtil;
import jadex.commons.future.ISuspendable;
import jadex.commons.future.ThreadSuspendable;

import java.io.File;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import junit.framework.TestSuite;


/**
 * Execute multiple component tests in a test suite.
 */
public class ComponentTestSuite extends TestSuite
{
	//-------- constructors --------

	/**
	 * Create a component test suite for components contained in a given path.
	 * @param path	The path to look for test cases in.
	 * @param root	The classpath root corresponding to the path.
	 * @param excludes	Files to exclude (if a pattern is contained in file path). 
	 */
	public ComponentTestSuite(File path, File root, String[] excludes) throws Exception
	{
		this(path, root, excludes, 600000);
	}
	
	/**
	 * Create a component test suite for components contained in a given path.
	 * @param path	The path to look for test cases in.
	 * @param root	The classpath root corresponding to the path.
	 * @param excludes	Files to exclude (if a pattern is contained in file path). 
	 * @param timeout	The test suite timeout (if tests are not completed, execution will be aborted). 
	 */
	public ComponentTestSuite(File path, File root, String[] excludes, long timeout) throws Exception
	{
		this(new String[]
		{
			"-platformname", SUtil.createUniqueId("testcases", 3),
			"-kernels", "\"all\"",	// Required for old hudson build, otherwise wrong bdi kernel is used as dependencies are not in correct order
			"-simulation", "true",
			"-libpath", "new String[]{\""+root.toURI().toURL().toString()+"\"}",
//			"-logging_level", "java.util.logging.Level.INFO",
			"-gui", "false",
			"-awareness", "false",
			"-saveonexit", "false",
			"-welcome", "false",
			"-autoshutdown", "false",
			"-printpass", "false"
		}, path, excludes, timeout);
	}
	
	/**
	 * Create a component test suite for components contained in a given path.
	 * @param args	The platform arguments.
	 * @param path	The path to look for test cases in.
	 * @param excludes	Files to exclude (if a pattern is contained in file path). 
	 * @param timeout	The test suite timeout (if tests are not completed, execution will be aborted). 
	 */
	public ComponentTestSuite(String[] args, File path, String[] excludes, final long timeout) throws Exception
	{
		super(path.toString());
		
		if(timeout>0)
		{
			new Timer(true).schedule(new TimerTask()
			{
				public void run()
				{
					System.out.println("Aborting test suite due to excessive run time (>"+timeout+" ms).");
					System.exit(1);
				}
			}, timeout);
		}
		
		// Tests must be available after constructor execution.
		// Todo: get rid of thread suspendable!?
		ISuspendable	ts	= new ThreadSuspendable();
		
//		System.out.println("start platform");
		IExternalAccess	rootcomp	= (IExternalAccess)Starter.createPlatform(args).get(ts);
//		System.out.println("end platform");
		IComponentManagementService cms = (IComponentManagementService)SServiceProvider.getServiceUpwards(rootcomp.getServiceProvider(), IComponentManagementService.class).get(ts);
		ILibraryService libsrv	= (ILibraryService)SServiceProvider.getService(rootcomp.getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(ts);
		
		// Does not work because if urls are added the models must use rids to identify the resources
//		try
//		{
//			URL url = root.toURI().toURL();
//			libsrv.addURL(url);
//		}
//		catch(Exception e)
//		{
//			throw new RuntimeException(e);
//		}
		
		// Scan for test cases.
		List<File>	todo	= new LinkedList<File>();
		todo.add(path);
//		System.out.println("Path: "+path);
		while(!todo.isEmpty())
		{
//			System.out.println("todo: "+todo);
			File	file	= (File)todo.remove(0);
			final String	abspath	= file.getAbsolutePath();
			boolean	exclude	= false;
			for(int i=0; !exclude && excludes!=null && i<excludes.length; i++)
			{
				exclude	= abspath.indexOf(excludes[i])!=-1;
			}
			
			if(!exclude)
			{
				if(file.isDirectory())
				{
					File[]	subs	= file.listFiles();
					todo.addAll(Arrays.asList(subs));
				}
				else
				{
					// Should support/use libservice.getClassLoader(abspath) 
					// rootcomp.getModel().getResourceIdentifier()
					if(((Boolean)SComponentFactory.isLoadable(rootcomp, abspath, null).get(ts)).booleanValue())
					{
						try
						{
							IModelInfo model = (IModelInfo)SComponentFactory.loadModel(rootcomp, abspath, null).get(ts);
							boolean istest = false;
							if(model!=null && model.getReport()==null)
							{
								IArgument[]	results	= model.getResults();
								for(int i=0; !istest && i<results.length; i++)
								{
									if(results[i].getName().equals("testresults") && Testcase.class.equals(
										results[i].getClazz().getType(libsrv.getClassLoader(model.getResourceIdentifier()).get(ts), model.getAllImports())))
									{
										istest	= true;
									}
								}
							}
							if(istest)
							{
								addTest(new ComponentTest(cms, model));
							}
							else if(model.getReport()!=null)
							{
								addTest(new BrokenComponentTest(abspath, model.getReport()));
							}
						}
						catch(final RuntimeException e)
						{
							addTest(new BrokenComponentTest(abspath, new IErrorReport()
							{
								public String getErrorText()
								{
									return "Error loading model: "+e;
								}
								
								public String getErrorHTML()
								{
									return getErrorText();
								}
								
								public Map<String, String> getDocuments()
								{
									return null;
								}
							}));							
						}
					}
				}
			}
		}
		// Hack!!! Isn't there some tearDown for the test suite?
		addTest(new Cleanup(rootcomp));
	}
}
