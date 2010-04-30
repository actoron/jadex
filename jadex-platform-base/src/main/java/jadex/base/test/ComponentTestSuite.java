package jadex.base.test;

import jadex.base.Platform;
import jadex.base.SComponentFactory;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.ILoadableComponentModel;
import jadex.commons.SReflect;
import jadex.service.IServiceContainer;
import jadex.service.library.ILibraryService;

import java.io.File;
import java.lang.reflect.Constructor;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import junit.framework.TestSuite;


/**
 * Execute multiple component tests in a test suite.
 */
public class ComponentTestSuite extends TestSuite
{
	//-------- attributes --------
	
	/** The platform. */
	protected IServiceContainer	platform;
	
	//-------- constructors --------

	/**
	 * Create a component test suite for the given components.
	 */
	public ComponentTestSuite(String name, String[] components) throws Exception
	{
		super(name);
		
		// Hack!!! Make configurations configurable.
		String[]	confs	= new String[]
		{
			"jadex/standalone/services_testconf.xml",
//		   	Platform.FALLBACK_STANDARDCOMPONENTS_CONFIGURATION,
			Platform.FALLBACK_APPLICATION_CONFIGURATION,
			Platform.FALLBACK_BDI_CONFIGURATION,
		   	Platform.FALLBACK_BPMN_CONFIGURATION,
		   	Platform.FALLBACK_MICRO_CONFIGURATION,
		   	Platform.FALLBACK_BDIBPMN_CONFIGURATION			
		};

		// hack!!! use reflection to avoid compile dependency
		Class	pfclass	= SReflect.findClass("jadex.standalone.Platform", null, this.getClass().getClassLoader());
		Constructor	pfcon	= pfclass.getConstructor(new Class[]{confs.getClass(), ClassLoader.class});
		platform	= (IServiceContainer)pfcon.newInstance(new Object[]{confs, this.getClass().getClassLoader()});
		platform.start();
		
		IComponentManagementService cms = (IComponentManagementService)platform
			.getService(IComponentManagementService.class);
		
		for(int i=0; i<components.length; i++)
		{
			addTest(new ComponentTest(cms, components[i]));
		}
	}
	
	/**
	 * Create a component test suite for components contained in a given path.
	 * @param path	The path to look for test cases in.
	 * @param root	The classpath root corresponding to the path.
	 */
	public ComponentTestSuite(File path, File root) throws Exception
	{
		this(path.getName(), new String[0]);
		
		IComponentManagementService cms = (IComponentManagementService)platform
			.getService(IComponentManagementService.class);
		ILibraryService	libsrv	= (ILibraryService)platform.getService(ILibraryService.class);
		libsrv.addURL(root.toURI().toURL());
		
		// Scan for test cases.
		List	todo	= new LinkedList();
		todo.add(path);
		while(!todo.isEmpty())
		{
			File	file	= (File)todo.remove(0);
			boolean	istest	= false;

			if(file.isDirectory())
			{
				File[]	subs	= file.listFiles();
				todo.addAll(Arrays.asList(subs));
			}
			else if(SComponentFactory.isLoadable(platform,  file.getAbsolutePath()))
			{
				try
				{
					ILoadableComponentModel model = SComponentFactory.loadModel(platform,  file.getAbsolutePath());
					
					if(model!=null && model.getReport().isEmpty())
					{
						IArgument[]	results	= model.getResults();
						for(int i=0; !istest && i<results.length; i++)
						{
							if(results[i].getName().equals("testresults") && results[i].getTypename().equals("Testcase"))
								istest	= true;
						}
					}
				}
				catch(Exception e)
				{
				}
			}
			
			if(istest)
			{
				addTest(new ComponentTest(cms, file.getAbsolutePath()));
			}
		}
	}
}
