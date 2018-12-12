package jadex.platform.service.settings;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.bridge.service.annotation.Service;
import jadex.bridge.service.annotation.ServiceComponent;
import jadex.bridge.service.annotation.ServiceShutdown;
import jadex.bridge.service.annotation.ServiceStart;
import jadex.bridge.service.types.settings.ISettingsService;
import jadex.commons.Boolean3;
import jadex.commons.IPropertiesProvider;
import jadex.commons.Properties;
import jadex.commons.SUtil;
import jadex.commons.future.CounterResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.ExceptionDelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentArgument;
import jadex.transformation.jsonserializer.JsonTraverser;

/**
 *  Agent that provides the settings service.
 */
@Service
@Agent(autoprovide=Boolean3.TRUE,
	autostart=Boolean3.TRUE,
	predecessors="jadex.platform.service.context.ContextAgent")
public class SettingsAgent	implements ISettingsService
{
	// -------- constants --------

	/** The filename extension for settings. */
//	public static final String	SETTINGS_EXTENSION = ".settings.xml";

	//-------- attributes --------
	
	/** The service provider. */
	@ServiceComponent
	protected IInternalAccess access;
	
	/** Directory used to save settings. */
	protected File settingsdir;
	
	/** The properties filename. */
	protected String filename;
	
	/** The current properties. */
	protected Properties	props;
	
	/** The registered properties provider (id->provider). */
	protected Map<String, IPropertiesProvider>	providers;
	
	/** Save settings on exit?. */
	@AgentArgument
	protected boolean	saveonexit;
	
	/** Do not save settings?. */
	@AgentArgument
	protected boolean	readonly;
	
	/** The context service. */
	//protected IContextService contextService;
	
	//-------- Service methods --------
	
