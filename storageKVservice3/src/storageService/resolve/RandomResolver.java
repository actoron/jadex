package storageService.resolve;

import java.util.ArrayList;
import java.util.List;

import storageService.VersionValuePair;

/**
 * ResolveRandom
 * @author frederik
 *
 */
public class RandomResolver implements Resolver {

	public RandomResolver() {
	}

	/**
	 * Return new list containing a random element from old list.
	 */
	@Override
	public List<VersionValuePair> resolve(List<VersionValuePair> list) {
		if (list == null)
			return null;

		int size = list.size();
		if(size == 0) {
			return new ArrayList<VersionValuePair>();
		}
		int i = (int) Math.floor(size * Math.random());
		VersionValuePair vvp = list.get(i);
		List<VersionValuePair> newList = new ArrayList<VersionValuePair>();
		newList.add(vvp);
		return newList;
	}

}
