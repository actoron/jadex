package jadex.base.test;

import jadex.base.Platform;
import jadex.base.SComponentFactory;
import jadex.bridge.IArgument;
import jadex.bridge.IComponentManagementService;
import jadex.bridge.ILoadableComponentModel;
import jadex.commons.SReflect;
import jadex.commons.concurrent.DefaultResultListener;
import jadex.service.IServiceContainer;
import jadex.service.library.ILibraryService;

import java.io.File;
import java.lang.reflect.Constructor;
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
	//-------- attributes --------
	
	/** The platform. */
	protected IServiceContainer	platform;
	
	//-------- constructors --------

	/**
	 * Create a component test suite for the given components.
	 */
	public ComponentTestSuite(String name, final String[] components) throws Exception
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
		
		platform.getService(IComponentManagementService.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				IComponentManagementService cms = (IComponentManagementService)result;
				for(int i=0; i<components.length; i++)
				{
					addTest(new ComponentTest(cms, components[i]));
				}
			}
		});
	}
	
	/**
	 * Create a component test suite for components contained in a given path.
	 * @param path	The path to look for test cases in.
	 * @param root	The classpath root corresponding to the path.
	 */
	public ComponentTestSuite(File path, File root) throws Exception
	{
		this(path, root, null);
	}
	
	/**
	 * Create a component test suite for components contained in a given path.
	 * @param path	The path to look for test cases in.
	 * @param root	The classpath root corresponding to the path.
	 */
	public ComponentTestSuite(final File path, final File root, final String[] excludes) throws Exception
	{
		this(path.getName(), new String[0]);
		
		platform.getService(IComponentManagementService.class).addResultListener(new DefaultResultListener()
		{
			public void resultAvailable(Object source, Object result)
			{
				final IComponentManagementService cms = (IComponentManagementService)result;
				platform.getService(ILibraryService.class).addResultListener(new DefaultResultListener()
				{
					public void resultAvailable(Object source, Object result)
					{
						final ILibraryService libsrv	= (ILibraryService)result;
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
									final String fabspath = abspath;
									SComponentFactory.isLoadable(platform,  abspath, libsrv.getClassLoader()).addResultListener(new DefaultResultListener()
									{
										public void resultAvailable(Object source, Object result)
										{
											if(((Boolean)result).booleanValue())
											{
												SComponentFactory.loadModel(platform, fabspath, libsrv.getClassLoader()).addResultListener(new DefaultResultListener()
												{
													public void resultAvailable(Object source, Object result)
													{
														boolean istest = false;
														ILoadableComponentModel model = (ILoadableComponentModel)result;
														if(model!=null && model.getReport().isEmpty())
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
												});
											}
										}
									});
								}
							}
						}
					}
				});
			}
		});
	}
}
