package storageService;

import java.io.Serializable;

public final class Tombstone implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public Tombstone() {
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		return true;
	}

}
