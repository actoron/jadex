package storageService.resolve;

import java.util.List;

import storageService.VersionValuePair;

public interface Resolver {

	/**
	 * Resolve conflics because of different versions.
	 * Resolver-Implementations may work on version and value;
	 * @param list
	 * @return List<VersionValuePair> May not contain more elements than param list.
	 */
	public List<VersionValuePair> resolve(List<VersionValuePair> list);

}
