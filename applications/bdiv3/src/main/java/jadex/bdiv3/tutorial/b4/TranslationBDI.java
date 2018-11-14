package jadex.bdiv3.tutorial.b4;

import java.util.HashMap;
import java.util.Map;

import jadex.bdiv3.BDIAgentFactory;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanAborted;
import jadex.bdiv3.annotation.PlanBody;
import jadex.bdiv3.annotation.PlanFailed;
import jadex.bdiv3.annotation.PlanPassed;
import jadex.bdiv3.features.IBDIAgentFeature;
import jadex.micro.annotation.Agent;
import jadex.micro.annotation.AgentBody;
import jadex.micro.annotation.AgentCreated;
import jadex.micro.annotation.AgentFeature;
import jadex.micro.annotation.Description;

/**
 *  The translation agent B4.
 *  
 *  Using other plan methods.
 */
@Agent(type=BDIAgentFactory.TYPE)
@Description("The translation agent B4. <br>  Using other plan methods.")
public class TranslationBDI
{
	//-------- attributes --------

//	/** The agent. */
//	@Agent
//	protected BDIAgent agent;
	
	/** The bdi api. */
	@AgentFeature
	protected IBDIAgentFeature bdi;
	
	/** The wordtable. */
	protected Map<String, String> wordtable;
	
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
		try
		{
			System.out.println(bdi.adoptPlan(new TranslatePlan("dog")).get());
		}
		catch(Exception e)
		{
			System.out.println("Plan exception: "+e);
		}
	}
	
	/**
	 *  Translate an English word to German.
	 */
	@Plan
	public class TranslatePlan
	{
		/** The plan api. */
//		@PlanAPI
//		protected IPlan plan;
		
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
		 *  The plan body.
		 */
		@PlanBody
		public String translateEnglishGerman()
		{
//			throw new PlanFailureException();
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
		public void failed(Exception e)
		{
			System.out.println("Plan failed: "+e);
		}
	}
}

