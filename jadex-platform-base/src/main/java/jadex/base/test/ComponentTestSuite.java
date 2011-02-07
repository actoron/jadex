package jadex.base.test;

import jadex.base.SComponentFactory;
import jadex.base.Starter;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IModelInfo;
import jadex.commons.future.ISuspendable;
import jadex.commons.future.ThreadSuspendable;
import jadex.commons.service.RequiredServiceInfo;
import jadex.commons.service.SServiceProvider;
import jadex.commons.service.library.ILibraryService;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
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
		this(path, root, excludes, 300000);
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
		this(new String[]{"-configname", "testcases", "-simulation", "true"}, path, root, excludes, timeout);
	}
	
	/**
	 * Create a component test suite for components contained in a given path.
	 * @param args	The platform arguments.
	 * @param path	The path to look for test cases in.
	 * @param root	The classpath root corresponding to the path.
	 * @param excludes	Files to exclude (if a pattern is contained in file path). 
	 * @param timeout	The test suite timeout (if tests are not completed, execution will be aborted). 
	 */
	public ComponentTestSuite(String[] args, File path, File root, String[] excludes, final long timeout) throws Exception
	{
		super(path.getName());
		
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
		
		IExternalAccess	rootcomp	= (IExternalAccess)Starter.createPlatform(args).get(ts);
		IComponentManagementService cms = (IComponentManagementService)SServiceProvider.getServiceUpwards(rootcomp.getServiceProvider(), IComponentManagementService.class).get(ts);
		ILibraryService libsrv	= (ILibraryService)SServiceProvider.getService(rootcomp.getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(ts);
		
		try
		{
			URL url = root.toURI().toURL();
			libsrv.addURL(url);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
		// Scan for test cases.
		List	todo	= new LinkedList();
		todo.add(path);
		while(!todo.isEmpty())
		{
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
					if(((Boolean)SComponentFactory.isLoadable(rootcomp, abspath).get(ts)).booleanValue())
					{
						IModelInfo model = (IModelInfo)SComponentFactory.loadModel(rootcomp, abspath).get(ts);
						boolean istest = false;
						if(model!=null && model.getReport()==null)
						{
							IArgument[]	results	= model.getResults();
							for(int i=0; !istest && i<results.length; i++)
							{
								if(results[i].getName().equals("testresults") && results[i].getTypename().equals("Testcase"))
									istest	= true;
							}
						}
						if(istest)
						{
							addTest(new ComponentTest(cms, abspath));
						}
					}
				}
			}
		}
	}
}
