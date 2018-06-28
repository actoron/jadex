package jadex.bridge.service.types.context;

import java.util.Map;

/**
 * Interface for Android Shared Preferences. 
 * @author Julian Kalinowski
 */
public interface IPreferences {
	
	/**
	 * Retrieve all values from the preferences.
	 */
	public Map<String, ?> getAll();
	
	/**
	 * Retrieve a boolean value from the preferences.
	 * @param key
	 * @param defValue
	 */
	public boolean getBoolean(String key, boolean defValue);
	
	/**
	 * Retrieve a String value from the preferences.
	 * @param key
	 * @param defValue
	 */
	public String getString(String key, String defValue);
	
	/**
	 * Set a String value in the preferences editor, to be written back once commit()  or apply()  are called.
	 * @param key
	 * @param value
	 */
	public void setString(String key, String value);
	
	/**
	 * Commit your preferences changes.
	 * @return true, if commit was successful, else false.
	 */
	public boolean commit();

}
