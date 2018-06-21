package jadex.commons;

import java.io.File;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.List;

/**
 *  Watcher for files, written to be backwards compatible.
 * 
 *  TODO: Implement polling.
 *
 */
public class FileWatcher
{
	/** Flag if running. */
	protected boolean running = true;
	
	/** The watch thread. */
	protected Thread thread;
	
	/** The watched file. */
	protected File watchedfile;
	
	/**
	 *  Creates the watcher.
	 * 
	 *  @param filepath File to watch.
	 *  @param run Callback on changes.
	 */
	public FileWatcher(String filepath, final Runnable run, boolean forcepoll)
	{
		File file = new File(filepath);
		if (!file.isDirectory())
		{
			file = new File(file.getAbsolutePath());
			watchedfile = file;
			file = new File(file.getParent());
		}
		
		if (!forcepoll)
		{
			try
			{
				ClassLoader cl = FileWatcher.class.getClassLoader();
				// Directory modification notification using WatchService, reflection used for
				// Java 6 compatibility.
				// Create the path object.
				Class<?> pathclazz = Class.forName("java.nio.file.Path", true, cl);
				Method topathmethod = File.class.getMethod("toPath", (Class<?>[]) null);
				final Method tofilemethod = pathclazz.getMethod("toFile", (Class<?>[]) null);
				Object path = topathmethod.invoke(file, (Object[]) null);
				
				// Get the default FileSystem using the factory.
				Class<?> fssclazz = Class.forName("java.nio.file.FileSystems", true, cl);
				Method getdefaultmethod = fssclazz.getMethod("getDefault", (Class<?>[]) null);
				Class<?> fsclazz = Class.forName("java.nio.file.FileSystem", true, cl);
				Object fs = getdefaultmethod.invoke(null, (Object[]) null);
				
				// Create new WatchService.
				final Class<?> wsclazz = Class.forName("java.nio.file.WatchService", true, cl);
				Method newwsmethod = fsclazz.getMethod("newWatchService", (Class<?>[]) null);
				final Object watchservice = newwsmethod.invoke(fs, (Object[]) null);
				
				// Get ENTRY_CREATE event type.
				Class<?> wekindsclazz = Class.forName("java.nio.file.WatchEvent$Kind", true, cl);
				Class<?> standardwatcheventkindsclazz = Class.forName("java.nio.file.StandardWatchEventKinds", true, cl);
				Object entrycreate = standardwatcheventkindsclazz.getField("ENTRY_CREATE").get(null);
				Object entrymodify = standardwatcheventkindsclazz.getField("ENTRY_MODIFY").get(null);
				final Object entryoverflow = standardwatcheventkindsclazz.getField("OVERFLOW").get(null);
				
				// Register WatchService on path.
				Object kindsarray = Array.newInstance(wekindsclazz, 2);
				Array.set(kindsarray, 0, entrycreate);
				Array.set(kindsarray, 1, entrymodify);
				Method registermethod = pathclazz.getMethod("register", new Class<?>[] { wsclazz, kindsarray.getClass() });
				registermethod.invoke(path, new Object[]{watchservice, kindsarray});
				
				thread = new Thread(new Runnable()
				{
					public void run()
					{
						try
						{
							while(running)
							{
								Class<?> wkclazz = Class.forName("java.nio.file.WatchKey");
								Class<?> weclazz = Class.forName("java.nio.file.WatchEvent");
								Method polleventsmethod = wkclazz.getMethod("pollEvents", (Class<?>[]) null);
								Method resetmethod = wkclazz.getMethod("reset", (Class<?>[]) null);
								Method takemethod = wsclazz.getMethod("take", (Class<?>[])null);
								
								Object val = takemethod.invoke(watchservice, (Object[])null);
								if (val!=null)
								{
									@SuppressWarnings("unchecked")
									List<Object> events = (List<Object>) polleventsmethod.invoke(val, (Object[]) null);
									for (Object ev : events)
									{ 
										Method kind = weclazz.getMethod("kind", (Class[]) null);
										Method context = weclazz.getMethod("context", (Class[]) null);
										if (kind.invoke(ev, (Object[]) null) == entryoverflow)
											continue;
										File file = (File) tofilemethod.invoke(context.invoke(ev, (Object[]) null), (Object[]) null);
										if (watchedfile == null || watchedfile.getCanonicalFile().equals(file.getCanonicalFile()))
										{
											try
											{
												run.run();
											}
											catch (Exception e)
											{
												return;
											}
										}
									}
									
									resetmethod.invoke(val, (Object[]) null);
								}
							}
						}
						catch (Exception e)
						{
							e.printStackTrace();
						}
					}
				});
				thread.setDaemon(true);
				thread.start();
			}
			catch (Exception e)
			{
				pollingMode(run);
			}
		}
		else
		{
			pollingMode(run);
		}
	}
	
	/**
	 *  Stops the monitoring.
	 */
	public void stop()
	{
		running = false;
		if (thread != null)
			thread.interrupt();
		thread = null;
	}
	
	/**
	 *  Use polling mode.
	 *  @param run The user runnable.
	 */
	protected void pollingMode(final Runnable run)
	{
		// TODO Dir support.
		thread = new Thread(new Runnable()
		{
			public void run()
			{
				boolean exists = watchedfile.exists(); 
				long lastmod = Long.MIN_VALUE;
				if (exists)
					lastmod = watchedfile.lastModified();
				
				while (running)
				{
					SUtil.sleep(1000);
					if (!exists)
					{
						if (watchedfile.exists())
						{
							exists = true;
							lastmod = watchedfile.lastModified();
						}
					}
					else
					{
						if (!watchedfile.exists() || lastmod != watchedfile.lastModified())
						{
							exists = watchedfile.exists();
							if (exists)
								lastmod = watchedfile.lastModified();
							run.run();
						}
					}
				}
			}
		});
		thread.setDaemon(true);
		thread.start();
	}
	
	/**
	 *  Override
	 */
	protected void finalize() throws Throwable
	{
		stop();
	}
}
