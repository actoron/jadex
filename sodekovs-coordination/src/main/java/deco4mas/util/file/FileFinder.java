/**
 * 
 */
package deco4mas.util.file;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.io.FileUtils;

/**
 * This class contains convenient methods for finding files.
 * 
 * @author Thomas Preisler
 */
public class FileFinder {

	/**
	 * Returns a List of files which match the given file name. The files are searched in the given root directory (recursively).
	 * 
	 * @param rootDir
	 *            the given root directory
	 * @param fileName
	 *            the given file name
	 * @return a List of matching files
	 */
	public static List<File> findFiles(File rootDir, String fileName) {
		List<File> matchingFiles = new ArrayList<File>();

		@SuppressWarnings("unchecked")
		Collection<File> files = FileUtils.listFiles(rootDir, null, true);

		for (File file : files) {
			if (file.isFile() && file.getName().equals(fileName)) {
				matchingFiles.add(file);
			}
		}

		return matchingFiles;
	}
}