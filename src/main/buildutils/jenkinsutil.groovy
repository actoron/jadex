Object fetchNextBuildNameFromGitTag()
{
	// Fetch major.minor version from properties
	def versionprops = readProperties  file: 'src/main/buildutils/jadexversion.properties'
	def version = versionprops.jadexversion_major + "." + versionprops.jadexversion_minor
	def patch = getLatestPatchVersion(version);
	def branchpatch = 1; // Start branch subnumbers at 1, because jadex-1.2.3-branch-0 is ugly(?)
	def buildname	= null;

	// Fetch latest major.minor.patch[-branchname-branchpatch] tag from git for non-master/stable branches
	if(includeBranchName(env.BRANCH_NAME))
	{
		def suffix = getLatestTagSuffix(version+"."+patch+"-"+env.BRANCH_NAME+"-")
		if(suffix!=null)
			branchpatch =  suffix as Integer;
	}
	
	// Create the build name based on version path and branch info.		
	buildname = createBuildname(version, patch, branchpatch, false)
	
	// If tag for buildname exists, but is not head -> there are commits since last tag -> increment (branch) patch number.
	if(getLatestTagSuffix(buildname.full)!=null && !isHead(buildname.full))
	{
		if(includeBranchName(env.BRANCH_NAME))
			branchpatch++
		else
			patch++
		buildname = createBuildname(version, patch, branchpatch, true)
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
 *  Fetch all tags matching the given major.minor version and 
 *  return the latest patch version.
 *  @return The found patch version or 0 if not found.
 */
int getLatestPatchVersion(version)
{
	def	patch	= 0;
	def status = sh (returnStatus: true,
		script: "git log --tags=\"${version}.*\" --no-walk --format=%D >tags.txt")
	if(status==0)
	{
		for(String tag: readFile('tags.txt').split("\\n"))
		{
			if(tag.startsWith("tag: "+version+"."))
			{
				tag	= tag.substring(("tag: "+version+".").length())
				if(tag.indexOf("-")!=-1)	// Strip version branch names in tag
					tag	= tag.substring(0, tag.indexOf("-"));
				if(tag.indexOf(",")!=-1)	// Strip git branch names after tag
					tag	= tag.substring(0, tag.indexOf(","));
				
				if(tag.matches("\\d+"))	// Skip tags not conforming to <major>.<minor>.<patch> or <major>.<minor>.<patch>-<branch>-<branchpatch>
				{
				 	patch = Math.max(patch, tag as Integer)
				}
				else
				{
					echo "ignored ${tag}"
				}
			}
		}
	}
	return patch
}

/**
 *  Fetch the latest tag matching the given prefix and return the suffix.
 *  @return The suffix or null, when no matching tag is found.
 */
String getLatestTagSuffix(prefix)
{
	def status = sh (returnStatus: true,
		script: "git describe --match \"${prefix}*\" --abbrev=0 > tag.txt")
	//git log --tags="4.0.*" --no-walk --format=%D >tags.txt
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