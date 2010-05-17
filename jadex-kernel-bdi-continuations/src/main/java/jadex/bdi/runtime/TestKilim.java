package jadex.bdi.runtime;

import kilim.Pausable;
import kilim.PauseReason;
import kilim.Task;


public class TestKilim extends Task implements PauseReason
{
	public Task		t;

	public String	name;

	public TestKilim(String name)
	{
		this.name = name;
	}

	public void setTask(Task t)
	{
		this.t = t;
	}

	public void execute() throws Pausable
	{
		System.out.println("t1 " + this);
		t.resume();
		Task.pause(this);
//		((TestKilim)t).switchTask(this);
		System.out.println("t2 " + this);
		t.resume();
		Task.pause(this);
//		((TestKilim)t).switchTask(this);
	}

	public boolean isValid(Task t)
	{
		return true;
	}

	public String toString()
	{
		return "TestKilim [name=" + this.name + "]";
	}

//	public boolean switchTask(PauseReason before)
//	{
//		if(scheduler == null)
//			return false;
//
//		boolean doSchedule = false;
//		synchronized(this)
//		{
//			if(done || running)
//				return false;
//			running = doSchedule = true;
//		}
//		if(doSchedule)
//		{
//			((MainScheduler)scheduler).switchTask(before, this);
//		}
//		return doSchedule;
//	}

	public static void main(String[] args)
	{
		TestKilim t1 = new TestKilim("task1");
		TestKilim t2 = new TestKilim("task2");
		t1.setTask(t2);
		t2.setTask(t1);
		NoThreadScheduler sch = new NoThreadScheduler();
		t1.setScheduler(sch);
		t2.setScheduler(sch);
		t1.resume();
//		sch.executeTasks();
		sch.executeStep();
		System.out.println("end");
	}
}
