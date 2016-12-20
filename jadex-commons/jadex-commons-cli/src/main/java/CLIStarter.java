import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import java.util.HashMap;
import java.util.Map;

import jadex.base.AbstractPlatformConfiguration;
import jadex.base.PlatformConfiguration;
import jadex.base.Starter;
import jadex.base.StarterConfiguration;
import jadex.bridge.IExternalAccess;
import jadex.bridge.service.types.monitoring.IMonitoringService;
import jadex.commons.future.IFuture;
import jadex.javaparser.SJavaParser;

import static jadex.base.IStarterConfiguration.*;

public class CLIStarter {

    /**
     *  Main for starting the platform (with meaningful fallbacks)
     *  @param args The arguments.
     *  @throws Exception
     */
    public static void main(String[] args) throws ParseException {
//		try
//		{
//			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//		}
//		catch(Exception e)
//		{
//			e.printStackTrace();
//		}

//        Options options = new Options();
//        DefaultParser parser = new DefaultParser();
//        CommandLine parse = parser.parse(options, args);
//
//        Map<String, String> results = new HashMap<String, String>();
//
//
//        Option[] parsedOptions = parse.getOptions();
//        for (Option o : parsedOptions) {
//            results.put(o.getArgName(), o.getValue());
//        }

//        PlatformConfiguration config = new PlatformConfiguration();
        PlatformConfiguration config = processArgs(args);

        // TODO: parse args with jadex

        Starter.createPlatform(config).get();

//		IExternalAccess access	= createPlatform(args).get();
//				Runtime.getRuntime().addShutdownHook(new Thread()
//				{
//					public void run()
//					{
//						try
//						{
////							System.out.println("killing: "+access.getComponentIdentifier().getPlatformName());
//							shutdown	= true;
//							access.killComponent().get(new ThreadSuspendable(), TERMINATION_TIMEOUT);
////							System.out.println("killed: "+access.getComponentIdentifier().getPlatformName());
//						}
//						catch(ComponentTerminatedException cte)
//						{
//							// Already killed.
//						}
//						catch(Throwable t)
//						{
//							t.printStackTrace();
//						}
//					}
//				});

//				// Continuously run garbage collector and finalizers.
//				Timer	gctimer	= new Timer();
//				gctimer.scheduleAtFixedRate(new TimerTask()
//				{
//					public void run()
//					{
//						System.gc();
//						System.runFinalization();
//					}
//				}, 1000, 1000);


        // Test CTRL-C shutdown behavior.
//				Timer	timer	= new Timer();
//				timer.schedule(new TimerTask()
//				{
//					public void run()
//					{
//						System.out.println(getClass().getName()+": Calling System.exit() for testing.");
//						System.exit(0);
//					}
//				}, 5000);
    }


    /**
     *  Parse an argument.
     *  @param key The key.
     *  @param stringValue The value.
     */
    public static void parseArg(String key, String stringValue, Object value, AbstractPlatformConfiguration config)
    {
        if(IStarterConfiguration.COMPONENT.equals(key))
        {
            config.addComponent((String) stringValue);
        }
        else if(IStarterConfiguration.DEBUGFUTURES.equals(key) && "true".equals(stringValue))
        {
            config.setDebugFutures(true);
        }
        else if(IStarterConfiguration.DEBUGSERVICES.equals(key) && "true".equals(stringValue))
        {
            config.setDebugServices(true);
        }
        else if(IStarterConfiguration.DEBUGSTEPS.equals(key) && "true".equals(stringValue))
        {
            config.setDebugSteps(true);
        }
        else if(IStarterConfiguration.DEFTIMEOUT.equals(key))
        {
            value = SJavaParser.evaluateExpression(stringValue, null);
//			BasicService.DEFTIMEOUT	= ((Number)stringValue).longValue();
            long to	= ((Number)value).longValue();
//			setLocalDefaultTimeout(platform, to);
//			setRemoteDefaultTimeout(platform, to);
            config.setDefaultTimeout(to);

//			BasicService.setRemoteDefaultTimeout(to);
//			BasicService.setLocalDefaultTimeout(to);
//			System.out.println("timeout: "+BasicService.DEFAULT_LOCAL);
        }
        else if(IStarterConfiguration.NOSTACKCOMPACTION.equals(key) && "true".equals(stringValue))
        {
            config.setNoStackCompaction(true);
        }
        else if(IStarterConfiguration.OPENGL.equals(key) && "false".equals(stringValue))
        {
            config.setOpenGl(false);
        }
        else if(IStarterConfiguration.MONITORING.equals(key))
        {
            IMonitoringService.PublishEventLevel moni = IMonitoringService.PublishEventLevel.OFF;
            if(value instanceof Boolean)
            {
                moni = ((Boolean)value).booleanValue()? IMonitoringService.PublishEventLevel.FINE: IMonitoringService.PublishEventLevel.OFF;
            }
            else if(value instanceof String)
            {
                moni = IMonitoringService.PublishEventLevel.valueOf((String)value);
            }
            else if(value instanceof IMonitoringService.PublishEventLevel)
            {
                moni = (IMonitoringService.PublishEventLevel)value;
            }
            config.setMonitoring(moni);
        }
        else
        {
            config.setValue(key, value);
        }
    }

