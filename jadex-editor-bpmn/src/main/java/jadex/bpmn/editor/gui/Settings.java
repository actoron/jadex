package jadex.bpmn.editor.gui;

import jadex.bpmn.editor.BpmnEditor;
import jadex.bpmn.model.task.ITask;
import jadex.bridge.ClassInfo;
import jadex.commons.IFilter;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.IIntermediateResultListener;
import jadex.commons.future.ISubscriptionIntermediateFuture;
import jadex.commons.future.ThreadSuspendable;
import jadex.commons.gui.future.SwingIntermediateResultListener;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Modifier;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Properties;
import java.util.Set;
import java.util.jar.JarEntry;

import com.mxgraph.view.mxStylesheet;

/**
 *  The editor settings.
 */
public class Settings
{
	
	/** The settings file name. */
	protected static final String SETTINGS_FILE_NAME = "settings.cfg";
	
	/** The class cache file name. */
	protected static final String CLASS_CACHE_FILE_NAME = "classes.cache";
	
	/** The last file opened or saved. */
	protected File lastfile;
	
	/** Files that were opened when editor was closed. */
	protected File[] openedfiles;
	
	/** The last file opened or saved. */
	protected int toolbariconsize = GuiConstants.DEFAULT_ICON_SIZE;
	
	/** Sequence edge enabled flag. */
	protected boolean sequenceedges = true;
	
	/** Data edge enabled flag. */
	protected boolean dataedges = true;
	
	/** Flag if simple name/type data edge auto-connect is enabled. */
	protected boolean nametypedataautoconnect = true;
	
	/** Flag if simple direct sequence edge auto-connect is enabled. */
	protected boolean directsequenceautoconnect = true;
	
	/** Flag if save settings on exit is enable. */
	protected boolean savesettingsonexit = true;
	
	/** Smooth zoom flag. */
	protected boolean smoothzoom = true;
	
	/** The selected style sheet */
	protected String selectedsheet = BpmnEditor.STYLE_SHEETS[0].getFirstEntity();
	
	/** The library home. */
	//protected File libraryhome;
	
	/** The library class loader entries. */
	protected File[] libentries;
	
	/** The library class loader. */
	protected ClassLoader libclassloader;
	
	/** Global task classes */
	protected List<ClassInfo> globaltaskclasses;

	/** Global interfaces */
	protected List<ClassInfo> globalinterfaces;
	
	/** Global exceptions. */
	protected List<ClassInfo> globalexceptions;
	
	/** Global allclasses */
	protected List<ClassInfo> globalallclasses;
	
	/**
	 *  Gets the selected style sheet.
	 *
	 *  @return The selected sheet.
	 */
	public String getSelectedSheet()
	{
		return selectedsheet;
	}

	/**
	 *  Sets the selected style sheet.
	 *
	 *  @param selectedsheet The selected sheet.
	 */
	public void setSelectedSheet(String selectedsheet)
	{
		this.selectedsheet = selectedsheet;
	}

	/**
	 *  Gets the opened files.
	 *
	 *  @return The opened files.
	 */
	public File[] getOpenedFiles()
	{
		return openedfiles;
	}

	/**
	 *  Sets the opened files.
	 *
	 *  @param openedfiles The opened files.
	 */
	public void setOpenedFiles(File[] openedfiles)
	{
		this.openedfiles = openedfiles;
	}
	
	/**
	 * 
	 * @return
	 */
	public int getToolbarIconSize()
	{
		return toolbariconsize;
	}

	/**
	 * @param toolbariconsize
	 */
	public void setToolbarIconSize(int toolbariconsize)
	{
		this.toolbariconsize = toolbariconsize;
	}
	
	
	/**
	 * 
	 */
	public boolean isSmoothZoom()
	{
		return smoothzoom;
	}
	
	/**
	 * 
	 */
	public void setSmoothZoom(boolean smoothzoom)
	{
		this.smoothzoom = smoothzoom;
	}
	
	/**
	 *  Gets the sequenceedges.
	 *
	 *  @return The sequenceedges.
	 */
	public boolean isSequenceEdges()
	{
		return sequenceedges;
	}

