Object fetchNextBuildNameFromGitTag()
{
	// Fetch major.minor version from properties
	def versionprops = readProperties  file: 'src/main/buildutils/jadexversion.properties'
	def version = versionprops.jadexversion_major + "." + versionprops.jadexversion_minor
	def patch = 0;
	def branchpatch = 1; // Start branch subnumbers at 1, because jadex-1.2.3-branch-0 is ugly(?)
	def buildname	= null;

	// Fetch latest major.minor.patch tag from git
	def suffix = getLatestTagSuffix(version+".")
	if(suffix!=null)
	{
		// Strip branch, if any
		if(suffix.indexOf("-")!=-1)
			suffix = suffix.substring(0, suffix.indexOf("-"));
		patch = suffix as Integer
		
		// Fetch latest major.minor.patch[-branchname-branchpatch] tag from git for non-master/stable branches
		if(includeBranchName(env.BRANCH_NAME))
		{
			suffix = getLatestTagSuffix(version+"."+patch+"-"+env.BRANCH_NAME+"-")
			if(suffix!=null)
				branchpatch =  suffix as Integer;
		}		

		// If there are commits since last tag -> increment (branch) patch number
		buildname = createBuildname(version, patch, branchpatch, false)
		if(suffix!=null && !isHead(buildname.full))
		{
			if(includeBranchName(env.BRANCH_NAME))
				branchpatch++
			else
				patch++
			buildname = createBuildname(version, patch, branchpatch, true)
		}
	}
	
	return buildname!=null ? buildname : createBuildname(version, patch, branchpatch, false);
}

/**
 *  Check if a branch name should be included in version number.
 */
boolean includeBranchName(branch)
{
	return !"master".equals(branch) && !"stable".equals(branch);
}

/**
 *  Create the build name object.
 */
Object	createBuildname(version, patch, branchpatch, isnew)
{
	def buildname = [:]
	buildname.suffix = patch + (includeBranchName(env.BRANCH_NAME) ? "-"+env.BRANCH_NAME+"-"+branchpatch : "")
	buildname.full = version + "." +buildname.suffix
	buildname.isnew	= isnew;
	return buildname;

}

/**
 *  Fetch the latest tag matching the given prefix and return the suffix.
 *  @return The suffix or null, when no matching tag is found. 
 */
String getLatestTagSuffix(prefix)
{
	def status = sh (returnStatus: true,
		script: "git describe --match \"${prefix}*\" --abbrev=0 > tag.txt")
	if(status==0)
	{
		return readFile('tag.txt').trim().substring(prefix.length())
	}
	return null
}

/**
 *  Check if a tag points to HEAD.
 */
boolean	isHead(tag)
{
	echo "Checking if ${tag} points to HEAD"
	def status = sh (returnStatus: true,
		script: "git tag --points-at HEAD > tags.txt")
	if(status==0)
	{
		return java.util.Arrays.asList(readFile('tags.txt').trim().split("\\n")).contains(tag)
	}
	return false;
}

return this