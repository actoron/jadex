package jadex.backup.job;

import jadex.backup.resource.BackupResource;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 *  A sync profile maps file states to actions.
 */
public class SyncProfile
{
	//-------- constants --------
	
	/** The update action. */
	public static final String	ACTION_UPDATE_ASK	= "update_ask";
	
	/** The override action. */
	public static final String	ACTION_OVERRIDE_ASK	= "override_ask";
	
	/** The copy action. */
	public static final String	ACTION_COPY_ASK	= "copy_ask";
	
	/** The revert action. */
	public static final String	ACTION_REVERT_ASK	= "revert_ask";
	
	/** The delete action. */
	public static final String	ACTION_DELETE_ASK	= "delete_ask";
	
	/** The skip action. */
	public static final String	ACTION_SKIP_ASK	= "skip_ask";
	
	/** The update action. */
	public static final String	ACTION_UPDATE	= "update";
	
	/** The override action. */
	public static final String	ACTION_OVERRIDE	= "override";
	
	/** The copy action. */
	public static final String	ACTION_COPY	= "copy";
	
	/** The revert action. */
	public static final String	ACTION_REVERT	= "revert";
	
	/** The delete action. */
	public static final String	ACTION_DELETE	= "delete";
	
	/** The skip action. */
	public static final String	ACTION_SKIP	= "skip";
	
	/** The allowed actions for a given state change. */
	public static final Map<String, List<String>>	ALLOWED_ACTIONS;
	
	static
	{
		Map<String, List<String>>	actions	= new LinkedHashMap<String, List<String>>();
		actions.put(BackupResource.FILE_UNCHANGED, Arrays.asList(new String[]{ACTION_SKIP}));
		actions.put(BackupResource.FILE_LOCAL_MODIFIED, Arrays.asList(new String[]{ACTION_SKIP, ACTION_REVERT, ACTION_COPY}));
		actions.put(BackupResource.FILE_REMOTE_MODIFIED, Arrays.asList(new String[]{ACTION_UPDATE, ACTION_SKIP, ACTION_COPY, ACTION_OVERRIDE}));
		actions.put(BackupResource.FILE_CONFLICT, Arrays.asList(new String[]{ACTION_COPY, ACTION_SKIP, ACTION_UPDATE, ACTION_OVERRIDE}));
		
		actions.put(BackupResource.FILE_LOCAL_ADDED, Arrays.asList(new String[]{ACTION_SKIP, ACTION_DELETE}));
		actions.put(BackupResource.FILE_REMOTE_DELETED, Arrays.asList(new String[]{ACTION_DELETE, ACTION_SKIP, ACTION_OVERRIDE, ACTION_COPY}));
		actions.put(BackupResource.FILE_REMOTE_DELETED_CONFLICT, Arrays.asList(new String[]{ACTION_COPY, ACTION_SKIP, ACTION_DELETE, ACTION_OVERRIDE}));

		actions.put(BackupResource.FILE_LOCAL_DELETED, Arrays.asList(new String[]{ACTION_SKIP, ACTION_REVERT}));
		actions.put(BackupResource.FILE_REMOTE_ADDED, Arrays.asList(new String[]{ACTION_UPDATE, ACTION_SKIP, ACTION_OVERRIDE}));
		actions.put(BackupResource.FILE_LOCAL_DELETED_CONFLICT, Arrays.asList(new String[]{ACTION_UPDATE, ACTION_SKIP, ACTION_OVERRIDE}));
		ALLOWED_ACTIONS	= Collections.unmodifiableMap(actions);
	}
	
	//-------- constructors --------
	
	/** The profile name. */
	protected String	name;
	
	/** The action mapping (state->default action). */
	protected Map<String, String>	actions;
	
	
	//-------- constructors --------
	
	/**
	 *  Create a sync profile.
	 */
	public SyncProfile()
	{
		// bean constructor.
	}
	
	/**
	 *  Create a sync profile.
	 */
	public SyncProfile(String name, Map<String, String> actions)
	{
		this.name	= name;
		this.actions	= actions;
	}
	
	//-------- methods --------
	
	/**
	 *  Get the name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Set the name.
	 */
	public void setName(String name)
	{
		this.name = name;
	}

	/**
	 *  Get the actions.
	 */
	public Map<String, String> getActions()
	{
		return actions;
	}

	/**
	 *  Set the actions.
	 */
	public void setActions(Map<String, String> actions)
	{
		this.actions = actions;
	}
	
	//-------- helper methods --------
	
	/**
	 *  Test if an action should be performed as update. e.g. also revert and delete.
	 */
	public static boolean	isUpdate(String action)
	{
		return ACTION_UPDATE.equals(action) || ACTION_DELETE.equals(action) || ACTION_REVERT.equals(action);
	}
	
	/**
	 *  Test if an action should be performed as copy.
	 */
	public static boolean	isCopy(String action)
	{
		return ACTION_COPY.equals(action);
	}
	
	/**
	 *  Test if an action should be performed as override.
	 */
	public static boolean	isOverride(String action)
	{
		return ACTION_OVERRIDE.equals(action);
	}

}
