package jadex.platform.service.settings;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;

import jadex.bridge.IComponentIdentifier;
import jadex.bridge.service.types.settings.IPlatformSettings;
import jadex.commons.SUtil;
import jadex.commons.collection.IAutoLock;
import jadex.commons.collection.RwAutoLock;
import jadex.commons.future.Future;
import jadex.commons.transformation.traverser.ITraverseProcessor;
import jadex.transformation.jsonserializer.JsonTraverser;

public class PlatformSettings implements IPlatformSettings
{
	/** The settings dir. */
	protected File settingsdir;
	
	/** Read-only flag. */
	protected boolean readonly = false;
	
	/** Locking mechanism. */
	protected RwAutoLock lock = new RwAutoLock();
	
	/**
	 *  Creates the settings manager.
	 */
	public PlatformSettings(IComponentIdentifier id, boolean readonly)
	{
		this.readonly = readonly;
		settingsdir = getSettingsDir(id);
		if (settingsdir.exists() && !settingsdir.isDirectory())
		{
			//access.getLogger().log(Level.WARNING, "Invalid settings directory '" + settingsdir.getName() + "', switching to read-only.");
			this.readonly = true;
		}
		else if (!settingsdir.exists() && !readonly)
		{
			readonly = readonly || !settingsdir.mkdir();
		}
	}

	/**
	 *  Get the settings directory for a platform.
	 */
	public static File getSettingsDir(IComponentIdentifier id)
	{
		return new File(SUtil.getAppDir(), "settings_" + id.getRoot().getPlatformPrefix());
	}
	
	/**
	 *  Saves arbitrary state to a persistent directory as JSON.
	 *  Object must be serializable and the ID must be unique.
	 *  
	 *  @param id Unique ID for the saved state.
	 *  @param state The state being saved.
	 *  @return Null, when done.
	 */
	public void saveState(String id, Object state)
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
				try (IAutoLock l = lock.writeLock())
				{
					SUtil.moveFile(tmpfile, file);
				}
			}
			catch(Exception e)
			{
				System.err.println("Warning: Could not save state " + id + ": " + e);
				e.printStackTrace();
			}
			finally
			{
				if (os != null)
					SUtil.close(os);
			}
		}
	}
	
	/**
	 *  Loads arbitrary state form a persistent directory.
	 *  
	 *  @param id Unique ID for the saved state.
	 *  @return The state or null if none was found or corrupt.
	 */
	public Object loadState(String id)
	{
		Object ret = null;
		File file = new File(settingsdir, id + ".json");
		
		try (IAutoLock l = lock.readLock())
		{
			String json = new String(SUtil.readFile(file), SUtil.UTF8);
			ret = fromJson(json);
		}
		catch (Exception e)
		{
			throw SUtil.throwUnchecked(e);
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
	public void saveFile(String filename, byte[] content)
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
			try (IAutoLock l = lock.writeLock())
			{
				SUtil.moveFile(tmpfile, file);
			}
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
	}
	
	/**
	 *  Directly loads a file from the settings directory.
	 *  
	 *  @param filename Name of the file.
	 *  @return Content of the file or null if not found.
	 */
	public byte[] loadFile(String filename)
	{
		byte[] ret = null;
		File file = (new File(settingsdir, filename)).getAbsoluteFile();
		if (file.exists())
		{
			try (IAutoLock l = lock.readLock())
			{
				ret = SUtil.readFile(file);
			}
			catch (Exception e)
			{
			}
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
									 PlatformSettings.class.getClassLoader(),
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
		Object ret = JsonTraverser.objectFromString(json, PlatformSettings.class.getClassLoader(), null, null, rprocs);
		return ret;
	}
}
