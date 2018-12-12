package jadex.bdiv3.tutorial.e2;

import java.util.Map;

import jadex.bdiv3.annotation.Belief;
import jadex.bdiv3.annotation.Capability;
import jadex.bdiv3.annotation.Goal;
import jadex.bdiv3.annotation.GoalParameter;
import jadex.bdiv3.annotation.GoalResult;
import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.Trigger;

/**
 *  The translation capability.
 */
@Capability
public class TranslationCapability
{
	/**
	 *  The translation goal.
	 */
	@Goal
	public class Translate
	{
		/** The english word. */
		@GoalParameter
		protected String eword;
		
		/** The german word. */
		@GoalResult
		protected String gword;
		
		/**
		 *  Create a new translate goal. 
		 */
		public Translate(String eword)
		{
			this.eword = eword;
		}
	}
	
	/**
	 *  Create a new capabiliy.
	 */
	public TranslationCapability()
	{
	}
	
	/**
	 *  Translate a word.
	 */
	@Plan(trigger=@Trigger(goals=Translate.class))
	protected String translate(String eword)
	{
		return getWordtable().get(eword);
	}
	
	/**
	 *  Get the wordtable.
	 */
	@Belief
	public native Map<String, String> getWordtable();
	
	/**
	 *  Set the wordtable.
	 */
	@Belief
	public native void setWordtable(Map<String, String> wordtable);
}
