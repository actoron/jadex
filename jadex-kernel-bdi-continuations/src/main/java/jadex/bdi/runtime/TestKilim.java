package jadex.bdi.runtime;

import kilim.Pausable;
import kilim.PauseReason;
import kilim.Scheduler;
import kilim.Task;

public class TestKilim extends Task implements PauseReason
{
	public Task t;
	
	public void setTask(Task t)
	{
		this.t = t;
	}

	public void execute() throws Pausable
	{
		System.out.println("t1 "+this);
		t.resume();
		pause(this);
		System.out.println("t2 "+this);
		t.resume();
	}
	
	public boolean isValid(Task t) 
	{
		return true;
	}
	
	public static void main(String[] args)
	{
		TestKilim t1 = new TestKilim();
		TestKilim t2 = new TestKilim();
		t1.setTask(t2);
		t2.setTask(t1);
		t1.setScheduler(MainScheduler.MAIN_SCHEDULER);
		t2.setScheduler(MainScheduler.MAIN_SCHEDULER);
		t1.resume();
	}
}
