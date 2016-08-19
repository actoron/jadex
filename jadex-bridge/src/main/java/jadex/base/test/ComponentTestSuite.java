package jadex.base.test;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

import jadex.base.Starter;
import jadex.base.test.impl.ComponentLoadTest;
import jadex.base.test.impl.ComponentStartTest;
import jadex.base.test.impl.ComponentTest;
import jadex.bridge.IErrorReport;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Timeout;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.factory.SComponentFactory;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.SNonAndroid;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import junit.framework.TestResult;
import junit.framework.TestSuite;


/**
 * Execute multiple component tests in a test suite.
 */
@RunWith(AllTests.class)
public class ComponentTestSuite extends TestSuite implements IAbortableTestSuite
{
	//-------- constants --------
	
	/**
	 *  The default test platform arguments.
	 */
	public static final String[]	DEFARGS	= new String[]
	{
		"-platformname", "testcases_*",
//		"-kernels", "\"all\"",	// Required for old hudson build, otherwise wrong bdi kernel is used as dependencies are not in correct order
		"-simulation", "true",
		"-asyncexecution", "true",
//		"-libpath", "new String[]{\""+root.toURI().toURL().toString()+"\"}",
//		"-logging", "true",
//		"-logging", path.toString().indexOf("bdiv3")!=-1 ? "true" : "false",
//		"-logging_level", "java.util.logging.Level.WARNING",
		"-debugfutures", "true",
//		"-nostackcompaction", "true",
		"-gui", "false",
		"-awareness", "false",
		"-saveonexit", "false",
		"-welcome", "false",
		"-autoshutdown", "false",
		"-opengl", "false",
		"-cli", "false",
//		"-persist", "true", // for testing persistence
//		"-niotcptransport", "false",
//		"-tcptransport", "true",
//		"-deftimeout", "-1",
		"-printpass", "false",
		// Hack!!! include ssl transport if available
		"-ssltcptransport", (SReflect.findClass0("jadex.platform.service.message.transport.ssltcpmtp.SSLTCPTransport", null, ComponentTestSuite.class.getClassLoader())!=null ? "true" : "false"),  
	};

	//-------- attributes --------
	
	/** Indicate when the suite is aborted due to excessive run time. */
	public boolean	aborted;
	
	/** The platform. */
	protected IExternalAccess	platform;
	
	/** The class loader. */
	protected ClassLoader	classloader;
	
	/** The timeout (if any). */
	protected long	timeout;
	
	/** The timeout timer (if any). */
	protected Timer	timer;
	
	//-------- constructors --------

	/**
	 * Create a component test suite for components contained in a given path.
	 * @param projectDir	The project directory.
	 * @param excludes	Files to exclude (if a pattern is contained in file path). 
	 */
	public ComponentTestSuite(String projectDir, String[] excludes) throws Exception
	{
		this(SUtil.findOutputDirs(projectDir), excludes);
	}
	
	/**
	 * Create a component test suite for components contained in a given path.
	 * @param root	The class path root corresponding to the path.
	 * @param excludes	Files to exclude (if a pattern is contained in file path). 
	 */
	public ComponentTestSuite(File[] root, String[] excludes) throws Exception
	{
		this(root, root, excludes);
	}
	
	/**
	 * Create a component test suite for components contained in a given path.
	 * @param path	The path to look for test cases in.
	 * @param root	The classpath root corresponding to the path.
	 * @param excludes	Files to exclude (if a pattern is contained in file path). 
	 */
	public ComponentTestSuite(File[] path, File[] root, String[] excludes) throws Exception
	{
		this(path, root, excludes, true, true, true);
//		this(path, root, excludes, false, false, true);
	}
	
	/**
	 * Create a component test suite for components contained in a given path.
	 * @param path	The path to look for test cases in.
	 * @param root	The classpath root corresponding to the path.
	 * @param excludes	Files to exclude (if a pattern is contained in file path). 
	 * @param test	Run test components.
	 * @param load	Include broken components (will cause test failure if any). Also shows loadable, but not startable components as succeeded tests.
	 * @param start	Try starting components, which are no test cases.
	 */
	public ComponentTestSuite(File[] path, File[] root, String[] excludes, boolean test, boolean load, boolean start) throws Exception
	{
		this(DEFARGS, path, root, excludes, test, load, start);
	}
	
