package jadex.base.service.settings;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.types.settings.ISettingsService;
import jadex.commons.IPropertiesProvider;
import jadex.commons.Properties;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.xml.PropertiesXMLHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 *  Default settings service implementation.
 */
public class SettingsService extends BasicService implements ISettingsService
{
	// -------- constants --------

	/** The filename extension for settings. */
	public static final String	SETTINGS_EXTENSION	= ".settings.xml";

	//-------- attributes --------
	
	/** The service provider. */
	protected IInternalAccess	access;
	
	/** The properties filename. */
	protected String filename;
	
	/** The current properties. */
	protected Properties	props;
	
	/** The registered properties provider (id->provider). */
	protected Map	providers;
	
	/** Save settings on exit?. */
	protected boolean	saveonexit;
	
	//-------- constructors --------
	
	/**
	 *  Create a settings service.
	 *  @param prefix The settings file prefix to be used (if any).
	 *    Uses name from service provider, if no prefix is given.
	 */
	public SettingsService(IInternalAccess access, boolean saveonexit)
	{
		super(access.getServiceContainer().getId(), ISettingsService.class, null);
		this.access	= access;
		this.providers	= new LinkedHashMap();
		this.saveonexit	= saveonexit;
		
		filename	= access.getComponentIdentifier().getPlatformPrefix() + SETTINGS_EXTENSION;
	}
	
	//-------- BasicService overridings --------
	
	/**
	 *  Start the service.
	 *  @return A future that is done when the service has completed starting.  
	 */
	public IFuture<Void>	startService()
	{
		final Future<Void>	ret	= new Future<Void>();
		super.startService().addResultListener(access.createResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				loadProperties().addResultListener(access.createResultListener(new DelegationResultListener(ret)));
			}
		}));
		return ret;
	}
	
	/**
	 *  Shutdown the service.
	 *  @return A future that is done when the service has completed its shutdown.  
	 */
	public IFuture<Void>	shutdownService()
	{
		final Future<Void>	ret	= new Future<Void>();
		if(saveonexit)
		{
			// Cannot use access.createResultListener() as component is already terminated.
			saveProperties(true).addResultListener(new DelegationResultListener<Void>(ret)
			{
				public void customResultAvailable(Void result)
				{
					SettingsService.super.shutdownService().addResultListener(new DelegationResultListener<Void>(ret));
				}
			});
		}
		else
		{
			super.shutdownService().addResultListener(new DelegationResultListener<Void>(ret));
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
//		System.out.println("register: "+id);
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
				provider.setProperties(sub).addResultListener(access.createResultListener(new DelegationResultListener(ret)));
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
				provider.getProperties().addResultListener(access.createResultListener(new DelegationResultListener(ret)
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
				.addResultListener(access.createResultListener(new DelegationResultListener(ret)));
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
	public IFuture<Properties> loadProperties()
	{
		final Future<Properties>	ret	= new Future<Properties>();
		
		try
		{
			props = readPropertiesFromStore();
		}
		catch(Exception e)
		{
			props	= new Properties();
		}
		
		final CounterResultListener	crl	= new CounterResultListener(providers.size(),
			access.createResultListener(new DelegationResultListener(ret)));
		for(Iterator it=providers.keySet().iterator(); it.hasNext(); )
		{
			final String	id	= (String)it.next();
			IPropertiesProvider	provider	= (IPropertiesProvider)providers.get(id);
			
			Properties	sub	= props.getSubproperty(id);
			if(sub!=null)
			{
				provider.setProperties(sub).addResultListener(access.createResultListener(crl));
			}
			else
			{
				crl.resultAvailable(null);
			}
		}

		return ret;
	}
	
	/**
	 * Reads and returns the stored Properties, usually from a file.
	 * @return {@link Properties}
	 * @throws FileNotFoundException
	 * @throws Exception
	 * @throws IOException
	 */
	protected Properties readPropertiesFromStore() throws FileNotFoundException, IOException 
	{
		Properties ret;
		// Todo: Which class loader to use? library service unavailable, because it depends on settings service?
		File file = getFile(filename);
		FileInputStream fis = new FileInputStream(file.exists() ? file : getFile("default"+SETTINGS_EXTENSION));
		ret	= (Properties)PropertiesXMLHelper.read(fis, getClass().getClassLoader());
		fis.close();
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
				try
				{
					writePropertiesToStore(props);
				}
				catch(Exception e)
				{
					System.out.println("Warning: Could not save settings: "+e);
				}
				ret.setResult(null);
			}

			
		};
		rl	= shutdown ? rl : access.createResultListener(rl); 
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
			rl	= shutdown ? rl : access.createResultListener(rl); 
			provider.getProperties().addResultListener(rl);
		}
		
		return ret;
	}
	

	/**
	 * Writes the given properties into a Store, usually a file.
	 * @throws FileNotFoundException
	 * @throws Exception
	 * @throws IOException
	 */
	protected void writePropertiesToStore(Properties props) throws FileNotFoundException,
			Exception, IOException 
	{
		// Todo: Which class loader to use? library service unavailable, because
		// it depends on settings service?
		File file = getFile(filename);
		FileOutputStream os = new FileOutputStream(file);
		PropertiesXMLHelper.write(props, os, getClass().getClassLoader());
		os.close();
	}
	
	//-------- Helper methods --------
	
	/**
	 * Returns the File object for a path to a file.
	 * @param path Path to the file
	 * @return The File Object for the given path.
	 */
	protected File getFile(String path) {
		return new File(path);
	}
}
