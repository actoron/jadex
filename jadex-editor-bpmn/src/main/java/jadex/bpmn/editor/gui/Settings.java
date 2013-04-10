package jadex.bpmn.editor.gui;

import jadex.bpmn.editor.BpmnEditor;
import jadex.commons.SUtil;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

/**
 *  The editor settings.
 */
public class Settings
{
	
	/** The settings file name. */
	protected static final String SETTINGS_FILE_NAME = "settings.cfg";
	
	/** The last file opened or saved. */
	protected File lastfile;

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
			
			ret.setLastFile(new File(props.getProperty("lastfile")));
		}
		catch (IOException e)
		{
		}
		
		return ret;
	}
}
