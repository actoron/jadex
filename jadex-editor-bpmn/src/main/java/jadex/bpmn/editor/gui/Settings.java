package jadex.bpmn.editor.gui;

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
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.jar.JarEntry;

import javax.swing.SwingUtilities;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Opcodes;

import com.mxgraph.view.mxStylesheet;

import jadex.bpmn.editor.BpmnEditor;
import jadex.bpmn.editor.gui.propertypanels.PropertyPanelFactory;
import jadex.bpmn.model.task.ITask;
import jadex.bpmn.model.task.annotation.Task;
import jadex.bridge.ClassInfo;
import jadex.commons.IFilter;
import jadex.commons.SReflect;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;
import jadex.commons.future.IResultListener;

/**
 *  The editor settings.
 */
public class Settings
{
	
	/** The settings file name. */
	protected static final String SETTINGS_FILE_NAME = "settings.cfg";
	
	/** The class cache file name. */
	protected static final String CLASS_CACHE_FILE_NAME = "classes.cache";
	
	/** The property panel factory */
	protected PropertyPanelFactory propertypanelfactory;
	
	/** The progress bar for background tasks. */
	protected BackgroundProgressBar bgprogressbar;
	
	/** The image provider. */
	protected ImageProvider imageprovider = new ImageProvider();
	
	/** The last file opened or saved. */
	protected File lastfile;
	
	/** Files that were opened when editor was closed. */
	protected File[] openedfiles;
	
	/** The last file opened or saved. */
	protected int toolbariconsize = GuiConstants.DEFAULT_ICON_SIZE;
	
	/** The name of the look and feel */
	protected String lfname = "Metal";
	
	/** Sequence edge enabled flag. */
	protected boolean sequenceedges = true;
	
	/** Data edge enabled flag. */
	protected boolean dataedges = true;
	
	/** Flag if simple name/type data edge auto-connect is enabled. */
	protected boolean nametypedataautoconnect = true;
	
	/** Flag if simple direct sequence edge auto-connect is enabled. */
	protected boolean directsequenceautoconnect = true;
	
	/** Flag if save settings on exit is enabled. */
	protected boolean savesettingsonexit = true;
	
	/** Flag if Jadex extensions are enabled. */
	protected boolean jadexextensions = true;
	
	/** Smooth zoom flag. */
	protected boolean smoothzoom = true;
	
	/** The selected style sheet */
	protected String selectedsheet = BpmnEditor.STYLE_SHEETS.get(0).getFirstEntity();
	
	/** The library home. */
	//protected File libraryhome;
	
	/** The library class loader entries. */
	protected File[] libentries;
	
	/** The library class loader. */
	protected ClassLoader libclassloader;
	
	/** Global task classes */
	protected List<ClassInfo> globaltaskclasses = new ArrayList<ClassInfo>();

	/** Global interfaces */
	protected List<ClassInfo> globalinterfaces = new ArrayList<ClassInfo>();
	
	/** Global exceptions. */
	protected List<ClassInfo> globalexceptions = new ArrayList<ClassInfo>();
	
	/** Global allclasses */
	protected List<ClassInfo> globalallclasses = new ArrayList<ClassInfo>();
	
	/** Flag if a scan is in progress */
	protected boolean scanning;
	
	/**
	 *  Gets the progress bar.
	 *
	 *  @return The progress bar.
	 */
	public BackgroundProgressBar getProgressBar()
	{
		return bgprogressbar;
	}

	/**
	 *  Sets the progress bar.
	 *
	 *  @param bgprogressbar The progress bar.
	 */
	public void setProgressBar(BackgroundProgressBar progressbar)
	{
		this.bgprogressbar = progressbar;
	}
	
	/**
	 *  Returns the image provider.
	 *  
	 * 	@return The image provider.
	 */
	public ImageProvider getImageProvider()
	{
		return imageprovider;
	}
	
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
	 *  Gets the look and feel name.
	 *
	 *  @return The name.
	 */
	public String getLfName()
	{
		return lfname;
	}