	/**
	 *  Sets the sequenceedges.
	 *
	 *  @param sequenceedges The sequenceedges.
	 */
	public void setSequenceEdges(boolean sequenceedges)
	{
		this.sequenceedges = sequenceedges;
	}

	/**
	 *  Gets the dataedges.
	 *
	 *  @return The dataedges.
	 */
	public boolean isDataEdges()
	{
		return dataedges;
	}

	/**
	 *  Sets the dataedges.
	 *
	 *  @param dataedges The dataedges.
	 */
	public void setDataEdges(boolean dataedges)
	{
		this.dataedges = dataedges;
	}

	/**
	 *  Gets the nametypedataautoconnect.
	 *
	 *  @return The nametypedataautoconnect.
	 */
	public boolean isNameTypeDataAutoConnect()
	{
		return nametypedataautoconnect;
	}

	/**
	 *  Sets the nametypedataautoconnect.
	 *
	 *  @param nametypedataautoconnect The nametypedataautoconnect.
	 */
	public void setNameTypeDataAutoConnect(boolean nametypedataautoconnect)
	{
		this.nametypedataautoconnect = nametypedataautoconnect;
	}
	
	/**
	 *  Gets the directsequenceautoconnect.
	 *
	 *  @return The directsequenceautoconnect.
	 */
	public boolean isDirectSequenceAutoConnect()
	{
		return directsequenceautoconnect;
	}

	/**
	 *  Sets the directsequenceautoconnect.
	 *
	 *  @param directsequenceautoconnect The directsequenceautoconnect.
	 */
	public void setDirectSequenceAutoConnect(boolean directsequenceautoconnect)
	{
		this.directsequenceautoconnect = directsequenceautoconnect;
	}

	/**
	 *  Gets the last file.
	 *
	 *  @return The last file.
	 */
	public File getLastFile()
	{
		return lastfile;
	}

	/**
	 *  Sets the last file.
	 *
	 *  @param lastfile The last file.
	 */
	public void setLastFile(File lastfile)
	{
		this.lastfile = lastfile;
	}
	
	
	
	/**
	 *  Gets the library home.
	 *
	 *  @return The library home.
	 */
//	public File getLibraryHome()
//	{
//		return libraryhome;
//	}

	/**
	 *  Sets the library home.
	 *
	 *  @param libraryhome The library home.
	 */
//	public void addLibraryHome(File libhome)
//	{
//		if (libhome != null && libhome.getPath().length() > 0)
//		{
////			this.libraryhome = libraryhome;
//			
//			File libdir = new File(libhome.getAbsolutePath() + File.separator + "lib");
//			if (!libdir.exists() || !libdir.isDirectory())
//			{
//				libdir = libhome;
//			}
//			
//			Set<File> entries = new HashSet<File>();
//			File[] files = libdir.listFiles();
////			List<URL> urls = new ArrayList<URL>();
//			if (files != null)
//			{
//				for (File file : files)
//				{
//					if (file.getAbsolutePath().endsWith(".jar"))
//					{
//						entries.add(file);
//						urls.add(file.toURI().toURL());
//					}
//				}
//			}
//			
//			if (entries.isEmpty())
//			{
//				// Attempt developer-mode search.
//				File[] dirs = libdir.listFiles();
//				for (File dir : dirs)
//				{
//					if (dir.isDirectory())
//					{
//						File targetdir = new File(dir.getAbsolutePath() + File.separator + "target" + File.separator + "classes");
//						if (targetdir.exists() && targetdir.isDirectory())
//						{
//							entries.add(targetdir);
//							urls.add(targetdir.toURI().toURL());
//						}
//					}
//				}
//			}
//			
//			if (libentries != null)
//			{
//				entries.addAll(Arrays.asList(libentries));
//			}
//			
//			setLibraryEntries(entries);
//		}
//		else
//		{
//			this.libraryhome = null;
//			homeclassloader = Settings.class.getClassLoader();
//		}
//	}
	
	/**
	 *  Sets the library entries.
	 *  @param entries The entries.
	 */
	public File[] getLibraryEntries()
	{
		return libentries;
	}
	
