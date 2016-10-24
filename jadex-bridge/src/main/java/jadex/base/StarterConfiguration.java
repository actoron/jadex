package jadex.base;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import jadex.bridge.modelinfo.ConfigurationInfo;
import jadex.bridge.modelinfo.IArgument;
import jadex.bridge.modelinfo.IModelInfo;
import jadex.bridge.modelinfo.UnparsedExpression;
import jadex.bridge.service.types.monitoring.IMonitoringService;
import jadex.commons.SReflect;
import jadex.javaparser.SJavaParser;

/**
 * Configuration of the platform setup.
 */
public class StarterConfiguration implements IStarterConfiguration {

    /** Constant for remote default timeout. */
    public static long DEFAULT_REMOTE_TIMEOUT = SReflect.isAndroid() ? 60000 : 30000;;

    /** Constant for local default timeout. */
    public static long DEFAULT_LOCAL_TIMEOUT = SReflect.isAndroid() ? 60000 : 30000;

    /** The reserved platform parameters. Those are (usually) not handled by the root component. */
    public static final Set<String> RESERVED;

    static {
        // Set deftimeout from environment, if set.
        String	dtoprop	= System.getProperty("jadex.deftimeout", System.getenv("jadex.deftimeout"));
        if(dtoprop!=null)
        {
            System.out.println("Property jadex.deftimeout is deprecated. Use jadex_deftimeout instead.");
        }
        else
        {
            dtoprop	= System.getProperty("jadex_deftimeout", System.getenv("jadex_deftimeout"));
        }
        if(dtoprop!=null)
        {
            DEFAULT_REMOTE_TIMEOUT = (Long.parseLong(dtoprop));
            DEFAULT_LOCAL_TIMEOUT = (Long.parseLong(dtoprop));
            System.out.println("Setting jadex_deftimeout: "+dtoprop);
        }

        RESERVED = new HashSet<String>();
        RESERVED.add(CONFIGURATION_FILE);
        RESERVED.add(CONFIGURATION_NAME);
        RESERVED.add(PLATFORM_NAME);
        RESERVED.add(COMPONENT_FACTORY);
        RESERVED.add(PLATFORM_COMPONENT);
        RESERVED.add(AUTOSHUTDOWN);
        RESERVED.add(MONITORING);
        RESERVED.add(RootComponentConfiguration.WELCOME);
        RESERVED.add(COMPONENT);
        RESERVED.add(DATA_PARAMETERCOPY);
        RESERVED.add(DATA_REALTIMETIMEOUT);
        RESERVED.add(PERSIST);
        RESERVED.add(DEBUGFUTURES);
        RESERVED.add(DEBUGSERVICES);
        RESERVED.add(DEBUGSTEPS);
        RESERVED.add(NOSTACKCOMPACTION);
        RESERVED.add(OPENGL);
        RESERVED.add(DEFTIMEOUT);
//		RESERVED.add(REGISTRY_SYNC);
    }

    /** Command line arguments. **/
    protected Map<String, Object>	cmdargs;
    /** Components to start. **/
    protected List<String> components;
    /** Default platform timeout. **/
    protected Long defaultTimeout;


    /** Platform model. Used to extract default values. */
    protected IModelInfo model;
    /** Name of the configured configuration **/
    protected ConfigurationInfo configurationInfo;


    /**
     * Creates an empty configuration.
     */
    public StarterConfiguration()
    {
        cmdargs = new HashMap<String, Object>();	// Starter arguments (required for instantiation of root component)
        components = new ArrayList<String>();	// Additional components to start
    }


    /**
     * Copy constructor.
     */
    public StarterConfiguration(StarterConfiguration source)
    {
        this.cmdargs = new HashMap<String, Object>(cmdargs);
        this.components = new ArrayList<String>(source.components);
    }

    /**
     * Sets the platform model to extract configuration values from it.
     * @param model
     */
    @Override
    public void setPlatformModel(IModelInfo model)
    {
        this.model = model;
        configurationInfo = getConfigurationInfo(model);
    }

    /**
     * Generic setter for cmd args.
     * @param key
     * @param value
     */
    public void setValue(String key, Object value)
    {
        this.cmdargs.put(key, value);
    }

