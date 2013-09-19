package storageService;

public class DBEntry {

	private String key;
	private Version version;
	private Object value;

	public DBEntry() {
	}

	public DBEntry(String key, Version version, Object value) {
		this.key = key;
		this.value = value;
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

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		DBEntry other = (DBEntry) obj;
		if (key.equals(other.getKey()) && version.equals(other.getVersion())
				&& value.equals(other.getValue())) {
			return true;
		} else {
			return false;
		}
	}

}
