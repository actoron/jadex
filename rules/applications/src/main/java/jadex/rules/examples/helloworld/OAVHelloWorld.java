package jadex.rules.examples.helloworld;

import jadex.rules.parser.conditions.ParserHelper;
import jadex.rules.rulesystem.IAction;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.IVariableAssignments;
import jadex.rules.rulesystem.LIFOAgenda;
import jadex.rules.rulesystem.RuleSystem;
import jadex.rules.rulesystem.RuleSystemExecutor;
import jadex.rules.rulesystem.Rulebase;
import jadex.rules.rulesystem.rete.RetePatternMatcherFunctionality;
import jadex.rules.rulesystem.rules.Rule;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;
import jadex.rules.state.OAVTypeModel;
import jadex.rules.state.javaimpl.OAVStateFactory;
import jadex.rules.tools.reteviewer.RuleEnginePanel;

/**
 *  Simple hello world program for illustrating how a simple
 *  rule application is set up.
 */
public class OAVHelloWorld
{
	/**
	 *  Main method.
	 *  @param args The arguments.
	 */
	public static void main(String[] args)
	{
		// Create simple OAV type model.
		OAVTypeModel helloworld_type_model = new OAVTypeModel("helloworld_type_model");
		helloworld_type_model.addTypeModel(OAVJavaType.java_type_model);
		OAVObjectType message_type = helloworld_type_model.createType("message"); 
		final OAVAttributeType message_has_text = message_type.createAttributeType("message_has_text", OAVJavaType.java_string_type);
		
		// Create rete system.
		IOAVState state = OAVStateFactory.createOAVState(helloworld_type_model); // Create the production memory.
		Rulebase rb	= new Rulebase();
		
		// The following three code fragments represent alternatives for condition creation
		
//		// Create rule condition manually.
//		Variable message = new Variable("?message", message_type);
//		ObjectCondition msgcon = new ObjectCondition(message_type);
//		msgcon.addConstraint(new BoundConstraint(null, message));

//		// Create rule condition using clips parser.
//		ICondition	msgcon	= ParserHelper.parseClipsCondition("?message <- (message)", helloworld_type_model);

		// Create rule condition using jcl (java condition language) parser.
		ICondition	msgcon	= ParserHelper.parseJavaCondition("message $message", helloworld_type_model);

		// Add rule to rulebase.
		rb.addRule(new Rule("new_message", msgcon, new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
//				Object message = assignments.getVariableValue("?message");	// Use for manual/clips condition
				Object message = assignments.getVariableValue("$message");	// Use for jcl condition
				System.out.println("New message found: "+state.getAttributeValue(message, message_has_text));
			}
		}));

		// Initialize rule system.
//		rete.getAgenda().setHistoryEnabled(true);
		
		// Add fact.
		Object m = state.createRootObject(message_type);
		state.setAttributeValue(m, message_has_text, "Hello OAV (object, attribute, value) World!");

		RuleSystem rete = new RuleSystem(state, rb, new RetePatternMatcherFunctionality(rb), new LIFOAgenda());

		// Initialize rule system.
		rete.init();
		
		RuleSystemExecutor exe = new RuleSystemExecutor(rete, true);
		RuleEnginePanel.createRuleEngineFrame(exe, "HelloWorld");
	}
}
