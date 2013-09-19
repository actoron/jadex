package storageService;

import java.util.List;

public interface Version {

	/**
	 * compare this version to other version
	 * @param v
	 * @return
	 */
	public Occurred compare(Version v);
	
	/**
	 * compare this version to other version
	 * @param v
	 * @return
	 */
	public Version getMax(List<Version> versions);
	
	/**
	 * concurrentOrNewer
	 */
	public boolean concurrentOrNewer(List<Version> versions);
	
	
}
