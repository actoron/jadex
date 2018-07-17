package jadex.bdiv3.examples.helloworld;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalCreationCondition;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;
import jadex.bdiv3.runtime.ChangeEvent;
import jadex.bridge.IInternalAccess;
import jadex.commons.future.IFuture;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.rules.eca.annotations.Event;

/**
 *  Hello World with goal driven print out.
 *  
 *  class is checked for annotations
 *  goal, plan type declarations from annotations or inline plans 
 *  are added to the agent type and conditions to eca rule system 
 *  class is rewritten to announce belief changes (field accesses and annotated methods)
 */
@Agent(type=BDIAgentFactory.TYPE)
public class HelloWorld3
{
	/** The bdi agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The text that is printed. */
	@Belief
//	private String[] sayhello = new String[3];
	private boolean[] sayhello = new boolean[3];
//	private byte[] sayhello = new byte[3];
//	private long[] sayhello = new long[3];
//	private short[] sayhello = new short[3];
//	private int[] sayhello = new int[3];
//	private float[] sayhello = new float[3];
//	private double[] sayhello = new double[3];
//	private char[] sayhello = new char[3];
	
	
//	@Belief
//	private byte[] by = new byte[2];
	
	/**
	 *  Simple hello world goal.
	 */
	@Goal
	public class HelloGoal
	{
		/** The text. */
		protected Object val;
		
		/**
		 *  Create a new goal whenever sayhello belief is changed.
		 */
		@GoalCreationCondition
		public HelloGoal(@Event(type=ChangeEvent.FACTCHANGED, value="sayhello") Object val)
		{
			this.val = val;
		}
		
		/**
		 *  Get the text.
		 *  @return the text.
		 */
		public String getText()
		{
			return ""+val;
		}
	}
	
	/**
	 *  The agent body.
	 *  
	 *  body is executed
	 *  changes variable value (sayhello=true)
	 *  notification is sent to eca rule system
 	 *  rule system finds creation condition of goal and executes it
	 *  right hand side creates goal and executes it
	 *  Plan is selected and executed (hello is printed out)
	 */
	@AgentBody
	public void body()
	{
//		sayhello[0] = "1";
//		sayhello[1] = "2";
//		sayhello[2] = "3";
		sayhello[0] = true;
//		sayhello[0] = 10;
//		System.out.println("body end: "+getClass().getName());
	}
	
	/**
	 *  Prints out goal text and passes.
	 */
	@Plan(trigger=@Trigger(goals=HelloGoal.class))
	protected IFuture<Void> printHello2(HelloGoal goal)
	{
		System.out.println("Plan body: "+goal.getText());
		agent.killComponent();
		return IFuture.DONE;
	}
}
