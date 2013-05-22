package jadex.bpmn.editor.gui;

import jadex.bpmn.editor.BpmnEditor;
import jadex.bridge.ClassInfo;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

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
	
	/** Flag if simple data edge auto-connect is enabled. */
	protected boolean simpledataautoconncect = true;
	
	/** Flag if save settings on exit is enable. */
	protected boolean savesettingsonexit = true;
	
	/** Smooth zoom flag. */
	protected boolean smoothzoom = true;
	
	/** The selected style sheet */
	protected String selectedsheet = BpmnEditor.STYLE_SHEETS[0].getFirstEntity();
	
	/** The library home. */
	protected File libraryhome;
	
	/** The home class loader. */
	protected ClassLoader homeclassloader;
	
	/** Global task classes */
	protected List<ClassInfo> globaltaskclasses;

	/** Global interfaces */
	protected List<ClassInfo> globalinterfaces;
	
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
	public File getLibraryHome()
	{
		return libraryhome;
	}

	/**
	 *  Sets the library home.
	 *
	 *  @param libraryhome The library home.
	 */
	public void setLibraryHome(File libraryhome)
	{
		if (libraryhome != null && libraryhome.getPath().length() > 0)
		{
			this.libraryhome = libraryhome;
			
			File libdir = new File(libraryhome.getAbsolutePath() + File.separator + "lib");
			if (!libdir.exists() || !libdir.isDirectory())
			{
				libdir = libraryhome;
			}
			
			File[] files = libdir.listFiles();
			List<URL> urls = new ArrayList<URL>();
			if (files != null)
			{
				for (File file : files)
				{
					if (file.getAbsolutePath().endsWith(".jar"))
					{
						try
						{
							urls.add(file.toURI().toURL());
						}
						catch (MalformedURLException e)
						{
						}
					}
				}
			}
			
			if (urls.isEmpty())
			{
				// Attempt developer-mode search.
				File[] dirs = libdir.listFiles();
				for (File dir : dirs)
				{
					if (dir.isDirectory())
					{
						File targetdir = new File(dir.getAbsolutePath() + File.separator + "target" + File.separator + "classes");
						if (targetdir.exists() && targetdir.isDirectory())
						{
							try
							{
								urls.add(targetdir.toURI().toURL());
							}
							catch (MalformedURLException e)
							{
							}
						}
					}
				}
			}
			
			homeclassloader = new URLClassLoader(urls.toArray(new URL[urls.size()]), Settings.class.getClassLoader());
		}
		else
		{
			this.libraryhome = null;
			homeclassloader = Settings.class.getClassLoader();
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
	 *  Gets the home class loader.
	 *
	 *  @return The home class loader.
	 */
	public ClassLoader getHomeClassLoader()
	{
		return homeclassloader;
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
		
		if (libraryhome != null)
		{
			props.put("homepath", libraryhome.getPath());
		}
		
		props.put("smoothzoom", String.valueOf(smoothzoom));
		
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
			
			prop = props.getProperty("homepath");
			if (prop != null)
			{
				ret.setLibraryHome(new File(prop));
			}
			
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
			
			File classcachefile = new File(BpmnEditor.HOME_DIR + File.separator + CLASS_CACHE_FILE_NAME);
			
			props = new Properties();
			is = new FileInputStream(classcachefile);
			props.load(is);
			is.close();
			
			List<ClassInfo> gis = new ArrayList<ClassInfo>();
			List<ClassInfo> gts = new ArrayList<ClassInfo>();
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
					else if(key.startsWith("ac"))
					{
						ac.add(new ClassInfo(props.getProperty(key)));
					}
				}
			}
			ret.setGlobalInterfaces(gis);
			ret.setGlobalTaskClasses(gts);
			ret.setGlobalAllClasses(ac);
		}
		catch (IOException e)
		{
		}
		
		return ret;
	}
}
