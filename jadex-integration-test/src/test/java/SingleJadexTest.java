import junit.framework.Test;
import junit.framework.TestSuite;

import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

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

import jadex.base.Starter;
import jadex.base.test.ComponentTestSuite;
import jadex.base.test.IAbortableTestSuite;
import jadex.base.test.Testcase;
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
import jadex.commons.SReflect;
import jadex.commons.SUtil;

/**
 * Created by kalinowski on 06.01.16.
 */

@RunWith(AllTests.class)
public class SingleJadexTest extends TestSuite implements IAbortableTestSuite {
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
//		"-debugfutures", "true",
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
                    "-ssltcptransport", (SReflect.findClass0("jadex.platform.service.message.transport.ssltcpmtp.SSLTCPTransport", null, SingleJadexTest.class.getClassLoader())!=null ? "true" : "false"),
    };


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

    public SingleJadexTest() throws Exception{
//        this("jadex-applications-micro", "/jadex/micro/testcases/longcall");

        this("jadex-applications-micro", "jadex.micro.testcases.recfutures");
    }

    /**
     *  Static method called by eclipse JUnit runner.
     */
    public static Test suite() throws Exception
    {
        return new SingleJadexTest();
    }

    public SingleJadexTest(String project, String testPackage) throws Exception {
//        super(SUtil.findOutputDirs(findDirForProject(testPackage)), new File[]{new File(project)}, null);
        super(testPackage);
        testPackage = testPackage.replace('.', '/');
        File[] roots = SUtil.findOutputDirs(project);
//        File[] roots = new File[]{new File(testPackage)};
        boolean runtests = true;
        boolean start = true;
        boolean load = true;

        this.timeout	= Starter.getLocalDefaultTimeout(null);	// Initial timeout for starting platform.
        startTimer();

        // Tests must be available after constructor execution.
        // Todo: get rid of thread suspendable!?

//		System.out.println("start platform");
        platform	= (IExternalAccess)Starter.createPlatform(DEFARGS).get();
        this.timeout	= Starter.getLocalDefaultTimeout(platform.getComponentIdentifier());
//		System.out.println("end platform");
        IComponentManagementService cms = (IComponentManagementService) SServiceProvider.getService(platform, IComponentManagementService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();
        ILibraryService libsrv	= (ILibraryService)SServiceProvider.getService(platform, ILibraryService.class, RequiredServiceInfo.SCOPE_PLATFORM).get();

        // Only works with x-rid hack or maven dependency service, because rms cannot use default classloader for decoding application messages.
//		final IResourceIdentifier	rid	= null;
        long ctimeout = Starter.getLocalDefaultTimeout(platform.getComponentIdentifier());	// Start with normal timeout for platform startup/shutdown.
        IResourceIdentifier rid	= null;
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
            List<String> scanForTestCases = scanForTestCases(roots[root], testPackage);
            this.timeout = Starter.getScaledLocalDefaultTimeout(platform.getComponentIdentifier(), 0.05*(scanForTestCases.size()+1));	// Timeout for loading models.
            startTimer();
            Logger.getLogger("ComponentTestSuite").info("Scanning for testcases: " + roots[root]+" (scan timeout: "+timeout+")");
            for (String abspath : scanForTestCases)
            {
                try
                {
                    //					System.out.println("Check: "+abspath);
                    if(((Boolean) SComponentFactory.isLoadable(platform, abspath, rid).get()).booleanValue())
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
                                if(ctimeout== Timeout.NONE || test.getTimeout()==Timeout.NONE)
                                {
                                    ctimeout	= Timeout.NONE;
                                }
                                else
                                {
                                    ctimeout	+= test.getTimeout();
                                }
                            }
                        }
                        else if(startable && model.getReport()!=null)
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
                                StringWriter sw	= new StringWriter();
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

            stopTimer();

            Logger.getLogger("ComponentTestSuite").info("Finished Building Suite for " + roots[root]+", cumulated execution timeout is: "+ctimeout);
        }
        this.timeout	= ctimeout;
    }

    private List<String> scanForTestCases(File root, String testPackage)
    {
        List<String> result = new ArrayList<String>();
//
//        if (SReflect.isAndroid())
//        {
//            try
//            {
//                // Scan for resource files in .apk
//                String	template	= path.toString().replace('.', '/');
//                ZipFile zip	= new ZipFile(root);
//                Enumeration< ? extends ZipEntry> entries	= zip.entries();
//                while(entries.hasMoreElements())
//                {
//                    ZipEntry	entry	= entries.nextElement();
//                    String name	= entry.getName();
//                    if(name.startsWith(template))
//                    {
//                        result.add(name);
////						System.out.println("Found potential Testcase: "+name);
//                    }
//                }
//                zip.close();
//
//                // Scan for classes in .dex
//                Enumeration<String> dexEntries = SUtil.androidUtils().getDexEntries(root);
//                String nextElement;
//                while(dexEntries.hasMoreElements())
//                {
//                    nextElement = dexEntries.nextElement();
//                    if(nextElement.toLowerCase().startsWith(path.toString().toLowerCase()))
////						&& nextElement.toLowerCase().split("\\.").length  (path.toString().split("\\.").length +1))
//                    {
//                        if(!nextElement.matches(".*\\$.*"))
//                        {
//                            // path-style identifier needed for Factories, but android doesn't use a classical classpath
//                            nextElement = nextElement.replaceAll("\\.", "/") + ".class";
//                            result.add(nextElement);
////							System.out.println("Found potential Testcase: " + nextElement);
//                        }
//                    }
//                }
//
//            }
//            catch (IOException e)
//            {
//                e.printStackTrace();
//            }
//        }
//        else
//        {
        File p = new File(root, testPackage);
        List<File>	todo	= new LinkedList<File>();
////			if(path.toString().indexOf("micro")!=-1)
            todo.add(p);
//
            while(!todo.isEmpty())
            {
                File	file	= (File)todo.remove(0);
                final String	abspath	= file.getAbsolutePath();
//                			System.out.println("todo: "+abspath);
//
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
//        }

        return result;
    }

    protected void startTimer()
    {
        final Thread	runner	= Thread.currentThread();

        if(timeout!=Timeout.NONE)
        {
            timer	= new Timer(true);
            timer.schedule(new TimerTask() {
                public void run() {
                    aborted = true;
                    System.out.println("Aborting test suite " + getName() + " due to excessive run time (>" + timeout + " ms).");
                    if (!SReflect.isAndroid()) {
                        runner.stop();
                    } else {
                        System.err.println("Aborting test suite " + getName() + " due to excessive run time (>" + timeout + " ms).");
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

    public boolean isAborted() {
        return aborted;
    }


}
