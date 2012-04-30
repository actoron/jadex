package jadex.base.service.settings;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.android.IAndroidContextService;
import jadex.bridge.service.types.android.IPreferences;
import jadex.commons.IPropertiesProvider;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.DelegationResultListener;
import jadex.commons.future.Future;
import jadex.commons.future.IFuture;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

/**
 * Android settings service implementation.
 */
public class AndroidSettingsService extends SettingsService {

	protected IAndroidContextService contextService;

	public static final String DEFAULT_PREFS_NAME = "jadex.base.service.settings.AndroidSettingsService.DEFAULT_PREFS_NAME";

	private IPreferences preferences;

	private boolean preferFileProperties;

	/**
	 * Creates an Android Settings Service
	 * 
	 * @param access
	 * @param saveonexit
	 * @param preferFileProperties if true, property in file overwrites property found in preferences
	 */
	public AndroidSettingsService(IInternalAccess access, boolean saveonexit, boolean preferFileProperties) {
		super(access, saveonexit);
		this.preferFileProperties = preferFileProperties;
	}

	@Override
	public IFuture<Void> startService() {
		IFuture<IAndroidContextService> service = SServiceProvider.getService(
				access.getServiceContainer(), IAndroidContextService.class);
		service.addResultListener(new DefaultResultListener<IAndroidContextService>() {
			@Override
			public void resultAvailable(IAndroidContextService result) {
				contextService = result;
			}
		});
		return super.startService();
	}

	@Override
	protected File getFile(String path) {
		return contextService.getFile(path);
	}

	@Override
	protected synchronized Properties readPropertiesFromStore() {
		this.preferences = contextService.getSharedPreferences(DEFAULT_PREFS_NAME);
		// preferences.setString("securityservice.password", "andori");
		Properties props = new Properties();
		try {
			props = super.readPropertiesFromStore();
		} catch (Exception e) {
		}
		//loadPreferencesIntoProperties(props, this.preferences, preferFileProperties);
		return props;
	}

	@Override
	protected synchronized void writePropertiesToStore(Properties props)
			throws IOException {
		savePropertiesAsPreferences(props, preferences, "");
		boolean committed = preferences.commit();
		if (!committed) {
			throw new IOException("Could not save Preferences as Properties!");
		}
	}

	private static void loadPreferencesIntoProperties(Properties props,
			IPreferences preferences, boolean preferFileProperties) {
		Map<String, ?> prefs = preferences.getAll();
		for (Entry<String, ?> pref : prefs.entrySet()) {
			String[] key = pref.getKey().split("\\.");
			String targetPropValue = (String) pref.getValue();
			Properties parentProps = props;

			for (int i = 0; i < key.length - 1; i++) {
				String id = key[i];

				Properties childProps = parentProps.getSubproperty(id);

				if (childProps == null) {
					childProps = new Properties(null, id, null);
					parentProps.addSubproperties(childProps);
				}

				parentProps = childProps;
			}

			String targetPropId = key[key.length - 1];
			Property targetProp = parentProps.getProperty(targetPropId);
			if (targetProp == null) {
				targetProp = new Property(targetPropId, targetPropValue);
				parentProps.addProperty(targetProp);
			} else if (!preferFileProperties){
				parentProps.getProperty(targetPropId).setValue(targetPropValue);
			}
		}
	}

	private static void overloadProperties(Properties props,
			IPreferences prefs, String path) {
		String appendType = props.getType();
		if (appendType != null) {
			path = path + appendType + ".";
		}

		for (Property prop : props.getProperties()) {
			String type = prop.getType();
			String value = prop.getValue();
			String prefKey = path + type;
			String prefValue = prefs.getString(prefKey, null);
			if (prefValue != null) {
				prop.setValue(prefValue.toString());
			}
		}

		for (Properties subprops : props.getSubproperties()) {
			overloadProperties(subprops, prefs, path);
		}
	}

	private static void savePropertiesAsPreferences(Properties props,
			IPreferences prefs, String path) {
		String appendType = props.getType();
		if (appendType != null) {
			path = path + appendType + ".";
		}

		for (Property prop : props.getProperties()) {
			String type = prop.getType();
			String value = prop.getValue();
			String prefKey = path + type;
			prefs.setString(prefKey, value);
		}

		for (Properties subprops : props.getSubproperties()) {
			savePropertiesAsPreferences(subprops, prefs, path);
		}
	}
}