	/**
	 *  Sets the look and feel name.
	 *
	 *  @param lfname The name.
	 */
	public void setLfName(String lfname)
	{
		this.lfname = lfname;
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
	 * 
	 */
	public boolean isJadexExtensions()
	{
		return jadexextensions;
	}
	
	/**
	 * 
	 */
	public void setJadexExtensions(boolean jadexextensions)
	{
		this.jadexextensions = jadexextensions;
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
	public IFuture<Void> scanForClasses()
	{
//		Set<ClassInfo>[] tmp = Settings.scanForClasses(this, getLibraryClassLoader(), true);
		
		if (scanning)
		{
			return IFuture.DONE;
		}
		
		scanning = true;
		
		final Set<ClassInfo>[] stmp = new Set[4];
		for (int i = 0; i < 4; ++i)
		{
			stmp[i] = new HashSet<ClassInfo>();
		}
		IFuture<Void> ret = Settings.scanForClasses(this, getLibraryClassLoader(), new FileFilter("$", false), new BpmnClassFilter(stmp, true), true);
		
		ret.addResultListener(new IResultListener<Void>()
		{
			public void resultAvailable(Void result)
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
				final List<ClassInfo>[] ltmp = new List[4];
				for(int i = 0; i < 4; ++i)
				{
					ltmp[i] = new ArrayList<ClassInfo>(stmp[i]);
					Collections.sort(ltmp[i], comp);
				}
//				System.out.println("found2: "+stmp[0]+" "+stmp[0].getClass());
				setGlobalTaskClasses(ltmp[0]);
				setGlobalInterfaces(ltmp[1]);
				setGlobalExceptions(ltmp[2]);
				setGlobalAllClasses(ltmp[3]);
				
				try
				{
					save();
				}
				catch (IOException e)
				{
				}
				scanning = false;
			}
			
			public void exceptionOccurred(Exception exception)
			{
				scanning = false;
			}
		});
		
		return ret;
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
//		System.out.println("task cls: "+globaltaskclasses);
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
	 *  Gets the property panel factory.
	 *  
	 *  @return The property panel factory.
	 */
	public PropertyPanelFactory getPropertyPanelFactory()
	{
		return propertypanelfactory;
	}
	
	/**
	 *  Sets the property panel factory.
	 * 
	 * @param propertypanelfactory The property panel factory.
	 */
	public void setPropertyPanelFactory(PropertyPanelFactory propertypanelfactory)
	{
		this.propertypanelfactory = propertypanelfactory;
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
		
		props.put("lfname", lfname);
		
		props.put("smoothzoom", String.valueOf(smoothzoom));
		
		props.put("jadexextensions", String.valueOf(jadexextensions));
		
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
		
		ccprops.put("build", String.valueOf(BpmnEditor.BUILD));
		
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
			
			prop = props.getProperty("lfname");
			if (prop != null)
			{
				ret.setLfName(prop);
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
			
			prop = props.getProperty("jadexextensions");
			if (prop != null)
			{
				try
				{
					ret.setJadexExtensions(Boolean.parseBoolean(prop));
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
			
			Map<Integer, File> openfiles = new TreeMap<Integer, File>();
			for(Object okey: props.keySet())
			{
				if(okey instanceof String)
				{
					String key = (String) okey;
					if (key.startsWith("openfile"))
					{
						int index = 0;
						try
						{
							index = Integer.parseInt(key.substring(8));
						}
						catch (Exception e)
						{
						}
						openfiles.put(index, new File(props.getProperty(key)));
					}
				}
			}
			ret.setOpenedFiles(openfiles.values().toArray(new File[openfiles.size()]));
			
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
			
			try
			{
				List<ClassInfo> gis = new ArrayList<ClassInfo>();
				List<ClassInfo> gts = new ArrayList<ClassInfo>();
				List<ClassInfo> ges = new ArrayList<ClassInfo>();
				List<ClassInfo> ac = new ArrayList<ClassInfo>();
				if (Integer.parseInt(props.getProperty("build")) == BpmnEditor.BUILD)
				{
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
			}
			catch (Exception e)
			{
			}
		}
		catch (IOException e)
		{
		}
		
		return ret;
	}
	
	/**
	 *  Scan for task classes.
	 */
//	public static final Set<ClassInfo>[] scanForClasses(Settings settings, final ClassLoader cl, final boolean includeboot)
//	{
//		final Set<ClassInfo> res1 = new HashSet<ClassInfo>();
//		final Set<ClassInfo> res2 = new HashSet<ClassInfo>();
//		final Set<ClassInfo> res3 = new HashSet<ClassInfo>();
//		final Set<ClassInfo> res4 = new HashSet<ClassInfo>();
//		
//		scanForClasses(settings, cl, new FileFilter("$", false), new BpmnClassFilter(res1, res2, res3, res4, includeboot), includeboot);
//		
//		return new Set[]{res1, res2, res3, res4};
//	}
	
	/**
	 *  Scan for task classes.
	 */
	protected static final IFuture<Void> scanForClasses(Settings settings, ClassLoader cl, IFilter<Object> filefilter, IFilter<Class<?>> classfilter, boolean includeboot)
	{
		URL[] urls = SUtil.getClasspathURLs(cl, includeboot).toArray(new URL[0]);
		return scanForClasses(settings, urls, filefilter, classfilter, includeboot);
	}
	
	/**
	 *  Scan for task classes.
	 *  
	 *  Note: This uses ASM for inspecting classes for the following reason:
	 *  Generally, if a class is loaded through a classloader and the classloader is
	 *  garbage-collected at a later point, the class will be unloaded as well. However,
	 *  some Java base classes are loaded through some sort of implicit classloader, even
	 *  if the parent of the classloader is null. Since that classloader cannot be
	 *  unloaded, the classes are permanently stored in memory once loaded. Since we potentially
	 *  scan all classes here, even Java system classes, this results in a huge baggage in memory
	 *  after scanning which cannot be unloaded. Therefore we have to use an arms-length
	 *  approach for touching classes, i.e. inspect them without loading them, which
	 *  is what ASM provides here.
	 */
	protected static final IFuture<Void> scanForClasses(final Settings settings, final URL[] urls, final IFilter<Object> filefilter, final IFilter<Class<?>> classfilter, boolean includeboot)
	{
		final Future<Void> ret = new Future<Void>();
		(new Thread(new Runnable()
		{
			public void run()
			{
				final BpmnClassFilter cf = ((BpmnClassFilter)classfilter);
				
				URLClassLoader rcl = new URLClassLoader(urls, null);
				
				String[] filenames = SReflect.scanForFiles(urls, filefilter);
				
				Map<String, Set<String>> ifacecache = new HashMap<String, Set<String>>();
				
				synchronized (settings.getProgressBar().getMonitor())
				{
					settings.getProgressBar().start("Scanning classes...", filenames.length);
					int count = 0;
					for(String filename : filenames)
					{
						filename = filename.replace("/", ".");
						final String cname = filename.substring(0, filename.length() - 6);
						filename = filename.replace(".", "/");
						filename = filename.replace("/class", ".class");
						
						final Set<String> ifaces = getInterfaces(filename, rcl, ifacecache);
//						System.out.println("found interfaces: "+ifacecache.size());
						
						Set<String> sclasses = getSuperClasses(filename, rcl);
						if (sclasses.contains("java/lang/Exception"))
						{
							cf.exception.add(new ClassInfo(cname));
						}
						
						InputStream is = rcl.getResourceAsStream(filename);
						try
						{
							ClassReader cr = new ClassReader(is);
							cf.all.add(new ClassInfo(cname));
							
							ClassVisitor cv = new ClassVisitor(Opcodes.ASM4)
							{
								public void visit(int version, int access, String name,
									String signature, String superName,
									String[] interfaces)
								{
									if((access & Opcodes.ACC_ABSTRACT) == 0 &&
										(access & Opcodes.ACC_PUBLIC) > 0 &&
										ifaces.contains("jadex/bpmn/model/task/ITask"))
									{
										cf.task.add(new ClassInfo(cname));
									}
									
									if ((access & Opcodes.ACC_INTERFACE) > 0)
									{
										cf.iface.add(new ClassInfo(cname));
									}
								}
								
								public AnnotationVisitor visitAnnotation(String desc, boolean visible)
								{
									if("Ljadex/bpmn/model/task/annotation/Task;".equals(desc))
									{
										cf.task.add(new ClassInfo(cname));
									}
									return super.visitAnnotation(desc, visible);
								}
							};
							
							cr.accept(cv, 0);
						}
						catch (Exception e)
						{
						}
						settings.getProgressBar().update(++count);
					}
					settings.getProgressBar().finish();
//					System.out.println("found1: "+cf.task+" "+cf.task.getClass());
					
					SwingUtilities.invokeLater(new Runnable()
					{
						public void run()
						{
							ret.setResult(null);
						}
					});
					
//					ISubscriptionIntermediateFuture<Class<?>> fut = SReflect.asyncScanForClasses(cl, filefilter, classfilter, -1, includeboot);
//					fut.addResultListener(new IIntermediateResultListener<Class<?>>()
//					{
//						public void intermediateResultAvailable(Class<?> result)
//						{
//						}
//						public void finished()
//						{
//						}
//						public void resultAvailable(Collection<Class<?>> result)
//						{
//						}
//						public void exceptionOccurred(Exception exception)
//						{
//						}
//					});
//					fut.get(new ThreadSuspendable());
				}
			}
		})).start();
		return ret;
	}
	
	protected static final Set<String> getSuperClasses(String cname, URLClassLoader rcl)
	{
		Set<String> ret = new HashSet<String>();
		
		try
		{
			InputStream is = rcl.getResourceAsStream(cname);
			ClassReader cr = new ClassReader(is);
			is.close();
			
			String sname = cr.getSuperName();
			if (sname != null)
			{
				ret.add(sname);
				ret.addAll(getSuperClasses(sname + ".class", rcl));
			}
		}
		catch (Exception e)
		{
		}
	
		return ret;
	}
	
	protected static final Set<String> getInterfaces(String cname, URLClassLoader rcl, Map<String, Set<String>> cache)
	{
		Set<String> ret = cache.get(cname);
		if (ret == null)
		{
			ret = new HashSet<String>();
			
			try
			{
				InputStream is = rcl.getResourceAsStream(cname);
				ClassReader cr = new ClassReader(is);
				is.close();
				
				String[] ifaces = cr.getInterfaces();
				for (String iface : ifaces)
				{
					ret.addAll(getInterfaces(iface + ".class", rcl, cache));
				}
				
				ret.addAll(Arrays.asList(ifaces));
				String sname = cr.getSuperName();
				if (sname != null)
				{
					sname += ".class";
					ret.addAll(getInterfaces(sname, rcl, cache));
				}
				
				cache.put(cname, ret);
			}
			catch(NullPointerException e)
			{
				e.printStackTrace();
			}
			catch (Exception e)
			{
//				e.printStackTrace();
			}
		}
		
		return ret;
	}
	
	/**
	 * 
	 */
	public static class BpmnClassFilter implements IFilter<Class<?>>
	{
		protected Set<ClassInfo> task;
		protected Set<ClassInfo> iface;
		protected Set<ClassInfo> exception;
		protected Set<ClassInfo> all;
		protected boolean includeboot;
		
		public BpmnClassFilter(Set<ClassInfo> task, Set<ClassInfo> iface, Set<ClassInfo> exception, Set<ClassInfo> all, boolean includeboot)
		{
			this.task = task;
			this.iface = iface;
			this.exception = exception;
			this.all = all;
			this.includeboot = includeboot;
		}
		
		public BpmnClassFilter(Set<ClassInfo>[] classes, boolean includeboot)
		{
			this.task = classes[0];
			this.iface = classes[1];
			this.exception = classes[2];
			this.all = classes[3];
			this.includeboot = includeboot;
		}
		
		public boolean filter(final Class<?> obj)
		{
			try
			{
				if(!obj.isInterface())
				{
					if (SReflect.isSupertype(Exception.class, obj))
					{
						ClassInfo ci = new ClassInfo(new String(obj.getName()));
						exception.add(ci);
					}
					
					if(!Modifier.isAbstract(obj.getModifiers()) && Modifier.isPublic(obj.getModifiers()))
					{
						ClassInfo ci = new ClassInfo(new String(obj.getName()));
						all.add(ci);
						if(!task.contains(ci))
						{
							ClassLoader cl = obj.getClassLoader();
							Class<?> tcl = Class.forName(ITask.class.getName(), false, cl);
							if(SReflect.isSupertype(tcl, obj))
							{
								task.add(ci);
							}
							else
							{
								if(obj.getAnnotation(Task.class)!=null)
								{
									task.add(ci);
								}
							}
						}
					}
				}
				else
				{
					// collect interfaces
					ClassInfo ci = new ClassInfo(new String(obj.getName()));
					iface.add(ci);
					all.add(ci);
				}
			}
			catch(Exception e)
			{
			}
			return false;
		}
	}
	
	/**
	 * 
	 */
	public static class FileFilter implements IFilter<Object>
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
