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
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVTypeModel;
import jadex.rules.state.javaimpl.OAVStateFactory;
import jadex.rules.tools.reteviewer.RuleEnginePanel;

/**
 *  Simple hello world program for illustrating how a simple
 *  rule application is set up.
 */
public class JavaHelloWorld
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
		/*OAVObjectType message_type = */helloworld_type_model.createJavaType(Message.class,OAVJavaType.KIND_BEAN);
		
		// Create rete system.
		IOAVState state = OAVStateFactory.createOAVState(helloworld_type_model); // Create the production memory.
		Rulebase rb	= new Rulebase();
		
		// The following five code fragments represent alternatives for condition creation
		
//		// Create rule condition manually.
//		Variable message = new Variable("?message", message_type);
//		ObjectCondition msgcon = new ObjectCondition(message_type);
//		msgcon.addConstraint(new BoundConstraint(null, message));

//		// Create rule condition using clips parser with imports.
//		ICondition	msgcon	= ParserHelper.parseClipsCondition("?message <- (Message)", helloworld_type_model, new String[]{"jadex.rules.examples.helloworld.*"});

//		// Create rule condition using clips parser with fully qualified java type name.
//		ICondition	msgcon	= ParserHelper.parseClipsCondition("?message <- (jadex.rules.examples.helloworld.Message)", helloworld_type_model);

		// Create rule condition using jcl (java condition language) parser with imports.
		ICondition	msgcon	= ParserHelper.parseJavaCondition("Message $message", helloworld_type_model, new String[]{"jadex.rules.examples.helloworld.*"});

//		// Create rule condition using jcl (java condition language) parser with fully qualified java type name.
//		ICondition	msgcon	= ParserHelper.parseJavaCondition("jadex.rules.examples.helloworld.Message $message", helloworld_type_model);

		// Add rule to rulebase.
		rb.addRule(new Rule("new_message", msgcon, new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
//				Message message = (Message)assignments.getVariableValue("?message");	// Use for manual/clips conditions
				Message message = (Message)assignments.getVariableValue("$message");	// Use for jcl conditions
				System.out.println("New message found: "+message.getText());
			}
		}));
		
		// Add fact.
		state.addJavaRootObject(new Message("Hello Java World!"));

		RuleSystem rete = new RuleSystem(state, rb, new RetePatternMatcherFunctionality(rb), new LIFOAgenda());

		// Initialize rule system.
		rete.init();
//		rete.getAgenda().setHistoryEnabled(true);
		
		RuleSystemExecutor exe = new RuleSystemExecutor(rete, true);
		RuleEnginePanel.createRuleEngineFrame(exe, "HelloWorld");
	}
}
