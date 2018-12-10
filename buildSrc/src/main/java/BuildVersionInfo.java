

/**
 *  Struct for build version information.
 */
public class BuildVersionInfo
{
	//-------- attributes --------
	
	/** Major version number to be incremented on incompatible API changes. */
    public final int	major;
    
	/** Minor version number to be incremented on backwards compatible API changes. */
    public final int	minor;

	/** Patch version number to be incremented on internal changes. */
    public final int	patch;
    
	/** The repo branch which was built (if any). */
    public final String	branch;

	/** The time stamp of the latest commit (for clean builds). */
    public final String	timestamp;
    
    /** Flag to indicate a non-clean build (i.e. with local modifications not under version control). */
    public final boolean	snapshot;

    //-------- constructors --------
    
    /**
     *  Create a version info object.
     *
     * @param major	Major version number to be incremented on incompatible API changes.
     * @param minor	Minor version number to be incremented on backwards compatible API changes.
     * @param patch	Patch version number to be incremented on internal changes.
     * @param branch	The repo branch which was built (if any).
     * @param timestamp	The time stamp of the latest commit (for clean builds).
     * @param snapshot	Flag to indicate a non-clean build (i.e. with local modifications not under version control).
     */
    public BuildVersionInfo(int major, int minor, int patch,
    	String branch, String timestamp, boolean snapshot)
    {
        this.major	= major;
        this.minor	= minor;
        this.patch	= patch;
        this.branch	= branch;
        this.timestamp	= timestamp;
        this.snapshot	= snapshot;
    }

    @Override
    public String toString()
    {
    	return major+"."+minor+"."+patch
    		+ (branch!=null && !branch.isEmpty() ? "-"+branch : "")
    		+ (snapshot ? "-SNAPSHOT" : "-"+timestamp);
    }
}