	/**
	 *  Scans for the global classes.
	 */
	public void scanForClasses()
	{
		Comparator<ClassInfo> comp = new Comparator<ClassInfo>()
		{
			public int compare(ClassInfo o1, ClassInfo o2)
			{
				String str1 = SReflect.getUnqualifiedTypeName(o1.toString());
				String str2 = SReflect.getUnqualifiedTypeName(o2.toString());
				return str1.compareTo(str2);
			}
		};
		Set<ClassInfo>[] tmp = Settings.scanForClasses(getLibraryClassLoader(), true);
		List<ClassInfo>[] stmp = new List[tmp.length];
		for (int i = 0; i < tmp.length; ++i)
		{
			stmp[i] = new ArrayList<ClassInfo>(tmp[i]);
			Collections.sort(stmp[i], comp);
		}
		setGlobalTaskClasses(stmp[0]);
		setGlobalInterfaces(stmp[1]);
		setGlobalInterfaces(stmp[2]);
		setGlobalAllClasses(stmp[3]);
	}
	
	/**
	 *  Sets the library entries.
	 *  @param entries The entries.
	 */
	public void setLibraryEntries(Collection<File> entries)
	{
		libentries = entries.toArray(new File[entries.size()]);
		
		URL[] urls = new URL[libentries.length];
		for (int i = 0; i < urls.length; ++i)
		{
			try
			{
				urls[i] = libentries[i].toURI().toURL();
			}
			catch (MalformedURLException e)
			{
			}
		}
		
		if (urls.length > 0)
		{
			libclassloader = new URLClassLoader(urls, Settings.class.getClassLoader());
		}
		else
		{
			libclassloader = Settings.class.getClassLoader();
		}
	}
	
	/**
	 *  Get the globaltaskclasses.
	 *  @return The globaltaskclasses.
	 */
	public List<ClassInfo> getGlobalTaskClasses()
	{
		return globaltaskclasses;
	}

	/**
	 *  Set the globaltaskclasses.
	 *  @param globaltaskclasses The globaltaskclasses to set.
	 */
	public void setGlobalTaskClasses(List<ClassInfo> globaltaskclasses)
	{
		this.globaltaskclasses = globaltaskclasses;
	}

	/**
	 *  Get the globalinterfaces.
	 *  @return The globalinterfaces.
	 */
	public List<ClassInfo> getGlobalInterfaces()
	{
		return globalinterfaces;
	}

	/**
	 *  Set the globalinterfaces.
	 *  @param globalinterfaces The globalinterfaces to set.
	 */
	public void setGlobalInterfaces(List<ClassInfo> globalinterfaces)
	{
		this.globalinterfaces = globalinterfaces;
	}
	
	/**
	 *  Gets the globalexceptions.
	 *
	 *  @return The globalexceptions.
	 */
	public List<ClassInfo> getGlobalExceptions()
	{
		return globalexceptions;
	}

	/**
	 *  Sets the globalexceptions.
	 *
	 *  @param globalexceptions The globalexceptions.
	 */
	public void setGlobalExceptions(List<ClassInfo> globalexceptions)
	{
		this.globalexceptions = globalexceptions;
	}

	/**
	 *  Get the allclasses.
	 *  @return The allclasses.
	 */
	public List<ClassInfo> getGlobalAllClasses()
	{
		return globalallclasses;
	}

	/**
	 *  Set all classes.
	 *  @param allclasses The classes to set.
	 */
	public void setGlobalAllClasses(List<ClassInfo> allclasses)
	{
		this.globalallclasses = allclasses;
	}
	
	/**
	 *  Gets the save settings on exit setting.
	 *
	 *  @return The save settings on exit setting.
	 */
	public boolean isSaveSettingsOnExit()
	{
		return savesettingsonexit;
	}

	/**
	 *  Sets the save settings on exit setting.
	 *
	 *  @param savesettingsonexit The save settings on exit setting.
	 */
	public void setSaveSettingsOnExit(boolean savesettingsonexit)
	{
		this.savesettingsonexit = savesettingsonexit;
	}
	
