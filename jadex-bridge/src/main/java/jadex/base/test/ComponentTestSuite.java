package jadex.base.test;

import jadex.base.Starter;
import jadex.base.test.impl.BrokenComponentTest;
import jadex.base.test.impl.ComponentStartTest;
import jadex.base.test.impl.ComponentTest;
import jadex.bridge.IErrorReport;
import jadex.bridge.IExternalAccess;
import jadex.bridge.IResourceIdentifier;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.cms.IComponentManagementService;
import jadex.bridge.service.types.factory.SComponentFactory;
import jadex.bridge.service.types.library.ILibraryService;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.future.Future;
import jadex.commons.future.ISuspendable;
import jadex.commons.future.ThreadSuspendable;

import java.awt.BorderLayout;
import java.awt.KeyboardFocusManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import junit.framework.TestResult;
import junit.framework.TestSuite;


/**
 * Execute multiple component tests in a test suite.
 */
public class ComponentTestSuite extends TestSuite
{
	//-------- attributes --------
	
	/** Indicate when the suite is aborted due to excessibe run time. */
	public boolean	aborted;
	
	/** The platform. */
	protected IExternalAccess	platform;
	
	/** The timeout timer (if any). */
	protected Timer	timer;
	
	/** The print memory timer (if any). */
	protected Timer	memtimer;
	
	//-------- constructors --------

	/**
	 * Create a component test suite for components contained in a given path.
	 * @param path	The path to look for test cases in.
	 * @param root	The classpath root corresponding to the path.
	 * @param excludes	Files to exclude (if a pattern is contained in file path). 
	 */
	public ComponentTestSuite(File path, File root, String[] excludes) throws Exception
	{
		this(path, root, excludes, SReflect.isAndroid() ? 2000000 : BasicService.getLocalDefaultTimeout()*10, true, true);
	}
	
	/**
	 * Create a component test suite for components contained in a given path.
	 * @param path	The path to look for test cases in.
	 * @param root	The classpath root corresponding to the path.
	 * @param excludes	Files to exclude (if a pattern is contained in file path). 
	 * @param timeout	The test suite timeout (if tests are not completed, execution will be aborted). 
	 * @param broken	Include broken components.
	 * @param start	Try starting components, which are no test cases.
	 */
	public ComponentTestSuite(File path, File root, String[] excludes, long timeout, boolean broken, boolean start) throws Exception
	{
		this(new String[]
		{
			"-platformname", "testcases_*",
//			"-kernels", "\"all\"",	// Required for old hudson build, otherwise wrong bdi kernel is used as dependencies are not in correct order
			"-simulation", "true",
			"-asyncexecution", "true",
//			"-libpath", "new String[]{\""+root.toURI().toURL().toString()+"\"}",
//			"-logging", "true",
//			"-debugfutures", "true",
//			"-nostackcompaction", "true",
			"-gui", "false",
			"-awareness", "false",
			"-saveonexit", "false",
			"-welcome", "false",
			"-autoshutdown", "false",
			"-opengl", "false",
			"-cli", "false",
//			"-niotcptransport", "false",
//			"-tcptransport", "true",
//			"-deftimeout", "-1",
			"-printpass", "false",
			// Hack!!! include ssl transport if available
			"-ssltcptransport", (SReflect.findClass0("jadex.platform.service.message.transport.ssltcpmtp.SSLTCPTransport", null, ComponentTestSuite.class.getClassLoader())!=null ? "true" : "false"),  
		}, path, root, excludes, timeout, broken, start);
	}
	
