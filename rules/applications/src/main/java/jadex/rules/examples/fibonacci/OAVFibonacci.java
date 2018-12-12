package jadex.rules.examples.fibonacci;

import jadex.rules.rulesystem.IAction;
import jadex.rules.rulesystem.ICondition;
import jadex.rules.rulesystem.IVariableAssignments;
import jadex.rules.rulesystem.PriorityAgenda;
import jadex.rules.rulesystem.RuleSystem;
import jadex.rules.rulesystem.RuleSystemExecutor;
import jadex.rules.rulesystem.Rulebase;
import jadex.rules.rulesystem.rete.RetePatternMatcherFunctionality;
import jadex.rules.rulesystem.rules.AndCondition;
import jadex.rules.rulesystem.rules.BoundConstraint;
import jadex.rules.rulesystem.rules.FunctionCall;
import jadex.rules.rulesystem.rules.IOperator;
import jadex.rules.rulesystem.rules.IPriorityEvaluator;
import jadex.rules.rulesystem.rules.LiteralConstraint;
import jadex.rules.rulesystem.rules.ObjectCondition;
import jadex.rules.rulesystem.rules.PredicateConstraint;
import jadex.rules.rulesystem.rules.Rule;
import jadex.rules.rulesystem.rules.Variable;
import jadex.rules.rulesystem.rules.functions.OperatorFunction;
import jadex.rules.rulesystem.rules.functions.Sub;
import jadex.rules.state.IOAVState;
import jadex.rules.state.OAVAttributeType;
import jadex.rules.state.OAVJavaType;
import jadex.rules.state.OAVObjectType;
import jadex.rules.state.OAVTypeModel;
import jadex.rules.state.javaimpl.OAVStateFactory;
import jadex.rules.tools.reteviewer.RuleEnginePanel;

/**
 *  Calculating fibonacci numbers example.
 */
public class OAVFibonacci
{
	/**
	 *  Main method.
	 *  @param args The arguments.
	 */
	public static void main(String[] args)
	{
		// Create rete system.
		OAVTypeModel fibonacci_type_model = new OAVTypeModel("fibonacci_type_model");
		fibonacci_type_model.addTypeModel(OAVJavaType.java_type_model);
		final OAVObjectType fibo_type = fibonacci_type_model.createType("fibo_type"); 
		final OAVAttributeType fibo_has_value = fibo_type.createAttributeType("fibo_has_value", OAVJavaType.java_long_type);
		final OAVAttributeType fibo_has_sequence = fibo_type.createAttributeType("fibo_has_sequence", OAVJavaType.java_integer_type);

		IOAVState state = OAVStateFactory.createOAVState(fibonacci_type_model); // Create the production memory.
		Rulebase rb	= new Rulebase();
		RuleSystem rete = new RuleSystem(state, rb, new RetePatternMatcherFunctionality(rb), new PriorityAgenda());
			
		// Add rules to rulebase.
		
		Variable fn = new Variable("?fn", fibo_type);
		ObjectCondition fncon = new ObjectCondition(fibo_type);
		fncon.addConstraint(new BoundConstraint(null, fn));
		fncon.addConstraint(new LiteralConstraint(fibo_has_value, Long.valueOf(-1)));
		rete.getRulebase().addRule(new Rule("recurse", fncon, new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object fn = assignments.getVariableValue("?fn");
				int fncnt = ((Integer)state.getAttributeValue(fn, fibo_has_sequence)).intValue();
				System.out.println("recurse for "+fncnt);
				
				Object fm = state.createRootObject(fibo_type);
				state.setAttributeValue(fm, fibo_has_value, Long.valueOf(-1));
				state.setAttributeValue(fm, fibo_has_sequence, Integer.valueOf(fncnt-1));
			}
		}, IPriorityEvaluator.PRIORITY_1));

		Variable f1 = new Variable("?f1", fibo_type);
		ObjectCondition f1con = new ObjectCondition(fibo_type);
		f1con.addConstraint(new BoundConstraint(null, f1));
		f1con.addConstraint(new LiteralConstraint(fibo_has_value, Long.valueOf(-1)));
		f1con.addConstraint(new LiteralConstraint(fibo_has_sequence, Integer.valueOf(1)));
		rete.getRulebase().addRule(new Rule("bootstrap1", f1con, new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object f1 = assignments.getVariableValue("?f1");
				int f1cnt = ((Integer)state.getAttributeValue(f1, fibo_has_sequence)).intValue();
				state.setAttributeValue(f1, fibo_has_value, Long.valueOf(1));
				long f1num = ((Long)state.getAttributeValue(f1, fibo_has_value)).longValue();
				System.out.println("f"+f1cnt+"="+f1num);
			}
		}, IPriorityEvaluator.PRIORITY_2));
		
		Variable f2 = new Variable("?f2", fibo_type);
		ObjectCondition f2con = new ObjectCondition(fibo_type);
		f2con.addConstraint(new BoundConstraint(null, f2));
		f2con.addConstraint(new LiteralConstraint(fibo_has_value, Long.valueOf(-1)));
		f2con.addConstraint(new LiteralConstraint(fibo_has_sequence, Integer.valueOf(2)));
		rete.getRulebase().addRule(new Rule("bootstrap2", f2con, new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object f2 = assignments.getVariableValue("?f2");
				int f2cnt = ((Integer)state.getAttributeValue(f2, fibo_has_sequence)).intValue();
				state.setAttributeValue(f2, fibo_has_value, Long.valueOf(1));
				long f2num = ((Long)state.getAttributeValue(f2, fibo_has_value)).longValue();
				System.out.println("f"+f2cnt+"="+f2num);
			}
		}));
		
		Variable f0 = new Variable("?f0", fibo_type);
		Variable f0seq = new Variable("?f0seq", OAVJavaType.java_integer_type);
