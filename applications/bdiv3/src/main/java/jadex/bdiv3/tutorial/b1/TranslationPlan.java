package jadex.bdiv3.tutorial.b1;

import java.util.HashMap;
import java.util.Map;

import jadex.bdiv3.annotation.Plan;
import jadex.bdiv3.annotation.PlanBody;

/**
 *  Translation plan that translates one word.
 */
@Plan
public class TranslationPlan
{
	/** The wordtable. */
	protected Map<String, String> wordtable;

	//-------- methods --------

	/**
	 *  Create a new TranslationPlan.
	 */
	public TranslationPlan()
	{
//		System.out.println("Created: "+this);
		this.wordtable = new HashMap<String, String>();
		this.wordtable.put("coffee", "Kaffee");
		this.wordtable.put("milk", "Milch");
		this.wordtable.put("cow", "Kuh");
		this.wordtable.put("cat", "Katze");
		this.wordtable.put("dog", "Hund");
	}
	
	/**
	 *  Plan body invoke once when plan is activated. 
	 */
	@PlanBody
	public void translateEnglishGerman()
	{
		String eword = "dog";
		String gword = wordtable.get(eword);
		System.out.println("Translated: "+eword+" - "+gword);
	}
}