    /**
     *  Create a platform configuration.
     *  @param args The command line arguments.
     *  @return StarterConfiguration
     */
    public static PlatformConfiguration processArgs(String args)
    {
        return processArgs(args.split("\\s+"));
    }

    /**
     *  Create a platform configuration.
     *  @param args The command line arguments.
     *  @return StarterConfiguration
     */
    public static PlatformConfiguration processArgs(String[] args)
    {
        PlatformConfiguration config = new PlatformConfiguration(args);
        if(args!=null)
        {
            for(int i=0; args!=null && i<args.length; i+=2)
            {
                parseArg(args[i], args[i+1], config);
            }
        }
        config.getRootConfig().setProgramArguments(args);

        return config;
    }

    public static void parseArgs(String[] args, AbstractPlatformConfiguration config) {
        for(int i=0; args!=null && i<args.length; i+=2)
        {
            parseArg(args[i], args[i+1], config);
        }
    }

    public static void parseArg(String okey, String val, AbstractPlatformConfiguration config) {
        String key = okey.startsWith("-")? okey.substring(1): okey;
        Object value = val;
        if(!StarterConfiguration.RESERVED.contains(key))
        {
            // if not reserved, value is parsed and written to root config.
            try
            {
                value = SJavaParser.evaluateExpression(val, null);
            }
            catch(Exception e)
            {
                System.out.println("Argument parse exception using as string: "+key+" \""+val+"\"");
            }
            config.getRootConfig().setValue(key, value);
        }

        parseArg(key, val, value, config);
    }

    /**
     *  Create the platform.
     *  @param args The command line arguments.
     *  @return The external access of the root component.
     *  @deprecated use PlatformConfiguration object instead.
     */
    @Deprecated
    public static IFuture<IExternalAccess> createPlatform(String... args)
    {
        PlatformConfiguration config = processArgs(args);
        return Starter.createPlatform(config);
    }

    /**
     *  Create the platform.
     *  @param args The command line arguments.
     *  @return The external access of the root component.
     *  @deprecated since 3.0.7. Use other createPlatform methods instead.
     */
    @Deprecated
    public static IFuture<IExternalAccess> createPlatform(Map<String, String> args)
    {
        PlatformConfiguration config = processArgs(args);
        return Starter.createPlatform(config);
    }

    /**
     *  Create a platform configuration.
     *  @param args The command line arguments.
     *  @return StarterConfiguration
     *  @deprecated since 3.0.7. Use other processArgs methods instead.
     */
    @Deprecated
    public static PlatformConfiguration processArgs(Map<String, String> args)
    {
        PlatformConfiguration config = new PlatformConfiguration(); // ?! hmm needs to be passed as parameter also?
        if(args!=null)
        {
            for(Map.Entry<String, String> arg: args.entrySet())
            {
                parseArg(arg.getKey(), arg.getValue(), config);
            }
        }
        return config;
    }
}