//		Variable f1 = new Variable("?f1", fibo_type);
		Variable f1seq = new Variable("?f1seq", OAVJavaType.java_integer_type);
//		Variable f2 = new Variable("?f2", fibo_type);
		Variable f2seq = new Variable("?f2seq", OAVJavaType.java_integer_type);
		
		ObjectCondition f00con = new ObjectCondition(fibo_type);
		f00con.addConstraint(new BoundConstraint(null, f0));
		f00con.addConstraint(new BoundConstraint(fibo_has_sequence, f0seq));
		f00con.addConstraint(new LiteralConstraint(fibo_has_value, Long.valueOf(-1), IOperator.NOTEQUAL));
		
		ObjectCondition f11con = new ObjectCondition(fibo_type);
		f11con.addConstraint(new BoundConstraint(null, f1));
		f11con.addConstraint(new BoundConstraint(fibo_has_sequence, f1seq));
		f11con.addConstraint(new PredicateConstraint(new FunctionCall(new OperatorFunction(IOperator.EQUAL), 
			new Object[]{f0seq, new FunctionCall(new Sub(), new Object[]{f1seq, Integer.valueOf(1)})})));
		f11con.addConstraint(new LiteralConstraint(fibo_has_value, Long.valueOf(-1), IOperator.NOTEQUAL));
		
		ObjectCondition f22con = new ObjectCondition(fibo_type);
		f22con.addConstraint(new BoundConstraint(null, f2));
		f22con.addConstraint(new BoundConstraint(fibo_has_sequence, f2seq));
		f22con.addConstraint(new LiteralConstraint(fibo_has_value, Long.valueOf(-1)));
		f22con.addConstraint(new PredicateConstraint(new FunctionCall(new OperatorFunction(IOperator.EQUAL), 
			new Object[]{f1seq, new FunctionCall(new Sub(), new Object[]{f2seq, Integer.valueOf(1)})})));
		
		rete.getRulebase().addRule(new Rule("calc", new AndCondition(new ICondition[]{f00con, f11con, f22con}), new IAction()
		{
			public void execute(IOAVState state, IVariableAssignments assignments)
			{
				Object f0 = assignments.getVariableValue("?f0");
				Object f1 = assignments.getVariableValue("?f1");
				Object f2 = assignments.getVariableValue("?f2");
				long f0num = ((Long)state.getAttributeValue(f0, fibo_has_value)).longValue();
				long f1num = ((Long)state.getAttributeValue(f1, fibo_has_value)).longValue();
				int f1cnt = ((Integer)state.getAttributeValue(f1, fibo_has_sequence)).intValue();
				long f2num = f0num+f1num;
				state.setAttributeValue(f2, fibo_has_value, Long.valueOf(f2num));
				System.out.println("f"+(f1cnt+1)+"="+f2num);
			}
		}));
		
		Object f50 = state.createRootObject(fibo_type);
		state.setAttributeValue(f50, fibo_has_value, Integer.valueOf(-1));
		state.setAttributeValue(f50, fibo_has_sequence, Integer.valueOf(50));
		
		// Initialize rule system.
		rete.init();
		
//		long start = System.currentTimeMillis();
//		rete.fireAllRules();
//		long stop = System.currentTimeMillis();
//		System.out.println( "fibanacci(" + state.getAttributeValue(f50, fibo_has_sequence) + ") = " + 
//			state.getAttributeValue(f50, fibo_has_value) + " took " + (stop-start) + "ms" );
		
		RuleSystemExecutor exe = new RuleSystemExecutor(rete, true);
		RuleEnginePanel.createRuleEngineFrame(exe, "Fibonacci");
	}
}
