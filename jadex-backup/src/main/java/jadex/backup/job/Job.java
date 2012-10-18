package jadex.backup.job;

import jadex.bridge.IExternalAccess;

import java.util.ArrayList;
import java.util.List;

/**
 *  Base class for all kinds of jobs.
 */
public abstract class Job
{
	//-------- attributes --------
	
	/** The id. */ 
	protected String id;
	
	/** The job name. */
	protected String name;
	
	/** Flag if job is active. */
	protected boolean active;
	
	/** The list of tasks. */
	protected List<Task> tasks;
	
	//-------- constructors --------
	
	/**
	 *  Create a new job.
	 */
	public Job()
	{
	}
	
	/**
	 *  Create a new job.
	 */
	public Job(String id, String name)
	{
		this.id = id;
		this.name = name;
	}

	//-------- methods --------
	
	/**
	 *  Get the id.
	 *  @return The id.
	 */
	public String getId()
	{
		return id;
	}

	/**
	 *  Set the id.
	 *  @param id The id to set.
	 */
	public void setId(String id)
	{
		this.id = id;
	}

	/**
	 *  Get the name.
	 *  @return The name.
	 */
	public String getName()
	{
		return name;
	}

	/**
	 *  Set the name.
	 *  @param name The name to set.
	 */
	public void setName(String name)
	{
		this.name = name;
	}	

	/**
	 *  Get the active.
	 *  @return The active.
	 */
	public boolean isActive()
	{
		return active;
	}

	/**
	 *  Set the active.
	 *  @param active The active to set.
	 */
	public void setActive(boolean active)
	{
		this.active = active;
	}
	
	/**
	 *  Get the tasks.
	 *  @return The tasks.
	 */
	public List<Task> getTasks()
	{
		return tasks;
	}

	/**
	 *  Set the tasks.
	 *  @param tasks The tasks to set.
	 */
	public void setTasks(List<Task> tasks)
	{
		System.out.println("setTasks: Job@"+super.hashCode());
		this.tasks = tasks;
	}

	/**
	 * 
	 */
	public void addTask(Task task)
	{
		System.out.println("addTask: Job@"+super.hashCode());
		if(tasks==null)
			tasks = new ArrayList<Task>();
		tasks.add(task);
	}
	
	//-------- additional convenience mapping methods --------
	
	/**
	 *  Get the agent type.
	 */
	public String getAgentType()
	{
		return null;
	}
	
	/**
	 *  Get the view.
	 */
	public Object getView(IExternalAccess ea, boolean editable)
	{
		return null;
	}

	/**
	 *  Get the hashcode.
	 */
	public int hashCode()
	{
		return 31 + ((id == null) ? 0 : id.hashCode());
	}

	/**
	 *  Test for equality.
	 *  @param obj The object.
	 */
	public boolean equals(Object obj)
	{
		return obj instanceof Job && ((Job)obj).getId().equals(getId()); 
	}
	
	public int	hackCode()
	{
		return super.hashCode();
	}
}
