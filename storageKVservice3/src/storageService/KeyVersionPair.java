package storageService;

public class KeyVersionPair {

	private String key;
	private Version version;

	/**
	 * Default constructor neccessary for jadex serializer.
	 */
	public KeyVersionPair() {
	}

	/**
	 * Constructor for normal use.
	 */
	public KeyVersionPair(String key, Version version) {
		this.key = key;
		this.version = version;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public Version getVersion() {
		return version;
	}

	public void setVersion(Version version) {
		this.version = version;
	}

	@Override
	public String toString() {
		return "Key=" + key + ", Version=" + version.toString();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		KeyVersionPair other = (KeyVersionPair) obj;
		if (key.equals(other.getKey()) && version.equals(other.getVersion())) {
			return true;
		} else {
			return false;
		}
	}

}
