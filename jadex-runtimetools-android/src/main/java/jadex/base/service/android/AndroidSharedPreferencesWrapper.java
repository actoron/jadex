package jadex.base.service.android;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import jadex.bridge.service.types.android.IPreferences;

public class AndroidSharedPreferencesWrapper implements IPreferences{
	
	private SharedPreferences prefs;
	
	private SharedPreferences.Editor prefEditor;
	
	private AndroidSharedPreferencesWrapper(SharedPreferences prefs) {
		this.prefs = prefs;
	}
	
	public static AndroidSharedPreferencesWrapper wrap(SharedPreferences prefs) {
		return new AndroidSharedPreferencesWrapper(prefs);
	}

	@Override
	public Map<String, ?> getAll() {
		return prefs.getAll();
	}

	@Override
	public boolean getBoolean(String key, boolean defValue) {
		return prefs.getBoolean(key, defValue);
	}

	@Override
	public String getString(String key, String defValue) {
		return prefs.getString(key, defValue);
	}

	@Override
	public void setString(String key, String value) {
		if (prefEditor == null) {
			prefEditor = prefs.edit();
		}
		prefEditor.putString(key, value);
	}

	@Override
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
