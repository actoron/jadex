package jadex.bpmn.runtime.persist;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jadex.bpmn.runtime.ProcessThread;
import jadex.bpmn.runtime.handler.SplitInfo;

/**
 *  Thread info object.
 */
public class ThreadInfo
{
	/** Id of the thread. */
	protected String id;
	
	/** Id of the next activity. */
	protected String activityid;
	
	/** Id of the last edge (if any). */
	protected String edgeid;
	
	/** The data of the current or last activity. */
	protected Map<String, Object> data;
	
	/** The data of the current data edges. */
	protected Map<String, Object> dataedges;
	
	/** The exception that has just occurred in the process (if any). */
	protected Exception	exception;
	
	/** Is the task canceled. */
	protected boolean canceled;
	
	/** The id counter for sub processes. */
	protected int idcnt;
	
	/** The split infos. */
	protected Map<String, SplitInfo> splitinfos;
	
	/** Subthread infos. */
	protected List<ThreadInfo> subthreadinfos;
	
	
	/**
	 *  Create a new ThreadInfo.
	 */
	public ThreadInfo()
	{
	}
	
	/**
	 *  Create a new ThreadInfo.
	 */
	public ThreadInfo(ProcessThread thread)
	{
		id	= thread.getId();
		activityid = thread.getActivity()!=null ? thread.getActivity().getId() : null;
		edgeid = thread.getLastEdge()!=null ? thread.getLastEdge().getId() : null;
		data = thread.getData();
		dataedges = thread.getDataEdges();
		exception = thread.getException();
		canceled = thread.isCanceled();
		idcnt = thread.idcnt;
		splitinfos = thread.splitinfos;
		
		if(thread.getSubthreads()!=null)
		{
			this.subthreadinfos	= new ArrayList<ThreadInfo>();
			for(ProcessThread pt: thread.getSubthreads())
			{
				subthreadinfos.add(new ThreadInfo(pt));
			}
		}
	}

	/**
	 *  Gets the id.
	 *  @return The id.
	 */
	public String getId()
	{
		return id;
	}

	/**
	 *  Sets the id.
	 *  @param id The id to set.
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 *  Gets the activityid.
	 *  @return The activityid.
	 */
	public String getActivityid()
	{
		return activityid;
	}

	/**
	 *  Sets the activityid.
	 *  @param activityid The activityid to set.
	 */
	public void setActivityid(String activityid)
	{
		this.activityid = activityid;
	}

	/**
	 *  Gets the edgeid.
	 *  @return The edgeid.
	 */
	public String getEdgeid()
	{
		return edgeid;
	}

	/**
	 *  Sets the edgeid.
	 *  @param edgeid The edgeid to set.
	 */
	public void setEdgeid(String edgeid)
	{
		this.edgeid = edgeid;
	}

	/**
	 *  Gets the data.
	 *  @return The data.
	 */
	public Map<String, Object> getData()
	{
		return data;
	}

	/**
	 *  Sets the data.
	 *  @param data The data to set.
	 */
	public void setData(Map<String, Object> data)
	{
		this.data = data;
	}

	/**
	 *  Gets the dataedges.
	 *  @return The dataedges.
	 */
	public Map<String, Object> getDataedges()
	{
		return dataedges;
	}

	/**
	 *  Sets the dataedges.
	 *  @param dataedges The dataedges to set.
	 */
	public void setDataedges(Map<String, Object> dataedges)
	{
		this.dataedges = dataedges;
	}

	/**
	 *  Gets the subthreads.
	 *  @return The subthreads.
	 */
	public List<ThreadInfo> getSubthreads()
	{
		return subthreadinfos;
	}

	/**
	 *  Sets the subthreads.
	 *  @param subthreads The subthreads to set.
	 */
	public void setSubthreads(List<ThreadInfo> subthreads)
	{
		this.subthreadinfos = subthreads;
	}

	/**
	 *  Gets the exception.
	 *  @return The exception.
	 */
	public Exception getException()
	{
		return exception;
	}

	/**
	 *  Sets the exception.
	 *  @param exception The exception to set.
	 */
	public void setException(Exception exception)
	{
		this.exception = exception;
	}

	/**
	 *  Gets the canceled.
	 *  @return The canceled.
	 */
	public boolean isCanceled()
	{
		return canceled;
	}

	/**
	 *  Sets the canceled.
	 *  @param canceled The canceled to set.
	 */
	public void setCanceled(boolean canceled)
	{
		this.canceled = canceled;
	}

	/**
	 *  Gets the idcnt.
	 *  @return The idcnt.
	 */
	public int getIdcnt()
	{
		return idcnt;
	}

	/**
	 *  Sets the idcnt.
	 *  @param idcnt The idcnt to set.
	 */
	public void setIdcnt(int idcnt)
	{
		this.idcnt = idcnt;
	}

	/**
	 *  Gets the splitinfos.
	 *  @return The splitinfos.
	 */
	public Map<String, SplitInfo> getSplitinfos()
	{
		return splitinfos;
	}

	/**
	 *  Sets the splitinfos.
	 *  @param splitinfos The splitinfos to set.
	 */
	public void setSplitinfos(Map<String, SplitInfo> splitinfos)
	{
		this.splitinfos = splitinfos;
	}
}
