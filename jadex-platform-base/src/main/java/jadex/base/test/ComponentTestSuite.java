package jadex.base.test;

import jadex.base.SComponentFactory;
import jadex.base.Starter;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IModelInfo;
import jadex.commons.ISuspendable;
import jadex.commons.ThreadSuspendable;
import jadex.service.SServiceProvider;
import jadex.service.library.ILibraryService;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

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
	public ComponentTestSuite(final File path, final File root, final String[] excludes) throws Exception
	{
		super(path.getName());
		
		// Tests must be available after constructor execution.
		// Todo: get rid of thread suspendable!?
		ISuspendable	ts	= new ThreadSuspendable();
		
		IExternalAccess	rootcomp	= (IExternalAccess)Starter.createPlatform(new String[]{"-configname", "all_kernels_no_daemons", "-simulation", "true"}).get(ts);
		IComponentManagementService cms = (IComponentManagementService)SServiceProvider.getServiceUpwards(rootcomp.getServiceProvider(), IComponentManagementService.class).get(ts);
		ILibraryService libsrv	= (ILibraryService)SServiceProvider.getService(rootcomp.getServiceProvider(), ILibraryService.class).get(ts);
		
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
					if(((Boolean)SComponentFactory.isLoadable(rootcomp.getServiceProvider(), abspath).get(ts)).booleanValue())
					{
						IModelInfo model = (IModelInfo)SComponentFactory.loadModel(rootcomp.getServiceProvider(), abspath).get(ts);
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