	/**
	 * Create a component test suite for components contained in a given path.
	 * @param args	The platform arguments.
	 * @param path	The path to look for test cases in.
	 * @param excludes	Files to exclude (if a pattern is contained in file path). 
	 * @param runtests	Run test components.
	 * @param broken	Include broken components (will cause test failure if any).
	 * @param start	Try starting components, which are no test cases.
	 */
	public ComponentTestSuite(String[] args, File[] paths, File roots[], String[] excludes, final boolean runtests, final boolean load, final boolean start) throws Exception
	{
		super(paths[0].toString());
		this.timeout	= Starter.getLocalDefaultTimeout(null);	// Initial timeout for starting platform.
		startTimer();
		
		// Tests must be available after constructor execution.
		// Todo: get rid of thread suspendable!?
		
//		System.out.println("start platform");
		platform	= (IExternalAccess)Starter.createPlatform(args).get();
		this.timeout	= Starter.getLocalDefaultTimeout(platform.getComponentIdentifier());
//		System.out.println("end platform");
		IComponentManagementService cms = (IComponentManagementService)SServiceProvider.getService(platform, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
		ILibraryService libsrv	= (ILibraryService)SServiceProvider.getService(platform, ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
		
		// Only works with x-rid hack or maven dependency service, because rms cannot use default classloader for decoding application messages.
//		final IResourceIdentifier	rid	= null;
		long ctimeout = Starter.getLocalDefaultTimeout(platform.getComponentIdentifier());	// Start with normal timeout for platform startup/shutdown.
		IResourceIdentifier	rid	= null;
		for(int root=0; root<roots.length; root++)
		{
			try
			{
				URL url = roots[root].toURI().toURL();
				if(rid==null)
				{
					rid	= libsrv.addURL(null, url).get();
				}
				else
				{
					libsrv.addURL(rid, url).get();
				}
			}
			catch(Exception e)
			{
				throw new RuntimeException(e);
			}
		}
		this.classloader	= libsrv.getClassLoader(null).get();
		stopTimer();
	
		for(int root=0; root<roots.length; root++)
		{			
			// Scan for test cases.
			List<String> scanForTestCases = scanForTestCases(roots[root], paths[root]);
			this.timeout = Starter.getScaledLocalDefaultTimeout(platform.getComponentIdentifier(), 0.05*scanForTestCases.size());	// Timeout for loading models.
			startTimer();
			Logger.getLogger("ComponentTestSuite").info("Scanning for testcases: " + paths[root]+" (scan timeout: "+timeout+")");
			for (String abspath : scanForTestCases)
			{	
				boolean	exclude	= false;
							
				for(int i=0; !exclude && excludes!=null && i<excludes.length; i++)
				{
					exclude	= abspath.indexOf(excludes[i])!=-1;
				}
				
				if(!exclude)
				{
					try
					{
//						if(abspath.indexOf("BDI")!=-1)
//						{
//							System.out.println("Check: "+abspath);							
//						}
						if(((Boolean)SComponentFactory.isLoadable(platform, abspath, rid).get()).booleanValue())
						{
	//						System.out.println("Loadable: "+abspath);
	//						if(abspath.indexOf("INeg")!=-1)
	//							System.out.println("test");
							boolean	startable	= ((Boolean)SComponentFactory.isStartable(platform, abspath, rid).get()).booleanValue();
							
	//						System.out.println("Startable: "+abspath+", "+startable);
							IModelInfo model = (IModelInfo)SComponentFactory.loadModel(platform, abspath, rid).get();
							boolean istest = false;
							if(model!=null && model.getReport()==null && startable)
							{
								IArgument[]	results	= model.getResults();
								for(int i=0; !istest && i<results.length; i++)
								{
									if(results[i].getName().equals("testresults") && Testcase.class.equals(
										results[i].getClazz().getType(libsrv.getClassLoader(rid).get(), model.getAllImports())))
									{
										istest	= true;
									}
								}
							}
							
							if(istest)
							{
								System.out.print(".");
	//								System.out.println("Test: "+abspath);
								if(runtests)
								{
									ComponentTest test = new ComponentTest(cms, model, this);
									test.setName(abspath);
									addTest(test);
									if(ctimeout==Timeout.NONE || test.getTimeout()==Timeout.NONE)
									{
										ctimeout	= Timeout.NONE;
									}
									else
									{
										ctimeout	+= test.getTimeout();
									}
								}
							}
							else if(startable && model.getReport()==null)
							{
								System.out.print(".");
	//								System.out.println("Start: "+abspath);
								if(start)
								{
									ComponentStartTest test = new ComponentStartTest(cms, model, this);
									test.setName(abspath);
									addTest(test);
									if(ctimeout==Timeout.NONE)
									{
										ctimeout	= Timeout.NONE;
									}
									else
									{
										// Delay instead of timeout as start test should be finished after that.
										ctimeout	+= test.getTimeout();
									}
								}
							}
							else if(load)
							{
								System.out.print(".");
	//							System.out.println("Load: "+abspath);
								ComponentLoadTest test = new ComponentLoadTest(model, model.getReport());
								test.setName(abspath);
								addTest(test);
							}
						}
					}
					catch(final RuntimeException e)
					{
						if(load)
						{
							ComponentLoadTest test = new ComponentLoadTest(abspath, new IErrorReport()
							{
								public String getErrorText()
								{
									StringWriter	sw	= new StringWriter();
									e.printStackTrace(new PrintWriter(sw));
									return "Error loading model: "+sw.toString();
								}
								
								public String getErrorHTML()
								{
									return getErrorText();
								}
								
								public Map<String, String> getDocuments()
								{
									return null;
								}
							});
							test.setName(abspath);
							addTest(test);
						}
					}
				}
			}
			
			stopTimer();
			
			Logger.getLogger("ComponentTestSuite").info("Finished Building Suite for " + paths[root]+", cumulated execution timeout is: "+ctimeout);
		}
		this.timeout	= ctimeout;
	}

	protected void startTimer()
	{
		final Thread	runner	= Thread.currentThread();
		
		if(timeout!=Timeout.NONE)
		{
			timer	= new Timer(true);
			timer.schedule(new TimerTask()
			{
				public void run()
				{
					aborted	= true;
					System.out.println("Aborting test suite "+getName()+" due to excessive run time (>"+timeout+" ms).");
					if(!SReflect.isAndroid()) 
					{
						try
						{
							runner.stop(new RuntimeException("Aborting test suite "+getName()+" due to excessive run time (>"+timeout+" ms)."));
						}
						catch(UnsupportedOperationException e)
						{
							runner.stop();
						}
					} 
					else 
					{
						System.err.println("Aborting test suite "+getName()+" due to excessive run time (>"+timeout+" ms).");
						System.exit(1);
					}
				}
			}, timeout);
		}
	}
	
	protected void	stopTimer()
	{
		if(timer!=null)
		{
			timer.cancel();
			timer	= null;
		}
	}

	private List<String> scanForTestCases(File root, File path)
	{
		List<String> result = new ArrayList<String>();
		
		if (SReflect.isAndroid())
		{
			try
			{
				// Scan for resource files in .apk
				String	template	= path.toString().replace('.', '/');
				ZipFile	zip	= new ZipFile(root);
				Enumeration< ? extends ZipEntry>	entries	= zip.entries();
				while(entries.hasMoreElements())
				{
					ZipEntry	entry	= entries.nextElement();
					String name	= entry.getName();
					if(name.startsWith(template))
					{
						result.add(name);
//						System.out.println("Found potential Testcase: "+name);
					}
				}
				zip.close();
				
				// Scan for classes in .dex
				Enumeration<String> dexEntries = SUtil.androidUtils().getDexEntries(root);
				String nextElement;
				while(dexEntries.hasMoreElements())
				{
					nextElement = dexEntries.nextElement();
					if(nextElement.toLowerCase().startsWith(path.toString().toLowerCase()))
//						&& nextElement.toLowerCase().split("\\.").length  (path.toString().split("\\.").length +1))
					{
						if(!nextElement.matches(".*\\$.*"))
						{
							// path-style identifier needed for Factories, but android doesn't use a classical classpath
							nextElement = nextElement.replaceAll("\\.", "/") + ".class";
							result.add(nextElement);
//							System.out.println("Found potential Testcase: " + nextElement);
						}
					}
				}
				
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		else
		{
			List<File>	todo	= new LinkedList<File>();
//			if(path.toString().indexOf("micro")!=-1)
			todo.add(path);
			
			while(!todo.isEmpty())
			{
				File	file	= (File)todo.remove(0);
				final String	abspath	= file.getAbsolutePath();
	//			System.out.println("todo: "+abspath);
				
				if(file.isDirectory())
				{
					File[]	subs	= file.listFiles();
					todo.addAll(Arrays.asList(subs));
				}
				else
				{
					result.add(abspath);
				}
			}
		}
		
		return result;
		
	}

	/**
	 *  Indicate when the suite is aborted due to excessive run time.
	 */
	public boolean isAborted()
	{
		return aborted;
	}
	
	/**
	 *  Overridden for pre and post code.
	 */
	public void run(TestResult result)
	{
		startTimer();
		if(timer==null)
		{
			this.timer	= new Timer(true);
		}
		timer.scheduleAtFixedRate(new TimerTask()
		{
			public void run()
			{
				System.out.println("Memory: free="+SUtil.bytesToString(Runtime.getRuntime().freeMemory())
					+", max="+SUtil.bytesToString(Runtime.getRuntime().maxMemory())
					+", total="+SUtil.bytesToString(Runtime.getRuntime().totalMemory()));
			}
		}, 0, 30000);

		super.run(result);

		cleanup(result);
	}
	
	/**
	 *  Called after test suite is finished.
	 */
	protected void	cleanup(TestResult result)
	{
		try
		{
			platform.killComponent().get();
		}
		catch(Exception e)
		{
			result.addError(this, e);
		}
		platform	= null;
		
		clearAWT();
		
		stopTimer();
	}
	
	/**
	 *  Workaround for AWT/Swing memory leaks.
	 */
	public static void	clearAWT()
	{
		if(SReflect.HAS_GUI)
		{
			SNonAndroid.clearAWT();
		}
	}

	/**
	 *  Get the class loader.
	 */
	public ClassLoader getClassLoader()
	{
		return classloader;
	}
}
