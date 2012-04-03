package jadex.base.service.settings;

import jadex.bridge.IInternalAccess;
import jadex.bridge.service.search.SServiceProvider;
import jadex.bridge.service.types.android.IAndroidContextService;
import jadex.bridge.service.types.android.IPreferences;
import jadex.commons.Properties;
import jadex.commons.Property;
import jadex.commons.future.DefaultResultListener;
import jadex.commons.future.IFuture;

import java.io.File;

/**
 * Android settings service implementation.
 */
public class AndroidSettingsService extends SettingsService {

	protected IAndroidContextService contextService;

	private final String DEFAULT_PREFS_NAME = "jadex.base.service.settings.AndroidSettingsService.DEFAULT_PREFS_NAME";

	private Properties properties;
	private IPreferences preferences;

	/**
	 * Creates an Android Settings Service
	 * 
	 * @param prefix
	 * @param access
	 * @param saveonexit
	 */
	public AndroidSettingsService(String prefix, IInternalAccess access,
			boolean saveonexit) {
		super(prefix, access, saveonexit);
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
	public IFuture<Properties> loadProperties() {
		preferences = contextService.getSharedPreferences(DEFAULT_PREFS_NAME);
//		preferences.setString("securityservice.password", "andori");
		IFuture<Properties> loadProperties = super.loadProperties();
		loadProperties
				.addResultListener(new DefaultResultListener<Properties>() {

					@Override
					public void resultAvailable(Properties result) {
						overloadProperties(props, preferences, "");
					}
				});
		return loadProperties;
	}

	@Override
	public IFuture<Void> saveProperties() {
		savePropertiesAsPreferences(props, preferences, "");
		preferences.commit();
		return super.saveProperties();
	}

	@Override
	public IFuture<Void> setProperties(String id, Properties props) {
		return super.setProperties(id, props);
	}

	@Override
	public IFuture<Properties> getProperties(String id) {
		return super.getProperties(id);
	}

	private static void overloadProperties(Properties props, IPreferences prefs,
			String path) {
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
