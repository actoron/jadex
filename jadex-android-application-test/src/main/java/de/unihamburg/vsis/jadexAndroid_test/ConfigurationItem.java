package de.unihamburg.vsis.jadexAndroid_test;

public class ConfigurationItem {

	private String _name;
	private String _configFile;

	public ConfigurationItem(String name, String configFile) {
		_name = name;
		_configFile = configFile;
	}

	public String get_name() {
		return _name;
	}

	public void set_name(String _name) {
		this._name = _name;
	}

	public String get_configFile() {
		return _configFile;
	}

	public void set_configFile(String _configFile) {
		this._configFile = _configFile;
	}
	
	@Override
	public String toString() {
		return _name;
	}
	


}
