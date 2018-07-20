package jadex.bdiv3.tutorial.b5;

import java.util.HashMap;
import java.util.Map;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanContextCondition;
import jadex.bdiv3.annotation.PlanFailed;
import jadex.bdiv3.annotation.PlanPassed;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.bdiv3.runtime.IPlan;
import jadex.bridge.IInternalAccess;
import jadex.bridge.component.IExecutionFeature;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.Description;

/**
 *  The translation agent B5.
 *  
 *  Using plan context conditions.
 */
@Agent(type=BDIAgentFactory.TYPE)
@Description("The translation agent B5. <br>  Using plan context conditions.")
public class TranslationBDI
{
	//-------- attributes --------

	/** The agent. */
	@Agent
	protected IInternalAccess agent;
	
	/** The bdi api. */
	@AgentFeature
	protected IBDIAgentFeature bdi;
	
	/** The wordtable. */
	protected Map<String, String> wordtable;
	
	/** The context. */
	@Belief
	protected boolean context = true;
	
	//-------- methods --------

	/**
	 *  Create method.
	 */
	@AgentCreated
	public void init()
	{
		this.wordtable = new HashMap<String, String>();
		this.wordtable.put("coffee", "Kaffee");
		this.wordtable.put("milk", "Milch");
		this.wordtable.put("cow", "Kuh");
		this.wordtable.put("cat", "Katze");
		this.wordtable.put("dog", "Hund");
	}
	
	
	/**
	 *  The agent body.
	 */
	@AgentBody
	public void body()
	{
		bdi.adoptPlan(new TranslatePlan("dog"));
		agent.getFeature(IExecutionFeature.class).waitForDelay(1000).get();
		context = false;
		System.out.println("context set to false");
	}
	
	/**
	 *  Translate an English word to German.
	 */
	@Plan
	public class TranslatePlan
	{
		/** The plan api. */
		@PlanAPI
		protected IPlan plan;
		
		/** The German word. */
		protected String gword;
		
		/**
		 *  Create a new TranslatePlan. 
		 */
		public TranslatePlan(String gword)
		{
			this.gword = gword;
		}
		
		/**
		 *  The context condition.
		 */
		@PlanContextCondition(beliefs="context")
		public boolean checkCondition()
		{
			return context;
		}
		
		/**
		 *  The plan body.
		 */
		@PlanBody
		public String translateEnglishGerman()
		{
			System.out.println("Plan started.");
			plan.waitFor(10000).get();
			System.out.println("Plan resumed.");
			return wordtable.get(gword);
		}
		
		/**
		 *  Called when plan passed.
		 */
		@PlanPassed
		public void passed()
		{
			System.out.println("Plan finished successfully.");
		}
		
		/**
		 *  Called when plan is aborted.
		 */
		@PlanAborted
		public void aborted()
		{
			System.out.println("Plan aborted.");
		}
		
		/**
		 *  Called when plan fails.
		 */
		@PlanFailed
		public void failed()
		{
			System.out.println("Plan failed.");
		}
	}
}