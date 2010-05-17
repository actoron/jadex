package jadex.bdi.runtime;

import kilim.Scheduler;
import kilim.Task;

public class MainScheduler extends Scheduler 
{
	static MainScheduler MAIN_SCHEDULER = new MainScheduler();

//	static
//	{
//    	Scheduler.setDefaultScheduler(MAIN_SCHEDULER); 
//	}
	
    public static void main(String[] args) throws Exception 
    {
//        new TestTask().start();
//        new TestTask().start();

//        MAIN_SCHEDULER.mainloop();
    }

//    void mainloop() throws InterruptedException
//    {
//        while(true) 
//        {
//            Task t = null;
//            synchronized(this) 
//            {
//                while (t == null) 
//                {
//                    t = (Task)super.runnableTasks.get();
//                    if (t == null) 
//                    {
//                        wait();
//                    }
//                }
//            }
//            t._runExecute(null);
//        }
//    }

    public void schedule(Task t) 
    {
        synchronized(this) 
        {
            super.runnableTasks.put(t);
            notify();
        }
    }

	// static class TestTask extends Task
	// {
	// public void execute() throws Pausable, Exception
	// {
	// for(int i = 0; i < 10; i++)
	// {
	// System.out.println("TestTask #" + id + ": " + i);
	// Task.yield(); // alternates between the two tasks.
	// }
	// }
	// }
} 