	/**
	 *  Start the service.
	 *  @return A future that is done when the service has completed starting.  
	 */
	@ServiceStart
	public IFuture<Void>	startService()
	{
		this.providers	= new LinkedHashMap<String, IPropertiesProvider>();
		this.filename	= "properties.json";
		settingsdir = new File(SUtil.getAppDir(), "settings_" + access.getId().getPlatformPrefix());
		if (settingsdir.exists() && !settingsdir.isDirectory())
		{
			access.getLogger().log(Level.WARNING, "Invalid settings directory '" + settingsdir.getName() + "', switching to read-only.");
			readonly = true;
		}
		else if (!settingsdir.exists() && !readonly)
			settingsdir.mkdir();
		//this.filename	= access.getComponentIdentifier().getPlatformPrefix() + SETTINGS_EXTENSION;
		
		final Future<Void>	ret	= new Future<Void>();
		//contextService = access.getComponentFeature(IRequiredServicesFeature.class).searchLocalService(new ServiceQuery<>( IContextService.class, ServiceScope.PLATFORM));
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
				provider.setProperties(sub).addResultListener(access.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)));
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
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
				provider.getProperties().addResultListener(access.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)
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
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public IFuture<Void>	setProperties(String id, Properties props)
	{
//		System.out.println("Set properties: "+id);
		final Future<Void>	ret	= new Future<Void>();
		this.props.removeSubproperties(id);
		this.props.addSubproperties(id, props);
		
		if(providers.containsKey(id))
		{
			((IPropertiesProvider)providers.get(id)).setProperties(props)
				.addResultListener(access.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener(ret)));
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
					access.getFeature(IExecutionFeature.class).createResultListener(new DelegationResultListener<Void>(ret)));
				
				for(Iterator<String> it=providers.keySet().iterator(); it.hasNext(); )
				{
					final String	id	= it.next();
					IPropertiesProvider	provider = providers.get(id);
					
					Properties	sub	= props.getSubproperty(id);
					if(sub!=null)
					{
						provider.setProperties(sub).addResultListener(access.getFeature(IExecutionFeature.class).createResultListener(crl));
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
		
		File file = new File(settingsdir, filename);
		
		try
		{
			String json = new String(SUtil.readFile(file), SUtil.UTF8);
			ArrayList<ITraverseProcessor> rprocs = new ArrayList<ITraverseProcessor>(JsonTraverser.readprocs.size() + 2);
			rprocs.addAll(JsonTraverser.readprocs);
			rprocs.add(rprocs.size() - 2, new JsonPropertiesProcessor());
			rprocs.add(rprocs.size() - 2, new JsonPropertyProcessor());
			Properties props = JsonTraverser.objectFromString(json, getClass().getClassLoader(), null, Properties.class, rprocs);
			if (props == null)
				throw new RuntimeException("Cannot load properties from file: " + file.getAbsolutePath());
			else
				ret.setResult(props);
		}
		catch (Exception e)
		{
			ret.setException(e);
		}
		
		return ret;
	}

	/**
	 *  Save the platform properties to the default location.
	 *  @return A future indicating when properties have been saved.
	 */
	public IFuture<Void> saveProperties()
	{
		return saveProperties(false);
	}
	
	/**
	 *  Save the platform properties to the default location.
	 *  @param shutdown	Flag indicating if called during shutdown.
	 *  @return A future indicating when properties have been saved.
	 */
	public IFuture<Void> saveProperties(boolean shutdown)
	{
		if(readonly)
			return IFuture.DONE;
		
//		System.out.println("Save properties"+(shutdown?" (shutdown)":""));
		final Future<Void>	ret	= new Future<Void>();
		
		IResultListener<Void>	rl	= new DelegationResultListener<Void>(ret)
		{
			public void customResultAvailable(Void result)
			{
				writePropertiesToStore(props)
					.addResultListener(new DelegationResultListener<Void>(ret));
			}
		};
		rl	= shutdown ? rl : access.getFeature(IExecutionFeature.class).createResultListener(rl); 
		final CounterResultListener<Void>	crl	= new CounterResultListener<Void>(providers.size(), rl);
		
		for(Iterator<String> it=providers.keySet().iterator(); it.hasNext(); )
		{
			final String	id	= it.next();
			IPropertiesProvider	provider	= providers.get(id);
			IResultListener<Properties>	rlp	= new ExceptionDelegationResultListener<Properties, Void>(ret)
			{
				public void customResultAvailable(Properties result)
				{
					props.removeSubproperties(id);
					props.addSubproperties(id, (Properties)result);
					crl.resultAvailable(null);
				}
			};
			rl	= shutdown ? rl : access.getFeature(IExecutionFeature.class).createResultListener(rl); 
			provider.getProperties().addResultListener(rlp);
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
	protected IFuture<Void> writePropertiesToStore(final Properties props) //throws FileNotFoundException, Exception, IOException 
	{
		//final Future<Void>	ret	= new Future<Void>();
		// Todo: Which class loader to use? library service unavailable, because
		// it depends on settings service?
		
		if (!readonly)
		{
			FileOutputStream os = null;
			
			File file = (new File(settingsdir, filename)).getAbsoluteFile();
			
			try
			{
				ArrayList<ITraverseProcessor> procs = new ArrayList<ITraverseProcessor>(JsonTraverser.writeprocs.size() + 2);
				procs.addAll(JsonTraverser.writeprocs);
				procs.add(procs.size() - 1, new JsonPropertiesProcessor());
				procs.add(procs.size() - 1, new JsonPropertyProcessor());
				String json = JsonTraverser.objectToString(props,
											 getClass().getClassLoader(),
											 false, false,
											 null, null,
											 procs);
				json = JsonTraverser.prettifyJson(json);
				
				File tmpfile = File.createTempFile(file.getName(), "");
				os = new FileOutputStream(tmpfile);
				os.write(json.getBytes(SUtil.UTF8));
				SUtil.close(os);
				SUtil.moveFile(tmpfile, file);
			}
			catch(Exception e)
			{
				System.out.println("Warning: Could not save settings: "+e);
			}
			finally
			{
				if (os != null)
					SUtil.close(os);
			}
		}
		
		return IFuture.DONE;
	}
	
	/**
	 * Returns the File object for a path to a file.
	 * @param path Path to the file
	 * @return The File Object for the given path.
	 */
//	protected IFuture<File> getFile(String path) 
//	{
//		return contextService.getFile(path);
//	}
	
	// -------------------------------- New API -------------------------------
	
	/**
	 *  Saves arbitrary state to a persistent directory as JSON.
	 *  Object must be serializable and the ID must be unique.
	 *  
	 *  @param id Unique ID for the saved state.
	 *  @param state The state being saved.
	 *  @return Null, when done.
	 */
	public IFuture<Void> saveState(String id, Object state)
	{
		if (!readonly)
		{
			OutputStream os = null;
			
			File file = (new File(settingsdir, id + ".json")).getAbsoluteFile();
			File tmpfile = null;
			
			try
			{
				tmpfile = File.createTempFile(file.getName(), ".tmp");
				
				String json = toJson(state);
				
				os = new FileOutputStream(tmpfile);
				os.write(json.getBytes(SUtil.UTF8));
				SUtil.close(os);
				SUtil.moveFile(tmpfile, file);
			}
			catch(Exception e)
			{
				System.out.println("Warning: Could not save state " + id + ": " + e);
				e.printStackTrace();
			}
			finally
			{
				if (os != null)
					SUtil.close(os);
			}
		}
		
		return IFuture.DONE;
	}
	
	/**
	 *  Loads arbitrary state form a persistent directory.
	 *  
	 *  @param id Unique ID for the saved state.
	 *  @return The state or null if none was found or corrupt.
	 */
	public IFuture<Object> loadState(String id)
	{
		Future<Object> ret = new Future<Object>();
		
		File file = new File(settingsdir, id + ".json");
		
		try
		{
			String json = new String(SUtil.readFile(file), SUtil.UTF8);
			Object state = fromJson(json);
			ret.setResult(state);
		}
		catch (Exception e)
		{
			ret.setResultIfUndone(null);
		}
		
		return ret;
	}
	
	/**
	 *  Directly saves a file in the settings directory.
	 *  
	 *  @param filename Name of the file.
	 *  @param content The file content.
	 *  @return Null, when done.
	 */
	public IFuture<Void> saveFile(String filename, byte[] content)
	{
		Future<Void> ret = new Future<>();
		File file = (new File(settingsdir, filename)).getAbsoluteFile();
		OutputStream os = null;
		try
		{
			File tmpfile = File.createTempFile(filename, ".tmp");
			os =  new FileOutputStream(tmpfile);
			os.write(content);
			SUtil.close(os);
			SUtil.moveFile(tmpfile, file);
			ret.setResult(null);
		}
		catch (Exception e)
		{
			ret.setException(e);
		}
		finally
		{
			SUtil.close(os);
		}
		return ret;
	}
	
	/**
	 *  Directly loads a file from the settings directory.
	 *  
	 *  @param filename Name of the file.
	 *  @return Content of the file or null if not found.
	 */
	public IFuture<byte[]> loadFile(String filename)
	{
		Future<byte[]> ret = new Future<>();
		File file = (new File(settingsdir, filename)).getAbsoluteFile();
		if (file.exists())
		{
			try
			{
				byte[] content = SUtil.readFile(file);
				ret.setResult(content);
			}
			catch (Exception e)
			{
				ret.setException(e);
			}
		}
		else
		{
			ret.setResult(null);
		}
		return ret;
	}
	
	/**
	 *  Converts object to JSON.
	 * 
	 *  @param object Object.
	 *  @return JSON string.
	 */
	public static final String toJson(Object object)
	{
		ArrayList<ITraverseProcessor> procs = new ArrayList<ITraverseProcessor>(JsonTraverser.writeprocs.size() + 1);
		procs.addAll(JsonTraverser.writeprocs);
		procs.add(procs.size() - 1, new JsonAuthenticationSecretProcessor());
		String json = JsonTraverser.objectToString(object,
									 SettingsAgent.class.getClassLoader(),
									 true, false,
									 null, null,
									 procs);
		json = JsonTraverser.prettifyJson(json);
		return json;
	}
	
	/**
	 *  Converts JSON to object.
	 * 
	 *  @param json JSON.
	 *  @return Object.
	 */
	public static final Object fromJson(String json)
	{
		ArrayList<ITraverseProcessor> rprocs = new ArrayList<ITraverseProcessor>(JsonTraverser.readprocs.size() + 1);
		rprocs.addAll(JsonTraverser.readprocs);
		rprocs.add(rprocs.size() - 2, new JsonAuthenticationSecretProcessor());
		Object ret = JsonTraverser.objectFromString(json, SettingsAgent.class.getClassLoader(), null, null, rprocs);
		return ret;
	}
}