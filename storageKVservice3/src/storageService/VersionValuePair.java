package storageService;

public class VersionValuePair {

	private Object value;
	private Version version;

	public VersionValuePair(Version version, Object object) {
		this.version = version;
		this.value = object;
	}

	public VersionValuePair() {
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

	/**
	 * Two VersionValuePairs are considered equal, if their versions are equal
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;

		VersionValuePair vvp = (VersionValuePair) obj;
		if (this.version.equals(vvp.getVersion())) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		if (version == null) {
			return "version == null";
		}
		if (value == null) {
			return version.toString() + ": Value=null";
		} else {
			return version.toString() + ": Value=" + value.toString();
		}
	}

}
