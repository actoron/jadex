package jadex.bpmn.editor.gui;

import jadex.bpmn.editor.BpmnEditor;
import jadex.commons.SUtil;
import jadex.commons.Tuple2;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
	
	/** The selected style sheet */
	protected String selectedsheet = BpmnEditor.STYLE_SHEETS[0].getFirstEntity();
	
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
	
	

	public int getToolbarIconSize()
	{
		return toolbariconsize;
	}

	public void setToolbarIconSize(int toolbariconsize)
	{
		this.toolbariconsize = toolbariconsize;
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
		
		OutputStream os = new FileOutputStream(tmpfile);
		props.store(os, "Jadex BPMN Editor Settings");
		os.close();
		
		SUtil.moveFile(tmpfile, settingsfile);
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
			for (Object okey : props.keySet())
			{
				if (okey instanceof String)
				{
					String key = (String) okey;
					if (key.startsWith("openfile"))
					{
						openfiles.add(new File(props.getProperty(key)));
					}
				}
			}
			ret.setOpenedFiles(openfiles.toArray(new File[openfiles.size()]));
		}
		catch (IOException e)
		{
		}
		
		return ret;
	}
}
