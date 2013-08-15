package jadex.bdiv3.tutorial.b5;

import jadex.bdiv3.BDIAgent;
import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.PlanAPI;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanContextCondition;
import jadex.bdiv3.annotation.PlanFailed;
import jadex.bdiv3.annotation.PlanPassed;
import jadex.bdiv3.runtime.IPlan;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.Description;

import java.util.HashMap;
import java.util.Map;

/**
 *  The translation agent B4.
 *  
 *  Using plan context condition and other plan methods.
 */
@Agent
@Description("The translation agent B4. <br>  Use of plan pre and context conditions.")
public class TranslationBDI
{
	//-------- attributes --------

	/** The agent. */
	@Agent
	protected BDIAgent agent;
	
	/** The wordtable. */
	protected Map<String, String> wordtable;
	
	/** The context. */
	@Belief
	protected boolean context = true;
	
	//-------- methods --------

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
		agent.adoptPlan("translateEnglishGerman");
	}
	
	/**
	 *  Translate an English word to German.
	 */
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
		@PlanContextCondition(events="context")
		public boolean checkPrecondition()
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
			plan.waitFor(2000).get();
			System.out.println("Plan resumed.");
			return wordtable.get(gword);
		}
		
		/**
		 * 
		 */
		@PlanPassed
		public void passed()
		{
			System.out.println("Plan finished successfully.");
		}
		
		/**
		 * 
		 */
		@PlanAborted
		public void aborted()
		{
			System.out.println("Plan finished successfully.");
		}
		
		/**
		 * 
		 */
		@PlanFailed
		public void failed()
		{
			System.out.println("Plan finished successfully.");
		}
	}
}