    /**
     * Generic getter for configuration parameters.
     * Retrieves values in 3 stages:
     * 1. From given command line arguments.
     * 2. From given model configuration ("auto", "fixed", ...)
     * 3. From model default values.
     *
     * For retrieval from model, setPlatformModel has to be called before.
     * @param key
     * @return
     */
    public Object getValue(String key) {

        Object val = cmdargs.get(key);
        if(val==null && model != null && configurationInfo != null)
        {
            val = getArgumentValueFromModel(key);
        }
        else if(val instanceof String)
        {
            // Try to parse value from command line.
            try
            {
                Object newval	= SJavaParser.evaluateExpression((String)val, null);
                if(newval!=null)
                {
                    val	= newval;
                }
            }
            catch(RuntimeException e)
            {
            }
        }

        return val;
    }

    // ---- getter/setter for individual properties ----

    /** Get the platform name. */
    @Override
    public String getPlatformName()
    {
        return (String)getValue(PLATFORM_NAME);
    }
    /** Set the platform name. */
    @Override
    public void setPlatformName(String value)
    {
        setValue(PLATFORM_NAME, value);
    }

    /** Get the configuration name. */
    @Override
    public String getConfigurationName()
    {
        return (String)getValue(CONFIGURATION_NAME);
    }
    /** Set the configuration name. */
    @Override
    public void setConfigurationName(String value)
    {
        setValue(CONFIGURATION_NAME, value);
    }

    /** Get autoshutdown flag. */
    @Override
    public boolean getAutoShutdown()
    {
        return Boolean.TRUE.equals(getValue(AUTOSHUTDOWN));
    }
    /** Set autoshutdown flag. */
    @Override
    public void setAutoShutdown(boolean value)
    {
        setValue(AUTOSHUTDOWN, value);
    }

    /** Get platform component. */
    @Override
    public Class getPlatformComponent()
    {
        return (Class)getValue(PLATFORM_COMPONENT);
    }
    /** Set platform component. */
    @Override
    public void setPlatformComponent(Class value)
    {
        setValue(PLATFORM_COMPONENT, value);
    }

    /**
     * Set the default timeout.
     * @param to timeout in ms.
     */
    @Override
    public void setDefaultTimeout(long to)
    {
        defaultTimeout = to;
    }
    /**
     * Gets the default timeout.
     * @return timeout in ms.
     */
    @Override
    public Long getDefaultTimeout()
    {
        return defaultTimeout;
    }

    /**
     * Get the default timeout for local calls.
     * @return default timeout in ms.
     */
    @Override
    public long getLocalDefaultTimeout() {
        return (defaultTimeout != null) ? defaultTimeout : DEFAULT_LOCAL_TIMEOUT;
    }

    /**
     * Get the default timeout for remote calls.
     * @return default timeout in ms.
     */
    @Override
    public long getRemoteDefaultTimeout() {
        return (defaultTimeout != null) ? defaultTimeout : DEFAULT_REMOTE_TIMEOUT;
    }

    /**
     * Add a component that is started after platform startup.
     * DO NOT use this method for starting BDI agents!
     * Pass a fully qualified classname as string to addComponent(String) instead.
     * @param clazz Class of the component.
     */
    @Override
    public void addComponent(Class clazz)
    {
        // check for loaded bdi classes here?
        components.add(clazz.getName() + ".class");
    }

    /**
     * Add a component that is started after platform startup.
     * @param path Path to the component.
     */
    @Override
    public void addComponent(String path)
    {
        components.add((String)path);
    }

    /**
     * Set the list of components to be started at startup.
     * @param newcomps List of components.
     */
    @Override
    public void setComponents(List<String> newcomps)
    {
        components = newcomps;
    }
    /**
     * Get the list of components to be started at startup.
     * @return List of components
     */
    @Override
    public List<String> getComponents()
    {
        return components;
    }

    /**
     * Get the component factory.
     * @return name of component factory
     */
    @Override
    public String getComponentFactory()
    {
        return (String)cmdargs.get(COMPONENT_FACTORY)!=null?
                (String)cmdargs.get(COMPONENT_FACTORY): FALLBACK_COMPONENT_FACTORY;
    }