	/**
	 *  Gets the library class loader.
	 *
	 *  @return The library class loader.
	 */
	public ClassLoader getLibraryClassLoader()
	{
		return libclassloader;
	}

	/**
	 *  Save the settings.
	 */
	public void save() throws IOException
	{
		File settingsdir = new File(BpmnEditor.HOME_DIR);
		if (!settingsdir.exists())
		{
			if (!settingsdir.mkdir())
			{
				throw new IOException("Could not create settings directory: " + settingsdir.getAbsolutePath());
			}
		}
		
		File settingsfile = new File(settingsdir.getAbsolutePath() + File.separator + SETTINGS_FILE_NAME);
		File tmpfile = File.createTempFile(SETTINGS_FILE_NAME, ".cfg");
		
		Properties props = new Properties();
		
		if (lastfile != null)
		{
			props.put("lastfile", lastfile.getAbsolutePath());
		}
		
		if (selectedsheet != null)
		{
			props.put("stylesheet", selectedsheet);
		}
		
//		if (libraryhome != null)
//		{
//			props.put("homepath", libraryhome.getPath());
//		}
		
		props.put("smoothzoom", String.valueOf(smoothzoom));
		
		props.put("sequenceedges", String.valueOf(sequenceedges));
		
		props.put("directsequenceautoconnect", String.valueOf(directsequenceautoconnect));
		
		props.put("dataedges", String.valueOf(dataedges));
		
		props.put("nametypedataautoconnect", String.valueOf(nametypedataautoconnect));
		
		props.put("savesettingsonexit", String.valueOf(savesettingsonexit));
		
		props.put("toolbariconsize", String.valueOf(toolbariconsize));
		
		if (openedfiles != null)
		{
			int counter = 0;
			for (File file : openedfiles)
			{
				props.put("openfile" + ++counter, file.getAbsolutePath());
			}
		}
		
		if (libentries != null)
		{
			int counter = 0;
			for (File file : libentries)
			{
				props.put("libentry" + ++counter, file.getAbsolutePath());
			}
		}
		
		Properties ccprops = new Properties();
		if(globalinterfaces!=null && globalinterfaces.size()>0)
		{
			for(int i=0; i<globalinterfaces.size(); i++)
			{
				ClassInfo inter = globalinterfaces.get(i);
				ccprops.put("gi"+i, inter.getTypeName());
			}
		}
		
		if(globaltaskclasses!=null && globaltaskclasses.size()>0)
		{
			for(int i=0; i<globaltaskclasses.size(); i++)
			{
				ClassInfo gt = globaltaskclasses.get(i);
				ccprops.put("gt"+i, gt.getTypeName());
			}
		}
		
		if(globalexceptions!=null && globalexceptions.size()>0)
		{
			for(int i=0; i<globalexceptions.size(); i++)
			{
				ClassInfo gt = globalexceptions.get(i);
				ccprops.put("ge"+i, gt.getTypeName());
			}
		}
		
		if(globalallclasses!=null && globalallclasses.size()>0)
		{
			for(int i=0; i<globalallclasses.size(); i++)
			{
				ClassInfo gt = globalallclasses.get(i);
				ccprops.put("ac"+i, gt.getTypeName());
			}
		}
		
		OutputStream os = new FileOutputStream(tmpfile);
		props.store(os, "Jadex BPMN Editor Settings");
		os.close();
		
		SUtil.moveFile(tmpfile, settingsfile);
		
		tmpfile = File.createTempFile(CLASS_CACHE_FILE_NAME, ".cache");
		os = new FileOutputStream(tmpfile);
		ccprops.store(os, "Jadex BPMN Editor Class Cache");
		os.close();
		
		File classcachefile = new File(settingsdir.getAbsolutePath() + File.separator + CLASS_CACHE_FILE_NAME);
		SUtil.moveFile(tmpfile, classcachefile);
	}
	
