package jadex.platform.service.context;
import jadex.bridge.service.types.context.IPreferences;

import java.util.Map;

import android.content.SharedPreferences;

public class AndroidSharedPreferencesWrapper implements IPreferences{
	
	private SharedPreferences prefs;
	
	private SharedPreferences.Editor prefEditor;
	
	private AndroidSharedPreferencesWrapper(SharedPreferences prefs) {
		this.prefs = prefs;
	}
	
	public static AndroidSharedPreferencesWrapper wrap(SharedPreferences prefs) {
		return new AndroidSharedPreferencesWrapper(prefs);
	}

	public Map<String, ?> getAll() {
		return prefs.getAll();
	}

	public boolean getBoolean(String key, boolean defValue) {
		return prefs.getBoolean(key, defValue);
	}

	public String getString(String key, String defValue) {
		return prefs.getString(key, defValue);
	}

	public void setString(String key, String value) {
		if (prefEditor == null) {
			prefEditor = prefs.edit();
		}
		prefEditor.putString(key, value);
	}

	public boolean commit() {
		boolean result = false;
		if (prefEditor != null) {
			result = prefEditor.commit();
			prefEditor = null;
		} else {
			result = true;
		}
		return result;
	}
}
