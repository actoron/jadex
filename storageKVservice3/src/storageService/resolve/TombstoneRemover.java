package storageService.resolve;

import java.util.Iterator;
import java.util.List;

import storageService.Tombstone;
import storageService.VersionValuePair;

/**
 * ResolveRandom
 * 
 * @author frederik
 * 
 */
public class TombstoneRemover implements Resolver {

	public TombstoneRemover() {
	}

	/**
	 * Return new list containing no Tombstones.
	 */
	@Override
	public List<VersionValuePair> resolve(List<VersionValuePair> list) {
		if (list == null)
			return null;

		Iterator<VersionValuePair> it = list.iterator();
		while (it.hasNext()) {
			VersionValuePair vvp = it.next();
			if (vvp.getValue().equals(new Tombstone())) {
				it.remove();
			}
		}
		return list;
	}

}
