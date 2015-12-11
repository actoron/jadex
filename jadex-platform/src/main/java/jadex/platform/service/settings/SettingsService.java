package jadex.platform.service.settings;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IArgumentsResultsFeature;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.RequiredServiceInfo;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.context.IContextService;
import jadex.bridge.service.types.settings.ISettingsService;
import jadex.commons.IPropertiesProvider;
import jadex.commons.Properties;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.xml.PropertiesXMLHelper;

/**
 *  Default settings service implementation.
 */
@Service
public class SettingsService implements ISettingsService
{
	// -------- constants --------

	/** The filename extension for settings. */
	public static final String	SETTINGS_EXTENSION	= ".settings.xml";

	//-------- attributes --------
	
	/** The service provider. */
	@ServiceComponent
	protected IInternalAccess	access;
	
	/** The properties filename. */
	protected String filename;
	
	/** The current properties. */
	protected Properties	props;
	
	/** The registered properties provider (id->provider). */
	protected Map<String, IPropertiesProvider>	providers;
	
	/** Save settings on exit?. */
	protected boolean	saveonexit;
	
	/** The context service. */
	protected IContextService contextService;
	
	//-------- Service methods --------
	
	/**
	 *  Start the service.
	 *  @return A future that is done when the service has completed starting.  
	 */
	@ServiceStart
	public IFuture<Void>	startService()
	{
		this.providers	= new LinkedHashMap<String, IPropertiesProvider>();
		Object	soe	= access.getComponentFeature(IArgumentsResultsFeature.class).getArguments().get("saveonexit");
		this.saveonexit	= soe instanceof Boolean && ((Boolean)soe).booleanValue();
		this.filename	= access.getComponentIdentifier().getPlatformPrefix() + SETTINGS_EXTENSION;
		
		final Future<Void>	ret	= new Future<Void>();
		contextService = SServiceProvider.getLocalService(access, IContextService.class, RequiredServiceInfo.SCOPE_PLATFORM);
		loadProperties().addResultListener(new DelegationResultListener<Void>(ret));
		
		return ret;
	}
	
	/**
	 *  Shutdown the service.
	 *  @return A future that is done when the service has completed its shutdown.  
	 */
	@ServiceShutdown
	public IFuture<Void>	shutdownService()
	{
		final Future<Void>	ret	= new Future<Void>();
		if(saveonexit)
		{
			saveProperties(true).addResultListener(new DelegationResultListener<Void>(ret));
		}
		else
		{
			ret.setResult(null);
		}
		return ret;
	}
	
	//-------- ISettingsService interface --------
	