    /**
     * Set the main configuration file, e.g. path to PlatformAgent.
     * @param value Path to configuration file
     */
    @Override
    public void setConfigurationFile(String value)
    {
        setValue(CONFIGURATION_FILE, value);
    }
    /**
     * Get the main configuration file, e.g. path to PlatformAgent.
     * @return Path to configuration file
     */
    @Override
    public String getConfigurationFile()
    {
        return (String)cmdargs.get(CONFIGURATION_FILE)!=null?
                (String)cmdargs.get(CONFIGURATION_FILE): FALLBACK_PLATFORM_CONFIGURATION;
    }

    /**
     * Set the monitoring level.
     * @param level
     */
    @Override
    public void setMonitoring(IMonitoringService.PublishEventLevel level)
    {
        setValue(MONITORING, level);
    }
    /**
     * Get the monitoring level.
     * @return
     */
    @Override
    public IMonitoringService.PublishEventLevel getMonitoring()
    {
        return (IMonitoringService.PublishEventLevel)getValue(MONITORING);
    }

    /**
     * Set the persist flag.
     * @param value
     */
    @Override
    public void setPersist(boolean value)
    {
        setValue(PERSIST, value);
    }
    /**
     * Get the persist flag.
     * @return boolean
     */
    @Override
    public boolean getPersist()
    {
        return (Boolean)getValue(PERSIST);
    }

    /**
     * Set the debug futures flag.
     * @param value
     */
    @Override
    public void setDebugFutures(boolean value)
    {
        setValue(DEBUGFUTURES, value);
    }

    /**
     * Get the debug futures flag.
     * @return
     */
    @Override
    public boolean getDebugFutures()
    {
        return Boolean.TRUE.equals(getValue(DEBUGFUTURES));
    }

    /**
     * Set the debug services flag.
     * @param value
     */
    @Override
    public void setDebugServices(boolean value)
    {
        setValue(DEBUGSERVICES, value);
    }
    /**
     * Get the debug services flag.
     * @return
     */
    @Override
    public boolean getDebugServices()
    {
        return Boolean.TRUE.equals(getValue(DEBUGSERVICES));
    }

    /**
     * Set the debug steps flag.
     * @param value
     */
    @Override
    public void setDebugSteps(boolean value)
    {
        setValue(DEBUGSTEPS, value);
    }

    /**
     * Get the debug steps flag.
     * @return
     */
    @Override
    public boolean getDebugSteps()
    {
        return Boolean.TRUE.equals(getValue(DEBUGSTEPS));
    }

    /**
     * Set the no stack compaction flag.
     * @param value
     */
    @Override
    public void setNoStackCompaction(boolean value)
    {
        setValue(NOSTACKCOMPACTION, value);
    }

    /**
     * Get the no stack compaction flag.
     * @return True if no stack compaction.
     */
    @Override
    public boolean getNoStackCompaction()
    {
        return Boolean.TRUE.equals(getValue(NOSTACKCOMPACTION));
    }

    /**
     *  Get the boolean value of a flag.
     */
    @Override
    public boolean getBooleanValue(String key)
    {
        return Boolean.TRUE.equals(getValue(key));
    }

