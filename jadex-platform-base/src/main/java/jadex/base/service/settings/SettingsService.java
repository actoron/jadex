package jadex.base.service.settings;

import jadex.bridge.ISettingsService;
import jadex.bridge.service.BasicService;
import jadex.bridge.service.IServiceProvider;
import jadex.commons.IPropertiesProvider;
import jadex.commons.Properties;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.xml.PropertiesXMLHelper;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
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
	protected IServiceProvider	provider;
	
	/** The properties file. */
	protected File	file;
	
	/** The current properties. */
	protected Properties	props;
	
	/** The registered properties provider (id->provider). */
	protected Map	providers;
	
	//-------- constructors --------
	
	/**
	 *  Create a settings service.
	 *  @param prefix The settings file prefix to be used (if any).
	 *    Uses name from service provider, if no prefix is given.
	 */
	public SettingsService(String prefix, IServiceProvider provider)
	{
		super(provider.getId(), ISettingsService.class, null);
		this.provider	= provider;
		this.providers	= new LinkedHashMap();
		
		if(prefix==null)
		{
			prefix	= provider.getId().toString();
			
			// Strip auto-generated platform suffix (hack???).
			if(prefix.indexOf('_')!=-1)
			{
				prefix	= prefix.substring(0, prefix.lastIndexOf('_'));
			}
		}
		
		file	= new File(prefix + SETTINGS_EXTENSION);
	}
	
	//-------- BasicService overridings --------
	
	/**
	 *  Start the service.
	 *  @return A future that is done when the service has completed starting.  
	 */
	public IFuture	startService()
	{
		final Future	ret	= new Future();
		super.startService().addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				loadProperties().addResultListener(new DelegationResultListener(ret));
			}
		});
		return ret;
	}
	
	/**
	 *  Shutdown the service.
	 *  @return A future that is done when the service has completed its shutdown.  
	 */
	public IFuture	shutdownService()
	{
		final Future	ret	= new Future();
		saveProperties().addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				SettingsService.super.shutdownService().addResultListener(new DelegationResultListener(ret));
			}
		});
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
	public IFuture	registerPropertiesProvider(String id, IPropertiesProvider provider)
	{
		Future	ret	= new Future();
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
				provider.setProperties(sub).addResultListener(new DelegationResultListener(ret));
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
	public IFuture	deregisterPropertiesProvider(final String id)
	{
		final Future	ret	= new Future();
		if(!providers.containsKey(id))
		{
			ret.setException(new IllegalArgumentException("Id not contained: "+id));
		}
		else
		{
			IPropertiesProvider	provider	= (IPropertiesProvider)providers.remove(id);
//			System.out.println("Removed provider: "+id+", "+provider);
			provider.getProperties().addResultListener(new DelegationResultListener(ret)
			{
				public void customResultAvailable(Object result)
				{
					props.removeSubproperties(id);
					props.addSubproperties(id, (Properties)result);
					ret.setResult(null);
				}
			});
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
	public IFuture	setProperties(final String id, final Properties props, boolean save)
	{
		final Future	ret	= new Future();
		this.props.removeSubproperties(id);
		this.props.addSubproperties(id, props);
		
		IFuture	saved;
		if(save)
			saved	= saveProperties();
		else
			saved	= IFuture.DONE;
		
		saved.addResultListener(new DelegationResultListener(ret)
		{
			public void customResultAvailable(Object result)
			{
				if(providers.containsKey(id))
				{
					((IPropertiesProvider)providers.get(id)).setProperties(props)
						.addResultListener(new DelegationResultListener(ret));
				}
				else
				{
					ret.setResult(null);
				}
			}
		});
		return ret;
	}
	
	/**
	 *  Get the properties for a given id.
	 *  @param id 	A unique id to identify the properties (e.g. component or service name).
	 *  @return A future containing the properties (if any).
	 */
	public IFuture	getProperties(String id)
	{
		return new Future(props.getSubproperty(id));
	}
	
	
	/**
	 *  Load the default platform properties.
	 *  @return A future indicating when properties have been loaded.
	 */
	public IFuture	loadProperties()
	{
		try
		{
			// Todo: Which class loader to use? library service unavailable, because it depends on settings service?
			FileInputStream fis = new FileInputStream(file);
			props	= (Properties)PropertiesXMLHelper.getPropertyReader().read(fis, getClass().getClassLoader(), null);
			fis.close();
		}
		catch(Exception e)
		{
			props	= new Properties();
		}
		return IFuture.DONE;
	}
	
	/**
	 *  Save the platform properties to the default location.
	 *  @return A future indicating when properties have been saved.
	 */
	public IFuture	saveProperties()
	{
		try
		{
			// Todo: Which class loader to use? library service unavailable, because it depends on settings service?
			FileOutputStream os = new FileOutputStream(file);
			PropertiesXMLHelper.getPropertyWriter().write(props, os, getClass().getClassLoader(), null);
			os.close();
		}
		catch(Exception e)
		{
			System.out.println("Warning: Could not save settings: "+e);
		}
		
		return IFuture.DONE;
	}
}