	/**
	 *  Register a property provider.
	 *  Settings of registered property providers will be automatically saved
	 *  and restored, when properties are loaded.
	 *  @param id 	A unique id to identify the properties (e.g. component or service name).
	 *  @param provider 	The properties provider.
	 */
	public IFuture<Void>	registerPropertiesProvider(String id, IPropertiesProvider provider)
	{
		Future<Void>	ret	= new Future<Void>();
		if(providers.containsKey(id))
		{
			ret.setException(new IllegalArgumentException("Id already contained: "+id));
		}
		else
		{
//			System.out.println("Added provider: "+id+", "+provider);
			providers.put(id, provider);
			Properties	sub	= props.getSubproperty(id);
			if(sub!=null)
			{
				provider.setProperties(sub).addResultListener(access.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)));
			}
			else
			{
				ret.setResult(null);
			}
		}
		return ret;
	}
	
	/**
	 *  Deregister a property provider.
	 *  Settings of a deregistered property provider will be saved
	 *  before the property provider is removed.
	 *  @param id 	A unique id to identify the properties (e.g. component or service name).
	 */
	public IFuture<Void>	deregisterPropertiesProvider(final String id)
	{
		final Future<Void>	ret	= new Future<Void>();
		if(!providers.containsKey(id))
		{
			ret.setException(new IllegalArgumentException("Id not contained: "+id));
		}
		else
		{
			IPropertiesProvider	provider	= (IPropertiesProvider)providers.remove(id);
//			System.out.println("Removed provider: "+id+", "+provider);
			if(saveonexit)
			{
//				provider.getProperties().addResultListener(new DelegationResultListener(ret)
				provider.getProperties().addResultListener(access.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)
				{
					public void customResultAvailable(Object result)
					{
						props.removeSubproperties(id);
						props.addSubproperties(id, (Properties)result);
						ret.setResult(null);
					}
				}));
			}
			else
			{
				ret.setResult(null);
			}
		}
		return ret;
	}
	
	/**
	 *  Set the properties for a given id.
	 *  Overwrites existing settings (if any).
	 *  @param id 	A unique id to identify the properties (e.g. component or service name).
	 *  @param properties 	The properties to set.
	 *  @param save 	Save platform properties after setting.
	 *  @return A future indicating when properties have been set.
	 */
	public IFuture<Void>	setProperties(String id, Properties props)
	{
//		System.out.println("Set properties: "+id);
		final Future<Void>	ret	= new Future<Void>();
		this.props.removeSubproperties(id);
		this.props.addSubproperties(id, props);
		
		if(providers.containsKey(id))
		{
			((IPropertiesProvider)providers.get(id)).setProperties(props)
				.addResultListener(access.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)));
		}
		else
		{
			ret.setResult(null);
		}
		
		return ret;
	}
	
	/**
	 *  Get the properties for a given id.
	 *  @param id 	A unique id to identify the properties (e.g. component or service name).
	 *  @return A future containing the properties (if any).
	 */
	public IFuture<Properties>	getProperties(String id)
	{
		return new Future<Properties>(props.getSubproperty(id));
	}
	
	
	/**
	 *  Load the default platform properties.
	 *  @return A future indicating when properties have been loaded.
	 */
	public IFuture<Void> loadProperties()
	{
		final Future<Void>	ret	= new Future<Void>();
		
		readOrCreateProperties().addResultListener(new ExceptionDelegationResultListener<Properties, Void>(ret)
		{
			public void customResultAvailable(Properties mprops)
			{
				props = mprops;
				
				final CounterResultListener<Void>	crl	= new CounterResultListener<Void>(providers.size(),
					access.getComponentFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Void>(ret)));
				
				for(Iterator<String> it=providers.keySet().iterator(); it.hasNext(); )
				{
					final String	id	= it.next();
					IPropertiesProvider	provider = providers.get(id);
					
					Properties	sub	= props.getSubproperty(id);
					if(sub!=null)
					{
						provider.setProperties(sub).addResultListener(access.getComponentFeature(IExecutionFeature.class).createResultListener(crl));
					}
					else
					{
						crl.resultAvailable(null);
					}
				}
			}
		});
		
		return ret;
	}
	
	/**
	 * 
	 */
	protected IFuture<Properties> readOrCreateProperties()
	{
		final Future<Properties> ret = new Future<Properties>();
		readPropertiesFromStore().addResultListener(new IResultListener<Properties>()
		{
			public void resultAvailable(Properties result)
			{
				ret.setResult(result);
			}
			
			public void exceptionOccurred(Exception exception)
			{
				ret.setResult(new Properties());
			}
		});
		return ret;
	}
	
	/**
	 * Reads and returns the stored Properties, usually from a file.
	 * @return {@link Properties}
	 * @throws FileNotFoundException
	 * @throws Exception
	 * @throws IOException
	 */
	protected IFuture<Properties> readPropertiesFromStore() //throws FileNotFoundException, IOException 
	{
		final Future<Properties> ret = new Future<Properties>();
		
		// Todo: Which class loader to use? library service unavailable, because it depends on settings service?
		getFile(filename).addResultListener(new ExceptionDelegationResultListener<File, Properties>(ret)
		{
			public void customResultAvailable(File file)
			{
				if(!file.exists())
				{
					getFile("default"+SETTINGS_EXTENSION).addResultListener(new ExceptionDelegationResultListener<File, Properties>(ret)
					{
						public void customResultAvailable(File file)
						{
							proceed(file);
						}
					});
				}
				else
				{
					proceed(file);
				}
			}
			
			protected void proceed(File file)
			{
				FileInputStream fis = null;
				try
				{
					fis = new FileInputStream(file);
					Properties props = (Properties)PropertiesXMLHelper.read(fis, getClass().getClassLoader());
					ret.setResult(props);
				}
				catch(Exception e)
				{
					ret.setException(e);
				}
				finally
				{
					if(fis!=null)
					{
						try
						{
							fis.close();
						}
						catch(Exception e)
						{
						}
					}
				}
			}
		});
		
		return ret;
	}

	/**
	 *  Save the platform properties to the default location.
	 *  @return A future indicating when properties have been saved.
	 */
	public IFuture<Void>	saveProperties()
	{
		return saveProperties(false);
	}
	
	/**
	 *  Save the platform properties to the default location.
	 *  @param shutdown	Flag indicating if called during shutdown.
	 *  @return A future indicating when properties have been saved.
	 */
	public IFuture<Void>	saveProperties(boolean shutdown)
	{
//		System.out.println("Save properties"+(shutdown?" (shutdown)":""));
		final Future<Void>	ret	= new Future<Void>();
		
		IResultListener	rl	= new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				writePropertiesToStore(props);
				ret.setResult(null);
			}

			
		};
		rl	= shutdown ? rl : access.getComponentFeature(IExecutionFeature.class).createResultListener(rl); 
		final CounterResultListener	crl	= new CounterResultListener(providers.size(), rl);
		
		for(Iterator it=providers.keySet().iterator(); it.hasNext(); )
		{
			final String	id	= (String)it.next();
			IPropertiesProvider	provider	= (IPropertiesProvider)providers.get(id);
			rl	= new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					props.removeSubproperties(id);
					props.addSubproperties(id, (Properties)result);
					crl.resultAvailable(null);
				}
			};
			rl	= shutdown ? rl : access.getComponentFeature(IExecutionFeature.class).createResultListener(rl); 
			provider.getProperties().addResultListener(rl);
		}
		
		return ret;
	}
	
	/**
	 *  Set the save on exit policy.
	 *  @param saveonexit The saveonexit flag.
	 */
	public IFuture<Void> setSaveOnExit(boolean saveonexit)
	{
		this.saveonexit = saveonexit;
		return IFuture.DONE;
	}
	
	//-------- Helper methods --------

	/**
	 * Writes the given properties into a Store, usually a file.
	 * @throws FileNotFoundException
	 * @throws Exception
	 * @throws IOException
	 */
	protected void writePropertiesToStore(final Properties props) //throws FileNotFoundException, Exception, IOException 
	{
		// Todo: Which class loader to use? library service unavailable, because
		// it depends on settings service?
		getFile(filename).addResultListener(new IResultListener<File>()
		{
			public void resultAvailable(File file)
			{
				FileOutputStream os = null;
				try
				{
					os = new FileOutputStream(file);
					PropertiesXMLHelper.write(props, os, getClass().getClassLoader());
				}
				catch(Exception e)
				{
					System.out.println("Warning: Could not save settings: "+e);
				}
				finally
				{
					if(os!=null)
					{
						try
						{
							os.close();
						}
						catch(Exception e)
						{
						}
					}
				}
			}
			
			public void exceptionOccurred(Exception exception)
			{
				
			}
		});
	
	}
	
	/**
	 * Returns the File object for a path to a file.
	 * @param path Path to the file
	 * @return The File Object for the given path.
	 */
	protected IFuture<File> getFile(String path) 
	{
		return contextService.getFile(path);
	}
}