	/**
	 *  Loads the settings.
	 *  
	 *  @return The settings.
	 */
	public static final Settings load()
	{
		Settings ret = new Settings();
		try
		{
			File settingsfile = new File(BpmnEditor.HOME_DIR + File.separator + SETTINGS_FILE_NAME);
			
			Properties props = new Properties();
			InputStream is = new FileInputStream(settingsfile);
			props.load(is);
			is.close();
			
			String prop = props.getProperty("lastfile");
			if (prop != null)
			{
				ret.setLastFile(new File(prop));
			}
			
			prop = props.getProperty("savesettingsonexit");
			if (prop != null)
			{
				ret.setSaveSettingsOnExit(Boolean.parseBoolean(prop));
			}
			
//			prop = props.getProperty("homepath");
//			if (prop != null)
//			{
//				ret.setLibraryHome(new File(prop));
//			}
			
			prop = props.getProperty("stylesheet");
			if (prop != null)
			{
				for (Tuple2<String, mxStylesheet> sheet : BpmnEditor.STYLE_SHEETS)
				{
					if (sheet.getFirstEntity().equals(prop))
					{
						ret.setSelectedSheet(prop);
						break;
					}
				}
			}
			
			prop = props.getProperty("smoothzoom");
			if (prop != null)
			{
				try
				{
					ret.setSmoothZoom(Boolean.parseBoolean(prop));
				}
				catch (Exception e)
				{
				}
			}
			
			prop = props.getProperty("sequenceedges");
			if (prop != null)
			{
				try
				{
					ret.setSequenceEdges(Boolean.parseBoolean(prop));
				}
				catch (Exception e)
				{
				}
			}
			
			prop = props.getProperty("directsequenceautoconnect");
			if (prop != null)
			{
				try
				{
					ret.setDirectSequenceAutoConnect(Boolean.parseBoolean(prop));
				}
				catch (Exception e)
				{
				}
			}
			
			prop = props.getProperty("dataedges");
			if (prop != null)
			{
				try
				{
					ret.setDataEdges(Boolean.parseBoolean(prop));
				}
				catch (Exception e)
				{
				}
			}
			
			prop = props.getProperty("nametypedataautoconnect");
			if (prop != null)
			{
				try
				{
					ret.setNameTypeDataAutoConnect(Boolean.parseBoolean(prop));
				}
				catch (Exception e)
				{
				}
			}
			
			prop = props.getProperty("toolbariconsize");
			if (prop != null)
			{
				try
				{
					int size = Integer.parseInt(prop);
					for (int i = 0; i < GuiConstants.ICON_SIZES.length; ++i)
					{
						if (GuiConstants.ICON_SIZES[i] == size)
						{
							ret.setToolbarIconSize(size);
							break;
						}
					}
				}
				catch (NumberFormatException e)
				{
				}
			}
			
			List<File> openfiles = new ArrayList<File>();
			for(Object okey: props.keySet())
			{
				if(okey instanceof String)
				{
					String key = (String) okey;
					if (key.startsWith("openfile"))
					{
						openfiles.add(new File(props.getProperty(key)));
					}
				}
			}
			ret.setOpenedFiles(openfiles.toArray(new File[openfiles.size()]));
			
			List<File> lentries = new ArrayList<File>();
			for(Object okey: props.keySet())
			{
				if(okey instanceof String)
				{
					String key = (String) okey;
					if (key.startsWith("libentry"))
					{
						lentries.add(new File(props.getProperty(key)));
					}
				}
			}
			ret.setLibraryEntries(lentries);
			
			File classcachefile = new File(BpmnEditor.HOME_DIR + File.separator + CLASS_CACHE_FILE_NAME);
			
			props = new Properties();
			is = new FileInputStream(classcachefile);
			props.load(is);
			is.close();
			
			List<ClassInfo> gis = new ArrayList<ClassInfo>();
			List<ClassInfo> gts = new ArrayList<ClassInfo>();
			List<ClassInfo> ges = new ArrayList<ClassInfo>();
			List<ClassInfo> ac = new ArrayList<ClassInfo>();
			for(Object okey: props.keySet())
			{
				if(okey instanceof String)
				{
					String key = (String) okey;
					if(key.startsWith("gi"))
					{
						gis.add(new ClassInfo(props.getProperty(key)));
					}
					else if(key.startsWith("gt"))
					{
						gts.add(new ClassInfo(props.getProperty(key)));
					}
					else if(key.startsWith("ge"))
					{
						ges.add(new ClassInfo(props.getProperty(key)));
					}
					else if(key.startsWith("ac"))
					{
						ac.add(new ClassInfo(props.getProperty(key)));
					}
				}
			}
			ret.setGlobalInterfaces(gis);
			ret.setGlobalTaskClasses(gts);
			ret.setGlobalExceptions(ges);
			ret.setGlobalAllClasses(ac);
		}
		catch (IOException e)
		{
		}
		
		return ret;
	}
	
