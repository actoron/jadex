package jadex.maven;

import java.io.File;
import java.util.regex.Pattern;

import org.apache.commons.io.FilenameUtils;

public class ResourceUtils
{
	/**
	 * Get the relative path from one file to another, specifying the directory
	 * separator. If one of the provided resources does not exist, it is assumed
	 * to be a file unless it ends with '/' or '\'.
	 * 
	 * @param targetPath
	 *            is calculated to this file
	 * @param basePath
	 *            is calculated from this file
	 * @param pathSeparator
	 *            directory separator. The platform default is not assumed so
	 *            that we can test Unix behaviour when running on Windows (for
	 *            example)
	 * @return target relative path from the base
	 */

	public static String getRelativePath(String targetPath, String basePath, String pathSeparator)
	{
		if (targetPath.toLowerCase().startsWith("file:/"))
		{
			targetPath = targetPath.substring(6);
			while (targetPath.startsWith("/"))
				targetPath = targetPath.substring(1);
		}
		if (basePath.toLowerCase().startsWith("file:/"))
		{
			basePath = basePath.substring(6);
			while (basePath.startsWith("/"))
				basePath = basePath.substring(1);
		}

		String tempS = basePath;
		while (true)
		{
			File baseF = new File(tempS);
			if ((baseF.exists()) && (baseF.isFile()))
			{
				basePath = baseF.getAbsolutePath();
				break;
			}
			if (!pathSeparator.equals("/"))
				break;
			if (tempS.startsWith("/"))
				break;
			tempS = "/" + tempS;
		}
		tempS = targetPath;
		while (true)
		{
			File targetF = new File(tempS);
			if ((targetF.exists()) && (targetF.isFile()))
			{
				targetPath = targetF.getAbsolutePath();
				break;
			}
			if (!pathSeparator.equals("/"))
				break;
			if (tempS.startsWith("/"))
				break;
			tempS = "/" + tempS;
		}

		// Normalize the paths
		// System.out.println("ResourceUtils.getRelativePath()..target("+pathSeparator+"):"+targetPath);
		// System.out.println("ResourceUtils.getRelativePath()..base("+pathSeparator+"):"+basePath);
		String normalizedTargetPath = FilenameUtils.normalizeNoEndSeparator(targetPath);
		String normalizedBasePath = FilenameUtils.normalizeNoEndSeparator(basePath);
		// Undo the changes to the separators made by normalization

		if (pathSeparator.equals("/"))
		{
			normalizedTargetPath = FilenameUtils.separatorsToUnix(normalizedTargetPath);
			normalizedBasePath = FilenameUtils.separatorsToUnix(normalizedBasePath);
		}
		else if (pathSeparator.equals("\\"))
		{
			normalizedTargetPath = FilenameUtils.separatorsToWindows(normalizedTargetPath);
			normalizedBasePath = FilenameUtils.separatorsToWindows(normalizedBasePath);
		}
		else
		{
			throw new IllegalArgumentException("Unrecognised dir separator '" + pathSeparator + "'");
		}

		// System.out.println("ResourceUtils.getRelativePath()..normalizedTarget("+pathSeparator+"):"+normalizedTargetPath);
		// System.out.println("ResourceUtils.getRelativePath()..normalizedBase("+pathSeparator+"):"+normalizedBasePath);

		String[] base = normalizedBasePath.split(Pattern.quote(pathSeparator));
		String[] target = normalizedTargetPath.split(Pattern.quote(pathSeparator));
		// First get all the common elements. Store them as a string,
		// and also count how many of them there are.
		StringBuffer common = new StringBuffer();
		int commonIndex = 0;
		while (commonIndex < target.length && commonIndex < base.length && target[commonIndex].trim().equals(base[commonIndex].trim()))
		{
			common.append(target[commonIndex].trim() + pathSeparator);
			commonIndex++;
		}
		if (commonIndex == 0)
		{
			// No single common path element. This most
			// likely indicates differing drive letters, like C: and D:.
			// These paths cannot be relativized.
			File ff = new File(targetPath);
			if ((ff.exists()) && (ff.isFile()))
				return ff.getAbsolutePath();
			else
				throw new IllegalArgumentException("No common path element found for '" + normalizedTargetPath + "' and '"
						+ normalizedBasePath + "'");
		}
		// The number of directories we have to backtrack depends on whether the
		// base is a file or a dir
		// For example, the relative path from
		//
		// /foo/bar/baz/gg/ff to /foo/bar/baz
		//
		// ".." if ff is a file
		// "../.." if ff is a directory
		//
		// The following is a heuristic to figure out if the base refers to a
		// file or dir. It's not perfect, because
		// the resource referred to by this path may not actually exist, but
		// it's the best I can do
		boolean baseIsFile = true;
		File baseResource = new File(normalizedBasePath);
		if (baseResource.exists())
		{
			baseIsFile = baseResource.isFile();
		}
		else if (basePath.endsWith(pathSeparator))
		{
			baseIsFile = false;
		}
		StringBuffer relative = new StringBuffer();
		if (base.length != commonIndex)
		{
			int numDirsUp = baseIsFile ? base.length - commonIndex - 1 : base.length - commonIndex;
			for (int i = 0; i < numDirsUp; i++)
			{
				relative.append(".." + pathSeparator);
			}
		}
		relative.append(normalizedTargetPath.substring(common.length()));
		// System.out.println("ResourceUtils.getRelativePath()..relativeTarget:"+relative.toString());
		return relative.toString();
	}

}
