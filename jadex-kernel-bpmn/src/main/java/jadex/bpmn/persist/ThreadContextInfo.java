package jadex.bpmn.persist;

import java.util.HashMap;
import java.util.Map;

public class ThreadContextInfo
{
	/** Thread infos. */
	protected Map<ThreadInfo, ThreadContextInfo> threadinfos;
	
	public ThreadContextInfo()
	{
		threadinfos = new HashMap<ThreadInfo, ThreadContextInfo>();
	}

	/**
	 *  Gets the threadinfos.
	 *
	 *  @return The threadinfos.
	 */
	public Map<ThreadInfo, ThreadContextInfo> getThreadInfos()
	{
		return threadinfos;
	}

	/**
	 *  Sets the threadinfos.
	 *
	 *  @param threadinfos The threadinfos to set.
	 */
	public void setThreadInfos(Map<ThreadInfo, ThreadContextInfo> threadinfos)
	{
		this.threadinfos = threadinfos;
	}
	
	
}