	/**
	 *  Scan for task classes.
	 */
	public static final Set<ClassInfo>[] scanForClasses(ClassLoader cl, final boolean includeboot)
	{
		final Set<ClassInfo> res1 = new HashSet<ClassInfo>();
		final Set<ClassInfo> res2 = new HashSet<ClassInfo>();
		final Set<ClassInfo> res3 = new HashSet<ClassInfo>();
		final Set<ClassInfo> res4 = new HashSet<ClassInfo>();
		
		scanForClasses(cl, new FileFilter("$", false), new IFilter<Class<?>>()
		{
			public boolean filter(final Class<?> obj)
			{
				boolean ret = false;
				try
				{
					if(!obj.isInterface())
					{
						if (SReflect.isSupertype(Exception.class, obj))
						{
							ClassInfo ci = new ClassInfo(obj.getName());
							res3.add(ci);
						}
						
						if(!Modifier.isAbstract(obj.getModifiers()) && Modifier.isPublic(obj.getModifiers()))
						{
							ClassInfo ci = new ClassInfo(obj.getName());
							res4.add(ci);
							if(!res1.contains(ci))
							{
								ClassLoader cl = obj.getClassLoader();
								Class<?> taskcl = Class.forName(ITask.class.getName(), includeboot, cl);
								ret = SReflect.isSupertype(taskcl, obj);
								if(ret)
								{
									res1.add(ci);
								}
							}
						}
					}
					else
					{
						// collect interfaces
						ClassInfo ci = new ClassInfo(obj.getName());
						res2.add(ci);
						res4.add(ci);
					}
				}
				catch(Exception e)
				{
				}
				return ret;
			}
		});
		
		return new Set[]{res1, res2, res3, res4};
	}
	
	/**
	 *  Scan for task classes.
	 */
	protected static final List<ClassInfo> scanForClasses(ClassLoader cl, IFilter<Object> filefilter, IFilter<Class<?>> classfilter)
	{
		final List<ClassInfo> taskclasses = new ArrayList<ClassInfo>();
		
		ISubscriptionIntermediateFuture<Class<?>> fut = SReflect.asyncScanForClasses(cl, filefilter, classfilter, -1, true);
		fut.addResultListener(new SwingIntermediateResultListener<Class<?>>(new IIntermediateResultListener<Class<?>>()
		{
			public void intermediateResultAvailable(Class<?> result)
			{
//				System.out.println("Found: "+result.getName());
				taskclasses.add(new ClassInfo(result));
			}
			public void finished()
			{
			}
			public void resultAvailable(Collection<Class<?>> result)
			{
			}
			public void exceptionOccurred(Exception exception)
			{
			}
		}));
		fut.get(new ThreadSuspendable());
		
		return taskclasses;
	}
	
	/**
	 * 
	 */
	private static class FileFilter implements IFilter<Object>
	{
		/** The filename. */
		protected String filename;
		
		/** The contains flag. */
		protected boolean contains;
		
		/**
		 * 
		 */
		public FileFilter(String filename, boolean contains)
		{
			this.filename = filename;
			this.contains = contains;
		}
		
		/**
		 * 
		 */
		public boolean filter(Object obj)
		{
			if(filename==null)
				return true;
			
			String	fn	= "";
			if(obj instanceof File)
			{
				File	f	= (File)obj;
				fn	= f.getName();
			}
			else if(obj instanceof JarEntry)
			{
				JarEntry	je	= (JarEntry)obj;
				fn	= je.getName();
			}
			
			return fn.endsWith(".class") && (contains? fn.indexOf(filename)!=-1: fn.indexOf(filename)==-1);
		}
	}
}