	/**
	 * Create a component test suite for components contained in a given path.
	 * @param args	The platform arguments.
	 * @param path	The path to look for test cases in.
	 * @param excludes	Files to exclude (if a pattern is contained in file path). 
	 * @param timeout	The test suite timeout (if tests are not completed, execution will be aborted).
	 * @param broken	Include broken components.
	 * @param start	Try starting components, which are no test cases.
	 */
	public ComponentTestSuite(String[] args, File path, File root, String[] excludes, final long timeout, final boolean broken, final boolean start) throws Exception
	{
		super(path.toString());
		
		final Thread	runner	= Thread.currentThread();
		
		if(timeout>0)
		{
			timer	= new Timer(true);
			timer.schedule(new TimerTask()
			{
				public void run()
				{
					aborted	= true;
//					System.out.println("Aborting test suite "+getName()+" due to excessive run time (>"+timeout+" ms).");
					if (!SReflect.isAndroid()) {
						runner.stop(new RuntimeException("Aborting test suite "+getName()+" due to excessive run time (>"+timeout+" ms)."));
					} else {
						System.err.println("Aborting test suite "+getName()+" due to excessive run time (>"+timeout+" ms).");
						System.exit(1);
					}
				}
			}, timeout);
		}
		
		// Tests must be available after constructor execution.
		// Todo: get rid of thread suspendable!?
		ISuspendable	ts	= new ThreadSuspendable();
		
//		System.out.println("start platform");
		platform	= (IExternalAccess)Starter.createPlatform(args).get(ts);
//		System.out.println("end platform");
		IComponentManagementService cms = (IComponentManagementService)SServiceProvider.getServiceUpwards(platform.getServiceProvider(), IComponentManagementService.class).get(ts);
		ILibraryService libsrv	= (ILibraryService)SServiceProvider.getService(platform.getServiceProvider(), ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).get(ts);
		
		// Only works with x-rid hack or maven dependency service, because rms cannot use default classloader for decoding application messages.
//		final IResourceIdentifier	rid	= null;
		final IResourceIdentifier	rid;
		try
		{
			URL url = root.toURI().toURL();
			rid	= libsrv.addURL(null, url).get(ts);
		}
		catch(Exception e)
		{
			throw new RuntimeException(e);
		}
		
		// Scan for test cases.
		System.out.println("Scanning for testcases: "+path);
		List<String> scanForTestCases = scanForTestCases(root, path);
		for (String abspath : scanForTestCases)
		{	
			if (SReflect.isAndroid()) {
				// path-style identifier needed for Factories, but android doesn't use a
				// classical classpath
				abspath = abspath.replaceAll("\\.", "/") + ".class";
			}
			boolean	exclude	= false;
						
			for(int i=0; !exclude && excludes!=null && i<excludes.length; i++)
			{
				exclude	= abspath.indexOf(excludes[i])!=-1;
			}
			
			if(!exclude)
			{
				if(((Boolean)SComponentFactory.isLoadable(platform, abspath, rid).get(ts)).booleanValue())
				{
					if(((Boolean)SComponentFactory.isStartable(platform, abspath, rid).get(ts)).booleanValue())
					{
//						System.out.println("Building TestCase: " + abspath);
						try
						{
							IModelInfo model = (IModelInfo)SComponentFactory.loadModel(platform, abspath, rid).get(ts);
							boolean istest = false;
							if(model!=null && model.getReport()==null)
							{
								IArgument[]	results	= model.getResults();
								for(int i=0; !istest && i<results.length; i++)
								{
									if(results[i].getName().equals("testresults") && Testcase.class.equals(
										results[i].getClazz().getType(libsrv.getClassLoader(rid).get(ts), model.getAllImports())))
									{
										istest	= true;
									}
								}
							}
							
							if(istest)
							{
								ComponentTest test = new ComponentTest(cms, model, this);
								test.setName(abspath);
								addTest(test);
							}
							else if(model.getReport()!=null)
							{
								if(broken)
								{
									BrokenComponentTest test = new BrokenComponentTest(abspath, model.getReport());
									test.setName(abspath);
									addTest(test);
								}
							}
							else
							{
								if(start)
								{
									ComponentStartTest test = new ComponentStartTest(cms, model, this);
									test.setName(abspath);
									addTest(test);
								}
							}
						}
						catch(final RuntimeException e)
						{
							BrokenComponentTest test = new BrokenComponentTest(abspath, new IErrorReport()
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
							});
							test.setName(abspath);
							addTest(test);							
						}
					}
				}
			}
		}
		
//		System.out.println("Finished Building Suite for " + path);
	}

	private List<String> scanForTestCases(File root, File path)
	{
		List<String> result = new ArrayList<String>();
		
		List<File>	todo	= new LinkedList<File>();
//		if(path.toString().indexOf("micro")!=-1)
		todo.add(path);
		
		if (SReflect.isAndroid())
		{
			try
			{
				Enumeration<String> dexEntries = SUtil.androidUtils().getDexEntries(root);
				String nextElement;
				while (dexEntries.hasMoreElements()) {
					nextElement = dexEntries.nextElement();
					if (nextElement.toLowerCase().startsWith(path.toString().toLowerCase())) {
//							&& nextElement.toLowerCase().split("\\.").length  (path.toString().split("\\.").length +1)) {
						if (!nextElement.matches(".*\\$.*")) {
							result.add(nextElement);
							System.out.println("Found potential Testcase: " + nextElement);
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
		this.memtimer	= new Timer(true);
		memtimer.scheduleAtFixedRate(new TimerTask()
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
		if(timer!=null)
		{
			timer.cancel();
			timer	= null;
		}
		
		if(memtimer!=null)
		{
			memtimer.cancel();
			memtimer	= null;
		}
		
		try
		{
			platform.killComponent().get(new ThreadSuspendable());
		}
		catch(Exception e)
		{
			result.addError(this, e);
		}
		platform	= null;
		
		clearAWT();
	}
	
	/**
	 *  Workaround for AWT/Swing memory leaks.
	 */
	public static void	clearAWT()
	{
		// Java Bug not releasing the last focused window, see:
		// http://www.lucamasini.net/Home/java-in-general-/the-weakness-of-swing-s-memory-model
		// http://bugs.sun.com/view_bug.do?bug_id=4726458
		
		final Future<Void>	disposed	= new Future<Void>();
		
		SwingUtilities.invokeLater(new Runnable()
		{
			public void run()
			{
				javax.swing.Timer	t	= new javax.swing.Timer(100, new ActionListener()
				{
					public void actionPerformed(ActionEvent e)
					{
						final JFrame f	= new JFrame("dummy");
						f.getContentPane().add(new JButton("Dummy"), BorderLayout.CENTER);
						f.setSize(100, 100);
						f.setVisible(true);
						
						javax.swing.Timer	t	= new javax.swing.Timer(100, new ActionListener()
						{
							public void actionPerformed(ActionEvent e)
							{
								f.dispose();
								javax.swing.Timer	t	= new javax.swing.Timer(100, new ActionListener()
								{
									public void actionPerformed(ActionEvent e)
									{
//										System.out.println("cleanup dispose");
										KeyboardFocusManager.getCurrentKeyboardFocusManager().clearGlobalFocusOwner();
										disposed.setResult(null);
									}
								});
								t.setRepeats(false);
								t.start();

							}
						});
						t.setRepeats(false);
						t.start();
					}
				});
				t.setRepeats(false);
				t.start();
			}
		});
		
		disposed.get(new ThreadSuspendable(), 30000);
		
//		// Another bug not releasing the last drawn window.
//		// http://bugs.sun.com/bugdatabase/view_bug.do?bug_id=6857676
//		
//		try
//		{
//			Class<?> clazz	= Class.forName("sun.java2d.pipe.BufferedContext");
//			Field	field	= clazz.getDeclaredField("currentContext");
//			field.setAccessible(true);
//			field.set(null, null);
//		}
//		catch(Throwable e)
//		{
//			e.printStackTrace();
//		}

	}
}
