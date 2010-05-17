package jadex.bdi.runtime;

import kilim.Scheduler;
import kilim.Task;


public class NoThreadScheduler extends Scheduler
{
	public NoThreadScheduler()
	{
		super(0);
	}
	
    public void schedule(Task t) 
    {
        synchronized(this) 
        {
            runnableTasks.put(t);
        }
    }
    
    public void executeTasks()
    {
    	synchronized(this)
    	{
    		boolean executed;
    		do
    		{
    			executed = executeStep();
    		}
    		while(executed);
    	}
    }
    
    public boolean executeStep()
    {
    	boolean ret = false;
    	synchronized(this) 
        {
            Task t = (Task)runnableTasks.get();
            if(t!=null)
            {
            	t._runExecute(null);
            	ret = true;
            }
        }
    	return ret;
    }
	
//	public void switchTask(PauseReason before, Task now)
//	{
//		synchronized(this)
//		{
//			try
//			{
//				if(before!=null)
//					before.pause(before);
//			}
//			catch(Exception e)
//			{
//				e.printStackTrace();
//			}
//				
//			if(now!=null)
//			{
//				now._runExecute(null);
//			}
//		}
//	}
}

