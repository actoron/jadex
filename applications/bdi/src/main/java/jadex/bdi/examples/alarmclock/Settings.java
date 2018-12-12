package jadex.bdi.examples.alarmclock;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 *  Contains all settings that need to be amde persist.
 */
public class Settings implements Cloneable, Serializable
{
	//-------- attributes --------

	/** The ampm time format. */
	protected boolean ampm;

	/** The font size. */
	protected int fontsize;

	/** The alarms. */
	protected List<Alarm> alarms;

	/** The actual filename for this settings. */
	protected String filename;

	/** The autosave flag. */
	protected boolean autosave;

	//-------- constructors --------

	/**
	 *  Create new settings.
	 */
	public Settings()
	{
		this(false, 14, new Alarm[0], true, null);
	}

	/**
	 *  Create new settings.
	 *  @param filename The filename.
	 */
	public Settings(String filename)
	{
		this(false, 14, new Alarm[0], true, filename);
	}

	/**
	 *  Create new settings.
	 *  @param ampm The ampm format indicator.
	 * 	@param alarms The alarms.
	 * 	@param autosave The autosave option.
	 * 	@param filename The filename.
	 *
	 */
	public Settings(boolean ampm, int fontsize, Alarm[] alarms, boolean autosave, String filename)
	{
		this.ampm = ampm;
		this.fontsize = fontsize;
		setAlarms(alarms);
		this.autosave = autosave;
		this.filename = filename;
	}

	//-------- methods --------

	/**
	 *  Test is ampm format.
	 *  @return True, if ampm format.
	 */
	public boolean isAMPM()
	{
		return ampm;
	}

	/**
	 *  Set the ampm mode.
	 *  @param ampm The ampm mode.
	 */
	public void setAMPM(boolean ampm)
	{
		this.ampm = ampm;
		if(filename != null && isAutosave())
		{
			save0();
		}
	}

	/**
	 *  Get the font size.
	 *  @return The font size.
	 */
	public int getFontsize()
	{
		return fontsize;
	}

	/**
	 *  Set the font size
	 *  @param fontsize The fontsize.
	 */
	public void setFontsize(int fontsize)
	{
		this.fontsize = fontsize;
	}

	/**
	 *  Get the alarms.
	 *  @return The alarms.
	 */
	public Alarm[] getAlarms()
	{
		return (Alarm[])alarms.toArray(new Alarm[alarms.size()]);
	}
	
	/**
	 *  Add a new alarm.
	 *  @param alarm The alarm.
	 */
	public void addAlarm(Alarm alarm)
	{
		alarms.add(alarm);
		if(filename != null && isAutosave())
		{
			save0();
		}
	}

	/**
	 *  Remove an alarm.
	 *  @param alarm The alarm.
	 */
	public void removeAlarm(Alarm alarm)
	{
		alarms.remove(alarm);
		if(filename != null && isAutosave())
		{
			save0();
		}
	}

	/**
	 *  Test if autosave.
	 *  @return True, if autosave.
	 */
	public boolean isAutosave()
	{
		return autosave;
	}

	/**
	 *  Test the autosave option.
	 *  @param autosave
	 */
	public void setAutosave(boolean autosave)
	{
		this.autosave = autosave;
		if(filename != null && isAutosave())
		{
			save0();
		}
	}

	/**
	 *  Set the alarms.
	 *  @param alarms The alarms.
	 */
	public void setAlarms(Alarm[] alarms)
	{
		this.alarms = new ArrayList<Alarm>();
		for(int i = 0; alarms != null && i < alarms.length; i++)
		{
			addAlarm(alarms[i]);
		}
		if(filename != null && isAutosave())
		{
			save0();
		}
	}
	
	/**
	 *  Get the filename.
	 *  @return The filename.
	 */
	public String getFilename()
	{
		return filename;
	}

	/**
	 *  Set the filename.
	 *  @param filename The filename.
	 */
	public void setFilename(String filename)
	{
		this.filename = filename;
		if(filename!=null && isAutosave())
		{
			save0();
		}
	}

	/**
	 *  Save settings without exception.
	 */
	protected void save0()
	{
		try
		{
			save();
		}
		catch(Exception e)
		{
			System.out.println("Could not save settings: "+e);
		}
	}

	/**
	 * Creates and returns a copy of this object.  The precise meaning
	 * of "copy" may depend on the class of the object. The general
	 * intent is that, for any object <tt>x</tt>, the expression:
	 * @return a clone of this instance.
	 */
	protected Object clone()
	{
		Settings ret = null;
		try
		{
			ret = (Settings)super.clone();
			ret.alarms = new ArrayList<Alarm>();
			for(int i = 0; i < alarms.size(); i++)
			{
				ret.addAlarm((Alarm)((Alarm)alarms.get(i)).clone());
			}
		}
		catch(CloneNotSupportedException e)
		{
			e.printStackTrace();
		}
		return ret;
	}


	/**
	 *  Save the settings.
	 */
	public void save() throws IOException
	{
		FileOutputStream fos = new FileOutputStream(filename);
		ObjectOutputStream enc = new ObjectOutputStream(fos);
	    enc.writeObject(this);
	    enc.close();
		fos.close();
	}
	
	/**
	 *  Load the settings.
	 *  @param settings_loc The settings location.
	 *  @return The loaded settings.
	 */
	public static Settings loadSettings(String settings_loc)
	{
		Settings ret = null;
		try
		{
			final FileInputStream fis = new FileInputStream(settings_loc);
			ObjectInputStream dec = new ObjectInputStream(fis);
			ret = (Settings)dec.readObject();
			dec.close();
		}
		catch(Exception e)
		{
			System.out.println("Could not load settings: "+e);
//			e.printStackTrace();
			ret = new Settings("./alarmclock_settings.ser");
		}
//		Alarm[]	alarms	= ret.getAlarms();
//		for(int i=0; i<alarms.length; i++)
//			alarms[i].setClock(clock);
		return ret;
	}
	
	/**
	 *  Save the settings.
	 * /
	public void save() throws IOException
	{
		FileOutputStream fos = new FileOutputStream(filename);
		fos.write(JavaWriter.objectToByteArray(this, null));
		fos.close();
	}*/
	
	/**
	 *  Load the settings.
	 *  @param settings_loc The settings location.
	 *  @return The loaded settings.
	 * /
	public static Settings loadSettings(String settings_loc, IExternalAccess agent)
	{
		Settings ret = null;
		try
		{
			final FileInputStream fis = new FileInputStream(settings_loc);
			ret = (Settings)JavaReader.getInstance().read(fis, null, null);
			fis.close();
		}
		catch(Exception e)
		{
			System.out.println("Could not load settings: "+e);
//			e.printStackTrace();
			ret = new Settings("./alarmclock_settings.xml");
		}
		Alarm[]	alarms	= ret.getAlarms();
		for(int i=0; i<alarms.length; i++)
			alarms[i].setClock((IClockService)agent.getComponentFeature(IRequiredServicesFeature.class).getService(IClockService.class));
		return ret;
	}*/
}