    /**
     * Set the OPENGL flag.
     * @param value
     */
    @Override
    public void setOpenGl(boolean value)
    {
        setValue(OPENGL, value);
        Class<?> p2d = SReflect.classForName0("jadex.extension.envsupport.observer.perspective.Perspective2D", Starter.class.getClassLoader());
        if(p2d!=null)
        {
            try
            {
                p2d.getField("OPENGL").set(null, Boolean.valueOf(value));
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
    }
    /**
     * Get the OPENGL flag.
     * @return
     */
    @Override
    public boolean getOpenGl()
    {
        return Boolean.TRUE.equals(getValue(OPENGL));
    }


    /**  Puts parsed value into component args to be available at instance. */
//	public void putArgumentValue(String name, Object val)
//	{
//		rootconfig.setValue(name, val);
//	}

    private Object getArgumentValueFromModel(String name)
    {
        Object val = null;

        boolean	found	= false;
        // first try to get the value from choosen configuration
        if(configurationInfo!=null)
        {
            UnparsedExpression[]	upes	= configurationInfo.getArguments();
            for(int i=0; !found && i<upes.length; i++)
            {
                if(name.equals(upes[i].getName()))
                {
                    found	= true;
                    val	= upes[i];
                }
            }
        }
        // if this fails, get default value.
        if(!found)
        {
            IArgument arg	= model.getArgument(name);
            if(arg!=null)
            {
                val	= arg.getDefaultValue();
            }
        }
        val	= SJavaParser.getParsedValue(val, model.getAllImports(), null, Starter.class.getClassLoader());
//			val	= UnparsedExpression.getParsedValue(val, model.getAllImports(), null, model.getClassLoader());
        return val;
    }

    /**
     * Get the configuration name.
     */
    private ConfigurationInfo	getConfigurationInfo(IModelInfo model)
    {
        String	configname	= getConfigurationName();//(String)cmdargs.get(CONFIGURATION_NAME);
        if(configname==null)
        {
            Object	val	= null;
            IArgument	arg	= model.getArgument(CONFIGURATION_NAME);
            if(arg!=null)
            {
                val	= arg.getDefaultValue();
            }
            val	= SJavaParser.getParsedValue(val, model.getAllImports(), null, Starter.class.getClassLoader());
//			val	= UnparsedExpression.getParsedValue(val, model.getAllImports(), null, model.getClassLoader());
            configname	= val!=null ? val.toString() : null;
        }

        ConfigurationInfo	compConfig	= configname!=null
                ? model.getConfiguration(configname)
                : model.getConfigurations().length>0 ? model.getConfigurations()[0] : null;

        return compConfig;
    }



    /**
     * Enhance this config with given other config.
     * Will overwrite all values that are set in the other config.
     * @param other
     */
    public void enhanceWith(StarterConfiguration other)
    {
        for (Map.Entry<String, Object> entry : other.cmdargs.entrySet())
        {
            this.setValue(entry.getKey(), entry.getValue());
        }
    }

    /**
     *  Parse an argument.
     *  @param key The key.
     *  @param stringValue The value.
     */
    public void parseArg(String key, String stringValue, Object value)
    {
        if(COMPONENT.equals(key))
        {
            addComponent((String) stringValue);
        }
        else if(DEBUGFUTURES.equals(key) && "true".equals(stringValue))
        {
            setDebugFutures(true);
        }
        else if(DEBUGSERVICES.equals(key) && "true".equals(stringValue))
        {
            setDebugServices(true);
        }
        else if(DEBUGSTEPS.equals(key) && "true".equals(stringValue))
        {
            setDebugSteps(true);
        }
        else if(DEFTIMEOUT.equals(key))
        {
//            Object value = SJavaParser.evaluateExpression(stringValue, null);
//				BasicService.DEFTIMEOUT	= ((Number)stringValue).longValue();
            long to	= ((Number)value).longValue();
//			setLocalDefaultTimeout(platform, to);
//			setRemoteDefaultTimeout(platform, to);
            setDefaultTimeout(to);

//			BasicService.setRemoteDefaultTimeout(to);
//			BasicService.setLocalDefaultTimeout(to);
//			System.out.println("timeout: "+BasicService.DEFAULT_LOCAL);
        }
        else if(NOSTACKCOMPACTION.equals(key) && "true".equals(stringValue))
        {
            setNoStackCompaction(true);
        }
        else if(OPENGL.equals(key) && "false".equals(stringValue))
        {
            setOpenGl(false);
        }
        else if(MONITORING.equals(key))
        {
            Object tmpmoni = getValue(StarterConfiguration.MONITORING);
            IMonitoringService.PublishEventLevel moni = IMonitoringService.PublishEventLevel.OFF;
            if(tmpmoni instanceof Boolean)
            {
                moni = ((Boolean)tmpmoni).booleanValue()? IMonitoringService.PublishEventLevel.FINE: IMonitoringService.PublishEventLevel.OFF;
            }
            else if(tmpmoni instanceof String)
            {
                moni = IMonitoringService.PublishEventLevel.valueOf((String)tmpmoni);
            }
            else if(tmpmoni instanceof IMonitoringService.PublishEventLevel)
            {
                moni = (IMonitoringService.PublishEventLevel)tmpmoni;
            }
            setMonitoring(moni);
        }
        else
        {
            setValue(key, value);
        }
    }

    /**
     * Checks this configuration for consistency errors.
     */
    protected void checkConsistency()
    {
        // no checks yet
    }
}
