package jadex.rules.examples;

import jadex.rules.rulesystem.IAction;
import jadex.rules.rulesystem.IVariableAssignments;
import jadex.rules.rulesystem.LIFOAgenda;
import jadex.rules.rulesystem.RuleSystem;
import jadex.rules.rulesystem.RuleSystemExecutor;
import jadex.rules.rulesystem.Rulebase;
import jadex.rules.rulesystem.rete.RetePatternMatcherFunctionality;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.Rule;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;
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
		OAVObjectType message_type = new OAVJavaType(Message.class,OAVJavaType.KIND_BEAN, helloworld_type_model);
		
		// Create rete system.
		IOAVState state = OAVStateFactory.createOAVState(helloworld_type_model); // Create the production memory.
		Rulebase rb	= new Rulebase();
		RuleSystem rete = new RuleSystem(state, rb, new RetePatternMatcherFunctionality(rb), new LIFOAgenda());
		
		// Add rule to rulebase.
		Variable message = new Variable("?message", message_type);
		ObjectCondition msgcon = new ObjectCondition(message_type);
		msgcon.addConstraint(new BoundConstraint(null, message));
		rete.getRulebase().addRule(new Rule("new_message", msgcon, new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Message message = (Message)assignments.getVariableValue("?message");
				System.out.println("New message found: "+message.getText());
			}
		}));
		
		// Add fact.
		state.addJavaRootObject(new Message("Hello Java World!"));
		
		// Initialize rule system.
		rete.init();
//		rete.getAgenda().setHistoryEnabled(true);
		
		RuleSystemExecutor exe = new RuleSystemExecutor(rete, true);
		RuleEnginePanel.createRuleEngineFrame(exe, "HelloWorld");
	}
}
