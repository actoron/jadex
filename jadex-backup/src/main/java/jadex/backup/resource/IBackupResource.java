package jadex.backup.resource;

import jadex.commons.SUtil;
import jadex.commons.Tuple;
import jadex.commons.Tuple2;

import java.io.File;
import java.io.InputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * 
 */
public interface IBackupResource
{
	/** State when local and remote files are the same. */
	public static final String				FILE_UNCHANGED					= "file_unchanged";

	/** State when remote file is newer than local file. */
	public static final String				FILE_REMOTE_MODIFIED			= "file_remote_modified";

	/** State when local file is newer than remote file. */
	public static final String				FILE_LOCAL_MODIFIED				= "file_local_modified";

	/** State when both local file and remote file are modified. */
	public static final String				FILE_CONFLICT					= "file_conflict";

	/** State when file was added remotely and doesn't exist locally. */
	public static final String				FILE_REMOTE_ADDED				= "file_remote_added";

	/** State when file was added locally and doesn't exist remotely. */
	public static final String				FILE_LOCAL_ADDED				= "file_local_added";

	/** State when file was deleted remotely and not changed locally. */
	public static final String				FILE_REMOTE_DELETED				= "file_remote_deleted";

	/** State when file was deleted locally and not changed remotely. */
	public static final String				FILE_LOCAL_DELETED				= "file_local_deleted";

	/** State when file was deleted remotely and changed locally. */
	public static final String				FILE_REMOTE_DELETED_CONFLICT	= "file_remote_deleted_conflict";

	/** State when file was deleted locally and changed remotely. */
	public static final String				FILE_LOCAL_DELETED_CONFLICT		= "file_local_deleted_conflict";

	/** State change table &lt;remote modified, local modified, remote existing, local existing> -> state. */
	public static final Map<Tuple, String>	STATE_CHANGES = Collections.unmodifiableMap(SUtil.createHashMap
	(
		new Tuple[]
		{
			new Tuple(new Object[]{false,	false,	true,	true}),
			new Tuple(new Object[]{false,	true,	true,	true}),
			new Tuple(new Object[]{true,	false,	true,	true}),
			new Tuple(new Object[]{true,	true,	true,	true}),
			new Tuple(new Object[]{false,	true,	false,	true}),
			new Tuple(new Object[]{true,	false,	false,	true}),
			new Tuple(new Object[]{true,	true,	false,	true}),
			new Tuple(new Object[]{false,	true,	true,	false}),
			new Tuple(new Object[]{true,	false,	true,	false}),
			new Tuple(new Object[]{true,	true,	true,	false}),
			new Tuple(new Object[]{false,	false,	false,	false}),
			new Tuple(new Object[]{false,	true,	false,	false}),
			new Tuple(new Object[]{true,	false,	false,	false}),
			new Tuple(new Object[]{true,	true,	false,	false})
		}, new String[]
		{
			FILE_UNCHANGED,
			FILE_LOCAL_MODIFIED,
			FILE_REMOTE_MODIFIED,
			FILE_CONFLICT,
			FILE_LOCAL_ADDED,
			FILE_REMOTE_DELETED,
			FILE_REMOTE_DELETED_CONFLICT,
			FILE_LOCAL_DELETED,
			FILE_REMOTE_ADDED,
			FILE_LOCAL_DELETED_CONFLICT,
			FILE_UNCHANGED,
			FILE_UNCHANGED,
			FILE_UNCHANGED,
			FILE_UNCHANGED
		}
	));
	
//	/**
//	 *  Get the resource root directory.
//	 */
//	public File getResourceRoot();
//
//	/**
//	 *  Get the location for a file.
//	 *  @param file	The file.
//	 *  @return	The file location as absolute path from the resource root.
//	 */
//	public String getLocation(File file);
//
//	/**
//	 *  Get the file for a file info.
//	 *  @param path	The resource-relative file location.
//	 *  @return The local file.
//	 */
//	public File getFile(String path);
	
	/**
	 *  Get the file for a file info.
	 *  @param path	The resource-relative file location.
	 *  @return The local file.
	 */
	public InputStream getFileData(String path);
	
	/**
	 *  Close the resource.
	 */
	public void dispose();

	/**
	 *  Get the resource id.
	 *  The resource id is a globally unique id that is
	 *  used to identify the same resource on different
	 *  hosts, i.e. provided by different service instances.
	 */
	public String getResourceId();

	/**
	 *  Get the local resource id.
	 *  The local resource id is a unique id that is
	 *  used to identify an individual instance of a
	 *  distributed resource on a specific host.
	 */
	public String getLocalId();

	/**
	 *  Get the file info for a local file.
	 *  Also updates the meta information, when the file or directory has changed on disk.
	 *  @param location The local file.
	 *  @return The file info.
	 */
	public FileMetaInfo getFileInfo(String location);

	/**
	 *  Check if a local file info is current, i.e. the local file has not changed.
	 *  @param location	The file location.
	 *  @param fi	The local file info or null if the file does not exist locally.
	 *  @return False when the local file has changed with respect to the previous information.
	 */
	public boolean isCurrent(String location, FileMetaInfo fi);

	/**
	 *  Get the state of the local file with respect to a remote file info.
	 *  @param fi The remote file info.
	 *  @return The current local file info and the file state compared to the remote info.
	 */
	public Tuple2<FileMetaInfo, String> getState(FileMetaInfo fi);

	/**
	 *  Get the file infos of a directory.
	 */
	public List<FileMetaInfo> getDirectoryContents(FileMetaInfo dir);

	/**
	 *  Get a temporary location for downloading a file.
	 *  @param path	The resource location.
	 *  @param remote	The download source.
	 */
	public File getTempLocation(String path, IResourceService remote);

	/**
	 *  Update a file with a new version.
	 *  @param localfi	The local file info.
	 *  @param remotefi	The remote file info.
	 *  @param tmp	The new remote file already downloaded to a temporary location or null if the file was deleted remotely.
	 */
	public void updateFromRemote(FileMetaInfo localfi, FileMetaInfo remotefi, File tmp);

	/**
	 *  Copy the original file and update the file with a new version.
	 *  @param localfi	The local file info.
	 *  @param remotefi	The remote file info.
	 *  @param tmp	The new remote file already downloaded to a temporary location or null if the file was deleted remotely.
	 */
	public void updateAsCopy(FileMetaInfo localfi, FileMetaInfo remotefi, File tmp);

	/**
	 *  Override a remote change and update local file info as if the current local file was newer.
	 *  @param localfi	The local file info or null if the file does not exist locally.
	 *  @param remotefi	The remote file info.
	 */
	public void overrideRemoteChange(FileMetaInfo localfi, FileMetaInfo remotefi);

}